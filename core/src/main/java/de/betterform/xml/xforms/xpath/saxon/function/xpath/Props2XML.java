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
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.tree.iter.ListIterator;;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collections;

/**
 * betterFORM Project
 * User: Tobi Krebs
 * Date: 01.11.11
 * Time: 20:50
 */
public class Props2XML extends XFormsFunction {
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
	public SequenceIterator iterate(XPathContext xpathContext) throws XPathException
	{
		XPathFunctionContext functionContext = getFunctionContext(xpathContext);

		if (functionContext != null)
		{
			final de.betterform.xml.xforms.model.Instance instance;
			if (argument.length == 0)
			{
				return new ListIterator(Collections.EMPTY_LIST);
			}
			else
			{
                String props = argument[0].evaluateAsString(xpathContext).toString().trim();

                String[] propArray = props.substring(1, props.length()-1).trim().split(",");

                Document document = DOMUtil.newDocument(false, false);
                Element xfElement = document.createElement("xfElement");
                document.appendChild(xfElement);

                for(int i = 0; i < propArray.length; i++) {
                    if(propArray[i].contains("\":\"")) {
                        String tmp = propArray[i].trim();
                        String aName = tmp.substring(1,tmp.indexOf("\":\"")).trim();
                        String aValue = tmp.substring(tmp.indexOf("\":\"")+3).trim();
                        aValue = aValue.substring(0, aValue.length()-1);
                        xfElement.setAttribute(aName, aValue);
                }
                }


                return new ListIterator(XPathUtil.getRootContext(document, ""));
			}
		}
		return new ListIterator(Collections.EMPTY_LIST);
	}
}
