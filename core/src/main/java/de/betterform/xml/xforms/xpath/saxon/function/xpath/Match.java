/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:tobias.krebs@betterform.de">tobi</a>
 * @version $Id: Match 26.10.2010 tobi $
 */
public class Match extends XFormsFunction {
    private static Map m_regexPatterns = new HashMap();

    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
        return this;
    }

    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
        if (argument.length < 2 || argument.length > 3) {
            throw new XPathException("There must be 2 arguments (input, regex) or 3 (input, regex, flags) arguments for this function");
        }

        final Expression inputExpression = argument[0];
        final String input = inputExpression.evaluateAsString(xpathContext).toString();

        final Expression regexExpression = argument[1];
        final String regex = regexExpression.evaluateAsString(xpathContext).toString();

        final String flags;
        if (argument.length == 3) {
            final Expression flagsExpression = argument[2];
            flags = flagsExpression.evaluateAsString(xpathContext).toString();
        } else {
            flags = null;
        }

        return BooleanValue.get(match(input,regex,flags));
    }

     public boolean match(String input, String regex, String flags) {

        String regexKey = ((flags == null) || (flags.indexOf('i') == -1)) ? "s " + regex : "i " + regex;

        Pattern pattern = (Pattern) m_regexPatterns.get(regexKey);

        if (pattern == null) {
            pattern = (regexKey.charAt(0) == 'i') ? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) : Pattern.compile(regex);
            m_regexPatterns.put(regexKey, pattern);
        }

        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
