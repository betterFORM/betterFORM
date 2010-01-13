/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.action.XFormsAction;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;

public class RepeatIndexFunctionTest extends BetterFormTestCase {

    protected void setUp() throws Exception {
         super.setUp();
    }

    public void testIndexFunctionBeforeRepeat() throws Exception{
        Document hostDoc = (Document) this.processor.getXForms();
        // DOMUtil.prettyPrintDOM(hostDoc);
        String value = XPathUtil.evaluateAsString(hostDoc, "//*[@id='showRepeatIndex']/bf:data");
        assertEquals("Repeat Index must be '3'", "3", value);

    }


    protected String getTestCaseURI() {
        return "9.3.1.b.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }
}
