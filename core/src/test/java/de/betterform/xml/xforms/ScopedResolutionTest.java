/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */
package de.betterform.xml.xforms;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.BindingElement;

/**
 * Tests scoped resolution.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ScopedResolutionTest.java 3083 2008-01-21 11:29:21Z joern $
 */
public class ScopedResolutionTest extends TestCase {

//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl = null;

    /**
     * Creates a new scoped resolution test.
     *
     * @param name the test name.
     */
    public ScopedResolutionTest(String name) {
        super(name);
    }

    /**
     * Runs the scoped resolution test.
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
        return new TestSuite(ScopedResolutionTest.class);
    }

    /**
     * Test case 1 for scoped resolution of ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionUI1() throws Exception {
        assertScopedResolution("ui-input-1");
    }

    /**
     * Test case 2 for scoped resolution of ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionUI2() throws Exception {
        assertScopedResolution("ui-input-2");
    }

    /**
     * Test case 3 for scoped resolution of ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionUI3() throws Exception {
        assertScopedResolution("ui-input-3");
    }

    /**
     * Test case 4 for scoped resolution of ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionUI4() throws Exception {
        assertScopedResolution("ui-input-4");
    }

    /**
     * Test case 5 for scoped resolution of ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionUI5() throws Exception {
        assertScopedResolution("ui-input-5");
    }

    /**
     * Test case 6 for scoped resolution of ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionUI6() throws Exception {
        assertScopedResolution("ui-input-6");
    }

    /**
     * Test case 1 for scoped resolution of model bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionModel1() throws Exception {
        assertScopedResolution("model-input-1");
    }

    /**
     * Test case 2 for scoped resolution of model bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionModel2() throws Exception {
        assertScopedResolution("model-input-2");
    }

    /**
     * Test case 3 for scoped resolution of model bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionModel3() throws Exception {
        assertScopedResolution("model-input-3");
    }

    /**
     * Test case 4 for scoped resolution of model bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionModel4() throws Exception {
        assertScopedResolution("model-input-4");
    }

    /**
     * Test case 5 for scoped resolution of model bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionModel5() throws Exception {
        assertScopedResolution("model-input-5");
    }

    /**
     * Test case 6 for scoped resolution of model bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionModel6() throws Exception {
        assertScopedResolution("model-input-6");
    }

    /**
     * Test case 1 for scoped resolution of model vs ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionMixed1() throws Exception {
        assertScopedResolution("mixed-input-1");
    }

    /**
     * Test case 2 for scoped resolution of model vs ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionMixed2() throws Exception {
        assertScopedResolution("mixed-input-2");
    }

    /**
     * Test case 3 for scoped resolution of model vs ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionMixed3() throws Exception {
        assertScopedResolution("mixed-input-3");
    }

    /**
     * Test case 4 for scoped resolution of model vs ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionMixed4() throws Exception {
        assertScopedResolution("mixed-input-4");
    }

    /**
     * Test case 5 for scoped resolution of model vs ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionMixed5() throws Exception {
        assertScopedResolution("mixed-input-5");
    }

    /**
     * Test case 6 for scoped resolution of model vs ui bindings.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testScopedResolutionMixed6() throws Exception {
        assertScopedResolution("mixed-input-6");
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("ScopedResolutionTest.xml"));
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

    private void assertScopedResolution(String controlId) throws XFormsException {
        XFormsElement element = this.xformsProcesssorImpl.getContainer().lookup(controlId);
        assertNotNull("element " + controlId + " not initialized", element);
        assertTrue("element " + controlId + " not bound", element instanceof BindingElement);
        assertTrue("element " + controlId + " bound to wrong instance", ("default-instance").equals(((BindingElement) element).getInstanceId()));
        assertTrue("element " + controlId + " bound to wrong node", ("default-value").equals(((BindingElement) element).getInstanceValue()));
    }

}

// end of class
