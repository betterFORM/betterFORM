/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.dom.DOMUtil;

import java.io.InputStream;

/**
 * Test cases for XForms 1.1. 'if' attribute for Conditional Execution of Actions
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: IfTest.java 2797 2007-08-10 12:45:24Z joern $
 */
public class VariableTest extends BetterFormTestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }


    public void testVariableCreation() throws Exception {
        this.processor.dispatch("trigger-variables", DOMEventNames.ACTIVATE);

        // DOMUtil.prettyPrintDOM(((XFormsProcessorImpl)this.processor).getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument());

        String p1 = (String) this.processor.getContextParam("foo");
        String p2 = (String) this.processor.getContextParam("bar");

        assertEquals(p1, "bar");
        assertEquals(p2, "true");
        

    }


    protected String getTestCaseURI() {
        return "VariableTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
