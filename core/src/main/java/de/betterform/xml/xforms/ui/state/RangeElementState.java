/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.ui.state;

import de.betterform.xml.xforms.XFormsConstants;
// end of class

public class RangeElementState extends BoundElementState {

    /**
     * Sets arbitrary properties.
     * <p/>
     * Only <code>value, start, end and step</code> are reccognized.
     *
     * @param name the property name.
     * @param value the property value.
     */
    public void setProperty(String name, Object value) {
        if (XFormsConstants.VALUE_ATTRIBUTE .equals(name)) {
             UIElementStateUtil.setStateAttribute(this.state, name,((String)value));
        }
        else if (XFormsConstants.START_ATTRIBUTE .equals(name)) {
             UIElementStateUtil.setStateAttribute(this.state, name,((String)value));
        }
        else if (XFormsConstants.STEP_ATTRIBUTE .equals(name)) {
             UIElementStateUtil.setStateAttribute(this.state, name,((String)value));
        }
        else if (XFormsConstants.END_ATTRIBUTE .equals(name)) {
             UIElementStateUtil.setStateAttribute(this.state, name,((String)value));
        }
    }

}
