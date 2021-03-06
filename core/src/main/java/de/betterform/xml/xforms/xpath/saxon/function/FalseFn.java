/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;

/*
 * @author Tobi Krebs <tobias.krebs@betterform.de>
*/public class FalseFn extends XFormsFunction {


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
    public Item evaluateItem(final XPathContext xpathContext) throws XPathException {
        return BooleanValue.FALSE;
    }


    public Sequence call(final XPathContext context,
                         final Sequence[] arguments) throws XPathException {
        return evaluateItem(context);
    }
}
