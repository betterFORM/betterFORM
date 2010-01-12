// Copyright 2010 betterForm
/*
 *
 *    Artistic License
 *
 *    Preamble
 *
 *    The intent of this document is to state the conditions under which a Package may be copied, such that
 *    the Copyright Holder maintains some semblance of artistic control over the development of the
 *    package, while giving the users of the package the right to use and distribute the Package in a
 *    more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *    Definitions:
 *
 *    "Package" refers to the collection of files distributed by the Copyright Holder, and derivatives
 *    of that collection of files created through textual modification.
 *
 *    "Standard Version" refers to such a Package if it has not been modified, or has been modified
 *    in accordance with the wishes of the Copyright Holder.
 *
 *    "Copyright Holder" is whoever is named in the copyright or copyrights for the package.
 *
 *    "You" is you, if you're thinking about copying or distributing this Package.
 *
 *    "Reasonable copying fee" is whatever you can justify on the basis of media cost, duplication
 *    charges, time of people involved, and so on. (You will not be required to justify it to the
 *    Copyright Holder, but only to the computing community at large as a market that must bear the
 *    fee.)
 *
 *    "Freely Available" means that no fee is charged for the item itself, though there may be fees
 *    involved in handling the item. It also means that recipients of the item may redistribute it under
 *    the same conditions they received it.
 *
 *    1. You may make and give away verbatim copies of the source form of the Standard Version of this
 *    Package without restriction, provided that you duplicate all of the original copyright notices and
 *    associated disclaimers.
 *
 *    2. You may apply bug fixes, portability fixes and other modifications derived from the Public Domain
 *    or from the Copyright Holder. A Package modified in such a way shall still be considered the
 *    Standard Version.
 *
 *    3. You may otherwise modify your copy of this Package in any way, provided that you insert a
 *    prominent notice in each changed file stating how and when you changed that file, and provided that
 *    you do at least ONE of the following:
 *
 *        a) place your modifications in the Public Domain or otherwise make them Freely
 *        Available, such as by posting said modifications to Usenet or an equivalent medium, or
 *        placing the modifications on a major archive site such as ftp.uu.net, or by allowing the
 *        Copyright Holder to include your modifications in the Standard Version of the Package.
 *
 *        b) use the modified Package only within your corporation or organization.
 *
 *        c) rename any non-standard executables so the names do not conflict with standard
 *        executables, which must also be provided, and provide a separate manual page for each
 *        non-standard executable that clearly documents how it differs from the Standard
 *        Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    4. You may distribute the programs of this Package in object code or executable form, provided that
 *    you do at least ONE of the following:
 *
 *        a) distribute a Standard Version of the executables and library files, together with
 *        instructions (in the manual page or equivalent) on where to get the Standard Version.
 *
 *        b) accompany the distribution with the machine-readable source of the Package with
 *        your modifications.
 *
 *        c) accompany any non-standard executables with their corresponding Standard Version
 *        executables, giving the non-standard executables non-standard names, and clearly
 *        documenting the differences in manual pages (or equivalent), together with instructions
 *        on where to get the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    5. You may charge a reasonable copying fee for any distribution of this Package. You may charge
 *    any fee you choose for support of this Package. You may not charge a fee for this Package itself.
 *    However, you may distribute this Package in aggregate with other (possibly commercial) programs as
 *    part of a larger (possibly commercial) software distribution provided that you do not advertise this
 *    Package as a product of your own.
 *
 *    6. The scripts and library files supplied as input to or produced as output from the programs of this
 *    Package do not automatically fall under the copyright of this Package, but belong to whomever
 *    generated them, and may be sold commercially, and may be aggregated with this Package.
 *
 *    7. C or perl subroutines supplied by you and linked into this Package shall not be considered part of
 *    this Package.
 *
 *    8. The name of the Copyright Holder may not be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 *    9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
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
