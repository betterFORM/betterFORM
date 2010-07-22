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
public class Delete_10_4_d_Test extends BetterFormTestCase {
//	static {
//		org.apache.log4j.BasicConfigurator.configure();
//	}

    public void test1() throws Exception {
    	assertEquals(2d, evaluateInInstanceAsDouble("Instance_1", "count(/number_lists/number_list[1]/number)"));
        assertEquals("1",  evaluateInInstanceAsString("Instance_1", "string(/number_lists/number_list[1]/number[1])"));
        assertEquals("2",  evaluateInInstanceAsString("Instance_1", "string(/number_lists/number_list[1]/number[2])"));
    }
    
    public void test2() throws Exception {
    	assertEquals(2d, evaluateInInstanceAsDouble("Instance_1", "count(/number_lists/number_list[2]/number)"));
    	assertEquals("4",  evaluateInInstanceAsString("Instance_1", "string(/number_lists/number_list[2]/number[1])"));
    	assertEquals("6",  evaluateInInstanceAsString("Instance_1", "string(/number_lists/number_list[2]/number[2])"));
    }
    
    public void test3() throws Exception {
    	assertEquals(2d, evaluateInInstanceAsDouble("Instance_1", "count(/number_lists/number_list[3]/number)"));
    	assertEquals("8",  evaluateInInstanceAsString("Instance_1", "string(/number_lists/number_list[3]/number[1])"));
    	assertEquals("9",  evaluateInInstanceAsString("Instance_1", "string(/number_lists/number_list[3]/number[2])"));
    }
    
    public void test4() throws Exception {
    	assertEquals(2d, evaluateInInstanceAsDouble("Instance_1", "count(/number_lists/number_list[4]/number)"));
    	assertEquals("10",  evaluateInInstanceAsString("Instance_1", "string(/number_lists/number_list[4]/number[1])"));
    	assertEquals("11",  evaluateInInstanceAsString("Instance_1", "string(/number_lists/number_list[4]/number[2])"));
    }
    
    public void test5() throws Exception {
    	assertEquals(2d, evaluateInInstanceAsDouble("Instance_1", "count(/number_lists/number_list[5]/number)"));
    	assertEquals("13",  evaluateInInstanceAsString("Instance_1", "string(/number_lists/number_list[5]/number[1])"));
    	assertEquals("14",  evaluateInInstanceAsString("Instance_1", "string(/number_lists/number_list[5]/number[2])"));
    }
    
    public void test6() throws Exception {
    	assertEquals(1d, evaluateInInstanceAsDouble("instance_2", "count(/number_lists/number_list/number)"));
    	assertEquals("17",  evaluateInInstanceAsString("instance_2", "string(/number_lists/number_list/number[1])"));
    }
    
    protected String getTestCaseURI() {
        return "delete-10.4.d.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}

// end of class
