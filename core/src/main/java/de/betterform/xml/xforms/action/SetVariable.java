/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.exception.XFormsComputeException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.Map;

public class SetVariable extends AbstractBoundAction {
    private static Log LOGGER = LogFactory.getLog(SetVariable.class);

    /**
     * Creates a bound action implementation.
     *
     * @param element the element.
     * @param model   the context model.
     */
    public SetVariable(Element element, Model model) {
        super(element, model);
    }

    @Override
    public void init() throws XFormsException {
        super.init();
        LOGGER.info("USE WITH CAUTION - THIS ACTION IS STILL EXPERIMENTAL");
    }

    @Override
    public boolean hasBindingExpression() {
        return true;
    }

    public void perform() throws XFormsException {
        final String name = getXFormsAttribute("name");
        String select = getXFormsAttribute("select");
        updateXPathContext();

        if (getLogger().isDebugEnabled()){
            getLogger().debug("declaring variable at: " + DOMUtil.getCanonicalPath(this.element));
        }

        try {
            select = XPathCache.getInstance().evaluateAsString(nodeset, position, select, getPrefixMapping(), xpathFunctionContext);
        } catch (Exception e) {
            throw new XFormsComputeException("invalid select expression at: " + DOMUtil.getCanonicalPath(this.getElement()) + ":" + select, e, this.target, select);
        }

        this.container.getProcessor().getContext().put(name, select);

        if(getLogger().isDebugEnabled()){
            getLogger().debug("storing variable '" + name + "' with value '" + select + "'");
        }

        if (select != null) {
            Map map = new HashMap(1);
            map.put(XFormsConstants.VARIABLE_NAME, name);
            map.put(XFormsConstants.VARIABLE_VALUE, select);
            this.container.dispatch(this.target,"betterform-variable-changed",map);
        }

    }

    protected Log getLogger() {
        return LOGGER;
    }
}
