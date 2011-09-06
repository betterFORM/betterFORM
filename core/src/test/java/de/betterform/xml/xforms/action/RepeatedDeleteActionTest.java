/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;

import java.util.Map;

/**
 * Test cases for the delete action.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: DeleteActionTest.java 3474 2008-08-15 22:29:43Z joern $
 */
public class RepeatedDeleteActionTest extends BetterFormTestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }


    public void testDeleteWithRepeatedButton() throws Exception{
        this.processor.dispatch("C14", DOMEventNames.ACTIVATE);
        assertEquals(3, (int)evaluateInDefaultContextAsDouble("count(/data/a)"));
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
        return "test-repeat-error.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}

// end of class
