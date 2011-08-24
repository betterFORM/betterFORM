/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.connector.ant;


import de.betterform.connector.AbstractConnector;
import de.betterform.connector.SubmissionHandler;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class AntSubmissionHandler extends AbstractConnector implements SubmissionHandler {
      /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(AntSubmissionHandler.class);
    private String buildFile = "build.xml";


    public Map submit(Submission submission, Node instance) throws XFormsException {
        LOGGER.debug("AntSubmissionHandler.submit()");
        if (! "none".equalsIgnoreCase(submission.getReplace())) {
           throw new XFormsException("submission mode '" + submission.getReplace() + "' at: " + DOMUtil.getCanonicalPath(submission.getElement()) + " not supported");
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (submission.getMethod().equals("get")) {
            try {
                String uri = getURI();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

                String buildFilePath = (new URI(uri)).getSchemeSpecificPart().substring((new URI(uri)).getSchemeSpecificPart().indexOf(':')+1);
                if (! "".equals(buildFilePath)) {
                    File buildFile;
                    String target;
                    //get Traget from uri
                    if (uri.contains("#")) {
                        buildFile = new File(buildFilePath);
                        target = uri.substring(uri.indexOf('#')+1);
                    } else {
                        //get target from xform
                        buildFile = new File(buildFilePath);
                        target = ((Document) instance).getElementsByTagName("target").item(0).getTextContent();
                    }

                    LOGGER.debug("AntSubmissionHandler.runTarget() BuildFile: " + buildFile.getAbsolutePath() + " with Target:" + target);
                    runTarget(buildFile, target, outputStream, errorStream);

                    if (LOGGER.isDebugEnabled()) {
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        factory.setNamespaceAware(true);
                        factory.setValidating(false);
                        Document document = factory.newDocumentBuilder().newDocument();
                        document.appendChild(document.createElementNS(null, "ant"));

                        Element rootElement = document.getDocumentElement();

                        Element element = document.createElement("buildFile");
                        DOMUtil.setElementValue(element, buildFile.getAbsolutePath());
                        rootElement.appendChild(element);

                        element = document.createElement("target");
                        DOMUtil.setElementValue(element, target);
                        rootElement.appendChild(element);

                        element = document.createElement("output-stream");
                        DOMUtil.setElementValue(element, outputStream.toString());
                        rootElement.appendChild(element);

                        element = document.createElement("error-stream");
                        DOMUtil.setElementValue(element, errorStream.toString());
                        rootElement.appendChild(element);

                        DOMUtil.prettyPrintDOM(document, stream);
                    }
                }  else {
                    throw new XFormsException("submission method '" + submission.getMethod() + "' at: " + DOMUtil.getCanonicalPath(submission.getElement()) + " not supported");
                }
            } catch (Exception e) {
                throw new XFormsException(e);
            }
        } else {
            throw new XFormsException("submission method '" + submission.getMethod() + "' at: " + DOMUtil.getCanonicalPath(submission.getElement()) + " not supported");
        }

        Map response = new HashMap();
        response.put(XFormsProcessor.SUBMISSION_RESPONSE_STREAM, new ByteArrayInputStream(stream.toByteArray()));
        return response;
    }

    private void runTarget(File buildFile, String target, ByteArrayOutputStream outputStream, ByteArrayOutputStream errorStream) {
        Project project = new Project();
        DefaultLogger consoleLogger = new DefaultLogger();
        consoleLogger.setErrorPrintStream(new PrintStream(errorStream));
        consoleLogger.setOutputPrintStream(new PrintStream(outputStream));

        project.setUserProperty("ant.file", buildFile.getAbsolutePath());
        project.init();
        ProjectHelper projectHelper = ProjectHelper.getProjectHelper();
        project.addReference("ant.projectHelper", projectHelper);
        projectHelper.parse(project, buildFile);
        project.executeTarget(target);
    }
}
