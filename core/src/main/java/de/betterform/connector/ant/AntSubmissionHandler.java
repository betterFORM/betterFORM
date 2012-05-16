/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 * Author: Tobi Krebs (tobias.krebs AT betterform.de)
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
import org.w3c.dom.Attr;
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

/*
 * A Submissionhandler which allows one to run Ant-Files on the Server. The ant-build-file has to be accessible by the server for this two work.
 *
 * Basic syntax for the submissionhandler is :
 *  ant://<PATH-To-BUILD-FILE>[#TARGET]
 *
 * There exist two ways of invoking a target inside an ant-file via the submission handler:
 *
 * 1. As Fragment-Parameter:
 *   You can specify the desired target as part of the resource URI via an Fragment-Attribute:
 *
 *   e.g. to run the target "test" for the build-file "build-test.xml" (absolute path: /opt/tomcat/webapps/betterform/build-files/build-test.xml) you would specify an resource-URI like this:
 *      ant://{$webapp.realpath}/build-files/build-test.xml#test
 *   a submission declaration could look like this:
 *         <xf:submission id="s-ant-uri-target"
 *              method="get"
 *              replace="none"
 *              ref="instance()"
 *              validate="false"
 *              resource="ant://{$webapp.realpath}/build-files/build-test.xml#test">
 *
 * 2. Via an instance-element "target".
 *   As alternative you can specify the target via an "target"-element (<target/>) inside an instance.
 *   You can only specify one target per instance right now.
 *
 *   Sample instance:
 *   <xf:instance id="i-target">
 *      <data xmlns="">
 *          <target>test</target>
 *      </data>
 *  </xf:instance>
 *
 *  The resource-URI would then look like this:
 *      ant://{$webapp.realpath}/build-files/build-test.xml
 *
 *  And the Submission :
 *         <xf:submission id="s-ant-uri-target"
 *              method="get"
 *              replace="none"
 *              ref="instance('i-target')"
 *              validate="false"
 *              resource="ant://{$webapp.realpath}/build-files/build-test.xml">
 *
 *
 *  The results form the "ant-run" will be return by the submission-handler inside the SUBMISSION_RESPONSE_STREAM in the following format:
 *
 *  <ant>
 *      <buildFile>PATH-TO-BUILD-FILE</buildFile>
 *      <target>TARGET-WHICH-HAS-BEEN-RUN</target>
 *      <output-stream>OUTPUT-OF-SYSTEM-OUT</output-stream>
 *      <error-stream>OUTPUT-OF-SYSTEM-ERROR</error-stream>
 *  </ant>
 */
public class AntSubmissionHandler extends AbstractConnector implements SubmissionHandler {
      /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(AntSubmissionHandler.class);
    private String buildFile = "build.xml";


    public Map submit(Submission submission, Node instance) throws XFormsException {
        LOGGER.debug("AntSubmissionHandler.submit()");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (submission.getMethod().equals("get")) {
            try {
                String uri = getURI();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

                String buildFilePath = (new URI(uri)).getSchemeSpecificPart().substring((new URI(uri)).getSchemeSpecificPart().indexOf(':')+1);
                if (! "".equals(buildFilePath)) {
                    File buildFile = new File(buildFilePath);
                    String target = null;
                    if (uri.contains("#")) {
                        //got Traget from uri
                        target = uri.substring(uri.indexOf('#')+1);
                    } else if(((Document) instance).getElementsByTagName("target").item(0) != null){
                        //got target from xform
                        target = ((Document) instance).getElementsByTagName("target").item(0).getTextContent();
                    }else {
                        // use default target
                        target = "default";
                    }

                    LOGGER.debug("AntSubmissionHandler.runTarget() BuildFile: " + buildFile.getAbsolutePath() + " with Target:" + target);
                    runTarget(buildFile, target, outputStream, errorStream);

                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    factory.setValidating(false);
                    Document document = factory.newDocumentBuilder().newDocument();
                    document.appendChild(document.createElementNS(null, "ant"));

                    Element rootElement = document.getDocumentElement();
                    Attr filename = document.createAttribute("fileName");
                    filename.setValue(buildFile.getName());
                    rootElement.setAttributeNode(filename);
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

    private void runTarget(File buildFile, String target, ByteArrayOutputStream outputStream, ByteArrayOutputStream errorStream) throws Exception {
        Project project = new Project();
        DefaultLogger consoleLogger = new DefaultLogger();
        project.addBuildListener(consoleLogger);
        consoleLogger.setErrorPrintStream(new PrintStream(errorStream));
        consoleLogger.setOutputPrintStream(new PrintStream(outputStream));
        consoleLogger.setMessageOutputLevel(Project.MSG_INFO);


        project.setUserProperty("ant.file", buildFile.getAbsolutePath());
        project.init();
        ProjectHelper projectHelper = ProjectHelper.getProjectHelper();
        project.addReference("ant.projectHelper", projectHelper);
        projectHelper.parse(project, buildFile);
        if("default".equals(target)){
            String defaultTarget =project.getDefaultTarget();
            if(defaultTarget != null){
                project.executeTarget(defaultTarget);
            }else {
                throw new XFormsException("No target was given for Ant file and no default target is present on the build file");
            }

        }else {
            project.executeTarget(target);
        }

    }
}
