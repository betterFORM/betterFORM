/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.ns.NamespaceResolver;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.exception.XFormsLinkException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.ui.state.HelperElementState;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of <b>8.3.3 The label Element</b>, <b>8.3.4 The help
 * Element</b>, <b>8.3.5 The hint Element</b>, and <b>8.3.6 The alert
 * Element</b> (so called common children).
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Common.java 3496 2008-08-28 09:58:15Z joern $
 */
public class Common extends BindingElement {
    private static final Log LOGGER = LogFactory.getLog(Common.class);

    /**
     * Creates a new common child element handler.
     *
     * @param element the host document element.
     * @param model   the context model.
     */
    public Common(Element element, Model model) {
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

        initializeDefaultAction();
        updateXPathContext();

        if (hasBindingExpression()) {
            initializeElementState();
            return;
        }

        if (isLinked()) {
            // get linked value
            String srcAttribute = getXFormsAttribute(SRC_ATTRIBUTE);
            String value = "";
            Object result;

            try {
                result = this.container.getConnectorFactory().createURIResolver(srcAttribute, this.element).resolve();
                if (result instanceof Document) {
                    NamespaceResolver.init(((Document) result).getDocumentElement());
                    try {
                        value = XPathUtil.evaluateAsString((Document) result, "/");
                    }
                    catch (Exception e) {
                        Map props = new HashMap(1);
                        props.put(XFormsEventNames.RESOURCE_URI_PROPERTY, srcAttribute);
                        throw new XFormsLinkException("object model not supported at " + this, e, this.model.getTarget(), props);
                    }
                } else if (result instanceof String) {
                    value = (String) result;
                } else {
                    throw new XFormsException("unknown response type");
                }
            }
            catch (Exception e) {
                Map props = new HashMap(1);
                props.put(XFormsEventNames.RESOURCE_URI_PROPERTY, srcAttribute);
                throw new XFormsLinkException("uri resolution failed at " + this, e, this.model.getTarget(), props);
            }


            // remove any content and initialize text node
            DOMUtil.removeAllChildren(this.element);
            DOMUtil.setElementValue(this.element, value);
            return;
        }

        // initialize children
        initializeChildren();
    }

    public String getElementValue(){
        return DOMUtil.getElementValue(this.element);
    }
/*
    public void setGeneratedId(String generatedId) throws XFormsException {
        //suppress id generation
    }
*/


    /**
     * Performs element update.
     *
     * @throws XFormsException if any error occurred during update.
     */
    public void refresh() throws XFormsException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("refresh" + this);
        }
        updateXPathContext();
        if (hasBindingExpression()) {
            updateElementState();
            return;
        }

        if (isLinked()) {
            // NOP
            return;
        }

        updateChildren();
    }

    /**
     * Performs element disposal.
     *
     * @throws XFormsException if any error occurred during disposal.
     */
    public void dispose() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " dispose");
        }

        disposeDefaultAction();
        disposeElementState();
        disposeChildren();
        disposeSelf();
    }

    // common specific methods

    /**
     * Checks wether this element is linked to an external resource.
     * <p/>
     * This element is considered linked if it has a <code>src</code>
     * attribute.
     *
     * @return <code>true</code> if this element is linked to an external
     *         resource, otherwise <code>false</code>.
     */
    public boolean isLinked() {
        return getXFormsAttribute(SRC_ATTRIBUTE) != null;
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
        return hasBindingExpression() ? new HelperElementState() : null;
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
