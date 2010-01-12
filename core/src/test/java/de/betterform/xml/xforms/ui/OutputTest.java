// Copyright 2010 betterForm
/*
 *
 *    Artistic License
 *
 *    Preamble
 *
 *    The intent of this document is to state the conditions under which a Package may be copied, such that
 *    the Copyright Holder maintains some semblance of artistic control over the development of the
 *    package, while giving the users of the package the right to use and distribute the Package in a
 *    more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *    Definitions:
 *
 *    "Package" refers to the collection of files distributed by the Copyright Holder, and derivatives
 *    of that collection of files created through textual modification.
 *
 *    "Standard Version" refers to such a Package if it has not been modified, or has been modified
 *    in accordance with the wishes of the Copyright Holder.
 *
 *    "Copyright Holder" is whoever is named in the copyright or copyrights for the package.
 *
 *    "You" is you, if you're thinking about copying or distributing this Package.
 *
 *    "Reasonable copying fee" is whatever you can justify on the basis of media cost, duplication
 *    charges, time of people involved, and so on. (You will not be required to justify it to the
 *    Copyright Holder, but only to the computing community at large as a market that must bear the
 *    fee.)
 *
 *    "Freely Available" means that no fee is charged for the item itself, though there may be fees
 *    involved in handling the item. It also means that recipients of the item may redistribute it under
 *    the same conditions they received it.
 *
 *    1. You may make and give away verbatim copies of the source form of the Standard Version of this
 *    Package without restriction, provided that you duplicate all of the original copyright notices and
 *    associated disclaimers.
 *
 *    2. You may apply bug fixes, portability fixes and other modifications derived from the Public Domain
 *    or from the Copyright Holder. A Package modified in such a way shall still be considered the
 *    Standard Version.
 *
 *    3. You may otherwise modify your copy of this Package in any way, provided that you insert a
 *    prominent notice in each changed file stating how and when you changed that file, and provided that
 *    you do at least ONE of the following:
 *
 *        a) place your modifications in the Public Domain or otherwise make them Freely
 *        Available, such as by posting said modifications to Usenet or an equivalent medium, or
 *        placing the modifications on a major archive site such as ftp.uu.net, or by allowing the
 *        Copyright Holder to include your modifications in the Standard Version of the Package.
 *
 *        b) use the modified Package only within your corporation or organization.
 *
 *        c) rename any non-standard executables so the names do not conflict with standard
 *        executables, which must also be provided, and provide a separate manual page for each
 *        non-standard executable that clearly documents how it differs from the Standard
 *        Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    4. You may distribute the programs of this Package in object code or executable form, provided that
 *    you do at least ONE of the following:
 *
 *        a) distribute a Standard Version of the executables and library files, together with
 *        instructions (in the manual page or equivalent) on where to get the Standard Version.
 *
 *        b) accompany the distribution with the machine-readable source of the Package with
 *        your modifications.
 *
 *        c) accompany any non-standard executables with their corresponding Standard Version
 *        executables, giving the non-standard executables non-standard names, and clearly
 *        documenting the differences in manual pages (or equivalent), together with instructions
 *        on where to get the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    5. You may charge a reasonable copying fee for any distribution of this Package. You may charge
 *    any fee you choose for support of this Package. You may not charge a fee for this Package itself.
 *    However, you may distribute this Package in aggregate with other (possibly commercial) programs as
 *    part of a larger (possibly commercial) software distribution provided that you do not advertise this
 *    Package as a product of your own.
 *
 *    6. The scripts and library files supplied as input to or produced as output from the programs of this
 *    Package do not automatically fall under the copyright of this Package, but belong to whomever
 *    generated them, and may be sold commercially, and may be aggregated with this Package.
 *
 *    7. C or perl subroutines supplied by you and linked into this Package shall not be considered part of
 *    this Package.
 *
 *    8. The name of the Copyright Holder may not be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 *    9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
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
