/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

/**
 * Implementation of <b>8.3.2 The mediatype Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Mediatype.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class Mediatype extends BindingElement {
    private static final Log LOGGER = LogFactory.getLog(Mediatype.class);

    /**
     * Creates a new mediatype element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Mediatype(Element element, Model model) {
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
        XFormsElement xFormsElement = getParentObject();
        if(xFormsElement instanceof Upload){
            ((Upload) xFormsElement).setMediatype(this);
        }else if(xFormsElement instanceof Output) {
           //TODO: Find better Solution for Handling of Output and Upload-Controls
           //Output-Mediatype gets handle via  AttributeOrValueChild for the moment
           //@see de.betterform.xml.xforms.ui.Output
        }else{
            throw new XFormsException("Parent Element at " + DOMUtil.getCanonicalPath(xFormsElement.getElement()) + " does not support mediatype");
        }
    }

    // mediatype specific methods

    /**
     * Returns the value of this helper element.
     *
     * @return the value of this helper element.
     */
    public String getValue() throws XFormsException {
        try {
            if (hasBindingExpression()) {
                return getNodeValue();
            }
        }catch(XFormsException e) {
            LOGGER.warn("Value for Mediatype " + this+ " does not exist");
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
        if (hasBindingExpression()) {
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


    /* check if mediatype is valid */
    /* see http://www.iana.org/assignments/media-types/ for valid mediatypes */
    public static boolean isMediaTypeValid(String mediatype) {
        if (mediatype == null || mediatype.equals("")) {
            return false;
        }
        if (mediatype.startsWith("application/") ||
                mediatype.startsWith("audio/") ||
                mediatype.startsWith("example/") ||
                mediatype.startsWith("image/") ||
                mediatype.startsWith("message/") ||
                mediatype.startsWith("model/") ||
                mediatype.startsWith("multipart/") ||
                mediatype.startsWith("text/") ||
                mediatype.startsWith("video/")) {
            return true;
        }
        return false;
    }
}

// end of class
