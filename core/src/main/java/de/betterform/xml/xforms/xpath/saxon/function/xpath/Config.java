/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;



/**
 * @author <a href="mailto:tobias.krebs@betterform.de">tobi</a>
 * @version $Id: Config 21.10.2010 tobi $
 */
public class Config extends XFormsFunction {
     /**
     * Pre-evaluate a function at compile time. Functions that do not allow
     * pre-evaluation, or that need access to context information, can override this method.
     *
     * @param visitor an expression visitor
     * @return the result of the early evaluation, or the original expression, or potentially
     *         a simplified expression
     */

    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
        return this;
    }
    
    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
	    if (argument.length != 1) {
            throw new XPathException("There must be 1 argument (key) for this function");
        }

        final Expression keyExpression = argument[0];
        final String key = keyExpression.evaluateAsString(xpathContext).toString();

        String value = "";
        try {
            value = de.betterform.xml.config.Config.getInstance().getProperty(key);
        } catch (XFormsConfigException e) {
        }
        return new StringValue(value);
    }
}
