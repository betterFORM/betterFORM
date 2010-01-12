/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;

/**
 * Implementation of the 7.11.1 The choose() Function <p/> This function
 * provides a conditional test that chooses an object to return based on the
 * boolean parameter. If the boolean parameter is true, then the first object is
 * returned, otherwise the second object is returned. Each of the object
 * parameters can be of any XPath datatype as described in Section 7.1 XPath
 * Datatypes, and this function does no type conversion of the parameter it
 * choose to return.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class Choose extends XFormsFunction {

    private static final long serialVersionUID = 5219021128840577523L;

    /**
     * Iterate over the contents of the collection
     * @param context the dynamic context
     * @return an iterator, whose items will always be nodes (typically but not necessarily document nodes)
     * @throws XPathException
     */

    public SequenceIterator iterate(final XPathContext xpathContext) throws XPathException {
	if (argument[0].effectiveBooleanValue(xpathContext)) {
	    return argument[1].iterate(xpathContext);
	}
	return argument[2].iterate(xpathContext);
    }
}
