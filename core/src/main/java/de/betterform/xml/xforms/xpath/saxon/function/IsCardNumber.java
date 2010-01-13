/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;

/**
 * Implementation of 7.6.2 The is-card-number() Function
 * 
 * If the string parameter conforms to the pattern restriction of the
 * card-number datatype, then this function applies the Luhn algorithm described
 * in [Luhn Patent] and returns true if the number satisfies the formula.
 * Otherwise, false is returned. If the parameter is omitted, it defaults to the
 * string-value of the current context node.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class IsCardNumber extends XFormsFunction {

	private static final long serialVersionUID = 1752327624237248755L;

	/**
	 * Evaluate in a general context
	 */
	public Item evaluateItem(XPathContext xpathContext) throws XPathException {
		String luhnnumber = argument[0].evaluateAsString(xpathContext)
				.toString();
		try {
			int result = 0;
			boolean alternation = false;
			for (int i = luhnnumber.length() - 1; i >= 0; i--) {
				int n = Integer.parseInt(luhnnumber.substring(i, i + 1));
				if (alternation) {
					n *= 2;
					if (n > 9) {
						n = (n % 10) + 1;
					}
				}
				result += n;
				alternation = !alternation;
			}
			return BooleanValue.get(result % 10 == 0);
		} catch (NumberFormatException e) {
			return BooleanValue.FALSE;
		}
	}
}
