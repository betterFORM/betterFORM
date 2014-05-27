/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.constraints;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

    private String statusText = "OK";

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

    public String getStatusText() {
    	return this.statusText;
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
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("validate: item " + modelItem.getNode() + " is unmodified: validation skipped");
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

        boolean valid = modelItem.isValid();
        
        
        if (valid && modelItem.isRequired()) {
        	if (modelItem.getValue().length() == 0 &&
        		(! containsSubElement(modelItem.getNode())) ) {
            		valid = false;
        	}
        }

        if (!valid) {
        	this.statusText = modelItem.getNode() + " is invalid or required but empty: validation stopped";

        	if (LOGGER.isDebugEnabled()) {
                LOGGER.warn("validate: item " + this.statusText);
            }

            // stop validation on invalid or reqiured-but-empty item
            this.discontinued = true;
            return false;
        }

        // default
        return true;
    }

    private boolean containsSubElement(Object object) {
		if (object instanceof Element) {
			Element e = (Element)object;
			NodeList nl = e.getChildNodes();
			for (int i=0; i<nl.getLength(); i++) {
				Node n = nl.item(i);
				if (n.getNodeType() == Node.ELEMENT_NODE) {
					return true;
				}
			}
		}

		return false;
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
