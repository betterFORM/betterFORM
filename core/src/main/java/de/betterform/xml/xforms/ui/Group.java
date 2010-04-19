/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.events.*;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.state.BoundElementState;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of <b>9.1.1 The group Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Group.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class Group extends BindingElement implements EventListener,DefaultAction {
    private static final Log LOGGER = LogFactory.getLog(Group.class);
    private boolean hasFocus=false;

    /**
     * Creates a new group element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Group(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element disposal.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if any error occurred during disposal.
     */
    @Override
    public void dispose() throws XFormsException {
        super.dispose();
        ((EventTarget)this.element).removeEventListener(DOMEventNames.FOCUS_IN, this, false);
        ((EventTarget)this.element).removeEventListener(DOMEventNames.FOCUS_OUT, this, false);
    }

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        super.init();
        //todo: should probably only registered in case there's an action handler for this group for focus events
        ((EventTarget)this.element).addEventListener(DOMEventNames.FOCUS_IN, this, false);
        ((EventTarget)this.element).addEventListener(DOMEventNames.FOCUS_OUT, this, false);

        initializeActions();
    }

    // lifecycle template methods

    /**
     * Initializes the default action.
     */
    protected void initializeDefaultAction() {
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.BINDING_EXCEPTION, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.PREVIOUS, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.NEXT, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.FOCUS, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.HELP, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.HINT, this);
    }

    // implementation of 'de.betterform.xml.events.DefaultAction'

    /**
     * Performs the implementation specific default action for this event.
     *
     * @param event the event.
     */
    public void performDefault(Event event) {
        super.performDefault(event);
        if (isCancelled(event)) {
            getLogger().debug(this + " event " + event.getType() + " cancelled");
            return;
        }
        if (event.getType().equals(XFormsEventNames.PREVIOUS)) {
            // todo
            getLogger().warn(this + " default action for " + event.getType() + " is not implemented yet");
            return;
        }
        if (event.getType().equals(XFormsEventNames.NEXT)) {
            // todo
            getLogger().warn(this + " default action for " + event.getType() + " is not implemented yet");
            return;
        }
        if (event.getType().equals(XFormsEventNames.FOCUS)) {
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("Default processing for xforms-focus event: " + this.id);
            }
            String current = this.container.getFocussedContainerId();
            if(current != null && !(current.equals(this.id))){
                try {
                    this.container.dispatch(this.container.getFocussedContainerId(),DOMEventNames.FOCUS_OUT);
                } catch (XFormsException e) {
                    LOGGER.warn("silently failed DOMFocusOut");
                }
            }
            this.container.setFocussedContainerId(this.id);
            try {
                this.container.dispatch(getFirstFocusableControl(),DOMEventNames.FOCUS_IN);
            } catch (XFormsException e) {
                    LOGGER.warn("silently failed DOMFocusIn");
            }
            return;
        }
        if (event.getType().equals(DOMEventNames.FOCUS_IN)) {
            getLogger().warn(this + " default action for " + event.getType() + " is not implemented yet");
        }
        if (event.getType().equals(DOMEventNames.FOCUS_OUT)) {
            getLogger().warn(this + " default action for " + event.getType() + " is not implemented yet");
        }

        if (event.getType().equals(XFormsEventNames.HELP)) {
            // todo
            getLogger().warn(this + " default action for " + event.getType() + " is not implemented yet");
            return;
        }
        if (event.getType().equals(XFormsEventNames.HINT)) {
            // todo
            getLogger().warn(this + " default action for " + event.getType() + " is not implemented yet");
            return;
        }
    }

    private String getFirstFocusableControl() throws XFormsException {
        return XPathUtil.evaluateAsString(this.element,".//*["+ NamespaceConstants.BETTERFORM_PREFIX +":data/@" + NamespaceConstants.BETTERFORM_PREFIX + ":enabled='true'][1]/@id");
    }

    // template methods

    /**
     * Factory method for the element state.
     *
     * @return an element state implementation or <code>null</code> if no state
     *         keeping is required.
     * @throws XFormsException if an error occurred during creation.
     */
    protected UIElementState createElementState() throws XFormsException {
        return isBound() ? new BoundElementState(false, false) : null;
    }

    /**
     * Returns the logger object.
     *
     * @return the logger object.
     */
    protected Log getLogger() {
        return LOGGER;
    }

    public void handleEvent(Event event) {
        try {
            if (event instanceof XMLEvent) {
                XMLEvent xmlEvent = (XMLEvent) event;
                String type = xmlEvent.getType();

                if (DOMEventNames.FOCUS_IN.equals(type)) {
                    if(LOGGER.isDebugEnabled()){
                        LOGGER.debug("Group catching DOMFocusIn: " + this.id);
                    }
                    if(event.getEventPhase() == Event.BUBBLING_PHASE){
                        String current = this.container.getFocussedContainerId();
                        boolean sendDomFocusIn = false;
                        if(current != null && !(current.equals(this.id))){
                            String focusedGroupId = this.container.getFocussedContainerId();
                            if(this.container.lookup(focusedGroupId) != null) {
                                this.container.dispatch(focusedGroupId,DOMEventNames.FOCUS_OUT);
                            }
                            sendDomFocusIn = true;
                        }
                        this.container.setFocussedContainerId(this.id);
                        if (sendDomFocusIn) {
                            String focusedGroupId = this.container.getFocussedContainerId();
                            if(this.container.lookup(focusedGroupId) != null) {
                                this.container.dispatch(focusedGroupId,DOMEventNames.FOCUS_IN);
                            }
                        }
                        event.stopPropagation();
                    }
                }else if (DOMEventNames.FOCUS_OUT.equals(type)){
                    if(LOGGER.isDebugEnabled()){
                        LOGGER.debug("Group catching DOMFocusOut: " + this.id);
                    }
//                    String current = this.container.getFocussedContainerId();
//
//                    if(current != null && current.equals(this.container.getFocussedContainerId()) && this.container.getFocussedControlId() == null){
//                        this.container.setFocussedContainerId(null);
//                        this.container.dispatch(current,DOMEventNames.FOCUS_OUT);
//                    }
//                    event.stopPropagation();


//                    if(this.container.getFocussedControlId() == null){
//                        this.container.dispatch("dummy",XFormsEventNames.FOCUS);
//                    }
//                    event.stopPropagation();
                }
            }
        }catch(Exception e){
            this.container.handleEventException(e);
        }
    }
}

// end of class
