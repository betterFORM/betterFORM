// Copyright 2010 betterForm
/*
 *
 * Artistic License
 *
 * Preamble
 *
 * The intent of this document is to state the conditions under which a
 * Package may be copied, such that the Copyright Holder maintains some
 * semblance of artistic control over the development of the package, while
 * giving the users of the package the right to use and distribute the
 * Package in a more-or-less customary fashion, plus the right to make
 * reasonable modifications.
 *
 * Definitions:
 *
 * "Package" refers to the collection of files distributed by the Copyright
 * Holder, and derivatives of that collection of files created through
 * textual modification.
 *
 * "Standard Version" refers to such a Package if it has not been modified,
 * or has been modified in accordance with the wishes of the Copyright
 * Holder.
 *
 * "Copyright Holder" is whoever is named in the copyright or copyrights
 * for the package.
 *
 * "You" is you, if you're thinking about copying or distributing this
 * Package.
 *
 * "Reasonable copying fee" is whatever you can justify on the basis of
 * media cost, duplication charges, time of people involved, and so
 * on. (You will not be required to justify it to the Copyright Holder, but
 * only to the computing community at large as a market that must bear the
 * fee.)
 *
 * "Freely Available" means that no fee is charged for the item itself,
 * though there may be fees involved in handling the item. It also means
 * that recipients of the item may redistribute it under the same
 * conditions they received it.
 *
 * 1. You may make and give away verbatim copies of the source form of the
 * Standard Version of this Package without restriction, provided that you
 * duplicate all of the original copyright notices and associated
 * disclaimers.
 *
 * 2. You may apply bug fixes, portability fixes and other modifications
 * derived from the Public Domain or from the Copyright Holder. A Package
 * modified in such a way shall still be considered the Standard Version.
 *
 * 3. You may otherwise modify your copy of this Package in any way,
 * provided that you insert a prominent notice in each changed file stating
 * how and when you changed that file, and provided that you do at least
 * ONE of the following:
 *
 * a) place your modifications in the Public Domain or otherwise make them
 * Freely Available, such as by posting said modifications to Usenet or an
 * equivalent medium, or placing the modifications on a major archive site
 * such as ftp.uu.net, or by allowing the Copyright Holder to include your
 * modifications in the Standard Version of the Package.
 *
 * b) use the modified Package only within your corporation or
 * organization.
 *
 * c) rename any non-standard executables so the names do not conflict with
 * standard executables, which must also be provided, and provide a
 * separate manual page for each non-standard executable that clearly
 * documents how it differs from the Standard Version.
 *
 * d) make other distribution arrangements with the Copyright Holder.
 *
 * 4. You may distribute the programs of this Package in object code or
 * executable form, provided that you do at least ONE of the following:
 *
 * a) distribute a Standard Version of the executables and library files,
 * together with instructions (in the manual page or equivalent) on where
 * to get the Standard Version.
 *
 * b) accompany the distribution with the machine-readable source of the
 * Package with your modifications.
 *
 * c) accompany any non-standard executables with their corresponding
 * Standard Version executables, giving the non-standard executables
 * non-standard names, and clearly documenting the differences in manual
 * pages (or equivalent), together with instructions on where to get the
 * Standard Version.
 *
 * d) make other distribution arrangements with the Copyright Holder.
 *
 * 5. You may charge a reasonable copying fee for any distribution of this
 * Package. You may charge any fee you choose for support of this
 * Package. You may not charge a fee for this Package itself.  However, you
 * may distribute this Package in aggregate with other (possibly
 * commercial) programs as part of a larger (possibly commercial) software
 * distribution provided that you do not advertise this Package as a
 * product of your own.
 *
 * 6. The scripts and library files supplied as input to or produced as
 * output from the programs of this Package do not automatically fall under
 * the copyright of this Package, but belong to whomever generated them,
 * and may be sold commercially, and may be aggregated with this Package.
 *
 * 7. C or perl subroutines supplied by you and linked into this Package
 * shall not be considered part of this Package.
 *
 * 8. The name of the Copyright Holder may not be used to endorse or
 * promote products derived from this software without specific prior
 * written permission.
 *
 * 9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 * MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
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
