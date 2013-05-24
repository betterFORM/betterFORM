/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.exist;

import de.betterform.connector.SubmissionHandler;
import de.betterform.connector.http.AbstractHTTPConnector;
import de.betterform.connector.serializer.SerializerRequestWrapper;
import de.betterform.connector.util.URIUtils;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;
import java.util.Map;

/**
 *  This handler submits instance data to an eXistdb instance via API.
 */
public class ExistSubmissionHandler extends AbstractHTTPConnector implements SubmissionHandler {
    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(ExistSubmissionHandler.class);

    /**
     *
     *
     * @param submission the submission issuing the request.
     * @param instance   the instance data to be serialized and submitted.
     * @return a map holding the response mime-type and the response stream.
     * @throws de.betterform.xml.xforms.exception.XFormsException if any error occurred during submission.
     */
    public Map submit(Submission submission, Node instance) throws XFormsException {
        try {
            //in case we need the context for a connection or something...
//            Map context = submission.getContainerObject().getProcessor().getContext();



            String method = submission.getMethod();

            String mediatype = "application/xml";
            if (submission.getMediatype() != null) {
                mediatype = submission.getMediatype();
            }

            String encoding = submission.getEncoding();
            if (submission.getEncoding() == null) {
                encoding = getDefaultEncoding();
            }

            SerializerRequestWrapper wrapper = new SerializerRequestWrapper(new ByteArrayOutputStream());
            serialize(submission, instance, wrapper);
            ByteArrayOutputStream stream = (ByteArrayOutputStream) wrapper.getBodyStream();

            /*
             * Some extension mechanism here could be handy 
             */
             boolean streamNotEmpty = !("".equals(stream.toString()));
            // HTTP POST
            if (method.equals("post")) {
                if(streamNotEmpty){
//                    post(getURI(), stream.toString(encoding), mediatype, encoding);
                }
                else {
//                    post(getURI(), mediatype, encoding);
                }
            }

            // GET
            else if (method.equals("get")) {
                if(URIUtils.getURIWithoutFragment(getURI()).indexOf("?") == -1 && streamNotEmpty){
//                    get(URIUtils.getURIWithoutFragment(getURI()) + "?" + stream.toString(encoding));
                }else if(streamNotEmpty){
//                    get(URIUtils.getURIWithoutFragment(getURI()) + "&" + stream.toString(encoding));
                }else{
//                    get(URIUtils.getURIWithoutFragment(getURI()));
                }
            }
            // PUT
            else if (method.equals("put")) {
                if(streamNotEmpty){
//                    put(getURI(), stream.toString(encoding), mediatype, encoding);
                }
                else {
//                    put(getURI(), mediatype, encoding);
                }
            }
            // HTTP DELETE
            else if (method.equals("delete")) {
            	if(getURI().indexOf("?") == -1 && streamNotEmpty){
//                    delete(getURI() + "?" + stream.toString(encoding));
                }else if(streamNotEmpty){
//                	delete(getURI() + "&" + stream.toString(encoding));
                }else {
//                    delete(getURI());
                }
            }
            else {
                throw new XFormsException("submission method '" + method + "' not supported");
            }

            Map response = getResponseHeader();
            response.put(XFormsProcessor.SUBMISSION_RESPONSE_STREAM, getResponseBody());
            response.put(XFormsConstants.RESPONSE_STATUS_CODE, String.valueOf(statusCode));
            response.put(XFormsConstants.RESPONSE_REASON_PHRASE, reasonPhrase);

            return response;
        } catch (Exception e) {
            throw new XFormsException(e);
        }
    }


}

//end of class
