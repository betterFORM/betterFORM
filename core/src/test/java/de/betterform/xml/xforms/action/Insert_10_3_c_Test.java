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
public class Insert_10_3_c_Test extends BetterFormTestCase {
//	static {
//		org.apache.log4j.BasicConfigurator.configure();
//	}

    
    public void test1() throws Exception {
    	assertEquals(0d, evaluateInInstanceAsDouble("second", "count(/number_list[2]/number)"));
    }

    public void test2() throws Exception {
    	assertEquals(5d, evaluateInInstanceAsDouble("first", "count(/number_lists/number_list[1]/number)"));
    	assertEquals("1",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[1]/number[1])"));
    	assertEquals("2",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[1]/number[2])"));
    	assertEquals("3",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[1]/number[3])"));
    	assertEquals("0",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[1]/number[4])"));
    	assertEquals("3",  evaluateInInstanceAsString("first", "string(/number_lists/number_list[1]/number[5])"));
    }
    

    protected String getTestCaseURI() {
        return "Insert-10.3.c.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}

// end of class
