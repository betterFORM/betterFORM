/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.base;

import de.betterform.xml.ns.NamespaceResolver;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author joern turner
 * @version $Id: XMLBaseResolverTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class XMLBaseResolverTest extends TestCase {
//    static {
//        BasicConfigurator.configure();
//    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testResolveXMLBase() throws Exception {
        Document doc = getXmlResource("XMLBaseResolverTest1.xml");
        NamespaceResolver.init(doc.getDocumentElement());

        Node n = XPathUtil.evaluateAsSingleNode(doc, "descendant::*[@xlink:href][1]");
        String s = XMLBaseResolver.resolveXMLBase(n, "new.xml");
        assertTrue(s.equals("http://example.org/today/new.xml"));

        n = XPathUtil.evaluateAsSingleNode(doc, "descendant::*[@xlink:href][2]");

        s = XMLBaseResolver.resolveXMLBase(n, "pick1.xml");
        assertTrue(s.equals("http://example.org/hotpicks/pick1.xml"));

        n = XPathUtil.evaluateAsSingleNode(doc, "descendant::*[@xlink:href][3]");

        s = XMLBaseResolver.resolveXMLBase(n, "pick2.xml");
        assertTrue(s.equals("http://example.org/hotpicks/pick2.xml"));
        n = XPathUtil.evaluateAsSingleNode(doc, "descendant::*[@xlink:href][4]");

        s = XMLBaseResolver.resolveXMLBase(n, "pick3.xml");
        assertTrue(s.equals("http://example.org/hotpicks/pick3.xml"));
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testResolveXMLBaseLess() throws Exception {
        Document doc = getXmlResource("XMLBaseResolverTest2.xml");
        NamespaceResolver.init(doc.getDocumentElement());

        Node n = XPathUtil.evaluateAsSingleNode(doc, "descendant::*[@xlink:href][1]");
        String s = XMLBaseResolver.resolveXMLBase(n, "new.xml");
        assertTrue(s.equals("new.xml"));

        n = XPathUtil.evaluateAsSingleNode(doc, "descendant::*[@xlink:href][2]");
        s = XMLBaseResolver.resolveXMLBase(n, "pick1.xml");
        assertTrue(s.equals("pick1.xml"));

        n = XPathUtil.evaluateAsSingleNode(doc, "descendant::*[@xlink:href][3]");
        s = XMLBaseResolver.resolveXMLBase(n, "pick2.xml");
        assertTrue(s.equals("pick2.xml"));

        n = XPathUtil.evaluateAsSingleNode(doc, "descendant::*[@xlink:href][4]");
        s = XMLBaseResolver.resolveXMLBase(n, "pick3.xml");
        assertTrue(s.equals("pick3.xml"));
    }

    /**
     *
     */
    protected void setUp() throws Exception {
    }

    /**
     * __UNDOCUMENTED__
     */
    protected void tearDown() {
    }

    // ++++++++++++ oops, don't look at my private parts!  +++++++++++++++
    //helper - should be moved elsewhere...
    private Document getXmlResource(String fileName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        // Create builder.
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parse files.
        return builder.parse(getClass().getResourceAsStream(fileName));
    }
}
