/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;

/**
 * Implementation of the 7.10.2 The current() Function <p/> Returns the context
 * node used to initialize the evaluation of the containing XPath expression.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class Current extends XFormsFunction {
    /**
     * 
     */
    private static final long serialVersionUID = -4093826587796615212L;
    
    /**
     * Get the static properties of this expression (other than its type). The result is
     * bit-signficant. These properties are used for optimizations. In general, if
     * property bit is set, it is true, but if it is unset, the value is unknown.
     */

     public int computeSpecialProperties() {
         return StaticProperty.CONTEXT_DOCUMENT_NODESET |
                 StaticProperty.SINGLE_DOCUMENT_NODESET |
                 StaticProperty.ORDERED_NODESET |
                 StaticProperty.NON_CREATIVE;
     }

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
    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
	XPathContext originalCallerContext = xpathContext;
	for(;originalCallerContext.getCaller() != null; originalCallerContext = originalCallerContext.getCaller());
	return originalCallerContext.getContextItem();
    }

    /**
     * Determine the dependencies
     */

    public int getIntrinsicDependencies() {
	return StaticProperty.DEPENDS_ON_CURRENT_ITEM;
    }
}
