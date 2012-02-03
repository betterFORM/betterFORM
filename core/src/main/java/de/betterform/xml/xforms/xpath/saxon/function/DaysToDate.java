/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.DateValue;
import net.sf.saxon.value.NumericValue;
import net.sf.saxon.value.StringValue;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Implementation of 7.9.5 The days-to-date() Function <p/> This function
 * returns a string containing a lexical xsd:date that corresponds to the number
 * of days passed as the parameter according to the following rules:
 * 
 * The number parameter is rounded to the nearest whole number, and the result
 * is interpreted as the difference between the desired date and 1970-01-01. An
 * input parameter value of NaN results in output of the empty string.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class DaysToDate extends XFormsFunction {

    private static final long serialVersionUID = -166224567432883455L;

    /**
     * Evaluate in a general context
     */
    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
	final NumericValue secondsAsNumericValue = ((NumericValue) argument[0].evaluateItem(xpathContext));

	if (secondsAsNumericValue.isNaN()) {
	    return StringValue.EMPTY_STRING;
	}

	GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
	cal.setTime(new Date(Math.round(secondsAsNumericValue.getDoubleValue()) * 1000 * 60 * 60 * 24));

	return new StringValue(new DateValue(cal, DateValue.NO_TIMEZONE).getStringValue());

    }
}
