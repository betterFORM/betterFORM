/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms;

import de.betterform.xml.xforms.exception.XFormsException;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests context model resolution.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ContextModelResolutionTest.java 3083 2008-01-21 11:29:21Z joern $
 */
public class ContextModelResolutionTest extends TestCase {

//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl = null;

    /**
     * Creates a new context model test.
     *
     * @param name the test name.
     */
    public ContextModelResolutionTest(String name) {
        super(name);
    }

    /**
     * Runs the context model test.
     *
     * @param args arguments are ignored.
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Returns a test suite.
     *
     * @return a test suite.
     */
    public static Test suite() {
        return new TestSuite(ContextModelResolutionTest.class);
    }

    /**
     * Test case 1 for context model resolution of ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionUI1() throws Exception {
        assertContextModelResolution("ui-group-1", "default-model");
    }

    /**
     * Test case 2 for context model resolution of ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionUI2() throws Exception {
        assertContextModelResolution("ui-group-2", "additional-model");
    }

    /**
     * Test case 3 for context model resolution of ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionUI3() throws Exception {
        assertContextModelResolution("ui-group-3", "additional-model");
    }

    /**
     * Test case 4 for context model resolution of ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionUI4() throws Exception {
        assertContextModelResolution("ui-group-4", "default-model");
    }

    /**
     * Test case 5 for context model resolution of ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionUI5() throws Exception {
        assertContextModelResolution("ui-input-1", "default-model");
    }

    /**
     * Test case 6 for context model resolution of ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionUI6() throws Exception {
        assertContextModelResolution("ui-input-2", "additional-model");
    }

    /**
     * Test case 1 for context model resolution of model bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionModel1() throws Exception {
        assertContextModelResolution("model-group-1", "default-model");
    }

    /**
     * Test case 2 for context model resolution of model bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionModel2() throws Exception {
        assertContextModelResolution("model-group-2", "additional-model");
    }

    /**
     * Test case 3 for context model resolution of model bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionModel3() throws Exception {
        assertContextModelResolution("model-group-3", "additional-model");
    }

    /**
     * Test case 4 for context model resolution of model bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionModel4() throws Exception {
        assertContextModelResolution("model-group-4", "default-model");
    }

    /**
     * Test case 5 for context model resolution of model bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionModel5() throws Exception {
        assertContextModelResolution("model-input-1", "default-model");
    }

    /**
     * Test case 6 for context model resolution of model bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionModel6() throws Exception {
        assertContextModelResolution("model-input-2", "additional-model");
    }

    /**
     * Test case 1 for context model resolution of model vs ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionMixed1() throws Exception {
        assertContextModelResolution("mixed-group-1", "default-model");
    }

    /**
     * Test case 2 for context model resolution of model vs ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionMixed2() throws Exception {
        assertContextModelResolution("mixed-group-2", "additional-model");
    }

    /**
     * Test case 3 for context model resolution of model vs ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionMixed3() throws Exception {
        assertContextModelResolution("mixed-group-3", "additional-model");
    }

    /**
     * Test case 4 for context model resolution of model vs ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionMixed4() throws Exception {
        assertContextModelResolution("mixed-group-4", "default-model");
    }

    /**
     * Test case 5 for context model resolution of model vs ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionMixed5() throws Exception {
        assertContextModelResolution("mixed-input-1", "default-model");
    }

    /**
     * Test case 6 for context model resolution of model vs ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextModelResolutionMixed6() throws Exception {
        assertContextModelResolution("mixed-input-2", "additional-model");
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("ContextModelResolutionTest.xml"));
        this.xformsProcesssorImpl.init();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

    private void assertContextModelResolution(String controlId, String modelId) throws XFormsException {
        XFormsElement element = this.xformsProcesssorImpl.getContainer().lookup(controlId);
        assertNotNull("element " + controlId + " not initialized", element);
        assertTrue("element " + controlId + " bound to wrong model", modelId.equals(element.getModel().getId()));
    }

}

// end of class
