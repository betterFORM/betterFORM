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
 * Tests the switch element.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: SwitchTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class SwitchTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document host;

    /**
     * Tests initializing a switch without a binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSwitch() throws Exception {
        assertEquals("3", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id='switch']/*)"));

        assertEquals("xf:case", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch']/*[1])"));
        assertEquals("xf:case", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch']/*[2])"));
        assertEquals("xf:case", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch']/*[3])"));
    }

    /**
     * Tests initializing a switch with a model binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSwitchModelBinding() throws Exception {
        assertEquals("4", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id='switch-model-binding']/*)"));

        assertEquals("xf:case", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch-model-binding']/*[1])"));
        assertEquals("xf:case", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch-model-binding']/*[2])"));
        assertEquals("xf:case", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch-model-binding']/*[3])"));

        assertEquals("bf:data", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch-model-binding']/*[4])"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-model-binding']/bf:data/@enabled"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-model-binding']/bf:data/@readonly"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-model-binding']/bf:data/@required"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-model-binding']/bf:data/@valid"));
    }

    /**
     * Tests initializing a switch with an ui binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSwitchUIBinding() throws Exception {
        assertEquals("4", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id='switch-ui-binding']/*)"));

        assertEquals("xf:case", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch-ui-binding']/*[1])"));
        assertEquals("xf:case", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch-ui-binding']/*[2])"));
        assertEquals("xf:case", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch-ui-binding']/*[3])"));

        assertEquals("bf:data", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch-ui-binding']/*[4])"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-ui-binding']/bf:data/@enabled"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-ui-binding']/bf:data/@readonly"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-ui-binding']/bf:data/@required"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-ui-binding']/bf:data/@valid"));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("SwitchTest.xhtml"));
        this.xformsProcesssorImpl.init();
        this.host = xformsProcesssorImpl.getXForms();
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
