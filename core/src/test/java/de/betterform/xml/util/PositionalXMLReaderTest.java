/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.util;

import de.betterform.xml.dom.DOMUtil;
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

    public void testGetLineNumber() throws Exception {
        Document doc = readResourceWithPositionalXMLReader("5.2.4.a-Duration.xhtml");

        Node n = XPathUtil.evaluateAsSingleNode(doc, "/xhtml:html//xforms:bind[@id='dayTime_bind']");
        assertEquals("19",n.getUserData("lineNumber"));

    }

    public void testNamespaces() throws Exception {
        Document doc = readResourceWithPositionalXMLReader("PositionalReaderTest.xhtml");

        Node n = XPathUtil.evaluateAsSingleNode(doc, "//*[@id='foo']");
        assertEquals("29",n.getUserData("lineNumber"));
        n = XPathUtil.evaluateAsSingleNode(doc, "//*[@id='t-changeValue']");
        assertEquals("30",n.getUserData("lineNumber"));
    }

    public void testGetLineNumberWithMultilineComments() throws Exception {
        Document doc = readResourceWithPositionalXMLReader("brokenform.xhtml");

        Node n = XPathUtil.evaluateAsSingleNode(doc, "//*[@id='ui']");
        assertEquals("30", n.getUserData("lineNumber"));
    }

    public void testGetLineNumber2() throws Exception {
        Document doc = readResourceWithPositionalXMLReader("BindError.xhtml");

        Node n = XPathUtil.evaluateAsSingleNode(doc, "/html:html[1]/html:body[1]/html:div[1]/xf:model[1]/xf:bind[1]");
        assertEquals("15", n.getUserData("lineNumber"));
    }

    private Document readResourceWithPositionalXMLReader(String filePath) throws Exception {
        InputStream is = getClass().getResourceAsStream(filePath);
        Document doc = PositionalXMLReader.readXML(is);
        is.close();

        //DOMUtil.prettyPrintDOM(doc);
        return doc;
    }

}
