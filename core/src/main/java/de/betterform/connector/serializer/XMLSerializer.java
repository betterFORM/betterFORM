/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.serializer;

import de.betterform.connector.InstanceSerializer;
import de.betterform.xml.xforms.model.submission.Submission;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;

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


