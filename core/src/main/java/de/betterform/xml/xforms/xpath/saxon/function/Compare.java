/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.Int64Value;

/**
 * Implementation of 7.7.8 The compare() Function
 * 
 * This function returns -1, 0, or 1, depending on whether the value of the
 * first argument is respectively less than, equal to, or greater than the value
 * of second argument based on lexicographic comparison using Unicode code point
 * values [Unicode Collation Algorithm].
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class Compare extends XFormsFunction {

    private static final long serialVersionUID = -8331394395194343808L;

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
	final String arg1 = argument[0].evaluateAsString(xpathContext).toString();
	final String arg2 = argument[1].evaluateAsString(xpathContext).toString();

	int result = arg1.compareTo(arg2);
	
	if (result < 0)
	{
	    return Int64Value.MINUS_ONE;
	}
	else if (result == 0)
	{
	    return Int64Value.ZERO;
	}
	return Int64Value.PLUS_ONE;
    }
}
