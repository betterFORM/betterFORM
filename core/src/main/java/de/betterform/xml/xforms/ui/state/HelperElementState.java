/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui.state;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.ui.BindingElement;
import de.betterform.xml.xforms.ui.UIElementState;

/**
 * State keeper for helper elements.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: HelperElementState.java 3492 2008-08-27 12:37:01Z joern $
 */
public class HelperElementState implements UIElementState {

    private BindingElement owner;
    private String currentValue;

    /**
     * Creates a new output value state.
     */
    public HelperElementState() {
        // NOP
    }

    /**
     * Sets the owning element.
     *
     * @param owner the owning element.
     */
    public void setOwner(BindingElement owner) {
        this.owner = owner;
    }

    // implementation of 'de.betterform.xml.xforms.ui.UIElementState'

    /**
     * Initializes this element state.
     *
     * @throws XFormsException if an error occurred during init.
     */
    public void init() throws XFormsException {
        // remove any content
        DOMUtil.removeAllChildren(this.owner.getElement());

        // set model item value only before xforms-ready (not during repeat processing)
        if (!this.owner.getModel().isReady()) {
            ModelItem modelItem = UIElementStateUtil.getModelItem(this.owner);
            if (modelItem != null) {
                String value = modelItem.getValue();
                DOMUtil.setElementValue(this.owner.getElement(), value);

                // keep value
                this.currentValue = value;
            }
        }
    }

    /**
     * Updates this element state.
     *
     * @throws XFormsException if an error occurred during update.
     */
    public void update() throws XFormsException {
        // update model item value
        ModelItem modelItem = UIElementStateUtil.getModelItem(this.owner);
        String value = modelItem != null ? modelItem.getValue() : null;
        DOMUtil.setElementValue(this.owner.getElement(), value);

        // dispatch value change events
        UIElementStateUtil.dispatchBetterFormEvents(this.owner, this.currentValue, value);

        // keep value
        this.currentValue = value;
    }

    /**
     * Disposes this element state.
     *
     * @throws XFormsException if an error occurred during disposal.
     */
    public void dispose() throws XFormsException {
        // remove any content and free resources
        DOMUtil.removeAllChildren(this.owner.getElement());
        this.owner = null;
    }

    /**
     * Returns the current value.
     *
     * @return the current value.
     */
    public String getValue() {
        return this.currentValue;
    }

    /**
     * Sets an arbitrary property.
     * <p/>
     * Does nothing in this implementation.
     *
     * @param name the property name.
     * @param value the property value.
     */
    public void setProperty(String name, Object value) {
    }

    public Object getSchemaValue(){
        //todo: review this again
        return null;
    }


}

// end of class
