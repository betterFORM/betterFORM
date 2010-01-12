/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.model.constraints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.xforms.model.ModelItem;

/**
 * Submission validator mode for instance validation during
 * <code>xforms-submit</code> processing.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: SubmissionValidatorMode.java 2873 2007-09-28 09:08:48Z lars $
 */
public class SubmissionValidatorMode implements ValidatorMode {
    private static Log LOGGER = LogFactory.getLog(SubmissionValidatorMode.class);

    private boolean discontinued = false;

    // member access

    /**
     * Checks wether the validation process has been discontinued by method
     * <code>continueValidation(ModelItem)</code> returning <code>false</code>.
     *
     * @return <code>true</true> if the validation process has been
     *         discontinued, <code>false</code> otherwise.
     */
    public boolean isDiscontinued() {
        return this.discontinued;
    }

    // implementation of 'de.betterform.xml.xforms.model.constraints.ValidatorMode'

    /**
     * Decide wether a particular model item has to be validated.
     *
     * @param modelItem the model item to be validated.
     * @return <code>true</true> if the model item is enabled or its value has
     * changed, <code>false</code> otherwise.
     */
    public boolean performValidation(ModelItem modelItem) {
        if (!modelItem.isRelevant()) {
            // ignore disabled items until submission validates only enabled items
            return false;
        }

        if (!modelItem.getStateChangeView().hasValueChanged()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("validate: item " + modelItem.getNode() + " is unmodified: validation skipped");
            }

            // skip unmodified item for performance reasons
            return false;
        }

        // default
        return true;
    }

    /**
     * Decide wether validation has to be continued after a particular model
     * item has been validated.
     *
     * @param modelItem the model item which has been validated.
     * @return <code>true</true> if the model item is found to be valid,
     *         <code>false</code> otherwise.
     */
    public boolean continueValidation(ModelItem modelItem) {
        if (!modelItem.isRelevant()) {
            // don't care for disabled items until submission validates enabled items only
            return true;
        }

        if (!modelItem.isValid() || (modelItem.isRequired() && modelItem.getValue().length() == 0)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("validate: item " + modelItem.getNode() + " is invalid or required but empty: validation stopped");
            }

            // stop validation on invalid or reqiured-but-empty item
            this.discontinued = true;
            return false;
        }

        // default
        return true;
    }

    // standard methods

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object.
     */
    public String toString() {
        return "submission";
    }

}
