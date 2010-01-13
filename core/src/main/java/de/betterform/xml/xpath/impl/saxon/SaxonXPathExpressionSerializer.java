/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xpath.impl.saxon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.saxon.expr.Assignation;
import net.sf.saxon.expr.AxisExpression;
import net.sf.saxon.expr.BinaryExpression;
import net.sf.saxon.expr.CompareToIntegerConstant;
import net.sf.saxon.expr.ContextItemExpression;
import net.sf.saxon.expr.ErrorExpression;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.FilterExpression;
import net.sf.saxon.expr.FunctionCall;
import net.sf.saxon.expr.IntegerRangeTest;
import net.sf.saxon.expr.IsLastExpression;
import net.sf.saxon.expr.Literal;
import net.sf.saxon.expr.ParentNodeExpression;
import net.sf.saxon.expr.PathExpression;
import net.sf.saxon.expr.PatternMatchExpression;
import net.sf.saxon.expr.RootExpression;
import net.sf.saxon.expr.SimpleExpression;
import net.sf.saxon.expr.SimpleMappingExpression;
import net.sf.saxon.expr.TailExpression;
import net.sf.saxon.expr.Token;
import net.sf.saxon.expr.UnaryExpression;
import net.sf.saxon.expr.VariableReference;
import net.sf.saxon.instruct.Instruction;
import net.sf.saxon.instruct.NumberInstruction;
import net.sf.saxon.instruct.SimpleContentConstructor;
import net.sf.saxon.om.Axis;
import net.sf.saxon.om.FastStringBuffer;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.pattern.PatternSponsor;
import net.sf.saxon.sort.ConditionalSorter;
import net.sf.saxon.sort.SortExpression;
import net.sf.saxon.sort.TupleExpression;
import net.sf.saxon.sort.TupleSorter;

/**
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class SaxonXPathExpressionSerializer {
    public static String serialize(Expression expr, Map prefixMapping) {
	FastStringBuffer result = new FastStringBuffer(64);

	final Map<String, String> reversePrefixMapping = new HashMap<String, String>();
	for (Iterator<Map.Entry<String, String>> it = prefixMapping.entrySet().iterator(); it.hasNext();) {
		Map.Entry<String, String> entry = it.next();
		reversePrefixMapping.put(entry.getValue(), entry.getKey());
	}
	
	serialize(result, expr, reversePrefixMapping);

	return result.toString();
    }

	/**
     * @param result
     * @param expr
     */
    private static void serialize(FastStringBuffer result, Expression expr, Map reversePrefixMapping) {
	if (expr instanceof Assignation) {
	    // XXX not yet supported
	} else if (expr instanceof AxisExpression) {
	    AxisExpression axisExpression = (AxisExpression) expr;
	    result.append(Axis.axisName[axisExpression.getAxis()]);
	    result.append("::");

	    final NodeTest nodeTest = axisExpression.getNodeTest();
	    if (nodeTest == null) {
	    	result.append("node()");
	    }
	    else {
	    	result.append(fixPreFixes( nodeTest.toString(), reversePrefixMapping));
	    }
	} else if (expr instanceof BinaryExpression) {
	    BinaryExpression binaryExpression = (BinaryExpression) expr;
	    result.append('(');
	    serialize(result, binaryExpression.getOperands()[0], reversePrefixMapping);
	    result.append(Token.tokens[binaryExpression.getOperator()]);
	    serialize(result, binaryExpression.getOperands()[1], reversePrefixMapping);
	    result.append(')');
	} else if (expr instanceof CompareToIntegerConstant) {
	    CompareToIntegerConstant compareToIntegerConstant = (CompareToIntegerConstant) expr;
	    result.append('(');
	    serialize(result, compareToIntegerConstant.getOperand(), reversePrefixMapping);
	    result.append(Token.tokens[compareToIntegerConstant.getComparisonOperator()]);
	    result.append(Long.toString(compareToIntegerConstant.getComparand()));
	    result.append(')');
	} else if (expr instanceof ConditionalSorter) {
	    // XXX not yet supported
	} else if (expr instanceof ContextItemExpression) {
	    result.append('.');
	} else if (expr instanceof ErrorExpression) {
	    // Error do nothing
	} else if (expr instanceof FilterExpression) {
	    FilterExpression filterExpression = (FilterExpression) expr;
	    result.append('(');
	    serialize(result, filterExpression.getBaseExpression(), reversePrefixMapping);
	    result.append('[');
	    serialize(result, filterExpression.getFilter(), reversePrefixMapping);
	    result.append("])");

	} else if (expr instanceof FunctionCall) {
	    FunctionCall functionCall = (FunctionCall) expr;
	    StructuredQName name = functionCall.getFunctionName();
	    if (name.getPrefix() != null && name.getPrefix().length() > 0)
	    {
		result.append(name.getPrefix());
		result.append(":");
	    }
	    result.append(name.getLocalName());
	    result.append("(");
	    
	    Iterator iter = functionCall.iterateSubExpressions();
	    boolean first = true;
	    while (iter.hasNext()) {
		result.append(first ? "" : ", ");
		SaxonXPathExpressionSerializer.serialize(result, (Expression) iter.next(), reversePrefixMapping);
		first = false;
	    }
	    
	    result.append(")");
	} else if (expr instanceof Instruction) {
	    // This is not an XPath expression
	} else if (expr instanceof IntegerRangeTest) {
	    // XXX not yet supported
	} else if (expr instanceof IsLastExpression) {
	    result.append("position() eq last()");
	} else if (expr instanceof Literal) {
	    Literal literal = (Literal)expr;
	    result.append(literal.getValue().toString());
	} else if (expr instanceof NumberInstruction) {
	    // This is not an XPath expression
	} else if (expr instanceof PathExpression) {
	    PathExpression pathExpression = (PathExpression)expr;
	    result.append('(');
	    serialize(result, pathExpression.getStartExpression(), reversePrefixMapping);
	    result.append('/'); 
	    serialize(result, pathExpression.getStepExpression(), reversePrefixMapping);
	    result.append(')');
	} else if (expr instanceof PatternMatchExpression) {
	    // XXX not yet supported
	} else if (expr instanceof PatternSponsor) {
	    // XXX not yet supported
	} else if (expr instanceof SimpleContentConstructor) {
	    // This is not an XPath expression
	} else if (expr instanceof SimpleExpression) {
	    // This is not an XPath expression
	} else if (expr instanceof SimpleMappingExpression) {
	    // XXX not yet supported
	} else if (expr instanceof ParentNodeExpression) {
	    result.append("..");
	} else if (expr instanceof RootExpression) {
	    // do nothing
	} else if (expr instanceof SortExpression) {
	    // XXX not yet supported
	} else if (expr instanceof TailExpression) {
	    // XXX not yet supported
	} else if (expr instanceof TupleExpression) {
	    // This is not an XPath expression
	} else if (expr instanceof TupleSorter) {
	    // This is not an XPath expression
	} else if (expr instanceof UnaryExpression) {
	    UnaryExpression unaryExpression = (UnaryExpression)expr;
	    serialize(result, unaryExpression.getBaseExpression(), reversePrefixMapping); // Not sure if this is correct in all cases
	} else if (expr instanceof VariableReference) {
	    VariableReference variableReference = (VariableReference)expr;
	    String d = variableReference.getDisplayName();
	    result.append("$");
	    result.append(d == null ? "$" : d);
	}
    }

	private static String fixPreFixes(String xpath, Map<String, String> reversePrefixMapping) {
		FastStringBuffer result = new FastStringBuffer(xpath.length());
		
		Matcher m = Pattern.compile("\\{[^\\}]+\\}").matcher(xpath);
		
		int iLast = 0;
		while(m.find())
		{
			result.append(xpath.substring(iLast, m.start()));
			final String match = m.group();
			result.append(reversePrefixMapping.get(match.substring(1, match.length()-1)));
			result.append(':');
			iLast = m.end();
		}
		
		result.append(xpath.substring(iLast));
		return result.toString();
	}
}
