/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.submission.Submission;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joern Turner
 */
public class ModelSubmissionHandler extends AbstractConnector implements SubmissionHandler {
    private static Log LOGGER = LogFactory.getLog(ModelSubmissionHandler.class);

    /**
     * Purpose:<br/>
     * The ModelSubmissionHandler can be used to exchange data between XForms models. It is capable of replacing data
     * in a certain instance of the receiver model.<br/><br/>
     *
     * Syntax: model:[Model ID]#instance('[Instance ID]')/[XPath]<br/><br/>
     *
     * Caveats:<br/>
     * - Model and Instance IDs must be known to allow explicit addressing<br/>
     * - the form author has to make sure that no ID collisions take place<br/>
     * - the XPath must be explicitly given even if the the target Node is the root Node of the instance
     *
     *
     * @param submission the submission issuing the request.
     * @param instance   the instance data to be serialized and submitted.
     * @return
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if any error occurred during submission.
     */

    public Map submit(Submission submission, Node instance) throws XFormsException {
        try {
            String replaceMode = submission.getReplace();
            if (!(replaceMode.equals("none") || replaceMode.equals("instance"))) {
                throw new XFormsException("ModelSubmissionHandler only supports 'none' or 'instance' as replace mode");
            }

            String submissionMethod = submission.getMethod();
            String resourceAttr = getURI();
            String resourceModelId = null;
            String instanceId = null;
            String xpath = null;

            try {
                int devider = resourceAttr.indexOf("#");
                resourceModelId = resourceAttr.substring(resourceAttr.indexOf(":") + 1, devider);

                int instanceIdStart = resourceAttr.indexOf("(") + 1;
                int instanceIdEnd = resourceAttr.indexOf(")");
                instanceId = resourceAttr.substring(instanceIdStart + 1, instanceIdEnd - 1);
                if(resourceAttr.indexOf("/") != -1){
                    xpath = resourceAttr.substring(resourceAttr.indexOf("/"));
                }else{
                    throw new XFormsException("Syntax error: xpath mustn't be null. You've to provide at least the path to the rootnode.");
                }
            } catch (IndexOutOfBoundsException e) {
                throw new XFormsException("Syntax error in expression: " + resourceAttr);
            }

            Model providerModel;
            Model receiverModel;
            if(submissionMethod.equalsIgnoreCase("get")){
                providerModel = submission.getContainerObject().getModel(resourceModelId);
                receiverModel = submission.getModel();
                Node targetNode = XPathUtil.getAsNode(XPathCache.getInstance().evaluate(providerModel.getInstance(instanceId).getRootContext().getNodeset(),
                        1,
                        xpath,
                        providerModel.getPrefixMapping(),
                        providerModel.getXPathFunctionContext()),
                        1);

                if (targetNode == null) {
                    throw new XFormsException("targetNode for xpath: " + xpath + " not found");
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("targetNode to replace............");
                    DOMUtil.prettyPrintDOM(targetNode);
                }

                Document result = DOMUtil.newDocument(true,false);
                result.appendChild(result.importNode(targetNode.cloneNode(true),true));
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("result Instance after insertion ............");
                    DOMUtil.prettyPrintDOM(result);
                }

                Map response=new HashMap(1);
                response.put(XFormsProcessor.SUBMISSION_RESPONSE_DOCUMENT,result);
                return response;
            }else if(submissionMethod.equalsIgnoreCase("post")){
                providerModel = submission.getModel();

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Instance Data to post............");
                    DOMUtil.prettyPrintDOM(instance);
                }

                receiverModel = submission.getContainerObject().getModel(resourceModelId);
                Node targetNode = XPathUtil.getAsNode(XPathCache.getInstance().evaluate(receiverModel.getInstance(instanceId).getRootContext().getNodeset(),
                        1, xpath,
                        receiverModel.getPrefixMapping(),
                        receiverModel.getXPathFunctionContext()),
                        1);

                if (targetNode == null) {
                    throw new XFormsException("targetNode for xpath: " + xpath + " not found");
                }
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("targetNode to replace............");
                    DOMUtil.prettyPrintDOM(targetNode);
                }
                //todo:review - this can be an Eleement if ref was used on submission!!!
                if (instance instanceof Document) {
                    Document toImport = (Document) instance;
                    targetNode.getParentNode().replaceChild(targetNode.getOwnerDocument().importNode(toImport.getDocumentElement(), true), targetNode);
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("result Instance after insertion ............");
                    DOMUtil.prettyPrintDOM(receiverModel.getDefaultInstance().getInstanceDocument());
                }
            }else{
                throw new XFormsException("Submission method '" + submissionMethod + "' not supported");
            }
            receiverModel.rebuild();
            receiverModel.recalculate();
            receiverModel.revalidate();
            receiverModel.refresh();

            return new HashMap(1);
        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    }
}
