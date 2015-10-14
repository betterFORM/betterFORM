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
 * Tests the case element.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: CaseTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class CaseTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document host;

    /**
     * Tests initializing a switch with a selected case.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCaseDefaultSelected() throws Exception {
        assertEquals("0", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id eq 'switch-default']/xf:case[1]/@selected)"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-default']/xf:case[2]/@selected"));
        assertEquals("0", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id eq 'switch-default']/xf:case[3]/@selected)"));

        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-default']/xf:case[1]/bf:data/@selected"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-default']/xf:case[2]/bf:data/@selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-default']/xf:case[3]/bf:data/@selected"));
    }

    /**
     * Tests initializing a switch with no selected cases.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCaseNoneSelected() throws Exception {
        assertEquals("0", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id eq 'switch-none']/xf:case[1]/@selected)"));
        assertEquals("0", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id eq 'switch-none']/xf:case[2]/@selected)"));
        assertEquals("0", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id eq 'switch-none']/xf:case[3]/@selected)"));

        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-none']/xf:case[1]/bf:data/@selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-none']/xf:case[2]/bf:data/@selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-none']/xf:case[3]/bf:data/@selected"));
    }

    /**
     * Tests initializing a switch with multiple selected cases.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCaseMultipleSelected() throws Exception {
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-multiple']/xf:case[1]/@selected"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-multiple']/xf:case[2]/@selected"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-multiple']/xf:case[3]/@selected"));

        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-multiple']/xf:case[1]/bf:data/@selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-multiple']/xf:case[2]/bf:data/@selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-multiple']/xf:case[3]/bf:data/@selected"));
    }

    /**
     * Tests initializing a switch with multiple selected cases.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCaseAllDeselected() throws Exception {
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-all']/xf:case[1]/@selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-all']/xf:case[2]/@selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-all']/xf:case[3]/@selected"));

        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-all']/xf:case[1]/bf:data/@selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-all']/xf:case[2]/bf:data/@selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id eq 'switch-all']/xf:case[3]/bf:data/@selected"));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("CaseTest.xhtml"));
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
