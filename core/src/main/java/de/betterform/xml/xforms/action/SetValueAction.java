/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.exception.XFormsComputeException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Implements the action as defined in <code>10.1.9 The setvalue
 * Element</code>.
 *
 * @author Ulrich Nicolas Liss&eacute; Joern Turner
 * @version $Id: SetValueAction.java 3479 2008-08-19 10:44:53Z joern $
 */
public class SetValueAction extends AbstractBoundAction {
    private static Log LOGGER = LogFactory.getLog(SetValueAction.class);
    private String nodeValue;
    private String valueAttribute;

    /**
     * Creates a setvalue action implementation.
     *
     * @param element the element.
     * @param model   the context model.
     */
    public SetValueAction(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        super.init();

        this.valueAttribute = getXFormsAttribute(VALUE_ATTRIBUTE);
        if (this.valueAttribute == null) {
            Node child = this.element.getFirstChild();

            if ((child != null) && (child.getNodeType() == Node.TEXT_NODE)) {
                this.nodeValue = child.getNodeValue();
            } else {
                this.nodeValue = "";
            }
        }
    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>setvalue</code> action.
     *
     * @throws XFormsException if an error occurred during <code>setvalue</code>
     *                         processing.
     */
    public void perform() throws XFormsException {

        final String value;
        updateXPathContext();
        if (this.valueAttribute != null) {

            try {
//                value = XPathCache.getInstance().evaluateAsString(nodeset, position, valueAttribute, getPrefixMapping(), xpathFunctionContext);
                value = XPathCache.getInstance().evaluateAsString(nodeset, position, valueAttribute, getPrefixMapping(), xpathFunctionContext);
            } catch (Exception e) {
                throw new XFormsComputeException("invalid value expression at: " + DOMUtil.getCanonicalPath(this.getElement()), e, this.target, this.valueAttribute);
            }

        } else {
            // reset node value
            value = this.nodeValue;

        }

        boolean success = setNodeValue(value != null ? value : "");

        if (!success) {
            return;
        }

        // update behaviour
        doRecalculate(true);
        doRevalidate(true);
        doRefresh(true);
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
