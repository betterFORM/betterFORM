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
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
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
        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:type"));


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
        assertEquals("", XPathUtil.evaluateAsString(host, "//*[@id='input-missing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-missing']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-missing']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-missing']/bf:data/@bf:required"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-missing']/bf:data/@bf:enabled"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "boolean(//*[@id='input-missing']/bf:data/@bf:type)"));

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

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id='input-missing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-missing']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-missing']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-missing']/bf:data/@bf:required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-missing']/bf:data/@bf:enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id='input-missing']/bf:data/@bf:type"));

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

        assertEquals("", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:required"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:enabled"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "boolean(//*[@id='input-existing']/bf:data/@bf:type)"));

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

        assertEquals("foobar", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:type"));

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

        assertEquals("01.01.2001", XPathUtil.evaluateAsString(host, "//*[@id='input-date']/bf:data"));

        assertEquals("2001-01-01", XPathUtil.evaluateAsString(host, "//*[@id='input-date']/bf:data/@bf:schema-value"));
        assertEquals("date", XPathUtil.evaluateAsString(host, "//*[@id='input-date']/bf:data/@bf:type"));

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

        assertEquals("foobar", XPathUtil.evaluateAsString(host, "//*[@id='input-trigger']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-trigger']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-trigger']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-trigger']/bf:data/@bf:required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-trigger']/bf:data/@bf:enabled"));
        assertEquals("xs:string", XPathUtil.evaluateAsString(host, "//*[@id='input-trigger']/bf:data/@bf:type"));

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

        assertEquals("foobar", XPathUtil.evaluateAsString(host, "//*[@id='input-calculate']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-calculate']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-calculate']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-calculate']/bf:data/@bf:required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-calculate']/bf:data/@bf:enabled"));
        assertEquals("xs:string", XPathUtil.evaluateAsString(host, "//*[@id='input-calculate']/bf:data/@bf:type"));

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

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:type"));

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

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:type"));

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

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:valid"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:type"));

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

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:type"));

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

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:readonly"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:type"));

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

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:type"));

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

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:required"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:type"));

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

        assertEquals("item", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data"));
        assertEquals("true", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:valid"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:readonly"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:required"));
        assertEquals("false", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:enabled"));
        assertEquals("xs:token", XPathUtil.evaluateAsString(host, "//*[@id='input-existing']/bf:data/@bf:type"));

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
