// Copyright 2010 betterForm
package de.betterform.xml.xforms.model;

import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;

/**
 * Test cases for the instance implementation.
 *
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class NoInstance_4_2_2_Test extends BetterFormTestCase {
//	static {
//		org.apache.log4j.BasicConfigurator.configure();
//	}

    /**
     * Tests instance initialization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInit() throws Exception {
        assertTrue(evaluateInDefaultContextAsNode("/*[1]/input1") != null);
        assertTrue(evaluateInDefaultContextAsNode("/*[1]/group") != null);
        assertTrue(evaluateInDefaultContextAsNode("/*[1]/group/nested-input1") != null);
    }


    protected String getTestCaseURI() {
        return "4.2.2-NoInstance.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}

// end of class
