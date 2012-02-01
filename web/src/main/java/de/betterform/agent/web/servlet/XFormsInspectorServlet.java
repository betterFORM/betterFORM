/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.servlet;

import de.betterform.agent.web.WebFactory;
import de.betterform.agent.web.WebUtil;
import de.betterform.xml.config.Config;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.XPathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.xforms.XFormsModelElement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;

/**
 * creates debugging output of a XForms document. Supports to render the host document as it exists at the time
 * this servlet is called as well as the output of any instance document.
 *
 * @author Joern Turner
 */
public class XFormsInspectorServlet extends HttpServlet /* extends AbstractXFormsServlet */ {
    private static final Log LOGGER = LogFactory.getLog(XFormsInspectorServlet.class);
    public static final String defContentType = WebUtil.HTML_CONTENT_TYPE;
    private de.betterform.agent.web.WebFactory webFactory;

    /**
     * Returns a short description of the servlet.
     *
     * @return - Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "responsible for showing the views to the user in betterForm XForms applications";
    }

    /**
     * Destroys the servlet.
     */
    public void destroy() {
    }

    /**
     *
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String resource = request.getRequestURI();

        /*
         must be in the form:
            'xforms/[SESSION-ID]/[model-id]/[instance-id]' OR
            'xforms/[SESSION-ID]/hostDOM'
        */
        //

        // later probably and optional xpath locationpath can be added
        HttpSession session = request.getSession(true);


        String[] steps = XPathUtil.splitPathExpr(resource);
        String xformsSessionId;
        if (resource.indexOf("hostDOM") != -1) {
            xformsSessionId = steps[steps.length-2];
        } else {
            xformsSessionId = steps[steps.length-3];
        }
        XFormsProcessor processor = WebUtil.getWebProcessor(xformsSessionId);
        if(processor == null){
            sendError(request, response, session, null,"Processor with sessionId '" + xformsSessionId + "' not found.");
            return;
        }

        try {

            response.setContentType("text/html");
            OutputStream out = response.getOutputStream();
            Element rootNode = DOMUtil.createRootElement("data");
            DOMUtil.appendElement(rootNode,"URI",processor.getBaseURI());
            DOMUtil.appendElement(rootNode,"context",request.getContextPath());
            if (resource.indexOf("hostDOM") != -1) {
                // output Host document markup
                Document host = (Document) processor.getXForms();
                request.setAttribute(WebFactory.IGNORE_RESPONSE_BODY, "TRUE");
                WebUtil.doTransform(getServletContext(),response, host,"highlightDocument.xsl",rootNode);
            } else {
                String modelId = steps[steps.length-2];
                String instanceId = steps[steps.length-1];

                //try to get model
                XFormsModelElement model;
                try{
                    model = processor.getXFormsModel(modelId) ;
                }catch(XFormsException xe){
                    sendError(request, response, session, xe,"Model with id '" + modelId + "' not found.");
                    return;
                }
                try{
                    Document instance = model.getInstanceDocument(instanceId);
                    DOMUtil.appendElement(rootNode,"model",modelId);
                    DOMUtil.appendElement(rootNode,"instance",instanceId);
                    WebUtil.doTransform(getServletContext(), response, instance, "highlightDocument.xsl", rootNode);

//                    DOMUtil.prettyPrintDOM(instance, out);
                }catch(DOMException de){
                    sendError(request, response, session, null, "Instance with id '" + instanceId + "' not found.");
                    return;
                }
            }

        } catch (XFormsException e) {
            //todo: should use sendError?
            e.printStackTrace();
        }
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, HttpSession session, Exception e,String message) throws ServletException, IOException {
        session.setAttribute("betterform.exception", e);
        session.setAttribute("betterform.exception.message",message);
        session.setAttribute("betterform.referer", request.getRequestURL());
        String path = null;
        try {
            path = "/" + Config.getInstance().getProperty(WebFactory.ERROPAGE_PROPERTY);
        } catch (XFormsConfigException ce) {
            ce.printStackTrace();
        }
        getServletContext().getRequestDispatcher(path).forward(request,response);
    }
}
