/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ConversionRules;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.*;

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
    @Override
    public Item evaluateItem(final XPathContext xpathContext) throws XPathException {
        final CharSequence date = argument[0].evaluateAsString(
            xpathContext);
        return daysFromDate(date.toString());
    }

    public Sequence call(final XPathContext context,
                         final Sequence[] arguments) throws XPathException {
        final String date = arguments[0].head().getStringValue();
        return daysFromDate(date);
    }

    private NumericValue daysFromDate(final String date) {
        CalendarValue argAsValue;
        try {
            argAsValue = (CalendarValue) DateTimeValue.makeDateTimeValue(date, new ConversionRules()).asAtomic();
        } catch (final XPathException e1) {
            try {
        	    argAsValue = new DateValue(date);
            } catch (XPathException e2) {
        	    return DoubleValue.NaN;
            }
        }
        
        final double days = argAsValue.getCalendar().getTimeInMillis() / (1000d * 60 * 60 * 24);
        return new Int64Value(days > 0 ? (int)Math.floor(days) : (int)Math.ceil(days));

    }
}
