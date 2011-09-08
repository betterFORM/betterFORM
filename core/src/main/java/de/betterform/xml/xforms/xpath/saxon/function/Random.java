/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;


import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.DoubleValue;

/**
 * Implementation of 7.7.7 The random() Function
 * 
 * This function generates and returns a uniformly distributed random or
 * pseudorandom number in the range from 0.0 up to but excluding 1.0. This
 * function accepts an optional boolean parameter that is false by default. If
 * true, the random number generator for this function is first seeded with a
 * source of randomness before generating the return value. A typical
 * implementation may seed the random number generator with the current system
 * time in milliseconds when random(true) is invoked, and it may apply a linear
 * congruential formula to generate return values on successive invocations of
 * the function.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class Random extends XFormsFunction {
    private static final long serialVersionUID = 7337391384571199716L;
    
    private static java.util.Random random;

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
	boolean isSeed = argument != null && argument.length > 0 && ((BooleanValue)argument[0].evaluateItem(xpathContext)).getBooleanValue();

	synchronized (Random.class) 
	{
		if (random == null || isSeed)
		{
		    random = new java.util.Random();
		}
		
		return new DoubleValue(random.nextDouble());
    }
    }
}
