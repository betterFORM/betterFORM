/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.DateTimeValue;
import net.sf.saxon.value.NumericValue;
import net.sf.saxon.value.StringValue;

/**
 * Implementation of 7.9.7 The seconds-to-dateTime() Function <p/> This function
 * returns a string containing a lexical xsd:dateTime that corresponds to the
 * number of seconds passed as the parameter according to the following rules:
 * 
 * The number parameter is rounded to the nearest whole number, and the result
 * is interpreted as the difference between the desired UTC dateTime and
 * 1970-01-01T00:00:00Z. An input parameter value of NaN results in output of
 * the empty string. This function does not support leap seconds.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class SecondsToDateTime extends XFormsFunction {

    private static final long serialVersionUID = -166224567432883455L;

    /**
     * Evaluate in a general context
     */
    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
	final NumericValue secondsAsNumericValue = ((NumericValue) argument[0].evaluateItem(xpathContext)); 
	
	if (secondsAsNumericValue.isNaN())
	{
	    return StringValue.EMPTY_STRING;
	}

	GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
	cal.setTime(new Date(secondsAsNumericValue.longValue() * 1000));

	return new StringValue(new DateTimeValue(cal, true).getStringValue());

    }
}
