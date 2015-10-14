/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.NumericValue;

/**
 * Implementation of 7.6.2 The is-card-number() Function
 * 
 * If the string parameter conforms to the pattern restriction of the
 * card-number datatype, then this function applies the Luhn algorithm described
 * in [Luhn Patent] and returns true if the number satisfies the formula.
 * Otherwise, false is returned. If the parameter is omitted, it defaults to the
 * string-value of the current context node.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class Power extends XFormsFunction
{
    private static final long serialVersionUID = 7337391384571199716L;

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
	@Override
	public Item evaluateItem(XPathContext xpathContext) throws XPathException
	{
	    //XXX what if arguments of power are no double values 
	    final double base = ((NumericValue)argument[0].evaluateItem(xpathContext)).getDoubleValue();
	    final double exponent = ((NumericValue)argument[1].evaluateItem(xpathContext)).getDoubleValue();
	    return pow(base, exponent);
	}

	public Sequence call(final XPathContext context,
						 final Sequence[] arguments) throws XPathException {
		final double base = ((DoubleValue)arguments[0].head()).getDoubleValue();
		final double exponent = ((DoubleValue)arguments[1].head()).getDoubleValue();
		return pow(base, exponent);
	}

	private DoubleValue pow(final double base, final double exponent) {
		return new DoubleValue(Math.pow(base, exponent));
	}
}
