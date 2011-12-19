/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model;


import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xforms.exception.XFormsLinkException;

/**
 * Test cases for the instance implementation.
 *
 * @author Tobi Krebs
 */
public class InvalidInstanceTest extends XMLTestBase {
    private XFormsProcessorImpl xformsProcesssorImpl;

    /**
     * Tests instance initialization.
     *
     * @throws Exception if any error occurred during the test.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("InvalidInstanceTest.xhtml"));
    }

    protected String getTestCaseURI() {
        return "InvalidInstanceTest.xhtml";  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void testInvalidInstance() {
        Exception exception = null;
        try {
            this.xformsProcesssorImpl.init();
        } catch (Exception e) {
            exception = e;
        }

        assertNotNull(exception);
        assertTrue(exception instanceof XFormsLinkException);
    }
}

// end of class
