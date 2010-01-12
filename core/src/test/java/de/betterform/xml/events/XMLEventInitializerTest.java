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
package de.betterform.xml.events;

import junit.framework.TestCase;
import de.betterform.xml.events.impl.DefaultXMLEventInitializer;
import de.betterform.xml.events.impl.XercesXMLEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for XMLEventInitializer implementations.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XMLEventInitializerTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class XMLEventInitializerTest extends TestCase {

    private XMLEvent xmlEvent;
    private XMLEventInitializer xmlEventInitializer;

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsModelConstruct() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.MODEL_CONSTRUCT, false, true, "context");

        assertEquals(XFormsEventNames.MODEL_CONSTRUCT, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsModelConstructDone() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.MODEL_CONSTRUCT_DONE, false, true, "context");

        assertEquals(XFormsEventNames.MODEL_CONSTRUCT_DONE, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsReady() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.READY, false, true, "context");

        assertEquals(XFormsEventNames.READY, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsModelDestruct() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.MODEL_DESTRUCT, false, true, "context");

        assertEquals(XFormsEventNames.MODEL_DESTRUCT, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsPrevious() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.PREVIOUS, false, true, "context");

        assertEquals(XFormsEventNames.PREVIOUS, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsNext() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.NEXT, false, true, "context");

        assertEquals(XFormsEventNames.NEXT, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsFocus() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.FOCUS, false, true, "context");

        assertEquals(XFormsEventNames.FOCUS, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsHelp() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.HELP, false, false, "context");

        assertEquals(XFormsEventNames.HELP, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(true, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsHint() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.HINT, false, false, "context");

        assertEquals(XFormsEventNames.HINT, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(true, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsRebuild() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.REBUILD, false, false, "context");

        assertEquals(XFormsEventNames.REBUILD, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(true, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsRefresh() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.REFRESH, false, false, "context");

        assertEquals(XFormsEventNames.REFRESH, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(true, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsRevalidate() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.REVALIDATE, false, false, "context");

        assertEquals(XFormsEventNames.REVALIDATE, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(true, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsRecalculate() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.RECALCULATE, false, false, "context");

        assertEquals(XFormsEventNames.RECALCULATE, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(true, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsReset() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.RESET, false, false, "context");

        assertEquals(XFormsEventNames.RESET, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(true, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsSubmit() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.SUBMIT, false, false, "context");

        assertEquals(XFormsEventNames.SUBMIT, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(true, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitDOMActivate() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, DOMEventNames.ACTIVATE, false, false, "context");

        assertEquals(DOMEventNames.ACTIVATE, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(true, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsValueChanged() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.VALUE_CHANGED, false, true, "context");

        assertEquals(XFormsEventNames.VALUE_CHANGED, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsSelect() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.SELECT, false, true, "context");

        assertEquals(XFormsEventNames.SELECT, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsDeselect() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.DESELECT, false, true, "context");

        assertEquals(XFormsEventNames.DESELECT, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsScrollFirst() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.SCROLL_FIRST, false, true, "context");

        assertEquals(XFormsEventNames.SCROLL_FIRST, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsScrollLast() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.SCROLL_LAST, false, true, "context");

        assertEquals(XFormsEventNames.SCROLL_LAST, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsInsert() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.INSERT, false, true, "context");

        assertEquals(XFormsEventNames.INSERT, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsDelete() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.DELETE, false, true, "context");

        assertEquals(XFormsEventNames.DELETE, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsValid() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.VALID, false, true, "context");

        assertEquals(XFormsEventNames.VALID, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsInvalid() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.INVALID, false, true, "context");

        assertEquals(XFormsEventNames.INVALID, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitDOMFocusIn() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, DOMEventNames.FOCUS_IN, false, true, "context");

        assertEquals(DOMEventNames.FOCUS_IN, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitDOMFocusOut() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, DOMEventNames.FOCUS_OUT, false, true, "context");

        assertEquals(DOMEventNames.FOCUS_OUT, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsReadonly() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.READONLY, false, true, "context");

        assertEquals(XFormsEventNames.READONLY, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsReadwrite() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.READWRITE, false, true, "context");

        assertEquals(XFormsEventNames.READWRITE, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsRequired() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.REQUIRED, false, true, "context");

        assertEquals(XFormsEventNames.REQUIRED, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsOptional() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.OPTIONAL, false, true, "context");

        assertEquals(XFormsEventNames.OPTIONAL, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsEnabled() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.ENABLED, false, true, "context");

        assertEquals(XFormsEventNames.ENABLED, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsDisabled() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.DISABLED, false, true, "context");

        assertEquals(XFormsEventNames.DISABLED, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsInRange() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.IN_RANGE, false, true, "context");

        assertEquals(XFormsEventNames.IN_RANGE, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsOutOfRange() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.OUT_OF_RANGE, false, true, "context");

        assertEquals(XFormsEventNames.OUT_OF_RANGE, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsSubmitDone() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.SUBMIT_DONE, false, true, "context");

        assertEquals(XFormsEventNames.SUBMIT_DONE, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals(null, this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsSubmitError() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.SUBMIT_ERROR, false, true, "context");

        assertEquals(XFormsEventNames.SUBMIT_ERROR, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsBindinggException() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.BINDING_EXCEPTION, false, true, "context");

        assertEquals(XFormsEventNames.BINDING_EXCEPTION, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsLinkException() throws Exception {
        Map props = new HashMap(1);
        props.put(XFormsEventNames.RESOURCE_URI_PROPERTY,"aURI");
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.LINK_EXCEPTION, false, true, props);

        assertEquals(XFormsEventNames.LINK_EXCEPTION, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("aURI", this.xmlEvent.getContextInfo(XFormsEventNames.RESOURCE_URI_PROPERTY));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsLinkError() throws Exception {
        Map props = new HashMap(1);
        props.put(XFormsEventNames.RESOURCE_URI_PROPERTY,"aURI");
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.LINK_ERROR, false, true, props);

        assertEquals(XFormsEventNames.LINK_ERROR, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("aURI", this.xmlEvent.getContextInfo(XFormsEventNames.RESOURCE_URI_PROPERTY));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXFormsComputeException() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, XFormsEventNames.COMPUTE_EXCEPTION, false, true, "context");

        assertEquals(XFormsEventNames.COMPUTE_EXCEPTION, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitBetterFormLoadURI() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, BetterFormEventNames.LOAD_URI, false, true, "context");

        assertEquals(BetterFormEventNames.LOAD_URI, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitBetterFormRenderMessage() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, BetterFormEventNames.RENDER_MESSAGE, false, true, "context");

        assertEquals(BetterFormEventNames.RENDER_MESSAGE, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitBetterFormReplaceAll() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, BetterFormEventNames.REPLACE_ALL, false, true, "context");

        assertEquals(BetterFormEventNames.REPLACE_ALL, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitBetterFormStateChanged() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, BetterFormEventNames.STATE_CHANGED, false, true, "context");

        assertEquals(BetterFormEventNames.STATE_CHANGED, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitBetterFormNodeInserted() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, BetterFormEventNames.NODE_INSERTED, false, true, "context");

        assertEquals(BetterFormEventNames.NODE_INSERTED, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitBetterFormNodeDeleted() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, BetterFormEventNames.NODE_DELETED, false, true, "context");

        assertEquals(BetterFormEventNames.NODE_DELETED, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitBetterFormPrototypeCloned() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, BetterFormEventNames.PROTOTYPE_CLONED, false, true, "context");

        assertEquals(BetterFormEventNames.PROTOTYPE_CLONED, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitBetterFormIDGenerated() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, BetterFormEventNames.ID_GENERATED, false, true, "context");

        assertEquals(BetterFormEventNames.ID_GENERATED, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitBetterFormItemInserted() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, BetterFormEventNames.ITEM_INSERTED, false, true, "context");

        assertEquals(BetterFormEventNames.ITEM_INSERTED, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitBetterFormItemDeleted() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, BetterFormEventNames.ITEM_DELETED, false, true, "context");

        assertEquals(BetterFormEventNames.ITEM_DELETED, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitBetterFormIndexChanged() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, BetterFormEventNames.INDEX_CHANGED, false, true, "context");

        assertEquals(BetterFormEventNames.INDEX_CHANGED, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitBetterFormSwitchToggled() throws Exception {
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, BetterFormEventNames.SWITCH_TOGGLED, false, true, "context");

        assertEquals(BetterFormEventNames.SWITCH_TOGGLED, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Tests event initialization.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitCustomEvent() throws Exception {
        String type = "some-arbitrary-name-" + System.currentTimeMillis();
        this.xmlEventInitializer.initXMLEvent(this.xmlEvent, type, true, false, "context");

        assertEquals(type, this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(false, this.xmlEvent.getCancelable());
        assertEquals("context", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xmlEvent = new XercesXMLEvent();
        this.xmlEventInitializer = new DefaultXMLEventInitializer();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.xmlEvent = null;
        this.xmlEventInitializer = null;
    }

}
