/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;
import org.w3c.xforms.XFormsModelElement;

/**
 * Test cases for xf:load action with show=embedded including cross model submissions
 *
 * @author Joern Turner
 * @author Lars Windauer
 * @version $Id: 
 */
public class LoadActionEmbedTest extends BetterFormTestCase {
     protected EventCountListener messageCountListener;
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }


    public void testLoadEmbedForms() throws Exception{
        Document xformDoc = (Document) this.processor.getXForms();
        assertEquals("", "bind.xml", XPathUtil.evaluateAsString(xformDoc, "//xf:input[1]/bf:data"));
        this.processor.dispatch("trigger-load-embed-1", DOMEventNames.ACTIVATE);


        XFormsModelElement childModel = this.processor.getXFormsModel("m-child-1");
        assertNotNull(childModel);
        Document childInstance = childModel.getInstanceDocument("i-child-1");
        assertNotNull(childInstance);

        xformDoc = (Document) this.processor.getXForms();
        // DOMUtil.prettyPrintDOM(xformDoc);
        assertEquals("", "false", XPathUtil.evaluateAsString(xformDoc, "//xf:input[1]/bf:data"));
        assertEquals("", "boolean", XPathUtil.evaluateAsString(xformDoc, "//xf:input[1]/bf:data/@bf:type"));
        assertEquals("", "2009-10-01", XPathUtil.evaluateAsString(xformDoc, "//xf:input[2]/bf:data/@bf:schema-value"));
        assertEquals("", "date", XPathUtil.evaluateAsString(xformDoc, "//xf:input[2]/bf:data/@bf:type"));
        assertEquals("", "2009-10-01", XPathUtil.evaluateAsString(xformDoc, "//xf:input[3]/bf:data/@bf:schema-value"));
        assertEquals("", "date", XPathUtil.evaluateAsString(xformDoc, "//xf:input[3]/bf:data/@bf:type"));
        assertEquals("", "4", XPathUtil.evaluateAsString(xformDoc, "//xf:input[4]/bf:data"));
        assertEquals("", "5", XPathUtil.evaluateAsString(xformDoc, "//xf:input[5]/bf:data"));


        // unlaod embedded form and replace it by different one
        this.processor.dispatch("trigger-load-embed-2", DOMEventNames.ACTIVATE);

        childModel = this.processor.getXFormsModel("m-child-2");
        assertNotNull(childModel);

        childInstance = childModel.getInstanceDocument("i-child-2");
        assertNotNull(childInstance);

        xformDoc = (Document) this.processor.getXForms();
        assertEquals("", "4", XPathUtil.evaluateAsString(xformDoc, "//xf:input[1]/bf:data"));
        assertEquals("", "5", XPathUtil.evaluateAsString(xformDoc, "//xf:input[2]/bf:data"));
        assertEquals("", "true", XPathUtil.evaluateAsString(xformDoc, "//xf:input[3]/bf:data"));
        assertEquals("", "boolean", XPathUtil.evaluateAsString(xformDoc, "//xf:input[3]/bf:data/@bf:type"));
        assertEquals("", "2003-10-01", XPathUtil.evaluateAsString(xformDoc, "//xf:input[4]/bf:data/@bf:schema-value"));
        assertEquals("", "date", XPathUtil.evaluateAsString(xformDoc, "//xf:input[4]/bf:data/@bf:type"));
        assertEquals("", "2009-10-01", XPathUtil.evaluateAsString(xformDoc, "//xf:input[5]/bf:data/@bf:schema-value"));
        assertEquals("", "date", XPathUtil.evaluateAsString(xformDoc, "//xf:input[5]/bf:data/@bf:type"));
    }

    public void testEventExecutionEmbedForm() throws Exception{
        Document xformDoc = (Document) this.processor.getXForms();
        assertEquals("", "bind.xml", XPathUtil.evaluateAsString(xformDoc, "//xf:input[1]/bf:data"));
        this.processor.dispatch("trigger-load-embed-1", DOMEventNames.ACTIVATE);
        this.processor.dispatch("t-message",DOMEventNames.ACTIVATE);
        assertEquals("xf:message was dispatched only once!", 1, this.messageCountListener.getCount());
    }

    public void testCrossModelSubmissionEmbeddedForm() throws Exception{
        Document xformDoc = (Document) this.processor.getXForms();
        assertEquals("", "bind.xml", XPathUtil.evaluateAsString(xformDoc, "//xf:input[1]/bf:data"));
        this.processor.dispatch("trigger-load-embed-3", DOMEventNames.ACTIVATE);

        Model childModel = (Model) this.processor.getXFormsModel("m-child-1");
        assertNotNull(childModel);

        Document childInstance = childModel.getInstanceDocument("i-child-1");
        assertNotNull(childInstance);      
        assertEquals("", "item 1", XPathUtil.evaluateAsString(childInstance, "/data/items/item[1]"));
        assertEquals("", "item 2", XPathUtil.evaluateAsString(childInstance, "/data/items/item[2]"));
        assertEquals("", "item 3", XPathUtil.evaluateAsString(childInstance, "/data/items/item[3]"));


    }
    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.messageCountListener = new EventCountListener(BetterFormEventNames.RENDER_MESSAGE);
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.addEventListener(BetterFormEventNames.RENDER_MESSAGE, this.messageCountListener, true);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.removeEventListener(BetterFormEventNames.RENDER_MESSAGE, this.messageCountListener, true);
        this.messageCountListener = null;
        super.tearDown();
    }

    protected String getTestCaseURI() {
        return "LoadActionEmbedTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
