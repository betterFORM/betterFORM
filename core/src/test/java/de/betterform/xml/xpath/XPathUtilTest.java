/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xpath;

import de.betterform.xml.xforms.XMLTestBase;

import java.util.Arrays;

/**
 * Unit tests for XPath utility.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XPathUtilTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class XPathUtilTest extends XMLTestBase {

    /**
     * Tests getFirstPart().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetFirstPart() throws Exception {
        assertEquals(null, XPathUtil.getFirstPart(null));
        assertEquals("", XPathUtil.getFirstPart(""));
        assertEquals("a", XPathUtil.getFirstPart("a"));
        assertEquals("a", XPathUtil.getFirstPart("/a"));
        assertEquals("a", XPathUtil.getFirstPart("/a/"));
        assertEquals("a", XPathUtil.getFirstPart("a/b"));
        assertEquals("a", XPathUtil.getFirstPart("/a/b"));
        assertEquals("instance('i')", XPathUtil.getFirstPart("instance('i')/a"));
        assertEquals("a[.=instance('i')/b]", XPathUtil.getFirstPart("a[.=instance('i')/b]"));
    }

    /**
     * Tests getNodesetAndPredicates().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetNodesetAndPredicates() throws Exception {
        assertArrayEquals(null, XPathUtil.getNodesetAndPredicates(null));
        assertArrayEquals(new String[]{""}, XPathUtil.getNodesetAndPredicates(""));
        assertArrayEquals(new String[]{"a"}, XPathUtil.getNodesetAndPredicates("a"));
        assertArrayEquals(new String[]{"/a"}, XPathUtil.getNodesetAndPredicates("/a"));
        assertArrayEquals(new String[]{"/a", "p"}, XPathUtil.getNodesetAndPredicates("/a[p]"));
        assertArrayEquals(new String[]{"/a", "p", "q"}, XPathUtil.getNodesetAndPredicates("/a[p][q]"));
        assertArrayEquals(new String[]{"/a/b", "p"}, XPathUtil.getNodesetAndPredicates("/a/b[p]"));
        assertArrayEquals(new String[]{"/a/b", "p", "q"}, XPathUtil.getNodesetAndPredicates("/a/b[p][q]"));
        assertArrayEquals(new String[]{"/a[1]/b[2]/c", "3"}, XPathUtil.getNodesetAndPredicates("/a[1]/b[2]/c[3]"));
    }

    /**
     * Tests hasInstanceFunction().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testHasInstanceFunction() throws Exception {
        assertEquals(false, XPathUtil.hasInstanceFunction(null));
        assertEquals(false, XPathUtil.hasInstanceFunction(""));
        assertEquals(false, XPathUtil.hasInstanceFunction("a"));
        assertEquals(false, XPathUtil.hasInstanceFunction("a[.=instance('i')/b]"));
        assertEquals(true, XPathUtil.hasInstanceFunction("instance('i')"));
        assertEquals(true, XPathUtil.hasInstanceFunction("instance('i')/a"));
        assertEquals(true, XPathUtil.hasInstanceFunction("instance(/b/c)"));
    }

    /**
     * Tests getInstanceParameter().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetInstanceParameter() throws Exception {
        assertEquals(null, XPathUtil.getInstanceParameter(null));
        assertEquals(null, XPathUtil.getInstanceParameter(""));
        assertEquals(null, XPathUtil.getInstanceParameter("a"));
        assertEquals(null, XPathUtil.getInstanceParameter("a[.=instance('i')/b]"));
        assertEquals("'i'", XPathUtil.getInstanceParameter("instance('i')"));
        assertEquals("'i'", XPathUtil.getInstanceParameter("instance('i')/a"));
        assertEquals("/b/c", XPathUtil.getInstanceParameter("instance(/b/c)"));
    }

    /**
     * Tests isAbsolutePath().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIsAbsolutePath() throws Exception {
        assertEquals(false, XPathUtil.isAbsolutePath(null));
        assertEquals(false, XPathUtil.isAbsolutePath(""));
        assertEquals(false, XPathUtil.isAbsolutePath("a"));
        assertEquals(true, XPathUtil.isAbsolutePath("/a"));
        assertEquals(true, XPathUtil.isAbsolutePath("//a"));
        assertEquals(true, XPathUtil.isAbsolutePath("instance('i')"));
        assertEquals(false, XPathUtil.isAbsolutePath("a[.=instance('i')/b]"));
    }

    /**
     * Tests isDotReference().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIsDotReference() throws Exception {
        assertEquals(false, XPathUtil.isDotReference(null));
        assertEquals(false, XPathUtil.isDotReference(""));
        assertEquals(false, XPathUtil.isDotReference("a"));
        assertEquals(true, XPathUtil.isDotReference("."));
        assertEquals(false, XPathUtil.isDotReference("/."));
        assertEquals(false, XPathUtil.isDotReference("./"));
    }

    /**
     * Tests stripSelfReference().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testStripSelfReference() throws Exception {
        assertEquals(null, XPathUtil.stripSelfReference(null));
        assertEquals("", XPathUtil.stripSelfReference(""));
        assertEquals("a", XPathUtil.stripSelfReference("a"));
        assertEquals("", XPathUtil.stripSelfReference("./"));
        assertEquals("a", XPathUtil.stripSelfReference("./a"));
        assertEquals("a/./b", XPathUtil.stripSelfReference("a/./b"));
    }

    /**
     * Tests splitPathExpr().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSplitPathExpr() throws Exception {
        assertArrayEquals(null, XPathUtil.splitPathExpr(null));
        assertArrayEquals(new String[0], XPathUtil.splitPathExpr(""));

        assertArrayEquals(new String[]{"a", "b", "c"}, XPathUtil.splitPathExpr("a/b/c"));
        assertArrayEquals(new String[]{"", "a", "b", "c"}, XPathUtil.splitPathExpr("/a/b/c"));
        assertArrayEquals(new String[]{"", "a", "b", "c"}, XPathUtil.splitPathExpr("/a/b/c/"));
        assertArrayEquals(new String[]{"", "", "a", "b", "c"}, XPathUtil.splitPathExpr("//a/b/c"));
        assertArrayEquals(new String[]{"", "", "a", "b", "c"}, XPathUtil.splitPathExpr("//a/b/c/"));

        assertArrayEquals(new String[]{"a", "n:b", "c"}, XPathUtil.splitPathExpr("a/n:b/c"));
        assertArrayEquals(new String[]{"a", "n:b", "c[p]"}, XPathUtil.splitPathExpr("a/n:b/c[p]"));
        assertArrayEquals(new String[]{"a", "n:b", "c[d/e]"}, XPathUtil.splitPathExpr("a/n:b/c[d/e]"));
        assertArrayEquals(new String[]{"a", "n:b[d/n:e]", "c"}, XPathUtil.splitPathExpr("a/n:b[d/n:e]/c"));
    }

    /**
     * Tests splitPathExpr().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSplitPathExprCount() throws Exception {
        assertArrayEquals(null, XPathUtil.splitPathExpr(null, 4711));
        assertArrayEquals(new String[0], XPathUtil.splitPathExpr("", 4711));

        assertArrayEquals(new String[]{"a", "b", "c"}, XPathUtil.splitPathExpr("a/b/c", -23));
        assertArrayEquals(new String[]{"a", "b", "c"}, XPathUtil.splitPathExpr("a/b/c", 0));
        assertArrayEquals(new String[]{"a"}, XPathUtil.splitPathExpr("a/b/c", 1));
        assertArrayEquals(new String[]{"a", "b"}, XPathUtil.splitPathExpr("a/b/c", 2));
        assertArrayEquals(new String[]{"a", "b", "c"}, XPathUtil.splitPathExpr("a/b/c", 3));
        assertArrayEquals(new String[]{"a", "b", "c"}, XPathUtil.splitPathExpr("a/b/c", 4711));
    }

    /**
     * Tests joinPathExpr().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testJoinPathExpr() throws Exception {
        assertEquals(null, XPathUtil.joinPathExpr(null));
        assertEquals("", XPathUtil.joinPathExpr(new String[0]));

        assertEquals("a/b/c", XPathUtil.joinPathExpr(new String[]{"a", "b", "c"}));
        assertEquals("/a/b/c", XPathUtil.joinPathExpr(new String[]{"", "a", "b", "c"}));
        assertEquals("/a/b/c/", XPathUtil.joinPathExpr(new String[]{"", "a", "b", "c", ""}));
        assertEquals("//a/b/c", XPathUtil.joinPathExpr(new String[]{"", "", "a", "b", "c"}));
        assertEquals("//a/b/c/", XPathUtil.joinPathExpr(new String[]{"", "", "a", "b", "c", ""}));

        assertEquals("a/n:b/c", XPathUtil.joinPathExpr(new String[]{"a", "n:b", "c"}));
        assertEquals("a/n:b/c[p]", XPathUtil.joinPathExpr(new String[]{"a", "n:b", "c[p]"}));
        assertEquals("a/n:b/c[d/e]", XPathUtil.joinPathExpr(new String[]{"a", "n:b", "c[d/e]"}));
        assertEquals("a/n:b[d/n:e]/c", XPathUtil.joinPathExpr(new String[]{"a", "n:b[d/n:e]", "c"}));
    }

    /**
     * Tests joinPathExpr().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testJoinPathExprStartEnd() throws Exception {
        assertEquals(null, XPathUtil.joinPathExpr(null, 0, 4711));
        assertEquals(null, XPathUtil.joinPathExpr(null, 4711, 0));
        assertEquals("", XPathUtil.joinPathExpr(new String[0], 0, 0));

        assertEquals("a/b/c", XPathUtil.joinPathExpr(new String[]{"a", "b", "c"}, 0, 3));
        assertEquals("/a/b/c", XPathUtil.joinPathExpr(new String[]{"", "a", "b", "c"}, 0, 4));
        assertEquals("a/b/c", XPathUtil.joinPathExpr(new String[]{"", "a", "b", "c"}, 1, 4));
    }

    /**
     * Tests splitStep().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSplitStep() throws Exception {
        assertArrayEquals(null, XPathUtil.splitStep(null));
        assertArrayEquals(new String[0], XPathUtil.splitStep(""));

        assertArrayEquals(new String[]{"a"}, XPathUtil.splitStep("a"));
        assertArrayEquals(new String[]{"a", "p"}, XPathUtil.splitStep("a[p]"));
        assertArrayEquals(new String[]{"a", "p", "q"}, XPathUtil.splitStep("a[p][q]"));
        assertArrayEquals(new String[]{"child::a", "p", "q"}, XPathUtil.splitStep("child::a[p][q]"));
        assertArrayEquals(new String[]{"a", "b[p]", "c/d[q]"}, XPathUtil.splitStep("a[b[p]][c/d[q]]"));
        assertArrayEquals(new String[]{"a", "f(b)", "g(c/d[q])"}, XPathUtil.splitStep("a[f(b)][g(c/d[q])]"));
    }

    /**
     * Tests joinStep().
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testJoinStep() throws Exception {
        assertEquals(null, XPathUtil.joinStep(null));
        assertEquals("", XPathUtil.joinStep(new String[0]));

        assertEquals("a", XPathUtil.joinStep(new String[]{"a"}));
        assertEquals("a[p]", XPathUtil.joinStep(new String[]{"a", "p"}));
        assertEquals("a[p][q]", XPathUtil.joinStep(new String[]{"a", "p", "q"}));
        assertEquals("child::a[p][q]", XPathUtil.joinStep(new String[]{"child::a", "p", "q"}));
        assertEquals("a[b[p]][c/d[q]]", XPathUtil.joinStep(new String[]{"a", "b[p]", "c/d[q]"}));
        assertEquals("a[f(b)][g(c/d[q])]", XPathUtil.joinStep(new String[]{"a", "f(b)", "g(c/d[q])"}));
    }

    private static void assertArrayEquals(String[] expected, String[] actual) {
        if (expected == null) {
            assertNull(actual);
            return;

        }

        assertTrue(Arrays.equals(expected, actual));
    }

}
