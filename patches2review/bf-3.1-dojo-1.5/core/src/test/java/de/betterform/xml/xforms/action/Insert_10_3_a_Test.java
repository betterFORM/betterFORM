/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */
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
public class Insert_10_3_a_Test extends BetterFormTestCase {
//	static {
//		org.apache.log4j.BasicConfigurator.configure();
//	}

    /**
     * Tests instance initialization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextOneNode() throws Exception {
    	assertEquals(2d, evaluateInInstanceAsDouble("second", "count(/number_lists/number_list/number)"));
        assertEquals("0",  evaluateInInstanceAsString("second", "string(/number_lists/number_list/number[1])"));
        assertEquals("0",  evaluateInInstanceAsString("second", "string(/number_lists/number_list/number[2])"));
    }
    
    public void test2() throws Exception {
    	assertEquals(4d, evaluateInInstanceAsDouble("first", "count(/number_lists/number_list[1]/number)"));
        assertEquals("1",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[1]/number[1])"));
        assertEquals("2",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[1]/number[2])"));
        assertEquals("3",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[1]/number[3])"));
        assertEquals("3",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[1]/number[4])"));
    }

    public void test3() throws Exception {
    	assertEquals(6d, evaluateInInstanceAsDouble("first", "count(/number_lists/number_list[2]/number)"));
    	assertEquals("4",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[2]/number[1])"));
    	assertEquals("5",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[2]/number[2])"));
    	assertEquals("6",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[2]/number[3])"));
    	assertEquals("6",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[2]/number[4])"));
    	assertEquals("6",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[2]/number[5])"));
    	assertEquals("6",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[2]/number[6])"));
    }
    

    protected String getTestCaseURI() {
        return "insert-10.3.a.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}

// end of class
