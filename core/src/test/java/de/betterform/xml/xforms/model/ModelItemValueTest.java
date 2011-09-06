/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model;

import junit.framework.TestCase;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.model.XercesElementImpl;
import de.betterform.xml.xforms.model.XercesNodeImpl;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Model item tests.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ModelItemValueTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class ModelItemValueTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private static String XML =
            "<data xmlns:xs='" + NamespaceConstants.XMLSCHEMA_NS + "' xmlns:xsi='" + NamespaceConstants.XMLSCHEMA_INSTANCE_NS + "'>" +
                "<element attribute='value'/>" +
                "<text>I am TEXT</text>" + "" +
                "<cdata><![CDATA[I am <b>CDATA</b>]]></cdata>" +
                "<nillable xsi:nil='true' xsi:type='xs:token'/>" +
            "</data>";

    private Document document;

    /**
     * Tests value retrieval for elements.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetValueForElements() throws Exception {
        ModelItem modelItem;

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0));
        assertEquals("", modelItem.getValue());

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("text").item(0));
        assertEquals("I am TEXT", modelItem.getValue());

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("cdata").item(0));
        assertEquals("I am <b>CDATA</b>", modelItem.getValue());
    }

    /**
     * Tests value retrieval for attributes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetValueForAttributes() throws Exception {
        ModelItem modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0).getAttributes().getNamedItem("attribute"));
        assertEquals("value", modelItem.getValue());
    }

    /**
     * Tests value retrieval for text nodes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetValueForTextNodes() throws Exception {
        ModelItem modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("text").item(0).getFirstChild());
        assertEquals("I am TEXT", modelItem.getValue());
    }

    /**
     * Tests value retrieval for text nodes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetValueForCDATASections() throws Exception {
        ModelItem modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("cdata").item(0).getFirstChild());
        assertEquals("I am <b>CDATA</b>", modelItem.getValue());
    }

    /**
     * Tests value update for elements.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueForElements() throws Exception {
        ModelItem modelItem;

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0));
        modelItem.setValue("test");
        assertEquals("test", this.document.getElementsByTagName("element").item(0).getFirstChild().getNodeValue());

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("text").item(0));
        modelItem.setValue("test");
        assertEquals("test", this.document.getElementsByTagName("element").item(0).getFirstChild().getNodeValue());

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("cdata").item(0));
        modelItem.setValue("test");
        assertEquals("test", this.document.getElementsByTagName("element").item(0).getFirstChild().getNodeValue());
    }

    /**
     * Tests value update for attributes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueForAttributes() throws Exception {
        ModelItem modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0).getAttributes().getNamedItem("attribute"));
        modelItem.setValue("test");
        assertEquals("test", this.document.getElementsByTagName("element").item(0).getAttributes().getNamedItem("attribute").getNodeValue());
    }

    /**
     * Tests value update for text nodes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueForTextNodes() throws Exception {
        ModelItem modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("text").item(0).getFirstChild());
        modelItem.setValue("test");
        assertEquals("test", this.document.getElementsByTagName("text").item(0).getFirstChild().getNodeValue());
    }

    /**
     * Tests value update for text nodes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueForCDATASections() throws Exception {
        ModelItem modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("cdata").item(0).getFirstChild());
        modelItem.setValue("test");
        assertEquals("test", this.document.getElementsByTagName("cdata").item(0).getFirstChild().getNodeValue());
    }

    /**
     * Tests @xsi:nil support.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIsXSINillable() throws Exception {
        ModelItem modelItem;

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0));
        assertEquals(false, modelItem.isXSINillable());

        modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0).getAttributes().getNamedItem("attribute"));
        assertEquals(false, modelItem.isXSINillable());

        modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("text").item(0).getFirstChild());
        assertEquals(false, modelItem.isXSINillable());

        modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("cdata").item(0).getFirstChild());
        assertEquals(false, modelItem.isXSINillable());

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("nillable").item(0));
        assertEquals(true, modelItem.isXSINillable());
    }

    /**
     * Tests @xsi:type support.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetXSIType() throws Exception {
        ModelItem modelItem;

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0));
        assertEquals(null, modelItem.getXSIType());

        modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0).getAttributes().getNamedItem("attribute"));
        assertEquals(null, modelItem.getXSIType());

        modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("text").item(0).getFirstChild());
        assertEquals(null, modelItem.getXSIType());

        modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("cdata").item(0).getFirstChild());
        assertEquals(null, modelItem.getXSIType());

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("nillable").item(0));
        assertEquals("xs:token", modelItem.getXSIType());
    }

    /**
     * Tests @xsi:nil support for value update.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueNillable() throws Exception {
        ModelItem modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("nillable").item(0));

        modelItem.setValue("test");
        assertEquals("false", this.document.getElementsByTagName("nillable").item(0).getAttributes().getNamedItemNS(NamespaceConstants.XMLSCHEMA_INSTANCE_NS, "nil").getNodeValue());
        modelItem.setValue(null);
        assertEquals("true", this.document.getElementsByTagName("nillable").item(0).getAttributes().getNamedItemNS(NamespaceConstants.XMLSCHEMA_INSTANCE_NS, "nil").getNodeValue());
        modelItem.setValue("test");
        assertEquals("false", this.document.getElementsByTagName("nillable").item(0).getAttributes().getNamedItemNS(NamespaceConstants.XMLSCHEMA_INSTANCE_NS, "nil").getNodeValue());
        modelItem.setValue("");
        assertEquals("true", this.document.getElementsByTagName("nillable").item(0).getAttributes().getNamedItemNS(NamespaceConstants.XMLSCHEMA_INSTANCE_NS, "nil").getNodeValue());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        this.document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(XML.getBytes("UTF-8")));
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.document = null;
    }

}
