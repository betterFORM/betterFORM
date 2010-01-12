package de.betterform.xml.xforms.model.submission;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;

/**
 * Created by IntelliJ IDEA.
 * @author Lars Windauer
 * @author Joern Turner
 * Date: May 5, 2009

 */
public class CrossModelSubmissionTest extends BetterFormTestCase {
/*
    static {
        org.apache.log4j.BasicConfigurator.configure();
    }
*/

    public void testCrossModelSubmission() throws Exception{
        assertEquals("", "false", XPathUtil.evaluateAsString((Document) this.processor.getXForms(), "//xf:input[1]/bf:data"));
        assertEquals("", "2009-09-09", XPathUtil.evaluateAsString((Document) this.processor.getXForms(), "//xf:input[2]/bf:data/@bf:schema-value"));
        assertEquals("", "item instance 2", XPathUtil.evaluateAsString((Document) this.processor.getXForms(), "//xf:input[3]/bf:data"));

        this.processor.dispatch("trigger-cross-model-submission-1", DOMEventNames.ACTIVATE);
        // DOMUtil.prettyPrintDOM(this.processor.getXForms());
        // dump(((XFormsProcessorImpl)processor).getContainer().getModel("model-2").getDefaultInstance().getInstanceDocument());
        assertEquals("", "true", XPathUtil.evaluateAsString((Document) this.processor.getXForms(), "//xf:input[1]/bf:data"));
        assertEquals("", "2001-01-01", XPathUtil.evaluateAsString((Document) this.processor.getXForms(), "//xf:input[2]/bf:data/@bf:schema-value"));
        assertEquals("", "item instance 1", XPathUtil.evaluateAsString((Document) this.processor.getXForms(), "//xf:input[3]/bf:data"));
    }

    public void testCrossModelSubmissionWithTarget() throws Exception{        
        assertEquals("", "false", XPathUtil.evaluateAsString((Document) this.processor.getXForms(), "//xf:input[1]/bf:data"));
        assertEquals("", "2009-09-09", XPathUtil.evaluateAsString((Document) this.processor.getXForms(), "//xf:input[2]/bf:data/@bf:schema-value"));
        assertEquals("", "item instance 2", XPathUtil.evaluateAsString((Document) this.processor.getXForms(), "//xf:input[3]/bf:data"));

        this.processor.dispatch("trigger-cross-model-submission-2", DOMEventNames.ACTIVATE);
        Document instance1Model2 = this.processor.getXFormsModel("model-2").getInstanceDocument("i-1-m-2");
        // DOMUtil.prettyPrintDOM(instance1Model2);
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();

    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {

        super.tearDown();
    }

    protected String getTestCaseURI() {
        return "CrossModelSubmissionTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
