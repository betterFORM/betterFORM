/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.xml.xforms.action;

import junit.framework.TestCase;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.TestEventListener;
import org.w3c.dom.events.EventTarget;

/**
 * Test cases for the toggle action.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ToggleActionTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class ToggleActionTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private EventTarget eventTarget;
    private TestEventListener selectListener;
    private TestEventListener deselectListener;
    private TestEventListener switchListener;

    /**
     * Tests toggling a case.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testToggle() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger", DOMEventNames.ACTIVATE);

        assertEquals("case-2", this.selectListener.getId());
        assertEquals("case-1", this.deselectListener.getId());

//        assertNull(this.switchListener.getContext());
        assertEquals("case-2", this.switchListener.getContext("selected"));
        assertEquals("case-1", this.switchListener.getContext("deselected"));

        assertNotNull(this.switchListener.getPropertyNames());
        assertEquals(2, this.switchListener.getPropertyNames().size());
    }

    /**
     * Tests toggling an unknown case.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testToggleWrong() throws Exception {
        try {
            this.xformsProcesssorImpl.dispatch("trigger-wrong", DOMEventNames.ACTIVATE);
            fail();
        }
        catch (Exception e) {
            assertTrue(e instanceof XFormsBindingException);
        }
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("ToggleActionTest.xhtml"));
        this.xformsProcesssorImpl.init();

        this.selectListener = new TestEventListener();
        this.deselectListener = new TestEventListener();
        this.switchListener = new TestEventListener();

        this.eventTarget = (EventTarget) this.xformsProcesssorImpl.getXForms().getDocumentElement();
        this.eventTarget.addEventListener(XFormsEventNames.SELECT, this.selectListener, true);
        this.eventTarget.addEventListener(XFormsEventNames.DESELECT, this.deselectListener, true);
        this.eventTarget.addEventListener(BetterFormEventNames.SWITCH_TOGGLED, this.switchListener, true);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.eventTarget.removeEventListener(XFormsEventNames.SELECT, this.selectListener, true);
        this.eventTarget.removeEventListener(XFormsEventNames.DESELECT, this.deselectListener, true);
        this.eventTarget.removeEventListener(BetterFormEventNames.SWITCH_TOGGLED, this.switchListener, true);
        this.eventTarget = null;

        this.selectListener = null;
        this.deselectListener = null;
        this.switchListener = null;

        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

}

// end of class
