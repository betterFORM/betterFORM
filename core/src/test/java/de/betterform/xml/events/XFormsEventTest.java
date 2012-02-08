/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.events;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.action.EventCountListener;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;


/**

 *
 * @author Joern Turner
 * @author Lars Windauer
 *

 */
public class XFormsEventTest extends BetterFormTestCase {
    private TestEventListener messageListener;
    private EventCountListener invalidCountListener;

    protected String getTestCaseURI() {
        return "EventTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.invalidCountListener = new EventCountListener(XFormsEventNames.INVALID);
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.addEventListener(XFormsEventNames.INVALID, this.invalidCountListener, true);

    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        invalidCountListener = null;
        eventTarget = null;
    }

    /**
     * Tests a modal message.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInvalidListener() throws Exception {
        this.processor.dispatch("t-invalid", DOMEventNames.ACTIVATE);
        XFormsProcessorImpl xformsProcesssorImpl = (XFormsProcessorImpl) processor;
        Document defaultInstance = xformsProcesssorImpl.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument();
        DOMUtil.prettyPrintDOM(defaultInstance);

        assertEquals("must be the same", "invalid", XPathUtil.evaluateAsString(defaultInstance, "/top/events"));
        assertEquals("must be the same", "false", XPathUtil.evaluateAsString(defaultInstance, "/top/constraint"));
        assertEquals("must be the same", "calc1:invalid", XPathUtil.evaluateAsString(defaultInstance, "/top/calculate1"));
        assertEquals("must be the same", "calc2:invalid", XPathUtil.evaluateAsString(defaultInstance, "/top/calculate2"));

        this.processor.dispatch("t-valid", DOMEventNames.ACTIVATE);
        Document defaultInstance2 = xformsProcesssorImpl.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument();
        DOMUtil.prettyPrintDOM(defaultInstance);
        assertEquals("must be the same", "valid", XPathUtil.evaluateAsString(defaultInstance2, "/top/events"));
        assertEquals("must be the same", "true", XPathUtil.evaluateAsString(defaultInstance2, "/top/constraint"));
        assertEquals("must be the same", "calc1:valid", XPathUtil.evaluateAsString(defaultInstance2, "/top/calculate1"));
        assertEquals("must be the same", "calc2:valid", XPathUtil.evaluateAsString(defaultInstance2, "/top/calculate2"));


    }


}
