/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SequenceTool;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.ListIterator;

import java.util.Collections;

public class InstanceOfModel extends XFormsFunction {
    private static final long serialVersionUID = -5302742873313974258L;

    /**
     * Pre-evaluate a function at compile time. Functions that do not allow
     * pre-evaluation, or that need access to context information, can override this method.
     *
     * @param visitor an expression visitor
     * @return the result of the early evaluation, or the original expression, or potentially
     *         a simplified expression
     */
    @Override
    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
        return this;
    }

    /**
     * Evaluate in a general context
     */
    @Override
    public SequenceIterator iterate(final XPathContext xpathContext) throws XPathException {
        if (argument.length != 2) {
            throw new XPathException(
                "There must be 2 arguments (modelId, instanceId) for this function");
        }

        final Expression modelIDExpression = argument[0];
        final String modelId = modelIDExpression.evaluateAsString(
            xpathContext).toString();

        final Expression instanceIdExpression = argument[1];
        final String instanceId = instanceIdExpression.evaluateAsString(
            xpathContext).toString();

        return instanceOfModel(xpathContext, modelId, instanceId);
    }

    public Sequence call(final XPathContext context,
                         final Sequence[] arguments) throws XPathException {
        final String modelId = arguments[0].head().getStringValue();
        final String instanceId = arguments[0].head().getStringValue();

        return SequenceTool.toLazySequence(instanceOfModel(context, modelId, instanceId));
    }

    private SequenceIterator instanceOfModel(final XPathContext context, final String modelId, final String instanceId) throws XPathException {
        final XPathFunctionContext functionContext = getFunctionContext(context);

        if (functionContext != null) {
            final XFormsElement element = functionContext.getXFormsElement();
            final Model model;
            try {
                model = element.getContainerObject().getModel(modelId);
            } catch (final XFormsException e) {
                throw new XPathException("Model: " + modelId + " not found");
            }



            final de.betterform.xml.xforms.model.Instance instance = model.getInstance(instanceId);
            if (instance != null) {
                return new ListIterator(instance.getInstanceNodeset());
            }
        }
        return new ListIterator(Collections.EMPTY_LIST);
    }
}
