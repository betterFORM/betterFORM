/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.xforms.exception;

import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.om.Item;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.XFormsProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;

import java.util.*;

/**
 * Signals an <code>xforms-submit-error</code> error indication.
 *
 * @author Joern Turner, Ulrich Nicolas Liss&eacute;
 * @version $Id: XFormsSubmitError.java 2090 2006-03-16 09:37:00Z joernt $
 */
public class XFormsSubmitError extends XFormsErrorIndication {
    private static final String messagePre="xforms-submit-error: ";

    /**
     * Creates a new <code>xforms-submit-error</code> error indication.
     *
     * @param message the error message.
     * @param target  the event target.
     * @param info    the context information.
     */
    public XFormsSubmitError(String message, EventTarget target, Object info) {
        this(message, null, target, info);
    }

    /**
     * Creates a new <code>xforms-submit-error</code> error indication.
     *
     * @param message the error message.
     * @param cause   the root cause.
     * @param target  the event target.
     * @param info    the context information.
     */
    public XFormsSubmitError(String message, Exception cause, EventTarget target, Object info) {
        super(messagePre  + message + "- " + DOMUtil.getCanonicalPath((Node) target), cause, target, info);
        this.id = "xforms-submit-error";
    }

    /**
     * Specifies wether this error indication is fatal or non-fatal.
     *
     * @return <code>false</code>.
     */
    public boolean isFatal() {
        return false;
    }

    /**
     * todo: response status code should be fetched from underlying protocol handler (submmissionhandler)
     * todo: response-reason-phrase should be handled
     * todo: response-body should be handled
     * @param submissionEl
     * @param container
     * @param locationPath
     * @param errorType
     * @param resourceURI
     * @return
     */
    public static Map<String, Object> constructInfoObject(Element submissionEl, Container container, final String locationPath, final String errorType, final String resourceURI) {
    	return constructInfoObject(submissionEl, container, locationPath, errorType, resourceURI, Double.NaN, null, "", "");
    }
    
    public static Map<String, Object> constructInfoObject(Element submissionEl, Container container, final String locationPath, final String errorType, final String resourceURI, final double responseStatusCode, final Map responseHeaders, final String responseReasonPhrase, final String responseBody) {
    	Map<String, Object> result = new HashMap<String, Object>();
    	
    	result.put("location-path", locationPath);
    	result.put("error-type", errorType);
    	result.put("resource-uri", resourceURI);
    	result.put("response-status-code", Double.valueOf(responseStatusCode));
    	
    	if (responseHeaders != null) {
    		final Document ownerDocument = submissionEl.getOwnerDocument();
    		final DocumentWrapper wrapper = container.getDocumentWrapper(submissionEl);
    		List<Item> headerItems = new ArrayList<Item>(responseHeaders.size());
    		for (Iterator<Map.Entry<String, String>> it = responseHeaders.entrySet().iterator(); it.hasNext();) {
    			Map.Entry<String, String> entry =  it.next();
    			if (!XFormsProcessor.SUBMISSION_RESPONSE_STREAM.equals(entry.getKey())) {
    				
    				Element headerEl = ownerDocument.createElement("header");
    				
    				Element nameEl = ownerDocument.createElement("name");
    				nameEl.appendChild(ownerDocument.createTextNode(entry.getKey()));
    				headerEl.appendChild(nameEl);

    				Element valueEl = ownerDocument.createElement("value");
    				valueEl.appendChild(ownerDocument.createTextNode(entry.getValue()));
    				headerEl.appendChild(valueEl);
    				
    				headerItems.add(wrapper.wrap(headerEl));
    			}
    		}
    		result.put(XFormsConstants.RESPONSE_HEADERS, headerItems);
    	}
    	
    	result.put("response-reason-phrase", responseReasonPhrase);
    	result.put("response-body", responseBody);
    	
    	return result;
    }
}

//end of class
