/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xpath.impl.saxon;

import net.sf.saxon.expr.*;

import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.xpath.saxon.function.Instance;
import de.betterform.xml.xpath.XPathReferenceFinder;

import java.util.*;

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
     * @param functionContext the function context
     * @return the set of all references to other nodes.
     * @throws XFormsException if a reference detection error occurred.
     */
    public Set getReferences(String xpath, Map prefixMapping, Container container) throws XFormsException {
        try {
            Expression expression = XPathCache.getInstance().getXPathExpression(xpath, prefixMapping, container.getConfiguration()).getInternalExpression();
            HashSet references = new HashSet();
            addExpressionReferences(references, null, expression, prefixMapping);

            return references;
        }
        catch (Exception e) {
            throw new XFormsException("Can't resolve references for: " + xpath + " (" + e.getMessage() + ")",e);
        }
    }

    private void addExpressionReferences(HashSet references, String context, Expression expression, Map prefixMapping) {
	if (expression instanceof AxisExpression)
	{
	    references.add((context!=null?(context + "/"):"") + SaxonXPathExpressionSerializer.serialize(expression, prefixMapping));
	}
	else if (expression instanceof PathExpression)
	{
	    final PathExpression pathExpression = (PathExpression)expression;
	    final Expression startExpression = pathExpression.getStartExpression();
	    
	    addExpressionReferences(references, context, startExpression, prefixMapping); 
	    addExpressionReferences(references, SaxonXPathExpressionSerializer.serialize(startExpression, prefixMapping), pathExpression.getStepExpression(), prefixMapping);
	    
	}
	else if (expression instanceof FilterExpression)
	{
	    final FilterExpression filterExpression = (FilterExpression)expression;
	    
	    final HashSet baseReferences = new HashSet();
	    addExpressionReferences(baseReferences, context, filterExpression.getBaseExpression(), prefixMapping);
	    
	    for (Iterator it = baseReferences.iterator(); it.hasNext();) {
		String newContext = (String) it.next();
		
		addExpressionReferences(references, newContext, filterExpression.getFilter(), prefixMapping);
	    }
	    
	    references.addAll(baseReferences);
	}
	else if (expression instanceof FunctionCall)
	{
	    String newContext = SaxonXPathExpressionSerializer.serialize(expression, prefixMapping);
	    
	    if (expression instanceof Instance)
	    {
		references.add(newContext);
	    }
	    
	    for (Iterator it = expression.iterateSubExpressions(); it.hasNext();) {
		
		addExpressionReferences(references, context, (Expression) it.next(), prefixMapping);
	    }
	}
	else if (expression instanceof ContextItemExpression) {
	    references.add(context!=null?context:".");
	}
	else if (expression instanceof ParentNodeExpression) {
	    references.add(context!=null?(context + "/.."):"..");
	}
	else if (expression instanceof Assignation) {
	  addExpressionReferences(references, context, ((Assignation)expression).getSequence(), prefixMapping);  
	}
	else {
	    Iterator iter = expression.iterateSubExpressions();
	    while (iter.hasNext()) {
		addExpressionReferences(references, context, (Expression) iter.next(), prefixMapping);
	    }
	}
//        // handle location pathes like "../relative/path" or "//absolute/path"
//        if (expression instanceof LocationPath) {
//            // put location path in context and add as reference
//            LocationPath locationPath = (LocationPath) expression;
//            Path path = addLocationPath(context, (LocationPath) expression);
//            references.put(path.toString(), path);
//
//            // pass empty location path when there is no context
//            Expression contextExpression = (Expression) (context == null
//                                                ? this.compiler.locationPath(locationPath.isAbsolute(), null)
//                                                : context);
//            // add references of step predicates
//            addStepReferences(references, contextExpression, locationPath.getSteps());
//            return;
//        }
//        // handle expression pathes like "instance('id')/path" or "ext:function()[predicate]/path"
//        if (expression instanceof ExpressionPath) {
//            // add expression path as reference
//            ExpressionPath expressionPath = (ExpressionPath) expression;
//            references.put(expressionPath.toString(), expressionPath);
//
//            // add references in expression predicates
//            Expression[] predicates = expressionPath.getPredicates();
//            for (int i = 0; predicates != null && i < predicates.length; i++) {
//                addExpressionReferences(references, expressionPath.getExpression(), predicates[i]);
//            }
//
//            // add references in step predicates
//            addStepReferences(references, expressionPath.getExpression(), expressionPath.getSteps());
//            return;
//        }
//        // handle standalone instance() function
//        if (expression instanceof ExtensionFunction) {
//            ExtensionFunction function = (ExtensionFunction) expression;
//            // todo: how to handle all functions returning a nodeset ?
//            if (function.getFunctionName().getName().equals("instance")) {
//                references.put(expression.toString(), expression);
//                return;
//            }
//        }
//        // handle all other operations like "black + white" or "count(../apples and /oranges)"
//        if (expression instanceof Operation) {
//            Operation operation = (Operation) expression;
//            Expression[] arguments = operation.getArguments();
//
//            // add references in function arguments
//            for (int i = 0; arguments != null && i < arguments.length; i++) {
//                addExpressionReferences(references, context, arguments[i]);
//            }
//        }
//        // don't handle constants
//        // if (expression instanceof Constant) {
//        // }
//        // don't handle variable references (maybe needed someday)
//        // if (expression instanceof VariableReference) {
//        //  }
    }

   

//    private void addStepReferences(Map references, Expression context, Step[] steps) {
//        Path path;
//        Step step;
//        Step tmp;
//        Expression[] predicates;
//
//        for (int i = 0; steps != null && i < steps.length; i++) {
//            step = steps[i];
//            predicates = step.getPredicates();
//
//            if (predicates != null && predicates.length > 0) {
//                // create path without current predicates as context
//                tmp = step;
//                steps[i] = (Step) this.compiler.step(step.getAxis(), step.getNodeTest(), null);
//                path = addSteps(context, steps, i + 1);
//                steps[i] = tmp;
//
//                // add references in step predicates
//                for (int j = 0; j < predicates.length; j++) {
//                    addExpressionReferences(references, path, predicates[j]);
//                }
//            }
//        }
//    }
//
//    private Path addLocationPath(Expression context, LocationPath path) {
//        if (context == null || path.isAbsolute()) {
//            return path;
//        }
//
//        Step[] steps = path.getSteps();
//        return addSteps(context, steps, steps.length);
//    }
//
//    private Path addSteps(Expression context, Step[] steps, int length) {
//        List stepList = new ArrayList();
//        if (context instanceof Path) {
//            Path path = (Path) context;
//            if (path.getSteps() != null) {
//                stepList.addAll(Arrays.asList(((Path) context).getSteps()));
//            }
//        }
//        for (int i = 0; i < length; i++) {
//            stepList.add(steps[i]);
//        }
//        Step[] stepArray = (Step[]) stepList.toArray(new Step[stepList.size()]);
//
//        if (context instanceof LocationPath) {
//            LocationPath locationPath = (LocationPath) context;
//            return (Path) this.compiler.locationPath(locationPath.isAbsolute(), stepArray);
//        }
//        if (context instanceof ExpressionPath) {
//            ExpressionPath expressionPath = (ExpressionPath) context;
//            return (Path) this.compiler.expressionPath(expressionPath.getExpression(), expressionPath.getPredicates(), stepArray);
//        }
//        return (Path) this.compiler.expressionPath(context, null, stepArray);
//
//    }

}
