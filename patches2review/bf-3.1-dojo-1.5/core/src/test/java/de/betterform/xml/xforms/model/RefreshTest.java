/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */



package de.betterform.xml.xforms.model;

import junit.framework.TestCase;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;

/**
 *
 */
public class RefreshTest extends TestCase {

    private XFormsProcessorImpl xformsProcesssorImpl;
    String resourceName = "RefreshTest.xhtml";
    private Document host;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        xformsProcesssorImpl = new XFormsProcessorImpl();
        String path = getClass().getResource(resourceName).getPath();
        xformsProcesssorImpl.setBaseURI("file://" + path.substring(0, path.lastIndexOf(resourceName)));
        xformsProcesssorImpl.setXForms(getClass().getResourceAsStream(resourceName));
        xformsProcesssorImpl.init();
        host = xformsProcesssorImpl.getContainer().getDocument();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testUnboundRefresh() throws Exception {
        xformsProcesssorImpl.dispatch("triggerUnbound", DOMEventNames.ACTIVATE);
        //DOMUtil.prettyPrintDOM(processor.getContainer().getDocument());
        assertEquals("10", XPathUtil.evaluateAsString(host, "//xf:output[@id='output1']/bf:data"));
        assertEquals("11", XPathUtil.evaluateAsString(host, "//xf:output[@id='output3']/bf:data"));

    }

    public void testCommonUpdating() throws Exception {
        assertEquals("2", XPathUtil.evaluateAsString(host, "//xf:output[@id='output2']/xf:label"));
        xformsProcesssorImpl.dispatch("triggerCommon", DOMEventNames.ACTIVATE);
        assertEquals("10", XPathUtil.evaluateAsString(host, "//xf:output[@id='output2']/xf:label"));

    }

    public void testCaseUpdating() throws Exception {
        xformsProcesssorImpl.dispatch("triggerUnbound", DOMEventNames.ACTIVATE);
        assertEquals("10", XPathUtil.evaluateAsString(host, "//xf:output[@id='output5']/bf:data"));
        assertEquals("11", XPathUtil.evaluateAsString(host, "//xf:output[@id='output6']/bf:data"));
        xformsProcesssorImpl.dispatch("triggerSwitchCase", DOMEventNames.ACTIVATE);
        //DOMUtil.prettyPrintDOM(processor.getContainer().getDocument());
        assertEquals("10", XPathUtil.evaluateAsString(host, "//xf:output[@id='output7']/bf:data"));
        assertEquals("11", XPathUtil.evaluateAsString(host, "//xf:output[@id='output8']/bf:data"));

    }

    public void testReplaceInstance() throws XFormsException {
        xformsProcesssorImpl.dispatch("replace-instance", DOMEventNames.ACTIVATE);
        //DOMUtil.prettyPrintDOM(processor.getContainer().getDocument());
        assertEquals("4", XPathUtil.evaluateAsString(host, "//xf:output[@id='output1']/bf:data"));
        assertEquals("5", XPathUtil.evaluateAsString(host, "//xf:output[@id='output3']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:output[@id='output4']/bf:data/@bf:enabled"));
    }

    public void testMultipleSetValue() throws XFormsException {
        xformsProcesssorImpl.dispatch("triggerMultipleSetvalue", DOMEventNames.ACTIVATE);
        //DOMUtil.prettyPrintDOM(processor.getContainer().getDocument());
        assertEquals("40", XPathUtil.evaluateAsString(host, "//xf:output[@id='output2']/bf:data"));
    }


}
