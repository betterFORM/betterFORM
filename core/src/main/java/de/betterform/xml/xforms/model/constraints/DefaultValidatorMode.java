/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.model.constraints;

import de.betterform.xml.xforms.model.ModelItem;

/**
 * Default validator mode for instance validation during
 * <code>xforms-revalidate</code> processing.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: DefaultValidatorMode.java 2100 2006-03-28 16:36:43Z unl $
 */
public class DefaultValidatorMode implements ValidatorMode {

    // implementation of 'de.betterform.xml.xforms.model.constraints.ValidatorMode'

    /**
     * Decide wether a particular model item has to be validated.
     *
     * @param modelItem the model item to be validated.
     * @return <code>true</true>.
     */
    public boolean performValidation(ModelItem modelItem) {
        return true;
    }

    /**
     * Decide wether validation has to be continued after a particular model
     * item has been validated.
     *
     * @param modelItem the model item which has been validated.
     * @return <code>true</true>.
     */
    public boolean continueValidation(ModelItem modelItem) {
        return true;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object.
     */
    public String toString() {
        return "default";
    }

}
