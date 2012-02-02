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
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.ListIterator;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
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

    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
	return this;
    }

     /**
     * Evaluate in a general context
     */
	public SequenceIterator iterate(XPathContext xpathContext) throws XPathException {
        if (argument.length != 2) {
            throw new XPathException("There must be 2 arguments (attributeName, attributeValue) for this function");
        }

        XPathFunctionContext functionContext = getFunctionContext(xpathContext);

        if (functionContext != null) {
            final Expression attributeNameExpression = argument[0];
            final String attributeName = attributeNameExpression.evaluateAsString(xpathContext).toString();

            final Expression attributeValueExpression = argument[1];
            final String attributeValue = attributeValueExpression.evaluateAsString(xpathContext).toString();

            Document document = DOMUtil.newDocument(false, false);
            Element attributeNode = document.createElement("attributeNode");
            document.appendChild(attributeNode);
            attributeNode.setAttribute(attributeName, attributeValue);

            return new ListIterator(XPathUtil.getRootContext(document, ""));
        }
        return new ListIterator(Collections.EMPTY_LIST);
    }
}
