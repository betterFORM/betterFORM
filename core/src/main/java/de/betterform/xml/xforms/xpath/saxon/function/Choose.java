/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SequenceTool;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.AtomicValue;

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
     * @param xpathContext the dynamic context
     * @return an iterator, whose items will always be nodes (typically but not necessarily document nodes)
     * @throws XPathException
     */
    @Override
    public SequenceIterator iterate(final XPathContext xpathContext) throws XPathException {
        if (argument[0].effectiveBooleanValue(xpathContext)) {
            return argument[1].iterate(xpathContext);
        }
        return argument[2].iterate(xpathContext);
    }

    public Sequence call(final XPathContext context,
                         final Sequence[] arguments) throws XPathException {
        final SequenceIterator result;
        if(((AtomicValue)arguments[0].head()).effectiveBooleanValue()) {
            result = arguments[1].iterate();
        } else {
            result = arguments[2].iterate();
        }

        return SequenceTool.toLazySequence(result);
    }
}
