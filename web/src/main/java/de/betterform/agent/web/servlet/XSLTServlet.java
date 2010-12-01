/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * @author Joern Turner
 */
public class XSLTServlet extends HttpServlet /* extends AbstractXFormsServlet */ {
    private static final Log LOGGER = LogFactory.getLog(XSLTServlet.class);
    public static final String defContentType = "text/html; charset=UTF-8";
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
     *
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String contentType = null;
        Document doc = null;
        String xsl = null;



//        contentType = (String) request.getAttribute("contenttype");
//        if (contentType == null) contentType = defContentType;
//        doc = (Document) request.getAttribute("xml-doc");

//        xsl = (String) request.getAttribute("xsl-file-name");

        String path = getServletContext().getRealPath("/resources/xslt");
        // Output goes in the response stream.
        PrintWriter out = response.getWriter();

        if (doc == null || xsl == null) {
            String errorMsg = "";
            if (doc == null)
                errorMsg += "XML document missing from call to OutputGenerator<br />";
            if (xsl == null)
                errorMsg += "XSL filename missing from call to OutputGenerator<br />";
            StringBuffer buf = generateError(errorMsg);
            log(errorMsg);
            out.write(buf.toString());
            return;
        }

        // The servlet returns HTML.
        response.setContentType(contentType);
        if (cache == null) cache = new HashMap();
        Transformer t = null;
        // Get the XML input document and the stylesheet.
        Source xmlSource = new DOMSource(doc);
        // Perform the transformation, sending the output to the response.

        // XSL processing can be time consuming, but this is mainly due to the overhead
        // of compiling the XSL sheet.
        // By caching the compiled sheets, the process is speeded up dramatically.
        // Time saved on subsequent requests can be 99% or more (hundreds of milliseconds).
        try {
            // check if the XSL sheet was found in cache, and use that if available
            if (cache.containsKey(xsl)) t = (Transformer) cache.get(xsl);
            else {
                // otherwise, load the XSL sheet from disk, compile it and store the compiled
                // sheet in the cache
                TransformerFactory tFactory = TransformerFactory.newInstance();
                Source xslSource = new StreamSource(new File(path, xsl + ".xsl"));
                t = tFactory.newTransformer(xslSource);
                cache.put(xsl, t);
            }
            // perform the XSL transformation of the XML Document into the servlet outputstream
            t.transform(xmlSource, new StreamResult(out));
        }
        catch (TransformerConfigurationException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
            throw new ServletException(e);
        }
        catch (TransformerException e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

    }
}
