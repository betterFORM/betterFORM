/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
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
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xpath.XPathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.xforms.XFormsModelElement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.OutputStream;

/**
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

            if (resource.indexOf("hostDOM") != -1) {
                // output Host document markup
                Node host = processor.getXForms();
                OutputStream out = response.getOutputStream();
                response.setContentType("text/plain");
                request.setAttribute(WebFactory.IGNORE_RESPONSE_BODY, "TRUE");
                DOMUtil.prettyPrintDOM(host, out);
                out.close();
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
                // fetch Instance and serialize as XML
                try{
                    Document instance = model.getInstanceDocument(instanceId);
                    OutputStream out = response.getOutputStream();
                    response.setContentType("application/xml");
                    DOMUtil.prettyPrintDOM(instance, out);
                }catch(DOMException de){
                    sendError(request, response, session, null,"Instance with id '" + instanceId + "' not found.");
                    return;
                }
            }

        } catch (XFormsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        // write to outputstream
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
