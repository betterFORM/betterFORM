/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.flux;

import de.betterform.agent.web.WebFactory;
import de.betterform.agent.web.WebProcessor;
import de.betterform.agent.web.WebUtil;
import de.betterform.agent.web.event.DefaultUIEventImpl;
import de.betterform.agent.web.event.EventQueue;
import de.betterform.agent.web.event.UIEvent;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.BindingElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

import javax.servlet.http.HttpServletRequest;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Processor for DWR calls. This
 * class is not exposed through DWR. Instead a Facadeclass 'FluxFacade' will be
 * exposed that only allows to use the dispatch method. All other methods will
 * be hidden for security.
 *
 * @author Joern Turner
 * @version $Id: FluxAdapter.java 2970 2007-10-30 11:25:03Z lars $
 */
public class FluxProcessor extends WebProcessor {
    private static final Log LOGGER = LogFactory.getLog(FluxProcessor.class);
    public static final String FLUX_ACTIVATE_EVENT = "flux-action-event";
    private transient EventQueue eventQueue;


    public FluxProcessor() {
        super();
        this.eventQueue = new EventQueue();
    }

    public EventQueue getEventQueue() {
        return this.eventQueue;
    }
    

    /**
     * initialize the Adapter. This is necessary cause often the using
     * application will need to configure the Adapter before actually using it.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *
     */
    public void init() throws XFormsException {
        super.init();

        // processor has already been shutdown
        if (checkForExitEvent() != null) {
            return;
        }
        /*
        Eventlistener for the events below MUST be registered always and cannot be optimized as in WebProcessor.
        They are used to update the client state and are not intended for form authors.
         */
        this.root.addEventListener(BetterFormEventNames.STATE_CHANGED, this, true);
        this.root.addEventListener(BetterFormEventNames.PROTOTYPE_CLONED, this, true);
        this.root.addEventListener(BetterFormEventNames.ID_GENERATED, this, true);
        this.root.addEventListener(BetterFormEventNames.ITEM_INSERTED, this, true);
        this.root.addEventListener(BetterFormEventNames.ITEM_DELETED, this, true);
        this.root.addEventListener(BetterFormEventNames.ITEM_CHANGED, this, true);
        this.root.addEventListener(BetterFormEventNames.INDEX_CHANGED, this, true);
        this.root.addEventListener(BetterFormEventNames.SWITCH_TOGGLED, this, true);
        this.root.addEventListener(BetterFormEventNames.AVT_CHANGED, this, true);
		//TODO, see where BetterFormEventNames.SHOW/HIDE should be added? Lars: moved to WebProcessor
    }


    public void dispatchEvent(String id) throws XFormsException, FluxException {
        UIEvent uiActivateEvent = new DefaultUIEventImpl();
        uiActivateEvent.initEvent(FLUX_ACTIVATE_EVENT, id, null);
        handleUIEvent(uiActivateEvent);
    }


    /**
     * handles a UIEvent to trigger some XForms processing such as updating
     * of values or execution of triggers. Maps 'external' UIEvents from the client to
     * DOM Events understood by the XFormsProcessor.
     *
     * @param uiEvent an application specific event
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *
     */
    public void handleUIEvent(UIEvent uiEvent) throws XFormsException {
        super.handleUIEvent(uiEvent);

        this.eventQueue.flush();

        String targetId = uiEvent.getId();

        if (uiEvent.getEventName().equalsIgnoreCase(FluxFacade.FLUX_ACTIVATE_EVENT)) {
            dispatch(targetId, DOMEventNames.ACTIVATE);
        } else if (uiEvent.getEventName().equalsIgnoreCase("SETINDEX")) {
            int index = Integer.parseInt((String) uiEvent.getContextInfo());
            setRepeatIndex(targetId, index);
        } else if (uiEvent.getEventName().equalsIgnoreCase("SETVALUE")) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Event contextinfo: " + uiEvent.getContextInfo());
            }

            setControlValue(targetId, (String) uiEvent.getContextInfo());
        } else if (uiEvent.getEventName().equalsIgnoreCase("http-request")) {
            HttpServletRequest request = (HttpServletRequest) uiEvent.getContextInfo();
            getHttpRequestHandler().handleUpload(request);
        } else if(uiEvent.getEventName().equalsIgnoreCase("SETLOCALE")){
            setLocale(uiEvent.getContextInfo().toString());
        }else {
            throw new XFormsException("Unknown or illegal uiEvent type");
        }
    }

    /**
     * listen to processor with XMLEvents and add a xmlEvent object to the
     * EventQueue for every incoming DOM Event from the processor.
     *
     * @param event the handled DOMEvent
     */
    public void handleEvent(Event event) {
        super.handleEvent(event);
        try {
            if (event instanceof XMLEvent) {
                XMLEvent xmlEvent = (XMLEvent) event;
                String type = xmlEvent.getType();

                if (BetterFormEventNames.REPLACE_ALL.equals(type)) {
                    // get event context and store it in session
                    Map submissionResponse = new HashMap();
                    submissionResponse.put("header", xmlEvent.getContextInfo("header"));
                    submissionResponse.put("body", xmlEvent.getContextInfo("body"));
                    this.xformsProcessor.setContextParam(WebFactory.BETTERFORM_SUBMISSION_RESPONSE, submissionResponse);

                    // add event properties to log
                    xmlEvent.addProperty("webcontext", (String) getContextParam("contextroot"));
                    this.eventQueue.add(xmlEvent);


                    this.exitEvent = xmlEvent;
                    shutdown();
                    return;
                } else if (BetterFormEventNames.LOAD_URI.equals(type)) {
                    // get event properties
                    String show = (String) xmlEvent.getContextInfo("show");
                    if ("embed".equals(show)) {
                        Element targetElement = (Element) xmlEvent.getContextInfo("targetElement");
                        StringWriter result = new StringWriter();
                        generateUI(targetElement,result);
                        xmlEvent.addProperty("targetElement",result.toString());
/*
                        if(LOGGER.isDebugEnabled()) {
                            DOMUtil.prettyPrintDOM(targetElement);
                            LOGGER.debug("xf:load show=\"embed\" Markup: \n" + result.toString() +"\n");
                        }
                        DOMUtil.prettyPrintDOM(targetElement);
                        System.out.println("xf:load show=\"embed\" Markup: \n" + result.toString() +"\n");
*/
                    }

                    // add event to log
                    this.eventQueue.add(xmlEvent);
                    if ("replace".equals(show)) {
                        this.exitEvent = xmlEvent;
                        shutdown();
                        //this.xformsSession.getManager().deleteXFormsSession(this.xformsSession.getKey());
                        WebUtil.removeSession(getKey());
                    }

                    return;
                } else if (BetterFormEventNames.STATE_CHANGED.equals(type)) {
                    /*
                    todo: This is a HACK cause BETTERFORM_STATE_CHANGED events are not sent consistently. For some reason
                    todo: datatype changes are not always signalled e.g. when the datatype changes from a 'date' to
                    todo: a 'string'. This should be fixed in betterForm Core - after that this branch should be removed again.
                     */

                    // get event properties
                    Element target = (Element) event.getTarget();
                    String targetId = target.getAttributeNS(null, "id");
                    String targetName = target.getLocalName();
                    String dataType = (String) xmlEvent.getContextInfo("type");
                    if (dataType == null) {
                        XFormsElement control = lookup(targetId);

                        //todo: this is copied from EventLog code - this is really a HACK!
                        if (EventQueue.HELPER_ELEMENTS.contains(targetName)) {
                            String parentId = ((Element) target.getParentNode()).getAttributeNS(null, "id");
                            xmlEvent.addProperty("parentId", parentId);
                        } else if (control instanceof BindingElement) {
                            if(LOGGER.isDebugEnabled()) {
                                DOMUtil.prettyPrintDOM(control.getElement());
                            }
                            
                            Element bfData = DOMUtil.getChildElement(control.getElement(), NamespaceConstants.BETTERFORM_PREFIX+":data");
                            String internalType = bfData.getAttributeNS(NamespaceConstants.BETTERFORM_NS, "type");
                            xmlEvent.addProperty("type", internalType);
                        }
                    }
                    this.eventQueue.add(xmlEvent);
                    return;
                }else if (XFormsEventNames.VERSION_EXCEPTION.equals(type)) {
                    WebUtil.removeSession(getKey());
                    xmlEvent.addProperty("errorinformation",xmlEvent.getContextInfo().get("error-information"));
                }
                this.eventQueue.add(xmlEvent);
            }
        }
        catch (Exception e) {
            handleEventException(e);
        }
    }

/*
     public void createUIElement(String id,
                                String xfRole,
                                String ref,
                                String value,
                                String model ) throws XFormsException{
        ((XFormsProcessorImpl)this.xformsProcessor).createUIElement(id,xfRole,ref,value,model);
     }
*/
    private Map copyMap(Map generatedIds) {
        HashMap tmpMap = new HashMap(generatedIds.size());
        for (Iterator iterator = generatedIds.keySet().iterator(); iterator.hasNext();) {
            Object key =  iterator.next();
            tmpMap.put(key,generatedIds.get(key));

        }
        return tmpMap;
    }

/*
    private String generatedIdsToString() {
        String generatedIdsResult = "{";
        for (Iterator iterator = generatedIds.keySet().iterator(); iterator.hasNext();) {
            String key = (String) iterator.next();
            generatedIdsResult += key.toString() + ":";
            generatedIdsResult += "'"+generatedIds.get(key)+"'";
            if(iterator.hasNext()){
                generatedIdsResult += ",";
            }
        }
        generatedIdsResult += "}";
        return generatedIdsResult;
    }
*/


    /**
     * terminates the XForms processing. right place to do cleanup of
     * resources.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *
     */
    public void shutdown() throws XFormsException {
        if (this.root != null) {
            this.root.removeEventListener(BetterFormEventNames.STATE_CHANGED, this, true);
            this.root.removeEventListener(BetterFormEventNames.PROTOTYPE_CLONED, this, true);
            this.root.removeEventListener(BetterFormEventNames.ID_GENERATED, this, true);
            this.root.removeEventListener(BetterFormEventNames.ITEM_INSERTED, this, true);
            this.root.removeEventListener(BetterFormEventNames.ITEM_DELETED, this, true);
            this.root.removeEventListener(BetterFormEventNames.ITEM_CHANGED, this, true);
            this.root.removeEventListener(BetterFormEventNames.INDEX_CHANGED, this, true);
            this.root.removeEventListener(BetterFormEventNames.SWITCH_TOGGLED, this, true);            
            this.root.removeEventListener(BetterFormEventNames.AVT_CHANGED, this, true);
        }

//        super.shutdown();
    }

                /*
    private List handleUIEvent(UIEvent uiEvent, String sessionKey) throws FluxException, XFormsException {
        FluxProcessor processor = getProcessor(sessionKey);
        if (processor != null) {
            try {
                processor.handleUIEvent(uiEvent);
            }
            catch (XFormsException e) {
                LOGGER.error(e.getMessage());
            }
        } else {
            //session expired or cookie got lost
            throw new FluxException("Session expired. Please start again.");
        }
        EventQueue eventQueue  = processor.getEventQueue();
        List<XMLEvent> eventlog = eventQueue.getEventList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Eventlog: " + this);
            for(XMLEvent xmlEvent : eventlog) {
                LOGGER.debug(xmlEvent.toString());
            }            
            LOGGER.debug("Processor: " + processor);
        }
        return eventlog;
    }
*/

}
// end of class
