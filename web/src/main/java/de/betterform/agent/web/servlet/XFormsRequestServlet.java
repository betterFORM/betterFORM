/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.servlet;

import de.betterform.agent.web.WebFactory;
import de.betterform.agent.web.WebUtil;
import de.betterform.xml.dom.DOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Simple servlet to work in conjunction with XFormsFilter.
 *
 * @author Joern Turner
 */
public class XFormsRequestServlet extends HttpServlet {
    private static final Log LOGGER = LogFactory.getLog(XFormsRequestServlet.class);

    /**
     * This ia jus a little demo servlet to show operation with XFormsFilter. It parses the XForms
     * document form the file system and writes it out to the ServletStream. Additionally it sets the request property
     * XFORMS_FILTER to allow the Filter to work efficiently.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        LOGGER.debug("hit XFormsRequestServlet");


        request.setCharacterEncoding("UTF-8");
        WebUtil.nonCachingResponse(response);

        Document doc;

        //locate it
//        String formURI = request.getParameter(WebFactory.FORM_PARAM_NAME);
        String formURI = WebUtil.getFormUrlAsString(request);
        if (formURI.startsWith("http://")) {
            InputStream inputStream = new URL(formURI).openStream();
            try {
                doc = DOMUtil.parseInputStream(inputStream, true, false);
            } catch (ParserConfigurationException e) {
                throw new ServletException(e);
            } catch (SAXException e) {
                throw new ServletException(e);
            }
        } else {
            String realPath = WebFactory.getRealPath(formURI, getServletContext());
            File xfDoc = new File(realPath);
            try {
                doc = DOMUtil.parseXmlFile(xfDoc, true, false);
            } catch (ParserConfigurationException e) {
                throw new ServletException(e);
            } catch (SAXException e) {
                throw new ServletException(e);
            }
        }

        //parse it

        request.setAttribute(WebFactory.XFORMS_NODE, doc);
//        try {
//            DOMUtil.prettyPrintDOM(doc,response.getOutputStream());
//        } catch (TransformerException e) {
//            throw new ServletException(e);
//        }

        //do the Filter twist
        response.getOutputStream().close();
    }

}
