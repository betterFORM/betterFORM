/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model;

import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xforms.action.UpdateHandler;
import org.w3c.dom.events.EventTarget;

/**
 * Test cases for extensions functions declared on model.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: SubmissionTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class SubmissionTest extends XMLTestBase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private EventTarget eventTarget;
    private TestEventListener submitDoneListener;
    private TestEventListener submitErrorListener;

    /**
     * Tests deferred update behaviour of a replace instance submission.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testReplaceInstanceUpdateBehaviour() throws Exception {
        Model model = this.xformsProcesssorImpl.getContainer().getDefaultModel();
        UpdateHandler updateHandler = new UpdateHandler(model);
        updateHandler.doRebuild(true);
        updateHandler.doRecalculate(true);
        updateHandler.doRevalidate(true);
        updateHandler.doRefresh(true);
        model.setUpdateHandler(updateHandler);

        EventTarget target = model.getTarget();
        TestEventListener rebuildListener = new TestEventListener();
        TestEventListener recalculateListener = new TestEventListener();
        TestEventListener revalidateListener = new TestEventListener();
        TestEventListener refreshListener = new TestEventListener();
        target.addEventListener(XFormsEventNames.REBUILD, rebuildListener, false);
        target.addEventListener(XFormsEventNames.RECALCULATE, recalculateListener, false);
        target.addEventListener(XFormsEventNames.REVALIDATE, revalidateListener, false);
        target.addEventListener(XFormsEventNames.REFRESH, refreshListener, false);

        this.xformsProcesssorImpl.dispatch("submission-replace-instance", XFormsEventNames.SUBMIT);
        updateHandler.doUpdate();

        assertEquals("submission-replace-instance", this.submitDoneListener.getId());
        assertEquals(null, this.submitErrorListener.getId());

        assertEquals(null, rebuildListener.getId());
        assertEquals(null, recalculateListener.getId());
        assertEquals(null, revalidateListener.getId());
        assertEquals(null, refreshListener.getId());
    }

    /**
     * Tests submission chaining.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSubmissionChained() throws Exception{
        this.xformsProcesssorImpl.dispatch("submission-chained", XFormsEventNames.SUBMIT);

        assertEquals("submission-replace-instance", this.submitDoneListener.getId());
        assertEquals(null, this.submitErrorListener.getId());
    }

    /**
     * Tests submission with an empty nodeset binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testEmptyNodeset() throws Exception{
        this.xformsProcesssorImpl.dispatch("submission-empty-nodeset", XFormsEventNames.SUBMIT);

        assertEquals(null, this.submitDoneListener.getId());
        assertEquals("submission-empty-nodeset", this.submitErrorListener.getId());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("SubmissionTest.xhtml"));
        this.xformsProcesssorImpl.init();

        this.submitDoneListener = new TestEventListener();
        this.submitErrorListener = new TestEventListener();

        this.eventTarget = (EventTarget) this.xformsProcesssorImpl.getXForms().getDocumentElement();
        this.eventTarget.addEventListener(XFormsEventNames.SUBMIT_DONE, this.submitDoneListener, true);
        this.eventTarget.addEventListener(XFormsEventNames.SUBMIT_ERROR, this.submitErrorListener, true);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.eventTarget.removeEventListener(XFormsEventNames.SUBMIT_DONE, this.submitDoneListener, true);
        this.eventTarget.removeEventListener(XFormsEventNames.SUBMIT_ERROR, this.submitErrorListener, true);
        this.eventTarget = null;

        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

}
