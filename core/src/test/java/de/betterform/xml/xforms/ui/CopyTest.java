/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.ui;

import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import org.w3c.dom.Document;

/**
 * Test cases for extensions functions declared on model.
 *
 * @author Joern Turner
 * @version $Id: CopyTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class CopyTest extends XMLTestBase {
/*    static {
        org.apache.log4j.BasicConfigurator.configure();
    }*/

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
    }

    public void testCopy() throws Exception{
        Document document = getXmlResource("CopyTest.xhtml");

        XFormsProcessorImpl xformsProcesssorImpl = new XFormsProcessorImpl();
        xformsProcesssorImpl.setXForms(document);
        xformsProcesssorImpl.init();
        
        /* 1) check that the initial instance is correct */
        Document expected = getXmlResource("CopyExpected.xml");
        assertTrue(getComparator().compare(expected, xformsProcesssorImpl.getContainer().getModel("form_data").getDefaultInstance().getInstanceDocument()));
        
        /* 1.1) The initial instance node should be selected */
        XFormsElement xfe = xformsProcesssorImpl.getContainer().lookup("C22");
        assertTrue(xfe instanceof Item);
        assertTrue(((Item)xfe).isSelected());
        
        /* 2) change the selection to two other items */
        //pass the id of the xforms:copy as the value for the selector
        xformsProcesssorImpl.setControlValue("C4","C14 C21");

        //de.betterform.xml.dom.DOMUtil.prettyPrintDOM(processor.getContainer().getDocument());
        //de.betterform.xml.dom.DOMUtil.prettyPrintDOM(processor.getContainer().getModel("form_data").getDefaultInstance().getInstanceDocument());

        /* 2.1) check the first selected item is actually selected */
        xfe = xformsProcesssorImpl.getContainer().lookup("C8");
        assertTrue(xfe instanceof Item);
        assertTrue(((Item)xfe).isSelected());


        
        /* 2.2) check the second selected item is actually selected */
        xfe = xformsProcesssorImpl.getContainer().lookup("C15");
        assertTrue(xfe instanceof Item);
        assertTrue(((Item)xfe).isSelected());
        
        /* 2.3) check that the instance has correctly been updated with the new selections */
        expected = getXmlResource("CopyExpected2.xml");
        assertTrue(getComparator().compare(expected, xformsProcesssorImpl.getContainer().getModel("form_data").getDefaultInstance().getInstanceDocument()));
        
        
        //de.betterform.xml.dom.DOMUtil.prettyPrintDOM(processor.getContainer().getDocument());
        //de.betterform.xml.dom.DOMUtil.prettyPrintDOM(processor.getContainer().getModel("form_data").getDefaultInstance().getInstanceDocument());
    }
}

// end of class
