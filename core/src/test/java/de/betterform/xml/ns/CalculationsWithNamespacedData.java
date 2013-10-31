/**
 * Tests calculations on namespaced da.
 *
 * @author Lars Windauer
 * @version
 */

package de.betterform.xml.ns;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;


public class CalculationsWithNamespacedData extends BetterFormTestCase {
    static {
        org.apache.log4j.BasicConfigurator.configure();
    }

    private Instance instance;

    /**
     * Tests instance initialization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInit() throws Exception {
        assertNotNull(this.instance);
        assertEquals("i-namespace", this.instance.getId());
        assertNotNull(this.instance.getInstanceDocument());
        assertEquals("data", this.instance.getInstanceDocument().getDocumentElement().getNodeName());
    }


    /**
     * Tests calculations with not namespaced data
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCalcOnNonNamespacedData() throws Exception {
        Document instance =((XFormsProcessorImpl)processor).getContainer().getDefaultModel().getInstanceDocument("i-nonamespace");
        System.out.println("integer" + XPathUtil.evaluateAsString(instance, "//integer"));
        assertEquals("", XPathUtil.evaluateAsString(instance, "//integer"));
        assertEquals("NaN", XPathUtil.evaluateAsString(instance, "//calculated"));
        processor.dispatch("changedNotNamespaceValue", DOMEventNames.ACTIVATE);
        assertEquals("5", XPathUtil.evaluateAsString(instance, "//integer"));
        assertEquals("10", XPathUtil.evaluateAsString(instance, "//calculated"));
    }

    /**
     * Tests calculations with  namespaced data
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCalcOnNamespacedData() throws Exception {
        Document instance =((XFormsProcessorImpl)processor).getContainer().getDefaultModel().getInstanceDocument("i-namespace");
        System.out.println("integer" + XPathUtil.evaluateAsString(instance, "//c:integer"));
        assertEquals("", XPathUtil.evaluateAsString(instance, "//c:integer"));
        assertEquals("NaN", XPathUtil.evaluateAsString(instance, "//c:calculated"));
        processor.dispatch("changedNamespaceValue", DOMEventNames.ACTIVATE);
        assertEquals("5", XPathUtil.evaluateAsString(instance, "//c:integer"));
        assertEquals("10", XPathUtil.evaluateAsString(instance, "//c:calculated"));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.instance = getDefaultModel().getDefaultInstance();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.instance = null;
        super.tearDown();
    }


    protected String getTestCaseURI() {
        return "CalculationsWithNamespacedData.xhtml";  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
