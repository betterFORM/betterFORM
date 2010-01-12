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
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.model.XercesElementImpl;
import de.betterform.xml.xforms.model.XercesNodeImpl;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

/**
 * Model item tests.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ModelItemValueTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class ModelItemValueTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private static String XML =
            "<data xmlns:xs='" + NamespaceConstants.XMLSCHEMA_NS + "' xmlns:xsi='" + NamespaceConstants.XMLSCHEMA_INSTANCE_NS + "'>" +
                "<element attribute='value'/>" +
                "<text>I am TEXT</text>" + "" +
                "<cdata><![CDATA[I am <b>CDATA</b>]]></cdata>" +
                "<nillable xsi:nil='true' xsi:type='xs:token'/>" +
            "</data>";

    private Document document;

    /**
     * Tests value retrieval for elements.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetValueForElements() throws Exception {
        ModelItem modelItem;

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0));
        assertEquals("", modelItem.getValue());

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("text").item(0));
        assertEquals("I am TEXT", modelItem.getValue());

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("cdata").item(0));
        assertEquals("I am <b>CDATA</b>", modelItem.getValue());
    }

    /**
     * Tests value retrieval for attributes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetValueForAttributes() throws Exception {
        ModelItem modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0).getAttributes().getNamedItem("attribute"));
        assertEquals("value", modelItem.getValue());
    }

    /**
     * Tests value retrieval for text nodes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetValueForTextNodes() throws Exception {
        ModelItem modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("text").item(0).getFirstChild());
        assertEquals("I am TEXT", modelItem.getValue());
    }

    /**
     * Tests value retrieval for text nodes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetValueForCDATASections() throws Exception {
        ModelItem modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("cdata").item(0).getFirstChild());
        assertEquals("I am <b>CDATA</b>", modelItem.getValue());
    }

    /**
     * Tests value update for elements.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueForElements() throws Exception {
        ModelItem modelItem;

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0));
        modelItem.setValue("test");
        assertEquals("test", this.document.getElementsByTagName("element").item(0).getFirstChild().getNodeValue());

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("text").item(0));
        modelItem.setValue("test");
        assertEquals("test", this.document.getElementsByTagName("element").item(0).getFirstChild().getNodeValue());

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("cdata").item(0));
        modelItem.setValue("test");
        assertEquals("test", this.document.getElementsByTagName("element").item(0).getFirstChild().getNodeValue());
    }

    /**
     * Tests value update for attributes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueForAttributes() throws Exception {
        ModelItem modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0).getAttributes().getNamedItem("attribute"));
        modelItem.setValue("test");
        assertEquals("test", this.document.getElementsByTagName("element").item(0).getAttributes().getNamedItem("attribute").getNodeValue());
    }

    /**
     * Tests value update for text nodes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueForTextNodes() throws Exception {
        ModelItem modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("text").item(0).getFirstChild());
        modelItem.setValue("test");
        assertEquals("test", this.document.getElementsByTagName("text").item(0).getFirstChild().getNodeValue());
    }

    /**
     * Tests value update for text nodes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueForCDATASections() throws Exception {
        ModelItem modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("cdata").item(0).getFirstChild());
        modelItem.setValue("test");
        assertEquals("test", this.document.getElementsByTagName("cdata").item(0).getFirstChild().getNodeValue());
    }

    /**
     * Tests @xsi:nil support.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIsXSINillable() throws Exception {
        ModelItem modelItem;

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0));
        assertEquals(false, modelItem.isXSINillable());

        modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0).getAttributes().getNamedItem("attribute"));
        assertEquals(false, modelItem.isXSINillable());

        modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("text").item(0).getFirstChild());
        assertEquals(false, modelItem.isXSINillable());

        modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("cdata").item(0).getFirstChild());
        assertEquals(false, modelItem.isXSINillable());

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("nillable").item(0));
        assertEquals(true, modelItem.isXSINillable());
    }

    /**
     * Tests @xsi:type support.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetXSIType() throws Exception {
        ModelItem modelItem;

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0));
        assertEquals(null, modelItem.getXSIType());

        modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("element").item(0).getAttributes().getNamedItem("attribute"));
        assertEquals(null, modelItem.getXSIType());

        modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("text").item(0).getFirstChild());
        assertEquals(null, modelItem.getXSIType());

        modelItem = new XercesNodeImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("cdata").item(0).getFirstChild());
        assertEquals(null, modelItem.getXSIType());

        modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("nillable").item(0));
        assertEquals("xs:token", modelItem.getXSIType());
    }

    /**
     * Tests @xsi:nil support for value update.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetValueNillable() throws Exception {
        ModelItem modelItem = new XercesElementImpl(String.valueOf(System.currentTimeMillis()));
        modelItem.setNode(this.document.getElementsByTagName("nillable").item(0));

        modelItem.setValue("test");
        assertEquals("false", this.document.getElementsByTagName("nillable").item(0).getAttributes().getNamedItemNS(NamespaceConstants.XMLSCHEMA_INSTANCE_NS, "nil").getNodeValue());
        modelItem.setValue(null);
        assertEquals("true", this.document.getElementsByTagName("nillable").item(0).getAttributes().getNamedItemNS(NamespaceConstants.XMLSCHEMA_INSTANCE_NS, "nil").getNodeValue());
        modelItem.setValue("test");
        assertEquals("false", this.document.getElementsByTagName("nillable").item(0).getAttributes().getNamedItemNS(NamespaceConstants.XMLSCHEMA_INSTANCE_NS, "nil").getNodeValue());
        modelItem.setValue("");
        assertEquals("true", this.document.getElementsByTagName("nillable").item(0).getAttributes().getNamedItemNS(NamespaceConstants.XMLSCHEMA_INSTANCE_NS, "nil").getNodeValue());
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
        this.document = factory.newDocumentBuilder().parse(new ByteArrayInputStream(XML.getBytes("UTF-8")));
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.document = null;
    }

}
