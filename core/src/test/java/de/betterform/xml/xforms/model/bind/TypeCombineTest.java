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
public class TypeCombineTest extends XMLTestBase {
	static {
		org.apache.log4j.BasicConfigurator.configure();
	}

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document doc;

    public TypeCombineTest(String name) {
        super(name);
    }

    public void testTypeSameParent() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("aBind");
        assertNotNull(bind);
        assertNull(bind.getDatatype());
        DOMUtil.prettyPrintDOM(doc);
        assertEquals("string", XPathUtil.evaluateAsString(doc, "//*[@id eq 'a']/bf:data/@type"));

    }

    public void testTypeCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("bBind1");
        assertNotNull(bind);
        //bf:type is ignored so default will apply. If nothing is specified via @type it returns null and later in processing defaults to 'string'
        assertNull(bind.getDatatype());

        bind = (Bind) xformsProcesssorImpl.getContainer().lookup("bBind2");
        assertNotNull(bind);
        assertNull(bind.getDatatype());

        //nothing was specified via a valid bind/@type so type is defaulting to 'string'
        assertEquals("string", XPathUtil.evaluateAsString(doc, "//*[@id eq 'b']/bf:data/@type"));
    }
    public void testTypeMixedCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("cBind1");
        assertNotNull(bind);
        assertEquals("date",bind.getDatatype());

        bind = (Bind) xformsProcesssorImpl.getContainer().lookup("cBind2");
        assertNotNull(bind);
        assertNull(bind.getDatatype());

        assertEquals("date", XPathUtil.evaluateAsString(doc, "//*[@id eq 'c']/bf:data/@type"));
    }

    public void testTypeMixedOneParentCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("dBind");
        assertNotNull(bind);
        assertEquals("date",bind.getDatatype());
        assertEquals("date", XPathUtil.evaluateAsString(doc, "//*[@id eq 'd']/bf:data/@type"));
    }

    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream("TypeCombineTest.xhtml"));

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
