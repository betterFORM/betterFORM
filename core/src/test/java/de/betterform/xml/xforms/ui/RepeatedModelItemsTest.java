/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.ui;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Element;

/**
 * Test cases for repeated model items.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RepeatedModelItemsTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class RepeatedModelItemsTest extends XMLTestBase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Element element;

    /**
     * Tests init.
     *
     * @throws Exception if any error occurrerd during the test.
     */
    public void testInit() throws Exception {
        assertItemData(1, true, false, false, false);
        assertItemInputData(1, 1, true, false, false, true, "xs:integer", "1");
        assertItemInputData(1, 2, true, false, false, false, "xs:string", "one");
        assertItemInputData(1, 3, true, false, false, true, "xs:token", "one");

        assertItemData(2, false, true, false, false);
        assertItemInputData(2, 1, false, true, false, true, "xs:integer", "2");
        assertItemInputData(2, 2, false, true, false, false, "xs:string", "two");
        assertItemInputData(2, 3, false, true, false, true, "xs:token", "two");

        assertItemData(3, false, false, true, false);
        assertItemInputData(3, 1, false, false, false, true, "xs:integer", "3");
        assertItemInputData(3, 2, false, false, true, false, "xs:string", "three");
        assertItemInputData(3, 3, false, false, false, true, "xs:token", "three");

        assertItemData(4, false, false, false, true);
        assertItemInputData(4, 1, false, false, false, true, "xs:integer", "4");
        assertItemInputData(4, 2, false, false, false, true, "xs:string", "four");
        assertItemInputData(4, 3, false, false, false, true, "xs:token", "four");
    }

    /**
     * Tests insert.
     *
     * @throws Exception if any error occurrerd during the test.
     */
    public void testInsert() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-insert", DOMEventNames.ACTIVATE);

        assertItemData(1, true, false, false, false);
        assertItemInputData(1, 1, true, false, false, true, "xs:integer", "1");
        assertItemInputData(1, 2, true, false, false, false, "xs:string", "one");
        assertItemInputData(1, 3, true, false, false, true, "xs:token", "one");

        assertItemData(2, false, true, false, false);
        assertItemInputData(2, 1, false, true, false, true, "xs:integer", "2");
        assertItemInputData(2, 2, false, true, false, false, "xs:string", "two");
        assertItemInputData(2, 3, false, true, false, true, "xs:token", "two");

        assertItemData(3, false, false, true, false);
        assertItemInputData(3, 1, false, false, false, true, "xs:integer", "3");
        assertItemInputData(3, 2, false, false, true, false, "xs:string", "three");
        assertItemInputData(3, 3, false, false, false, true, "xs:token", "three");

        assertItemData(4, false, false, false, true);
        assertItemInputData(4, 1, false, false, false, true, "xs:integer", "4");
        assertItemInputData(4, 2, false, false, false, true, "xs:string", "four");
        assertItemInputData(4, 3, false, false, false, true, "xs:token", "four");

        assertItemData(5, false, false, false, true);
        assertItemInputData(5, 1, false, false, false, true, "xs:integer", "4");
        assertItemInputData(5, 2, false, false, false, true, "xs:string", "four");
        assertItemInputData(5, 3, false, false, false, true, "xs:token", "four");
    }

    /**
     * Tests update.
     *
     * @throws Exception if any error occurrerd during the test.
     */
    public void testUpdate() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-update", DOMEventNames.ACTIVATE);

        assertItemData(1, true, false, false, false);
        assertItemInputData(1, 1, true, false, false, true, "xs:integer", "1");
        assertItemInputData(1, 2, true, false, false, false, "xs:string", "one");
        assertItemInputData(1, 3, true, false, false, true, "xs:token", "one");

        assertItemData(2, false, true, false, false);
        assertItemInputData(2, 1, false, true, false, true, "xs:integer", "2");
        assertItemInputData(2, 2, false, true, false, false, "xs:string", "two");
        assertItemInputData(2, 3, false, true, false, true, "xs:token", "two");

        assertItemData(3, false, false, true, false);
        assertItemInputData(3, 1, false, false, false, true, "xs:integer", "3");
        assertItemInputData(3, 2, false, false, true, false, "xs:string", "three");
        assertItemInputData(3, 3, false, false, false, true, "xs:token", "three");

        assertItemData(4, false, false, false, true);
        assertItemInputData(4, 1, false, false, false, true, "xs:integer", "4");
        assertItemInputData(4, 2, false, false, false, true, "xs:string", "four");
        assertItemInputData(4, 3, false, false, false, true, "xs:token", "four");

        dump(this.xformsProcesssorImpl.getXForms());
        assertItemData(5, false, false, false, false);
        assertItemInputData(5, 1, false, false, false, true, "xs:integer", "5");
        assertItemInputData(5, 2, false, false, false, false, "xs:string", "four");
        assertItemInputData(5, 3, false, false, false, true, "xs:token", "four");
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("RepeatedModelItemsTest.xhtml"));
        this.xformsProcesssorImpl.init();

        this.element = this.xformsProcesssorImpl.getContainer().lookup("repeat-1").getElement();
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

    private void assertItemData(int position, boolean enabled, boolean readonly, boolean required, boolean valid) throws XFormsException {
        assertEquals(String.valueOf(enabled), XPathUtil.evaluateAsString(this.element,"xf:group[" + position + "]/bf:data/@enabled"));
        assertEquals(String.valueOf(readonly), XPathUtil.evaluateAsString(this.element,"xf:group[" + position + "]/bf:data/@readonly"));
        assertEquals(String.valueOf(required), XPathUtil.evaluateAsString(this.element,"xf:group[" + position + "]/bf:data/@required"));
        assertEquals(String.valueOf(valid), XPathUtil.evaluateAsString(this.element,"xf:group[" + position + "]/bf:data/@valid"));
    }

    private void assertItemInputData(int item, int position, boolean enabled, boolean readonly, boolean required, boolean valid, String type, String value) throws XFormsException {
        assertEquals(String.valueOf(enabled), XPathUtil.evaluateAsString(this.element,"xf:group[" + item + "]/xf:input[" + position + "]/bf:data/@enabled"));
        assertEquals(String.valueOf(readonly), XPathUtil.evaluateAsString(this.element,"xf:group[" + item + "]/xf:input[" + position + "]/bf:data/@readonly"));
        assertEquals(String.valueOf(required), XPathUtil.evaluateAsString(this.element,"xf:group[" + item + "]/xf:input[" + position + "]/bf:data/@required"));
        assertEquals(String.valueOf(valid), XPathUtil.evaluateAsString(this.element,"xf:group[" + item + "]/xf:input[" + position + "]/bf:data/@valid"));
        assertEquals(type, XPathUtil.evaluateAsString(this.element,"xf:group[" + item + "]/xf:input[" + position + "]/bf:data/@type"));
        assertEquals(value, XPathUtil.evaluateAsString(this.element,"xf:group[" + item + "]/xf:input[" + position + "]/bf:data"));
    }
}

// end of class
