/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model.bind;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Test cases for binding contexts.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: BindingTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class ConstraintCombineTest extends XMLTestBase {
	static {
		org.apache.log4j.BasicConfigurator.configure();
	}

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document doc;
    private TestEventListener invalidListener;
    private TestEventListener stateChangedListener;

    public ConstraintCombineTest(String name) {
        super(name);
    }

    public void testConstraintSameParent() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("aBind");
        assertNotNull(bind);
        assertEquals("true() and boolean-from-string(.)", bind.getConstraint());

        DOMUtil.prettyPrintDOM(doc);

        this.xformsProcesssorImpl.setControlValue("a", "false");

        assertEquals("false", XPathUtil.evaluateAsString(doc, "//*[@id='a']/bf:data/@bf:valid"));

        this.xformsProcesssorImpl.setControlValue("a", "true");
        assertEquals("true", XPathUtil.evaluateAsString(doc, "//*[@id='a']/bf:data/@bf:valid"));


    }

    public void testConstraintCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("bBind1");
        assertNotNull(bind);
        assertEquals("true()",bind.getConstraint());

        bind = (Bind) xformsProcesssorImpl.getContainer().lookup("bBind2");
        assertNotNull(bind);
        assertEquals("true() and false()", bind.getConstraint());
        DOMUtil.prettyPrintDOM(doc);
        assertEquals("false", XPathUtil.evaluateAsString(doc, "//*[@id='b']/bf:data/@bf:valid"));
    }

    public void testConstraintMixedCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("cBind1");
        assertNotNull(bind);
        assertEquals("false()",bind.getConstraint());

        bind = (Bind) xformsProcesssorImpl.getContainer().lookup("cBind2");
        assertNotNull(bind);
        assertEquals("false() or true()",bind.getConstraint());

        assertEquals("true", XPathUtil.evaluateAsString(doc, "//*[@id='c']/bf:data/@bf:constraint"));
    }

    public void testConstraintMixedOneParentCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("dBind");
        assertNotNull(bind);
        assertEquals("false() or true()",bind.getConstraint());
        assertEquals("true", XPathUtil.evaluateAsString(doc, "//*[@id='d']/bf:data/@bf:constraint"));
    }

    public void testConstraintCombineStandard() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("eBind1");
        assertNotNull(bind);
        assertEquals("false()",bind.getConstraint());

        bind = (Bind) xformsProcesssorImpl.getContainer().lookup("eBind2");
        assertNotNull(bind);
        assertEquals("false() or true()",bind.getConstraint());

        assertEquals("true", XPathUtil.evaluateAsString(doc, "//*[@id='e']/bf:data/@bf:constraint"));
    }

    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream("constraintCombineTest.xhtml"));

        this.invalidListener = new TestEventListener();
        this.stateChangedListener = new TestEventListener();


        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(document);
        this.xformsProcesssorImpl.init();

        this.doc = this.xformsProcesssorImpl.getXForms();
    }

    protected void tearDown() throws Exception {
        this.stateChangedListener=null;
        this.invalidListener = null;
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

    private void register(EventTarget eventTarget, boolean bubbles) {
        eventTarget.addEventListener(XFormsEventNames.INVALID, this.invalidListener, bubbles);
        eventTarget.addEventListener(BetterFormEventNames.STATE_CHANGED, this.stateChangedListener, bubbles);
    }

    private void deregister(EventTarget eventTarget, boolean bubbles) {
        eventTarget.removeEventListener(XFormsEventNames.INVALID, this.invalidListener, bubbles);
        eventTarget.removeEventListener(BetterFormEventNames.STATE_CHANGED, this.stateChangedListener, true);
    }
}
// end of class
