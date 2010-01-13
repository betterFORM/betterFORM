// Copyright 2010 betterForm
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
