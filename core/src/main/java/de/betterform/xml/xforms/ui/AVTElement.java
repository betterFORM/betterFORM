/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.ui;




import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;

// end of class

/**
 * Implementation of <b>8.1.5 The output Element</b>.
 * <p/>
 * Note: In case this control is not bound but has a <code>value</code>
 * attribute, re-evaluation of this attribute occurs during
 * <code>xforms-refresh</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @author Nick Van den Bleeken
 * @version $Id: Output.java 3253 2008-07-08 09:26:40Z lasse $
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
        return null;
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
