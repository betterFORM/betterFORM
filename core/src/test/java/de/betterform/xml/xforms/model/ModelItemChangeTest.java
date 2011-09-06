/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model;

import junit.framework.TestCase;
import de.betterform.xml.xforms.model.XercesElementImpl;
import de.betterform.xml.xforms.model.XercesNodeImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Model item tests.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ModelItemChangeTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class ModelItemChangeTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private ModelItem modelItem;
    private ModelItem parentItem;

    /**
     * Tests no change.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testNoChange() throws Exception {
        assertEquals(false, this.parentItem.getStateChangeView().hasValueChanged());
        assertEquals(false, this.parentItem.getStateChangeView().hasValidChanged());
        assertEquals(false, this.parentItem.getStateChangeView().hasReadonlyChanged());
        assertEquals(false, this.parentItem.getStateChangeView().hasRequiredChanged());
        assertEquals(false, this.parentItem.getStateChangeView().hasEnabledChanged());

        assertEquals(false, this.modelItem.getStateChangeView().hasValueChanged());
        assertEquals(false, this.modelItem.getStateChangeView().hasValidChanged());
        assertEquals(false, this.modelItem.getStateChangeView().hasReadonlyChanged());
        assertEquals(false, this.modelItem.getStateChangeView().hasRequiredChanged());
        assertEquals(false, this.modelItem.getStateChangeView().hasEnabledChanged());
    }

    /**
     * Tests valid changes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidChange() throws Exception {
        assertValidChange(true, true, true, true, false);
        assertValidChange(true, true, true, false, true);
        assertValidChange(true, false, true, true, true);
        assertValidChange(true, false, true, false, true);
        assertValidChange(true, true, false, true, true);
        assertValidChange(true, true, false, false, false);
        assertValidChange(true, false, false, true, false);
        assertValidChange(true, false, false, false, false);
        assertValidChange(false, true, true, true, true);
        assertValidChange(false, true, true, false, false);
        assertValidChange(false, false, true, true, false);
        assertValidChange(false, false, true, false, false);
        assertValidChange(false, true, false, true, true);
        assertValidChange(false, true, false, false, false);
        assertValidChange(false, false, false, true, false);
        assertValidChange(false, false, false, false, false);
    }

    /**
     * Tests readonly changes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testReadonlyChange() throws Exception {
        assertReadonlyChange(true, true, true, true, false);
        assertReadonlyChange(true, true, true, false, false);
        assertReadonlyChange(true, false, true, true, false);
        assertReadonlyChange(true, false, true, false, true);
        assertReadonlyChange(true, true, false, true, false);
        assertReadonlyChange(true, true, false, false, false);
        assertReadonlyChange(true, false, false, true, false);
        assertReadonlyChange(true, false, false, false, true);
        assertReadonlyChange(false, true, true, true, false);
        assertReadonlyChange(false, true, true, false, false);
        assertReadonlyChange(false, false, true, true, false);
        assertReadonlyChange(false, false, true, false, true);
        assertReadonlyChange(false, true, false, true, true);
        assertReadonlyChange(false, true, false, false, true);
        assertReadonlyChange(false, false, false, true, true);
        assertReadonlyChange(false, false, false, false, false);
    }

    /**
     * Tests required changes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testRequiredChange() throws Exception {
        assertRequiredChange(true, true, false);
        assertRequiredChange(true, false, true);
        assertRequiredChange(false, true, true);
        assertRequiredChange(false, false, false);
    }

    /**
     * Tests enabled changes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testEnabledChange() throws Exception {
        assertEnabledChange(true, true, true, true, false);
        assertEnabledChange(true, true, true, false, true);
        assertEnabledChange(true, false, true, true, true);
        assertEnabledChange(true, false, true, false, true);
        assertEnabledChange(true, true, false, true, true);
        assertEnabledChange(true, true, false, false, false);
        assertEnabledChange(true, false, false, true, false);
        assertEnabledChange(true, false, false, false, false);
        assertEnabledChange(false, true, true, true, true);
        assertEnabledChange(false, true, true, false, false);
        assertEnabledChange(false, false, true, true, false);
        assertEnabledChange(false, false, true, false, false);
        assertEnabledChange(false, true, false, true, true);
        assertEnabledChange(false, true, false, false, false);
        assertEnabledChange(false, false, false, true, false);
        assertEnabledChange(false, false, false, false, false);
    }

    /**
     * Tests value change.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValueChange() throws Exception {
        this.parentItem.setValue("");
        assertEquals(false, this.parentItem.getStateChangeView().hasValueChanged());
        this.parentItem.setValue("test");
        assertEquals(true, this.parentItem.getStateChangeView().hasValueChanged());

        this.modelItem.setValue("");
        assertEquals(false, this.modelItem.getStateChangeView().hasValueChanged());
        this.modelItem.setValue("test");
        assertEquals(true, this.modelItem.getStateChangeView().hasValueChanged());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        Document document = factory.newDocumentBuilder().newDocument();
        Element element = document.createElement("element");
        document.appendChild(element);
        Attr attr = document.createAttribute("attribute");
        element.setAttributeNode(attr);

        this.parentItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        this.parentItem.setNode(element);

        this.modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        this.modelItem.setNode(attr);
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

    private void assertValidChange(boolean constraintBefore, boolean constraintAfter, boolean datatypeBefore, boolean datatypeAfter, boolean expected) {
        this.modelItem.getLocalUpdateView().setConstraintValid(constraintBefore);
        this.modelItem.getLocalUpdateView().setDatatypeValid(datatypeBefore);
        this.modelItem.getStateChangeView().reset();

        this.modelItem.getLocalUpdateView().setConstraintValid(constraintAfter);
        this.modelItem.getLocalUpdateView().setDatatypeValid(datatypeAfter);
        assertEquals(expected, this.modelItem.getStateChangeView().hasValidChanged());
    }

    private void assertReadonlyChange(boolean localBefore, boolean localAfter, boolean parentBefore, boolean parentAfter, boolean expected) {
        this.parentItem.getLocalUpdateView().setLocalReadonly(parentBefore);
        this.parentItem.getStateChangeView().reset();
        this.modelItem.getLocalUpdateView().setLocalReadonly(localBefore);
        this.modelItem.getStateChangeView().reset();

        this.parentItem.getLocalUpdateView().setLocalReadonly(parentAfter);
        this.modelItem.getLocalUpdateView().setLocalReadonly(localAfter);
        assertEquals(expected, this.modelItem.getStateChangeView().hasReadonlyChanged());
    }

    private void assertRequiredChange(boolean localBefore, boolean localAfter, boolean expected) {
        this.modelItem.getLocalUpdateView().setLocalRequired(localBefore);
        this.modelItem.getStateChangeView().reset();

        this.modelItem.getLocalUpdateView().setLocalRequired(localAfter);
        assertEquals(expected, this.modelItem.getStateChangeView().hasRequiredChanged());
    }

    private void assertEnabledChange(boolean localBefore, boolean localAfter, boolean parentBefore, boolean parentAfter, boolean expected) {
        this.parentItem.getLocalUpdateView().setLocalRelevant(parentBefore);
        this.parentItem.getStateChangeView().reset();
        this.modelItem.getLocalUpdateView().setLocalRelevant(localBefore);
        this.modelItem.getStateChangeView().reset();

        this.parentItem.getLocalUpdateView().setLocalRelevant(parentAfter);
        this.modelItem.getLocalUpdateView().setLocalRelevant(localAfter);
        assertEquals(expected, this.modelItem.getStateChangeView().hasEnabledChanged());
    }

}
