// Copyright 2010 betterForm
package de.betterform.xml.dom;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * DOMUtil blackbox tests
 *
 * @author joern turner
 * @version $Id: DOMUtilTest.java 3476 2008-08-18 21:53:47Z joern $
 */
public class DOMUtilTest extends TestCase {


    public void testCanonicalPath() throws Exception{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream("canonicalpath.xml"));


        String canonPath;
        assertEquals("/", DOMUtil.getCanonicalPath(document));

        Node root = document.getDocumentElement();
        canonPath = DOMUtil.getCanonicalPath(root);
        assertEquals("/root[1]",canonPath);

        Node foo1 =  DOMUtil.findNthChildNS(root,"","foo1",1);
        canonPath = DOMUtil.getCanonicalPath(foo1);
        assertEquals("/root[1]/foo1[1]",canonPath);

        Node foo2 =  DOMUtil.findNthChildNS(root,"","foo2",1);
        canonPath = DOMUtil.getCanonicalPath(foo2);
        assertEquals("/root[1]/foo2[1]",canonPath);


        Node elem1 = DOMUtil.findNthChildNS(root,"","elem1",1);
        canonPath = DOMUtil.getCanonicalPath(elem1);
        assertEquals("/root[1]/elem1[1]",canonPath);


        Node att1 = ((Element) elem1).getAttributeNode("att1");
        canonPath = DOMUtil.getCanonicalPath(att1);
        assertEquals("/root[1]/elem1[1]/@att1",canonPath);

        Node att2 = ((Element) elem1).getAttributeNode("att2");
        canonPath = DOMUtil.getCanonicalPath(att2);
        assertEquals("/root[1]/elem1[1]/@att2",canonPath);

        Node elem2 = DOMUtil.getLastChildElement(root);
        canonPath = DOMUtil.getCanonicalPath(elem2);
        assertEquals("/root[1]/elem1[2]",canonPath);

        Node elem22 = DOMUtil.getLastChildElement(elem2);
        canonPath = DOMUtil.getCanonicalPath(elem22);
        assertEquals("/root[1]/elem1[2]/elem2[2]",canonPath);

        Node elem221 = DOMUtil.getFirstChildElement(elem22);
        canonPath = DOMUtil.getCanonicalPath(elem221);
        assertEquals("/root[1]/elem1[2]/elem2[2]/elem3[1]",canonPath);
    }
    
    public void testCanonicalPathNS() throws Exception{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream("canonicalpathNS.xml"));


        String canonPath;
        Node root = document.getDocumentElement();
        canonPath = DOMUtil.getCanonicalPath(root);
        assertEquals("/root[1]",canonPath);

        Node elem1 = DOMUtil.findNthChildNS(root,"bar","elem1",1);
        canonPath = DOMUtil.getCanonicalPath(elem1);
        assertEquals("/root[1]/foo:elem1[1]",canonPath);


        Node att1 = ((Element) elem1).getAttributeNodeNS("bar","att1");
        canonPath = DOMUtil.getCanonicalPath(att1);
        assertEquals("/root[1]/foo:elem1[1]/@foo:att1",canonPath);

        Node att2 = ((Element) elem1).getAttributeNodeNS(null,"att2");
        canonPath = DOMUtil.getCanonicalPath(att2);
        assertEquals("/root[1]/foo:elem1[1]/@att2",canonPath);

        Node elem2 = DOMUtil.getLastChildElement(root);
        canonPath = DOMUtil.getCanonicalPath(elem2);
        assertEquals("/root[1]/foo:elem1[2]",canonPath);

        Node elem22 = DOMUtil.getLastChildElement(elem2);
        canonPath = DOMUtil.getCanonicalPath(elem22);
        assertEquals("/root[1]/foo:elem1[2]/foo:elem2[2]",canonPath);

        Node elem221 = DOMUtil.getFirstChildElement(elem22);
        canonPath = DOMUtil.getCanonicalPath(elem221);
        assertEquals("/root[1]/foo:elem1[2]/foo:elem2[2]/foo:elem3[1]",canonPath);
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testImportNode() throws Exception {
        Document in = getXmlResource("DOMUtilTest.xml");
        Document expected = getXmlResource("DOMUtilTest.xml");
        assertNotNull(in);
        assertNotNull(expected);

        Node result = DOMUtil.importNode(expected, in.getDocumentElement());

        DOMComparator comparator = new DOMComparator();
        comparator.setIgnoreNamespaceDeclarations(true);
        assertTrue(comparator.compare(expected.getDocumentElement(), result));
    }

    // ++++++++++++ oops, don't look at my private parts!  +++++++++++++++
    private Document getXmlResource(String fileName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        // Create builder.
        DocumentBuilder builder = factory.newDocumentBuilder();



        // Parse files.
        return builder.parse(getClass().getResourceAsStream(fileName));
    }

}
