/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model;


import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xforms.exception.XFormsLinkException;
import org.w3c.dom.events.EventTarget;

/**
 * Test cases for the instance implementation.
 *
 * @author Tobi Krebs
 */
public class InvalidInstanceNoXMLSourceTest extends XMLTestBase {
    private XFormsProcessorImpl xformsProcesssorImpl;
    private TestEventListener LinkListener;

    /**
     * Tests instance initialization.
     *
     * @throws Exception if any error occurred during the test.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("InvalidInstanceNoXMLSourceTest.xhtml"));
        String path = getClass().getResource("InvalidInstanceNoXMLSourceTest.xhtml").getPath();
        String baseURI = "file://" + path.substring(0, path.lastIndexOf("InvalidInstanceNoXMLSourceTest.xhtml"));
        this.xformsProcesssorImpl.setBaseURI(baseURI);

        this.LinkListener = new TestEventListener();
        EventTarget eventTarget = (EventTarget) this.xformsProcesssorImpl.getXForms();
        eventTarget.addEventListener(XFormsEventNames.LINK_EXCEPTION, this.LinkListener, true);
    }

    protected void tearDown() throws Exception {
        EventTarget eventTarget = (EventTarget) this.xformsProcesssorImpl.getXForms();
        eventTarget.removeEventListener(XFormsEventNames.INSERT, this.LinkListener, true);
        this.LinkListener=null;
        this.xformsProcesssorImpl.shutdown();
        super.tearDown();
    }

    protected String getTestCaseURI() {
        return "InvalidInstanceNoXMLSourceTest.xhtml";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void testInvalidInstanceNoXMLSource() throws Exception {
        try{
            this.xformsProcesssorImpl.init();
            fail();
        }catch (XFormsLinkException e){
            assertNotNull(e);
            assertEquals("noXML.txt", this.LinkListener.getContext("resource-uri"));
            assertEquals("Content is not allowed in prolog.", this.LinkListener.getContext("detailMessage"));
        }


    }
}

// end of class
