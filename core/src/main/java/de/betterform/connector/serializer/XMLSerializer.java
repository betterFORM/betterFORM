/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.serializer;

import de.betterform.connector.InstanceSerializer;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.model.submission.Submission;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.ArrayList;
import java.util.List;

/**
 * Serialize instance as application/xml.
 *
 * @author Peter Mikula <peter.mikula@digital-artefacts.fi>
 * @version $Id: XMLSerializer.java 2099 2006-03-28 16:24:07Z unl $
 */
public class XMLSerializer implements InstanceSerializer {

    public void serialize(Submission submission, Node instance,
                         SerializerRequestWrapper wrapper, String defaultEncoding) throws Exception {
    	
    	if(instance.getNodeType() == Node.ELEMENT_NODE || instance.getNodeType() == Node.DOCUMENT_NODE) {
	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

	        if (submission.getVersion() != null) {
	            transformer.setOutputProperty(OutputKeys.VERSION, submission.getVersion());
	        }
	        if (submission.getIndent() != null) {
	            transformer.setOutputProperty(OutputKeys.INDENT, Boolean.TRUE.equals(submission.getIndent()) ? "yes" : "no");
	        }
	        if (submission.getIncludeNamespacePrefixes() != null) {
                Element rootElem = (Element) instance.getFirstChild();
                NamedNodeMap attrs = rootElem.getAttributes();
                List includedNamesspace = submission.getIncludeNamespacePrefixes();
                List<Attr> removeAttributes = new ArrayList<Attr>();
                if (attrs != null) {
                    int attrlength = attrs.getLength();
                    for (int c = 0; c < attrlength; c++) {
                        Node attr = attrs.item(c);
                        if (NamespaceConstants.XMLNS_NS.equals(attr.getNamespaceURI())) {
                            if (!includedNamesspace.contains(attr.getLocalName())) {
                                removeAttributes.add((Attr) attr);
                            }
                        }
                    }
                }
                for(Attr attribute : removeAttributes){
                    rootElem.removeAttributeNode(attribute);
                }
                instance.replaceChild(instance.getFirstChild(), rootElem);
	        }
	        if (submission.getMediatype() != null) {
	            transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, submission.getMediatype());
	        }
	        if (submission.getEncoding() != null) {
	            transformer.setOutputProperty(OutputKeys.ENCODING, submission.getEncoding());
	        } else {
	            transformer.setOutputProperty(OutputKeys.ENCODING, defaultEncoding);
	        }
	        if (submission.getOmitXMLDeclaration() != null) {
	            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, submission.getOmitXMLDeclaration().booleanValue() ? "yes" : "no");
	        }
	        if (submission.getStandalone() != null) {
	            transformer.setOutputProperty(OutputKeys.STANDALONE, Boolean.TRUE.equals(submission.getStandalone()) ? "yes" : "no");
	        }
	        if (submission.getCDATASectionElements() != null) {
	            transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, submission.getCDATASectionElements());
	        }
	
	        transformer.transform(new DOMSource(instance), new StreamResult(wrapper.getBodyStream()));
    	}
    	else {
    		wrapper.getBodyStream().write(instance.getTextContent().getBytes(defaultEncoding));
    	}
    }
}

//end of class


