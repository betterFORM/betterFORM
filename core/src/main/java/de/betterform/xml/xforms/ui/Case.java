/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.state.UIElementStateUtil;
import de.betterform.xml.events.XFormsEventNames;
import org.w3c.dom.Element;

/**
 * Implementation of <b>9.2.2 The case Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Case.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class Case extends AbstractUIElement {
    private static Log LOGGER = LogFactory.getLog(Case.class);
    private boolean selected;
    private Element state;

    /**
     * Creates a new case element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Case(Element element, Model model) {
        super(element, model);
    }

    // case specific methods

    /**
     * Checks wether this Case is selected.
     *
     * @return <code>true</code> if this Case is selected, otherwise
     *         <code>false</code>.
     */
    public boolean isSelected() {
        return this.selected;
    }

    /**
     * Returns the Switch this Case belongs to.
     *
     * @return the Switch this Case belongs to.
     */
    public Switch getSwitch() {
        return (Switch) getParentObject();
    }

    /**
     * Selects this Case.
     * <p/>
     * Note: As per Spec Errata this only changes the state. The xf:selected
     * attribute is left untouched.
     */
    public void select() throws XFormsException {
        // update state
        this.selected = true;
        UIElementStateUtil.setStateAttribute(this.state, SELECTED_ATTRIBUTE, String.valueOf(this.selected));
        container.dispatch(this.target, XFormsEventNames.ENABLED ,null);

        // register with switch
        getSwitch().setSelected(this);
    }

    /**
     * Deselects this Case.
     * <p/>
     * Note: As per Spec Errata this only changes the state. The xf:selected
     * attribute is left untouched.
     */
    public void deselect() throws XFormsException{
        // update state
        this.selected = false;
        UIElementStateUtil.setStateAttribute(this.state, SELECTED_ATTRIBUTE, String.valueOf(this.selected));
        container.dispatch(this.target, XFormsEventNames.DISABLED ,null);
    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " init");
        }

        initializeCase();
        initializeChildren();
        initializeActions();
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

        disposeCase();
        disposeChildren();
        disposeSelf();
    }

    /**
     * Initializes this Case
     * <p/>
     * A custom data element for keeping the selection state is created.
     */
    protected final void initializeCase() {
        // todo: maybe integrate with UIElementState, but case is never bound ...
        this.state = UIElementStateUtil.createStateElement(this.element);
        this.element.setAttributeNS(null,"caseId", this.originalId);
    }

    protected void updateChildren() throws XFormsException {
        if(isSelected()){
            super.updateChildren();
        }
    }

    /**
     * Disposes this Case.
     */
    protected final void disposeCase() {
        this.element.removeChild(this.state);
    }

    // template methods

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
