/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.ui.state.CopyElementState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

/**
 * Implementation of <b>9.3.4 The copy Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Copy.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class Copy extends BindingElement {
    private static final Log LOGGER = LogFactory.getLog(Copy.class);

    /**
     * Creates a new copy element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Copy(Element element, Model model) {
        super(element, model);
    }

    // template methods

    /**
     * Factory method for the element state.
     *
     * @return an element state implementation or <code>null</code> if no state
     *         keeping is required.
     * @throws XFormsException if an error occurred during creation.
     */
    protected UIElementState createElementState() throws XFormsException
    {
    	CopyElementState state = new CopyElementState();
    	
        return state;
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
