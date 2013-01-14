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
import java.util.List;

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
        assertEquals("false", XPathUtil.evaluateAsString(doc, "//*[@id='a']/bf:data/@bf:valid"));

        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("a").getTarget();

        register(eventTarget, false);
        this.xformsProcesssorImpl.setControlValue("a", "false");
        deregister(eventTarget,false);
        assertEquals("a",this.invalidListener.getId());
        List l = (List) this.invalidListener.getContext("alerts");
        assertEquals("second constraint failed",l.get(0) );

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
        XFormsElement xfe = xformsProcesssorImpl.getContainer().lookup("cBind1");
        Bind bind = (Bind) xfe;
        assertNotNull(bind);
        assertEquals("true()",bind.getConstraint());

        bind = (Bind) xformsProcesssorImpl.getContainer().lookup("cBind2");
        assertNotNull(bind);
        assertEquals("true() and false()",bind.getConstraint());

        assertEquals("false", XPathUtil.evaluateAsString(doc, "//*[@id='c']/bf:data/@bf:valid"));
    }

    public void testConstraintMixedOneParentCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("dBind");
        assertNotNull(bind);
        assertEquals("true() and false()",bind.getConstraint());
        assertEquals("false", XPathUtil.evaluateAsString(doc, "//*[@id='d']/bf:data/@bf:valid"));
    }

    public void testConstraintCombineStandard() throws Exception{
        //test bind 1 static expression
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("eBind1");
        assertNotNull(bind);
        assertEquals("true()",bind.getConstraint());

        //test bind 2 static expression
        bind = (Bind) xformsProcesssorImpl.getContainer().lookup("eBind2");
        assertNotNull(bind);
        assertEquals("true() and boolean-from-string(.)",bind.getConstraint());
        assertEquals("false", XPathUtil.evaluateAsString(doc, "//*[@id='e']/bf:data/@bf:valid"));

        //test modification
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("e").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.setControlValue("e", "false");
        deregister(eventTarget,false);

        //assert alert messages
        assertEquals("e",this.invalidListener.getId());
        List l = (List) this.invalidListener.getContext("alerts");
        assertEquals("invalid",l.get(0) );


    }

    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream("ConstraintCombineTest.xhtml"));

        this.invalidListener = new TestEventListener();
        this.stateChangedListener = new TestEventListener();
//        EventTarget eventTarget = (EventTarget) this.xformsProcesssorImpl.getXForms().getDocumentElement();
//        register(eventTarget,true);

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
