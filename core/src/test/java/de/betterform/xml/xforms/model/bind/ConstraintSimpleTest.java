/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model.bind;

import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
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
public class ConstraintSimpleTest extends XMLTestBase {
	static {
		org.apache.log4j.BasicConfigurator.configure();
	}

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document doc;
    private TestEventListener invalidListener;
    private TestEventListener stateChangedListener;

    public ConstraintSimpleTest(String name) {
        super(name);
    }

    public void testSimpleConstraint() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("aBind");
        assertNotNull(bind);
        assertEquals(". = 'false'", bind.getConstraint());
        assertEquals("true", XPathUtil.evaluateAsString(doc, "//*[@id='a']/bf:data/@valid"));

        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("a").getTarget();

        register(eventTarget, false);
        this.xformsProcesssorImpl.setControlValue("a", "foo");
        deregister(eventTarget,false);
        assertEquals("a",this.invalidListener.getId());
        List l = (List) this.invalidListener.getContext("alerts");
        assertEquals("invalid",l.get(0) );

    }


    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream("ConstraintSimpleTest.xhtml"));

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
