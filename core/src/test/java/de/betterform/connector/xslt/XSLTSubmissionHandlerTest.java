/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.xslt;

import de.betterform.xml.dom.DOMComparator;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import junit.framework.TestCase;
import org.w3c.dom.Document;

/**
 * Tests the output control.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XSLTSubmissionHandlerTest.java 2823 2007-08-29 21:40:35Z joern $
 */
public class XSLTSubmissionHandlerTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;


    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();

        String path = getClass().getResource("XSLTSubmissionTest.xhtml").getPath();
        //        System.out.println("path: " + path);
        //set the XForms document to process
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("XSLTSubmissionTest.xhtml"));

        //set the base URI for this process
        this.xformsProcesssorImpl.setBaseURI("file://" + path);


        this.xformsProcesssorImpl.init();
    }

    public void testXSLTSubmission() throws Exception {
        this.xformsProcesssorImpl.dispatch("do1", DOMEventNames.ACTIVATE);
        Document doc = xformsProcesssorImpl.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument();

        assertEquals("1", XPathUtil.evaluateAsString(doc, "//item[1]"));
        assertEquals("2", XPathUtil.evaluateAsString(doc, "//item[2]"));
        assertEquals("3", XPathUtil.evaluateAsString(doc, "//item[3]"));
        assertEquals("4", XPathUtil.evaluateAsString(doc, "//item[4]"));
        assertEquals("5", XPathUtil.evaluateAsString(doc, "//item[5]"));
        this.xformsProcesssorImpl.dispatch("do2", DOMEventNames.ACTIVATE);
        doc = xformsProcesssorImpl.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument();
        assertEquals("5", XPathUtil.evaluateAsString(doc, "//item[1]"));
        assertEquals("4", XPathUtil.evaluateAsString(doc, "//item[2]"));
        assertEquals("3", XPathUtil.evaluateAsString(doc, "//item[3]"));
        assertEquals("2", XPathUtil.evaluateAsString(doc, "//item[4]"));
        assertEquals("1", XPathUtil.evaluateAsString(doc, "//item[5]"));
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
