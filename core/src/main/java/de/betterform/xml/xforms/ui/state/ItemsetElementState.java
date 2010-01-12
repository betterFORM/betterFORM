/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.ui.state;

import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.BindingElement;
import de.betterform.xml.xforms.ui.Itemset;
import de.betterform.xml.xforms.ui.UIElementState;
import org.w3c.dom.Element;

/**
 * State keeper for itemset elements.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ItemsetElementState.java 3492 2008-08-27 12:37:01Z joern $
 */
public class ItemsetElementState implements UIElementState {

    private Itemset owner;
    private Element state;

    /**
     * Creates a new itemset element state.
     */
    public ItemsetElementState() {
        // NOP
    }

    /**
     * Sets the owning element.
     *
     * @param owner the owning element.
     */
    public void setOwner(BindingElement owner) {
        this.owner = (Itemset) owner;
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
    }

    /**
     * Updates this element state.
     *
     * @throws XFormsException if an error occurred during update.
     */
    public void update() throws XFormsException {
        // NOP
    }

    /**
     * Disposes this element state.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException if an error
     * occurred during disposal.
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
     * Does nothing in this implementation.
     *
     * @param name the property name.
     * @param value the property value.
     */
    public void setProperty(String name, Object value) {
    }

}

// end of class
