/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.submission.Submission;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

/**
 * Implementation of <b>8.1.9 The submit Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Submit.java 3457 2008-08-13 15:03:54Z joern $
 */
public class Submit extends Trigger implements EventListener {
    private static final Log LOGGER = LogFactory.getLog(Submit.class);
    private String submissionId = null;

    /**
     * Creates a new submit element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Submit(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace(this + " init");
        }
        

        initializeDefaultAction();
        initializeInstanceNode();
        updateXPathContext();
        initializeSubmit();
        initializeElementState();
        initializeChildren();
        initializeActions();
    }

    @Override
    protected void initializeDefaultAction() {
        super.initializeDefaultAction();
        this.container.getXMLEventService().registerDefaultAction(this.target, DOMEventNames.ACTIVATE, this);
    }

    @Override
    public void performDefault(Event event) {
        super.performDefault(event);
        if (event.getType().equals(DOMEventNames.ACTIVATE)) {
            try {
                if(this.submissionId != null && this.container.lookup(this.submissionId) != null) {
                	this.container.dispatch(this.submissionId, XFormsEventNames.SUBMIT, null);
                }
            } catch (XFormsException e) {
                this.container.handleEventException(e);
            }
        }
    }

    /**
     * Performs element disposal.
     *
     * @throws XFormsException if any error occurred during disposal.
     */
    public void dispose() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " dispose");
        }

        disposeDefaultAction();
        disposeChildren();
        disposeElementState();
        disposeSubmit();
        disposeSelf();
    }

    // form control methods

    /**
     * Sets the value of this form control.
     * <p/>
     * If this method is called a warning is issued since the value of a
     * <code>submit</code> control cannot be set.
     *
     * @param value the value to be set.
     */
    public void setValue(String value) throws XFormsException {
        getLogger().warn(this + " set value: the value of a submit control cannot be set");
    }

    // event handling methods

    /**
     * This method is called whenever an event occurs of the type for which the
     * <code>EventListener</code> interface was registered.
     *
     * @param event The <code>Event</code> contains contextual information about
     * the event. It also contains the <code>stopPropagation</code> and
     * <code>preventDefault</code> methods which are used in determining the
     * event's flow and default action.
     */
    public void handleEvent(Event event) {

    }

    // lifecycle template methods

    /**
     * Initializes the submit element.
     */
    protected final void initializeSubmit() throws XFormsException {
        this.submissionId = getXFormsAttribute(SUBMISSION_ATTRIBUTE);
        if (this.submissionId == null) {
            throw new XFormsBindingException("missing submission attribute at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, null);
        }

        Object submission = this.container.lookup(this.submissionId);
        if (submission != null && submission instanceof Submission) {
            this.target.addEventListener(DOMEventNames.ACTIVATE, this, false);
            // throw new XFormsBindingException("invalid submission id at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, this.submissionId);
        }
    }

    /**
     * Disposes the submit element.
     */
    protected final void disposeSubmit() {
        this.submissionId = null;
        this.target.removeEventListener(DOMEventNames.ACTIVATE, this, false);
    }

    // template methods

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
