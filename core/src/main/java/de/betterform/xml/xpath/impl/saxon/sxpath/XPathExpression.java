/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xpath.impl.saxon.sxpath;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContextMajor;
import net.sf.saxon.instruct.SlotManager;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceExtent;
import net.sf.saxon.value.Value;

import javax.xml.transform.Source;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class is a representation of an XPath Expression for use with the {@link XPathEvaluator} class.
 * It is modelled on the XPath API defined in JAXP 1.3, but is cut down to remove any dependencies on JAXP 1.3,
 * making it suitable for use on vanilla JDK 1.4 installations.
 *
 * @author Michael H. Kay
 */


public class XPathExpression {

    private XPathEvaluator evaluator;
    private Expression expression;
    private SlotManager stackFrameMap;
    private int numberOfExternalVariables;

    /**
     * The constructor is protected, to ensure that instances can only be
     * created using the createExpression() method of XPathEvaluator
     * @param evaluator the creating XPathEvaluator
     * @param exp the internal representation of the compiled expression
     */

    protected XPathExpression(XPathEvaluator evaluator, Expression exp) {
        expression = exp;
        this.evaluator = evaluator;
    }

    /**
     * Define the number of slots needed for local variables within the expression
     * @param map the stack frame map identifying all the variables used by the expression, both
     * those declared internally, and references to variables declared externally
     * @param numberOfExternalVariables the number of slots in the stack frame allocated to
     * externally-declared variables. These must be assigned a value before execution starts
     */

    protected void setStackFrameMap(SlotManager map, int numberOfExternalVariables) {
        stackFrameMap = map;
        this.numberOfExternalVariables = numberOfExternalVariables;
    }

    /**
     * Create a dynamic context suitable for evaluating this expression
     * @param contextItem the initial context item, which may be null if no
     * context item is required, or if it is to be supplied later
     * @return an XPathDynamicContext object representing a suitable dynamic context. This will
     * be initialized with a stack frame suitable for holding the variables used by the
     * expression.
     */

    public XPathDynamicContext createDynamicContext(Item contextItem) {
        XPathContextMajor context = new XPathContextMajor(contextItem, evaluator.getExecutable());
        context.openStackFrame(stackFrameMap);
        return new XPathDynamicContext(context);
    }

    /**
     * Execute the expression, returning the result as a {@link SequenceIterator}, whose members will be instances
     * of the class {@link net.sf.saxon.om.Item}
     *
     * <p>Note that if evaluation of the expression fails with a dynamic error, this will generally
     * be reported in the form of an exception thrown by the next() method of the {@link SequenceIterator}
     * @param context the XPath dynamic context
     * @return an iterator over the result of the expression
     */

    public SequenceIterator iterate(XPathDynamicContext context) throws XPathException {
        context.checkExternalVariables(stackFrameMap, numberOfExternalVariables);
        return expression.iterate(context.getXPathContextObject());
    }

    /**
     * Execute the expression, returning the result as a List, whose members will be instances
     * of the class {@link net.sf.saxon.om.Item}
     * @param context the XPath dynamic context
     * @return a list of items representing the result of the expression
     */

    public List evaluate(XPathDynamicContext context) throws XPathException {
        SequenceIterator iter = expression.iterate(context.getXPathContextObject());
        List list = new ArrayList(20);
        while (true) {
            Item item = iter.next();
            if (item == null) {
                break;
            }
            list.add(item);
        }
        return list;
    }

    /**
     * Execute the expression, returning the result as a single {@link net.sf.saxon.om.Item}
     * If the result of the expression is a sequence containing more than one item, items after
     * the first are discarded. If the result is an empty sequence, the method returns null.
     * @param context the XPath dynamic context
     * @return the first item in the result of the expression, or null
     */

    public Item evaluateSingle(XPathDynamicContext context) throws XPathException {
        return expression.iterate(context.getXPathContextObject()).next();
    }


    /**
     * Execute a prepared XPath expression, returning the results as a List in which items have been converted
     * to the appropriate Java object.
     *
     * @param source the document or other node against which the XPath expression
     *      will be evaluated. This may be a Saxon NodeInfo object, representing a node in an
     *      existing tree, or it may be any kind of JAXP Source object such as a StreamSource
     *      SAXSource or DOMSource. For the way in which the source document is built, see
     *      {@link net.sf.saxon.Configuration#buildDocument}
     * @return The results of the expression, as a List. The List represents the sequence
     *         of items returned by the expression. Each item in the list will either be an instance
     *         of net.sf.saxon.om.NodeInfo, representing a node, or a Java object representing an atomic value.
     */

    public List evaluate(Source source) throws XPathException {
        NodeInfo origin;
        if (source instanceof NodeInfo) {
            origin = (NodeInfo)source;
        } else {
            origin = evaluator.getConfiguration().buildDocument(source);
        }
        XPathDynamicContext dynamicContext = createDynamicContext(origin);
        SequenceIterator iter = iterate(dynamicContext);
        SequenceExtent extent = new SequenceExtent(iter);
        List result = (List)extent.convertToJava((Item) dynamicContext.getXPathContextObject());
        if (result == null) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    /**
     * Execute a prepared XPath expression, returning the first item in the result.
     * This is useful where it is known that the expression will only return
     * a singleton value (for example, a single node, or a boolean).
     * @param source the document or other node against which the XPath expression
     *       will be evaluated. This may be a Saxon NodeInfo object, representing a node in an
     *       existing tree, or it may be any kind of JAXP Source object such as a StreamSource
     *       SAXSource or DOMSource. For the way in which the source document is built, see
     *      {@link net.sf.saxon.Configuration#buildDocument}
     *
     * @return The first item in the sequence returned by the expression. If the expression
     *         returns an empty sequence, this method returns null. Otherwise, it returns the first
     *         item in the result sequence, represented as a Java object using the same mapping as for
     *         the evaluate() method
     */

    public Object evaluateSingle(Source source) throws XPathException {
        NodeInfo origin;
        if (source instanceof NodeInfo) {
            origin = (NodeInfo)source;
        } else {
            origin = evaluator.getConfiguration().buildDocument(source);
        }
        XPathDynamicContext context = createDynamicContext(origin);
        SequenceIterator iterator = iterate(context);
        Item item = iterator.next();
        if (item == null) {
            return null;
        } else {
            return Value.convertToJava(item);
        }
    }

    /**
     * Get a raw iterator over the results of the expression. This returns results without
     * any conversion of the returned items to "native" Java classes. This method is intended
     * for use by applications that need to process the results of the expression using
     * internal Saxon interfaces.
     * @param source the document or other node against which the XPath expression
     *      will be evaluated. This may be a Saxon NodeInfo object, representing a node in an
     *      existing tree, or it may be any kind of JAXP Source object such as a StreamSource
     *      SAXSource or DOMSource. For the way in which the source document is built, see
     *      {@link net.sf.saxon.Configuration#buildDocument}
     * @return an iterator over the results of the expression
     * @deprecated since 8.9 - use {@link #iterate}
     */

    public SequenceIterator rawIterator(Source source) throws XPathException {
        NodeInfo origin;
        if (source instanceof NodeInfo) {
            origin = (NodeInfo)source;
        } else {
            origin = evaluator.getConfiguration().buildDocument(source);
        }
        XPathDynamicContext context = createDynamicContext(origin);
        return iterate(context);
    }

    /**
     * Low-level method to get the internal Saxon expression object. This exposes a wide range of
     * internal methods that may be needed by specialized applications, and allows greater control
     * over the dynamic context for evaluating the expression.
     *
     * @return the underlying Saxon expression object.
     */

    public Expression getInternalExpression() {
        return expression;
    }

}

//
// The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the
// License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the License for the specific language governing rights and limitations under the License.
//
// The Original Code is: all this file.

// The Initial Developer of the Original Code is
// Michael H. Kay.
//
// Contributor(s):
//
