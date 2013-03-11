/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.DateTimeValue;
import net.sf.saxon.value.DoubleValue;

import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Implementation of 7.9.6 The seconds-from-dateTime() Function <p/> This
 * function returns a possibly fractional number of seconds, according to the
 * following rules:
 * 
 * If the string parameter represents a legal lexical xsd:dateTime, the return
 * value is equal to the number of seconds difference between the specified
 * dateTime (normalized to UTC) and 1970-01-01T00:00:00Z. If no time zone is
 * specified, UTC is used. Any other input string parameter causes a return
 * value of NaN. This function does not support leap seconds.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class SecondsFromDateTime extends XFormsFunction {

    private static final long serialVersionUID = -166224567432883455L;

    /**
     * Evaluate in a general context
     */
    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
	final CharSequence argAsString = argument[0].evaluateAsString(xpathContext);
     /*
	try {
	    DateTimeValue argAsDateTime = (DateTimeValue) DateTimeValue.makeDateTimeValue(argAsString).asAtomic();

	    return new DoubleValue(argAsDateTime.getCalendar().getTimeInMillis() / 1000d);
	} catch (XPathException e1) {
	    return DoubleValue.NaN;
	}*/

        return DoubleValue.NaN;
    }
}
