/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.xml.events;

import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.ui.Selector;
import de.betterform.xml.xforms.action.EventCountListener;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;

/**
 * Test cases for xf:load action with show=embedded including cross model submissions
 *
 * @author Joern Turner
 * @author Lars Windauer
 * @version $Id:
 */
public class XFormsSwitchTest extends BetterFormTestCase {
     private EventCountListener setvalueCountListener;
     private EventCountListener messageCountListener;
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }


    public void testSelectCaseInteraction() throws Exception{
        XFormsProcessorImpl xformsProcesssorImpl = (XFormsProcessorImpl) this.processor;
        Document defaultInstance = xformsProcesssorImpl.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument();

        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select1"));
        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/case1"));

        Selector selector = (Selector) xformsProcesssorImpl.getContainer().lookup("select-1");
        assertEquals("1", selector.getValue());
        selector.setValue("2");

        defaultInstance = xformsProcesssorImpl.getXFormsModel("m-1").getInstanceDocument("i-1");
        // DOMUtil.prettyPrintDOM(defaultInstance);

        assertEquals("", "2", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select1"));
        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/case1"));

        assertEquals(1,this.setvalueCountListener.getCount());
        // assertEquals(0,this.messageCountListener.getCount());

    }
    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.setvalueCountListener = new EventCountListener(XFormsEventNames.VALUE_CHANGED);
        this.messageCountListener = new EventCountListener(BetterFormEventNames.RENDER_MESSAGE);
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.addEventListener(XFormsEventNames.VALUE_CHANGED, this.setvalueCountListener, true);
        eventTarget.addEventListener(BetterFormEventNames.RENDER_MESSAGE, this.messageCountListener, true);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();

        eventTarget.removeEventListener(XFormsEventNames.VALUE_CHANGED, this.setvalueCountListener, true);
        eventTarget.removeEventListener(BetterFormEventNames.RENDER_MESSAGE, this.messageCountListener, true);
        this.setvalueCountListener = null;
        this.messageCountListener = null;
        super.tearDown();
    }

    protected String getTestCaseURI() {
        return "XFormsSwitchTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
