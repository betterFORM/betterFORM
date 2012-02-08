/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model;


import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;

// end of class

/**
 * Test cases for the instance implementation.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: InstanceTest.java 3264 2008-07-16 12:26:54Z joern $
 */
public class EmptyModelTest extends BetterFormTestCase {
     private Model model;

    /**
     * Tests instance initialization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInit() throws Exception {
        this.model = ((XFormsProcessorImpl)processor).getContainer().getDefaultModel();
        assertNotNull(this.model);
        /*
        there's no default instance for this form as there are only empty models and no UI binding elements.
        Therefore no instance will be created.
        */
        Instance defaultInstance = this.model.getDefaultInstance();
        assertNull(defaultInstance);
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
        return "3.3.1.a1.xhtml";  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}