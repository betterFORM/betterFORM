/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.action.EventCountListener;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;

public class RepeatSetIndexEventTest extends BetterFormTestCase {

    static {
        org.apache.log4j.BasicConfigurator.configure();
    }


    private EventCountListener scrollFirstListener;
    private EventCountListener scrollLastListener;
    private EventTarget eventTarget;
    private Repeat repeat;



    protected void setUp() throws Exception {
        super.setUp();

        this.scrollFirstListener = new EventCountListener(XFormsEventNames.SCROLL_FIRST);
        this.scrollLastListener = new EventCountListener(XFormsEventNames.SCROLL_LAST);

        this.eventTarget = (EventTarget) this.processor.getXForms();
        this.eventTarget.addEventListener(XFormsEventNames.SCROLL_FIRST, this.scrollFirstListener, true);
        this.eventTarget.addEventListener(XFormsEventNames.SCROLL_LAST, this.scrollLastListener, true);

        this.repeat = (Repeat) this.processor.lookup("lineset");

    }

    protected void tearDown() {
        this.scrollFirstListener = null;
        this.scrollLastListener = null;

        this.eventTarget = null;
        this.repeat = null;

    }


    public void testSetRepeatIndexBiggerThenRepeatItems() throws Exception {
        Document hostDoc = (Document) this.processor.getXForms();
        DOMUtil.prettyPrintDOM(hostDoc);
        String value = XPathUtil.evaluateAsString(hostDoc, "//*[@id='indexOutput']/bf:data");
        assertEquals("Repeat Index must be '1'", "1", value);
        assertEquals(1, this.repeat.getIndex());

        this.processor.dispatch("setIndexOneHundred", "DOMActivate");
        value = XPathUtil.evaluateAsString(hostDoc, "//*[@id='indexOutput']/bf:data");
        assertEquals("Repeat Index must be '3'", "3", value);
        assertEquals(3, this.repeat.getIndex());
        assertEquals("scroll first must not be fired", 0, this.scrollFirstListener.getCount());
        assertEquals("scroll last must be fired only once", 1, this.scrollLastListener.getCount());
    }

    public void testSetRepeatIndexToNegativeNumber() throws Exception {
        Document hostDoc = (Document) this.processor.getXForms();
        DOMUtil.prettyPrintDOM(hostDoc);
        String value = XPathUtil.evaluateAsString(hostDoc, "//*[@id='indexOutput']/bf:data");
        assertEquals("Repeat Index must be '1'", "1", value);
        assertEquals(1, this.repeat.getIndex());

        this.processor.dispatch("setIndexNegative", "DOMActivate");
        value = XPathUtil.evaluateAsString(hostDoc, "//*[@id='indexOutput']/bf:data");
        assertEquals("Repeat Index must be '1'", "1", value);
        assertEquals(1, this.repeat.getIndex());
        assertEquals("scroll last must not be fired", 0, this.scrollLastListener.getCount());
        assertEquals("scroll first must be fired only once", 1, this.scrollFirstListener.getCount());
    }

    public void testSetRepeatIndexToTwo() throws Exception {
        Document hostDoc = (Document) this.processor.getXForms();
        DOMUtil.prettyPrintDOM(hostDoc);
        String value = XPathUtil.evaluateAsString(hostDoc, "//*[@id='indexOutput']/bf:data");
        assertEquals("Repeat Index must be '1'", "1", value);
        assertEquals(1, this.repeat.getIndex());


        this.processor.dispatch("setIndexTwo", "DOMActivate");
        value = XPathUtil.evaluateAsString(hostDoc, "//*[@id='indexOutput']/bf:data");
        assertEquals("Repeat Index must be '2'", "2", value);
        assertEquals(2, this.repeat.getIndex());
        assertEquals("scroll-last event must not be fired", 0, this.scrollLastListener.getCount());
        assertEquals("scroll-first event must not be fired", 0, this.scrollFirstListener.getCount());
    }


    protected String getTestCaseURI() {
        return "10.5.a.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }
}
