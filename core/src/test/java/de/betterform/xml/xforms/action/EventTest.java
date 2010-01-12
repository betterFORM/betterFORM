/* Copyright 2008, 2009 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */

package de.betterform.xml.xforms.action;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.io.InputStream;

/**
 *
 * @version $Id: EventTest.java $
 * @author joern turner
 */
public class EventTest extends BetterFormTestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }



    public void testObserverXFReady() throws Exception {
        assertEquals("5", evaluateInDefaultContextAsString("/number_lists/results/xfReady[1]"));
        assertEquals("10", evaluateInDefaultContextAsString("/number_lists/number_list[1]/number[1]"));
    }

    public void testTargetNoFire() throws Exception {
        assertEquals("3", evaluateInDefaultContextAsString("/number_lists/number_list[1]/number[3]"));
        assertEquals("4", evaluateInDefaultContextAsString("/number_lists/number_list[1]/number[4]"));
        this.processor.setControlValue("input2", "5");
        assertEquals("5", evaluateInDefaultContextAsString("/number_lists/number_list[1]/number[3]"));
        assertEquals("4", evaluateInDefaultContextAsString("/number_lists/number_list[1]/number[4]"));


    }

    public void testTargetFire() throws Exception {
        assertEquals("2", evaluateInDefaultContextAsString("/number_lists/number_list[1]/number[2]"));
        assertEquals("4", evaluateInDefaultContextAsString("/number_lists/number_list[1]/number[4]"));
        this.processor.setControlValue("input1", "5");
        assertEquals("5", evaluateInDefaultContextAsString("/number_lists/number_list[1]/number[2]"));
        assertEquals("10", evaluateInDefaultContextAsString("/number_lists/number_list[1]/number[4]"));


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
        return "events.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }
}