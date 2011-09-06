/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.dom;

import junit.framework.TestCase;

import java.util.List;

/**
 * Test cases for the DOM whitespace utility.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: DOMWhitespaceTest.java 2797 2007-08-10 12:45:24Z joern $
 */
public class DOMWhitespaceTest extends TestCase {

    /**
     * Test case 1 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText1() throws Exception {
        List list = DOMWhitespace.denormalizeText(null);
        assertNull("list should be null", list);
    }

    /**
     * Test case 2 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText2() throws Exception {
        List list = DOMWhitespace.denormalizeText("");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 1);
        assertTrue("wrong list item at index 0", list.get(0).equals(""));
    }

    /**
     * Test case 3 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText3() throws Exception {
        List list = DOMWhitespace.denormalizeText(" \n\t");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 1);
        assertTrue("wrong list item at index 0", list.get(0).equals(" \n\t"));
    }

    /**
     * Test case 4 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText4() throws Exception {
        List list = DOMWhitespace.denormalizeText("this is text");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 1);
        assertTrue("wrong list item at index 0", list.get(0).equals("this is text"));
    }

    /**
     * Test case 5 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText5() throws Exception {
        List list = DOMWhitespace.denormalizeText("this is text ");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 2);
        assertTrue("wrong list item at index 0", list.get(0).equals("this is text"));
        assertTrue("wrong list item at index 1", list.get(1).equals(" "));
    }

    /**
     * Test case 6 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText6() throws Exception {
        List list = DOMWhitespace.denormalizeText(" this is text");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 2);
        assertTrue("wrong list item at index 0", list.get(0).equals(" "));
        assertTrue("wrong list item at index 1", list.get(1).equals("this is text"));

    }

    /**
     * Test case 7 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText7() throws Exception {
        List list = DOMWhitespace.denormalizeText(" this is text ");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 3);
        assertTrue("wrong list item at index 0", list.get(0).equals(" "));
        assertTrue("wrong list item at index 1", list.get(1).equals("this is text"));
        assertTrue("wrong list item at index 2", list.get(2).equals(" "));
    }

    /**
     * Test case 8 for text denormalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDenormalizeText8() throws Exception {
        List list = DOMWhitespace.denormalizeText("this is text  this is text");
        assertNotNull("no list", list);
        assertTrue("wrong list size", list.size() == 3);
        assertTrue("wrong list item at index 0", list.get(0).equals("this is text"));
        assertTrue("wrong list item at index 1", list.get(1).equals("  "));
        assertTrue("wrong list item at index 2", list.get(2).equals("this is text"));
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
