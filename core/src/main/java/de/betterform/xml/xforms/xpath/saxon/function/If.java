/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.AtomicValue;

/**
 * Implementation of the 7.7.2 The if() Function
 * <p/>
 * Function if evaluates the first parameter as boolean, returning the second parameter when true, otherwise the third parameter.
 *
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class If extends XFormsFunction
{
	private static final long serialVersionUID = -3790096351696399703L;


	/**
	 * Evaluate in a general context
	 */
	@Override
	public Item evaluateItem(XPathContext xpathContext) throws XPathException
	{
		if(argument[0].effectiveBooleanValue(xpathContext))
		{
            return argument[1].evaluateItem(xpathContext);
		}
        return argument[2].evaluateItem(xpathContext);
	}

	public Sequence call(final XPathContext context,
											 final Sequence[] arguments) throws XPathException {
		final AtomicValue condition = (AtomicValue)arguments[0].head();
		if(condition != null && condition.effectiveBooleanValue()) {
			return arguments[1].head();
		} else {
			return arguments[2].head();
		}
	}
}
