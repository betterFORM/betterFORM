/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.CalendarValue;
import net.sf.saxon.value.DateTimeValue;
import net.sf.saxon.value.DateValue;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.Int64Value;

/**
 * Implementation of 7.9.2 The local-dateTime() Function <p/> This function
 * returns a lexical xsd:dateTime obtained as if by the following rules: the
 * result of now() is converted to a local dateTime based on the user agent time
 * zone information. If no time zone information is available, then the result
 * of now() is returned.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class DaysFromDate extends XFormsFunction {

    private static final long serialVersionUID = 7762214764963406284L;

    /**
     * Evaluate in a general context
     */
    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
	final CharSequence argAsString = argument[0].evaluateAsString(xpathContext);
	
	CalendarValue argAsValue = null;
        try {
            argAsValue = (CalendarValue) DateTimeValue.makeDateTimeValue(argAsString).asAtomic();
        } catch (XPathException e1) {
            try {
        	argAsValue = new DateValue(argAsString);
            } catch (XPathException e2) {
        	return DoubleValue.NaN;
            }
        }
        
        double days = argAsValue.getCalendar().getTimeInMillis() / (1000d * 60 * 60 * 24);
        return new Int64Value(days > 0 ? (int)Math.floor(days) : (int)Math.ceil(days));

    }
}
