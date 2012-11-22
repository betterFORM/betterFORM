/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.bind;

import java.util.Map;

/**
 * Declaration viewport to model items. Provides access to declarations of a
 * model item's properties. 
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: DeclarationView.java 2090 2006-03-16 09:37:00Z joernt $
 */
public interface DeclarationView {

    /**
     * Returns the <code>type</code> declaration of a model item.
     *
     * @return the <code>type</code> declaration of a model item.
     */
    String getDatatype();

    /**
     * Sets the <code>type</code> declaration of a model item.
     *
     * @param datatype the <code>type</code> declaration of a model item.
     */
    void setDatatype(String datatype);

    /**
     * Returns the <code>readonly</code> declaration of a model item.
     *
     * @return the <code>readonly</code> declaration of a model item.
     */
    String getReadonly();

    /**
     * Sets the <code>readonly</code> declaration of a model item.
     *
     * @param readonly the <code>readonly</code> declaration of a model item.
     */
    void setReadonly(String readonly);

    /**
     * Returns the <code>required</code> declaration of a model item.
     *
     * @return the <code>required</code> declaration of a model item.
     */
    String getRequired();

    /**
     * Sets the <code>required</code> declaration of a model item.
     *
     * @param required the <code>required</code> declaration of a model item.
     */
    void setRequired(String required);

    /**
     * Returns the <code>relevant</code> declaration of a model item.
     *
     * @return the <code>relevant</code> declaration of a model item.
     */
    String getRelevant();

    /**
     * Sets the <code>relevant</code> declaration of a model item.
     *
     * @param relevant the <code>relevant</code> declaration of a model item.
     */
    void setRelevant(String relevant);

    /**
     * Returns the <code>calculate</code> declaration of a model item.
     *
     * @return the <code>calculate</code> declaration of a model item.
     */
    String getCalculate();

    /**
     * Sets the <code>calculate</code> declaration of a model item.
     *
     * @param calculate the <code>calculate</code> declaration of a model item.
     */
    void setCalculate(String calculate);

    /**
     * Returns the <code>constraint</code> declaration of a model item.
     *
     * @return the <code>constraint</code> declaration of a model item.
     */
    String getConstraint();

    /**
     * Sets the <code>constraint</code> declaration of a model item.
     *
     * @param constraint the <code>constraint</code> declaration of a model
     * item.
     */
    void setConstraint(String constraint);

    /**
     * Returns the <code>p3ptype</code> declaration of a model item.
     *
     * @return the <code>p3ptype</code> declaration of a model item.
     * @deprecated without replacement
     */
    String getP3PType();

    /**
     * Sets the <code>p3ptype</code> declaration of a model item.
     *
     * @param p3ptype the <code>p3ptype</code> declaration of a model item.
     * @deprecated without replacement
     */
    void setP3PType(String p3ptype);
        
    Map<String, String> getCustomMIPs();
    
    void setCustomMIPs(Map<String, String> customMIPs);
    
}
