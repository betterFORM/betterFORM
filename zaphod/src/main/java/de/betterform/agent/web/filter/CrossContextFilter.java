/*
 * This filter allows a web application to
 * use chiba in different context.
 *
 * To Install this filter you can use the following snippet
 * in your web.xml file:
 * <filter>
<filter-name>CrossContextFilter</filter-name>
<filter-class>de.betterform.agent.web.filter.CrossContextFilter</filter-class>
<init-param>
<param-name>xforms.engine.webcontext</param-name>
<param-value>chiba</param-value>
</init-param>
</filter>
<filter-mapping>
<filter-name>CrossContextFilter</filter-name>
<url-pattern>/*</url-pattern>
</filter-mapping>
 */
package de.betterform.agent.web.filter;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author fabian.otto@chiba-project.org
 */
public class CrossContextFilter implements Filter {
    private static final String ALTERNATIVE_ROOT = "ResourcePath";
//    private static final String XFORMSBASEURI = "XFormsBaseURI";
    private static final String XFORMSINPUTSTREAM = "XFormsInputStream";
    private static final String FORWARD_URL = "betterform.base.url";

    private static final boolean debug = false;
    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured.
    private FilterConfig filterConfig = null;
    private static final String message = "Please edit the file META-INF/context.xml and add 'crossContext=\"true\"' as attribute.";
    // context name of Chiba.
    private ServletContext context = null;
    // the repeating servlet in Chiba
    private String xformsServlet = null;
    // URL Prefix for URLs getting forwarded.
    private String xformsResources = null;


    public CrossContextFilter() {
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        if (debug) {
            log("CrossContextFilter:doFilter()");
        }

        // Create wrappers for the request and response objects.
        // Using these, you can extend the capabilities of the
        // request and response, for example, allow setting parameters
        // on the request before sending the request to the rest of the filter chain,
        // or keep track of the cookies that are set on the response.
        //
        // Caveat: some servers do not handle wrappers very well for forward or
        // include requests. This wrapper buffers the ouput to its stream.

        Throwable problem = forward(chain, request, response);

        // If there was a problem, we want to rethrow it if it is
        // a known type, otherwise log it.
        if (problem != null) {
            if (problem instanceof ServletException) {
                throw (ServletException) problem;
            }
            if (problem instanceof IOException) {
                throw (IOException) problem;
            }
            sendProcessingError(problem, response);
        }
    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) throws javax.servlet.ServletException {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                log("CrossContextFilter: Initializing filter");
            }
        }
        String xformsContext = filterConfig.getInitParameter("xforms.engine.webcontext");

        // get chiba's context dispatcher
        this.context = getFilterConfig().getServletContext().getContext("/" + xformsContext);

        if (context == null) {
            System.out.println("ERROR: Could not access the context " + xformsContext + ". " + CrossContextFilter.message);
            throw new ServletException("Could not access context " + xformsContext + ". ");
        }
        // If not set "/repeater" is used!
        if (filterConfig.getInitParameter("xforms.engine.servlet") != null) {
            this.xformsServlet = filterConfig.getInitParameter("xforms.engine.servlet");
        } else {
            this.xformsServlet = "/repeater";
        }

        this.xformsResources = filterConfig.getInitParameter("xforms.engine.resources");
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("CrossContextFilter()");
        }
        StringBuffer sb = new StringBuffer("CrossContextFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());

    }

    private Throwable forward(FilterChain chain, ServletRequest request, ServletResponse response) throws ServletException, IOException {
        Throwable problem = null;
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        try {
            if (httpRequest.getRequestURI().startsWith(xformsResources)) {
                resourceForward(request, response);
            } else if (httpRequest.getHeader("betterform-internal") != null) {
                chain.doFilter(request, response);
                response.setContentType("text/xml");
            } else {
                formForward(chain, request, response);
            }
        } catch (Throwable t) {
            /* If an exception is thrown somewhere down the filter chain,
               we still want to execute our after processing, and then
               rethrow the problem after that.
            */
            problem = t;
            t.printStackTrace();
            if (problem instanceof ServletException && problem.getCause() != null) {
                Throwable e = problem.getCause();
                printErrorPage(response, request, e);
            }
        }
        return problem;
    }

    private void formForward(FilterChain chain, ServletRequest request, ServletResponse response) throws IOException, ServletException {
        /* Process other filter and send the response to chiba for xforms processing.
           Since the filter should only run when a specified URL pattern matches this
           should be only the case for processing xforms.
        */
        BufferedHttpServletResponseWrapper bufferedResponse = new BufferedHttpServletResponseWrapper((HttpServletResponse) response);
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        chain.doFilter(request, bufferedResponse);
        System.out.println("Request: " + httpRequest.getRequestURI());
        System.out.println("Request Content Type: " + httpRequest.getContentType());
        RequestDispatcher dispatcher = context.getRequestDispatcher(xformsServlet);
        // create and set input stream from buffer for chiba
        request.setAttribute(XFORMSINPUTSTREAM, bufferedResponse.getInputStream());
        request.setAttribute(ALTERNATIVE_ROOT, xformsResources);
        request.setAttribute(FORWARD_URL, httpRequest.getRequestURL().toString());

        // forward the orignal request and response to chiba's context.
        dispatcher.forward(request, response);
    }

    private void resourceForward(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        /* Request is beeing forwarded to chiba. Just changing the URI.
           This is most likely an resource like an image or a css file.
        */
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI().replace(xformsResources, "");
        System.out.println("forward to new uri:" + uri);
        RequestDispatcher dispatcher = context.getRequestDispatcher(uri);
        dispatcher.forward(request, response);
    }

    private void printErrorPage(ServletResponse response, ServletRequest request, Throwable problem) throws IOException {
        response.setContentType("text/html");
        ServletOutputStream out = response.getOutputStream();
        String xformsContext = filterConfig.getInitParameter("xforms.engine.webcontext");


        String msg = problem.getMessage();
        int start = problem.getMessage().indexOf("/");
        String xpath ="unknown";
        String cause="";
        if(msg != null && start > 3){
            xpath = problem.getMessage().substring(start-1);
            msg=msg.substring(0,start-3);
        }
        if(problem.getCause() != null && problem.getCause().getMessage() != null){
            cause = problem.getCause().getMessage();
        }

        out.print("<html>\n" +
                "<head>\n" +
                "\t<title>Error Page</title>\n" +
                "\t<style>\n" +
                "\tbody{\n" +
                "        font-family:Tahoma;\n" +
                "        font-size:14pt;\n" +
                "        background:url('/" + xformsContext + "/resources/images/bgOne.gif') repeat-x scroll;\n" +
                "    }\n" +
                "\tpre { font-size:8pt; }\n" +
                "    .errorContent{\n" +
                "        margin-top:50px;\n" +
                "        width:600px;\n" +
                "        border:thin solid steelblue;\n" +
                "        margin-left:auto;\n" +
                "        margin-right:auto;\n" +
                "        padding:20px;\n" +
                "    }\n" +
                "    .message1{\n" +
                "        display:block;\n" +
                "        color:steelblue;\n" +
                "        font-weight:bold;\n" +
                "    }\n" +
                "    .message2{\n" +
                "        display:block;\n" +
                "        color:darkred;\n" +
                "        font-size:12pt;\n" +
                "        padding-top:30px;\n" +
                "        font-weight:bold;\n" +
                "    }\n" +
                "    .message3{\n" +
                "        display:block;\n" +
                "        font-size:10pt;\n" +
                "        color:steelblue;\n" +
                "        margin-top:10px;\n" +
                "    }\n" +
                "    input{\n" +
                "        margin-top:20px;\n" +
                "        margin-left:0;\n" +
                "        margin-bottom:0;\n" +
                "    }\n" +
                "\t</style>\n" +
                "</head>\n" +
                "<body>");
        out.print("<div class=\"errorContent\">\n" +
                "    <img src=\"/" + xformsContext + "/resources/images/error.png\" width=\"24\" height=\"24\" alt=\"Error\" style=\"float:left;padding-right:5px;\"/>\n" +
                "    <div class=\"message1\">\n" +
                "        Oops, an error occured...<br/>\n" +
                "\n" +
                "    </div>");    
        out.print("<div class=\"message2\">" + msg + "</div>\n" +
                "    <div class=\"message3\"><strong>URL:</strong><br/>" + request.getAttribute("betterform.referer") + "</div>\n" +
                "    <div class=\"message3\"><strong>Element causing Exception:</strong><br/>" + xpath + "</div>\n" +
                "    <div class=\"message3\"><strong>Caused by:</strong><br/>" + cause + "</div>\n" +
                "    <form>\n" +
                "        <input type=\"button\" value=\"Back\" onClick=\"history.back()\">\n" +
                "    </form>\n" +
//                "    <div class=\"message3\">\n" +
//                "    <a href=\"mailto:<%=Config.getInstance().getProperty(\"admin.mail\") %>?subject=XForms%20Problem%20at%20<%=session.getAttribute(\"betterform.referer\")%>&Body=Message:%0D<%= msg %>%0D%0DElement%20causing%20Exception:%0D<%= xpath %>%0D%0DCaused%20by:%0D<%= URLEncoder.encode(cause,\"UTF-8\") %>\">Report this problem...</a>\n" +
//                "    </div>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
        response.getOutputStream().close();
    }

    private void sendProcessingError(Throwable t, ServletResponse response) {
        String stackTrace = getStackTrace(t);

        if (stackTrace != null && !stackTrace.equals("")) {
            try {
                response.setContentType("text/html");
                PrintStream ps = new PrintStream(response.getOutputStream());
                PrintWriter pw = new PrintWriter(ps);
                pw.print("<html>\n<head>\n<title>Error</title>\n</head>\n<body>\n"); //NOI18N

                // PENDING! Localize this for next official release
                pw.print("<h1>The resource did not process correctly</h1>\n<pre>\n");
                pw.print(stackTrace);
                pw.print("</pre></body>\n</html>"); //NOI18N
                pw.close();
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        } else {
            try {
                PrintStream ps = new PrintStream(response.getOutputStream());
                t.printStackTrace(ps);
                ps.close();
                response.getOutputStream().close();
            } catch (Exception ex) {
            }
        }
    }

    public static String getStackTrace(Throwable t) {
        String stackTrace = null;
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.close();
            sw.close();
            stackTrace = sw.getBuffer().toString();
        } catch (Exception ex) {
        }
        return stackTrace;
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }
}
