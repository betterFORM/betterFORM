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
 * Tests repeat structures.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RangeTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class RangeTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document host;

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("RangeTest.xhtml"));
        this.xformsProcesssorImpl.init();
        this.host = xformsProcesssorImpl.getContainer().getDocument();

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

    /**
     * Tests range bound to integer.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInteger() throws Exception {
        assertEquals("-2", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-1']/bf:data/@bf:start"));
        assertEquals("2", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-1']/bf:data/@bf:end"));
        assertEquals("1", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-1']/bf:data/@bf:step"));
    }

    /**
     * Tests range bound to integer.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIntegerDefaults() throws Exception {
        assertEquals("-2", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-2']/bf:data/@bf:start"));
        assertEquals("2", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-2']/bf:data/@bf:end"));
        assertEquals("1", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-2']/bf:data/@bf:step"));
    }

    /**
     * Tests range bound to float.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testFloat() throws Exception {
        assertEquals("-2", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-3']/bf:data/@bf:start"));
        assertEquals("2", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-3']/bf:data/@bf:end"));
        assertEquals("1", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-3']/bf:data/@bf:step"));
    }

    /**
     * Tests range bound to float.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testFloatDefaults() throws Exception {
        assertEquals("-2", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-4']/bf:data/@bf:start"));
        assertEquals("2", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-4']/bf:data/@bf:end"));
        assertEquals("1", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-4']/bf:data/@bf:step"));
    }

    /**
     * Tests range bound to double.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDouble() throws Exception {
        assertEquals("-2", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-5']/bf:data/@bf:start"));
        assertEquals("2", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-5']/bf:data/@bf:end"));
        assertEquals("1", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-5']/bf:data/@bf:step"));
    }

    /**
     * Tests range bound to double.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDoubleDefaults() throws Exception {
        assertEquals("-2", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-6']/bf:data/@bf:start"));
        assertEquals("2", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-6']/bf:data/@bf:end"));
        assertEquals("1", XPathUtil.evaluateAsString(host, "//xf:range[@id='range-6']/bf:data/@bf:step"));
    }

}

// end of class
