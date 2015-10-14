/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xpath.impl.saxon;

import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.xpath.saxon.function.Instance;
import de.betterform.xml.xpath.XPathReferenceFinder;
import net.sf.saxon.expr.*;
import net.sf.saxon.om.AxisInfo;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.type.Type;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Adrian Baker
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: SaxonReferenceFinderImpl.java 2269 2006-08-18 21:01:25Z unl $
 */
public class SaxonReferenceFinderImpl implements XPathReferenceFinder {

  /**
   * Creates a new Saxon-based reference finder.
   */
  public SaxonReferenceFinderImpl() {
  }

  /**
   * Returns the set of all references to other nodes contained in the given
   * XPath expression.
   * 
   * @param xpath the XPath expression.
   * @param prefixMapping the prefix to name space mapping
   * @return the set of all references to other nodes.
   * @throws XFormsException if a reference detection error occurred.
   */
  public Set<String> getReferences(String xpath, Map prefixMapping, Container container) throws XFormsException {
    try {
      // we need this expression to remember the number
      Expression expression = XPathCache.getInstance().getXPathExpression(xpath, prefixMapping, container.getConfiguration()).getInternalExpression();
      Set<String> references = new HashSet<String>();
      addExpressionReferences(references, null, expression, prefixMapping);

      return references;
    } catch (Exception e) {
      throw new XFormsException("Can't resolve references for: " + xpath + " (" + e.getMessage() + ")", e);
    }
  }

  private void addExpressionReferences(Set<String> references, String context, Expression expression, Map prefixMapping) {
    if (expression instanceof AxisExpression && ((AxisExpression) expression).getAxis() == AxisInfo.PARENT && !(((AxisExpression)expression).getNodeTest() instanceof NodeKindTest && ((NodeKindTest)((AxisExpression)expression).getNodeTest()).getNodeKind() == Type.ELEMENT)) {
      references.add(context != null ? (context + "/..") : "..");
    } else if (expression instanceof AxisExpression) {
      references.add(
          (context != null ? (context + "/") : "") + SaxonXPathExpressionSerializer.serialize(
              expression, prefixMapping));
//    } else if(expression instanceof SimpleStepExpression && ((SimpleStepExpression) expression).getSelectExpression() instanceof ItemChecker && ((ItemChecker)((SimpleStepExpression) expression).getSelectExpression()).getBaseExpression() instanceof ContextItemExpression) {
    } else if(expression instanceof SimpleStepExpression && ((SimpleStepExpression) expression).getSelectExpression() instanceof ItemChecker && ((ItemChecker)((SimpleStepExpression) expression).getSelectExpression()).getBaseExpression() instanceof ContextItemExpression) {
      final SimpleStepExpression simpleStepExpression = (SimpleStepExpression) expression;
      //ignore ContextItem select action
      final Expression rhsExpression = simpleStepExpression.getActionExpression();
      addExpressionReferences(references, context, rhsExpression, prefixMapping);
    } else if (expression instanceof SlashExpression) {
      final SlashExpression slashExpression = (SlashExpression) expression;
      final Expression lhsExpression = slashExpression.getSelectExpression();
      final Expression rhsExpression = slashExpression.getActionExpression();
      if(lhsExpression instanceof SimpleStepExpression && ((SimpleStepExpression) lhsExpression).getSelectExpression() instanceof ItemChecker && ((ItemChecker)((SimpleStepExpression) lhsExpression).getSelectExpression()).getBaseExpression() instanceof ContextItemExpression) {
        //ignore ContextItem select action
        final Expression lhsExpressionAction = ((SimpleStepExpression) lhsExpression).getActionExpression();
        addExpressionReferences(references, context, lhsExpressionAction, prefixMapping);
        final String newContext = SaxonXPathExpressionSerializer.serialize(lhsExpressionAction, prefixMapping);
        addExpressionReferences(references, newContext, rhsExpression, prefixMapping);
      } else {
        addExpressionReferences(references, context, lhsExpression,
            prefixMapping);
        final String newContext = SaxonXPathExpressionSerializer.serialize(lhsExpression, prefixMapping);
        addExpressionReferences(references, newContext, rhsExpression, prefixMapping);
      }

    } else if (expression instanceof FilterExpression) {
      final FilterExpression filterExpression = (FilterExpression) expression;
      final Expression lhsExpression = filterExpression.getSelectExpression();
      final Expression rhsExpression = filterExpression.getActionExpression();

      if(lhsExpression instanceof SimpleStepExpression && ((SimpleStepExpression) lhsExpression).getSelectExpression() instanceof ItemChecker && ((ItemChecker)((SimpleStepExpression) lhsExpression).getSelectExpression()).getBaseExpression() instanceof ContextItemExpression) {
        //ignore ContextItem select action
        final Expression lhsExpressionAction = ((SimpleStepExpression) lhsExpression).getActionExpression();
        addExpressionReferences(references, context, lhsExpressionAction, prefixMapping);
        final String newContext = SaxonXPathExpressionSerializer.serialize(lhsExpressionAction, prefixMapping);
        addExpressionReferences(references, newContext, rhsExpression, prefixMapping);
      } else {
        addExpressionReferences(references, context, lhsExpression, prefixMapping);
        final String newContext = SaxonXPathExpressionSerializer.serialize(lhsExpression, prefixMapping);
        addExpressionReferences(references, newContext, rhsExpression, prefixMapping);
      }
    } else if (expression instanceof FunctionCall) {
      String newContext = SaxonXPathExpressionSerializer.serialize(expression, prefixMapping);

      if (expression instanceof Instance) {
        references.add(newContext);
      }

      for (final Operand o : expression.operands()) {
        addExpressionReferences(references, context, o.getExpression(), prefixMapping);
      }
    } else if (expression instanceof ContextItemExpression) {
      references.add(context != null ? context : ".");
    } else if (expression instanceof Assignation) {
      Assignation a = (Assignation) expression;
      addExpressionReferences(references, context, a.getSequence(), prefixMapping);
      if (null != a.getAction()) {
        addExpressionReferences(references, context, a.getAction(), prefixMapping);
      }
    } else {
      for(final Operand o : expression.operands()) {
        addExpressionReferences(references, context, o.getExpression(), prefixMapping);
      }
    }
  }

}
