/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.filter;

import de.betterform.agent.web.WebFactory;
import de.betterform.generator.XSLTGenerator;
import de.betterform.xml.xforms.exception.XFormsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URISyntaxException;

public class XSLTFilter implements Filter {
    private static final Log LOG = LogFactory.getLog(XSLTFilter.class);

    private FilterConfig filterConfig = null;
    private String xsltPath;
    private String xslFile;

    public void init(FilterConfig filterConfig) throws
            ServletException {
        this.filterConfig = filterConfig;
        this.xsltPath = filterConfig.getInitParameter("xsltHome");
        this.xslFile = filterConfig.getInitParameter("xsltFile");
    }

    public void destroy() {
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse
            response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest  = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        ServletContext servletContext = filterConfig.getServletContext();

        String stylePath = servletContext.getRealPath(xsltPath);
        File styleFile = new File(stylePath,xslFile);


        PrintWriter out = response.getWriter();
        CharResponseWrapper wrapper = new CharResponseWrapper((HttpServletResponse) response);
        try {
            XSLTGenerator generator = WebFactory.setupTransformer(xsltPath,xslFile,servletContext);
//        Source styleSource = new StreamSource(styleFile);

            wrapper.setContentType("text/html");
            chain.doFilter(request, wrapper);

            StringReader sr = new StringReader(wrapper.toString());
            generator.setInput(sr);
//            Source xmlSource = new StreamSource((Reader) sr);
            CharArrayWriter caw = new CharArrayWriter();
            generator.setOutput(caw);

//            StreamResult result = new StreamResult(caw);
//            transformer.transform(xmlSource, result);
            generator.generate();
            response.setContentLength(caw.toString().length());
            out.write(caw.toString());

        } catch (URISyntaxException e) {
            out.println(e.toString());
            out.write(wrapper.toString());
        } catch (XFormsException e) {
            out.println(e.toString());
            out.write(wrapper.toString());
        }
    }

}
