// Copyright 2010 betterForm
package de.betterform.xml.xforms.ui;

import junit.framework.TestCase;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import de.betterform.xml.dom.DOMUtil;
import org.w3c.dom.Document;

/**
 * Tests the upload control.
 *
 * @author Joern Turner
 * @author Lars Windauer
 * @version $Id: AVTTest.java
 */
public class AVTTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;

    /**
     * Tests ui element state.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testAVT() throws Exception {

        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("AVT.xhtml"));
        this.xformsProcesssorImpl.init();

        Document host = this.xformsProcesssorImpl.getContainer().getDocument();
        // DOMUtil.prettyPrintDOM(host);

        assertEquals("must be the same", "hallo foo welt", XPathUtil.evaluateAsString(host, "//*[@id='output1']/bf:data"));
        assertEquals("must be the same", "hallo foo welt", XPathUtil.evaluateAsString(host, "//*[@id='compClass']/@class"));
        assertEquals("must be the same", "hallo foo welt", XPathUtil.evaluateAsString(host, "//*[@id='output1']/@class"));
        assertEquals("must be the same", "hallo foo welt", XPathUtil.evaluateAsString(host, "//*[@id='input1']/@class"));

        Selector selector = (Selector) this.xformsProcesssorImpl.getContainer().lookup("select");
        selector.setValue("bar");

        assertEquals("must be the same", "hallo bar welt", XPathUtil.evaluateAsString(host, "//*[@id='output1']/bf:data"));
        assertEquals("must be the same", "hallo bar welt", XPathUtil.evaluateAsString(host, "//*[@id='compClass']/@class"));
        assertEquals("must be the same", "hallo bar welt", XPathUtil.evaluateAsString(host, "//*[@id='output1']/@class"));
        assertEquals("must be the same", "hallo bar welt", XPathUtil.evaluateAsString(host, "//*[@id='input1']/@class"));


        // DOMUtil.prettyPrintDOM(host);

        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }


    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {

    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
    }

}
