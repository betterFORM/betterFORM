/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.xforms.exception.XFormsException;

/**
 * UI Element state.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: UIElementState.java 3492 2008-08-27 12:37:01Z joern $
 */
public interface UIElementState {

    static final String STATE_ELEMENT = "data";

    static final String VALID_PROPERTY = "valid";
    static final String READONLY_PROPERTY = "readonly";
    static final String REQUIRED_PROPERTY = "required";
    static final String ENABLED_PROPERTY = "enabled";

    static final String TYPE_ATTRIBUTE = "type";
    static final String P3PTYPE_ATTRIBUTE = "p3ptype";
    static final String VALUE = "value";
    static final String SCHEMA_VALUE = "schema-value";
    static final String BASE_TYPE = "base-type";

    /**
     * Sets the owning element.
     *
     * @param owner the owning element.
     */
    void setOwner(BindingElement owner);

    /**
     * Initializes this element state.
     *
     * @throws XFormsException if an error occurred during init.
     */
    void init() throws XFormsException;

    /**
     * Updates this element state.
     *
     * @throws XFormsException if an error occurred during update.
     */
    void update() throws XFormsException;

    /**
     * Disposes this element state.
     *
     * @throws XFormsException if an error occurred during disposal.
     */
    void dispose() throws XFormsException;

    /**
     * Returns the current value.
     *
     * @return the current value.
     */
    Object getValue();

    Object getSchemaValue();
    

    /**
     * Sets an arbitrary property. Behaviour is implementation specific.
     *
     * @param name the property name.
     * @param value the property value.
     */
    void setProperty(String name, Object value);

}
