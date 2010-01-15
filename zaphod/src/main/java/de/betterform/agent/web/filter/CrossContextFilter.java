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

import de.betterform.agent.web.filter.BufferedHttpServletResponseWrapper;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
        BufferedHttpServletResponseWrapper bufferedResponse = new BufferedHttpServletResponseWrapper((HttpServletResponse) response);
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        Throwable problem = null;

        
        if (httpRequest.getRequestURI().startsWith(xformsResources)) {
            /* Request is beeing forwarded to chiba. Just changing the URI. 
               This is most likely an resource like an image or a css file.
            */
            String uri = httpRequest.getRequestURI().replace(xformsResources, "");
            System.out.println("forward to new uri:" + uri);
            RequestDispatcher dispatcher = context.getRequestDispatcher(uri);
            dispatcher.forward(request, response);
        } else {
            /* Process other filter and send the response to chiba for xforms processing. 
               Since the filter should only run when a specified URL pattern matches this
               should be only the case for processing xforms.
            */ 
            try {
                chain.doFilter(request, bufferedResponse);
                System.out.println("Request: " + httpRequest.getRequestURI());
                RequestDispatcher dispatcher = context.getRequestDispatcher(xformsServlet);
                // create and set input stream from buffer for chiba
                request.setAttribute(XFORMSINPUTSTREAM, bufferedResponse.getInputStream());
                request.setAttribute(ALTERNATIVE_ROOT, xformsResources);
                // forward the orignal request and response to chiba's context.
                dispatcher.forward(request, response);
            } catch (Throwable t) {
                /* If an exception is thrown somewhere down the filter chain,
                   we still want to execute our after processing, and then
                   rethrow the problem after that.
                */
                problem = t;
                t.printStackTrace();
            }
        }
        return problem;
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
