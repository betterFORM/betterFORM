/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SequenceTool;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.ListIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collections;

/**
 * betterFORM Project
 * User: Tobi Krebs
 * Date: 11.11.11
 * Time: 16:35
 */
public class CreateAttributeNode extends XFormsFunction {

    /**
     * Pre-evaluate a function at compile time. Functions that do not allow
     * pre-evaluation, or that need access to context information, can override this method.
     * @param visitor an expression visitor
     * @return the result of the early evaluation, or the original expression, or potentially
     * a simplified expression
     */
    @Override
    public Expression preEvaluate(final ExpressionVisitor visitor) throws XPathException {
	    return this;
    }

    /**
     * Evaluate in a general context
     */
    @Override
    public SequenceIterator iterate(final XPathContext xpathContext) throws XPathException {
        if (argument.length != 2) {
            throw new XPathException("There must be 2 arguments (attributeName, attributeValue) for this function");
        }

        final Expression attributeNameExpression = argument[0];
        final String attributeName = attributeNameExpression.evaluateAsString(xpathContext).toString();

        final Expression attributeValueExpression = argument[1];
        final String attributeValue = attributeValueExpression.evaluateAsString(xpathContext).toString();

        return createAttribute(xpathContext, attributeName, attributeValue);
    }

    public Sequence call(final XPathContext context,
                         final Sequence[] arguments) throws XPathException {
        final String attributeName = arguments[0].head().getStringValue();
        final String attributeValue = arguments[1].head().getStringValue();

        return SequenceTool.toLazySequence(createAttribute(context, attributeName, attributeValue));
    }

    private SequenceIterator createAttribute(final XPathContext context, final String name, final String value) {
        final XPathFunctionContext functionContext = getFunctionContext(context);
        if (functionContext != null) {
            final Document document = DOMUtil.newDocument(false, false);
            final Element attributeNode = document.createElement("attributeNode");
            document.appendChild(attributeNode);
            attributeNode.setAttribute(name, value);

            return new ListIterator(XPathUtil.getRootContext(document, ""));
        }
        return new ListIterator(Collections.EMPTY_LIST);
    }
}
