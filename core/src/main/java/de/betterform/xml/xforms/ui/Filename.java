/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of <b>8.3.1 The filename Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Filename.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class Filename extends BindingElement {
    private static final Log LOGGER = LogFactory.getLog(Filename.class);

    /**
     * Creates a new filename element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Filename(Element element, Model model) {
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

        initializeInstanceNode();
        ((Upload) getParentObject()).setFilename(this);
    }

    // filename specific methods

    /**
     * Returns the value of this helper element.
     *
     * @return the value of this helper element.
     */
    public String getValue() throws XFormsException {
        try {
            if (isBound()) {
                return getNodeValue();
            }
        }catch (XFormsException e) {
            LOGGER.warn("Node value does not exist");
        }

        return null;
    }

    /**
     * Sets the value of this helper element.
     * <p/>
     * The bound instance data is updated, but no events are dispatched.
     *
     * @param value the value to be set.
     */
    public void setValue(String value) throws XFormsException {
        if (isBound()) {
            setNodeValue(value);
        }
    }

    // template methods

    /**
     * Factory method for the element state.
     *
     * @return an element state implementation or <code>null</code> if no state
     *         keeping is required.
     * @throws XFormsException if an error occurred during creation.
     */
    protected UIElementState createElementState() throws XFormsException {
        return null;
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
