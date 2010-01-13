/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.Int64Value;

/**
 * Implementation of the 7.8.4 The count-non-empty() Function 
 * <p/> 
 * Function count-non-empty returns the number of non-empty nodes in argument node-set. A
 * node is considered non-empty if it is convertible into a string with a
 * greater-than zero length.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class CountNonEmpty extends XFormsFunction {
	private static final long serialVersionUID = 789407985529669610L;

	/**
	 * Evaluate in a general context
	 */
	public Item evaluateItem(XPathContext xpathContext) throws XPathException {
		long count = 0;
		SequenceIterator iterator = argument[0].iterate(xpathContext);
		for (Item it = iterator.next(); it != null; it = iterator.next()) {
			if (it.getStringValue().length() > 0) {
				++count;
			}
		}
		return Int64Value.makeIntegerValue(count);
	}
}
