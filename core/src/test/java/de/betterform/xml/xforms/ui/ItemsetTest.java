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
package de.betterform.xml.xforms.ui;

import junit.framework.TestCase;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;

/**
 * Tests the itemset element.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ItemsetTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class ItemsetTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;

    /**
     * Tests initializing the itemset element.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInit() throws Exception {
        assertInitializedItemset("select-1");
        assertInitializedItemset("select-2");
        assertInitializedItemset("select-3");
        assertInitializedItemset("select-4");
        assertInitializedItemset("select-5");
        assertInitializedItemset("select-6");
    }

    /**
     * Tests updating the itemset element.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUpdate() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-update", DOMEventNames.ACTIVATE);

        assertUpdatedItemset("select-1");
        assertUpdatedItemset("select-2");
        assertUpdatedItemset("select-3");
        assertUpdatedItemset("select-4");
        assertUpdatedItemset("select-5");
        assertUpdatedItemset("select-6");
    }

    /**
     * Tests updating the itemset element.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUpdateGrow() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-insert", DOMEventNames.ACTIVATE);
        // DOMUtil.prettyPrintDOM(processor.getContainer().getDocument());
        assertGrownItemset("select-1");
        assertGrownItemset("select-2");
        assertGrownItemset("select-3");
        assertGrownItemset("select-4");
        assertGrownItemset("select-5");
        assertGrownItemset("select-6");
    }

    /**
     * Tests updating the itemset element.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUpdateShrink() throws Exception {
        this.xformsProcesssorImpl.dispatch("trigger-delete", DOMEventNames.ACTIVATE);

        assertShrunkItemset("select-1");
        assertShrunkItemset("select-2");
        assertShrunkItemset("select-3");
        assertShrunkItemset("select-4");
        assertShrunkItemset("select-5");
        assertShrunkItemset("select-6");
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("ItemsetTest.xhtml"));
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

    // helper

    protected void assertInitializedItemset(String selectId) throws XFormsException {
        Document host = this.xformsProcesssorImpl.getContainer().getDocument();

        assertEquals(selectId, "123", XPathUtil.evaluateAsString(host, "//*[@id='" + selectId + "']/bf:data"));
        assertEquals(selectId, "1", XPathUtil.evaluateAsString(host, "count(//*[@id='" + selectId + "']/xf:itemset/bf:data/xf:item)"));
        if(selectId.equals("select-1") || selectId.equals("select-3") || selectId.equals("select-5")){
            assertEquals(selectId, "4", XPathUtil.evaluateAsString(host, "count(//*[@id='" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "false", "", "");
            assertItem(host, selectId, 2, "true", "First", "123");
            assertItem(host, selectId, 3, "false", "Second", "124");
            assertItem(host, selectId, 4, "false", "Third", "125");
        }
        else {
            assertEquals(selectId, "3", XPathUtil.evaluateAsString(host, "count(//*[@id='" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "true", "First", "123");
            assertItem(host, selectId, 2, "false", "Second", "124");
            assertItem(host, selectId, 3, "false", "Third", "125");
        }
    }

    protected void assertUpdatedItemset(String selectId) throws XFormsException {
        Document host = this.xformsProcesssorImpl.getContainer().getDocument();

        assertEquals(selectId, "125", XPathUtil.evaluateAsString(host, "//*[@id='" + selectId + "']/bf:data"));
        if(selectId.equals("select-1") || selectId.equals("select-3") || selectId.equals("select-5")){
            assertEquals(selectId, "4", XPathUtil.evaluateAsString(host, "count(//*[@id='" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "false", "", "");
            assertItem(host, selectId, 2, "false", "First", "123");
            assertItem(host, selectId, 3, "false", "Second", "124");
            assertItem(host, selectId, 4, "true", "Third", "125");
        }
        else {
            assertEquals(selectId, "3", XPathUtil.evaluateAsString(host, "count(//*[@id='" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "false", "First", "123");
            assertItem(host, selectId, 2, "false", "Second", "124");
            assertItem(host, selectId, 3, "true", "Third", "125");
        }
    }

    protected void assertGrownItemset(String selectId) throws XFormsException {
        Document host = this.xformsProcesssorImpl.getContainer().getDocument();

        assertEquals(selectId, "123", XPathUtil.evaluateAsString(host, "//*[@id='" + selectId + "']/bf:data"));
        if(selectId.equals("select-1") || selectId.equals("select-3") || selectId.equals("select-5")){
            assertEquals(selectId, "5", XPathUtil.evaluateAsString(host, "count(//*[@id='" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "false", "", "");
            assertItem(host, selectId, 2, "true", "First", "123");
            assertItem(host, selectId, 3, "false", "Fourth", "126");
            assertItem(host, selectId, 4, "false", "Second", "124");
            assertItem(host, selectId, 5, "false", "Third", "125");

        }else {
            assertEquals(selectId, "4", XPathUtil.evaluateAsString(host, "count(//*[@id='" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "true", "First", "123");
            assertItem(host, selectId, 2, "false", "Fourth", "126");
            assertItem(host, selectId, 3, "false", "Second", "124");
            assertItem(host, selectId, 4, "false", "Third", "125");
        }
    }


    protected void assertShrunkItemset(String selectId) throws XFormsException {
        Document host = this.xformsProcesssorImpl.getContainer().getDocument();

        assertEquals(selectId, "123", XPathUtil.evaluateAsString(host, "//*[@id='" + selectId + "']/bf:data"));
        if(selectId.equals("select-1") || selectId.equals("select-3") || selectId.equals("select-5")){
            assertEquals(selectId, "3", XPathUtil.evaluateAsString(host, "count(//*[@id='" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "false", "", "");
            assertItem(host, selectId, 2, "true", "First", "123");
            assertItem(host, selectId, 3, "false", "Third", "125");

        }else {
            assertEquals(selectId, "2", XPathUtil.evaluateAsString(host, "count(//*[@id='" + selectId + "']/xf:itemset/xf:item)"));
            assertItem(host, selectId, 1, "true", "First", "123");
            assertItem(host, selectId, 2, "false", "Third", "125");

        }
    }

    private void assertItem(Document host, String id, int position, String selected, String label, String value) throws XFormsException {
        assertEquals(id, "true", XPathUtil.evaluateAsString(host, "boolean(//*[@id='" + id + "']/xf:itemset/xf:item[" + position + "]/@id)"));
        assertEquals(id, selected, XPathUtil.evaluateAsString(host, "//*[@id='" + id + "']/xf:itemset/xf:item[" + position + "]/@selected"));
        assertEquals(id, "true", XPathUtil.evaluateAsString(host, "boolean(//*[@id='" + id + "']/xf:itemset/xf:item[" + position + "]/xf:label/@id)"));
        assertEquals(id, label, XPathUtil.evaluateAsString(host, "//*[@id='" + id + "']/xf:itemset/xf:item[" + position + "]/xf:label"));
        assertEquals(id, "true", XPathUtil.evaluateAsString(host, "boolean(//*[@id='" + id + "']/xf:itemset/xf:item[" + position + "]/xf:value/@id)"));
        assertEquals(id, value, XPathUtil.evaluateAsString(host, "//*[@id='" + id + "']/xf:itemset/xf:item[" + position + "]/xf:value"));
    }
}

// end of class
