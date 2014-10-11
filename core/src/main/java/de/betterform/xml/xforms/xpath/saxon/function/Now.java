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

import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Implementation of 7.9.3 The now() Function <p/> The now function returns the
 * current UTC date and time as a string value in the canonical XML Schema
 * xsd:dateTime format. If time zone information is available, it is used to
 * convert the date and time to UTC. If no time zone information is available,
 * then the date and time are assumed to be in UTC.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class Now extends XFormsFunction {
    /**
     * Evaluate in a general context
     */
    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
        GregorianCalendar now = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        return evaluateItem(xpathContext, now);
    }

    final Item evaluateItem(XPathContext xpathContext, GregorianCalendar now) throws XPathException {
        return new StringValue(new DateTimeValue(now, true).getStringValue());
    }
}
