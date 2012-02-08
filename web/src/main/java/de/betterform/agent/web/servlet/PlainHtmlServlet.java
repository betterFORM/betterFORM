/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.servlet;

import de.betterform.agent.web.WebProcessor;
import de.betterform.agent.web.WebUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This servlet handles clients with accept only plain html without javascript. Cause not the complete XForms
 * functionality can be covered in pure request/response fashion there will be some limitations in user-experience but
 * they'll still work.
 *
 * @author Joern Turner
 * @version $Id: PlainHtmlServlet.java 2875 2007-09-28 09:43:30Z lars $
 */
public class PlainHtmlServlet extends HttpServlet {
    private static final Log LOGGER = LogFactory.getLog(PlainHtmlServlet.class);

    /**
     * Returns a short description of the servlet.
     *
     * @return - Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Plain HTML Servlet Controller for betterForm XForms Processor";
    }

    /**
     * Destroys the servlet.
     */
    public void destroy() {
    }

    /**
     * handles all interaction with the user during a form-session.
     * <p/>
     * Note: this method is only triggered if the
     * browser has javascript turned off.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);
        request.setCharacterEncoding("UTF-8");
        WebUtil.nonCachingResponse(response);

        WebProcessor webProcessor = WebUtil.getWebProcessor(request, session);
        try {

            webProcessor.setRequest(request);
            webProcessor.setResponse(response);
            webProcessor.handleRequest();
        } catch (Exception e) {
            webProcessor.close(e);
        }
    }

}

// end of class
