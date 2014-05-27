/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model.bind;

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
public class RequiredCombineTest extends XMLTestBase {
	static {
		org.apache.log4j.BasicConfigurator.configure();
	}

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document doc;

    public RequiredCombineTest(String name) {
        super(name);
    }

    public void testReadonlySameParent() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("aBind");
        assertNotNull(bind);
        assertEquals("false() or true()", bind.getRequired());
        assertEquals("true", XPathUtil.evaluateAsString(doc, "//*[@id='a']/bf:data/@required"));

    }

    public void testrequiredCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("bBind1");
        assertNotNull(bind);
        assertEquals("false()",bind.getRequired());

        bind = (Bind) xformsProcesssorImpl.getContainer().lookup("bBind2");
        assertNotNull(bind);
        assertEquals("false() or true()",bind.getRequired());
        assertEquals("true", XPathUtil.evaluateAsString(doc, "//*[@id='b']/bf:data/@required"));
    }
    public void testrequiredMixedCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("cBind1");
        assertNotNull(bind);
        assertEquals("false()",bind.getRequired());

        bind = (Bind) xformsProcesssorImpl.getContainer().lookup("cBind2");
        assertNotNull(bind);
        assertEquals("false() or true()",bind.getRequired());

        assertEquals("true", XPathUtil.evaluateAsString(doc, "//*[@id='c']/bf:data/@required"));
    }

    public void testrequiredMixedOneParentCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("dBind");
        assertNotNull(bind);
        assertEquals("false() or true()",bind.getRequired());
        assertEquals("true", XPathUtil.evaluateAsString(doc, "//*[@id='d']/bf:data/@required"));
    }

    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream("RequiredCombineTest.xhtml"));

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
