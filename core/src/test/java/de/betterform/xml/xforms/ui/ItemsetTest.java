/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.ui;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import junit.framework.TestCase;
import org.w3c.dom.Document;

/**
 * Tests the itemset element.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ItemsetTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class ItemsetTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;

    /**
     * Tests initializing the itemset element.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInit() throws Exception {
        assertInitializedItemset("select-1");
        assertInitializedItemset("select-2");
        assertInitializedItemset("select-3");
        assertInitializedItemset("select-4");
        assertInitializedItemset("select-5");
        assertInitializedItemset("select-6");
    }

    /**
     * Tests updating the itemset element.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUpdate() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-update", DOMEventNames.ACTIVATE);

        assertUpdatedItemset("select-1");
        assertUpdatedItemset("select-2");
        assertUpdatedItemset("select-3");
        assertUpdatedItemset("select-4");
        assertUpdatedItemset("select-5");
        assertUpdatedItemset("select-6");
    }

    /**
     * Tests updating the itemset element.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUpdateGrow() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-insert", DOMEventNames.ACTIVATE);
        // DOMUtil.prettyPrintDOM(processor.getContainer().getDocument());
        assertGrownItemset("select-1");
        assertGrownItemset("select-2");
        assertGrownItemset("select-3");
        assertGrownItemset("select-4");
        assertGrownItemset("select-5");
        assertGrownItemset("select-6");
    }

    /**
     * Tests updating the itemset element.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUpdateShrink() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-delete", DOMEventNames.ACTIVATE);

        assertShrunkItemset("select-1");
        assertShrunkItemset("select-2");
        assertShrunkItemset("select-3");
        assertShrunkItemset("select-4");
        assertShrunkItemset("select-5");
        assertShrunkItemset("select-6");
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("ItemsetTest.xhtml"));
        this.xformsProcesssorImpl.init();
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

    // helper

    protected void assertInitializedItemset(String selectId) throws XFormsException {
        Document host = this.xformsProcesssorImpl.getContainer().getDocument();

        assertEquals(selectId, "123", XPathUtil.evaluateAsString(host, "//*[@id eq '" + selectId + "']/bf:data"));
        assertEquals(selectId, "1", XPathUtil.evaluateAsString(host, "count(//*[@id eq '" + selectId + "']/xf:itemset/bf:data/xf:item)"));
        if(selectId.equals("select-1") || selectId.equals("select-3") || selectId.equals("select-5")){
            assertEquals(selectId, "3", XPathUtil.evaluateAsString(host, "count(//*[@id eq '" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "true", "First", "123");
            assertItem(host, selectId, 2, "false", "Second", "124");
            assertItem(host, selectId, 3, "false", "Third", "125");
        }
        else {
            assertEquals(selectId, "3", XPathUtil.evaluateAsString(host, "count(//*[@id eq '" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "true", "First", "123");
            assertItem(host, selectId, 2, "false", "Second", "124");
            assertItem(host, selectId, 3, "false", "Third", "125");
        }
    }

    protected void assertUpdatedItemset(String selectId) throws XFormsException {
        Document host = this.xformsProcesssorImpl.getContainer().getDocument();

        assertEquals(selectId, "125", XPathUtil.evaluateAsString(host, "//*[@id eq '" + selectId + "']/bf:data"));
        if(selectId.equals("select-1") || selectId.equals("select-3") || selectId.equals("select-5")){
            assertEquals(selectId, "3", XPathUtil.evaluateAsString(host, "count(//*[@id eq '" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "false", "First", "123");
            assertItem(host, selectId, 2, "false", "Second", "124");
            assertItem(host, selectId, 3, "true", "Third", "125");
        }
        else {
            assertEquals(selectId, "3", XPathUtil.evaluateAsString(host, "count(//*[@id eq '" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "false", "First", "123");
            assertItem(host, selectId, 2, "false", "Second", "124");
            assertItem(host, selectId, 3, "true", "Third", "125");
        }
    }

    protected void assertGrownItemset(String selectId) throws XFormsException {
        Document host = this.xformsProcesssorImpl.getContainer().getDocument();

        assertEquals(selectId, "123", XPathUtil.evaluateAsString(host, "//*[@id eq '" + selectId + "']/bf:data"));
        if(selectId.equals("select-1") || selectId.equals("select-3") || selectId.equals("select-5")){
            assertEquals(selectId, "4", XPathUtil.evaluateAsString(host, "count(//*[@id eq '" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "true", "First", "123");
            assertItem(host, selectId, 2, "false", "Fourth", "126");
            assertItem(host, selectId, 3, "false", "Second", "124");
            assertItem(host, selectId, 4, "false", "Third", "125");

        }else {
            assertEquals(selectId, "4", XPathUtil.evaluateAsString(host, "count(//*[@id eq '" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "true", "First", "123");
            assertItem(host, selectId, 2, "false", "Fourth", "126");
            assertItem(host, selectId, 3, "false", "Second", "124");
            assertItem(host, selectId, 4, "false", "Third", "125");
        }
    }


    protected void assertShrunkItemset(String selectId) throws XFormsException {
        Document host = this.xformsProcesssorImpl.getContainer().getDocument();

        assertEquals(selectId, "123", XPathUtil.evaluateAsString(host, "//*[@id eq '" + selectId + "']/bf:data"));
        if(selectId.equals("select-1") || selectId.equals("select-3") || selectId.equals("select-5")){
            assertEquals(selectId, "2", XPathUtil.evaluateAsString(host, "count(//*[@id eq '" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "true", "First", "123");
            assertItem(host, selectId, 2, "false", "Third", "125");

        }else {
            assertEquals(selectId, "2", XPathUtil.evaluateAsString(host, "count(//*[@id eq '" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "true", "First", "123");
            assertItem(host, selectId, 2, "false", "Third", "125");

        }
    }

    private void assertItem(Document host, String id, int position, String selected, String label, String value) throws XFormsException {
        assertEquals(id, "true", XPathUtil.evaluateAsString(host, "boolean(//*[@id eq '" + id + "']/xf:itemset/xf:item[" + position + "]/@id)"));
        assertEquals(id, selected, XPathUtil.evaluateAsString(host, "//*[@id eq '" + id + "']/xf:itemset/xf:item[" + position + "]/@selected"));
        assertEquals(id, "true", XPathUtil.evaluateAsString(host, "boolean(//*[@id eq '" + id + "']/xf:itemset/xf:item[" + position + "]/xf:label/@id)"));
        assertEquals(id, label, XPathUtil.evaluateAsString(host, "//*[@id eq '" + id + "']/xf:itemset/xf:item[" + position + "]/xf:label"));
        assertEquals(id, "true", XPathUtil.evaluateAsString(host, "boolean(//*[@id eq '" + id + "']/xf:itemset/xf:item[" + position + "]/xf:value/@id)"));
        assertEquals(id, value, XPathUtil.evaluateAsString(host, "//*[@id eq '" + id + "']/xf:itemset/xf:item[" + position + "]/xf:value"));
    }
}

// end of class
