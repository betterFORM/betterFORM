/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function.extensions;

import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.sort.*;
import net.sf.saxon.om.*;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.StringValue;

public class BFSort extends XFormsFunction {

    /**
     *
     */
    private static final long serialVersionUID = -5525518237429365423L;

    @Override
    public SequenceIterator iterate(final XPathContext xpathContext)
        throws XPathException {

        final SequenceIterator nodeset = argument[0].iterate(xpathContext);
        final Expression sortByKey = argument[1];
        return sortnodes(xpathContext, nodeset, sortByKey);
    }

    public Sequence call(final XPathContext context,
                         final Sequence[] arguments) throws XPathException {
        final SequenceIterator nodeset = arguments[0].iterate();
        final Item sortByKey = arguments[0].head();


        final SortKeyEvaluator ske = new SortKeyEvaluator() {
            public AtomicValue evaluateSortKey(int n, XPathContext context) throws XPathException {
                return new StringValue(sortByKey.getStringValue());
            }
        };

        final AtomicComparer comparers[] = {
            AtomicSortComparer.makeSortComparer(null, Type.ITEM, context)
        };
        return SequenceTool.toLazySequence(new SortedIterator(context, nodeset, ske, comparers, true));
    }

    protected SequenceIterator sortnodes(final XPathContext xpathContext,
                                         final SequenceIterator nodeset,
                                         final Expression sortByKey) throws XPathException {


        final SortKeyEvaluator ske = new SortKeyEvaluator() {
            public AtomicValue evaluateSortKey(final int i, final XPathContext context)
                throws XPathException {
                final Item item = sortByKey.evaluateItem(context);
                final AtomicValue value;
                if (item instanceof NodeInfo) {
                    value = ((NodeInfo) item).atomize().itemAt(i);
                } else {
                    value = null;
                }
                return value;
            }
        };

        final SortKeyDefinition skd = new SortKeyDefinition(getContainer());
        skd.setSortKey(sortByKey, true);

        final AtomicComparer[] comparers = { skd.makeComparator(xpathContext) };

        return new SortedIterator(xpathContext, nodeset, ske, comparers, true);
    }
}
