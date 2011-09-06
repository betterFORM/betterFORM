/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.agent.web.WebFactory;
import de.betterform.agent.web.WebProcessor;
import de.betterform.agent.web.WebUtil;
import de.betterform.xml.config.XFormsConfigException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * The XFormsServlet handles all interactions between client and
 * form-processor (XFormsProcessorImpl) for the whole lifetime of a form-filling session.
 * <br>
 * The Processor will be started through a Get-request from the client
 * pointing to the desired form-container. The Processor instance will
 * be stored in a Session-object.<br>
 * <br>
 * All further interaction will be handled through Post-requests.
 * Incoming request params will be mapped to data and action-handlers.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @author William Boyd
 * @version $Id: XFormsServlet.java 3007 2007-11-12 14:17:02Z lars $
 */
public class XFormsServlet extends HttpServlet {

    private static final Log LOGGER = LogFactory.getLog(XFormsServlet.class);
    //init-params
    protected String contextRoot = null;
    protected String configPath;
    protected String useragent;
    private de.betterform.agent.web.WebFactory webFactory;

    /**
     * Returns a short description of the servlet.
     *
     * @return - Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Servlet Controller for betterForm XForms Processor";
    }

    /**
     * Destroys the servlet.
     */
    public void destroy() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("cleanups allocated resources");
        }
//        webFactory.destroyXFormsSessionManager();
    }

    /**
     * Initializes the servlet.
     *
     * @param config - the ServletConfig object
     * @throws javax.servlet.ServletException
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        useragent = config.getInitParameter("useragent");

        webFactory = new WebFactory();
        webFactory.setServletContext(getServletContext());
        try {
            webFactory.initConfiguration(useragent);
            webFactory.initLogging(getClass());
            webFactory.initTransformerService(getServletContext().getRealPath("."));
            webFactory.initXFormsSessionCache();

        } catch (XFormsConfigException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Starts a new form-editing session.<br>
     * <p/>
     * The default value of a number of settings can be overridden as follows:
     * <p/>
     * 1. The uru of the xform to be displayed can be specified by using a param name of 'form' and a param value
     * of the location of the xform file as follows, which will attempt to load the current xforms file.
     * <p/>
     * http://localhost:8080/betterform/XFormsServlet?form=/forms/hello.xhtml
     * <p/>
     * 2. The uri of the XSLT file used to generate the form can be specified using a param name of 'xslt' as follows:
     * <p/>
     * http://localhost:8080/betterform/XFormsServlet?form=/forms/hello.xhtml&xslt=/betterform/my.xslt
     * <p/>
     * 3. Besides these special params arbitrary other params can be passed via the GET-string and will be available
     * in the context map of XFormsProcessorImpl. This means they can be used as instance data (with the help of ContextResolver)
     * or to set params for URI resolution.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see de.betterform.connector.ConnectorFactory
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        WebUtil.nonCachingResponse(response);

        HttpSession session = request.getSession(true);

        request.setAttribute(WebFactory.USER_AGENT, useragent);

        WebProcessor webProcessor = null;
        try {
            webProcessor = WebFactory.createWebProcessor(request);
            webProcessor.setRequest(request);
            webProcessor.setResponse(response);
            webProcessor.setHttpSession(session);
            webProcessor.setBaseURI(request.getRequestURL().toString());
            webProcessor.configure();
            webProcessor.setXForms();
            webProcessor.init();
            webProcessor.handleRequest();

        } catch (Exception e) {
            e.printStackTrace();
            if (webProcessor != null) {
                webProcessor.close(e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute(WebFactory.XFORMS_INPUTSTREAM,req.getInputStream());
        req.setAttribute(XFormsPostServlet.INIT_BY_POST,true);
        doGet(req,resp);
    }
}
// end of class

