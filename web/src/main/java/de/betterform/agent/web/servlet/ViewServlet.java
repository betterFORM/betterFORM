/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.servlet;

import de.betterform.agent.web.WebProcessor;
import de.betterform.agent.web.WebUtil;
import de.betterform.generator.UIGenerator;
import de.betterform.xml.xforms.XFormsProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This servlet follows the PFG pattern (Post, forward, get) and provides the GET part. A seperate GET request at the end
 * of the request processing is used to render the output. This avoids problems with cached POST requests and the user
 * using the back button of the browser. Cause the GET request does not trigger such a warning the user can smoothly step
 * backwards.
 *
 * @author Joern Turner
 * @version $Version: $
 * @see PlainHtmlServlet
 */
public class ViewServlet extends HttpServlet /* extends AbstractXFormsServlet */ {
    private static final Log LOGGER = LogFactory.getLog(ViewServlet.class);

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
     * This method is only called when non-scripted mode is used to update the UI. This basically exists to support
     * the PFG (POST/FORWARD/GET) pattern that allows to use the browser back button without POSTDATA warning from the browser
     * and to re-initialize the preceding form (if any).
     * <p/>
     * To make sure that an update is requested from the XFormsSession and not by the user clicking the reload button the
     * XFormsSession holds a property XFormsSession.UPDATE_REQUEST when coming from XFormsSession. If this property exists,
     * the UI is refreshed otherwise the form is re-inited with a GET.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        XFormsProcessor webAdapter = null;

        HttpSession session = request.getSession(true);
        request.setCharacterEncoding("UTF-8");
        WebUtil.nonCachingResponse(response);

        String referer = request.getParameter("referer");

        WebProcessor webProcessor = WebUtil.getWebProcessor(request, response, session);
        try {
            if (webProcessor == null || webProcessor.getContextParam("update") == null) {
                LOGGER.info("session does not exist: creating new one");

                //todo: this has to be moved out in config or determined from request somehow - this won't work with the XFormsFilter
                //                    response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/XFormsServlet?" + referer));
                response.sendRedirect(response.encodeRedirectURL(referer));
            } else {
                //todo: check here if we got called by the WebProcessor or directly e.g. through a reload
                //todo: if not send back to XFormsServlet
                webProcessor.removeContextParam("update");
                response.setContentType(WebUtil.HTML_CONTENT_TYPE);

                UIGenerator uiGenerator = (UIGenerator) webProcessor.getContextParam(WebProcessor.UIGENERATOR);
                uiGenerator.setInput(webAdapter.getXForms());
                uiGenerator.setOutput(response.getOutputStream());
                uiGenerator.generate();

                response.getOutputStream().close();
            }
        } catch (Exception e) {
            webProcessor.close(e);
        }
    }
}
