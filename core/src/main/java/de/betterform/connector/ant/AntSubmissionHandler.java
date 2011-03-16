/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.connector.ant;


import de.betterform.connector.AbstractConnector;
import de.betterform.connector.SubmissionHandler;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.net.URI;
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

        if (submission.getMethod().equals("get")) {
            try {
                String uri = getURI();

                String buildFilePath = (new URI(uri)).getSchemeSpecificPart().substring((new URI(uri)).getSchemeSpecificPart().indexOf(':')+1);
                if (! "".equals(buildFilePath)) {
                    File buildFile;
                    String target;
                    //get Traget from uri
                    if (uri.contains("#")) {
                        buildFile = new File(buildFilePath);
                        target = uri.substring(uri.indexOf('#'+1));
                    } else {
                        //get target from xform
                        buildFile = new File(buildFilePath);
                        target = ((Document) instance).getElementsByTagName("target").item(0).getTextContent();
                    }

                    LOGGER.debug("AntSubmissionHandler.runTarget() BuildFile: " + buildFile.getAbsolutePath() + " with Target:" + target);
                    runTarget(buildFile, target);
                }  else {
                    throw new XFormsException("submission method '" + submission.getMethod() + "' at: " + DOMUtil.getCanonicalPath(submission.getElement()) + " not supported");
                }
            } catch (Exception e) {
                throw new XFormsException(e);
            }
        } else {
            throw new XFormsException("submission method '" + submission.getMethod() + "' at: " + DOMUtil.getCanonicalPath(submission.getElement()) + " not supported");
        }
        return null;
    }

    private void runTarget(File buildFile, String target) {
        Project project = new Project();
        project.setUserProperty("ant.file", buildFile.getAbsolutePath());
        project.init();
        ProjectHelper projectHelper = ProjectHelper.getProjectHelper();
        project.addReference("ant.projectHelper", projectHelper);
        projectHelper.parse(project, buildFile);
        project.executeTarget(target);
    }
}
