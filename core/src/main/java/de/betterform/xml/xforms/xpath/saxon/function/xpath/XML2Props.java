/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import net.sf.saxon.dom.DOMNodeWrapper;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Created by IntelliJ IDEA.
 * User: dev
 * Date: 11/2/11
 * Time: 4:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class XML2Props extends XFormsFunction {
    /**
     * Pre-evaluate a function at compile time. Functions that do not allow
     * pre-evaluation, or that need access to context information, can override this method.
     * @param visitor an expression visitor
     * @return the result of the early evaluation, or the original expression, or potentially
     * a simplified expression
     */
    @Override
    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
	    return this;
    }

	/**
	 * Evaluate in a general context
	 */
    @Override
	public Item evaluateItem(final XPathContext xpathContext) throws XPathException {
        final Item item = argument[0].evaluateItem(xpathContext);
        return xmlToProps(xpathContext, item);
    }

    public Sequence call(final XPathContext context,
                         final Sequence[] arguments) throws XPathException {
        return xmlToProps(context, arguments[0].head());
    }

    private StringValue xmlToProps(final XPathContext context, final Item item) {
		final XPathFunctionContext functionContext = getFunctionContext(context);
		if (functionContext != null) {
                if (item != null) {
                final Node node = (Node) ((DOMNodeWrapper) item).getUnderlyingNode();

                final NamedNodeMap namedNodeMap = node.getAttributes();
                final StringBuilder props = new StringBuilder("{");

                for (int i = 0;i <namedNodeMap.getLength(); i++) {
                    final Attr attributeNode = (Attr) namedNodeMap.item(i);
                    props.append("\"" + attributeNode.getName() + "\"");
                    props.append(":");
                    props.append("\"" + attributeNode.getValue() + "\"");
                    props.append(',');
                }

                props.deleteCharAt(props.length()-1);
                props.append("}");

                return new StringValue(props.toString());
			}
		}
		return new StringValue("{}");
	}
}

