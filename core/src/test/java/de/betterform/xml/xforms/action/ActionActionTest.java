/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.action;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.events.EventTarget;

/**
 * Test cases for the action action (deferred update behvaiour and further
 * update sequencing).
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ActionActionTest.java 3264 2008-07-16 12:26:54Z joern $
 */
public class ActionActionTest extends BetterFormTestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private EventCountListener rebuildCountListener;
    private EventCountListener recalculateCountListener;
    private EventCountListener revalidateCountListener;
    private EventCountListener refreshCountListener;

    /**
     * Tests an update w/o outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUpdate() throws Exception {
        this.processor.dispatch("trigger-update", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(2, this.recalculateCountListener.getCount());
        assertEquals(2, this.revalidateCountListener.getCount());
        assertEquals(2, this.refreshCountListener.getCount());
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests an update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUpdateHandler() throws Exception {
        this.processor.dispatch("trigger-update-handler", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(1, this.recalculateCountListener.getCount());
        assertEquals(1, this.revalidateCountListener.getCount());
        assertEquals(1, this.refreshCountListener.getCount());
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests an update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUpdateHandlerFlags() throws Exception {
        this.processor.dispatch("trigger-update-handler-flags", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(0, this.recalculateCountListener.getCount());
        assertEquals(0, this.revalidateCountListener.getCount());
        assertEquals(0, this.refreshCountListener.getCount());
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests a nested update w/o outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testNestedUpdate() throws Exception {
        this.processor.dispatch("trigger-nested-update", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(3, this.recalculateCountListener.getCount());
        assertEquals(3, this.revalidateCountListener.getCount());
        assertEquals(3, this.refreshCountListener.getCount());
        assertEquals("4", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests a nested update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testNestedUpdateHandler() throws Exception {
        this.processor.dispatch("trigger-nested-update-handler", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(1, this.recalculateCountListener.getCount());
        assertEquals(1, this.revalidateCountListener.getCount());
        assertEquals(1, this.refreshCountListener.getCount());
        assertEquals("4", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests a dependant update w/o outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDependantUpdate() throws Exception {
        this.processor.dispatch("trigger-dependant-update", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(4, this.recalculateCountListener.getCount());
        assertEquals(4, this.revalidateCountListener.getCount());
        assertEquals(4, this.refreshCountListener.getCount());
        assertEquals("4", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests a dependant update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDependantUpdateHandler() throws Exception {
        this.processor.dispatch("trigger-dependant-update-handler", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(1, this.recalculateCountListener.getCount());
        assertEquals(1, this.revalidateCountListener.getCount());
        assertEquals(1, this.refreshCountListener.getCount());
        assertEquals("4", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests a dependant update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDependantUpdateNestedHandler() throws Exception {
        this.processor.dispatch("trigger-dependant-update-nested-handler", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(1, this.recalculateCountListener.getCount());
        assertEquals(1, this.revalidateCountListener.getCount());
        assertEquals(1, this.refreshCountListener.getCount());
        assertEquals("4", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests a sequence update w/o outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSequenceUpdate() throws Exception {
        this.processor.dispatch("trigger-sequence-update", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(6, this.recalculateCountListener.getCount());
        assertEquals(6, this.revalidateCountListener.getCount());
        assertEquals(6, this.refreshCountListener.getCount());
        assertEquals("4", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[2])"));
    }

    /**
     * Tests a sequence update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSequenceUpdateHandler() throws Exception {
        this.processor.dispatch("trigger-sequence-update-handler", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(3, this.recalculateCountListener.getCount());
        assertEquals(3, this.revalidateCountListener.getCount());
        assertEquals(3, this.refreshCountListener.getCount());
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[3])"));
    }

    /**
     * Tests a sequence update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSequenceUpdateNestedHandler() throws Exception {
        this.processor.dispatch("trigger-sequence-update-nested-handler", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(2, this.recalculateCountListener.getCount());
        assertEquals(2, this.revalidateCountListener.getCount());
        assertEquals(2, this.refreshCountListener.getCount());
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[4])"));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.rebuildCountListener = new EventCountListener(XFormsEventNames.REBUILD);
        this.recalculateCountListener = new EventCountListener(XFormsEventNames.RECALCULATE);
        this.revalidateCountListener = new EventCountListener(XFormsEventNames.REVALIDATE);
        this.refreshCountListener = new EventCountListener(XFormsEventNames.REFRESH);

        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.addEventListener(XFormsEventNames.REBUILD, this.rebuildCountListener, true);
        eventTarget.addEventListener(XFormsEventNames.RECALCULATE, this.recalculateCountListener, true);
        eventTarget.addEventListener(XFormsEventNames.REVALIDATE, this.revalidateCountListener, true);
        eventTarget.addEventListener(XFormsEventNames.REFRESH, this.refreshCountListener, true);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.removeEventListener(XFormsEventNames.REBUILD, this.rebuildCountListener, true);
        eventTarget.removeEventListener(XFormsEventNames.RECALCULATE, this.recalculateCountListener, true);
        eventTarget.removeEventListener(XFormsEventNames.REVALIDATE, this.revalidateCountListener, true);
        eventTarget.removeEventListener(XFormsEventNames.REFRESH, this.refreshCountListener, true);

        this.rebuildCountListener = null;
        this.recalculateCountListener = null;
        this.revalidateCountListener = null;
        this.refreshCountListener = null;

        super.tearDown();
    }

    protected String getTestCaseURI() {
        return "ActionActionTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
