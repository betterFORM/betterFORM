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
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.events.EventTarget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tests the selector elements.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: SelectorTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class SelectorTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private TestEventListener selectListener1;
    private TestEventListener selectListener2;
    private TestEventListener selectListener3;
    private TestEventListener deselectListener1;
    private TestEventListener deselectListener2;
    private TestEventListener deselectListener3;

    /**
     * Tests Select1 with static Items.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelect1Static() throws Exception {
        Selector selector = (Selector) this.xformsProcesssorImpl.getContainer().lookup("select-1");
        assertEquals("123", selector.getValue());

        String item1 = "item-1-1";
        String item2 = "item-1-2";
        String item3 = "item-1-3";

        register(item1, item2, item3);

        Map params = new HashMap(1);
        params.put("context-info", item2);
        this.xformsProcesssorImpl.getContainer().dispatch(selector.getId(),DOMEventNames.ACTIVATE,params,true,false);
        selector.setValue("124");
        deregister(item1, item2, item3);

        assertEquals("124", selector.getValue());

        assertEquals(null, this.selectListener1.getId());
        assertEquals(item2, this.selectListener2.getId());
        assertEquals(null, this.selectListener3.getId());
        assertEquals(item1, this.deselectListener1.getId());
        assertEquals(null, this.deselectListener2.getId());
        assertEquals(null, this.deselectListener3.getId());
    }

    /**
     * Tests Select1 with static Items.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelect1StaticTriggered() throws Exception {
        Selector selector = (Selector) this.xformsProcesssorImpl.getContainer().lookup("select-1");
        assertEquals("123", selector.getValue());

        String item1 = "item-1-1";
        String item2 = "item-1-2";
        String item3 = "item-1-3";

        register(item1, item2, item3);
        this.xformsProcesssorImpl.dispatch("trigger-1", DOMEventNames.ACTIVATE);
        deregister(item1, item2, item3);

        assertEquals("125", selector.getValue());

        assertEquals(null, this.selectListener1.getId());
        assertEquals(null, this.selectListener2.getId());
        assertEquals(null, this.selectListener3.getId());
        assertEquals(null, this.deselectListener1.getId());
        assertEquals(null, this.deselectListener2.getId());
        assertEquals(null, this.deselectListener3.getId());
    }

    /**
     * Tests Select with static Items.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelectStatic() throws Exception {
        Selector selector = (Selector) this.xformsProcesssorImpl.getContainer().lookup("select-2");
        assertEquals("123", selector.getValue());

        String item1 = "item-2-1";
        String item2 = "item-2-2";
        String item3 = "item-2-3";

        register(item1, item2, item3);

        Map params = new HashMap(1);
        params.put("context-info", item2+";"+item3);
        this.xformsProcesssorImpl.getContainer().dispatch(selector.getId(),DOMEventNames.ACTIVATE,params,true,false);

        selector.setValue("124 125");
        deregister(item1, item2, item3);

        assertEquals("124 125", selector.getValue());

        assertEquals(null, this.selectListener1.getId());
        assertEquals(item2, this.selectListener2.getId());
        assertEquals(item3, this.selectListener3.getId());
        assertEquals(item1, this.deselectListener1.getId());
        assertEquals(null, this.deselectListener2.getId());
        assertEquals(null, this.deselectListener3.getId());
    }

    /**
     * Tests Select1 with static Items.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelectStaticTriggered() throws Exception {
        Selector selector = (Selector) this.xformsProcesssorImpl.getContainer().lookup("select-2");
        assertEquals("123", selector.getValue());

        String item1 = "item-2-1";
        String item2 = "item-2-2";
        String item3 = "item-2-3";

        register(item1, item2, item3);
        this.xformsProcesssorImpl.dispatch("trigger-2", DOMEventNames.ACTIVATE);
        deregister(item1, item2, item3);

        assertEquals("125", selector.getValue());

        assertEquals(null, this.selectListener1.getId());
        assertEquals(null, this.selectListener2.getId());
        assertEquals(null, this.selectListener3.getId());
        assertEquals(null, this.deselectListener1.getId());
        assertEquals(null, this.deselectListener2.getId());
        assertEquals(null, this.deselectListener3.getId());
    }

    /**
     * Tests Select1 with dynamic Itemset.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelect1Dynamic() throws Exception {
        Selector selector = (Selector) this.xformsProcesssorImpl.getContainer().lookup("select-3");
        assertEquals("123", selector.getValue());

        List result = XPathUtil.evaluate(selector.getElement(), "descendant::xf:item/@id");
        String item0 = ((NodeInfo) result.get(0)).getStringValue();
        String item1 = ((NodeInfo) result.get(1)).getStringValue();
        String item2 = ((NodeInfo) result.get(2)).getStringValue();
        String item3 = ((NodeInfo) result.get(3)).getStringValue();


        register(item1, item2, item3);

        Map params = new HashMap(1);
        params.put("context-info", item2);
        this.xformsProcesssorImpl.getContainer().dispatch("select-3",DOMEventNames.ACTIVATE, params ,true,false);

        selector.setValue("124");
        deregister(item1, item2, item3);

        assertEquals("124", selector.getValue());

        assertEquals(null, this.selectListener1.getId());
        assertEquals(item2, this.selectListener2.getId());
        assertEquals(null, this.selectListener3.getId());
        assertEquals(item1, this.deselectListener1.getId());
        assertEquals(null, this.deselectListener2.getId());
        assertEquals(null, this.deselectListener3.getId());
    }

    /**
     * Tests Select1 with dynamic Itemset.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelect1DynamicTriggered() throws Exception {
        Selector selector = (Selector) this.xformsProcesssorImpl.getContainer().lookup("select-3");
        assertEquals("123", selector.getValue());

        List result = XPathUtil.evaluate(selector.getElement(), "descendant::xf:item/@id");
        String item1 = ((NodeInfo) result.get(0)).getStringValue();
        String item2 = ((NodeInfo) result.get(1)).getStringValue();
        String item3 = ((NodeInfo) result.get(2)).getStringValue();


        register(item1, item2, item3);
        this.xformsProcesssorImpl.dispatch("trigger-3", DOMEventNames.ACTIVATE);
        deregister(item1, item2, item3);

        assertEquals("125", selector.getValue());

        assertEquals(null, this.selectListener1.getId());
        assertEquals(null, this.selectListener2.getId());
        assertEquals(null, this.selectListener3.getId());
        assertEquals(null, this.deselectListener1.getId());
        assertEquals(null, this.deselectListener2.getId());
        assertEquals(null, this.deselectListener3.getId());
    }

    /**
     * Tests Select with dynamic Itemset.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelectDynamic() throws Exception {
        Selector selector = (Selector) this.xformsProcesssorImpl.getContainer().lookup("select-4");
        assertEquals("123", selector.getValue());

        List result = XPathUtil.evaluate(selector.getElement(), "descendant::xf:item/@id");
        String item1 = ((NodeInfo) result.get(0)).getStringValue();
        String item2 = ((NodeInfo) result.get(1)).getStringValue();
        String item3 = ((NodeInfo) result.get(2)).getStringValue();


        register(item1, item2, item3);

        Map params = new HashMap(1);
        params.put("context-info", item2+";"+item3);
        this.xformsProcesssorImpl.getContainer().dispatch("select-4",DOMEventNames.ACTIVATE, params ,true,false);

        selector.setValue("124 125");
        deregister(item1, item2, item3);

        assertEquals("124 125", selector.getValue());

        assertEquals(null, this.selectListener1.getId());
        assertEquals(item2, this.selectListener2.getId());
        assertEquals(item3, this.selectListener3.getId());
        assertEquals(item1, this.deselectListener1.getId());
        assertEquals(null, this.deselectListener2.getId());
        assertEquals(null, this.deselectListener3.getId());
    }

    /**
     * Tests Select1 with dynamic Itemset.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelectDynamicTriggered() throws Exception {
        Selector selector = (Selector) this.xformsProcesssorImpl.getContainer().lookup("select-4");
        assertEquals("123", selector.getValue());

        List result = XPathUtil.evaluate(selector.getElement(), "descendant::xf:item/@id");
        String item1 = ((NodeInfo) result.get(0)).getStringValue();
        String item2 = ((NodeInfo) result.get(1)).getStringValue();
        String item3 = ((NodeInfo) result.get(2)).getStringValue();


        register(item1, item2, item3);
        this.xformsProcesssorImpl.dispatch("trigger-4", DOMEventNames.ACTIVATE);
        deregister(item1, item2, item3);

        assertEquals("125", selector.getValue());

        assertEquals(null, this.selectListener1.getId());
        assertEquals(null, this.selectListener2.getId());
        assertEquals(null, this.selectListener3.getId());
        assertEquals(null, this.deselectListener1.getId());
        assertEquals(null, this.deselectListener2.getId());
        assertEquals(null, this.deselectListener3.getId());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.selectListener1 = new TestEventListener();
        this.selectListener2 = new TestEventListener();
        this.selectListener3 = new TestEventListener();
        this.deselectListener1 = new TestEventListener();
        this.deselectListener2 = new TestEventListener();
        this.deselectListener3 = new TestEventListener();

        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("SelectorTest.xhtml"));
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

        this.selectListener1 = null;
        this.selectListener2 = null;
        this.selectListener3 = null;
        this.deselectListener1 = null;
        this.deselectListener2 = null;
        this.deselectListener3 = null;
    }

    private void register(String item1, String item2, String item3) throws XFormsException {
        EventTarget eventTarget;

        eventTarget = this.xformsProcesssorImpl.getContainer().lookup(item1).getTarget();
        eventTarget.addEventListener(XFormsEventNames.SELECT, this.selectListener1, false);
        eventTarget = this.xformsProcesssorImpl.getContainer().lookup(item2).getTarget();
        eventTarget.addEventListener(XFormsEventNames.SELECT, this.selectListener2, false);
        eventTarget = this.xformsProcesssorImpl.getContainer().lookup(item3).getTarget();
        eventTarget.addEventListener(XFormsEventNames.SELECT, this.selectListener3, false);
        eventTarget = this.xformsProcesssorImpl.getContainer().lookup(item1).getTarget();
        eventTarget.addEventListener(XFormsEventNames.DESELECT, this.deselectListener1, false);
        eventTarget = this.xformsProcesssorImpl.getContainer().lookup(item2).getTarget();
        eventTarget.addEventListener(XFormsEventNames.DESELECT, this.deselectListener2, false);
        eventTarget = this.xformsProcesssorImpl.getContainer().lookup(item3).getTarget();
        eventTarget.addEventListener(XFormsEventNames.DESELECT, this.deselectListener3, false);
    }

    private void deregister(String item1, String item2, String item3) throws XFormsException {
        EventTarget eventTarget;

        eventTarget = this.xformsProcesssorImpl.getContainer().lookup(item1).getTarget();
        eventTarget.removeEventListener(XFormsEventNames.SELECT, this.selectListener1, false);
        eventTarget = this.xformsProcesssorImpl.getContainer().lookup(item2).getTarget();
        eventTarget.removeEventListener(XFormsEventNames.SELECT, this.selectListener2, false);
        eventTarget = this.xformsProcesssorImpl.getContainer().lookup(item3).getTarget();
        eventTarget.removeEventListener(XFormsEventNames.SELECT, this.selectListener3, false);
        eventTarget = this.xformsProcesssorImpl.getContainer().lookup(item1).getTarget();
        eventTarget.removeEventListener(XFormsEventNames.DESELECT, this.deselectListener1, false);
        eventTarget = this.xformsProcesssorImpl.getContainer().lookup(item2).getTarget();
        eventTarget.removeEventListener(XFormsEventNames.DESELECT, this.deselectListener2, false);
        eventTarget = this.xformsProcesssorImpl.getContainer().lookup(item3).getTarget();
        eventTarget.removeEventListener(XFormsEventNames.DESELECT, this.deselectListener3, false);
    }
}

// end of class
