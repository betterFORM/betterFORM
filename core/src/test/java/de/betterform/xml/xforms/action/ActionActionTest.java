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
package de.betterform.xml.xforms.action;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.events.EventTarget;

/**
 * Test cases for the action action (deferred update behvaiour and further
 * update sequencing).
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ActionActionTest.java 3264 2008-07-16 12:26:54Z joern $
 */
public class ActionActionTest extends BetterFormTestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private EventCountListener rebuildCountListener;
    private EventCountListener recalculateCountListener;
    private EventCountListener revalidateCountListener;
    private EventCountListener refreshCountListener;

    /**
     * Tests an update w/o outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUpdate() throws Exception {
        this.processor.dispatch("trigger-update", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(2, this.recalculateCountListener.getCount());
        assertEquals(2, this.revalidateCountListener.getCount());
        assertEquals(2, this.refreshCountListener.getCount());
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests an update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUpdateHandler() throws Exception {
        this.processor.dispatch("trigger-update-handler", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(1, this.recalculateCountListener.getCount());
        assertEquals(1, this.revalidateCountListener.getCount());
        assertEquals(1, this.refreshCountListener.getCount());
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests an update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUpdateHandlerFlags() throws Exception {
        this.processor.dispatch("trigger-update-handler-flags", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(0, this.recalculateCountListener.getCount());
        assertEquals(0, this.revalidateCountListener.getCount());
        assertEquals(0, this.refreshCountListener.getCount());
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests a nested update w/o outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testNestedUpdate() throws Exception {
        this.processor.dispatch("trigger-nested-update", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(3, this.recalculateCountListener.getCount());
        assertEquals(3, this.revalidateCountListener.getCount());
        assertEquals(3, this.refreshCountListener.getCount());
        assertEquals("4", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests a nested update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testNestedUpdateHandler() throws Exception {
        this.processor.dispatch("trigger-nested-update-handler", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(1, this.recalculateCountListener.getCount());
        assertEquals(1, this.revalidateCountListener.getCount());
        assertEquals(1, this.refreshCountListener.getCount());
        assertEquals("4", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests a dependant update w/o outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDependantUpdate() throws Exception {
        this.processor.dispatch("trigger-dependant-update", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(4, this.recalculateCountListener.getCount());
        assertEquals(4, this.revalidateCountListener.getCount());
        assertEquals(4, this.refreshCountListener.getCount());
        assertEquals("4", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests a dependant update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDependantUpdateHandler() throws Exception {
        this.processor.dispatch("trigger-dependant-update-handler", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(1, this.recalculateCountListener.getCount());
        assertEquals(1, this.revalidateCountListener.getCount());
        assertEquals(1, this.refreshCountListener.getCount());
        assertEquals("4", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests a dependant update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDependantUpdateNestedHandler() throws Exception {
        this.processor.dispatch("trigger-dependant-update-nested-handler", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(1, this.recalculateCountListener.getCount());
        assertEquals(1, this.revalidateCountListener.getCount());
        assertEquals(1, this.refreshCountListener.getCount());
        assertEquals("4", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests a sequence update w/o outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSequenceUpdate() throws Exception {
        this.processor.dispatch("trigger-sequence-update", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(6, this.recalculateCountListener.getCount());
        assertEquals(6, this.revalidateCountListener.getCount());
        assertEquals(6, this.refreshCountListener.getCount());
        assertEquals("4", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[2])"));
    }

    /**
     * Tests a sequence update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSequenceUpdateHandler() throws Exception {
        this.processor.dispatch("trigger-sequence-update-handler", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(3, this.recalculateCountListener.getCount());
        assertEquals(3, this.revalidateCountListener.getCount());
        assertEquals(3, this.refreshCountListener.getCount());
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[3])"));
    }

    /**
     * Tests a sequence update w/ outermost action handler.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSequenceUpdateNestedHandler() throws Exception {
        this.processor.dispatch("trigger-sequence-update-nested-handler", DOMEventNames.ACTIVATE);

        assertEquals(0, this.rebuildCountListener.getCount());
        assertEquals(2, this.recalculateCountListener.getCount());
        assertEquals(2, this.revalidateCountListener.getCount());
        assertEquals(2, this.refreshCountListener.getCount());
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("2", evaluateInDefaultContextAsString("string(/data/item[4])"));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.rebuildCountListener = new EventCountListener(XFormsEventNames.REBUILD);
        this.recalculateCountListener = new EventCountListener(XFormsEventNames.RECALCULATE);
        this.revalidateCountListener = new EventCountListener(XFormsEventNames.REVALIDATE);
        this.refreshCountListener = new EventCountListener(XFormsEventNames.REFRESH);

        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.addEventListener(XFormsEventNames.REBUILD, this.rebuildCountListener, true);
        eventTarget.addEventListener(XFormsEventNames.RECALCULATE, this.recalculateCountListener, true);
        eventTarget.addEventListener(XFormsEventNames.REVALIDATE, this.revalidateCountListener, true);
        eventTarget.addEventListener(XFormsEventNames.REFRESH, this.refreshCountListener, true);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.removeEventListener(XFormsEventNames.REBUILD, this.rebuildCountListener, true);
        eventTarget.removeEventListener(XFormsEventNames.RECALCULATE, this.recalculateCountListener, true);
        eventTarget.removeEventListener(XFormsEventNames.REVALIDATE, this.revalidateCountListener, true);
        eventTarget.removeEventListener(XFormsEventNames.REFRESH, this.refreshCountListener, true);

        this.rebuildCountListener = null;
        this.recalculateCountListener = null;
        this.revalidateCountListener = null;
        this.refreshCountListener = null;

        super.tearDown();
    }

    protected String getTestCaseURI() {
        return "ActionActionTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
