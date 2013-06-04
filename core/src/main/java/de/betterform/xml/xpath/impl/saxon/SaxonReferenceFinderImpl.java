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

import java.util.HashSet;
import java.util.Iterator;
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
  public Set getReferences(String xpath, Map prefixMapping, Container container) throws XFormsException {
    try {
      // we need this expression to remember the number
      Expression expression = XPathCache.getInstance().getXPathExpression(xpath, prefixMapping, container.getConfiguration()).getInternalExpression();
      HashSet references = new HashSet();
      addExpressionReferences(references, null, expression, prefixMapping);

      return references;
    } catch (Exception e) {
      throw new XFormsException("Can't resolve references for: " + xpath + " (" + e.getMessage() + ")", e);
    }
  }

  private void addExpressionReferences(HashSet references, String context, Expression expression, Map prefixMapping) {
    if (expression instanceof AxisExpression) {
      references.add((context != null ? (context + "/") : "") + SaxonXPathExpressionSerializer.serialize(expression, prefixMapping));
    } else if (expression instanceof SlashExpression) {
      final SlashExpression slashExpression = (SlashExpression) expression;
      final Expression lhsExpression = slashExpression.getControllingExpression();
      final Expression rhsExpression = slashExpression.getControlledExpression();

      addExpressionReferences(references, context, lhsExpression, prefixMapping);
      addExpressionReferences(references, SaxonXPathExpressionSerializer.serialize(lhsExpression, prefixMapping), rhsExpression, prefixMapping);

    } else if (expression instanceof FilterExpression) {
      final FilterExpression filterExpression = (FilterExpression) expression;

      String newContext = SaxonXPathExpressionSerializer.serialize(filterExpression.getControllingExpression(), prefixMapping);
      addExpressionReferences(references, context, filterExpression.getControllingExpression(), prefixMapping);
      addExpressionReferences(references, newContext, filterExpression.getFilter(), prefixMapping);
    } else if (expression instanceof FunctionCall) {
      String newContext = SaxonXPathExpressionSerializer.serialize(expression, prefixMapping);

      if (expression instanceof Instance) {
        references.add(newContext);
      }

      for (Iterator it = expression.iterateSubExpressions(); it.hasNext();) {
        addExpressionReferences(references, context, (Expression) it.next(), prefixMapping);
      }
    } else if (expression instanceof ContextItemExpression) {
      references.add(context != null ? context : ".");
    } else if (expression instanceof ParentNodeExpression) {
      references.add(context != null ? (context + "/..") : "..");
    } else if (expression instanceof Assignation) {
      Assignation a = (Assignation) expression;
      addExpressionReferences(references, context, a.getSequence(), prefixMapping);
      if (null != a.getAction()) {
        addExpressionReferences(references, context, a.getAction(), prefixMapping);
      }
    } else {
      Iterator iter = expression.iterateSubExpressions();
      while (iter.hasNext()) {
        addExpressionReferences(references, context, (Expression) iter.next(), prefixMapping);
      }
    }
  }

}
