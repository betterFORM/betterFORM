/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;

public class BecomingRelevantTest extends BetterFormTestCase {

    protected void setUp() throws Exception {
         super.setUp();
    }

    public void testInitialRelevance() throws Exception{
        // dump(this.processor.getXForms());

        assertEquals("true", evaluateInDefaultContextAsString("/person-name/enabled"));
        assertEquals("true", evaluateInDefaultContextAsString("/person-name/changed"));
        assertEquals("true", evaluateInDefaultContextAsString("/person-name/valid"));
        assertEquals("true", evaluateInDefaultContextAsString("/person-name/readwrite"));
        assertEquals("true", evaluateInDefaultContextAsString("/person-name/changed"));

    }


    protected String getTestCaseURI() {
        return "8.1.1.b.mod.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;  
    }
}
