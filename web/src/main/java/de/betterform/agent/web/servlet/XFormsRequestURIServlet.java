/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.servlet;

import de.betterform.BetterFORMConstants;
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
import java.io.*;

/**
 * Simple servlet to work in conjunction with XFormsFilter.
 *
 * @author Joern Turner
 */
public class XFormsRequestURIServlet extends HttpServlet {
    private static final Log LOGGER = LogFactory.getLog(XFormsRequestURIServlet.class);


    /**
     * This servlet uses the requestURI to locate and parse a XForms document for processing. The actual processing
     * is done by XFormsFilter. The parsed DOM of the document is passed as a request param to the filter.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        LOGGER.debug("hit XFormsRequestURIServlet");

        request.setCharacterEncoding("UTF-8");
            WebUtil.nonCachingResponse(response);

            Document doc;

            //locate it
            String formRequestURI = request.getRequestURI().substring(request.getContextPath().length()+1);
            File xfDoc = new File(getServletContext().getRealPath(formRequestURI));
        //TODO: XFORMS  PROCESSING
        if (request.getHeader(BetterFORMConstants.BETTERFORM_INTERNAL) != null) {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(xfDoc));
            BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
            
            int read;
            while ((read = in.read()) > -1) {
                out.write(read);
            }
            out.flush();
        } else {
            try {
                //parse it
                doc = DOMUtil.parseXmlFile(xfDoc, true, false);
            } catch (ParserConfigurationException e) {
                throw new ServletException(e);
            } catch (SAXException e) {
                throw new ServletException(e);
            }
    
            request.setAttribute(WebFactory.XFORMS_NODE, doc);
            //do the Filter twist
            response.getOutputStream().close();
        }
    }

}
