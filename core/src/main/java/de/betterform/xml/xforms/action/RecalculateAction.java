/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;

/**
 * Implements the action as defined in <code>10.1.4 The recalculate
 * Element</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RecalculateAction.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class RecalculateAction extends AbstractAction {
    private static final Log LOGGER = LogFactory.getLog(RecalculateAction.class);

    /**
     * Creates a recalculate action implementation.
     *
     * @param element the element.
     * @param model the context model.
     */
    public RecalculateAction(Element element, Model model) {
        super(element, model);
    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>recalculate</code> action.
     *
     * @throws XFormsException if an error occurred during
     * <code>recalculate</code> processing.
     */
    public void perform() throws XFormsException {
        // recalculate model
        this.model.recalculate();

        // update behavior
        doRecalculate(false);
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
