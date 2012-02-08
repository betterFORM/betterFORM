/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xpath;

import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.exception.XFormsException;

import java.util.Map;
import java.util.Set;

/**
 * A Reference Finder analyzes a given XPath expression and extracts all
 * absolute and relative references to other nodes which may affect the value of
 * the expression. This is needed for dependency tracking and recalculation.
 * <p/>
 * Special care has to taken with regard to the following points:
 * <ol>
 * <li>A reference finder is expected to handle predicates correctly, e.g. for
 * <code>parent[child!='']</code> it is supposed to return
 * <code>parent[child!='']</code> <i>and</i> <code>parent/child</code> as
 * references since the value of the selected parent as well as any child may
 * affect the value of the whole expression.</li>
 * <li>A reference finder is expected to handle all occurrences of the
 * <code>instance()</code> function coorrectly, i.e. it has to be handled like
 * an absolute location path.</li>
 * </ol>
 *
 * TODO: exception handling
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XPathReferenceFinder.java 3253 2008-07-08 09:26:40Z lasse $
 */
public interface XPathReferenceFinder {

    /**
     * Returns the set of all references to other nodes contained in the given
     * XPath expression.
     * <p/>
     * The returned references are expected to be strings and not some
     * XPath processor specific classes.
     *
     * @param xpath the XPath expression.
     * @param prefixMapping the prefix to name space mapping
     * @param functionContext the function context 
     * @return the set of all references to other nodes.
     * @throws XFormsException if a reference detection error occurred.
     */
    Set getReferences(String xpath, Map prefixMapping, Container container) throws XFormsException;
}
