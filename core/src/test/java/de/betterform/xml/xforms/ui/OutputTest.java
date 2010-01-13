/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */
package de.betterform.xml.xforms.ui;

import junit.framework.TestCase;
import de.betterform.xml.dom.DOMComparator;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;

/**
 * Tests the output control.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: OutputTest.java 3492 2008-08-27 12:37:01Z joern $
 */
public class OutputTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private TestEventListener stateChangedListener;

    /**
     * Tests output with an ui binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUIBinding() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-ui-binding");
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("1", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-ui-binding']/bf:data"));
        assertEquals("string", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-ui-binding']/bf:data/@bf:type"));
        assertEquals("1", output.getValue());


        register(output.getTarget(), false);
        this.xformsProcesssorImpl.setControlValue("input-item-1", "3");
        deregister(output.getTarget(), false);

        assertEquals("3", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-ui-binding']/bf:data"));
        assertEquals("3", output.getValue());
        assertEquals("output-ui-binding", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("3", this.stateChangedListener.getContext("value"));
    }

    public void testTreeUIBinding() throws Exception {
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        Node instanceNode = XPathUtil.evaluateAsSingleNode(host, "//tree");
        Node dataNode = XPathUtil.evaluateAsSingleNode(host, "//xf:output[@id='tree-ui-binding']/bf:data/*");
        assertTrue(getComparator().compare(instanceNode, dataNode));
    }

    /**
     * Tests output with a model binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testModelBinding() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-model-binding");
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("2", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-model-binding']/bf:data"));
        assertEquals("string", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-model-binding']/bf:data/@bf:type"));
        assertEquals("2", output.getValue());


        register(output.getTarget(), false);
        this.xformsProcesssorImpl.setControlValue("input-item-2", "3");
        deregister(output.getTarget(), false);

        assertEquals("3", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-model-binding']/bf:data"));
        assertEquals("3", output.getValue());
        assertEquals("output-model-binding", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("3", this.stateChangedListener.getContext("value"));
    }

    /**
     * Tests output with a string value expression.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testStringValueExpression() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-string-expression-value");
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("expression", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-string-expression-value']/bf:data"));
        assertEquals("string", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-string-expression-value']/bf:data/@bf:type"));
        assertEquals("expression", output.getValue());
    }

    /**
     * Tests output with an integer value expression.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIntegerValueExpression() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-integer-expression-value");
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("3", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-integer-expression-value']/bf:data"));
        assertEquals("string", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-integer-expression-value']/bf:data/@bf:type"));
        assertEquals("3", output.getValue());
    }

    /**
     * Tests output with a fraction value expression.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testFractionValueExpression() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-fraction-expression-value");
        Document host = xformsProcesssorImpl.getContainer().getDocument();

        // DOMUtil.prettyPrintDOM(host.getDocumentElement());
        assertEquals("1.5", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-fraction-expression-value']/bf:data/@bf:schema-value"));
        assertEquals("string", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-fraction-expression-value']/bf:data/@bf:type"));
        assertEquals("1.5", output.getSchemaValue());
    }

    /**
     * Tests output with a NaN value expression.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testNaNValueExpression() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-nan-expression-value");
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("NaN", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-nan-expression-value']/bf:data"));
        assertEquals("string", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-nan-expression-value']/bf:data/@bf:type"));
        assertEquals("NaN", output.getValue());
    }

    /**
     * Tests output with a nodeset value expression.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testNodesetValueExpression() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-nodeset-expression-value");
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("2", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-nodeset-expression-value']/bf:data"));
        assertEquals("string", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-nodeset-expression-value']/bf:data/@bf:type"));
        assertEquals("2", output.getValue());
    }

    /**
     * Tests output with an empty nodeset value expression.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testEmptyNodesetValueExpression() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-empty-nodeset-expression-value");
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-empty-nodeset-expression-value']/bf:data"));
        assertEquals("string", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-empty-nodeset-expression-value']/bf:data/@bf:type"));
        assertEquals(null, output.getValue());
    }

    /**
     * Tests output with a value expression.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValueExpressionUpdate() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-integer-expression-value");
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("3", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-integer-expression-value']/bf:data"));
        assertEquals("string", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-integer-expression-value']/bf:data/@bf:type"));
        assertEquals("3", output.getValue());


        register(output.getTarget(), false);
        this.xformsProcesssorImpl.setControlValue("input-item-1", "3");
        deregister(output.getTarget(), false);

        assertEquals("5", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-integer-expression-value']/bf:data"));
        assertEquals("5", output.getValue());
        assertEquals("output-integer-expression-value", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("5", this.stateChangedListener.getContext("value"));
    }

    /**
     * Tests output with a value expression.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValueExpressionContext() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-expression-context-value");
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("3", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-expression-context-value']/bf:data"));
        assertEquals("string", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-expression-context-value']/bf:data/@bf:type"));
        assertEquals("3", output.getValue());


        register(output.getTarget(), false);
        this.xformsProcesssorImpl.setControlValue("input-item-1", "3");
        deregister(output.getTarget(), false);

        assertEquals("5", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-expression-context-value']/bf:data"));
        assertEquals("5", output.getValue());
        assertEquals("output-expression-context-value", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("5", this.stateChangedListener.getContext("value"));
    }

    /**
     * Tests output with a value expression.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValueExpressionContextNonExisting() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-expression-empty-nodeset-context-value");
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-expression-empty-nodeset-context-value']/bf:data"));
        assertEquals("string", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-expression-empty-nodeset-context-value']/bf:data/@bf:type"));
        assertEquals(null, output.getValue());
    }

    /**
     * Tests output with an ui binding and a value expression.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUIBindingPrecedence() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-ui-binding-precedence");
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("1", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-ui-binding-precedence']/bf:data"));
        assertEquals("string", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-ui-binding-precedence']/bf:data/@bf:type"));
        assertEquals("1", output.getValue());

    }

    /**
     * Tests output with a model binding and a value expression.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testModelBindingPrecedence() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-model-binding-precedence");
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("2", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-model-binding-precedence']/bf:data"));
        assertEquals("string", XPathUtil.evaluateAsString(host, "//xf:output[@id='output-model-binding-precedence']/bf:data/@bf:type"));
        assertEquals("2", output.getValue());

    }

    /**
     * Tests output with neither a binding nor a value expression.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUnspecified() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-unspecified");
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("0", XPathUtil.evaluateAsString(host, "count(//xf:output[@id='output-unspecified']/bf:data)"));
        assertEquals(null, output.getValue());

    }

    public void testOtherModel() throws Exception {
        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-model2-ui-binding");
        assertEquals("foo", output.getValue());

        Output output2 = (Output) this.xformsProcesssorImpl.getContainer().lookup("output-model2-value");
        assertEquals("foo", output2.getValue());

    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.stateChangedListener = new TestEventListener();

        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("OutputTest.xhtml"));

        EventTarget eventTarget = (EventTarget) this.xformsProcesssorImpl.getXForms().getDocumentElement();
        register(eventTarget, true);
        this.xformsProcesssorImpl.init();
        deregister(eventTarget, true);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;

        this.stateChangedListener = null;
    }

    private void register(EventTarget eventTarget, boolean bubbles) {
        eventTarget.addEventListener(BetterFormEventNames.STATE_CHANGED, this.stateChangedListener, bubbles);
    }

    private void deregister(EventTarget eventTarget, boolean bubbles) {
        eventTarget.removeEventListener(BetterFormEventNames.STATE_CHANGED, this.stateChangedListener, bubbles);
    }

    private DOMComparator getComparator() {
        DOMComparator comparator = new DOMComparator();
        comparator.setIgnoreNamespaceDeclarations(true);
        comparator.setIgnoreWhitespace(true);
        comparator.setIgnoreComments(true);
        comparator.setErrorHandler(new DOMComparator.SystemErrorHandler());

        return comparator;
    }


}

// end of class
