/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xpath;

import java.util.List;

/**
 * XPath processor abstraction, work in progress ...
 *
 * TODO: exception handling, initial context (factory ?)
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XPathProcessor.java 2269 2006-08-18 21:01:25Z unl $
 */
public interface XPathProcessor {

    /**
     * Returns the list of all nodes matching the given expression.
     * <p/>
     * If an empty nodeset is selected, this method returns <code>null</code>.
     *
     * @param expression an XPath expression.
     * @return the list of all nodes matching the given expression.
     */
    List selectNodes(String expression);

    /**
     * Returns the first of all nodes matching the given expression.
     * <p/>
     * If an empty nodeset is selected, this method returns <code>null</code>.
     *
     * @param expression an XPath expression.
     * @return the first of all nodes matching the given expression.
     */
    Object selectSingleNode(String expression);

    Object evaluate(String context, String expression);

    Boolean evaluateBoolean(String context, String expression);

    Number evaluateNumber(String context, String expression);

    String evaluateString(String context, String expression);
}
