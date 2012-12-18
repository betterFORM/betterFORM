package de.betterform.xml.xforms;

import de.betterform.xml.ns.NamespaceConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * User: joernturner
 * Date: 28.11.12
 * Time: 14:00
 */
public class XFormsUtil implements NamespaceConstants{

    public static Element getFirstXFormsElement(Element contextNode, String localName){
        Element element = null;

        if (contextNode.getNodeType() == Node.DOCUMENT_NODE) {
            element = ((Document) contextNode).getDocumentElement();

            if (!(element.getNamespaceURI().equals(NamespaceConstants.XFORMS_NS) && element.getLocalName().equals(localName))) {
                element = null;
            }
        } else {
            NodeList nodes = contextNode.getElementsByTagNameNS(NamespaceConstants.XFORMS_NS, localName);

            if (nodes != null && nodes.item(0).getNodeType() == Node.ELEMENT_NODE) {
                element = (Element) nodes.item(0);
            }
        }
        return element;
    }
}
