/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.bind;

/**
 * Local Update viewport to model items. Provides access to local values of a
 * model item's properties.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: LocalUpdateView.java 2090 2006-03-16 09:37:00Z joernt $
 */
public interface LocalUpdateView {

    /**
     * Returns the local <code>datatype valid</code> state of a model item.
     *
     * @return the local <code>datatype valid</code> state of a model item.
     */
    boolean isDatatypeValid();

    /**
     * Sets the datatype valid state of a model item.
     *
     * @param datatypeValid the local <code>datatype valid</code> state of a
     * model item.
     */
    void setDatatypeValid(boolean datatypeValid);

    /**
     * Returns the local <code>readonly</code> state of a model item.
     *
     * @return the local <code>readonly</code> state of a model item.
     */
    boolean isLocalReadonly();

    /**
     * Sets the local <code>readonly</code> state of a model item.
     *
     * @param localReadonly the local <code>readonly</code> state of a model
     * item.
     */
    void setLocalReadonly(boolean localReadonly);

    /**
     * Returns the <code>required</code> state state of a model item.
     *
     * @return the <code>required</code> state state of a model item.
     */
    boolean isLocalRequired();

    /**
     * Sets the <code>required</code> state state of a model item.
     *
     * @param localRequired the <code>required</code> state state of a model
     * item.
     */
    void setLocalRequired(boolean localRequired);

    /**
     * Returns the <code>relevant</code> state state of a model item.
     *
     * @return the <code>relevant</code> state state of a model item.
     */
    boolean isLocalRelevant();

    /**
     * Sets the <code>relevant</code> state state of a model item.
     *
     * @param localRelevant the <code>relevant</code> state state of a model
     * item.
     */
    void setLocalRelevant(boolean localRelevant);

    /**
     * Returns the local <code>constraint valid</code> state of a model item.
     *
     * @return the local <code>constraint valid</code> state of a model item.
     */
    boolean isConstraintValid();

    /**
     * Sets the constraint valid state of a model item.
     *
     * @param constraintValid the local <code>constraint valid</code> state of a
     * model item.
     */
    void setConstraintValid(boolean constraintValid);

}
