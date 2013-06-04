/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import de.betterform.xml.xforms.model.Model;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.ListIterator;

import java.util.Collections;

public class Instance extends XFormsFunction
{
    private static final long serialVersionUID = -5302742873313974258L;

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
			Model model = functionContext.getXFormsElement().getModel();
			
			final de.betterform.xml.xforms.model.Instance instance;
			if (argument.length == 0)
			{
				instance = model.getDefaultInstance();
			}
			else
			{
				final Expression instanceIDExpression = argument[0];
				final String instanceID = instanceIDExpression.evaluateAsString(xpathContext).toString();
				
				instance = model.getInstance(instanceID);
			}
			
			if (instance != null)
			{
				return new ListIterator(instance.getInstanceNodeset());
			}
		}
		return new ListIterator(Collections.EMPTY_LIST);
	}
}
