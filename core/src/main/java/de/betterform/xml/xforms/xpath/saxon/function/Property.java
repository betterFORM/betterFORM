/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.exception.XFormsComputeException;
import de.betterform.xml.xforms.exception.XFormsException;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;

/**
 * 7.9.1 The property() Function
 * <p/>
 * Function property returns the XForms property named by the string parameter.
 * <p/>
 * The following properties are available for reading (but not modification).
 * <p/>
 * <b>version</b> version is defined as the string "1.0" for XForms 1.0
 * <p/>
 * <b>conformance-level</b> conformance-level strings are defined in 12
 * Conformance.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class Property extends XFormsFunction {
	private static final long serialVersionUID = -3790096351696399703L;

	private static final StringValue VERSION = new StringValue("1.1");
	private static final StringValue CONFORMANCE = new StringValue("full");

	/**
     * Pre-evaluate a function at compile time. Functions that do not allow
     * pre-evaluation, or that need access to context information, can override this method.
     * @param visitor an expression visitor
     * @return the result of the early evaluation, or the original expression, or potentially
     * a simplified expression
     */

    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
	return this;
    }

	/**
	 * Evaluate in a general context
	 */
	public Item evaluateItem(XPathContext xpathContext) throws XPathException {
		String name = argument[0].evaluateAsString(xpathContext).toString();

		if (("version").equals(name)) {
			return VERSION;
		}

		if (("conformance-level").equals(name)) {
			return CONFORMANCE;
		}

		XPathFunctionContext functionContext = getFunctionContext(xpathContext);
		XFormsElement xformsElement = functionContext.getXFormsElement();
        if(xformsElement != null) {

            try {
                xformsElement.getContainerObject().dispatch(xformsElement.getId(), XFormsEventNames.BINDING_EXCEPTION);
            } catch (XFormsException e) {
                throw new XPathException(new XFormsComputeException("Requesting invalid property '" + name + "'", xformsElement.getTarget(), this));
            }
        }else {
            throw new XPathException(new XFormsComputeException("Requesting invalid property '" + name + "'", xformsElement.getTarget(), this));
        }
        return new StringValue("");
	}
}
