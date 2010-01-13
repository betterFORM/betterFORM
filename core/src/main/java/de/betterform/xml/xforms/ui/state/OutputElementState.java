/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.xforms.ui.state;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.config.Config;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.BindingElement;
import de.betterform.xml.xforms.ui.Output;
import de.betterform.xml.xforms.ui.UIElementState;
import org.w3c.dom.Element;

/**
 * State keeper for output elements. This is only used in case the Output is not bound - in
 * that case the Output has a BoundElementState.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: OutputElementState.java 3492 2008-08-27 12:37:01Z joern $
 * @see de.betterform.xml.xforms.ui.state.BoundElementState
 */
public class OutputElementState implements UIElementState {

    private Output owner;
    private Element state;
    private String currentValue;

    /**
     * Creates a new output value state.
     */
    public OutputElementState() {
        // NOP
    }

    /**
     * Sets the owning element.
     *
     * @param owner the owning element.
     */
    public void setOwner(BindingElement owner) {
        this.owner = (Output) owner;
    }

    // implementation of 'de.betterform.xml.xforms.ui.UIElementState'

    /**
     * Initializes this element state.
     *
     * @throws XFormsException if an error occurred during init.
     */
    public void init() throws XFormsException {
        // create state element
        this.state = UIElementStateUtil.createStateElement(this.owner.getElement());

        // fake type and xpath
        String datatype = UIElementStateUtil.getDefaultDatatype(this.state);
        UIElementStateUtil.setStateAttribute(this.state, TYPE_ATTRIBUTE, datatype);

        // compute output value only before xforms-ready (not during repeat processing)
        if (!this.owner.getModel().isReady()) {
            //todo: must be changed to handle a Node as Child
            Object valueObject = this.owner.computeValueAttribute();
            if (valueObject != null) {
                String value = valueObject.toString();
                UIElementStateUtil.setStateAttribute(this.state, "schema-value", value);

                try {
                    value = attemptLocalisation(value);
                } catch (Exception e) {
                    throw new XFormsException("Can't parse value for localisation at: " + DOMUtil.getCanonicalPath(this.owner.getElement()),e);
                }


                DOMUtil.setElementValue(this.state, value);

                // keep value
                this.currentValue = value;
            }
        }
    }

    private String attemptLocalisation(String value) throws Exception {
        //attempt to interpret value as number
        if(value != null && Config.getInstance().getProperty(XFormsProcessorImpl.BETTERFORM_ENABLE_L10N).equals("true")) {
            try {
                Double.parseDouble(value);
                value = UIElementStateUtil.localiseValue(this.owner,this.state, "Double", value);
            } catch (NumberFormatException e) {
                //ignore
            }
        }
        return value;
    }

    /**
     * Updates this element state.
     *
     * @throws XFormsException if an error occurred during update.
     */
    public void update() throws XFormsException {
        // compute output value
        //todo: must be changed to handle a Node as Child
        Object valueObject = this.owner.computeValueAttribute();
        String value = valueObject != null ? valueObject.toString() : null;
/*
        try {
            value = attemptLocalisation(value);
        } catch (ParseException e) {
            throw new XFormsException(e);
        }
*/
        UIElementStateUtil.setStateAttribute(this.state, "schema-value", value);                               
        DOMUtil.setElementValue(this.state, value);

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
        //TODO: instanceof??
        UIElementStateUtil.setStateAttribute(this.state, name, (String) value);
    }

    public Object getSchemaValue() {
        return UIElementStateUtil.getStateAttribute(this.state, UIElementState.SCHEMA_VALUE);
    }

}

// end of class
