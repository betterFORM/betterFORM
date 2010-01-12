/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.connector.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.connector.SubmissionHandler;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * The HTTP submission handler serializes and submits instance data over HTTP/1.1.
 * <p/>
 * Currently, the driver supports all but the <code>multipart-post</code> submission methods. Maybe security
 * functionality will be added later, thus becoming a HTTPS handler.
 * <p/>
 * See the '/web/forms/action.xhtml'-form for examples how to use HTTP submission.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: HTTPSubmissionHandler.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class HTTPSubmissionHandler extends AbstractHTTPConnector implements SubmissionHandler {
    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(HTTPSubmissionHandler.class);

    /**
     * Serializes and submits the specified instance data over the <code>http</code> protocol.
     *
     * @param submission the submission issuing the request.
     * @param instance   the instance data to be serialized and submitted.
     * @return a map holding the response mime-type and the response stream.
     * @throws XFormsException if any error occurred during submission.
     */
    public Map submit(Submission submission, Node instance) throws XFormsException {
        try {
            String method = submission.getMethod();

            String mediatype = "application/xml";
            if (submission.getMediatype() != null) {
                mediatype = submission.getMediatype();
            }

            String encoding = submission.getEncoding();
            if (submission.getEncoding() == null) {
                encoding = getDefaultEncoding();
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            serialize(submission, instance, stream);

            /*
             * Some extension mechanism here could be handy 
             */
            boolean streamNotEmpty = !("".equals(stream.toString()));
            // HTTP POST
            if (method.equals("post")) {
                if(streamNotEmpty){
                    post(getURI(), stream.toString(encoding), mediatype + "; charset="+encoding, encoding);
                }
                else {
                    post(getURI(), mediatype + "; charset="+encoding, encoding);
                }
            }

            // HTTP GET
            else if (method.equals("get")) {
                if(getURI().indexOf("?") == -1 && streamNotEmpty){
                    get(getURI() + "?" + stream.toString(encoding));
                }else if(streamNotEmpty){
                    get(getURI() + "&" + stream.toString(encoding));                                        
                }else{
                    get(getURI());
                }
            }
            // HTTP PUT
            else if (method.equals("put")) {
                if(streamNotEmpty){
                    put(getURI(), stream.toString(encoding), mediatype + "; charset="+encoding, encoding);
                }
                else {
                    put(getURI(), mediatype + "; charset="+encoding, encoding);                    
                }
            }
            // HTTP  MULTIPART-POST
            else if (method.equals("multipart-post")) {
                
                // body comes with header
                if(streamNotEmpty){
                    String data = stream.toString(encoding);
                    int i = data.indexOf("\n\n");
                    if (i == -1) {
                        i = data.indexOf("\r\n\r\n");
                        if (i == -1) {
                            throw new XFormsException(
                                "serializer sent wrong multipart content.");
                        }
                        i += 2;
                    }
                    i += 2;

                    // get contenttype
                    String contentType = null;
                    StringTokenizer tok = new StringTokenizer(
                        data.substring(0, i)
                            .replaceAll("\r","")         // remove CR
                            .replaceAll("\n ", " ")      // multiline header
                            .replaceAll("\n\t", " ")     // multiline header
                    );
                    while(tok.hasMoreTokens()) {
                        String name = tok.nextToken("\n");
                        if (name.toLowerCase().startsWith("content-type:")) {
                            contentType = name.substring("content-type:".length());
                            break;
                        }
                    }
                    post(getURI(), data.substring(i), contentType, encoding);
                } else {
                    post(getURI(),"UTF8", encoding);

                }
                
            }
            // HTTP  FORM-DATA-POST            
            else if (method.equals("form-data-post")) {
                if(streamNotEmpty){
                    post(getURI(), stream.toString(encoding), "multipart/form-data; charset="+encoding, encoding);
                }else {
                    post(getURI(), "multipart/form-data; charset="+encoding, encoding);
                }
            }
            // HTTP URLENCODED-POST
            else if (method.equals("urlencoded-post")) {
                if(streamNotEmpty){
                    post(getURI(), stream.toString(encoding), "application/x-www-form-urlencoded; charset="+encoding, encoding);
                }else {
                    post(getURI(), "application/x-www-form-urlencoded; charset="+encoding, encoding);
                }
            }
            // HTTP DELETE            
            else if (method.equals("delete")) {
            	if(getURI().indexOf("?") == -1 && streamNotEmpty){
                    delete(getURI() + "?" + stream.toString(encoding));
                }else if(streamNotEmpty){
                	delete(getURI() + "&" + stream.toString(encoding));                                        
                }else {
                    delete(getURI());
                }
            }
            else {
                // Note: user has to provide mediatype in submission element otherwise this will
                // be probably wrong type (application/xml) ...
                if(streamNotEmpty){
                    post(getURI(), stream.toString(encoding), mediatype + "; charset="+encoding, encoding);
                }else {
                    post(getURI(), mediatype + "; charset="+encoding, encoding);
                }

            }

            Map response = getResponseHeader();
            response.put(XFormsProcessor.SUBMISSION_RESPONSE_STREAM, getResponseBody());

            return response;
        } catch (Exception e) {
            throw new XFormsException(e);
        }
    }


}

//end of class
