// Copyright 2010 betterForm
package de.betterform.xml.xforms.ui;

import junit.framework.TestCase;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
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
        assertEquals("0", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id='switch-default']/xf:case[1]/@selected)"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-default']/xf:case[2]/@selected"));
        assertEquals("0", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id='switch-default']/xf:case[3]/@selected)"));

        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-default']/xf:case[1]/bf:data/@bf:selected"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-default']/xf:case[2]/bf:data/@bf:selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-default']/xf:case[3]/bf:data/@bf:selected"));
    }

    /**
     * Tests initializing a switch with no selected cases.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCaseNoneSelected() throws Exception {
        assertEquals("0", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id='switch-none']/xf:case[1]/@selected)"));
        assertEquals("0", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id='switch-none']/xf:case[2]/@selected)"));
        assertEquals("0", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id='switch-none']/xf:case[3]/@selected)"));

        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-none']/xf:case[1]/bf:data/@bf:selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-none']/xf:case[2]/bf:data/@bf:selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-none']/xf:case[3]/bf:data/@bf:selected"));
    }

    /**
     * Tests initializing a switch with multiple selected cases.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCaseMultipleSelected() throws Exception {
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-multiple']/xf:case[1]/@selected"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-multiple']/xf:case[2]/@selected"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-multiple']/xf:case[3]/@selected"));

        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-multiple']/xf:case[1]/bf:data/@bf:selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-multiple']/xf:case[2]/bf:data/@bf:selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-multiple']/xf:case[3]/bf:data/@bf:selected"));
    }

    /**
     * Tests initializing a switch with multiple selected cases.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCaseAllDeselected() throws Exception {
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-all']/xf:case[1]/@selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-all']/xf:case[2]/@selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-all']/xf:case[3]/@selected"));

        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-all']/xf:case[1]/bf:data/@bf:selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-all']/xf:case[2]/bf:data/@bf:selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch-all']/xf:case[3]/bf:data/@bf:selected"));
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
