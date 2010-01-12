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
package de.betterform.xml.ns;

import junit.framework.TestCase;
import de.betterform.xml.dom.DOMUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Map;

/**
 * Tests the namespace resolver.
 *
 * @author Ulrich Nicolas Liss&eacute;, Joern Turner
 * @version $Id: NamespaceResolverTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class NamespaceResolverTest extends TestCase {

    private Element root = null;
    private Document document;

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        document = builder.parse(getClass().getResourceAsStream("NamespaceResolverTest.xhtml"));
        NamespaceResolver.init(document.getDocumentElement());
        this.root = (Element) document.getElementsByTagName("root").item(0);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.root = null;
    }

    public void testGetAllNamespacesHost() throws Exception {
        Map map = NamespaceResolver.getAllNamespaces(this.document.getDocumentElement());

        assertEquals("wrong number of namespaces in scope", 6, map.size());
        assertEquals("wrong 'xf' namespace", "http://www.w3.org/2002/xforms", map.get("xf"));
        assertEquals("wrong 'ev' namespace", "http://www.w3.org/2001/xml-events", map.get("ev"));
        assertEquals("wrong 'xsi' namespace", "http://www.w3.org/2001/XMLSchema-instance", map.get("xsi"));
        assertEquals("wrong 'xsd' namespace", "http://www.w3.org/2001/XMLSchema", map.get("xsd"));
        assertEquals("wrong 'test' namespace", "http://tempuri.org/ns1", map.get("test"));
    }

    /**
     * Test case for resolving all namespace declarations.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetAllNamespaces() throws Exception {
        Map map = NamespaceResolver.getAllNamespaces(this.root);

        assertEquals("wrong number of namespaces in scope", 7, map.size());

        assertEquals("wrong 'xf' namespace", "http://www.w3.org/2002/xforms", map.get("xf"));
        assertEquals("wrong 'ev' namespace", "http://www.w3.org/2001/xml-events", map.get("ev"));
        assertEquals("wrong 'xsi' namespace", "http://www.w3.org/2001/XMLSchema-instance", map.get("xsi"));
        assertEquals("wrong 'xsd' namespace", "http://www.w3.org/2001/XMLSchema", map.get("xsd"));
        assertEquals("wrong 'betterform' namespace", "http://betterform.sourceforge.net/xforms", map.get("betterform"));
        assertEquals("wrong 'test' namespace", "http://tempuri.org/ns2", map.get("test"));
    }

    /**
     * Test case for resolving namespace uris.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetNamespaceURI() throws Exception {
        assertNull("wrong unknown namespace", NamespaceResolver.getNamespaceURI(this.root, "xxx"));
        assertEquals("wrong default namespace", "", NamespaceResolver.getNamespaceURI(this.root, null));
        assertEquals("wrong default namespace", "", NamespaceResolver.getNamespaceURI(this.root, ""));

        assertEquals("wrong 'xf' namespace", "http://www.w3.org/2002/xforms", NamespaceResolver.getNamespaceURI(this.root, "xf"));
        assertEquals("wrong 'ev' namespace", "http://www.w3.org/2001/xml-events", NamespaceResolver.getNamespaceURI(this.root, "ev"));
        assertEquals("wrong 'xsi' namespace", "http://www.w3.org/2001/XMLSchema-instance", NamespaceResolver.getNamespaceURI(this.root, "xsi"));
        assertEquals("wrong 'xsd' namespace", "http://www.w3.org/2001/XMLSchema", NamespaceResolver.getNamespaceURI(this.root, "xsd"));
        assertEquals("wrong 'betterform' namespace", "http://betterform.sourceforge.net/xforms", NamespaceResolver.getNamespaceURI(this.root, "betterform"));

        assertEquals("wrong 'test' namespace", "http://tempuri.org/ns1", NamespaceResolver.getNamespaceURI(this.root.getOwnerDocument().getDocumentElement(), "test"));
        assertEquals("wrong 'test' namespace", "http://tempuri.org/ns2", NamespaceResolver.getNamespaceURI(this.root, "test"));
    }

    /**
     * Test case for resolving namespace prefixes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetPrefix() throws Exception {
        assertNull("wrong unknown namespace", NamespaceResolver.getPrefix(this.root, "http://tempuri.org/ns"));
        assertEquals("wrong default namespace", "", NamespaceResolver.getPrefix(this.root, ""));

        assertEquals("wrong 'xf' namespace", "xf", NamespaceResolver.getPrefix(this.root, "http://www.w3.org/2002/xforms"));
        assertEquals("wrong 'ev' namespace", "ev", NamespaceResolver.getPrefix(this.root, "http://www.w3.org/2001/xml-events"));
        assertEquals("wrong 'xsi' namespace", "xsi", NamespaceResolver.getPrefix(this.root, "http://www.w3.org/2001/XMLSchema-instance"));
        assertEquals("wrong 'xsd' namespace", "xsd", NamespaceResolver.getPrefix(this.root, "http://www.w3.org/2001/XMLSchema"));
        assertEquals("wrong 'betterform' namespace", "betterform", NamespaceResolver.getPrefix(this.root, "http://betterform.sourceforge.net/xforms"));

        assertEquals("wrong 'test' namespace", "test", NamespaceResolver.getPrefix(this.root.getOwnerDocument().getDocumentElement(), "http://tempuri.org/ns1"));
        assertEquals("wrong 'test' namespace", "test", NamespaceResolver.getPrefix(this.root, "http://tempuri.org/ns2"));
    }

    /**
     * Test case for applying namespace declarations.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testApplyNamespaces() throws Exception {
        NamespaceResolver.applyNamespaces(this.root.getOwnerDocument().getDocumentElement(), this.root);
        
        assertNotNull("wrong default namespace", this.root.getAttributeNodeNS(NamespaceConstants.XMLNS_NS, "xmlns"));
        assertEquals("wrong default namespace", "", this.root.getAttributeNS(NamespaceConstants.XMLNS_NS, "xmlns"));

        assertEquals("wrong 'xf' namespace", "http://www.w3.org/2002/xforms", this.root.getAttributeNS(NamespaceConstants.XMLNS_NS, "xf"));
        assertEquals("wrong 'ev' namespace", "http://www.w3.org/2001/xml-events", this.root.getAttributeNS(NamespaceConstants.XMLNS_NS, "ev"));
        assertEquals("wrong 'xsi' namespace", "http://www.w3.org/2001/XMLSchema-instance", this.root.getAttributeNS(NamespaceConstants.XMLNS_NS, "xsi"));
        assertEquals("wrong 'xsd' namespace", "http://www.w3.org/2001/XMLSchema", this.root.getAttributeNS(NamespaceConstants.XMLNS_NS, "xsd"));
        assertNull("wrong 'betterform' namespace", this.root.getAttributeNodeNS(NamespaceConstants.XMLNS_NS, "betterform"));

        assertEquals("wrong 'test' namespace", "http://tempuri.org/ns1", this.root.getAttributeNS(NamespaceConstants.XMLNS_NS, "test"));
    }

    /**
     * Test case for applying namespace declarations.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetExpandedName() throws Exception {
        assertEquals("foo", NamespaceResolver.getExpandedName(this.root.getOwnerDocument().getDocumentElement(), "foo"));
        assertEquals("{http://www.w3.org/2002/xforms}foo", NamespaceResolver.getExpandedName(this.root.getOwnerDocument().getDocumentElement(), "xf:foo"));
        assertEquals("{http://www.w3.org/2001/xml-events}foo", NamespaceResolver.getExpandedName(this.root.getOwnerDocument().getDocumentElement(), "ev:foo"));
        assertEquals("{http://www.w3.org/2001/XMLSchema-instance}foo", NamespaceResolver.getExpandedName(this.root.getOwnerDocument().getDocumentElement(), "xsi:foo"));
        assertEquals("{http://www.w3.org/2001/XMLSchema}foo", NamespaceResolver.getExpandedName(this.root.getOwnerDocument().getDocumentElement(), "xsd:foo"));
        assertEquals("{http://tempuri.org/ns1}foo", NamespaceResolver.getExpandedName(this.root.getOwnerDocument().getDocumentElement(), "test:foo"));
        assertEquals("foo", NamespaceResolver.getExpandedName(this.root.getOwnerDocument().getDocumentElement(), "xxx:foo"));
    }

}
