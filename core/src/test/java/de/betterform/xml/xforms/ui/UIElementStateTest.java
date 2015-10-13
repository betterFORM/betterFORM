/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.ui;

import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;

/**
 * Tests the ui element state.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: UIElementStateTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class UIElementStateTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private TestEventListener valueChangedListener;
    private TestEventListener validListener;
    private TestEventListener invalidListener;
    private TestEventListener readonlyListener;
    private TestEventListener readwriteListener;
    private TestEventListener requiredListener;
    private TestEventListener optionalListener;
    private TestEventListener enabledListener;
    private TestEventListener disabledListener;
    private TestEventListener stateChangedListener;
    private Document host;

    /**
     * Tests ui element state.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInit() throws Exception {
        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@type"));


/*
        assertEquals(null, this.valueChangedListener.getId());
        assertEquals(null, this.validListener.getId());
        assertEquals(null, this.invalidListener.getId());
        assertEquals(null, this.readonlyListener.getId());
        assertEquals(null, this.readwriteListener.getId());
        assertEquals(null, this.requiredListener.getId());
        assertEquals(null, this.optionalListener.getId());
        assertEquals(null, this.enabledListener.getId());
        assertEquals(null, this.disabledListener.getId());

        assertEquals(null, this.stateChangedListener.getId());
        assertEquals(null, this.stateChangedListener.getPropertyNames());
*/
    }

    /**
     * Tests ui element state with a missing instance node.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInitMissing() throws Exception {
        assertEquals("", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-missing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-missing']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-missing']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-missing']/bf:data/@required"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-missing']/bf:data/@enabled"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "boolean(//*[@id eq 'input-missing']/bf:data/@type)"));

/*
        assertEquals(null, this.valueChangedListener.getId());
        assertEquals(null, this.validListener.getId());
        assertEquals(null, this.invalidListener.getId());
        assertEquals(null, this.readonlyListener.getId());
        assertEquals(null, this.readwriteListener.getId());
        assertEquals(null, this.requiredListener.getId());
        assertEquals(null, this.optionalListener.getId());
        assertEquals(null, this.enabledListener.getId());
        assertEquals(null, this.disabledListener.getId());

        assertEquals(null, this.stateChangedListener.getId());
        assertEquals(null, this.stateChangedListener.getPropertyNames());
*/
    }

    /**
     * Tests ui element state after inserting a missing instance node.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertMissing() throws Exception {
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("input-missing").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.dispatch("insert-missing", DOMEventNames.ACTIVATE);
        deregister(eventTarget, false);

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-missing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-missing']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-missing']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-missing']/bf:data/@required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-missing']/bf:data/@enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-missing']/bf:data/@type"));

/*
        assertEquals(null, this.valueChangedListener.getId());
        assertEquals(null, this.validListener.getId());
        assertEquals(null, this.invalidListener.getId());
        assertEquals(null, this.readonlyListener.getId());
        assertEquals(null, this.readwriteListener.getId());
        assertEquals(null, this.requiredListener.getId());
        assertEquals(null, this.optionalListener.getId());
        assertEquals(null, this.enabledListener.getId());
        assertEquals(null, this.disabledListener.getId());
*/

        assertEquals("input-missing", this.stateChangedListener.getId());
        assertEquals(3, this.stateChangedListener.getPropertyNames().size());
        assertEquals("item", this.stateChangedListener.getContext("value"));
        assertEquals("true", this.stateChangedListener.getContext("enabled"));
        assertEquals("xs:token", this.stateChangedListener.getContext("type"));
    }

    /**
     * Tests ui element state after deleting an existing instance node.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteExisting() throws Exception {
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("input-existing").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.dispatch("delete-existing", DOMEventNames.ACTIVATE);
        register(eventTarget, false);

        assertEquals("", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@required"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@enabled"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "boolean(//*[@id eq 'input-existing']/bf:data/@type)"));

/*
        assertEquals(null, this.valueChangedListener.getId());
        assertEquals(null, this.validListener.getId());
        assertEquals(null, this.invalidListener.getId());
        assertEquals(null, this.readonlyListener.getId());
        assertEquals(null, this.readwriteListener.getId());
        assertEquals(null, this.requiredListener.getId());
        assertEquals(null, this.optionalListener.getId());
        assertEquals(null, this.enabledListener.getId());
        assertEquals(null, this.disabledListener.getId());
*/

        assertEquals("input-existing", this.stateChangedListener.getId());
//        assertEquals(3, this.stateChangedListener.getPropertyNames().size());
        assertEquals(null, this.stateChangedListener.getContext("value"));
        assertEquals("false", this.stateChangedListener.getContext("enabled"));
        assertEquals(null, this.stateChangedListener.getContext("type"));
    }

    /**
     * Tests value change notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValueChange() throws Exception {
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("input-existing").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.setControlValue("input-existing", "foobar");
        deregister(eventTarget, false);

        assertEquals("foobar", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@type"));

        assertEquals("input-existing", this.valueChangedListener.getId());
        assertEquals("input-existing", this.validListener.getId());
/*
        assertEquals(null, this.invalidListener.getId());
        assertEquals(null, this.readonlyListener.getId());
*/
        assertEquals("input-existing", this.readwriteListener.getId());
/*
        assertEquals(null, this.requiredListener.getId());
*/
        assertEquals("input-existing", this.optionalListener.getId());
        assertEquals("input-existing", this.enabledListener.getId());
/*
        assertEquals(null, this.disabledListener.getId());

        assertEquals(null, this.stateChangedListener.getId());
        assertEquals(null, this.stateChangedListener.getPropertyNames());
*/
    }
    /**
     * Tests date value change notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDateValueChange() throws Exception {
        System.out.println("Before control value changed");
        // DOMUtil.prettyPrintDOM(this.xformsProcesssorImpl.getContainer().getDocument());

        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("update-date").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.dispatch("update-date", DOMEventNames.ACTIVATE);
        deregister(eventTarget, false);

        // DOMUtil.prettyPrintDOM(this.xformsProcesssorImpl.getContainer().getDocument());

        assertEquals("01.01.2001", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-date']/bf:data"));

        assertEquals("2001-01-01", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-date']/bf:data/@schema-value"));
        assertEquals("date", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-date']/bf:data/@type"));

        // assertEquals("input-date", this.valueChangedListener.getId());
        // assertEquals(null, this.stateChangedListener.getId());
        // assertEquals(null, this.stateChangedListener.getPropertyNames());
    }

    /**
     * Tests value change notification after a setvalue action.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValueChangeTriggered() throws Exception {
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("input-trigger").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.dispatch("update-trigger", DOMEventNames.ACTIVATE);
        deregister(eventTarget, false);

        assertEquals("foobar", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-trigger']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-trigger']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-trigger']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-trigger']/bf:data/@required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-trigger']/bf:data/@enabled"));
        assertEquals("xs:string", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-trigger']/bf:data/@type"));

        assertEquals("input-trigger", this.valueChangedListener.getId());
        assertEquals("input-trigger", this.validListener.getId());
/*
        assertEquals(null, this.invalidListener.getId());
        assertEquals(null, this.readonlyListener.getId());
*/
        assertEquals("input-trigger", this.readwriteListener.getId());
/*
        assertEquals(null, this.requiredListener.getId());
*/
        assertEquals("input-trigger", this.optionalListener.getId());
        assertEquals("input-trigger", this.enabledListener.getId());
/*
        assertEquals(null, this.disabledListener.getId());
*/

        assertEquals("input-trigger", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("foobar", this.stateChangedListener.getContext("value"));
    }

    /**
     * Tests value change notification after recalculation.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValueChangeCalculated() throws Exception {
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("input-calculate").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.dispatch("update-trigger", DOMEventNames.ACTIVATE);
        deregister(eventTarget, false);

        assertEquals("foobar", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-calculate']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-calculate']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-calculate']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-calculate']/bf:data/@required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-calculate']/bf:data/@enabled"));
        assertEquals("xs:string", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-calculate']/bf:data/@type"));

        assertEquals("input-calculate", this.valueChangedListener.getId());
        assertEquals("input-calculate", this.validListener.getId());
        assertEquals(null, this.invalidListener.getId());
        assertEquals(null, this.readonlyListener.getId());
        assertEquals("input-calculate", this.readwriteListener.getId());
        assertEquals(null, this.requiredListener.getId());
        assertEquals("input-calculate", this.optionalListener.getId());
        assertEquals("input-calculate", this.enabledListener.getId());
        assertEquals(null, this.disabledListener.getId());

        assertEquals("input-calculate", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("foobar", this.stateChangedListener.getContext("value"));
    }

    /**
     * Tests valid notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidNotification() throws Exception {
        this.xformsProcesssorImpl.setControlValue("input-constraint", "false");
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("input-existing").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.setControlValue("input-constraint", "true");
        deregister(eventTarget, false);

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@type"));

        assertEquals("input-existing", this.validListener.getId());

        assertEquals("input-existing", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("true", this.stateChangedListener.getContext("valid"));
    }

    /**
     * Tests invalid notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInvalidNotification() throws Exception {
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("input-existing").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.setControlValue("input-constraint", "false");
        deregister(eventTarget, false);

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@type"));

        assertEquals("input-existing", this.invalidListener.getId());

        assertEquals("input-existing", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("false", this.stateChangedListener.getContext("valid"));
    }

    /**
     * Tests readonly notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testReadonlyNotification() throws Exception {
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("input-existing").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.setControlValue("input-readonly", "true");
        deregister(eventTarget, false);

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@valid"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@type"));

        assertEquals("input-existing", this.readonlyListener.getId());

        assertEquals("input-existing", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("true", this.stateChangedListener.getContext("readonly"));
    }

    /**
     * Tests readwrite notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testReadwriteNotification() throws Exception {
        this.xformsProcesssorImpl.setControlValue("input-readonly", "true");
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("input-existing").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.setControlValue("input-readonly", "false");
        deregister(eventTarget, false);

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@type"));

        assertEquals("input-existing", this.readwriteListener.getId());

        assertEquals("input-existing", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("false", this.stateChangedListener.getContext("readonly"));
    }

    /**
     * Tests required notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testRequiredNotification() throws Exception {
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("input-existing").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.setControlValue("input-required", "true");
        deregister(eventTarget, false);

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@readonly"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@type"));

        assertEquals("input-existing", this.requiredListener.getId());

        assertEquals("input-existing", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("true", this.stateChangedListener.getContext("required"));
    }

    /**
     * Tests optional notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testOptionalNotification() throws Exception {
        this.xformsProcesssorImpl.setControlValue("input-required", "true");
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("input-existing").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.setControlValue("input-required", "false");
        deregister(eventTarget, false);

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@type"));

        assertEquals("input-existing", this.optionalListener.getId());

        assertEquals("input-existing", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("false", this.stateChangedListener.getContext("required"));
    }

    /**
     * Tests enabled notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testEnabledNotification() throws Exception {
        this.xformsProcesssorImpl.setControlValue("input-relevant", "false");
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("input-existing").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.setControlValue("input-relevant", "true");
        deregister(eventTarget, false);

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@type"));

        assertEquals("input-existing", this.enabledListener.getId());

        assertEquals("input-existing", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("true", this.stateChangedListener.getContext("enabled"));
    }

    /**
     * Tests disabled notification.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDisabledNotification() throws Exception {
        EventTarget eventTarget = this.xformsProcesssorImpl.getContainer().lookup("input-existing").getTarget();
        register(eventTarget, false);
        this.xformsProcesssorImpl.setControlValue("input-relevant", "false");
        deregister(eventTarget, false);

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@required"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id eq 'input-existing']/bf:data/@type"));

        assertEquals("input-existing", this.disabledListener.getId());

        assertEquals("input-existing", this.stateChangedListener.getId());
        assertEquals(1, this.stateChangedListener.getPropertyNames().size());
        assertEquals("false", this.stateChangedListener.getContext("enabled"));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.valueChangedListener = new TestEventListener();
        this.validListener = new TestEventListener();
        this.invalidListener = new TestEventListener();
        this.readonlyListener = new TestEventListener();
        this.readwriteListener = new TestEventListener();
        this.requiredListener = new TestEventListener();
        this.optionalListener = new TestEventListener();
        this.enabledListener = new TestEventListener();
        this.disabledListener = new TestEventListener();
        this.stateChangedListener = new TestEventListener();

        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("UIElementStateTest.xhtml"));

        EventTarget eventTarget = (EventTarget) this.xformsProcesssorImpl.getXForms().getDocumentElement();
        register(eventTarget, true);

                
        this.xformsProcesssorImpl.init();

        deregister(eventTarget, true);
        this.host = xformsProcesssorImpl.getXForms();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;

        this.valueChangedListener = null;
        this.validListener = null;
        this.invalidListener = null;
        this.readonlyListener = null;
        this.readwriteListener = null;
        this.requiredListener = null;
        this.optionalListener = null;
        this.enabledListener = null;
        this.disabledListener = null;
        this.stateChangedListener = null;
    }

    private void register(EventTarget eventTarget, boolean bubbles) {
        eventTarget.addEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, bubbles);
        eventTarget.addEventListener(XFormsEventNames.VALID, this.validListener, bubbles);
        eventTarget.addEventListener(XFormsEventNames.INVALID, this.invalidListener, bubbles);
        eventTarget.addEventListener(XFormsEventNames.READONLY, this.readonlyListener, bubbles);
        eventTarget.addEventListener(XFormsEventNames.READWRITE, this.readwriteListener, bubbles);
        eventTarget.addEventListener(XFormsEventNames.REQUIRED, this.requiredListener, bubbles);
        eventTarget.addEventListener(XFormsEventNames.OPTIONAL, this.optionalListener, bubbles);
        eventTarget.addEventListener(XFormsEventNames.ENABLED, this.enabledListener, bubbles);
        eventTarget.addEventListener(XFormsEventNames.DISABLED, this.disabledListener, bubbles);
        eventTarget.addEventListener(BetterFormEventNames.STATE_CHANGED, this.stateChangedListener, bubbles);
    }

    private void deregister(EventTarget eventTarget, boolean bubbles) {
        eventTarget.removeEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, bubbles);
        eventTarget.removeEventListener(XFormsEventNames.VALID, this.validListener, bubbles);
        eventTarget.removeEventListener(XFormsEventNames.INVALID, this.invalidListener, bubbles);
        eventTarget.removeEventListener(XFormsEventNames.READONLY, this.readonlyListener, bubbles);
        eventTarget.removeEventListener(XFormsEventNames.READWRITE, this.readwriteListener, bubbles);
        eventTarget.removeEventListener(XFormsEventNames.REQUIRED, this.requiredListener, bubbles);
        eventTarget.removeEventListener(XFormsEventNames.OPTIONAL, this.optionalListener, bubbles);
        eventTarget.removeEventListener(XFormsEventNames.ENABLED, this.enabledListener, bubbles);
        eventTarget.removeEventListener(XFormsEventNames.DISABLED, this.disabledListener, bubbles);
        eventTarget.removeEventListener(BetterFormEventNames.STATE_CHANGED, this.stateChangedListener, bubbles);
    }

}

// end of class
