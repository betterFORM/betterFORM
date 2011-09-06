/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.dom.DOMUtil;

public class RepeatEmptyTest extends BetterFormTestCase {


    public void testEmptyRepeat() throws Exception {
            assertNotNull(this.processor.getXForms());
            this.processor.dispatch("insert", DOMEventNames.ACTIVATE);
            // DOMUtil.prettyPrintDOM(this.processor.getXForms());
        }

    protected String getTestCaseURI() {
        return "RepeatEmptyTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;  
    }
}
