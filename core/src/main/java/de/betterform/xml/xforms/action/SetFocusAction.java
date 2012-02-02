/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.ui.AbstractUIElement;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implements the action as defined in <code>10.1.7 The setfocus
 * Element</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: SetFocusAction.java 3457 2008-08-13 15:03:54Z joern $
 */
public class SetFocusAction extends AbstractAction {
    private static Log LOGGER = LogFactory.getLog(SetFocusAction.class);
    private String referencedControl = null;

    /**
     * Creates a setfocus action implementation.
     *
     * @param element the element.
     * @param model the context model.
     */
    public SetFocusAction(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     */
    public void init() throws XFormsException {
        super.init();
        // check if setfocus elem has 
        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (NamespaceConstants.XFORMS_NS.equals(node.getNamespaceURI()) && node.getLocalName().equals("control") && node.getNodeType() == Node.ELEMENT_NODE) {
                Element elementImpl = (Element) node;
                XFormsElement xfElem = (XFormsElement) this.element.getUserData("");
                if(elementImpl.hasAttributeNS(null,"value")){
                    String xpath = elementImpl.getAttributeNS(null, "value");
                    this.referencedControl = XPathCache.getInstance().evaluateAsString(xfElem.getModel().getDefaultInstance().getRootContext(), "string(" + xpath + ")");
                }else {
                    this.referencedControl = elementImpl.getTextContent();
                }
            }
        }

        if (this.referencedControl == null) {
            this.referencedControl = getXFormsAttribute(CONTROL_ATTRIBUTE);
        }
        if(this.referencedControl == null) {
            throw new XFormsBindingException("missing control attribute at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, null);
        }
    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>setfocus</code> action.
     *
     * @throws XFormsException if an error occurred during <code>setfocus</code>
     * processing.
     */
    public void perform() throws XFormsException {
        // check control idref
        Object controlObject = this.container.lookup(this.referencedControl);
        if (controlObject == null || !(controlObject instanceof AbstractUIElement)) {
            throw new XFormsBindingException("invalid control id at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, this.referencedControl);
        }

        // dispatch xforms-focus to form control
        this.container.dispatch(((AbstractUIElement) controlObject).getTarget(), XFormsEventNames.FOCUS, null);
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
