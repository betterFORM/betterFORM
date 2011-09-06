/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.dom;

import junit.framework.TestCase;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * The test suite for the DOM Comparator implementation.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: DOMComparatorTest.java 2797 2007-08-10 12:45:24Z joern $
 */
public class DOMComparatorTest extends TestCase {

    private DOMComparator comparator = null;
    private Document testDocument1 = null;
    private Document testDocument2 = null;
    private Document testDocument3 = null;
    private Document testDocument4 = null;
    private Document testDocument5 = null;

    /**
     * Tests attribute order comparison.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testAttributeOrder() throws Exception {
        assertEquals(true, this.comparator.compare(this.testDocument1, this.testDocument2));
    }

    /**
     * Tests comments comparison.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCompareComments() throws Exception {
        this.comparator.setIgnoreComments(false);

        assertEquals(false, this.comparator.compare(this.testDocument1, this.testDocument5));
    }

    /**
     * Tests whitespace comparison.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCompareWhitespace() throws Exception {
        this.comparator.setIgnoreWhitespace(false);

        assertEquals(false, this.comparator.compare(this.testDocument1, this.testDocument4));
    }

    /**
     * Tests document comparison.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDifferentDocuments() throws Exception {
        assertEquals(false, this.comparator.compare(this.testDocument2, this.testDocument3));
    }

    /**
     * Tests comments comparison.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIgnoreComments() throws Exception {
        this.comparator.setIgnoreComments(true);
        assertEquals(true, this.comparator.compare(this.testDocument1, this.testDocument5));
    }

    /**
     * Tests whitespace comparison.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIgnoreWhitespace() throws Exception {
        this.comparator.setIgnoreWhitespace(true);
        assertEquals(true, this.comparator.compare(this.testDocument1, this.testDocument4));
    }

    /**
     * Tests document comparison.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSameDocument() throws Exception {
        assertEquals(true, this.comparator.compare(this.testDocument1, this.testDocument1));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setCoalescing(false);
        factory.setExpandEntityReferences(false);
        factory.setIgnoringComments(false);
        factory.setIgnoringElementContentWhitespace(false);
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();

        this.testDocument1 = builder.parse(getClass().getResourceAsStream("DOMComparatorTest1.xml"));
        this.testDocument2 = builder.parse(getClass().getResourceAsStream("DOMComparatorTest2.xml"));
        this.testDocument3 = builder.parse(getClass().getResourceAsStream("DOMComparatorTest3.xml"));
        this.testDocument4 = builder.parse(getClass().getResourceAsStream("DOMComparatorTest4.xml"));
        this.testDocument5 = builder.parse(getClass().getResourceAsStream("DOMComparatorTest5.xml"));

        this.comparator = new DOMComparator();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.comparator = null;

        this.testDocument1 = null;
        this.testDocument2 = null;
        this.testDocument3 = null;
        this.testDocument4 = null;
        this.testDocument5 = null;
    }

}
