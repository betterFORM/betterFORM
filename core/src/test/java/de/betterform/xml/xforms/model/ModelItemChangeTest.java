// Copyright 2010 betterForm
/*
 *
 *    Artistic License
 *
 *    Preamble
 *
 *    The intent of this document is to state the conditions under which a
 *    Package may be copied, such that the Copyright Holder maintains some
 *    semblance of artistic control over the development of the package,
 *    while giving the users of the package the right to use and distribute
 *    the Package in a more-or-less customary fashion, plus the right to make
 *    reasonable modifications.
 *
 *    Definitions:
 *
 *    "Package" refers to the collection of files distributed by the
 *    Copyright Holder, and derivatives of that collection of files created
 *    through textual modification.
 *
 *    "Standard Version" refers to such a Package if it has not been
 *    modified, or has been modified in accordance with the wishes of the
 *    Copyright Holder.
 *
 *    "Copyright Holder" is whoever is named in the copyright or copyrights
 *    for the package.
 *
 *    "You" is you, if you're thinking about copying or distributing this Package.
 *
 *    "Reasonable copying fee" is whatever you can justify on the basis of
 *    media cost, duplication charges, time of people involved, and so
 *    on. (You will not be required to justify it to the Copyright Holder,
 *    but only to the computing community at large as a market that must bear
 *    the fee.)
 *
 *    "Freely Available" means that no fee is charged for the item itself,
 *    though there may be fees involved in handling the item. It also means
 *    that recipients of the item may redistribute it under the same
 *    conditions they received it.
 *
 *    1. You may make and give away verbatim copies of the source form of the
 *    Standard Version of this Package without restriction, provided that you
 *    duplicate all of the original copyright notices and associated
 *    disclaimers.
 *
 *    2. You may apply bug fixes, portability fixes and other modifications
 *    derived from the Public Domain or from the Copyright Holder. A Package
 *    modified in such a way shall still be considered the Standard Version.
 *
 *    3. You may otherwise modify your copy of this Package in any way,
 *    provided that you insert a prominent notice in each changed file
 *    stating how and when you changed that file, and provided that you do at
 *    least ONE of the following:
 *
 *        a) place your modifications in the Public Domain or otherwise make
 *        them Freely Available, such as by posting said modifications to
 *        Usenet or an equivalent medium, or placing the modifications on a
 *        major archive site such as ftp.uu.net, or by allowing the Copyright
 *        Holder to include your modifications in the Standard Version of the
 *        Package.
 *
 *        b) use the modified Package only within your corporation or
 *        organization.
 *
 *        c) rename any non-standard executables so the names do not conflict
 *        with standard executables, which must also be provided, and provide
 *        a separate manual page for each non-standard executable that
 *        clearly documents how it differs from the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    4. You may distribute the programs of this Package in object code or
 *    executable form, provided that you do at least ONE of the following:
 *
 *        a) distribute a Standard Version of the executables and library
 *        files, together with instructions (in the manual page or
 *        equivalent) on where to get the Standard Version.
 *
 *        b) accompany the distribution with the machine-readable source of
 *        the Package with your modifications.
 *
 *        c) accompany any non-standard executables with their corresponding
 *        Standard Version executables, giving the non-standard executables
 *        non-standard names, and clearly documenting the differences in
 *        manual pages (or equivalent), together with instructions on where
 *        to get the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    5. You may charge a reasonable copying fee for any distribution of this
 *    Package. You may charge any fee you choose for support of this
 *    Package. You may not charge a fee for this Package itself.  However,
 *    you may distribute this Package in aggregate with other (possibly
 *    commercial) programs as part of a larger (possibly commercial) software
 *    distribution provided that you do not advertise this Package as a
 *    product of your own.
 *
 *    6. The scripts and library files supplied as input to or produced as
 *    output from the programs of this Package do not automatically fall
 *    under the copyright of this Package, but belong to whomever generated
 *    them, and may be sold commercially, and may be aggregated with this
 *    Package.
 *
 *    7. C or perl subroutines supplied by you and linked into this Package
 *    shall not be considered part of this Package.
 *
 *    8. The name of the Copyright Holder may not be used to endorse or
 *    promote products derived from this software without specific prior
 *    written permission.
 *
 *    9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package de.betterform.xml.xforms.model;

import junit.framework.TestCase;
import de.betterform.xml.xforms.model.XercesElementImpl;
import de.betterform.xml.xforms.model.XercesNodeImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Model item tests.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ModelItemChangeTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class ModelItemChangeTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private ModelItem modelItem;
    private ModelItem parentItem;

    /**
     * Tests no change.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testNoChange() throws Exception {
        assertEquals(false, this.parentItem.getStateChangeView().hasValueChanged());
        assertEquals(false, this.parentItem.getStateChangeView().hasValidChanged());
        assertEquals(false, this.parentItem.getStateChangeView().hasReadonlyChanged());
        assertEquals(false, this.parentItem.getStateChangeView().hasRequiredChanged());
        assertEquals(false, this.parentItem.getStateChangeView().hasEnabledChanged());

        assertEquals(false, this.modelItem.getStateChangeView().hasValueChanged());
        assertEquals(false, this.modelItem.getStateChangeView().hasValidChanged());
        assertEquals(false, this.modelItem.getStateChangeView().hasReadonlyChanged());
        assertEquals(false, this.modelItem.getStateChangeView().hasRequiredChanged());
        assertEquals(false, this.modelItem.getStateChangeView().hasEnabledChanged());
    }

    /**
     * Tests valid changes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidChange() throws Exception {
        assertValidChange(true, true, true, true, false);
        assertValidChange(true, true, true, false, true);
        assertValidChange(true, false, true, true, true);
        assertValidChange(true, false, true, false, true);
        assertValidChange(true, true, false, true, true);
        assertValidChange(true, true, false, false, false);
        assertValidChange(true, false, false, true, false);
        assertValidChange(true, false, false, false, false);
        assertValidChange(false, true, true, true, true);
        assertValidChange(false, true, true, false, false);
        assertValidChange(false, false, true, true, false);
        assertValidChange(false, false, true, false, false);
        assertValidChange(false, true, false, true, true);
        assertValidChange(false, true, false, false, false);
        assertValidChange(false, false, false, true, false);
        assertValidChange(false, false, false, false, false);
    }

    /**
     * Tests readonly changes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testReadonlyChange() throws Exception {
        assertReadonlyChange(true, true, true, true, false);
        assertReadonlyChange(true, true, true, false, false);
        assertReadonlyChange(true, false, true, true, false);
        assertReadonlyChange(true, false, true, false, true);
        assertReadonlyChange(true, true, false, true, false);
        assertReadonlyChange(true, true, false, false, false);
        assertReadonlyChange(true, false, false, true, false);
        assertReadonlyChange(true, false, false, false, true);
        assertReadonlyChange(false, true, true, true, false);
        assertReadonlyChange(false, true, true, false, false);
        assertReadonlyChange(false, false, true, true, false);
        assertReadonlyChange(false, false, true, false, true);
        assertReadonlyChange(false, true, false, true, true);
        assertReadonlyChange(false, true, false, false, true);
        assertReadonlyChange(false, false, false, true, true);
        assertReadonlyChange(false, false, false, false, false);
    }

    /**
     * Tests required changes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testRequiredChange() throws Exception {
        assertRequiredChange(true, true, false);
        assertRequiredChange(true, false, true);
        assertRequiredChange(false, true, true);
        assertRequiredChange(false, false, false);
    }

    /**
     * Tests enabled changes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testEnabledChange() throws Exception {
        assertEnabledChange(true, true, true, true, false);
        assertEnabledChange(true, true, true, false, true);
        assertEnabledChange(true, false, true, true, true);
        assertEnabledChange(true, false, true, false, true);
        assertEnabledChange(true, true, false, true, true);
        assertEnabledChange(true, true, false, false, false);
        assertEnabledChange(true, false, false, true, false);
        assertEnabledChange(true, false, false, false, false);
        assertEnabledChange(false, true, true, true, true);
        assertEnabledChange(false, true, true, false, false);
        assertEnabledChange(false, false, true, true, false);
        assertEnabledChange(false, false, true, false, false);
        assertEnabledChange(false, true, false, true, true);
        assertEnabledChange(false, true, false, false, false);
        assertEnabledChange(false, false, false, true, false);
        assertEnabledChange(false, false, false, false, false);
    }

    /**
     * Tests value change.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValueChange() throws Exception {
        this.parentItem.setValue("");
        assertEquals(false, this.parentItem.getStateChangeView().hasValueChanged());
        this.parentItem.setValue("test");
        assertEquals(true, this.parentItem.getStateChangeView().hasValueChanged());

        this.modelItem.setValue("");
        assertEquals(false, this.modelItem.getStateChangeView().hasValueChanged());
        this.modelItem.setValue("test");
        assertEquals(true, this.modelItem.getStateChangeView().hasValueChanged());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        Document document = factory.newDocumentBuilder().newDocument();
        Element element = document.createElement("element");
        document.appendChild(element);
        Attr attr = document.createAttribute("attribute");
        element.setAttributeNode(attr);

        this.parentItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        this.parentItem.setNode(element);

        this.modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        this.modelItem.setNode(attr);
        this.modelItem.setParent(this.parentItem);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.modelItem.setParent(null);
        this.modelItem = null;
        this.parentItem = null;
    }

    private void assertValidChange(boolean constraintBefore, boolean constraintAfter, boolean datatypeBefore, boolean datatypeAfter, boolean expected) {
        this.modelItem.getLocalUpdateView().setConstraintValid(constraintBefore);
        this.modelItem.getLocalUpdateView().setDatatypeValid(datatypeBefore);
        this.modelItem.getStateChangeView().reset();

        this.modelItem.getLocalUpdateView().setConstraintValid(constraintAfter);
        this.modelItem.getLocalUpdateView().setDatatypeValid(datatypeAfter);
        assertEquals(expected, this.modelItem.getStateChangeView().hasValidChanged());
    }

    private void assertReadonlyChange(boolean localBefore, boolean localAfter, boolean parentBefore, boolean parentAfter, boolean expected) {
        this.parentItem.getLocalUpdateView().setLocalReadonly(parentBefore);
        this.parentItem.getStateChangeView().reset();
        this.modelItem.getLocalUpdateView().setLocalReadonly(localBefore);
        this.modelItem.getStateChangeView().reset();

        this.parentItem.getLocalUpdateView().setLocalReadonly(parentAfter);
        this.modelItem.getLocalUpdateView().setLocalReadonly(localAfter);
        assertEquals(expected, this.modelItem.getStateChangeView().hasReadonlyChanged());
    }

    private void assertRequiredChange(boolean localBefore, boolean localAfter, boolean expected) {
        this.modelItem.getLocalUpdateView().setLocalRequired(localBefore);
        this.modelItem.getStateChangeView().reset();

        this.modelItem.getLocalUpdateView().setLocalRequired(localAfter);
        assertEquals(expected, this.modelItem.getStateChangeView().hasRequiredChanged());
    }

    private void assertEnabledChange(boolean localBefore, boolean localAfter, boolean parentBefore, boolean parentAfter, boolean expected) {
        this.parentItem.getLocalUpdateView().setLocalRelevant(parentBefore);
        this.parentItem.getStateChangeView().reset();
        this.modelItem.getLocalUpdateView().setLocalRelevant(localBefore);
        this.modelItem.getStateChangeView().reset();

        this.parentItem.getLocalUpdateView().setLocalRelevant(parentAfter);
        this.modelItem.getLocalUpdateView().setLocalRelevant(localAfter);
        assertEquals(expected, this.modelItem.getStateChangeView().hasEnabledChanged());
    }

}
