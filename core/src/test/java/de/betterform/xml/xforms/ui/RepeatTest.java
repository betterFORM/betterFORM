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
import net.sf.saxon.om.NodeInfo;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests repeat structures.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RepeatTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class RepeatTest extends TestCase {
/*    static {
        org.apache.log4j.BasicConfigurator.configure();
    }*/

    private XFormsProcessorImpl xformsProcesssorImpl;
    private TestEventListener itemInsertedListener;
    private TestEventListener itemDeletedListener;
    private TestEventListener indexChangedListener;
    private TestEventListener stateChangedListener;

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("RepeatTest.xhtml"));
        this.xformsProcesssorImpl.init();

        this.itemInsertedListener = new TestEventListener();
        this.itemDeletedListener = new TestEventListener();
        this.indexChangedListener = new TestEventListener();
        this.stateChangedListener = new TestEventListener();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.itemInsertedListener = null;
        this.itemDeletedListener = null;
        this.indexChangedListener = null;
        this.stateChangedListener = null;

        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

    /**
     * Tests inserting before the current index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertBefore() throws Exception {
        Repeat repeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup("repeat");
        register(repeat.getTarget());
        this.xformsProcesssorImpl.dispatch("trigger-insert-before", DOMEventNames.ACTIVATE);
        deregister(repeat.getTarget());
                            
        assertEquals(2, repeat.getIndex());
        assertEquals(4, repeat.getContextSize());

        assertItemInserted("repeat", "repeat", "2");
        assertItemDeleted(null, null, null);
        assertIndexChanged("repeat", "repeat", "2");
        assertStateChanged(null, null);

        assertTrue(this.itemInsertedListener.getTime() < this.indexChangedListener.getTime());
    }

    /**
     * Tests inserting after the current index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertAfter() throws Exception {
        Repeat repeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup("repeat");
        register(repeat.getTarget());
        this.xformsProcesssorImpl.dispatch("trigger-insert-after", DOMEventNames.ACTIVATE);
        deregister(repeat.getTarget());
        // DOMUtil.prettyPrintDOM(this.xformsProcesssorImpl.getContainer().getDocument());

        assertEquals(3, repeat.getIndex());
        assertEquals(4, repeat.getContextSize());

        assertItemInserted("repeat", "repeat", "3");
        assertItemDeleted(null, null, null);
        assertIndexChanged("repeat", "repeat", "3");
        assertStateChanged(null, null);

        assertTrue(this.itemInsertedListener.getTime() < this.indexChangedListener.getTime());
    }

    /**
     * Tests inserting after last item.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertLast() throws Exception {
        Repeat repeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup("repeat-filter");
        register(repeat.getTarget());
        this.xformsProcesssorImpl.dispatch("trigger-insert-last", DOMEventNames.ACTIVATE);
        deregister(repeat.getTarget());

        assertEquals(3, repeat.getIndex());
        assertEquals(3, repeat.getContextSize());

        assertItemInserted("repeat-filter", "repeat-filter", "3");
        assertItemDeleted(null, null, null);
        assertIndexChanged("repeat-filter", "repeat-filter", "3");
        assertStateChanged(null, null);

        assertTrue(this.itemInsertedListener.getTime() < this.indexChangedListener.getTime());
    }

    /**
     * Tests deleting at the current index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteAt() throws Exception {
        Repeat repeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup("repeat");
        register(repeat.getTarget());
        this.xformsProcesssorImpl.dispatch("trigger-delete-at", DOMEventNames.ACTIVATE);
        deregister(repeat.getTarget());

        assertEquals(1, repeat.getIndex());
        assertEquals(2, repeat.getContextSize());

        assertItemInserted(null, null, null);
        assertItemDeleted("repeat", "repeat", "2");
        assertIndexChanged(null, null, null);
        assertStateChanged(null, null);
    }

    /**
     * Tests deleting all nodes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteAll() throws Exception {
        Repeat repeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup("repeat");
        register(repeat.getTarget());
        this.xformsProcesssorImpl.dispatch("trigger-delete-all", DOMEventNames.ACTIVATE);
        deregister(repeat.getTarget());

        assertEquals(0, repeat.getIndex());
        assertEquals(0, repeat.getContextSize());

        assertItemInserted(null, null, null);
        assertItemDeleted("repeat", "repeat", "1");
        assertIndexChanged("repeat", "repeat", "0");
        assertStateChanged("repeat", "false");

        assertTrue(this.itemDeletedListener.getTime() < this.indexChangedListener.getTime());
    }

    /**
     * Tests multiple insert and delete operations en bloc.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testBatchUpdate() throws Exception {
        Repeat repeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup("repeat");
        register(repeat.getTarget());
        this.xformsProcesssorImpl.dispatch("trigger-batch-update", DOMEventNames.ACTIVATE);
        deregister(repeat.getTarget());

        assertEquals(1, repeat.getIndex());
        assertEquals(3, repeat.getContextSize());

        assertItemInserted("repeat", "repeat", "1");
        assertItemDeleted("repeat", "repeat", "1");
        assertIndexChanged("repeat", "repeat", "1");
        assertStateChanged(null, null);

        assertTrue(this.itemDeletedListener.getTime() < this.itemInsertedListener.getTime());
    }

    /**
     * Tests index updating.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetIndex() throws Exception {
        Repeat repeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup("repeat");
        register(repeat.getTarget());

        Output output = (Output) this.xformsProcesssorImpl.getContainer().lookup("selection");
        assertEquals("first", output.getValue());

        assertEquals("1", XPathUtil.evaluateAsString(repeat.getElement(), "bf:data/@bf:index"));

        this.xformsProcesssorImpl.dispatch("trigger-setindex", DOMEventNames.ACTIVATE);
        deregister(repeat.getTarget());

        assertEquals(3, repeat.getIndex());
        assertEquals(3, repeat.getContextSize());
        assertEquals("last", output.getValue());
        assertEquals("3", XPathUtil.evaluateAsString(repeat.getElement(), "bf:data/@bf:index"));

        assertItemInserted(null, null, null);
        assertItemDeleted(null, null, null);
        assertIndexChanged("repeat", "repeat", "3");
        assertStateChanged(null, null);
    }

    /**
     * Tests for correct prototype handling.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testRepeatPrototype() throws Exception {
        Repeat repeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup("repeat");

        /*
        <bf:data ... >
            <xf:group id="<generated>" appearance="repeated">
                <xf:input id="input" ref="."/>
                <xf:output id="output" ref="."/>
            </xf:group>
        </bf:data>
        */
        assertEquals("1", XPathUtil.evaluateAsString(repeat.getElement(), "count(bf:data/*)"));

        assertEquals("group", XPathUtil.evaluateAsString(repeat.getElement(), "local-name(bf:data/*[1])"));
        assertEquals(NamespaceConstants.XFORMS_NS, XPathUtil.evaluateAsString(repeat.getElement(), "namespace-uri(bf:data/*[1])"));
        assertEquals("true", XPathUtil.evaluateAsString(repeat.getElement(), "string-length(bf:data/xf:group/@id) > 0"));
        assertEquals("repeated", XPathUtil.evaluateAsString(repeat.getElement(), "bf:data/xf:group/@appearance"));
        assertEquals("2", XPathUtil.evaluateAsString(repeat.getElement(), "count(bf:data/xf:group/*)"));

        assertEquals("input", XPathUtil.evaluateAsString(repeat.getElement(), "local-name(bf:data/xf:group/*[1])"));
        assertEquals(NamespaceConstants.XFORMS_NS, XPathUtil.evaluateAsString(repeat.getElement(), "namespace-uri(bf:data/xf:group/*[1])"));
        assertEquals("input", XPathUtil.evaluateAsString(repeat.getElement(), "bf:data/xf:group/xf:input/@id"));
        assertEquals(".", XPathUtil.evaluateAsString(repeat.getElement(), "bf:data/xf:group/xf:input/@ref"));

        assertEquals("output", XPathUtil.evaluateAsString(repeat.getElement(), "local-name(bf:data/xf:group/*[2])"));
        assertEquals(NamespaceConstants.XFORMS_NS, XPathUtil.evaluateAsString(repeat.getElement(), "namespace-uri(bf:data/xf:group/*[2])"));
        assertEquals("output", XPathUtil.evaluateAsString(repeat.getElement(), "bf:data/xf:group/xf:output/@id"));
        assertEquals(".", XPathUtil.evaluateAsString(repeat.getElement(), "bf:data/xf:group/xf:output/@ref"));
    }

    /**
     * Tests for correct prototype handling.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testRepeatPrototypeAnon() throws Exception {
        Repeat repeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup("repeat-anon");

        /*
        <bf:data ... >
            <xf:group id="<generated>" appearance="repeated">
                <xf:input id="input" ref="."/>
                <xf:output id="output" ref="."/>
            </xf:group>
        </bf:data>
        */
        assertEquals("1", XPathUtil.evaluateAsString(repeat.getElement(), "count(bf:data/*)"));

        assertEquals("group", XPathUtil.evaluateAsString(repeat.getElement(), "local-name(bf:data/*[1])"));
        assertEquals(NamespaceConstants.XFORMS_NS, XPathUtil.evaluateAsString(repeat.getElement(), "namespace-uri(bf:data/*[1])"));
        assertEquals("true", XPathUtil.evaluateAsString(repeat.getElement(), "string-length(bf:data/xf:group/@id) > 0"));
        assertEquals("repeated", XPathUtil.evaluateAsString(repeat.getElement(), "bf:data/xf:group/@appearance"));
        assertEquals("2", XPathUtil.evaluateAsString(repeat.getElement(), "count(bf:data/xf:group/*)"));

        assertEquals("input", XPathUtil.evaluateAsString(repeat.getElement(), "local-name(bf:data/xf:group/*[1])"));
        assertEquals(NamespaceConstants.XFORMS_NS, XPathUtil.evaluateAsString(repeat.getElement(), "namespace-uri(bf:data/xf:group/*[1])"));
        assertEquals("true", XPathUtil.evaluateAsString(repeat.getElement(), "string-length(bf:data/xf:group/xf:input/@id) > 0"));
        assertEquals(".", XPathUtil.evaluateAsString(repeat.getElement(), "bf:data/xf:group/xf:input/@ref"));

        assertEquals("output", XPathUtil.evaluateAsString(repeat.getElement(), "local-name(bf:data/xf:group/*[2])"));
        assertEquals(NamespaceConstants.XFORMS_NS, XPathUtil.evaluateAsString(repeat.getElement(), "namespace-uri(bf:data/xf:group/*[2])"));
        assertEquals("true", XPathUtil.evaluateAsString(repeat.getElement(), "string-length(bf:data/xf:group/xf:output/@id) > 0"));
        assertEquals(".", XPathUtil.evaluateAsString(repeat.getElement(), "bf:data/xf:group/xf:output/@ref"));
    }

    /**
     * Tests location path resolution.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLocationPath() throws Exception {
        Repeat repeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup("repeat");
        List ids = new ArrayList();

        List result = XPathUtil.evaluate(repeat.getElement(), ".//xf:input[bf:data]/@id");
        for (int i = 0; i < result.size(); i++) {
            ids.add(((NodeInfo) result.get(i)).getStringValue());

        }
        assertEquals(3, ids.size());
        for (int index = 0; index < ids.size(); index++) {
            String id = (String) ids.get(index);
            Text input = (Text) this.xformsProcesssorImpl.getContainer().lookup(id);
            assertEquals("/*[1]/item[" + String.valueOf(index + 1) + "]", input.getLocationPath());
        }
    }

    /**
     * Tests master / slave dependencies.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testMasterSlave() throws Exception {
        Repeat repeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup("slave-repeat");

        this.xformsProcesssorImpl.setRepeatIndex("master-repeat", 1);
        this.xformsProcesssorImpl.dispatch("trigger-insert-slave", DOMEventNames.ACTIVATE);

        assertEquals(2, repeat.getContextSize());
        assertEquals(2, repeat.getIndex());


        this.xformsProcesssorImpl.setRepeatIndex("master-repeat", 2);
        this.xformsProcesssorImpl.dispatch("trigger-insert-slave", DOMEventNames.ACTIVATE);

        assertEquals(2, repeat.getContextSize());
        assertEquals(2, repeat.getIndex());

        this.xformsProcesssorImpl.setRepeatIndex("master-repeat", 3);
        this.xformsProcesssorImpl.dispatch("trigger-insert-slave", DOMEventNames.ACTIVATE);

        assertEquals(2, repeat.getContextSize());
        assertEquals(2, repeat.getIndex());
    }

    public void testRepeatRefreshing() throws Exception {

        Repeat repeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-master-repeat");
        this.xformsProcesssorImpl.setRepeatIndex("nested-master-repeat", 3);
        assertEquals(3, repeat.getIndex());

        this.xformsProcesssorImpl.dispatch("trigger-insert-nested-master", DOMEventNames.ACTIVATE);
        Document host = xformsProcesssorImpl.getContainer().getDocument();
        assertEquals("7", XPathUtil.evaluateAsString(host, "//xf:repeat[@id='nested-master-repeat']//xf:repeat//xf:input/bf:data"));
        assertEquals("8", XPathUtil.evaluateAsString(host, "//xf:repeat[@id='nested-master-repeat']//xf:repeat//xf:output/bf:data"));
        assertEquals(1, repeat.getIndex());

    }


    private void register(EventTarget eventTarget) {
        eventTarget.addEventListener(BetterFormEventNames.ITEM_INSERTED, this.itemInsertedListener, false);
        eventTarget.addEventListener(BetterFormEventNames.NODE_DELETED, this.itemInsertedListener, false);
        eventTarget.addEventListener(BetterFormEventNames.ITEM_DELETED, this.itemDeletedListener, false);
        eventTarget.addEventListener(BetterFormEventNames.INDEX_CHANGED, this.indexChangedListener, false);
        eventTarget.addEventListener(BetterFormEventNames.STATE_CHANGED, this.stateChangedListener, false);
    }

    private void deregister(EventTarget eventTarget) {
        eventTarget.removeEventListener(BetterFormEventNames.ITEM_INSERTED, this.itemInsertedListener, false);
        eventTarget.removeEventListener(BetterFormEventNames.NODE_DELETED, this.itemInsertedListener, false);
        eventTarget.removeEventListener(BetterFormEventNames.ITEM_DELETED, this.itemDeletedListener, false);
        eventTarget.removeEventListener(BetterFormEventNames.INDEX_CHANGED, this.indexChangedListener, false);
        eventTarget.removeEventListener(BetterFormEventNames.STATE_CHANGED, this.stateChangedListener, false);
    }

    private void assertItemInserted(String target, String original, String position) {
        if (target == null) {
            assertEquals(null, this.itemInsertedListener.getId());
            assertEquals(null, this.itemInsertedListener.getContext());
            assertEquals(null, this.itemInsertedListener.getPropertyNames());
        } else {
            assertEquals(target, this.itemInsertedListener.getId());

//            assertEquals(null, this.itemInsertedListener.getContext());
            assertEquals(original, this.itemInsertedListener.getContext("originalId"));
            assertEquals(position, this.itemInsertedListener.getContext("position"));

            assertNotNull(this.itemInsertedListener.getPropertyNames());
            assertEquals(2, this.itemInsertedListener.getPropertyNames().size());
        }
    }

    private void assertItemDeleted(String target, String original, String position) {
        if (target == null) {
            assertEquals(null, this.itemDeletedListener.getId());
            assertEquals(null, this.itemDeletedListener.getContext());
            assertEquals(null, this.itemDeletedListener.getPropertyNames());
        } else {
            assertEquals(target, this.itemDeletedListener.getId());

//            assertEquals(null, this.itemDeletedListener.getContext());
            assertEquals(original, this.itemDeletedListener.getContext("originalId"));
            assertEquals(position, this.itemDeletedListener.getContext("position"));

            assertNotNull(this.itemDeletedListener.getPropertyNames());
            assertEquals(2, this.itemDeletedListener.getPropertyNames().size());
        }
    }

    private void assertIndexChanged(String target, String original, String index) {
        if (target == null) {
            assertEquals(null, this.indexChangedListener.getId());
            assertEquals(null, this.indexChangedListener.getContext());
            assertEquals(null, this.indexChangedListener.getPropertyNames());
        } else {
            assertEquals(target, this.indexChangedListener.getId());

//            assertEquals(null, this.indexChangedListener.getContext());
            assertEquals(original, this.indexChangedListener.getContext("originalId"));
            assertEquals(index, this.indexChangedListener.getContext("index"));

            assertNotNull(this.indexChangedListener.getPropertyNames());
            assertEquals(2, this.indexChangedListener.getPropertyNames().size());
        }
    }

    private void assertStateChanged(String target, String enabled) {
        if (target == null) {
            assertTrue(this.stateChangedListener.getId() == null || !this.stateChangedListener.getId().equals("repeat"));
        } else {
            assertEquals(target, this.stateChangedListener.getId());

//            assertEquals(null, this.stateChangedListener.getContext());
            assertEquals(enabled, this.stateChangedListener.getContext("enabled"));

            assertNotNull(this.stateChangedListener.getPropertyNames());
            assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        }
    }


}

// end of class
