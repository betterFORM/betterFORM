/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.xml.xforms.action;

import junit.framework.TestCase;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.TestEventListener;
import org.w3c.dom.events.EventTarget;

/**
 * Test cases for the message action.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: MessageActionTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class MessageActionTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private EventTarget eventTarget;
    private TestEventListener messageListener;

    /**
     * Tests a blank message.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testMessageBlank() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-blank", DOMEventNames.ACTIVATE);

        assertMessage("message-blank", "", "modal");
    }

    /**
     * Tests a bound message.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testMessageBound() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-bound", DOMEventNames.ACTIVATE);

        assertMessage("message-bound", "bound", "modal");
    }

    /**
     * Tests a linked message.
     *
     * @throws Exception if any error occurred during the test.
     */
//    public void testMessageLinked() throws Exception {
//        this.processor.dispatch("trigger-linked", DOMEventNames.ACTIVATE);
//
//        assertMessage("message-linked", "linked", "modal");
//    }

    /**
     * Tests a inline message.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testMessageInline() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-inline", DOMEventNames.ACTIVATE);

        assertMessage("message-inline", "inline", "modal");
    }

    /**
     * Tests a ephemeral message.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testMessageEphemeral() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-ephemeral", DOMEventNames.ACTIVATE);

        assertMessage("message-ephemeral", "inline", "ephemeral");
    }

    /**
     * Tests a modeless message.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testMessageModeless() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-modeless", DOMEventNames.ACTIVATE);

        assertMessage("message-modeless", "inline", "modeless");
    }

    /**
     * Tests a modal message.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testMessageModal() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-modal", DOMEventNames.ACTIVATE);

        assertMessage("message-modal", "inline", "modal");
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        String baseURI = "file://" + getClass().getResource("MessageActionTest.xhtml").getPath();
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("MessageActionTest.xhtml"));
        this.xformsProcesssorImpl.setBaseURI(baseURI);
        this.xformsProcesssorImpl.init();

        this.messageListener = new TestEventListener();

        this.eventTarget = (EventTarget) this.xformsProcesssorImpl.getXForms().getDocumentElement();
        this.eventTarget.addEventListener(BetterFormEventNames.RENDER_MESSAGE, this.messageListener, true);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.eventTarget.removeEventListener(BetterFormEventNames.RENDER_MESSAGE, this.messageListener, true);
        this.eventTarget = null;

        this.messageListener = null;

        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

    private void assertMessage(String target, String message, String level) {
        if (target == null) {
            assertNull(this.messageListener.getId());
            assertNull(this.messageListener.getContext());
            assertNull(this.messageListener.getPropertyNames());
        }
        else {
            assertEquals(target, this.messageListener.getId());

//            assertNull(this.messageListener.getContext());
            assertEquals(message, this.messageListener.getContext("message"));
            assertEquals(level, this.messageListener.getContext("level"));

            assertNotNull(this.messageListener.getPropertyNames());
            assertEquals(2, this.messageListener.getPropertyNames().size());
        }
    }

}

// end of class
