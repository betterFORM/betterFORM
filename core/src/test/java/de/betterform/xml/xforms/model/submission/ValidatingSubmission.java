/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.submission;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.ui.Output;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;

/**
 * Created by IntelliJ IDEA.
 * @author Lars Windauer
 * @author Joern Turner
 * Date: May 5, 2009

 */
public class ValidatingSubmission extends BetterFormTestCase {

/*
    static {
        org.apache.log4j.BasicConfigurator.configure();
    }
*/

    public void testValidatingSubmission() throws Exception{
        // DOMUtil.prettyPrintDOM(this.processor.getXForms());
        // dump(((XFormsProcessorImpl)processor).getContainer().getModel("model-2").getDefaultInstance().getInstanceDocument());
        assertEquals("", "", XPathUtil.evaluateAsString((Document) this.processor.getXForms(), "//xf:input[1]/bf:data"));
        assertEquals("", "", XPathUtil.evaluateAsString((Document) this.processor.getXForms(), "//xf:input[2]/bf:data"));
        assertEquals("", "", XPathUtil.evaluateAsString((Document) this.processor.getXForms(), "//xf:input[3]/bf:data"));
        assertEquals("", "", XPathUtil.evaluateAsString((Document) this.processor.getXForms(), "//xf:input[4]/bf:data"));

        Output output = (Output) ((XFormsProcessorImpl)this.processor).getContainer().lookup("result");

        // this.xformsProcesssorImpl.setControlValue("input-item-1", "3");
        assertEquals("empty", output.getValue());
        this.processor.dispatch("t-validate", DOMEventNames.ACTIVATE);
        assertEquals("invalid", output.getValue());

        this.processor.setControlValue("input1", "123456");
        this.processor.setControlValue("input2", "123456");
        this.processor.setControlValue("input3", "123456");
        this.processor.setControlValue("input4", "123456");

        this.processor.dispatch("t-validate", DOMEventNames.ACTIVATE);
        assertEquals("valid", output.getValue());

        // DOMUtil.prettyPrintDOM(this.processor.getXForms());
        dump(((XFormsProcessorImpl)processor).getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument());
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
        return "ValidatingSubmission.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
