/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.servlet;

import de.betterform.agent.web.WebUtil;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.util.PositionalXMLReader;
import de.betterform.xml.xforms.exception.XFormsErrorIndication;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * a servlet to generate an error page with xslt
 *
 * author: joern turner
 */
public class ErrorServlet extends HttpServlet {
    private static final Log LOGGER = LogFactory.getLog(ErrorServlet.class);


    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        WebUtil.nonCachingResponse(response);
        //pick up the exception details
        String xpath ="unknown";
        String cause= "";
        String msg = (String) getSessionAttribute(request,"betterform.exception.message");
        if (msg != null) {
            int start = msg.indexOf("::");
            if(start > 3){
                xpath = msg.substring(start+2);
                msg=msg.substring(0,start);
            }
            //todo: don't we need an 'else' here?
        }
        Exception ex = (Exception) getSessionAttribute(request, "betterform.exception");
        if(ex != null && ex.getCause() != null && ex.getCause().getMessage() != null){
            cause = ex.getCause().getMessage();
        }

        //create XML structure for exception details
        Element rootNode = DOMUtil.createRootElement("error");
        DOMUtil.appendElement(rootNode, "context", request.getContextPath());
        DOMUtil.appendElement(rootNode, "url", request.getSession().getAttribute("betterform.referer").toString());
        DOMUtil.appendElement(rootNode, "xpath", xpath);
        DOMUtil.appendElement(rootNode, "message", msg);
        DOMUtil.appendElement(rootNode, "cause", cause);

        //transform is different depending on exception type
        if(ex instanceof XFormsErrorIndication){
            Object o = ((XFormsErrorIndication)ex).getContextInfo();
            if(o instanceof HashMap){
                HashMap<String,Object> map = (HashMap) ((XFormsErrorIndication) ex).getContextInfo();
                if(map.size() != 0){
                    Element contextinfo = rootNode.getOwnerDocument().createElement("contextInfo");
                    rootNode.appendChild(contextinfo);
                    for(Map.Entry<String,Object> entry : map.entrySet()){
                        DOMUtil.appendElement(rootNode, entry.getKey(), entry.getValue().toString());
                    }
                }
            }
            //todo: check -> there are contextInfos containing a simple string but seems to be integrated within above error message already - skip for now
/*
            else{
            }
*/
            Document hostDoc = (Document) getSessionAttribute(request, "betterform.hostDoc");
            String serializedDoc = DOMUtil.serializeToString(hostDoc);
            //reparse
            try {
                byte bytes[] = serializedDoc.getBytes("UTF-8");
                Document newDoc = PositionalXMLReader.readXML(new ByteArrayInputStream(bytes));
                DOMUtil.prettyPrintDOM(newDoc);
                //eval xpath
                Node n = XPathUtil.evaluateAsSingleNode(newDoc,xpath);
                String linenumber = (String) n.getUserData("lineNumber");
                DOMUtil.appendElement(rootNode, "lineNumber", linenumber);

                WebUtil.doTransform(getServletContext(), response, newDoc, "highlightError.xsl", rootNode);

            } catch (Exception e) {

                LOGGER.error(e);

            } finally {

                // in all cases, log whatever information about the error is available

                try {

                    if (LOGGER.isErrorEnabled()) {
                        ByteArrayOutputStream messageBuf = new ByteArrayOutputStream();
                        DOMUtil.prettyPrintDOM(rootNode, messageBuf);
                        LOGGER.error(messageBuf.toString());
                    }

                } catch (Exception e) {
                    // last resort
                    LOGGER.error(msg, e);
                }

            }

        } else{
            WebUtil.doTransform(getServletContext(), response, rootNode.getOwnerDocument(), "error.xsl", null);
        }
    }



    private Object getSessionAttribute(HttpServletRequest request,String sessionVar) {
        Object o = request.getSession().getAttribute(sessionVar);
        request.getSession().removeAttribute(sessionVar);
        return o;
    }

}
