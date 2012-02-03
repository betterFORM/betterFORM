/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui.state;

import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.ui.BindingElement;
import de.betterform.xml.xforms.ui.Repeat;
import de.betterform.xml.xforms.ui.UIElementState;
import org.w3c.dom.Element;

/**
 * State keeper for repeat elements.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RepeatElementState.java 3492 2008-08-27 12:37:01Z joern $
 */
public class RepeatElementState implements UIElementState {

    private Repeat owner;
    private Element state;
    private boolean[] currentProperties;

    /**
     * Creates a new repeat element state.
     */
    public RepeatElementState() {
        // NOP
    }

    /**
     * Sets the owning element.
     *
     * @param owner the owning element.
     */
    public void setOwner(BindingElement owner) {
        this.owner = (Repeat) owner;
    }

    // implementation of 'de.betterform.xml.xforms.ui.UIElementState'

    /**
     * Initializes this element state.
     *
     * @throws XFormsException if an error occurred during init.
     */
    public void init() throws XFormsException {
        // create state element and append repeat prototype
        this.state = UIElementStateUtil.createStateElement(this.owner.getElement());
        this.state.appendChild(this.owner.getPrototype());

        // set enabled property and xpath attribute only before xforms-ready (not during repeat processing)
        if (!this.owner.getModel().isReady()) {
            ModelItem modelItem = UIElementStateUtil.getModelItem(this.owner);
            boolean[] properties = UIElementStateUtil.getModelItemProperties(null);
            properties[UIElementStateUtil.ENABLED] = modelItem != null;

            UIElementStateUtil.setStateAttribute(this.state, ENABLED_PROPERTY, String.valueOf(properties[UIElementStateUtil.ENABLED]));

            // keep properties
            this.currentProperties = properties;
        }
    }

    /**
     * Updates this element state.
     *
     * @throws XFormsException if an error occurred during update.
     */
    public void update() throws XFormsException {
        // update enabled property and xpath attribute
        ModelItem modelItem = UIElementStateUtil.getModelItem(this.owner);
        boolean[] properties = UIElementStateUtil.getModelItemProperties(null);
        properties[UIElementStateUtil.ENABLED] = modelItem != null;

        UIElementStateUtil.setStateAttribute(this.state, ENABLED_PROPERTY, String.valueOf(properties[UIElementStateUtil.ENABLED]));

        // dispatch property change events
        UIElementStateUtil.dispatchBetterFormEvents(this.owner, this.currentProperties, properties);

        // keep properties
        this.currentProperties = properties;
    }

    /**
     * Disposes this element state.
     *
     * @throws XFormsException if an error occurred during disposal.
     */
    public void dispose() throws XFormsException {
        // remove any content and free resources
        this.state.getParentNode().removeChild(this.state);
        this.state = null;
        this.owner = null;
    }

    /**
     * Returns the current value.
     *
     * @return the current value.
     */
    public String getValue() {
        return null;
    }


    public Object getSchemaValue() {
        return null;
    }


    /**
     * Sets an arbitrary property.
     * <p/>
     * Only <code>index</code> is recognized and updated immediately.
     *
     * @param name the property name.
     * @param value the property value.
     */
    public void setProperty(String name, Object value) {
        if (("index").equals(name)) {
            UIElementStateUtil.setStateAttribute(this.state, "index", String.valueOf(value));
        }
    }

}

// end of class
