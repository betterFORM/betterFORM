/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.file;

import de.betterform.connector.AbstractConnector;
import de.betterform.connector.SubmissionHandler;
import de.betterform.connector.serializer.SerializerRequestWrapper;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * The file submission driver serializes and submits instance data to a file.
 * <p/>
 * When using the <code>put</code> submission method, the driver only supports
 * the replace mode <code>none</code>. It simply serializes the instance data to
 * the file denoted by the connector URI. When this file exists, it will be
 * overwritten silently, otherwise it will be created.
 * <p/>
 * When using the <code>get</code> submission method, the driver ignores any
 * replace mode. Furthermore, it ignores the instance data completely and
 * returns the file denoted by the connector URI as a response stream.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: FileSubmissionHandler.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class FileSubmissionHandler extends AbstractConnector implements SubmissionHandler {
    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(FileSubmissionHandler.class);

    /**
     * Serializes and submits the specified instance data over the
     * <code>file</code> protocol.
     *
     * @param submission the submission issuing the request.
     * @param instance the instance data to be serialized and submitted.
     * @return <code>null</code>.
     * @throws XFormsException if any error occurred during submission.
     */
    public Map submit(Submission submission, Node instance) throws XFormsException {
        if (submission.getMethod().equalsIgnoreCase("get")) {
            try {
                // create uri
                URI uri = new URI(getURI());

                // use scheme specific part in order to handle UNC names
                String fileName = uri.getSchemeSpecificPart();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("getting file '" + fileName + "'");
                }

                // create file
                File file = new File(fileName);
                InputStream inputStream;

                // check for directory
                if (file.isDirectory()) {
                    // create input stream from directory listing
                    Document document = FileURIResolver.buildDirectoryListing(file);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    DOMUtil.prettyPrintDOM(document, outputStream);
                    inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                }
                else {
                    // create file input stream
                    inputStream = new FileInputStream(new File(fileName));
                }
                Map response = new HashMap();
                response.put(XFormsProcessor.SUBMISSION_RESPONSE_STREAM, inputStream);

                return response;
            }
            catch (Exception e) {
                throw new XFormsException(e);
            }
        }

        if (submission.getMethod().equalsIgnoreCase("put")) {
            if (!submission.getReplace().equals("none")) {
                throw new XFormsException("submission mode '" + submission.getReplace() + "' not supported");
            }

            try {
                // create uri
                URI uri = new URI(getURI());

                // use scheme specific part in order to handle UNC names
                String fileName = uri.getSchemeSpecificPart();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("putting file '" + fileName + "'");
                }

                // create output steam and serialize instance data
                FileOutputStream stream = new FileOutputStream(new File(fileName));
                SerializerRequestWrapper wrapper = new SerializerRequestWrapper(stream);
                serialize(submission, instance, wrapper);
                wrapper.getBodyStream().close();
            }
            catch (Exception e) {
                throw new XFormsException(e);
            }

            return new HashMap();
        }
        
        if(submission.getMethod().equalsIgnoreCase("delete")) {
        	try {
        		// create uri
                URI uri = new URI(getURI());

                // use scheme specific part in order to handle UNC names
                String fileName = uri.getSchemeSpecificPart();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("getting file '" + fileName + "'");
                }

                // create file
                File file = new File(fileName);
                file.delete();
        	}
        	catch(Exception e) {
                throw new XFormsException(e);
            }

            return new HashMap();
        }

        throw new XFormsException("submission method '" + submission.getMethod() + "' not supported");
    }
}

// end of class
