/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms;

import de.betterform.xml.xforms.exception.XFormsException;
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
public class BindingTest extends XMLTestBase {
//	static {
//		org.apache.log4j.BasicConfigurator.configure();
//	}

    private XFormsProcessorImpl xformsProcesssorImpl;

    public BindingTest(String name) {
        super(name);
    }

    public void testAbsoluteRefs() throws Exception {
        assertValues("My data", "My label", 1);
    }

    public void testBinds() throws Exception {
        assertValues("My data", "My label", 2);
    }

    public void testRefs1() throws Exception {
        assertValues("My data", "My label", 3);
    }

    public void testRefs2() throws Exception {
        assertValues("My data", "My label", 4);
    }

    public void testRelativeRefWithBind() throws Exception {
        assertValues("My data", "My label", 5);
    }

    public void testBindWithRefs() throws Exception {
        assertValues("My data", "My label", 6);
    }

    public void testRelativeGroupWithBind() throws Exception {
        assertValues("My data", "My label", 7);
    }

    public void testRelativeToText() throws Exception {
        assertValues("My other data", "My other label", 8);
    }

    public void testBindRelativeToText() throws Exception {
        assertValues("My other data", "My other label", 9);
    }

    public void testBindWithRef2() throws Exception {
        assertValues("My data", "My label", 10);
    }

    public void testBindWithRef3() throws Exception {
        assertValues("My data", "My label", 11);
    }

    public void testBindWithRef5() throws Exception {
        assertValues("My data", "My label", 15);
    }

    public void testNestedContext() throws Exception {
        Document doc = this.xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("1", XPathUtil.evaluateAsString(doc, "//*[@id='repeat-1']/xf:group[1]//xf:output/bf:data/text()"));
        assertEquals("2", XPathUtil.evaluateAsString(doc, "//*[@id='repeat-1']/xf:group[2]//xf:output/bf:data/text()"));
        assertEquals("3", XPathUtil.evaluateAsString(doc, "//*[@id='repeat-1']/xf:group[3]//xf:output/bf:data/text()"));
        assertEquals("1", XPathUtil.evaluateAsString(doc, "//*[@id='repeat-2']/xf:group[1]//xf:output/bf:data/text()"));
        assertEquals("2", XPathUtil.evaluateAsString(doc, "//*[@id='repeat-2']/xf:group[2]//xf:output/bf:data/text()"));
        assertEquals("3", XPathUtil.evaluateAsString(doc, "//*[@id='repeat-2']/xf:group[3]//xf:output/bf:data/text()"));
    }

    public void testDependentBinding() throws Exception {
        assertValues("My data", "My label", 13);
    }

    public void testDependentBinding2() throws Exception {
        assertValues("My data", "My label", 14);
    }

    public void testAbsoluteBindings() throws Exception {
        Document doc = this.xformsProcesssorImpl.getContainer().getDocument();

        assertEquals("1", XPathUtil.evaluateAsString(doc, "//*[@id='input-16']/bf:data/text()"));
        assertEquals("My label", XPathUtil.evaluateAsString(doc, "//*[@id='input-16']/xf:label/text()"));
    }


    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream("BindingTest.xhtml"));

        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(document);
        this.xformsProcesssorImpl.init();
    }

    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

    private void assertValues(String input, String label, int number) throws XFormsException {
        Document doc = this.xformsProcesssorImpl.getContainer().getDocument();
        assertEquals(input, XPathUtil.evaluateAsString(doc, "//*[@id='input-" + number + "']/bf:data/text()").trim());
        assertEquals(label, XPathUtil.evaluateAsString(doc, "//*[@id='input-" + number + "']/xf:label/text()"));
    }

}

// end of class
