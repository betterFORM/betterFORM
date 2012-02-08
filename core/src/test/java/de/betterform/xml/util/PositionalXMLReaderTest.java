/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.util;

import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.InputStream;

/**
 * Tests the namespace resolver.
 *
 * @author Ulrich Nicolas Liss&eacute;, Joern Turner
 * @version $Id: NamespaceResolverTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class PositionalXMLReaderTest extends TestCase {

    private Element root = null;
    private Document document;

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testgetLineNumber() throws Exception {
        getClass().getResourceAsStream("5.2.4.a-Duration.xhtml");
        InputStream is = getClass().getResourceAsStream("5.2.4.a-Duration.xhtml");
        Document doc = PositionalXMLReader.readXML(is);
        is.close();

        Node n = XPathUtil.evaluateAsSingleNode(doc, "/xhtml:html//xforms:bind[@id='dayTime_bind']");
        assertEquals("19",n.getUserData("lineNumber"));

    }

    public void testNamespaces() throws Exception {
        getClass().getResourceAsStream("5.2.4.a-Duration.xhtml");
        InputStream is = getClass().getResourceAsStream("PositionalReaderTest.xhtml");
        Document doc = PositionalXMLReader.readXML(is);
        is.close();

        Node n = XPathUtil.evaluateAsSingleNode(doc, "//*[@id='foo']");
        assertEquals("29",n.getUserData("lineNumber"));
        n = XPathUtil.evaluateAsSingleNode(doc, "//*[@id='t-changeValue']");
        assertEquals("30",n.getUserData("lineNumber"));



    }

}
