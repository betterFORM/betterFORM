/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;

/**
 * Implements the action as defined in <code>9.3.5 The insert Element</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: InsertAction.java 2629 2007-08-07 15:38:48Z lars $
 */
public class InsertAction11 extends InsertAction {
    private static final Log LOGGER = LogFactory.getLog(InsertAction11.class);

    /**
     * Creates an insert action implementation.
     *
     * @param element the element.
     * @param model   the context model.
     */
    public InsertAction11(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     */
    public void init() throws XFormsException {
        super.init();
    }


    /**
     * Performs the <code>insert</code> action.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if an error occurred during <code>insert</code>
     *          processing.
     */
    public void perform() throws XFormsException {
        // XXX implement XForms 1.1 insert action
        super.perform();
    }
}


