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
 * Implements the action as defined in <code>10.1.3 The rebuild Element</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RebuildAction.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class RebuildAction extends AbstractAction {
    private static final Log LOGGER = LogFactory.getLog(RebuildAction.class);

    /**
     * Creates a rebuild action implementation.
     *
     * @param element the element.
     * @param model the context model.
     */
    public RebuildAction(Element element, Model model) {
        super(element, model);
    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>rebuild</code> action.
     *
     * @throws XFormsException if an error occurred during <code>rebuild</code>
     * processing.
     */
    public void perform() throws XFormsException {
        // rebuild model
        this.model.rebuild();

        // update behaviour
        doRebuild(false);
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
