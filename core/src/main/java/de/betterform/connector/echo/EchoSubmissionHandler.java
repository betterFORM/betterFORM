/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.connector.echo;

import de.betterform.connector.AbstractConnector;
import de.betterform.connector.SubmissionHandler;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This SubmissionHandler is mainly for testing purposes. It simply returns the DOM it receives.
 *
 * @author Joern Turner
 * @version $Id: EchoSubmissionHandler.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class EchoSubmissionHandler extends AbstractConnector implements SubmissionHandler {

    /**
     * Returns (echoes) the instance data as submission response.
     *
     * @param submission the submission issuing the request.
     * @param instance the instance data to be serialized and submitted.
     * @return a map holding the original protocol specific response.
     * @throws XFormsException if any error occurred during submission.
     */
    public Map submit(Submission submission, Node instance) throws XFormsException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            serialize(submission, instance, outputStream);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            outputStream.close();

            Map response = new HashMap();
            response.put(XFormsProcessor.SUBMISSION_RESPONSE_STREAM, inputStream);

            return response;
        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    }
}
