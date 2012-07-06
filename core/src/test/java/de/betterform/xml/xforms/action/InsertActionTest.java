/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.action;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.events.EventTarget;

/**
 * Test cases for the insert action.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: InsertActionTest.java 3264 2008-07-16 12:26:54Z joern $
 */
public class InsertActionTest extends BetterFormTestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private TestEventListener insertListener;
    private TestEventListener rebuildListener;
    private TestEventListener recalulateListener;
    private TestEventListener revalidateListener;
    private TestEventListener refreshListener;

    /**
     * Tests inserting into an empty nodeset.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertIntoEmptyNodeset() throws Exception {
        this.processor.dispatch("insert-into-empty-nodeset", DOMEventNames.ACTIVATE);

        assertEquals(3, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[3])"));

        assertEquals(null, this.insertListener.getId());
        assertEquals(null, this.insertListener.getContext());
        assertEquals(null, this.rebuildListener.getId());
        assertEquals(null, this.recalulateListener.getId());
        assertEquals(null, this.revalidateListener.getId());
        assertEquals(null, this.refreshListener.getId());
    }

    /**
     * Tests inserting into an empty nodeset.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertIntoEmptyNodesetWithPredicate() throws Exception {
        this.processor.dispatch("insert-into-empty-nodeset-with-predicate", DOMEventNames.ACTIVATE);

        assertEquals(3, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[3])"));

        assertEquals(null, this.insertListener.getId());
        assertEquals(null, this.insertListener.getContext());
        assertEquals(null, this.rebuildListener.getId());
        assertEquals(null, this.recalulateListener.getId());
        assertEquals(null, this.revalidateListener.getId());
        assertEquals(null, this.refreshListener.getId());
    }

    /**
     * Tests inserting before -1.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertBeforeNegative() throws Exception {
        this.processor.dispatch("insert-before-negative", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());

        assertEquals("My data 3", evaluateInInstanceAsString("instance-event", "string(/data/inserted-nodes)"));
        assertEquals("My data 3", evaluateInInstanceAsString("instance-event", "string(/data/origin-nodes)"));
        assertEquals("My data 1", evaluateInInstanceAsString("instance-event", "string(/data/insert-location-node)"));
        assertEquals("before", evaluateInInstanceAsString("instance-event", "string(/data/position)"));
    }

    /**
     * Tests inserting before 0.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertBeforeZero() throws Exception {
        this.processor.dispatch("insert-before-zero", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting before 2.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertBeforeSecond() throws Exception {
        this.processor.dispatch("insert-before-second", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting before 4.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertBeforeNonExisting() throws Exception {
        this.processor.dispatch("insert-before-non-existing", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting before 1.5.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertBeforeFloat() throws Exception {
        this.processor.dispatch("insert-before-float", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting before 'NaN'.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertBeforeNaN() throws Exception {
        this.processor.dispatch("insert-before-nan", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting after -1.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertAfterNegative() throws Exception {
        this.processor.dispatch("insert-after-negative", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting after 0.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertAfterZero() throws Exception {
        this.processor.dispatch("insert-after-zero", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting after 2.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertAfterSecond() throws Exception {
        this.processor.dispatch("insert-after-second", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting after 4.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertAfterNonExisting() throws Exception {
        this.processor.dispatch("insert-after-non-existing", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting after 1.5.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertAfterFloat() throws Exception {
        this.processor.dispatch("insert-after-float", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting after 'NaN'.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertAfterNaN() throws Exception {
        this.processor.dispatch("insert-after-nan", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting into another instance.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertIntoOtherInstance() throws Exception {
        this.processor.dispatch("insert-into-other-instance", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInInstanceAsDouble("instance-2", "count(/data/item)"));
        assertEquals("My data 1",  evaluateInInstanceAsString("instance-2", "string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInInstanceAsString("instance-2", "string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInInstanceAsString("instance-2", "string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInInstanceAsString("instance-2", "string(/data/item[4])"));

        assertEquals("instance-2", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting with a predicate.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertWithPredicate() throws Exception {
        this.processor.dispatch("insert-with-predicate", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));


        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting with the index() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertWithIndexFunction() throws Exception {
        this.processor.setRepeatIndex("repeat", 2);
        this.processor.dispatch("insert-with-index-function", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting with the last() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertWithLastFunction() throws Exception {
        this.processor.dispatch("insert-with-last-function", DOMEventNames.ACTIVATE);
        Instance instance = getDefaultModel().getDefaultInstance();

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));
        assertEquals("item", evaluateInDefaultContextAsString("name(/data/*[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }
    
    public void testInsertWithOrigin() throws Exception {
        this.processor.dispatch("insert-with-origin", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("Template Version",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    public void testInsertWithRelativeOrigin() throws Exception {
        this.processor.dispatch("insert-with-relative-origin", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("foo",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting with a predicate and the last() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertWithPredicateAndLastFunction() throws Exception {
        this.processor.dispatch("insert-with-predicate-and-last-function", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInInstanceAsDouble("instance-2", "count(/data/item)"));
        assertEquals("My data 1",  evaluateInInstanceAsString("instance-2", "string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInInstanceAsString("instance-2", "string(/data/item[2])"));
        assertEquals("My data 2",  evaluateInInstanceAsString("instance-2", "string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInInstanceAsString("instance-2", "string(/data/item[4])"));

        assertEquals("instance-2", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests inserting with a model binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertWithBind() throws Exception {
        this.processor.dispatch("insert-with-bind", DOMEventNames.ACTIVATE);

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1",  evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2",  evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[3])"));
        assertEquals("My data 3",  evaluateInDefaultContextAsString("string(/data/item[4])"));

        assertEquals("instance-1", this.insertListener.getId());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }


    public void testInsertRepeatedRange() throws Exception {
        assertEquals(1, (int)evaluateInInstanceAsDouble("instance-range-integer", "count(/data/repeat)"));
        assertEquals(2, (int)evaluateInInstanceAsDouble("instance-range-integer", "count(/data/repeat/range)"));

        this.processor.dispatch("insert-repeated-range", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInInstanceAsDouble("instance-range-integer", "count(/data/repeat)"));
        assertEquals(2, (int)evaluateInInstanceAsDouble("instance-range-integer", "count(/data/repeat[1]/range)"));
        assertEquals(2, (int)evaluateInInstanceAsDouble("instance-range-integer", "count(/data/repeat[2]/range)"));
        assertEquals("3",  evaluateInInstanceAsString("instance-range-integer", "string(/data/repeat[1]/range[1])"));
        assertEquals("4",  evaluateInInstanceAsString("instance-range-integer", "string(/data/repeat[1]/range[2])"));
        assertEquals("3",  evaluateInInstanceAsString("instance-range-integer", "string(/data/repeat[2]/range[1])"));
        assertEquals("4",  evaluateInInstanceAsString("instance-range-integer", "string(/data/repeat[2]/range[2])"));

        this.processor.dispatch("insert-repeated-range", DOMEventNames.ACTIVATE);

        assertEquals(3, (int)evaluateInInstanceAsDouble("instance-range-integer", "count(/data/repeat)"));
        assertEquals(2, (int)evaluateInInstanceAsDouble("instance-range-integer", "count(/data/repeat[1]/range)"));
        assertEquals(2, (int)evaluateInInstanceAsDouble("instance-range-integer", "count(/data/repeat[2]/range)"));
        assertEquals(2, (int)evaluateInInstanceAsDouble("instance-range-integer", "count(/data/repeat[3]/range)"));
        assertEquals("3",  evaluateInInstanceAsString("instance-range-integer", "string(/data/repeat[1]/range[1])"));
        assertEquals("4",  evaluateInInstanceAsString("instance-range-integer", "string(/data/repeat[1]/range[2])"));
        assertEquals("3",  evaluateInInstanceAsString("instance-range-integer", "string(/data/repeat[2]/range[1])"));
        assertEquals("4",  evaluateInInstanceAsString("instance-range-integer", "string(/data/repeat[2]/range[2])"));
        assertEquals("3",  evaluateInInstanceAsString("instance-range-integer", "string(/data/repeat[3]/range[1])"));
        assertEquals("4",  evaluateInInstanceAsString("instance-range-integer", "string(/data/repeat[3]/range[2])"));
    }
    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.insertListener = new TestEventListener();
        this.rebuildListener = new TestEventListener();
        this.recalulateListener = new TestEventListener();
        this.revalidateListener = new TestEventListener();
        this.refreshListener = new TestEventListener();

        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.addEventListener(XFormsEventNames.INSERT, this.insertListener, true);
        eventTarget.addEventListener(XFormsEventNames.REBUILD, this.rebuildListener, true);
        eventTarget.addEventListener(XFormsEventNames.RECALCULATE, this.recalulateListener, true);
        eventTarget.addEventListener(XFormsEventNames.REVALIDATE, this.revalidateListener, true);
        eventTarget.addEventListener(XFormsEventNames.REFRESH, this.refreshListener, true);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.removeEventListener(XFormsEventNames.INSERT, this.insertListener, true);
        eventTarget.removeEventListener(XFormsEventNames.REBUILD, this.rebuildListener, true);
        eventTarget.removeEventListener(XFormsEventNames.RECALCULATE, this.recalulateListener, true);
        eventTarget.removeEventListener(XFormsEventNames.REVALIDATE, this.revalidateListener, true);
        eventTarget.removeEventListener(XFormsEventNames.REFRESH, this.refreshListener, true);

        this.insertListener = null;
        this.rebuildListener = null;
        this.recalulateListener = null;
        this.revalidateListener = null;
        this.refreshListener = null;

        super.tearDown();
    }
    
    protected String getTestCaseURI() {
        return "InsertActionTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}

// end of class
