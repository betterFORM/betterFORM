/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
/**
 * Implementation of the 7.7.1 The boolean-from-string() Function
 * <p/>
 * Function boolean-from-string returns true if the required parameter
 * string is "true" or "1", or false if parameter string is "false", or "0".
 * This is useful when referencing a Schema xsd:boolean datatype in an XPath
 * expression. If the parameter string matches none of the above strings,
 * according to a case-insensitive comparison, the return value is "false". 
 *
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class BooleanFromString extends XFormsFunction
{
	private static final long serialVersionUID = -8152654592543207734L;

	/**
	 * Evaluate in a general context
	 */
	public Item evaluateItem(XPathContext xpathContext) throws XPathException
	{
		String value = argument[0].evaluateAsString(xpathContext).toString();
        if("1".equals(value) || "true".equalsIgnoreCase(value))
        {
            return BooleanValue.TRUE;
        }
        return BooleanValue.FALSE;
	}
}
