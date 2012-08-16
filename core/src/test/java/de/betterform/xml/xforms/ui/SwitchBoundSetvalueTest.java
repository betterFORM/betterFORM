/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.ui;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
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
public class SwitchBoundSetvalueTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document host;

    private TestEventListener selectListener;
    private TestEventListener deselectListener1;
    private TestEventListener deselectListener2;
    private TestEventListener switchListener;
    private EventTarget eventTarget1;
    private EventTarget eventTarget2;
    private EventTarget eventTarget3;
    private EventTarget eventTarget;


    /**
     * Tests initializing a switch without a binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSwitch() throws Exception {
        DOMUtil.prettyPrintDOM(this.xformsProcesssorImpl.getContainer().getDocument());
        assertEquals("3", XPathUtil.evaluateAsString(host, "count(//xf:switch[@id='switch']/xf:case)"));

        assertEquals("xf:case", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch']/*[1])"));
        assertEquals("xf:case", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch']/*[2])"));
        assertEquals("xf:case", XPathUtil.evaluateAsString(host, "name(//xf:switch[@id='switch']/*[3])"));
    }

    /**
     * test if value change triggers the right case (case 2) if the bound node is changed by setvalue
     * @throws Exception
     */
    public void testSwitchSetValue() throws Exception {

        this.xformsProcesssorImpl.dispatch("triggerCase2", DOMEventNames.ACTIVATE);
        DOMUtil.prettyPrintDOM(xformsProcesssorImpl.getXForms());
        //assert the state of the bf:data/@bf:selected attributes - second one must be selected
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch']/xf:case[@id='creditCard']/bf:data/@bf:selected"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch']/xf:case[@id='cashCard']/bf:data/@bf:selected"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//xf:switch[@id='switch']/xf:case[@id='COD']/bf:data/@bf:selected"));

        assertEquals("creditCard", this.deselectListener1.getId());
        assertEquals("cashCard", this.selectListener.getId());
        assertEquals("COD", this.deselectListener2.getId());


    }


    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("SwitchBoundSetvalueTest.xhtml"));
        this.xformsProcesssorImpl.init();
        this.host = xformsProcesssorImpl.getXForms();

        this.selectListener = new TestEventListener();
        this.deselectListener1 = new TestEventListener();
        this.deselectListener2 = new TestEventListener();
        this.switchListener = new TestEventListener();


        this.eventTarget1 = (EventTarget) this.xformsProcesssorImpl.getContainer().lookup("creditCard").getElement();
        this.eventTarget2 = (EventTarget) this.xformsProcesssorImpl.getContainer().lookup("cashCard").getElement();
        this.eventTarget3 = (EventTarget) this.xformsProcesssorImpl.getContainer().lookup("COD").getElement();

        this.eventTarget1.addEventListener(XFormsEventNames.DESELECT, deselectListener1, false);
        this.eventTarget2.addEventListener(XFormsEventNames.SELECT, this.selectListener, false);
        this.eventTarget3.addEventListener(XFormsEventNames.DESELECT, this.deselectListener2, false);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.eventTarget1.removeEventListener(XFormsEventNames.DESELECT, deselectListener1, false);
        this.eventTarget2.removeEventListener(XFormsEventNames.SELECT, this.selectListener, false);
        this.eventTarget3.removeEventListener(XFormsEventNames.DESELECT, this.deselectListener2, false);
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

}

// end of class
