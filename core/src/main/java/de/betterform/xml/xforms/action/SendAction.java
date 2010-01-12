/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.submission.Submission;
import org.w3c.dom.Element;

/**
 * Implements the action as defined in <code>10.1.10 The send Element</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: SendAction.java 3457 2008-08-13 15:03:54Z joern $
 */
public class SendAction extends AbstractAction {
    private static Log LOGGER = LogFactory.getLog(SendAction.class);
    private String submissionAttribute = null;

    /**
     * Creates a send action implementation.
     *
     * @param element the element.
     * @param model the context model.
     */
    public SendAction(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        super.init();

        this.submissionAttribute = getXFormsAttribute(SUBMISSION_ATTRIBUTE);
        if (this.submissionAttribute == null) {
            throw new XFormsBindingException("missing submission attribute at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, null);
        }
    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>send</code> action.
     *
     * @throws XFormsException if an error occurred during <code>send</code>
     * processing.
     */
    public void perform() throws XFormsException {
        // check submission idref
        Object submissionObject = this.container.lookup(this.submissionAttribute);
        if (submissionObject == null || !(submissionObject instanceof Submission)) {
            throw new XFormsBindingException("invalid submission id at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, this.submissionAttribute);
        }

        // dispatch xforms-submit to submission
        this.container.dispatch(((Submission) submissionObject).getTarget(), XFormsEventNames.SUBMIT, null);
    }

    /**
     * Returns the logger object.
     *
     * @return the logger object.
     */
    protected Log getLogger() {
        return LOGGER;
    }
}

// end of class
