/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.flux;

import de.betterform.agent.web.WebProcessor;
import de.betterform.agent.web.WebUtil;
import de.betterform.agent.web.event.DefaultUIEventImpl;
import de.betterform.agent.web.event.EventQueue;
import de.betterform.agent.web.event.UIEvent;
import de.betterform.agent.web.upload.UploadInfo;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.events.impl.XercesXMLEventFactory;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContextFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * AJAX Facade class to hide the full functionality from the web-client and to
 * avoid overloaded methods which are not recommended by DWR.
 *
 * @author Joern Turner
 * @version $Id: FluxFacade.java 2875 2007-09-28 09:43:30Z lars $
 */
public class FluxFacade {
    //this is a custom event to activate a trigger in XForms.
    public static final String FLUX_ACTIVATE_EVENT = "flux-action-event";
    private static final Log LOGGER = LogFactory.getLog(FluxFacade.class);
    private HttpSession session;
    private HttpServletRequest request;
    private HttpServletResponse response;

    /**
     * grabs the actual web from the session.
     */
    public FluxFacade() {
        session = WebContextFactory.get().getSession(true);
        request = WebContextFactory.get().getHttpServletRequest();
        response = WebContextFactory.get().getHttpServletResponse();
    }

    /**
     * todo: implement XForms init via Ajax call
     *
     * @return update info to initially setup the browser page
     * @throws FluxException when everything goes wrong
     */
    public List<XMLEvent> init(String sessionKey) throws FluxException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FluxProcessor init called on Facade");
        }
        FluxProcessor processor = FluxUtil.getProcessor(sessionKey, request, response, session);
        return processor.getEventQueue().getEventList();
    }

    //todo: should be named 'dispatchActivateEvent'
    public List<XMLEvent> dispatchEvent(String id, String sessionKey) throws XFormsException {
        FluxProcessor processor = null;
        try {
            processor = FluxUtil.getProcessor(sessionKey, request, response, session);
            processor.dispatchEvent(id);
            return processor.getEventQueue().getEventList();
        } catch (FluxException e) {
            return this.renderErrorMessage(e.getMessage());
        }
    }

    private List<XMLEvent> renderErrorMessage(String message) {
        LOGGER.warn("betterFORM Error Message: " + message);
        XMLEvent errorEvent = new XercesXMLEventFactory().createXMLEvent(BetterFormEventNames.RENDER_MESSAGE);
        Map<String, String> params = new HashMap<String, String>(1);
        params.put("message", message);
        errorEvent.initXMLEvent(BetterFormEventNames.RENDER_MESSAGE, true, false, params);
        ArrayList<XMLEvent> list = new ArrayList<XMLEvent>();
        list.add(errorEvent);
        return list;
    }

    public List<XMLEvent> dispatchEventType(String id, String eventType, String sessionKey) throws XFormsException {
        return dispatchEventTypeWithContext(id, eventType, sessionKey, Collections.EMPTY_MAP);
    }

    public List<XMLEvent> dispatchEventTypeWithContext(String id, String eventType, String sessionKey, String contextInfo) throws XFormsException {
        Map params;
        if (contextInfo != null) {
            params = new HashMap(1);
            params.put("context-info", contextInfo);
        } else {
            params = Collections.EMPTY_MAP;
        }

        return dispatchEventTypeWithContext(id, eventType, sessionKey, params);
    }

    public List<XMLEvent> dispatchEventTypeWithContext(String id, String eventType, String sessionKey, Map contextInfo) throws XFormsException {
        FluxProcessor processor = null;
        try {
            processor = FluxUtil.getProcessor(sessionKey, request, response, session);
            processor.getEventQueue().flush();
        } catch (FluxException e) {
            e.printStackTrace();
        }
        try {
            if (contextInfo != null && (!contextInfo.isEmpty())) {
                assert processor != null:"Processor is null - you likely have no valid session.";
                processor.dispatch(id, eventType, contextInfo, true, false);
            } else {
                assert processor != null:"Processor is null - you likely have no valid session.";
                processor.dispatch(id, eventType);
            }
        } catch (XFormsException e) {
            return this.renderErrorMessage(e.getMessage());
        }
        return processor.getEventQueue().getEventList();
    }

    public List<XMLEvent> dispatchEventTypeWithContext(String id, String eventType, String sessionKey, Object[] contextInfo) throws XFormsException {
        Map params;
        if (contextInfo != null) {
            params = new HashMap();

            for (int i = 0; i < contextInfo.length; i++) {
                params.put("param" + i, contextInfo[i]);
            }

        } else {
            params = Collections.EMPTY_MAP;
        }

        return dispatchEventTypeWithContext(id, eventType, sessionKey, params);
    }

    public List setUIControlValue(String id, String value, String sessionKey) throws XFormsException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FluxFProcessor: " + this.toString());
        }
        UIEvent event = new DefaultUIEventImpl();
        event.initEvent("SETVALUE", id, value);
        List results = null;
        try {
            results = handleUIEvent(event, sessionKey);
        } catch (FluxException e) {
            return this.renderErrorMessage(e.getMessage());
        }
        return results;

    }

    /**
     * executes a trigger
     *
     * @param id the id of the trigger to execute
     * @param sessionKey the sessionKey identifying the user session
     * @return the list of events that may result through this action
     * @throws FluxException raises exception if problem during processing
     * occurs
     */
    /*
     public List fireAction(String id, String sessionKey) throws FluxException {
     UIEvent uiActivateEvent = new DefaultUIEventImpl();
     uiActivateEvent.initEvent(FLUX_ACTIVATE_EVENT, id, null);
     return handleUIEvent(uiActivateEvent, sessionKey);
     }
     */
    /**
     * sets the value of a control in the processor.
     *
     * @param id the id of the control in the host document
     * @param value the new value
     * @param sessionKey the sessionKey identifying the user session
     * @return the list of events that may result through this action
     * @throws FluxException raises exception if problem during processing
     * occurs
     */
    public List setXFormsValue(String id, String value, String sessionKey) throws FluxException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FluxFacade instance: " + this.toString());
        }
        UIEvent event = new DefaultUIEventImpl();
        event.initEvent("SETVALUE", id, value);
        return handleUIEvent(event, sessionKey);
    }


    /*    public Element getNodeset(String id, String sessionKey) throws FluxException {
     try {
     XFormsProcessor processor = FluxUtil.getProcessor(sessionKey);
     XFormsElement element = processor.lookup(id);
     if (element instanceof BindingElement){
     return ((BindingElement)element).
     }
     } catch (FluxException e) {
     throw new FluxException(e);
     }

     }*/
    public org.w3c.dom.Element getXFormsDOM(String sessionKey) throws FluxException {
        try {
            Element resultElem = ((Document) FluxUtil.getProcessor(sessionKey, request, response, session).getXForms()).getDocumentElement();
            if (LOGGER.isDebugEnabled()) {
                DOMUtil.prettyPrintDOM(resultElem);
            }
            return resultElem;
        } catch (XFormsException e) {
            throw new FluxException(e);
        }
    }

    public List setRepeatIndex(String id, String position, String sessionKey) throws FluxException {
        UIEvent event = new DefaultUIEventImpl();
        event.initEvent("SETINDEX", id, position);
        return handleUIEvent(event, sessionKey);
    }

    /**
     * fetches the progress of a running upload.
     *
     * @param id id of the upload control in use
     * @param filename filename for uploaded data
     * @param sessionKey the sessionKey identifying the user session
     * @return a array containing two elements for evaluation in browser. First
     * param is the upload control id and second will be the current progress of
     * the upload.
     */

    public List<XMLEvent> fetchProgress(String id, String filename, String sessionKey) {
        String progress;
        UploadInfo uploadInfo;

        if (session != null && session.getAttribute(WebProcessor.ADAPTER_PREFIX + sessionKey + "-uploadInfo") != null) {
            uploadInfo = (UploadInfo) session.getAttribute(WebProcessor.ADAPTER_PREFIX + sessionKey + "-uploadInfo");

            if (uploadInfo.isInProgress()) {
                double p = uploadInfo.getBytesRead() / uploadInfo.getTotalSize();

                progress = p + "";
                float total = uploadInfo.getTotalSize();
                float read = uploadInfo.getBytesRead();
                int iProgress = (int) Math.ceil((read / total) * 100);
                if (iProgress < 100) {
                    progress = Integer.toString(iProgress);
                } else {
                    uploadInfo.setStatus("completed");
                    progress = "100";
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Bytes total: " + uploadInfo.getTotalSize());
                    LOGGER.debug("Bytes read: " + uploadInfo.getBytesRead());
                    LOGGER.debug("elapsed time: " + uploadInfo.getElapsedTime());
                    LOGGER.debug("status: " + uploadInfo.getStatus());
                    LOGGER.debug("Percent completed: " + Math.ceil((read / total) * 100));
                }
            } else {
                progress = "100";
            }
        } else {
            //if session info is not found for some reason return 100 for safety which allows to exit
            //javascript polling of progress info
            progress = "100";
        }
        // XFormsSessionManager manager = (XFormsSessionManager) session.getAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER);
        // XFormsSession xFormsSession = manager.getWebProcessor(sessionKey);

        WebProcessor webProcessor = WebUtil.getWebProcessor(sessionKey, request, response, session);
        if (webProcessor == null) {
            return this.renderErrorMessage("ERROR: session " + sessionKey + " does not exist");
        }

        EventQueue eventQueue = null;
        if (webProcessor != null) {
            FluxProcessor processor = (FluxProcessor) webProcessor;
            eventQueue = processor.getEventQueue();
            XMLEvent progressEvent = eventQueue.add("upload-progress-event", id, "upload");
            eventQueue.addProperty(progressEvent, "progress", progress);

            List<XMLEvent> origEvents = eventQueue.getEventList();
            List<XMLEvent> eventList = new ArrayList<XMLEvent>();

            for (XMLEvent xmlEvent : origEvents) {
                eventList.add(xmlEvent);
            }
            eventQueue.flush();
            return eventList;
            //eventQueue.flush();
        } else {
            eventQueue = new EventQueue();
            XMLEvent progressEvent = eventQueue.add("upload-progress-event", id, "upload");
            eventQueue.addProperty(progressEvent, "progress", progress);
            return eventQueue.getEventList();
        }
    }

    /**
     * Note user typing activity (not value change), which extends session
     * lifetime.
     *
     * @param sessionKey the sessionKey identifying the user session
     */
    /*
     public void keepAlive(String sessionKey) {
     if (LOGGER.isDebugEnabled()) {
     LOGGER.debug("FluxFacade keepAlive: " + sessionKey);
     }
     XFormsSessionManager manager = (XFormsSessionManager) session.getAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER);
     XFormsSession xFormsSession = manager.getWebProcessor(sessionKey);
     xFormsSession.updateLRU();
     }
     */
    public List setLocale(String locale, String sessionKey) throws FluxException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("switching locale to '" + locale + "' for Session:'" + sessionKey + "'");
        }
        /*
         XFormsSessionManager manager = (XFormsSessionManager) session.getAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER);
         XFormsSession xFormsSession = manager.getWebProcessor(sessionKey);
         */

        WebProcessor webProcessor = WebUtil.getWebProcessor(sessionKey, request, response, session);

        if (webProcessor == null) {
            XMLEvent errorEvent = new XercesXMLEventFactory().createXMLEvent(BetterFormEventNames.RENDER_MESSAGE);
            Map params = new HashMap(1);
            params.put("message", "ERROR: FluxFacade.setLocale(): session " + sessionKey + " does not exist");
            errorEvent.initXMLEvent(BetterFormEventNames.RENDER_MESSAGE, true, false, params);
            ArrayList<XMLEvent> list = new ArrayList();
            list.add(errorEvent);
            return list;
        }

        Locale betterformlocale = new Locale(locale);
        webProcessor.setContextParam(XFormsProcessorImpl.BETTERFORM_LOCALE, betterformlocale);

        UIEvent event = new DefaultUIEventImpl();
        event.initEvent("SETLOCALE", "locale", locale);
        return handleUIEvent(event, sessionKey);

    }

    /**
     * Note page unload, which rapidly ages session.
     *
     * @param sessionKey the sessionKey identifying the user session
     */
    public void close(String sessionKey) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("FluxFacade close: " + sessionKey);
        }

        try {

            // don't use getWebProcessor to avoid needless error
            WebProcessor webProcessor = WebUtil.getWebProcessor(sessionKey, request, response, session);
            if (webProcessor == null) {
                return;
            }
            webProcessor.shutdown();
        } catch (XFormsException e) {
            LOGGER.warn("FluxFacade close: " + sessionKey, e);
        } finally {
            WebUtil.removeSession(sessionKey);
        }
    }

    private List handleUIEvent(UIEvent uiEvent, String sessionKey) throws FluxException {
        FluxProcessor processor = FluxUtil.getProcessor(sessionKey, request, response, session);
        if (processor != null) {
            try {
                processor.handleUIEvent(uiEvent);
            } catch (XFormsException e) {
                LOGGER.error(e.getMessage());
            }
        } else {
            //session expired or cookie got lost
            throw new FluxException("Sorry your session expired. Press Reload to start over.");
        }
        EventQueue eventQueue = processor.getEventQueue();
        List eventlog = eventQueue.getEventList();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Object ID: " + this);
            LOGGER.debug("EventLog: " + eventlog.toString());
            LOGGER.debug("FluxProcessor: " + processor);
        }
        return eventlog;
    }

    /*
     make this available through Facade for those that know what they are doing
     */
    public static org.w3c.dom.Element getInstanceDocument(String modelId, String instanceId, String sessionKey) {
        return FluxModel.getInstanceDocument(modelId, instanceId, sessionKey);
    }

}

// end of class
