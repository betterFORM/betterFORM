/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.ui;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;

/**
 * Tests the XForms 2.0 switch element.
 *
 * @author Joern Turner
 */
public class SwitchBoundNoNodeTest extends TestCase {
    static {
        org.apache.log4j.BasicConfigurator.configure();
    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document host;

    /**
     * assert that first switch is selected even if the caseref points to a non-existing node
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSwitch() throws Exception {
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch']/xf:case[@id='creditCard']/bf:data/@bf:selected"));
    }




    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("SwitchBoundNoNodeTest.xhtml"));
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
