/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.ui;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import junit.framework.TestCase;
import net.sf.saxon.om.NodeInfo;
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
        String item1 = ((NodeInfo) result.get(0)).getStringValue();
        String item2 = ((NodeInfo) result.get(1)).getStringValue();
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
