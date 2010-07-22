/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import java.util.Calendar;
import java.util.GregorianCalendar;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.DateValue;
import net.sf.saxon.value.StringValue;

/**
 * Implementation of 7.9.1 The local-date() Function <p/> This function returns
 * a lexical xsd:date obtained as if by the following rules: the result of now()
 * is converted to a local date based on the user agent time zone information.
 * If no time zone information is available, then the date portion of the result
 * of now() is returned.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class LocalDate extends XFormsFunction {

    private static final long serialVersionUID = -9194951362190572554L;

    /**
     * Evaluate in a general context
     */
    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
	GregorianCalendar now = new GregorianCalendar();	
	
	return new StringValue(new DateValue(now, now.get(Calendar.ZONE_OFFSET)/1000/60).getStringValue());
    }
}
