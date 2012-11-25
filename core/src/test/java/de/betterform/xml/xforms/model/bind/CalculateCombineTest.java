/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model.bind;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Test cases for binding contexts.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: BindingTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class CalculateCombineTest extends XMLTestBase {
	static {
		org.apache.log4j.BasicConfigurator.configure();
	}

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document doc;

    public CalculateCombineTest(String name) {
        super(name);
    }

    public void testCalculateSameParent() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("aBind");
        assertNotNull(bind);
        assertNull(bind.getCalculate());
        DOMUtil.prettyPrintDOM(doc);
        assertEquals("string", XPathUtil.evaluateAsString(doc, "//*[@id='a']/bf:data/@bf:type"));

    }

    public void testCalculateCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("bBind1");
        assertNotNull(bind);
        assertEquals("false()",bind.getCalculate());

        bind = (Bind) xformsProcesssorImpl.getContainer().lookup("bBind2");
        assertNotNull(bind);
        assertNull(bind.getDatatype());
        assertEquals("string", XPathUtil.evaluateAsString(doc, "//*[@id='b']/bf:data/@bf:type"));
    }
    public void testCalculateMixedCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("cBind1");
        assertNotNull(bind);
        assertEquals("false()",bind.getCalculate());

        bind = (Bind) xformsProcesssorImpl.getContainer().lookup("cBind2");
        assertNotNull(bind);
        assertNull(bind.getDatatype());

        assertEquals("date", XPathUtil.evaluateAsString(doc, "//*[@id='c']/bf:data/@bf:type"));
    }

    public void testCalculateMixedOneParentCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("dBind");
        assertNotNull(bind);
        assertEquals("date",bind.getCalculate());
        assertEquals("date", XPathUtil.evaluateAsString(doc, "//*[@id='d']/bf:data/@bf:type"));
    }

    public void testCalculateCombineStandard() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("eBind1");
        assertNotNull(bind);
        assertEquals("false()",bind.getCalculate());

        bind = (Bind) xformsProcesssorImpl.getContainer().lookup("eBind2");
        assertNotNull(bind);
        assertEquals("false() or true()",bind.getReadonly());

        assertEquals("true", XPathUtil.evaluateAsString(doc, "//*[@id='e']/bf:data/@bf:type"));
    }

    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream("CalculateCombineTest.xhtml"));

        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(document);
        this.xformsProcesssorImpl.init();
        this.doc = this.xformsProcesssorImpl.getXForms();
    }

    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }


}

// end of class
