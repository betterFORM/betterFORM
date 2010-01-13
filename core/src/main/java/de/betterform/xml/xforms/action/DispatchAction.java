/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.submission.AttributeOrValueChild;
import de.betterform.xml.xforms.ui.Output;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * Implements the action as defined in <code>10.1.2 The dispatch
 * Element</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: DispatchAction.java 3457 2008-08-13 15:03:54Z joern $
 */
public class DispatchAction extends AbstractAction {
    private static final Log LOGGER = LogFactory.getLog(DispatchAction.class);
    private AttributeOrValueChild eventName = null;
    private AttributeOrValueChild eventTarget = null;
    private AttributeOrValueChild delay = null;
    private boolean bubbles = true;
    private boolean cancelable = true;
    

    /**
     * Creates a dispatch action implementation.
     *
     * @param element the element.
     * @param model the context model.
     */
    public DispatchAction(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     */
    public void init() throws XFormsException {
        super.init();

        this.eventName= new AttributeOrValueChild(this.element, this.model, NAME_ATTRIBUTE);
        this.eventName.init();

        this.eventTarget = new AttributeOrValueChild(this.element, this.model,TARGETID_ATTRIBUTE);
        this.eventTarget.init();
        if (!this.eventTarget.isAvailable()) {
            throw new XFormsBindingException("missing targetid attribute or child at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, null);
        }

        String bubblesAttribute = getXFormsAttribute(BUBBLES_ATTRIBUTE);
        if (bubblesAttribute != null) {
            this.bubbles = Boolean.valueOf(bubblesAttribute).booleanValue();
        }

        String cancelableAttribute = getXFormsAttribute(CANCELABLE_ATTRIBUTE);
        if (cancelableAttribute != null) {
            this.cancelable = Boolean.valueOf(cancelableAttribute).booleanValue();
        }
        this.delay = new AttributeOrValueChild(this.element, this.model,DELAY);
        this.delay.init();


    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>dispatch</code> action.
     *
     * @throws XFormsException if an error occurred during <code>dispatch</code>
     * processing.
     */
    public void perform() throws XFormsException {
        if(this.delay.getValue() != null && this.delay.getValue() != ""){
            long sleep = Long.parseLong(this.delay.getValue());
            try {
                Thread.sleep(sleep);
            }catch(Exception e) {
                throw new XFormsException(e);
            }
        }
        // dispatch specified event
        if(this.eventName != null) {
            this.container.dispatch(this.eventTarget.getValue(), this.eventName.getValue(), null, this.bubbles, this.cancelable);
        }
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
