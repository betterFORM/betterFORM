/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model;

import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.exception.XFormsLinkException;
import junit.framework.TestCase;

/**
 * Test cases for the instance implementation.
 *
 * @author Joern Turner
 */

public class LinkException_4_2_2_Test extends TestCase {
/*
	static {
		org.apache.log4j.BasicConfigurator.configure();
	}
*/

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Instance instance;

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

    /**
     * Tests instance initialization.
     *
     * @throws Exception if any error occurred during the test.
     */
    // TODO: initialization of Actions happens before initialization of instances (see Container.initModels)
    public void testInitFailingSrc() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();

        String path = getClass().getResource("4.2.2LinkException.xhtml").getPath();
        String baseURI = "file://" + path.substring(0, path.lastIndexOf("4.2.2LinkException.xhtml"));

        this.xformsProcesssorImpl.setBaseURI(baseURI);
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("4.2.2LinkException.xhtml"));
        try{
            this.xformsProcesssorImpl.init();
        }catch (XFormsException e){
            assertTrue(e instanceof XFormsLinkException);
        }

    }


}
