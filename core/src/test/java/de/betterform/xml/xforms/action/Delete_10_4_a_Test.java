// Copyright 2010 betterForm
package de.betterform.xml.xforms.action;

import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;

import java.io.InputStream;

/**
 * Test cases for the instance implementation.
 *
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class Delete_10_4_a_Test extends BetterFormTestCase {
//	static {
//		org.apache.log4j.BasicConfigurator.configure();
//	}

    /**
     * Tests instance initialization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void test1() throws Exception {
    	assertEquals(1d, evaluateInInstanceAsDouble("second", "count(/number_lists/number_list/number)"));
        assertEquals("10",  evaluateInInstanceAsString("second", "string(/number_lists/number_list/number[1])"));
    }
    
    public void test2() throws Exception {
    	assertEquals(1d, evaluateInInstanceAsDouble("first", "count(/number_lists/number_list[2]/number)"));
    	assertEquals("4",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[2]/number[1])"));
    }

    public void test3() throws Exception {
    	assertEquals(2d, evaluateInInstanceAsDouble("first", "count(/number_lists/number_list[1]/number)"));
    	assertEquals("1",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[1]/number[1])"));
    	assertEquals("2",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[1]/number[2])"));
    }
    

    protected String getTestCaseURI() {
        return "delete-10.4.a.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}

// end of class
