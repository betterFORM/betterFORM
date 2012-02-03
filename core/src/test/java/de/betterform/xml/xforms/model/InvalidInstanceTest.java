/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model;


import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xforms.exception.XFormsLinkException;
import org.w3c.dom.events.EventTarget;

import java.util.HashMap;

/**
 * Test cases for the instance implementation.
 *
 * @author Tobi Krebs
 */
public class InvalidInstanceTest extends XMLTestBase {
    private XFormsProcessorImpl xformsProcesssorImpl;
    private TestEventListener LinkListener;

    /**
     * Tests instance initialization.
     *
     * @throws Exception if any error occurred during the test.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("InvalidInstanceTest.xhtml"));
        this.LinkListener = new TestEventListener();
        EventTarget eventTarget = (EventTarget) this.xformsProcesssorImpl.getXForms();
        eventTarget.addEventListener(XFormsEventNames.LINK_EXCEPTION, this.LinkListener, true);

    }

    protected void tearDown() throws Exception {
        EventTarget eventTarget = (EventTarget) this.xformsProcesssorImpl.getXForms();
        eventTarget.removeEventListener(XFormsEventNames.INSERT, this.LinkListener, true);
        this.LinkListener=null;
        super.tearDown();
    }

    protected String getTestCaseURI() {
        return "InvalidInstanceTest.xhtml";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void testInvalidInstance() throws Exception {
        Exception exception = null;
        try{
            this.xformsProcesssorImpl.init();
        }catch (XFormsLinkException e){
            assertNotNull(e);
            HashMap map = (HashMap) e.getContextInfo();
            assertEquals(map.get("resource-uri"),"#instance");
            assertEquals(map.get("resource-error"),"multiple root elements found in instance");
        }
//        assertNotNull(this.LinkListener.getId());
//        assertEquals("#instance",this.LinkListener.getContext("resource-uri"));
        //this assert a non-standard property 'resource-error' that is not defined by spec but gives additional information
//        assertEquals("multiple root elements found in instance",this.LinkListener.getContext("resource-error"));
    }
}

// end of class
