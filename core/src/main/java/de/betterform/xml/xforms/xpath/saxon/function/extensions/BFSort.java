/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function.extensions;

import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.expr.sort.AtomicComparer;
import net.sf.saxon.expr.sort.SortKeyDefinition;
import net.sf.saxon.expr.sort.SortKeyEvaluator;
import net.sf.saxon.expr.sort.SortedIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.Value;

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
			
			public AtomicValue evaluateSortKey(int i, XPathContext context)
					throws XPathException {

                SequenceIterator iterator =  sortByKey.evaluateItem(context).getTypedValue();
                Item item = iterator.next();
                Value value = null;
                if (item instanceof NodeInfo) {
					 value = ((NodeInfo) item).atomize();
				}
				return (AtomicValue) value;
			}
		};
		
		SortKeyDefinition skd = new SortKeyDefinition();
		skd.setSortKey(sortByKey, false);
		
		AtomicComparer[] comparers = { skd.makeComparator(xpathContext) };
		 
		return new SortedIterator(xpathContext, nodeset.iterate(xpathContext), ske, comparers, false);
	}	
}
