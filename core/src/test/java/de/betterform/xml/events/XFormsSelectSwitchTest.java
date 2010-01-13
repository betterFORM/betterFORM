/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.xml.events;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.action.EventCountListener;
import de.betterform.xml.xforms.ui.Selector;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
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
public class XFormsSelectSwitchTest extends BetterFormTestCase {
     private EventCountListener setvalueCountListener;
     private EventCountListener messageCountListener;
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    public void testComplexEventDispatching() throws Exception{
        Document xformDoc = (Document) this.processor.getXForms();
        Document defaultInstance = this.processor.getXFormsModel("m-1").getInstanceDocument("i-1");

        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select1"));
        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select2"));
        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select3"));
        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select4"));

        this.processor.dispatch("t-setvalue-to-two", DOMEventNames.ACTIVATE);
        // DOMUtil.prettyPrintDOM(this.processor.getXForms());
        defaultInstance = this.processor.getXFormsModel("m-1").getInstanceDocument("i-1");
        // DOMUtil.prettyPrintDOM(defaultInstance);

        assertEquals("", "2", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select1"));
        assertEquals("", "2", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select2"));
        assertEquals("", "2", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select3"));
        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select4"));


        assertEquals(3,this.setvalueCountListener.getCount());
        assertEquals(0,this.messageCountListener.getCount());
        this.processor.dispatch("t-case2", DOMEventNames.ACTIVATE);

        defaultInstance = this.processor.getXFormsModel("m-1").getInstanceDocument("i-1");
        assertEquals("", "2", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select1"));
        assertEquals("", "2", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select2"));
        assertEquals("", "2", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select3"));
        assertEquals("", "2", XPathUtil.evaluateAsString((Document)this.processor.getXForms(), "//*[@id='select-4']/bf:data"));

        assertEquals(3,this.setvalueCountListener.getCount());
        assertEquals(1,this.messageCountListener.getCount());


        this.processor.dispatch("t-setvalue-to-three", DOMEventNames.ACTIVATE);
        defaultInstance = this.processor.getXFormsModel("m-1").getInstanceDocument("i-1");
        assertEquals("", "3", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select1"));
        assertEquals("", "3", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select2"));
        assertEquals("", "2", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select3"));

        assertEquals("", "3", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select4"));
        assertEquals(6,this.setvalueCountListener.getCount());
        assertEquals(1,this.messageCountListener.getCount());

        XFormsProcessorImpl xformsProcesssorImpl = (XFormsProcessorImpl) this.processor;
        Selector selector = (Selector) xformsProcesssorImpl.getContainer().lookup("select-4");
        assertEquals("3", selector.getValue());
        selector.setValue("1");

        defaultInstance = this.processor.getXFormsModel("m-1").getInstanceDocument("i-1");
        // DOMUtil.prettyPrintDOM(defaultInstance);
        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select1"));
        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select2"));
        assertEquals("", "2", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select3"));
        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select4"));

        assertEquals(9,this.setvalueCountListener.getCount());

        assertEquals(1,this.messageCountListener.getCount());
/*
        assertEquals(7,this.setvalueCountListener.getCount());
        assertEquals(1,this.messageCountListener.getCount());
*/
    }

    public void testSelectCaseInteraction() throws Exception{
        XFormsProcessorImpl xformsProcesssorImpl = (XFormsProcessorImpl) this.processor;
        Document defaultInstance = xformsProcesssorImpl.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument();

        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select1"));
        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select2"));
        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select3"));
        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select4"));



        Selector selector = (Selector) xformsProcesssorImpl.getContainer().lookup("select-3");
        assertEquals("1", selector.getValue());
        selector.setValue("2");

        defaultInstance = xformsProcesssorImpl.getXFormsModel("m-1").getInstanceDocument("i-1");
        // DOMUtil.prettyPrintDOM(defaultInstance);
        assertEquals("", "2", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select1"));
        assertEquals("", "2", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select2"));
        assertEquals("", "2", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select3"));
        assertEquals("", "1", XPathUtil.evaluateAsString(defaultInstance, "/data/result/select4"));

        assertEquals(3,this.setvalueCountListener.getCount());
        assertEquals(0,this.messageCountListener.getCount());

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
        return "XFormsSwitchComplexTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
