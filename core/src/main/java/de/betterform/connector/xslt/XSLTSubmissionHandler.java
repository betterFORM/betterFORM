/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.xslt;

import de.betterform.connector.AbstractConnector;
import de.betterform.connector.ConnectorHelper;
import de.betterform.connector.SubmissionHandler;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import de.betterform.xml.xslt.TransformerService;
import de.betterform.xml.xslt.impl.CachingTransformerService;
import de.betterform.xml.xslt.impl.FileResourceResolver;
import de.betterform.xml.xslt.impl.HttpResourceResolver;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This submission applies an XSLT transformation to the submitted instance.
 * <p/>
 * For a base URI like e.g. <code>file:///Users/JohnDoe/Forms/bin</code> the
 * connector URI <code>xslt:../xsl/my.xslt?foo=bar</code> will result in the
 * following processing steps:
 * <ol>
 * <li>After URI resolution the XSLT stylesheet is loaded from
 * <code>file:///Users/JohnDoe/Forms/xsl/my.xslt</code>.</li>
 * <li>A Transformer is created for that stylesheet and receives <code>foo=bar</code>
 * as parameters.</li>
 * <li>The submitted instance is transformed using that Transformer.</li>
 * <li>The result is provided in the Submission's response via a stream.</li>
 * </ol>
 *
 * Parsing of an instance node is also supported with the 'parseString=true' param. It is assumed that the
 * instance node that is passed contains markup as a string that is to be parsed before feeding into
 * transformer.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @author Joern Turner
 * @version $Id: XSLTSubmissionHandler.java 559 2006-12-11 09:59:13Z joern $
 */
public class XSLTSubmissionHandler extends AbstractConnector implements SubmissionHandler {
    /**
     * The logger.
     */
    private static final Log LOGGER = LogFactory.getLog(XSLTSubmissionHandler.class);
    
    /**
     * Serializes and submits the specified instance data over the
     * <code>xslt</code> protocol.
     *
     * @param submission the submission issuing the request.
     * @param instance the instance data to be serialized and submitted.
     * @return xslt transformation result.
     * @throws XFormsException if any error occurred during submission.
     */
    public Map submit(Submission submission, Node instance) throws XFormsException {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("uri: " + getURI());
            }


            URI uri = ConnectorHelper.resolve(submission.getContainerObject().getProcessor().getBaseURI(), getURI());
            Map parameters = ConnectorHelper.getURLParameters(uri);
            URI stylesheetURI = ConnectorHelper.removeURLParameters(uri);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("stylesheet uri: " + stylesheetURI);
            }

            // pass output properties from submission
            CachingTransformerService transformerService  = (CachingTransformerService) getContext().get(TransformerService.TRANSFORMER_SERVICE);

            if (transformerService == null) {
            if(uri.getScheme().equals("http")) {
                transformerService = new CachingTransformerService(new HttpResourceResolver());
            }else if(uri.getScheme().equals("file")){
                transformerService = new CachingTransformerService(new FileResourceResolver());
            } else {
                throw new XFormsException("Protocol " + stylesheetURI.getScheme() + " not supported for XSLT Submission Handler");
            }
            }

            if(parameters != null && parameters.get("nocache") != null){
                transformerService.setNoCache(true);
            }

            Transformer transformer = transformerService.getTransformer(stylesheetURI);

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
            }
            else {
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            }
            if (submission.getOmitXMLDeclaration() != null) {
                transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, Boolean.TRUE.equals(submission.getOmitXMLDeclaration()) ? "yes" : "no");
            }
            if (submission.getStandalone() != null) {
                transformer.setOutputProperty(OutputKeys.STANDALONE, Boolean.TRUE.equals(submission.getStandalone()) ? "yes" : "no");
            }
            if (submission.getCDATASectionElements() != null) {
                transformer.setOutputProperty(OutputKeys.CDATA_SECTION_ELEMENTS, submission.getCDATASectionElements());
            }

            // pass parameters form uri if any
            if (parameters != null) {
                Iterator iterator = parameters.keySet().iterator();
                String name;
                while (iterator.hasNext()) {
                    name = (String) iterator.next();
                    transformer.setParameter(name, parameters.get(name));
                }
            }

            // transform instance to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            long start = System.currentTimeMillis();

            DOMSource domSource;
            //check for 'parse' param
            String parseParam=null;
            if(parameters.containsKey("parseString")) {
                parseParam = (String) parameters.get("parseString");
            }
            if(parseParam != null && parseParam.equalsIgnoreCase("true")){
                String xmlString = DOMUtil.getTextNodeAsString(instance);
                domSource = new DOMSource(DOMUtil.parseString(xmlString,true,false));
            }else{
                domSource = new DOMSource(instance);
            }

            transformer.transform(domSource, new StreamResult(outputStream));
            long end = System.currentTimeMillis();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("transformation time: " + (end - start) + " ms");
            }

            // create input stream from result
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            Map response = new HashMap();
            response.put(XFormsProcessor.SUBMISSION_RESPONSE_STREAM, inputStream);

            return response;
        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    }

}
