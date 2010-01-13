/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.base;

import de.betterform.xml.ns.NamespaceConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Basic Implementation of XML Base Recommendation 27 June 2001. This class handles only
 * URIs embedded in the content of the document as described in '4.1 Relation to RFC 2396'.
 *
 * @author joern turner
 * @version $Id: XMLBaseResolver.java 2273 2006-08-21 10:12:19Z unl $
 */
public class XMLBaseResolver {
    /**
     * resolves the XML Base for a given start Node and inputURI. Will walk upwards the tree,
     * pick up all xml:base Attributes and resolve the URI in context of their respective parents.
     * <p/>
     * The first part of the URI (the one to resolve) is passed in cause it may come from different,
     * markup-specific Attributes like xlink:href or xforms:src.
     *
     * @param start  - the Node where resolution starts
     * @param relURI - the URI to resolve
     * @return - absolute URI expression as String
     * @throws java.net.URISyntaxException will be thrown when the document root was reached and no absolute URI
     *                                     could be constructed
     */
    public static String resolveXMLBase(Node start, String relURI)
            throws URISyntaxException {
        return XMLBaseResolver.resolveXMLBase(start, relURI, false);
    }

    private static String getBase(Element e) {
        if (e.hasAttributeNS(NamespaceConstants.XML_NS, "base")) {
            return e.getAttributeNS(NamespaceConstants.XML_NS, "base");
        }

        return "";
    }

    private static String resolveXMLBase(Node start, String relURI, boolean base)
            throws URISyntaxException {

        if(relURI==null){
            throw new URISyntaxException(relURI , "is not a valid URI") ;
        }
        URI uri = new URI(relURI);

        if (uri.isAbsolute()) {
            return uri.toString(); //return absolute URI;
        }

        if (uri.isOpaque()) {
            return uri.toString();
        }

        Node n = start.getParentNode();

        if (n.getNodeType() == Node.DOCUMENT_NODE) {
            String baseFragment = getBase(((Document) n).getDocumentElement());
            String result = baseFragment + uri.normalize().toString();
            URI resolvedUri = new URI(result);

            if (resolvedUri.isAbsolute()) {
                // return absolute uri
                return resolvedUri.normalize().toString();
            }

            if (base || (baseFragment.length() > 0)) {
                // throw excpetion only if XML base markup is present
                throw new URISyntaxException(resolvedUri.toString(), "no absolute URI");
            }

            // no xml base markup found - return passed in uri
            return resolvedUri.normalize().toString();
        }

        if (n.getNodeType() == Node.ELEMENT_NODE) {
            Element e = (Element) n;
            String baseFragment = getBase(e);
            URI parentURI = new URI(baseFragment);

            return XMLBaseResolver.resolveXMLBase(e, parentURI.resolve(uri.toString()).toString(),
                    base || (baseFragment.length() > 0));
        }

        throw new URISyntaxException(relURI, "could not resolve to absolute URI");
    }
}
