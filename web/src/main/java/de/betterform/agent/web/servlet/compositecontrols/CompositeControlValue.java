/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.servlet.compositecontrols;

/**
 * Some XForms controls may actually be made up of several
 * html controls. These controls are known as composite controls.
 * The value of a composite control is the value of several html
 * controls put togther in a pre-defined manner.
 *
 * @author Adam Retter <adam.retter@devon.gov.uk>
 */
public interface CompositeControlValue {
    /**
     * id prefix for the control
     */
    public final static String prefix = "c_";

    /**
     * Set part of the composite controls value
     * i.e. the value from one of the child html controls
     */
    public void setPart(String id, String value);

    /**
     * Have all fields of the composite control been completed?
     */
    public boolean isComplete();

    /**
     * String representation of the composite controls value
     */
    public String toString();
}