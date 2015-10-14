/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui.extensions;

import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.ui.BindingElement;
import de.betterform.xml.xforms.ui.UIElementState;
import de.betterform.xml.xforms.ui.state.BoundElementState;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

/**
 * Implementation of a possible XForms 1.2 Dialog
 *
 * @author Ronald van Kuijk
 * @version $Id: Group.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class Dialog extends BindingElement implements EventListener {
    private static final Log LOGGER = LogFactory.getLog(Dialog.class);
    private boolean hasFocus = false;
    private boolean visible = false;

    /**
     * Creates a new group element handler.
     *
     * @param element the host document element.
     * @param model   the context model.
     */
    public Dialog(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element disposal.
     *
     * @throws XFormsException if any error occurred during disposal.
     */
    public void dispose() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " dispose");
        }

        disposeDefaultAction();
        disposeChildren();
        disposeElementState();
        disposeSelf();
    }

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " init");
        }

        initializeDialog();
        initializeDefaultAction();
        //initializeInstanceNode();
        // XXX Check if we need to add updateXPathContext to other init
        // definitions too
        //updateXPathContext();
        //initializeElementState();
        initializeChildren();


    }

    /**
     * This method is called whenever an event occurs of the type for which the
     * <code>EventListener</code> interface was registered.
     *
     * @param event The <code>Event</code> contains contextual information about
     *              the event. It also contains the <code>stopPropagation</code>
     *              and <code>preventDefault</code> methods which are used in
     *              determining the event's flow and default action.
     */
    public void handleEvent(Event event) {
        // Not needed for Dialog
    }

    // lifecycle template methods

    /**
     * Initializes the default action.
     */
    protected void initializeDialog() {

        this.container.getXMLEventService().registerDefaultAction(this.target,
                BetterFormEventNames.SHOW, this);
        this.container.getXMLEventService().registerDefaultAction(this.target,
                BetterFormEventNames.HIDE, this);
    }

    /**
     * Performs the implementation specific default action for this event.
     *
     * @param event the event.
     */
    public void performDefault(Event event) {
        super.performDefault(event);

        if (event.getType().equals(BetterFormEventNames.SHOW)) {

            handleShow(event);

            return;
        }
        if (event.getType().equals(BetterFormEventNames.HIDE)) {

            handleHide(event);

            return;
        }
    }

    private void handleShow(Event event) {

        if (!this.visible) {
                this.visible = true;
                String current = this.container.getFocussedContainerId();
                if (current != null && !(current.equals(this.id))) {
                    try {
                        this.container.dispatch(this.container
                                .getFocussedContainerId(),
                                DOMEventNames.FOCUS_OUT);
                    } catch (XFormsException e) {
                        LOGGER.warn("silently failed DOMFocusOut");
                    }
                }
                this.container.setFocussedContainerId(this.id);
                try {
                    this.container.dispatch(getFirstFocusableControl(),
                            DOMEventNames.FOCUS_IN);
                } catch (XFormsException e) {
                    LOGGER.warn("silently failed DOMFocusIn");
                }
        } else {
            LOGGER.info("Ignoring " + event.getType() + " event for: "
                    + this.id + " Since it is already visible");
        }
    }

    private void handleHide(Event event) {
        if (this.visible) {
                this.visible = false;
                String current = this.container.getFocussedContainerId();
                if (current.equals(this.id)) {
                    try {
                        this.container.dispatch(this.container
                                .getFocussedContainerId(),
                                DOMEventNames.FOCUS_OUT);
                    } catch (XFormsException e) {
                        LOGGER.warn("silently failed DOMFocusOut");
                    }
                }
                // What is going to get focus? The other trigger that
        } else {
            LOGGER.info("Ignoring " + event.getType() + " event for: "
                    + this.id + " Since it is not visible");
        }
    }

    private String getFirstFocusableControl() throws XFormsException {
        return XPathUtil.evaluateAsString(this.element, ".//*["
                + NamespaceConstants.BETTERFORM_PREFIX + ":data/@"
                    + NamespaceConstants.BETTERFORM_PREFIX
                + ":enabled eq 'true'][1]/@id");
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
        return hasBindingExpression() ? new BoundElementState(false, false) : null;
    }

    /**
     * Performs element update.
     *
     * @throws XFormsException if any error occurred during update.
     */
    public void refresh() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " update");
        }

        //updateXPathContext();
        //updateElementState();
        updateChildren();
    }

    /**
     * Returns the logger object.
     *
     * @return the logger object.
     */
	protected Log getLogger() {
		return LOGGER;
	}
}

// end of class
