/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

/**
 * Model item tests.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ModelItemPropertiesTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class ModelItemPropertiesTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private ModelItem modelItem;
    private ModelItem parentItem;

    /**
     * Tests default values.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDefaultValues() throws Exception {
        assertEquals(null, this.parentItem.getParent());
        assertEquals(this.parentItem, this.modelItem.getParent());

        assertEquals(true, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());

        assertNotNull(this.modelItem.getDeclarationView());
        assertNotNull(this.modelItem.getLocalUpdateView());
        assertNotNull(this.modelItem.getStateChangeView());
    }

    /**
     * Tests readonly computation.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testReadonlyComputation() throws Exception {
        this.parentItem.getLocalUpdateView().setLocalReadonly(true);
        this.modelItem.getLocalUpdateView().setLocalReadonly(true);
        assertEquals(true, this.modelItem.isValid());
        assertEquals(true, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());

        this.parentItem.getLocalUpdateView().setLocalReadonly(false);
        this.modelItem.getLocalUpdateView().setLocalReadonly(true);
        assertEquals(true, this.modelItem.isValid());
        assertEquals(true, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());

        this.parentItem.getLocalUpdateView().setLocalReadonly(true);
        this.modelItem.getLocalUpdateView().setLocalReadonly(false);
        assertEquals(true, this.modelItem.isValid());
        assertEquals(true, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());

        this.parentItem.getLocalUpdateView().setLocalReadonly(false);
        this.modelItem.getLocalUpdateView().setLocalReadonly(false);
        assertEquals(true, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());
    }

    /**
     * Tests required computation.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testRequiredComputation() throws Exception {
        this.parentItem.getLocalUpdateView().setLocalRequired(true);
        this.modelItem.getLocalUpdateView().setLocalRequired(true);
        assertEquals(true, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(true, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());

        this.parentItem.getLocalUpdateView().setLocalRequired(true);
        this.modelItem.getLocalUpdateView().setLocalRequired(false);
        assertEquals(true, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());

        this.parentItem.getLocalUpdateView().setLocalRequired(false);
        this.modelItem.getLocalUpdateView().setLocalRequired(true);
        assertEquals(true, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(true, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());

        this.parentItem.getLocalUpdateView().setLocalRequired(false);
        this.modelItem.getLocalUpdateView().setLocalRequired(false);
        assertEquals(true, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());
    }

    /**
     * Tests enabled computation.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testEnabledComputation() throws Exception {
        this.parentItem.getLocalUpdateView().setLocalRelevant(true);
        this.modelItem.getLocalUpdateView().setLocalRelevant(true);
        assertEquals(true, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());

        this.parentItem.getLocalUpdateView().setLocalRelevant(false);
        this.modelItem.getLocalUpdateView().setLocalRelevant(true);
        assertEquals(true, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(false, this.modelItem.isRelevant());

        this.parentItem.getLocalUpdateView().setLocalRelevant(true);
        this.modelItem.getLocalUpdateView().setLocalRelevant(false);
        assertEquals(true, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(false, this.modelItem.isRelevant());

        this.parentItem.getLocalUpdateView().setLocalRelevant(false);
        this.modelItem.getLocalUpdateView().setLocalRelevant(false);
        assertEquals(true, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(false, this.modelItem.isRelevant());
    }

    /**
     * Tests valid computation.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidComputation() throws Exception {
        this.modelItem.getLocalUpdateView().setConstraintValid(true);
        this.modelItem.getLocalUpdateView().setDatatypeValid(true);
        assertEquals(true, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());

        this.modelItem.getLocalUpdateView().setConstraintValid(false);
        this.modelItem.getLocalUpdateView().setDatatypeValid(true);
        assertEquals(false, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());

        this.modelItem.getLocalUpdateView().setConstraintValid(true);
        this.modelItem.getLocalUpdateView().setDatatypeValid(false);
        assertEquals(false, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());

        this.modelItem.getLocalUpdateView().setConstraintValid(false);
        this.modelItem.getLocalUpdateView().setDatatypeValid(false);
        assertEquals(false, this.modelItem.isValid());
        assertEquals(false, this.modelItem.isReadonly());
        assertEquals(false, this.modelItem.isRequired());
        assertEquals(true, this.modelItem.isRelevant());
    }
    
    public void testCustomMIP() throws Exception {
    	
    	Map<String, String> customMIPValues = new HashMap<String, String>();
    	customMIPValues.put("diff", "true");
        this.modelItem.getLocalUpdateView().setCustomMIPValues(customMIPValues);
        assertEquals("true", this.modelItem.getLocalUpdateView().getCustomMIPValues().get("diff"));

    	customMIPValues.put("diff", "false");
        this.modelItem.getLocalUpdateView().setCustomMIPValues(customMIPValues);
        assertEquals("false", this.modelItem.getLocalUpdateView().getCustomMIPValues().get("diff"));  
        
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.parentItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        this.modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        this.modelItem.setParent(this.parentItem);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.modelItem.setParent(null);
        this.modelItem = null;
        this.parentItem = null;
    }

}
