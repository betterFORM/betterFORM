/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.exec;

import de.betterform.connector.AbstractConnector;
import de.betterform.connector.SubmissionHandler;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import org.apache.log4j.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Executes the command specified by the connector's uri.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ExecSubmissionHandler.java 161 2005-11-30 14:41:17Z uli $
 */
public class ExecSubmissionHandler extends AbstractConnector implements SubmissionHandler {

    private static final Category LOGGER = Category.getInstance(ExecSubmissionHandler.class);

    public Map submit(Submission submission, Node node) throws XFormsException {
        if (! "instance".equalsIgnoreCase(submission.getReplace())) {
           return null;
        }

        try {
            // resolve action uri against base uri in order to get absolute path to program
            URI baseURI = new URI(submission.getContainerObject().getProcessor().getBaseURI());
            URI actionURI = new URI(getURI());
            String schemeAction = actionURI.getSchemeSpecificPart();
            File program = new File(baseURI.resolve(schemeAction));


            
            // execute program
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("executing '" + program.getAbsolutePath() + "'");
            }
            int code = new ProcessExecutor().execute(program.getParentFile(), program.getAbsolutePath() +  getParameters(node), outputStream, errorStream);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("stdout '" + outputStream.toString() + "'");
                LOGGER.debug("stderr '" + errorStream.toString() + "'");
            }

            // create response document
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            Document document = factory.newDocumentBuilder().newDocument();
            document.appendChild(document.createElementNS(null, "exec"));

            //todo: rewrite this part
            Element rootElement = document.getDocumentElement();

            Element element = document.createElement("command");
            DOMUtil.setElementValue(element, program.getName());
            rootElement.appendChild(element);

            element = document.createElement("working-dir");
            DOMUtil.setElementValue(element, program.getParentFile().toURI().getPath());
            rootElement.appendChild(element);

            element = document.createElement("exit-code");
            DOMUtil.setElementValue(element, String.valueOf(code));
            rootElement.appendChild(element);

            element = document.createElement("std-out");
            DOMUtil.setElementValue(element, outputStream.toString());
            rootElement.appendChild(element);

            element = document.createElement("std-err");
            DOMUtil.setElementValue(element, errorStream.toString());
            rootElement.appendChild(element);

//            // create response stream
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DOMUtil.prettyPrintDOM(document, stream);

            Map response = new HashMap();
            response.put(XFormsProcessor.SUBMISSION_RESPONSE_STREAM, new ByteArrayInputStream(stream.toByteArray()));

            return response;
            //return null;
        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    protected String getParameters(Node node) {
        Node rootNode = ((Document) node).getDocumentElement();
        Node argsNode = DOMUtil.getFirstChildByTagName(rootNode, "args");
        if (argsNode != null) {
            return " " +DOMUtil.getTextNodeAsString(argsNode);
        }

        return "";
    }
}
