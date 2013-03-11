/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.DateTimeValue;
import net.sf.saxon.value.StringValue;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Implementation of 7.9.8 The adjust-dateTime-to-timezone() Function <p/> This
 * function adjusts a legal lexical xsd:dateTime received as the string
 * parameter to the local time zone of the implementation, and returns the
 * result. If the string argument contains no time zone, then the result is the
 * string argument with the local time zone as the time zone component. If the
 * implementation does not have access to time zone information, UTC is used.
 * The result is empty string if the string argument is the empty sequence or
 * not a legal lexical xsd:dateTime.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class AdjustDateTimeToTimezone extends XFormsFunction {

    private static final long serialVersionUID = -5735803212070471603L;
    
    private static final int kLOCAL_TIME_OFFSET_IN_MINUTES = new GregorianCalendar().get(Calendar.ZONE_OFFSET)/1000/60;

    /**
     * Evaluate in a general context
     */
    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
    if (argument.length == 0) return StringValue.EMPTY_STRING;
    return StringValue.EMPTY_STRING;
	    /*
        final CharSequence argAsString = argument[0].evaluateAsString(xpathContext);

	try {
	    DateTimeValue argAsDateTime = (DateTimeValue) DateTimeValue.makeDateTimeValue(argAsString).asAtomic();

	    return new StringValue(argAsDateTime.adjustTimezone(kLOCAL_TIME_OFFSET_IN_MINUTES).getStringValue());
	} catch (XPathException e1) {
	    return StringValue.EMPTY_STRING;
	}    */
    }
}
