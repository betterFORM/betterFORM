/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.connector.ConnectorFactory;
import de.betterform.generator.XSLTGenerator;
import de.betterform.xml.config.Config;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import de.betterform.xml.xslt.TransformerService;
import de.betterform.xml.xslt.impl.CachingTransformerService;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

//end of class

/**
 * does additional include processing
 */
public class HTTPSubmissionHandlerXI extends HTTPSubmissionHandler {
    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(HTTPSubmissionHandlerXI.class);

    /**
     * Serializes and submits the specified instance data over the <code>http</code> protocol.
     *
     * @param submission the submission issuing the request.
     * @param instance   the instance data to be serialized and submitted.
     * @return a map holding the response mime-type and the response stream.
     * @throws de.betterform.xml.xforms.exception.XFormsException if any error occurred during submission.
     */
    public Map submit(Submission submission, Node instance) throws XFormsException {
        Map result = super.submit(submission,instance);
        InputStream inputStream = (InputStream) result.get(XFormsProcessor.SUBMISSION_RESPONSE_STREAM);


        if((submission.getReplace().equals("instance"))){
            String xsltPath = Config.getInstance().getProperty("resource.dir.name") + "xslt";

            CachingTransformerService transformerService  = (CachingTransformerService) getContext().get(TransformerService.TRANSFORMER_SERVICE);
            URI webappRealpath = null;
            try {
                webappRealpath = new URI((String) getContext().get("webapp.realpath"));
            } catch (URISyntaxException e) {
                throw new XFormsException("URISyntaxException for getContext().get('webapp.realpath')",e);
            }
            String styleSheetURIPath = webappRealpath.getSchemeSpecificPart() + xsltPath;
            URI styleSheetUri = new File(styleSheetURIPath,"include.xsl").toURI();

            XSLTGenerator generator = new XSLTGenerator();
            generator.setTransformerService(transformerService);
            generator.setStylesheetURI(styleSheetUri);

            ConnectorFactory connectorFactory = submission.getContainerObject().getConnectorFactory();
            String baseURI = connectorFactory.getAbsoluteURI(getURI(),submission.getElement()).toString();
            String uri =baseURI.substring(0,baseURI.lastIndexOf("/")+1);

            generator.setParameter("root",uri);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            generator.setInput(inputStream);
            generator.setOutput(outputStream);
            generator.generate();

            // create input stream from result
            inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        }
        Map response = new HashMap();
        response.put(XFormsProcessor.SUBMISSION_RESPONSE_STREAM, inputStream);
        
        return response;

    }


}
