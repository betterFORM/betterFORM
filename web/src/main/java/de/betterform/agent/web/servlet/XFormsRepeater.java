/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.agent.web.servlet;

import de.betterform.xml.xforms.exception.XFormsException;
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
import javax.servlet.ServletContext;

/**
 * The XFormsRepeater handles forwarded requests from other contexts and interacts with
 * form-processor (XFormsProcessorImpl) for the whole lifetime of a form-filling session.
 * <br>
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @author William Boyd
 * @author Fabian Otto
 * @version $Id: XFormsServlet.java 3007 2007-11-12 14:17:02Z lars $
 */
public class XFormsRepeater extends HttpServlet {

    private static final Log LOGGER = LogFactory.getLog(XFormsRepeater.class);
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
            webFactory.initTransformerService();
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
     * @see de.betterform.xml.xforms.connector.ConnectorFactory
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        WebUtil.nonCachingResponse(response);
        HttpSession session = null;

        if (request.getAttribute("Session") != null) {
            session = (HttpSession) request.getAttribute("Session");
        } else {
            session = request.getSession(true);
        }

        request.setAttribute(WebFactory.USER_AGENT, useragent);
        System.out.println("request: " + request.getRequestURL().toString());

        String url = request.getRequestURL().toString();
        ServletContext context = this.getServletContext();

        if (request.getAttribute("XFormsInputStream") != null) {
            System.out.println("Request has an extra Stream!: ");
            System.out.println("Context: " + context.getServletContextName());
        }
//        XFormsSession xFormsSession = null;
        WebProcessor webProcessor = null;
        try {
            webProcessor = WebFactory.createWebProcessor(request);
            webProcessor.setRequest(request);
            webProcessor.setResponse(response);
            webProcessor.setHttpSession(session);
            webProcessor.setBaseURI(url);
            webProcessor.setContext(context);
            webProcessor.configure();
            webProcessor.setXForms();
            webProcessor.init();
            webProcessor.handleRequest();
        } catch (Exception e) {
            if (webProcessor != null) {
                // attempt to shutdown processor
                try {
                    webProcessor.shutdown();
                } catch (XFormsException xfe) {
//                    LOG.error("Could not shutdown Processor: Error: " + xfe.getMessage() + " Cause: " + xfe.getCause());
                    
                }
                // store exception
                session.setAttribute("betterform.exception", e);
                session.setAttribute("betterform.referer", request.getRequestURL());
                //remove session from XFormsSessionManager
                WebUtil.removeSession(webProcessor.getKey());
//                context.getRequestDispatcher("/jsp/error.jsp").forward(request,response);
                throw new ServletException(e);
            }
        }
    }
}
// end of class

