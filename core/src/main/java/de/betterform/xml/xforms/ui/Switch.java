/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.ui.state.BoundElementState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Implementation of <b>9.2.1 The switch Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Switch.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class Switch extends BindingElement {
    private static final Log LOGGER = LogFactory.getLog(Switch.class);
    private Case selected = null;
    private boolean initAfterReady = false;

    /**
     * Creates a new switch element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Switch(Element element, Model model) {
        super(element, model);
    }

    // switch specific methods

    /**
     * Returns the currently selected Case.
     *
     * @return the currently selected Case.
     */
    public Case getSelected() {
        return this.selected;
    }

    /**
     * Sets the currently selected Case.
     *
     * @param selected the the currently selected Case.
     */
    public void setSelected(Case selected) {
        this.selected = selected;
    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        super.init();
        initializeSwitch();
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

        updateElementState();
        updateChildren();
        updateSwitch();
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

        disposeDefaultAction();
        disposeChildren();
        disposeElementState();
        disposeSwitch();
        disposeSelf();
    }

    // lifecycle template methods

    /**
     * Initializes the Case elements.
     * <p/>
     * If multiple Cases within a Switch are selected, the first selected Case
     * remains and all others are deselected. If none are selected, the first
     * becomes selected.
     */
    protected final void initializeSwitch() throws XFormsException {
        NodeList childNodes = getElement().getChildNodes();
        List cases = new ArrayList(childNodes.getLength());

        Node node;
        Case caseElement;
        String selectedAttribute;
        int selection = -1;

        for (int index = 0; index < childNodes.getLength(); index++) {
            node = childNodes.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE &&
                    NamespaceConstants.XFORMS_NS.equals(node.getNamespaceURI()) &&
                    CASE.equals(node.getLocalName())) {
                caseElement = (Case) ((Node) node).getUserData("");
                cases.add(caseElement);

                selectedAttribute = caseElement.getXFormsAttribute(SELECTED_ATTRIBUTE);
                if ((selection == -1) && ("true").equals(selectedAttribute)) {
                    // keep *first* selected case position
                    selection = cases.size() - 1;
                }
            }
        }

        if (selection == -1) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this + " init: choosing first case for selection by default");
            }

            // select first case if none is selected
            selection = 0;
        }

        // perform selection/deselection
        for (int index = 0; index < cases.size(); index++) {
            caseElement = (Case) cases.get(index);

            if (index == selection) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(this + " init: selecting case '" + caseElement.getId() + "'");
                }
                caseElement.select();
            }
            else {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(this + " init: deselecting case '" + caseElement.getId() + "'");
                }
                caseElement.deselect();
            }
        }

        // set state for updateSwitch()
        this.initAfterReady = this.model.isReady();
    }

    /**
     * Updates the Switch element.
     */
    protected final void updateSwitch() throws XFormsException {
        // if init happened after xforms-ready we are are part of repeat insert
        // processing so we have to dispatch an event to propagate the initial
        // selection state
        if (this.initAfterReady) {
            // dispatch internal betterform event
            HashMap map = new HashMap();
            map.put("selected", this.selected.getId());
            this.container.dispatch(this.target, BetterFormEventNames.SWITCH_TOGGLED, map);

            // reset state to prevent event being dispatched more than once
            this.initAfterReady = false;
        }
    }

    /**
     * Disposes the Switch element.
     */
    protected final void disposeSwitch() {
        this.selected = null;
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
     * Returns the logger object.
     *
     * @return the logger object.
     */
    protected Log getLogger() {
        return LOGGER;
    }

}

// end of class
