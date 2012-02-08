/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.ui;

import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import junit.framework.TestCase;
import org.w3c.dom.Document;

/**
 * Tests the group element.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: GroupTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class GroupTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document host;

    /**
     * Tests initializing a group without a binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGroup() throws Exception {
        assertEquals("1", XPathUtil.evaluateAsString(host, "count(//xf:group[@id='group']/*)"));
        assertEquals("xf:output", XPathUtil.evaluateAsString(host, "name(//xf:group[@id='group']/*[1])"));
    }

    /**
     * Tests initializing a group with a model binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGroupModelBinding() throws Exception {
        assertEquals("2", XPathUtil.evaluateAsString(host, "count(//xf:group[@id='group-model-binding']/*)"));
        assertEquals("xf:output", XPathUtil.evaluateAsString(host, "name(//xf:group[@id='group-model-binding']/*[1])"));

        assertEquals("bf:data", XPathUtil.evaluateAsString(host, "name(//xf:group[@id='group-model-binding']/*[2])"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:group[@id='group-model-binding']/bf:data/@bf:enabled"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:group[@id='group-model-binding']/bf:data/@bf:readonly"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:group[@id='group-model-binding']/bf:data/@bf:required"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:group[@id='group-model-binding']/bf:data/@bf:valid"));
    }

    /**
     * Tests initializing a group with an ui binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGroupUIBinding() throws Exception {
        assertEquals("2", XPathUtil.evaluateAsString(host, "count(//xf:group[@id='group-ui-binding']/*)"));
        assertEquals("xf:output", XPathUtil.evaluateAsString(host, "name(//xf:group[@id='group-ui-binding']/*[1])"));

        assertEquals("bf:data", XPathUtil.evaluateAsString(host, "name(//xf:group[@id='group-ui-binding']/*[2])"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:group[@id='group-ui-binding']/bf:data/@bf:enabled"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:group[@id='group-ui-binding']/bf:data/@bf:readonly"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:group[@id='group-ui-binding']/bf:data/@bf:required"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:group[@id='group-ui-binding']/bf:data/@bf:valid"));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("GroupTest.xhtml"));
        this.xformsProcesssorImpl.init();

        this.host = this.xformsProcesssorImpl.getContainer().getDocument();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

}

// end of class
