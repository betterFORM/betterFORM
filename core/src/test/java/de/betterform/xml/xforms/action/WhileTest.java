/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;

/**
 * Test cases for XForms 1.1. 'if' attribute for Conditional Execution of Actions
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: IfTest.java 2797 2007-08-10 12:45:24Z joern $
 */
public class WhileTest extends BetterFormTestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }


    public void testWhile() throws Exception {
        this.processor.dispatch("trigger-while", DOMEventNames.ACTIVATE);

        // DOMUtil.prettyPrintDOM(this.processor.getContainer().getDocument());
        
        assertEquals("7", evaluateInInstanceAsString("temps", "string(/data/counter)"));
        assertEquals("14", evaluateInInstanceAsString("temps", "string(/data/accumulator)"));
    }


    protected String getTestCaseURI() {
        return "WhileTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
