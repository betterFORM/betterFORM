/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms;

import de.betterform.connector.ConnectorFactory;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEventService;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.ns.NamespaceResolver;
import de.betterform.xml.xforms.exception.XFormsErrorIndication;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.bind.BindingResolver;
import de.betterform.xml.xforms.ui.AbstractFormControl;
import de.betterform.xml.xforms.ui.Group;
import de.betterform.xml.xforms.ui.Repeat;
import de.betterform.xml.xforms.ui.Switch;
import de.betterform.xml.xpath.impl.saxon.BetterFormXPathContext;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import net.sf.saxon.Configuration;
import net.sf.saxon.dom.DocumentWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.xforms.XFormsModelElement;

import java.util.*;

/**
 * This class represents a complete XForms document. It encapsulates the DOM
 * document.
 * <p/>
 * The XForms document may consist of pure XForms markup or may contain mixed
 * markup from other namespaces (e.g. HTML, SVG, WAP).
 * <p/>
 * Responsibilities are: <ul> <li>model creation and initialization</li>
 * <li>event creation, dispatching and error handling</li> <li>element
 * registry</li> </ul>
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @author Eduardo Millan <emillan@users.sourceforge.net>
 * @version $Id: Container.java 3492 2008-08-27 12:37:01Z joern $
 */
public class Container {
    private static final Log LOGGER = LogFactory.getLog(Container.class);
    private final Configuration fConfiguration = new Configuration();
    private BindingResolver bindingResolver;
    private XFormsProcessorImpl processor;
    private ConnectorFactory connectorFactory;
    private Document document;
    private Element root;
    private XMLEventService eventService;
    private List<Model> models;
    private Map xFormsElements;
    private XFormsElementFactory elementFactory;
    private CustomElementFactory customElementFactory;
    private boolean modelConstructDone = false;
    private int idCounter = 0;
    private List eventExceptions;
    public static final String XFORMS_1_0 = "1.0";
    public static final String XFORMS_1_1 = "1.1";
    private BetterFormXPathContext hostContext;
    private String version;
    private Stack<EventInfo> fEventInfoStack = new Stack<EventInfo>();
    private String focussedControlId=null;
    private String focussedContainerId=null;

    /**
     * associates DocumentContainer with Processor.
     *
     * @param processor the Processor object
     */
    public Container(XFormsProcessorImpl processor) {
        this.processor = processor;
    }

    /**
     * returns the processor for this container
     *
     * @return the processor for this container
     */
    public XFormsProcessorImpl getProcessor() {
        return processor;
    }

    /**
     * Returns the binding resolver.
     *
     * @return the binding resolver.
     */
    public BindingResolver getBindingResolver() {
        if (this.bindingResolver == null) {
            this.bindingResolver = new BindingResolver();
        }

        return this.bindingResolver;
    }

    /**
     * Returns the connector factory.
     *
     * @return the connector factory.
     */
    public ConnectorFactory getConnectorFactory() {
        if (this.connectorFactory == null) {
            try {
                this.connectorFactory = ConnectorFactory.getFactory();
                this.connectorFactory.setContext(this.processor.getContext());
            } catch (XFormsConfigException xce) {
                throw new RuntimeException(xce);
            }
        }

        return this.connectorFactory;
    }

    /**
     * Returns the XForms element factory which is responsible for creating
     * objects for all XForms elements in the input document.
     *
     * @return the XForms element factory
     * @see XFormsElementFactory
     */
    public XFormsElementFactory getElementFactory() {
        if (this.elementFactory == null) {
            this.elementFactory = new XFormsElementFactory();
        }

        return this.elementFactory;
    }

    /**
     * Returns the custom element factory which is responsible for creating
     * objects for all custom elements in the input document.
     *
     * @return the custom element factory
     * @see CustomElementFactory
     */
    public CustomElementFactory getCustomElementFactory() throws XFormsException {
        if (this.customElementFactory == null) {
            this.customElementFactory = new CustomElementFactory();
        }

        return this.customElementFactory;
    }

    /**
     * Returns the XML Event Service.
     *
     * @return the XML Event Service.
     */
    public XMLEventService getXMLEventService() {
        return this.eventService;
    }

    /**
     * returns the id of the currently focussed  control if any. This value will be set exclusively by an
     * setfocus action.
     *
     * @return the id of the currently focussed  control if any or null if undefined (no setfocus was called on the form)
     */
    public String getFocussedControlId(){
        return this.focussedControlId;
    }

    /**
     * sets the id of the currently focussed control
     *
     * @param id - an id of an existing focussable control
     */
    public void setFocussedControlId(String id){
        this.focussedControlId = id;
    }

    public String getFocussedContainerId(){
        return this.focussedContainerId;
    }

    public void setFocussedContainerId(String id){
        this.focussedContainerId = id;
    }

    /**
     * Sets the XML Event Service.
     *
     * @param eventService the XML Event Service.
     */
    public void setXMLEventService(XMLEventService eventService) {
        this.eventService = eventService;
    }

    /**
     * passes the XML container document which contains XForms markup. This
     * method must be called before init() and already builds up the
     * NamespaceResolver and RootContext objects.
     *
     * @param document a DOM Document
     */
    public void setDocument(Document document) {
        this.document = document;
        this.root = this.document.getDocumentElement();

        // check for betterform namespace
        if (!this.root.hasAttributeNS(NamespaceConstants.XMLNS_NS, NamespaceConstants.BETTERFORM_PREFIX)) {
            this.root.setAttributeNS(NamespaceConstants.XMLNS_NS,
                    "xmlns:" + NamespaceConstants.BETTERFORM_PREFIX,
                    NamespaceConstants.BETTERFORM_NS);
        }

        NamespaceResolver.init(root);
    }

    /**
     * Returns the container as a dom tree representing an (external) XML
     * representation of the xforms container.  The return value is live, that
     * means changes to the return tree affects the internal container
     * representation.
     *
     * @return the container as a document.
     */
    public Document getDocument() {
        return this.document;
    }

//    public NodeInfo getRootNodeInfo() {
//        return new DocumentWrapper(document, this.processor.getBaseURI(), new IndependentContext().getConfiguration()).wrap(document.getDocumentElement());
//    }

    /**
     * stores this container as userobject in document element and triggers
     * model initialization
     *
     * @throws XFormsException
     */
    public void init() throws XFormsException {
        this.root.setUserData("",this,null);

//        checkVersionCompatibility();

        // trigger model initialization
        initModels();

    }


    /**
     * @return the version of XForms required by form author or if attribute is not present "1.1" as default.
     *         <p/>
     *         todo: checks only the default model for now. This must be improved
     */
    public String getVersion() throws XFormsException {
        return this.version;
    }


    /**
     * Triggers model destruction.
     *
     * @throws XFormsException
     */
    public void shutdown() throws XFormsException {
        if (this.models != null) {
            Model model;
            for (int index = 0; index < this.models.size(); index++) {
                model = (Model) this.models.get(index);
                // todo: release resources in Model.performDefault()
                dispatch(model.getTarget(), XFormsEventNames.MODEL_DESTRUCT, null);
            }
        }
    }

    /**
     * Adds a DOM Event Listener to the specified target node.
     *
     * @param targetId      the id of the target node.
     * @param eventType     the event type.
     * @param eventListener the Event Listener.
     * @param useCapture    use event capturing or not.
     * @throws XFormsException if the target node could not be found.
     * todo: remove this method - unused
     */
    public void addEventListener(String targetId, String eventType, EventListener eventListener, boolean useCapture) throws XFormsException {
        EventTarget eventTarget = lookupEventTarget(targetId);
        if (eventTarget != null) {
            eventTarget.addEventListener(eventType, eventListener, useCapture);
            return;
        }

        throw new XFormsException("Unable to add eventlistener. Event target '" + targetId + "' not found");
    }

    /**
     * Removes a DOM Event Listener from the specified target node.
     *
     * @param targetId      the id of the target node.
     * @param eventType     the event type.
     * @param eventListener the Event Listener.
     * @param useCapture    use event capturing or not.
     * @throws XFormsException if the target node could not be found.
     * todo: remove this method - unused
     */
    public void removeEventListener(String targetId, String eventType, EventListener eventListener, boolean useCapture) throws XFormsException {
        EventTarget eventTarget = lookupEventTarget(targetId);
        if (eventTarget != null) {
            eventTarget.addEventListener(eventType, eventListener, useCapture);
            return;
        }

        throw new XFormsException("Unable to remove eventlistener. Event target '" + targetId + "' not found");
    }

    /**
     * Dispatches a DOM Event to the specified target node.
     *
     * @param targetId  the id of the target node.
     * @param eventType the event type.
     * @return <code>true</code> if the event has been cancelled during dispatch,
     *         otherwise <code>false</code>.
     * @throws XFormsException if the target node could not be found.
     */
    public boolean dispatch(String targetId, String eventType) throws XFormsException {
        return dispatch(targetId, eventType, null);

    }

    /**
     * Dispatches a DOM Event to the specified target node.
     *
     * @param targetId  the id of the target node.
     * @param eventType the event type.
     * @param info      a context information object.
     * @return <code>true</code> if the event has been cancelled during dispatch,
     *         otherwise <code>false</code>.
     * @throws XFormsException if the target node could not be found.
     */
    public boolean dispatch(String targetId, String eventType, Object info) throws XFormsException {
        return dispatch(targetId, eventType, info, true, true);
    }

    /**
     * Dispatches a DOM Event to the specified target node.
     * <p/>
     * The bubbles and cancelable parameters are ignored for well-known event types.
     *
     * @param targetId   the id of the target node.
     * @param eventType  the event type.
     * @param info       a context information object.
     * @param bubbles    specifies wether the event can bubble.
     * @param cancelable specifies wether the event's default action can be prevented.
     * @return <code>true</code> if the event has been cancelled during dispatch,
     *         otherwise <code>false</code>.
     * @throws XFormsException if the target node could not be found.
     */
    public boolean dispatch(String targetId, String eventType, Object info, boolean bubbles, boolean cancelable) throws XFormsException {
        XFormsElement xFormsElement = lookup(targetId);
        if(xFormsElement != null && xFormsElement instanceof AbstractFormControl && (DOMEventNames.FOCUS_IN.equals(eventType) || DOMEventNames.FOCUS_OUT.equals(eventType))){
            // if focussedContainerId xforms element does not exist set focussedContainerId to null
            if(!this.xFormsElements.containsKey(focussedContainerId)) {
                focussedContainerId = null;
            }
            //fetch parent XForms Element if any
            XFormsElement parent = xFormsElement.getEnclosingXFormsContainer();
            if(parent == null) {
                if(focussedContainerId != null){
                    dispatch(focussedContainerId,DOMEventNames.FOCUS_OUT);
                }
                focussedContainerId = null;
            }
            //todo: ...or switch or repeat
            else if(parent instanceof Group || parent instanceof Switch || parent instanceof Repeat){
                //check if parent group has not(!) already the focus
                if(!(parent.getId().equals(focussedContainerId))){
                    if(focussedContainerId != null) {
                        dispatch(focussedContainerId,DOMEventNames.FOCUS_OUT);
                    }
                    //remember current group
                    this.focussedContainerId = parent.getId();
                    //if not dispatch to group
                    if(DOMEventNames.FOCUS_IN.equals(eventType)) {
                        dispatch(parent.getId(),DOMEventNames.FOCUS_IN);    
                    }
                }
            }
        }

        EventTarget eventTarget = lookupEventTarget(targetId);
        if (eventTarget != null) {
            return dispatch(eventTarget, eventType, info, bubbles, cancelable);
        }

        throw new XFormsException("event target '" + targetId + "' not found for event '" + eventType + "'" );
    }

    /**
     * Dispatches a DOM Event to the specified target node.
     *
     * @param eventTarget the target node.
     * @param eventType   the event type.
     * @param info        a context information object.
     * @return <code>true</code> if the event has been cancelled during dispatch,
     *         otherwise <code>false</code>.
     * @throws XFormsException if the target node could not be found.
     */
    public boolean dispatch(EventTarget eventTarget, String eventType, Object info) throws XFormsException {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("dispatching event '" + eventType + "' to target '" + DOMUtil.getCanonicalPath((Node) eventTarget) + "'");
        }
        return dispatch(eventTarget, eventType, info, true, true);
    }

    /**
     * Dispatches a DOM Event to the specified target node.
     * <p/>
     * The bubbles and cancelable parameters are ignored for well-known event types.
     *
     * @param eventTarget the target node.
     * @param eventType   the event type.
     * @param info        a context information object.
     * @param bubbles     specifies wether the event can bubble.
     * @param cancelable  specifies wether the event's default action can be prevented.
     * @return <code>true</code> if the event has been cancelled during dispatch,
     *         otherwise <code>false</code>.
     * @throws XFormsException if the target node could not be found.
     */
    public boolean dispatch(EventTarget eventTarget, String eventType, Object info, boolean bubbles, boolean cancelable) throws XFormsException {
        boolean result = false;
        XFormsException xFormsException = null;
        try {
        	fEventInfoStack.push(new EventInfo(eventTarget, eventType, info, bubbles, cancelable));
            result = this.eventService.dispatch(eventTarget, eventType, bubbles, cancelable, info);
        }
        finally {
        	fEventInfoStack.pop();
            // exception(s) during event flow ?
            if (this.eventExceptions != null && this.eventExceptions.size() > 0) {
                Exception exception = (Exception) this.eventExceptions.get(0);

                if (exception instanceof XFormsErrorIndication) {
                    if (((XFormsErrorIndication) exception).isFatal()) {
                        // downcast fatal error for rethrowal
                        xFormsException = (XFormsException) exception;
                    } else {
                        LOGGER.warn("dispatch: non-fatal xforms error", exception);
                    }
                } else if (exception instanceof XFormsException) {
                    // downcast exception for rethrowal
                    xFormsException = (XFormsException) exception;
                } else {
                    // wrap exception for rethrowal
                    xFormsException = new XFormsException(exception);
                }

                // get rid of follow-up exceptions
                this.eventExceptions.clear();
            }
        }

        if (xFormsException != null) {
            // rethrow exception
            LOGGER.error("dispatch: exception during event flow", xFormsException);
            throw xFormsException;
        }

        return result;
    }

    /**
     * Stores an exception until the currently ongoing event flow has finished.
     *
     * @param exception an exception occurring during event flow.
     */
    public void handleEventException(Exception exception) {


        LOGGER.warn("handle event exception: " + exception.getClass().getName() + " kept for rethrowal after dispatch() has finished");

        if (this.eventExceptions == null) {
            this.eventExceptions = new ArrayList();
        }

        if (exception instanceof XFormsErrorIndication) {
            XFormsErrorIndication indication = (XFormsErrorIndication) exception;

            LOGGER.warn("XForms Error: " + indication.getMessage());
            if (!indication.isHandled()) {
                // dispatch error indication event
                try {
                    dispatch(indication.getEventTarget(),
                            indication.getEventType(),
                            indication.getContextInfo());
                }
                catch (XFormsException e) {
                    LOGGER.error("handle event exception: exception during error indication event", e);
                }

                // set error indication handled
                indication.setHandled();
            }
        }

        // keep exception
        this.eventExceptions.add(exception);
    }

    /**
     * Returns the specified XForms element.
     *
     * @param id the id of the XForms element.
     * @return the specified XForms element or <code>null</code> if the id is
     *         unknown.
     */
    public XFormsElement lookup(String id) {
        if (this.xFormsElements != null) {
            return (XFormsElement) this.xFormsElements.get(id);
        }

        return null;
    }

    /**
     * Generates an unique identifier.
     *
     * @return an unique identifier.
     */
    public String generateId() {
        // todo: build external is service
        String id = "C" + (++this.idCounter);

        while (lookup(id) != null) {
            id = "C" + (++this.idCounter);
        }

        return id;
    }

    /**
     * Registers the specified XForms element with this <code>container</code>.
     * <p/>
     * Attaches Container as listener for XForms exception events.
     *
     * @param element the XForms element to be registered.
     */
    public void register(XFormsElement element) {
        if (this.xFormsElements == null) {
            this.xFormsElements = new HashMap();
        }

        this.xFormsElements.put(element.getId(), element);
    }

    /**
     * Deregisters the specified XForms element with this
     * <code>container</code>.
     *
     * @param element the XForms element to be deregistered.
     */
    public void deregister(XFormsElement element) {
        if (this.xFormsElements != null) {
            this.xFormsElements.remove(element.getId());
        }
    }

    /**
     * convenience method to return default model without knowing its id.
     *
     * @return returns the first model in document order
     */
    public Model getDefaultModel() throws XFormsException {
        return getModel(null);
    }

    /**
     * return a model object by its id. If id is null or an empty string, the
     * default model (first found in document order) is returned.
     */
    public Model getModel(String id) throws XFormsException {
        if ((id == null) || (id.length() == 0)) {
            if (this.models != null && this.models.size() > 0) {
                return (Model) this.models.get(0);
            }

            throw new XFormsException("default model not found");
        }

        if (this.models != null) {
            Model model;
            for (int index = 0; index < this.models.size(); index++) {
                model = (Model) this.models.get(index);

                if (model.getId().equals(id)) {
                    return model;
                }
            }
        }

        throw new XFormsException("model '" + id + "' not found");
    }

    /**
     * Returns all models.
     *
     * @return all models.
     */
    public List getModels() {
        return this.models;
    }

    /**
     * returns true, if the default-processing for xforms-model-construct-done
     * Event has been executed already.
     *
     * @return true, if the default-processing for xforms-model-construct-done
     *         Event has been executed already.
     */
    public boolean isModelConstructDone() {
        return this.modelConstructDone;
    }

    /**
     * create Model-objects which simply hold their Model-element node (formerly
     * named XForm-element).
     * <p/>
     * The first Model-element found in the container is the default-model and
     * if it has no model-id it is stored with a key of 'default' in the models
     * hashtable. Otherwise the provided id is used.
     * <p/>
     * The subsequent model-elements are stored with their id as the key. If no
     * id exists an exception is thrown (as defined by Spec).
     * <p/>
     */
    private void initModels() throws XFormsException {
        this.models = new ArrayList();
        List<Element> modelElements = getModelElements();
        Model model;
        Element modelElement;

        // create all models and dispatch xforms-model-construct to all models
        final int nrOfModels = modelElements.size();
		if (nrOfModels == 0) {
            return;
        }

        for (int i = 0; i < nrOfModels; i++) {
            modelElement = modelElements.get(i);

            model = (Model) getElementFactory().createXFormsElement(modelElement, null);
            this.models.add(model);
        }

        for (int i = 0; i < nrOfModels; i++) {
        	boolean isCompatible= true;
            model = (Model) this.models.get(i);
            model.init();

            if (i == 0) {
	            isCompatible = checkVersionCompatibility();

        	}

            if(!(isCompatible)){
                return;
            }

            Initializer.initializeModelConstructActionElements(model, model.getElement());
	        dispatch(model.getTarget(), XFormsEventNames.MODEL_CONSTRUCT, null);
        }


        for (int i = 0; i < nrOfModels; i++) {
            model = (Model) this.models.get(i);
            dispatch(model.getTarget(), XFormsEventNames.MODEL_CONSTRUCT_DONE, null);

            // set flag to signal that construction has been performed
            this.modelConstructDone = true;
        }

        for (int i = 0; i < nrOfModels; i++) {
            model = (Model) this.models.get(i);
            dispatch(model.getTarget(), XFormsEventNames.READY, null);
        }
    }

    /**
     * Creates at runtime all models specified within an given element
     * Used to initialize embedded forms at runtime
     *
     * @param startElement
     * @throws XFormsException
     */
    public void createEmbeddedForm(Element startElement) throws XFormsException {
        // receive all models from given element
        List<Element> modelElements = getModelElements(startElement);
        final int nrOfModels = modelElements.size();
        // list containing all model of embded form
        ArrayList<Model> embeddedModels = new ArrayList<Model>(nrOfModels);
        for (int i = 0; i < nrOfModels; i++) {
            Element modelElement = modelElements.get(i);
            Model model = (Model) getElementFactory().createXFormsElement(modelElement, null);
            this.models.add(model);
            embeddedModels.add(model);
        }

        for (int i = 0; i < nrOfModels; i++) {
        	boolean isCompatible= true;
            Model model = embeddedModels.get(i);
            model.init();

        	if (i == 0) {
	            isCompatible = checkVersionCompatibility();
        	}

            if(!(isCompatible)){
                return;
            }
	        dispatch(model.getTarget(), XFormsEventNames.MODEL_CONSTRUCT, null);
        }


        for (Model model : embeddedModels) {
            dispatch(model.getTarget(), XFormsEventNames.MODEL_CONSTRUCT_DONE, null);
            // set up UI for specific model
            Initializer.initializeUIElements(model,startElement,null,null);
        }
        for (Model model: embeddedModels) {
            dispatch(model.getTarget(), XFormsEventNames.READY, null);
        }

    }

    /**
     *
     * @param start
     * @return
     */
	private List<Element> getModelElements(Element start){
        List<Element> result = new ArrayList<Element>();
        addModelElements(start, result);
        return result;
    }


    /**
     *
     * @return
     */
	private List<Element> getModelElements() {
        return getModelElements(root);
	}


    private void addModelElements(Node parent, List<Element> models) {
		for(Node it = parent.getFirstChild(); it != null; it = it.getNextSibling()) {
			if (it.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}

			if(NamespaceConstants.XFORMS_NS.equals(it.getNamespaceURI())) {
				Element el = (Element)it;
				if (XFormsConstants.MODEL.equals(el.getLocalName())) {
					models.add(el);
				}
				// Model is a top level XForms element so we don't need to go in recursion here
                else{
                    addModelElements(it, models);                    
                }
			}
			else {
				addModelElements(it, models);
			}
		}
	}

    public void removeModel(Model model) {
        if(this.models.contains(model)){
            this.models.remove(model);
        }
    }

    public Element getElementById(String id) throws XFormsException {
        String baseURI = getProcessor().getBaseURI();
        return (Element) XPathCache.getInstance().evaluateAsSingleNode(getHostContext(baseURI), "//*[@id='" + id + "']");
    }

    /**
     * todo:prefixMapping for Host document needed
     *
     * @param baseURI
     * @return
     */
    public BetterFormXPathContext getHostContext(String baseURI) {
        //lazy instanciation of BetterFormContext
        if (this.hostContext == null) {
            List nodes = de.betterform.xml.xpath.impl.saxon.XPathUtil.getRootContext(document, baseURI);
            this.hostContext = new BetterFormXPathContext(nodes, 1, Collections.EMPTY_MAP, null);
        }
        return this.hostContext;
    }

    /**
     * Returns the specified Event target.
     *
     * @param id the id of the Event target.
     * @return the specified Event target or <code>null</code> if the id is
     *         unknown.
     */
    private EventTarget lookupEventTarget(String id) throws XFormsException {
        XFormsElement xFormsElement = lookup(id);
        if (xFormsElement != null) {
            return xFormsElement.getTarget();
        }

        return (EventTarget) getElementById(id);
    }

    /**
     * check if the processor supports the version required by model
     *
     * @return
     * @throws XFormsException
     */
    private boolean checkVersionCompatibility() throws XFormsException {
        String versionString = null;
        final Element defaultModelElement = models.get(0).getElement();

        for (int i = 0; i < models.size(); i++) {
            Element modelElement = models.get(i).getElement();
            versionString = XFormsElement.getXFormsAttribute(modelElement, "version");


            if (i == 0) {
                //Default Model
                this.version = checkForValidVersion(versionString);
                
                if (this.version == null) {
                    HashMap contextInfo = new HashMap();
                    contextInfo.put("error-information", "version setting of default model not supported: '" + versionString + "'");
                    dispatch((EventTarget)defaultModelElement,XFormsEventNames.VERSION_EXCEPTION,contextInfo);
                    return false;
                }
            } else {
                String versionTmp = checkForValidVersion(versionString);

                if (versionTmp == null || versionTmp.compareTo(this.version) > 0) {
                    HashMap contextInfo = new HashMap();
                    contextInfo.put("error-information", "Incompatible version setting: " + versionString + " on model: " + DOMUtil.getCanonicalPath(modelElement));

                    dispatch((EventTarget)defaultModelElement,XFormsEventNames.VERSION_EXCEPTION,contextInfo);
                    return false;
                }
            }
        }
        LOGGER.info("running XForms version" + this.version);
        return true;
    }
    
    protected String checkForValidVersion(String versionAttribute) {
        String versionTmp = null;

        //default setting for betterForm currently
        if (versionAttribute == null || versionAttribute.equals("")) {
            return XFORMS_1_1;

        } else {
            String[] versionArray = versionAttribute.trim().split(" ");

            for (int j = 0; j < versionArray.length; j++) {
                if (XFORMS_1_1.equals(versionArray[j])) {
                    return XFORMS_1_1;
                } else if(XFORMS_1_0.equals(versionArray[j])) {
                    versionTmp = XFORMS_1_0;
                    //be nice an search further on for 1.1
                }
            }
        }

        return versionTmp;
    }

    /**
     * trigger a global refresh of all models. This can be necessary e.g. if the locale changes dynamically.
     */
    public void refresh() throws XFormsException {
        if (isModelConstructDone()) {
            List models = getModels();
            for (int i = 0; i < models.size(); i++) {
                XFormsModelElement model = (XFormsModelElement) models.get(i);
                model.refresh();
            }
        }
    }

    /**
     * @return Returns the current event info if any
     */
    public EventInfo getCurrentEventInfo() {
    	if (fEventInfoStack.isEmpty()) {
    		return null;
    	}
    	return fEventInfoStack.peek();
    }

	public Configuration getConfiguration() {
		return fConfiguration;
	}

    public static class EventInfo {
    	private final EventTarget eventTarget;
    	private final String eventType;
    	private final Object info;
    	private final boolean bubbles;
    	private final boolean cancelable;


		/**
		 * @param eventTarget
		 * @param eventType
		 * @param info
		 * @param bubbles
		 * @param cancelable
		 */
		public EventInfo(EventTarget eventTarget, String eventType,
				Object info, boolean bubbles, boolean cancelable) {
			this.eventTarget = eventTarget;
			this.eventType = eventType;
			this.info = info;
			this.bubbles = bubbles;
			this.cancelable = cancelable;
		}

		/**
		 * @return the eventTarget
		 */
		public EventTarget getEventTarget() {
			return eventTarget;
		}

		/**
		 * @return the eventType
		 */
		public String getEventType() {
			return eventType;
		}

		/**
		 * @return the info
		 */
		public Object getInfo() {
			return info;
		}

		/**
		 * @return the bubbles
		 */
		public boolean bubbles() {
			return bubbles;
		}

		/**
		 * @return the cancelable
		 */
		public boolean isCancelable() {
			return cancelable;
		}
    }
    
    private Map<Document, DocumentWrapper> fgDocumentWrapperCache = new HashMap<Document, DocumentWrapper>();
    public DocumentWrapper getDocumentWrapper(Node n) {
    	final Document ownerDocument = n.getOwnerDocument();
		DocumentWrapper documentWrapper = fgDocumentWrapperCache.get(ownerDocument);
		if (documentWrapper == null) {
			documentWrapper = new DocumentWrapper(ownerDocument, getProcessor().getBaseURI(), getConfiguration());
			fgDocumentWrapperCache.put(ownerDocument, documentWrapper);
		}
		
		return documentWrapper;
    }
}
