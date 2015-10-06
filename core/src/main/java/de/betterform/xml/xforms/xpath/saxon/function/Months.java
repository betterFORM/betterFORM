/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.DurationValue;

/**
 * Implementation of 7.9.10 The months() Function <p/> This function returns a
 * whole number of months, according to the following rules:
 * 
 * If the string parameter represents a legal lexical xsd:duration, the return
 * value is equal to the number specified in the months component plus 12 * the
 * number specified in the years component. The sign of the result will match
 * the sign of the duration. Day, hour, minute, and second components, if
 * present, are ignored. Any other input parameter causes a return value of NaN.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class Months extends XFormsFunction {

    private static final long serialVersionUID = -1401245866966186607L;

    /**
     * Evaluate in a general context
     */
	@Override
    public Item evaluateItem(final XPathContext xpathContext) throws XPathException {
		final CharSequence argAsString = argument[0].evaluateAsString(
			xpathContext);
		return months(argAsString.toString());
	}

	public Sequence call(final XPathContext context,
						 final Sequence[] arguments) throws XPathException {
		return months(arguments[0].head().getStringValue());
	}

	private DoubleValue months(final String arg) {
		try {
			final DurationValue argAsDurationValue = (DurationValue) DurationValue.makeDuration(arg).asAtomic();
			return new DoubleValue(argAsDurationValue.signum() * (argAsDurationValue.getYears() * 12 + argAsDurationValue.getMonths()));
		} catch (XPathException e1) {
			return DoubleValue.NaN;
		}
    }
}
