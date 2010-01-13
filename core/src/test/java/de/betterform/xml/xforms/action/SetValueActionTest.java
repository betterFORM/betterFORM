/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */
package de.betterform.xml.xforms.action;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.exception.XFormsComputeException;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.events.EventTarget;

import java.io.InputStream;

/**
 * Test cases for the setvalue action.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: SetValueActionTest.java 3456 2008-08-13 14:47:27Z joern $
 */
public class SetValueActionTest extends BetterFormTestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private TestEventListener rebuildListener;
    private TestEventListener recalulateListener;
    private TestEventListener revalidateListener;
    private TestEventListener refreshListener;

    public void testSetValueInitial() throws Exception {
        assertEquals("foo", evaluateInDefaultContextAsString("string(/data/node2)"));
    }

    /**
     * Tests setting a string literal value.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueStringLiteral() throws Exception {
        this.processor.dispatch("trigger-string-literal", DOMEventNames.ACTIVATE);

        assertEquals("literal", evaluateInDefaultContextAsString("string(/data/item)"));
        assertEquals(null, this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests setting a number literal value.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueNumberLiteral() throws Exception {
        this.processor.dispatch("trigger-number-literal", DOMEventNames.ACTIVATE);

        assertEquals("42", evaluateInDefaultContextAsString("string(/data/item)"));
        assertEquals(null, this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests setting a string evaluating expression value.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueStringExpression() throws Exception {
        this.processor.dispatch("trigger-string-expression", DOMEventNames.ACTIVATE);

        assertEquals("expression", evaluateInDefaultContextAsString("string(/data/item)"));
        assertEquals(null, this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests setting an integer evaluating expression value.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueIntegerExpression() throws Exception {
        this.processor.dispatch("trigger-integer-expression", DOMEventNames.ACTIVATE);

        assertEquals("42", evaluateInDefaultContextAsString("string(/data/item)"));
        assertEquals(null, this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests setting a fraction evaluating expression value.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueFractionExpression() throws Exception {
        this.processor.dispatch("trigger-fraction-expression", DOMEventNames.ACTIVATE);

        assertEquals("8.4", evaluateInDefaultContextAsString("string(/data/item)"));
        assertEquals(null, this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests setting a NaN evaluating expression value.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueNaNExpression() throws Exception {
        this.processor.dispatch("trigger-nan-expression", DOMEventNames.ACTIVATE);

        assertEquals("NaN", evaluateInDefaultContextAsString("string(/data/item)"));
        assertEquals(null, this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests setting a nodeset expression value.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueNodesetExpression() throws Exception {
        this.processor.dispatch("trigger-nodeset-expression", DOMEventNames.ACTIVATE);

        assertEquals("text", evaluateInDefaultContextAsString("string(/data/item)"));
        assertEquals(null, this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests setting an empty nodeset expression value.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueEmptyNodesetExpression() throws Exception {
        this.processor.dispatch("trigger-empty-nodeset-expression", DOMEventNames.ACTIVATE);

        assertEquals("", evaluateInDefaultContextAsString("string(/data/item)"));
        assertEquals(null, this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests setting an invalid expression value.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueInvalidExpression() throws Exception {
        try {
            this.processor.dispatch("trigger-invalid-expression", DOMEventNames.ACTIVATE);
            fail("XFormsComputeException expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof XFormsComputeException);
            assertEquals(null, this.rebuildListener.getId());
            assertEquals(null, this.recalulateListener.getId());
            assertEquals(null, this.revalidateListener.getId());
            assertEquals(null, this.refreshListener.getId());
        }
    }

    /**
     * Tests setting a value without a value attribute or a literal value.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValueMissing() throws Exception {
        this.processor.dispatch("trigger-value-missing", DOMEventNames.ACTIVATE);

        assertEquals("", evaluateInDefaultContextAsString("string(/data/item)"));
        assertEquals(null, this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests setting a value with both a value attribute and a literal value.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValuePrecedence() throws Exception {
        this.processor.dispatch("trigger-value-precedence", DOMEventNames.ACTIVATE);

        assertEquals("precedence", evaluateInDefaultContextAsString("string(/data/item)"));
        assertEquals(null, this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
    }

    /**
     * Tests setting a value with an empty-nodeset binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testEmptyNodesetBinding() throws Exception {
        this.processor.dispatch("trigger-empty-nodeset-binding", DOMEventNames.ACTIVATE);

        assertEquals("value", evaluateInDefaultContextAsString("string(/data/item)"));
        assertEquals(null, this.rebuildListener.getId());
        assertEquals(null, this.recalulateListener.getId());
        assertEquals(null, this.revalidateListener.getId());
        assertEquals(null, this.refreshListener.getId());
    }

    /**
     * Tests setting a string to a foreign model with ui binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueForeignModel() throws Exception {
        this.processor.dispatch("trigger-foreign-model", DOMEventNames.ACTIVATE);

        assertEquals("test", evaluateInModelContextAsString("model-2", "string(/data/item)"));
        assertEquals(null, this.rebuildListener.getId());
        assertEquals("model-2", this.recalulateListener.getId());
        assertEquals("model-2", this.revalidateListener.getId());
        assertEquals("model-2", this.refreshListener.getId());
    }

    /**
     * Tests setting a string to a foreign model with model binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueForeignModelViaBind() throws Exception {
        this.processor.dispatch("trigger-foreign-model-bind", DOMEventNames.ACTIVATE);

        assertEquals("test", evaluateInModelContextAsString("model-2", "string(/data/item)"));
        assertEquals(null, this.rebuildListener.getId());
        assertEquals("model-2", this.recalulateListener.getId());
        assertEquals("model-2", this.revalidateListener.getId());
        assertEquals("model-2", this.refreshListener.getId());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.rebuildListener = new TestEventListener();
        this.recalulateListener = new TestEventListener();
        this.revalidateListener = new TestEventListener();
        this.refreshListener = new TestEventListener();

        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
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
        eventTarget.removeEventListener(XFormsEventNames.REBUILD, this.rebuildListener, true);
        eventTarget.removeEventListener(XFormsEventNames.RECALCULATE, this.recalulateListener, true);
        eventTarget.removeEventListener(XFormsEventNames.REVALIDATE, this.revalidateListener, true);
        eventTarget.removeEventListener(XFormsEventNames.REFRESH, this.refreshListener, true);

        this.rebuildListener = null;
        this.recalulateListener = null;
        this.revalidateListener = null;
        this.refreshListener = null;

        super.tearDown();
    }
    
//    protected InputStream getTestCaseDocumentAsStream() {
//        return getClass().getResourceAsStream("SetValueActionTest.xhtml");
//    }

    protected String getTestCaseURI() {
        return "SetValueActionTest.xhtml";  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}

// end of class
