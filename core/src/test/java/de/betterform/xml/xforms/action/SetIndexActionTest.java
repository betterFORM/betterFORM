package de.betterform.xml.xforms.action;

import junit.framework.TestCase;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.ui.Output;
import de.betterform.xml.xforms.ui.Repeat;
import org.w3c.dom.events.EventTarget;

/**
 * Test cases for the setindex action.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: SetIndexActionTest.java 3411 2008-08-13 09:58:41Z joern $
 */
public class SetIndexActionTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Repeat repeat;
    private EventTarget eventTarget;
    private TestEventListener scrollFirstListener;
    private TestEventListener scrollLastListener;
    private Output selection;

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("SetIndexActionTest.xhtml"));
        this.xformsProcesssorImpl.init();

        this.scrollFirstListener = new TestEventListener();
        this.scrollLastListener = new TestEventListener();

        this.eventTarget = (EventTarget) this.xformsProcesssorImpl.getXForms().getDocumentElement();
        this.eventTarget.addEventListener(XFormsEventNames.SCROLL_FIRST, this.scrollFirstListener, true);
        this.eventTarget.addEventListener(XFormsEventNames.SCROLL_LAST, this.scrollLastListener, true);

        this.repeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup("repeat");
        this.selection = (Output) this.xformsProcesssorImpl.getContainer().lookup("selection");
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.eventTarget.removeEventListener(XFormsEventNames.SCROLL_FIRST, this.scrollFirstListener, true);
        this.eventTarget.removeEventListener(XFormsEventNames.SCROLL_LAST, this.scrollLastListener, true);
        this.eventTarget = null;

        this.scrollFirstListener = null;
        this.scrollLastListener = null;

        this.repeat = null;
        this.selection = null;

        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }
    
    /**
     * Tests setting the index before the first position.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetIndexBeforeFirst() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-before", DOMEventNames.ACTIVATE);

        assertEquals(1, this.repeat.getIndex());
        assertEquals("first", this.selection.getValue());
        assertEquals("repeat", this.scrollFirstListener.getId());
        assertEquals(null, this.scrollLastListener.getId());
    }

    /**
     * Tests setting the index to the first position.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetIndexToFirst() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-first", DOMEventNames.ACTIVATE);

        assertEquals(1, this.repeat.getIndex());
        assertEquals("first", this.selection.getValue());
        assertEquals(null, this.scrollFirstListener.getId());
        assertEquals(null, this.scrollLastListener.getId());
    }

    /**
     * Tests setting the index between the first and the last position.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetIndexBetweenFirstAndLast() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-between", DOMEventNames.ACTIVATE);

        assertEquals(2, this.repeat.getIndex());
        assertEquals("between", this.selection.getValue());
        assertEquals(null, this.scrollFirstListener.getId());
        assertEquals(null, this.scrollLastListener.getId());
    }

    /**
     * Tests setting the index to the last position.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetIndexToLast() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-last", DOMEventNames.ACTIVATE);

        assertEquals(3, this.repeat.getIndex());
        assertEquals("last", this.selection.getValue());
        assertEquals(null, this.scrollFirstListener.getId());
        assertEquals(null, this.scrollLastListener.getId());
    }

    /**
     * Tests setting the index after the last position.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetIndexAfterLast() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-after", DOMEventNames.ACTIVATE);

        assertEquals(3, this.repeat.getIndex());
        assertEquals("last", this.selection.getValue());
        assertEquals(null, this.scrollFirstListener.getId());
        assertEquals("repeat", this.scrollLastListener.getId());
    }

    /**
     * Tests setting the index without a number.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetIndexNaN() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-nan", DOMEventNames.ACTIVATE);

        assertEquals(1, this.repeat.getIndex());
        assertEquals("first", this.selection.getValue());
        assertEquals(null, this.scrollFirstListener.getId());
        assertEquals(null, this.scrollLastListener.getId());
    }

    /**
     * Tests setting the index to the position computed by 'last()'.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetIndexToComputed() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-computed", DOMEventNames.ACTIVATE);

        assertEquals(3, this.repeat.getIndex());
        assertEquals("last", this.selection.getValue());
        assertEquals(null, this.scrollFirstListener.getId());
        assertEquals(null, this.scrollLastListener.getId());
    }

    /**
     * Tests setting the index inside a repeat.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetIndexInsideRepeat() throws Exception {
        EventTarget target = this.xformsProcesssorImpl.getContainer().lookup("trigger-repeated").getTarget();
        this.xformsProcesssorImpl.getContainer().dispatch(target, DOMEventNames.ACTIVATE, null);

        assertEquals(3, this.repeat.getIndex());
        assertEquals("last", this.selection.getValue());
        assertEquals(null, this.scrollFirstListener.getId());
        assertEquals(null, this.scrollLastListener.getId());
    }


}

// end of class
