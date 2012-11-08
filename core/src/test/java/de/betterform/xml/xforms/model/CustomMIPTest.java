/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model;

import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import java.util.Map;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;

/**
 * Tests the ui element state.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: UIElementStateTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class CustomMIPTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private TestEventListener customMIPToggledListener;
    private Document host;

    /**
     * Tests custom mip initialization
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInit() throws Exception {
        
    }

    /**
     * Tests custom mip initialization state with a missing instance node.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInitMissing() throws Exception {
    	
    }

    /**
     * Tests custom MIP changed notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDiffNotification() throws Exception {
 
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {

        this.customMIPToggledListener = new TestEventListener();

        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("CustomMIPTest.xhtml"));

        EventTarget eventTarget = (EventTarget) this.xformsProcesssorImpl.getXForms().getDocumentElement();
        register(eventTarget, true);

                
        this.xformsProcesssorImpl.init();

        deregister(eventTarget, true);
        this.host = xformsProcesssorImpl.getXForms();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;

        this.customMIPToggledListener = null;
    }

    private void register(EventTarget eventTarget, boolean bubbles) {
        eventTarget.addEventListener(BetterFormEventNames.CUSTOM_MIP_CHANGED, this.customMIPToggledListener, bubbles);
    }

    private void deregister(EventTarget eventTarget, boolean bubbles) {
        eventTarget.removeEventListener(BetterFormEventNames.CUSTOM_MIP_CHANGED, this.customMIPToggledListener, bubbles);
    }

}

// end of class
