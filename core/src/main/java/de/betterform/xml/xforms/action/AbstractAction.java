/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEventConstants;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.bind.Binding;
import de.betterform.xml.xforms.model.bind.BindingResolver;
import de.betterform.xml.xforms.ui.Group;
import de.betterform.xml.xforms.ui.Item;
import de.betterform.xml.xforms.ui.RepeatItem;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import net.sf.saxon.om.NodeInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.util.List;


/**
 * Base class for all action implementations.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: AbstractAction.java 3501 2008-08-28 17:48:27Z joern $
 */
public abstract class AbstractAction extends XFormsElement implements EventListener, XFormsAction {
    private static final Log LOGGER = LogFactory.getLog(AbstractAction.class);

    protected int position = 1;

    /**
     * The id of the containing repeat item, if any.
     */
    protected String repeatItemId;

    /**
     * The event type by which this action is triggered.
     */
    protected String eventType;

    /**
     * the event target of the event
     */
    protected String eventTargetId;

    /**
     * the event phase when the event is triggered
     */
    protected String phase;
    protected String propagate;
    protected String defaultAction;

    /**
     * Creates an action implementation.
     *
     * @param element the element.
     * @param model   the context model.
     */
    public AbstractAction(Element element, Model model) {
        super(element, model);
    }

    // repeat stuff

    /**
     * Sets the id of the repeat item this element is contained in.
     *
     * @param repeatItemId the id of the repeat item this element is contained
     *                     in.
     */
    public void setRepeatItemId(String repeatItemId) throws XFormsException {
        this.repeatItemId = repeatItemId;
    }

    /**
     * Returns the id of the repeat item this element is contained in.
     *
     * @return the id of the repeat item this element is contained in.
     */
    public String getRepeatItemId() {
        return this.repeatItemId;
    }

    /**
     * Checks wether this element is repeated.
     *
     * @return <code>true</code> if this element is contained in a repeat item,
     *         <code>false</code> otherwise.
     */
    public boolean isRepeated() {
        return this.repeatItemId != null;
    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace(this + " init");
        }

        initializeAction();
    }

    /**
     * Performs element disposal.
     *
     * @throws XFormsException if any error occurred during disposal.
     */
    public void dispose() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " dispose");
        }

        disposeAction();
    }

    // lifecycle template methods

    /**
     * Initializes this action.
     */
    protected final void initializeAction() {
        if (!(getParentObject() instanceof XFormsAction)) {
            String event = this.element.getAttributeNS(NamespaceConstants.XMLEVENTS_NS, XMLEventConstants.EVENT_ATTRIBUTE);
            if (event.length() == 0) {
                // default if nothing other is specified
                event = DOMEventNames.ACTIVATE;
            }
            this.eventType = event;
            String observer = this.element.getAttributeNS(NamespaceConstants.XMLEVENTS_NS,"observer");

            this.eventTargetId = this.element.getAttributeNS(NamespaceConstants.XMLEVENTS_NS,"target");
            this.phase = this.element.getAttributeNS(NamespaceConstants.XMLEVENTS_NS,"phase");
            if(phase.equals("")){
                this.phase = "default";
            }
            this.propagate = this.element.getAttributeNS(NamespaceConstants.XMLEVENTS_NS,"propagate");
            if(propagate.equals("")){
                this.propagate = "continue";
            }
            this.defaultAction = this.element.getAttributeNS(NamespaceConstants.XMLEVENTS_NS,"defaultAction");
            if(defaultAction.equals("")){
                this.defaultAction = "perform";
            }
            String handler = this.element.getAttributeNS(NamespaceConstants.XMLEVENTS_NS,"handler");
            if(handler != null &&!(handler.equals(""))){
                LOGGER.warn("Attribute 'handler' for " + DOMUtil.getCanonicalPath(this.element) + " ignored (not implemented yet)");
            }


            EventTarget targetElement;
            if(eventTargetId != null && !(eventTargetId.equals(""))){
            	final XFormsElement xformsElement = this.container.lookup(this.eventTargetId);
            	if (xformsElement != null) {
            		targetElement = xformsElement.getTarget();
            	}
            	else {
            		// In some cases the xforms element isn't registered yet (e.g.: action refers to an Instance, or a forward reference)
            		targetElement = null;
            		try {
						targetElement = (EventTarget) de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(XPathUtil.evaluate(container.getDocument(), "//*[@id = '" + eventTargetId + "']"), 1);
					} catch (XFormsException e) { }
            	}
                addListener(targetElement);
            }

            if(observer != null && !(observer.equals(""))){
            	final XFormsElement xformsElement = this.container.lookup(observer);
            	if (xformsElement != null) {
            		targetElement = xformsElement.getTarget();
            	}
            	else {
            		// In some cases the xforms element isn't registered yet (e.g.: action refers to an Instance, or a forward reference)
            		targetElement = null;
            		try {
						targetElement = (EventTarget) de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(XPathUtil.evaluate(container.getDocument(), "//*[@id = '" + observer + "']"), 1);
					} catch (XFormsException e) { }
            	}
                addListener(targetElement);
            }else {
                XFormsElement parent = getParentObject();
                if(parent != null) {
                    targetElement = parent.getTarget();
                    addListener(targetElement);
                }else {
                    getLogger().warn("parent is null");
                }
            }
            if (getLogger().isTraceEnabled()) {
                getLogger().trace(this + " init: added handler for event '" + this.eventType + "' to " + DOMUtil.getCanonicalPath(this.element));
            }
        }
    }

    private void addListener(EventTarget targetElement) {
        addEventToUsedEvents();

        if(this.phase.equals("capture")){
            targetElement.addEventListener(this.eventType, this, true);
        }else{
            targetElement.addEventListener(this.eventType, this, false);
        }
    }

    private void addEventToUsedEvents() {
        //add eventType to list of used events in XFormsProcessor
        List usedEvents =  this.container.getProcessor().getEventList();
        if(!usedEvents.contains(this.eventType)){
            usedEvents.add(this.eventType);
        }
    }


    /**
     * Disposes this action.
     */
    protected final void disposeAction() {
        if (!(getParentObject() instanceof XFormsAction)) {
            XFormsElement parent = getParentObject();
            parent.getTarget().removeEventListener(this.eventType, this, false);
        }
    }

    // implementation of org.w3c.dom.event.EventListener

    /**
     * This method is called whenever an event occurs of the type for which the
     * <code>EventListener</code> interface was registered.
     *
     * @param event The <code>Event</code> contains contextual information about
     *              the event. It also contains the <code>stopPropagation</code> and
     *              <code>preventDefault</code> methods which are used in determining the
     *              event's flow and default action.
     */
    public final void handleEvent(Event event) {
        try {
            if (event.getType().equals(this.eventType)) {
                boolean doPerform = false;

                if(this.propagate.equals("stop")){
                    event.stopPropagation();
                }
                if(this.defaultAction.equals("cancel")){
                    event.preventDefault();
                }

                if (getLogger().isTraceEnabled()) {
                    String currentEventId = (((Element) event.getCurrentTarget()).getAttribute("id"));
                    String targetEventId = (((Element) event.getTarget()).getAttribute("id"));
                    LOGGER.trace("currentEventTargetId: '" + currentEventId+ "' eventTargetId:'" +  targetEventId+ "'");

                    getLogger().trace("handling event '" + this.eventType + "' target: " + DOMUtil.getCanonicalPath(this.element) + "' Event Phase: (" + event.getEventPhase() + ")");
                }

                if(getLogger().isDebugEnabled()){
                    switch (event.getEventPhase()) {
                        case Event.CAPTURING_PHASE:
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug("CAPTURING PHASE");
                            }

                            break;
                        case Event.AT_TARGET:
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug("AT_TARGET");
                            }

                            break;
                        case Event.BUBBLING_PHASE:
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug("BUBBLING");
                            }
                            break;
                    }
                }


                if(event.getEventPhase() == Event.CAPTURING_PHASE && this.phase.equals("capture")){
                    doPerform=true;
                }else if(event.getEventPhase() == Event.AT_TARGET){
                    doPerform=true;
                }else if(event.getEventPhase() == Event.BUBBLING_PHASE){
                    doPerform=true;
                }

                String targetEventId = (((Element) event.getTarget()).getAttribute("id"));
                if(!(this.eventTargetId.equals(""))){
                    if(event.getType().equals(this.eventType) && targetEventId.equals(this.eventTargetId)){
                        doPerform=true;
                    }else{
                        doPerform=false;
                    }
                }

                if(doPerform){
                    performConditional(this.element);
                }
            }
        }
        catch (Exception e) {
            // handle exception, prevent default action and stop event propagation
            this.container.handleEventException(e);
            event.preventDefault();
            event.stopPropagation();
        }
    }

    /**
     * checks @while and/or @if Attributes before actually executing the Action.
     * @param actionEl the Action Element to trigger
     * @throws XFormsException
     */
    protected void performConditional(Element actionEl) throws XFormsException {
        final XFormsAction action = (XFormsAction) actionEl.getUserData("");
        final String whileCondition = XFormsElement.getXFormsAttribute(actionEl, XFormsConstants.WHILE_ATTRIBUTE);

        if (whileCondition == null) {
            if (execute((AbstractAction) action)) {
                action.perform();
            }
        } else {
            while (evalCondition(whileCondition)) {
                if (execute((AbstractAction) action)) {
                    action.perform();
                } else {
                    break;
                }
            }
        }
    }

    /**
     * checks for existence of XForms 1.1 'if' attribute and if present evaluates it.
     *
     * @param action the DOM Element representing the action
     * @return true if no 'if' attribute is present or the if condition evaluates to 'true'. False otherwise
     */
//    protected boolean execute (Element action) throws XFormsException{
    protected boolean execute(AbstractAction action) throws XFormsException {
        String ifCondition = action.getXFormsAttribute(action.getElement(), XFormsConstants.IF_CONDITION);

        if (ifCondition == null) {
            return true;
        } else {
            if (action.evalCondition(ifCondition)) return true;
        }
        return false;
    }

    protected boolean evalCondition(String condition) throws XFormsException {

        List resultNodeset = evalInScopeContext();
        final String relativeExpr = condition;

        boolean b;
        try {
            String result = XPathCache.getInstance().evaluateAsString(resultNodeset, position, condition, getPrefixMapping(), xpathFunctionContext);
            if (result.equalsIgnoreCase("true")) {
                b = true;
            } else {
                b = false;
            }
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("evaluating " + relativeExpr +" in context '" + ((NodeInfo)resultNodeset.get(0)).getDisplayName() + "' to " + b);
            }
            return b;
        }
        catch (XFormsException e) {
            throw new XFormsException(e);
        }
    }
    // convenience

    /**
     * Tells the action wether to perform a rebuild or not.
     * <p/>
     * If an update handler is set on the context model, the rebuild will be
     * deferred by the handler. If there is no update handler and the rebuild
     * flag is set to true, the rebuild takes place immediately.
     *
     * @param rebuild specifies wether to perform a rebuild or not.
     */
    protected final void doRebuild(boolean rebuild) throws XFormsException {
        UpdateHandler updateHandler = this.model.getUpdateHandler();
        if (updateHandler != null) {
            updateHandler.doRebuild(rebuild);
            return;
        }

        if (rebuild) {
            this.container.dispatch(this.model.getTarget(), XFormsEventNames.REBUILD, null);
        }
    }

    /**
     * Tells the action wether to perform a recalculate or not.
     * <p/>
     * If an update handler is set on the context model, the recalculate will be
     * deferred by the handler. If there is no update handler and the recalculate
     * flag is set to true, the recalculate takes place immediately.
     *
     * @param recalculate specifies wether to perform a recalculate or not.
     */
    protected final void doRecalculate(boolean recalculate) throws XFormsException {
        UpdateHandler updateHandler = this.model.getUpdateHandler();
        if (updateHandler != null) {
            updateHandler.doRecalculate(recalculate);
            return;
        }

        if (recalculate) {
            this.container.dispatch(this.model.getTarget(), XFormsEventNames.RECALCULATE, null);
        }
    }

    /**
     * Tells the action wether to perform a revalidate or not.
     * <p/>
     * If an update handler is set on the context model, the revalidate will be
     * deferred by the handler. If there is no update handler and the revalidate
     * flag is set to true, the revalidate takes place immediately.
     *
     * @param revalidate specifies wether to perform a revalidate or not.
     */
    protected final void doRevalidate(boolean revalidate) throws XFormsException {
        UpdateHandler updateHandler = this.model.getUpdateHandler();
        if (updateHandler != null) {
            updateHandler.doRevalidate(revalidate);
            return;
        }

        if (revalidate) {
            this.container.dispatch(this.model.getTarget(), XFormsEventNames.REVALIDATE, null);
        }
    }

    /**
     * Tells the action wether to perform a refresh or not.
     * <p/>
     * If an update handler is set on the context model, the refresh will be
     * deferred by the handler. If there is no update handler and the refresh
     * flag is set to true, the refresh takes place immediately.
     *
     * @param refresh specifies wether to perform a refresh or not.
     */
    protected final void doRefresh(boolean refresh) throws XFormsException {
        UpdateHandler updateHandler = this.model.getUpdateHandler();
        if (updateHandler != null) {
            updateHandler.doRefresh(refresh);
            return;
        }

        if (refresh) {
            this.container.dispatch(this.model.getTarget(), XFormsEventNames.REFRESH, null);
        }
    }


    @Override 
    public Binding getEnclosingUIBinding(Element elementImpl, Object binding, String modelId) {
        if(!(binding instanceof Binding)){
            return null;
        }

        Binding bindingElem = (Binding) binding;
        if (BindingResolver.hasUIBinding(elementImpl)|| (bindingElem instanceof Group && bindingElem.hasBindingExpression()) && !(bindingElem instanceof RepeatItem) ||  (bindingElem instanceof Item) && (((Item)bindingElem).getItemset() != null)) {
             if (bindingElem.getModelId().equals(modelId)) {
                 return bindingElem;
             }
         }
        return null;
    }
}

// end of class
