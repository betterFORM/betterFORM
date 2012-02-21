/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;


import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.ui.state.AVTElementState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

// end of class

/**
 * Implementation of AVT
 * <p/>
 *
 */
public class AVTElement extends BindingElement {
    private static final Log LOGGER = LogFactory.getLog(AVTElement.class);

    /**
     * Creates a new output element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public AVTElement(Element element, Model model) {
        super(element, model);
    }

    protected UIElementState createElementState() throws XFormsException {
        return new AVTElementState();
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

    private String getAttributeValue(String attrName){
        return this.element.getAttribute(attrName);
    }

}
