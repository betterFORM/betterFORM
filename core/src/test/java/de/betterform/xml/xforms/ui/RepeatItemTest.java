/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.ui;

import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import junit.framework.TestCase;
import org.w3c.dom.Document;

/**
 * Tests the repeat items.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RepeatItemTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class RepeatItemTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Repeat enclosingRepeat;
    private Repeat nestedRepeat1;
    private Repeat nestedRepeat2;
    private Repeat nestedRepeat3;

    /**
     * Tests repeat item selection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIsSelected11() throws Exception {
        this.nestedRepeat1.setIndex(1);

        assertEquals(true, this.enclosingRepeat.getRepeatItem(1).isSelected());
        assertEquals(true, this.nestedRepeat1.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat1.getRepeatItem(2).isSelected());
        assertEquals(false, this.enclosingRepeat.getRepeatItem(2).isSelected());
        assertEquals(false, this.nestedRepeat2.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat2.getRepeatItem(2).isSelected());
        assertEquals(false, this.enclosingRepeat.getRepeatItem(3).isSelected());
        assertEquals(false, this.nestedRepeat3.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat3.getRepeatItem(2).isSelected());
    }

    /**
     * Tests repeat item selection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIsSelected12() throws Exception {
        this.nestedRepeat1.setIndex(2);

        assertEquals(true, this.enclosingRepeat.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat1.getRepeatItem(1).isSelected());
        assertEquals(true, this.nestedRepeat1.getRepeatItem(2).isSelected());
        assertEquals(false, this.enclosingRepeat.getRepeatItem(2).isSelected());
        assertEquals(false, this.nestedRepeat2.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat2.getRepeatItem(2).isSelected());
        assertEquals(false, this.enclosingRepeat.getRepeatItem(3).isSelected());
        assertEquals(false, this.nestedRepeat3.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat3.getRepeatItem(2).isSelected());
    }

    /**
     * Tests repeat item selection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIsSelected21() throws Exception {
        this.nestedRepeat2.setIndex(1);

        assertEquals(false, this.enclosingRepeat.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat1.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat1.getRepeatItem(2).isSelected());
        assertEquals(true, this.enclosingRepeat.getRepeatItem(2).isSelected());
        assertEquals(true, this.nestedRepeat2.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat2.getRepeatItem(2).isSelected());
        assertEquals(false, this.enclosingRepeat.getRepeatItem(3).isSelected());
        assertEquals(false, this.nestedRepeat3.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat3.getRepeatItem(2).isSelected());
    }

    /**
     * Tests repeat item selection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIsSelected22() throws Exception {
        this.nestedRepeat2.setIndex(2);

        assertEquals(false, this.enclosingRepeat.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat1.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat1.getRepeatItem(2).isSelected());
        assertEquals(true, this.enclosingRepeat.getRepeatItem(2).isSelected());
        assertEquals(false, this.nestedRepeat2.getRepeatItem(1).isSelected());
        assertEquals(true, this.nestedRepeat2.getRepeatItem(2).isSelected());
        assertEquals(false, this.enclosingRepeat.getRepeatItem(3).isSelected());
        assertEquals(false, this.nestedRepeat3.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat3.getRepeatItem(2).isSelected());
    }

    /**
     * Tests repeat item selection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIsSelected31() throws Exception {
        this.nestedRepeat3.setIndex(1);

        assertEquals(false, this.enclosingRepeat.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat1.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat1.getRepeatItem(2).isSelected());
        assertEquals(false, this.enclosingRepeat.getRepeatItem(2).isSelected());
        assertEquals(false, this.nestedRepeat2.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat2.getRepeatItem(2).isSelected());
        assertEquals(true, this.enclosingRepeat.getRepeatItem(3).isSelected());
        assertEquals(true, this.nestedRepeat3.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat3.getRepeatItem(2).isSelected());
    }

    /**
     * Tests repeat item selection.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIsSelected32() throws Exception {
        this.nestedRepeat3.setIndex(2);

        assertEquals(false, this.enclosingRepeat.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat1.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat1.getRepeatItem(2).isSelected());
        assertEquals(false, this.enclosingRepeat.getRepeatItem(2).isSelected());
        assertEquals(false, this.nestedRepeat2.getRepeatItem(1).isSelected());
        assertEquals(false, this.nestedRepeat2.getRepeatItem(2).isSelected());
        assertEquals(true, this.enclosingRepeat.getRepeatItem(3).isSelected());
        assertEquals(false, this.nestedRepeat3.getRepeatItem(1).isSelected());
        assertEquals(true, this.nestedRepeat3.getRepeatItem(2).isSelected());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("RepeatItemTest.xhtml"));
        this.xformsProcesssorImpl.init();

        Document host = this.xformsProcesssorImpl.getXForms();
        this.enclosingRepeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "//xf:repeat[bf:data][1]/@id"));
        this.nestedRepeat1 = (Repeat) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:repeat[2][bf:data]/@id"));
        this.nestedRepeat2 = (Repeat) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:repeat[3][bf:data]/@id"));
        this.nestedRepeat3 = (Repeat) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:repeat[4][bf:data]/@id"));
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.enclosingRepeat = null;
        this.nestedRepeat1 = null;
        this.nestedRepeat2 = null;
        this.nestedRepeat3 = null;

        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

}

// end of class
