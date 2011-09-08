/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;

/**
 * Test cases for repeated Switches.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RepeatedSwitchTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class RepeatedSwitchTest extends XMLTestBase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document host;

    /**
     * Tests init.
     *
     * @throws Exception if any error occurrerd during the test.
     */
    public void testInit() throws Exception {
        Document host = xformsProcesssorImpl.getXForms();
        assertRepeatCount(host, "repeat", 3);
        assertCaseSelection(host, "repeat", 1, true, false);
        assertCaseSelection(host, "repeat", 2, true, false);
        assertCaseSelection(host, "repeat", 3, true, false);
    }

    /**
     * Tests update.
     *
     * @throws Exception if any error occurrerd during the test.
     */
    public void testUpdate() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-edit", DOMEventNames.ACTIVATE);
        assertCaseSelection(host, "repeat", 1, false, true);
        assertCaseSelection(host, "repeat", 2, true, false);
        assertCaseSelection(host, "repeat", 3, true, false);

        this.xformsProcesssorImpl.dispatch("trigger-view", DOMEventNames.ACTIVATE);
        assertCaseSelection(host, "repeat", 1, true, false);
        assertCaseSelection(host, "repeat", 2, true, false);
        assertCaseSelection(host, "repeat", 3, true, false);

        this.xformsProcesssorImpl.setRepeatIndex("repeat", 2);
        this.xformsProcesssorImpl.dispatch("trigger-edit", DOMEventNames.ACTIVATE);
        assertCaseSelection(host, "repeat", 1, true, false);
        assertCaseSelection(host, "repeat", 2, false, true);
        assertCaseSelection(host, "repeat", 3, true, false);

        this.xformsProcesssorImpl.dispatch("trigger-view", DOMEventNames.ACTIVATE);
        assertCaseSelection(host, "repeat", 1, true, false);
        assertCaseSelection(host, "repeat", 2, true, false);
        assertCaseSelection(host, "repeat", 3, true, false);

        this.xformsProcesssorImpl.setRepeatIndex("repeat", 3);
        this.xformsProcesssorImpl.dispatch("trigger-edit", DOMEventNames.ACTIVATE);
        assertCaseSelection(host, "repeat", 1, true, false);
        assertCaseSelection(host, "repeat", 2, true, false);
        assertCaseSelection(host, "repeat", 3, false, true);

        this.xformsProcesssorImpl.dispatch("trigger-view", DOMEventNames.ACTIVATE);
        assertCaseSelection(host, "repeat", 1, true, false);
        assertCaseSelection(host, "repeat", 2, true, false);
        assertCaseSelection(host, "repeat", 3, true, false);
    }

    /**
     * Tests insert.
     *
     * @throws Exception if any error occurrerd during the test.
     */
    public void testInsert() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-insert", DOMEventNames.ACTIVATE);
        assertRepeatCount(host, "repeat", 4);
        assertCaseSelection(host, "repeat", 1, true, false);
        assertCaseSelection(host, "repeat", 2, true, false);
        assertCaseSelection(host, "repeat", 3, true, false);
        assertCaseSelection(host, "repeat", 4, true, false);
    }

    /**
     * Tests delete.
     *
     * @throws Exception if any error occurrerd during the test.
     */
    public void testDelete() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-delete", DOMEventNames.ACTIVATE);
        assertRepeatCount(host, "repeat", 2);
        assertCaseSelection(host, "repeat", 1, true, false);
        assertCaseSelection(host, "repeat", 2, true, false);
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("RepeatedSwitchTest.xhtml"));
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

    private static void assertRepeatCount(Document host, String id, int count) throws XFormsException {
        assertEquals(count + "", XPathUtil.evaluateAsString(host,"count(//xf:repeat[@id='" + id + "']/xf:group[@appearance='repeated'])"));
    }

    private static void assertCaseSelection(Document host, String id, int position, boolean case1, boolean case2) throws XFormsException {
        assertEquals(String.valueOf(case1), XPathUtil.evaluateAsString(host,"//xf:repeat[@id='" + id + "']/xf:group[@appearance='repeated'][" + position + "]/xf:switch/xf:case[1]/bf:data/@bf:selected"));
        assertEquals(String.valueOf(case2), XPathUtil.evaluateAsString(host,"//xf:repeat[@id='" + id + "']/xf:group[@appearance='repeated'][" + position + "]/xf:switch/xf:case[2]/bf:data/@bf:selected"));
    }

}
