/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.connector.exec;

import org.apache.log4j.Category;
import de.betterform.connector.AbstractConnector;
import de.betterform.connector.SubmissionHandler;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
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
        try {
            // resolve action uri against base uri in order to get absolute path to program
            URI baseURI = new URI(submission.getContainerObject().getProcessor().getBaseURI());
            URI actionURI = new URI(getURI());
            File program = new File(baseURI.resolve(actionURI.getSchemeSpecificPart()));

            // execute program
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("executing '" + program.getAbsolutePath() + "'");
            }
            int code = new ProcessExecutor().execute(program.getParentFile(), program.getAbsolutePath(), outputStream, errorStream);
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

//            JXPathContext context = JXPathContext.newContext(document.getDocumentElement());
//            // context.setFactory(new InstanceFactory());
//            context.createPathAndSetValue("command", program.getName());
//            context.createPathAndSetValue("working-dir", program.getParentFile().toURI().getPath());
//            context.createPathAndSetValue("exit-code", String.valueOf(code));
//            context.createPathAndSetValue("std-out", outputStream);
//            context.createPathAndSetValue("std-err", errorStream);
//
//            // create response stream
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            DOMUtil.prettyPrintDOM(document, stream);
//            Map response = new HashMap();
//            response.put(WebProcessor.SUBMISSION_RESPONSE_STREAM, new ByteArrayInputStream(stream.toByteArray()));

//            return response;
            return null;
        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    }

}
