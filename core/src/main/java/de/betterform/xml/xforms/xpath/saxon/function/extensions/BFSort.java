/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function.extensions;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.sort.AtomicComparer;
import net.sf.saxon.sort.SortKeyDefinition;
import net.sf.saxon.sort.SortKeyEvaluator;
import net.sf.saxon.sort.SortedIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.Value;
import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;

public class BFSort extends XFormsFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5525518237429365423L;

	@Override
	public SequenceIterator iterate(XPathContext xpathContext)
			throws XPathException {

		Expression nodeset = argument[0];
		Expression sortByKey = argument[1];
		return sortnodes(xpathContext, null, nodeset, sortByKey);
	}

	protected SequenceIterator sortnodes(XPathContext xpathContext, final XPathContext keyXPathContext,
			Expression nodeset,
			final Expression sortByKey) throws XPathException {
	
		final SortKeyEvaluator ske = new SortKeyEvaluator() {
			
			public Item evaluateSortKey(int i, XPathContext context)
					throws XPathException {

				Item item = sortByKey.evaluateItem(context);
				if (item instanceof NodeInfo) {
					Value value = ((NodeInfo) item).atomize();
					item =value.itemAt(0);
				}
				return item;
			}
		};
		
		SortKeyDefinition skd = new SortKeyDefinition();
		skd.setSortKey(sortByKey);
		
		AtomicComparer[] comparers = { skd.makeComparator(xpathContext) };
		 
		return new SortedIterator(xpathContext, nodeset.iterate(xpathContext), ske, comparers);
	}	
}
