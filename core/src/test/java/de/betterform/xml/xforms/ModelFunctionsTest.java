/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms;

import de.betterform.xml.xforms.exception.XFormsComputeException;
import org.w3c.dom.Document;

/**
 * Test cases for extensions functions declared on model.
 *
 * @author Joern Turner
 * @version $Id: ModelFunctionsTest.java 3083 2008-01-21 11:29:21Z joern $
 */
public class ModelFunctionsTest extends XMLTestBase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }


    /**
     * test the positive case - functions are found
     */
    public void testCheckExtensionFunctions() {
        Exception exception = null;
        
        try {
            XFormsProcessorImpl xformsProcesssorImpl = new XFormsProcessorImpl();
            Document form = getXmlResource("ModelTest2.xhtml");
            xformsProcesssorImpl.setXForms(form);
            xformsProcesssorImpl.init();
        } catch (Exception e) {
            exception = e;
        }

        assertNull(exception);
    }

    /**
     * functions do not exist.
     */
    public void testCheckExtensionFunctions2() {
        XFormsProcessorImpl xformsProcesssorImpl = new XFormsProcessorImpl();
        Exception exception = null;
        try {
            Document form = getXmlResource("ModelTest3.xhtml");
            xformsProcesssorImpl.setXForms(form);
            xformsProcesssorImpl.init();
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);
        assertTrue(exception instanceof XFormsComputeException);

    }

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

}

// end of class
