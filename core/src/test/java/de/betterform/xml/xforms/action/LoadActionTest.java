/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import junit.framework.TestCase;
import org.w3c.dom.events.EventTarget;

/**
 * Test cases for the load action.
 *
 * @author Ulrich Nicolas Liss&eacute; , Nick van den Bleeken, Joern Turner
 * @version $Id: LoadActionTest.java 3460 2008-08-14 09:11:58Z joern $
 */
public class LoadActionTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private EventTarget eventTarget;
    private TestEventListener loadListener;

    /**
     * Tests loading a bound URI.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLoadBind() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-bind", DOMEventNames.ACTIVATE);

        assertLoad("load-bind", "http://tempuri.org/bind.xml", "replace");
    }

    /**
     * Tests loading a bound URI.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLoadBindReplace() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-bind-replace", DOMEventNames.ACTIVATE);

        assertLoad("load-bind-replace", "http://tempuri.org/bind.xml", "replace");
    }

    /**
     * Tests loading a bound URI.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLoadBindNew() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-bind-new", DOMEventNames.ACTIVATE);

        assertLoad("load-bind-new", "http://tempuri.org/bind.xml", "new");
    }

    /**
     * Tests loading a resource URI.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLoadResource() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-resource", DOMEventNames.ACTIVATE);

        assertLoad("load-resource", "http://tempuri.org/resource.xml", "replace");
    }

    /**
     * Tests loading a resource URI.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLoadResourceReplace() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-resource-replace", DOMEventNames.ACTIVATE);

        assertLoad("load-resource-replace", "http://tempuri.org/resource.xml", "replace");
    }

    /**
     * Tests loading a resource URI.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLoadResourceNew() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-resource-new", DOMEventNames.ACTIVATE);

        assertLoad("load-resource-new", "http://tempuri.org/resource.xml", "new");
    }

    /**
     * Tests a resource conflict.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLoadResourceConflict() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-resource-conflict", DOMEventNames.ACTIVATE);

        assertNull(this.loadListener.getId());
        assertNull(this.loadListener.getContext());
        assertNull(this.loadListener.getContext("uri"));
        assertNull(this.loadListener.getContext("show"));
    }

    /**
     * Tests an invalid binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLoadInvalidBinding() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-invalid-binding", DOMEventNames.ACTIVATE);

        assertNull(this.loadListener.getId());
        assertNull(this.loadListener.getContext());
        assertNull(this.loadListener.getContext("uri"));
        assertNull(this.loadListener.getContext("show"));
    }
    
    /**
     * Tests loading a dynamic resource value URI.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLoadResourceValue() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-resource-value", DOMEventNames.ACTIVATE);

        assertLoad("load-resource-value", "http://tempuri.org/base/bind.xml", "replace");
    }

    /**
     * Tests loading a dynamic resource value URI.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLoadResourceValueReplace() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-resource-value-replace", DOMEventNames.ACTIVATE);

        assertLoad("load-resource-value-replace", "http://tempuri.org/base/bind.xml", "replace");
    }

    /**
     * Tests loading a dynamic resource value URI.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLoadResourceValueNew() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-resource-value-new", DOMEventNames.ACTIVATE);

        assertLoad("load-resource-value-new", "http://tempuri.org/base/bind.xml", "new");
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("LoadActionTest.xhtml"));
        this.xformsProcesssorImpl.init();

        this.loadListener = new TestEventListener();

        this.eventTarget = (EventTarget) this.xformsProcesssorImpl.getXForms().getDocumentElement();
        this.eventTarget.addEventListener(BetterFormEventNames.LOAD_URI, this.loadListener, true);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.eventTarget.removeEventListener(BetterFormEventNames.LOAD_URI, this.loadListener, true);
        this.eventTarget = null;

        this.loadListener = null;

        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

    private void assertLoad(String target, String uri, String show) {
        if (target == null) {
            assertNull(this.loadListener.getId());
            assertNull(this.loadListener.getContext());
            assertNull(this.loadListener.getPropertyNames());
        }
        else {
            assertEquals(target, this.loadListener.getId());

            //assertNull(this.loadListener.getContext());
            assertEquals(uri, this.loadListener.getContext("uri"));
            assertEquals(show, this.loadListener.getContext("show"));
            assertEquals(null, this.loadListener.getContext("target"));

            assertNotNull(this.loadListener.getPropertyNames());
            assertEquals(2, this.loadListener.getPropertyNames().size());
        }
    }

}

// end of class
