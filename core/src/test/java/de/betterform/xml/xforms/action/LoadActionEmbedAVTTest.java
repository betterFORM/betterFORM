/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
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
public class LoadActionEmbedAVTTest extends BetterFormTestCase {
     private EventCountListener messageCountListener;
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }


    public void testLoadEmbedAVTForms() throws Exception{
        Document xformDoc = (Document) this.processor.getXForms();
        this.processor.dispatch("trigger-load-embed-4", DOMEventNames.ACTIVATE);

        XFormsModelElement childModel = this.processor.getXFormsModel("m-child-2");
        assertNotNull(childModel);
        Document childInstance = childModel.getInstanceDocument("i-child-2");
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
        return "LoadActionEmbedAVTTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
