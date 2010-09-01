/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.bind;

import de.betterform.xml.xforms.exception.XFormsException;

import java.util.List;


/**
 * This interface models a binding element as referred to in chapter
 * <code>7.3 Evaluation Context</code> of the spec.
 *
 * @version $Id: Binding.java 3474 2008-08-15 22:29:43Z joern $
 */
public interface Binding {
    /**
     * Returns the binding expression.
     *
     * @return the binding expression.
     */
    String getBindingExpression();
    
    /**
     * Returns the context expression.
     *
     * @return the context expression.
     */
    String getContextExpression();

    /**
     * Returns the id of the binding element.
     *
     * @return the id of the binding element.
     */
    String getBindingId();

    /**
     * Returns the enclosing element.
     *
     * @return the enclosing element.
     */
    Binding getEnclosingBinding();

    /**
     * Returns the location path.
     *
     * @return the location path.
     */
    String getLocationPath();

    /**
     * Returns the model id of the binding element.
     *
     * @return the model id of the binding element.
     */
    String getModelId();


    List getNodeset();
    
    int getPosition();

    boolean hasBindingExpression();
    
    String getInstanceId() throws XFormsException;
}

