/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.servlet;

import de.betterform.agent.web.WebFactory;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.dom.DOMUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 */
public class XSLTServlet extends HttpServlet /* extends AbstractXFormsServlet */ {
    private static final Log LOGGER = LogFactory.getLog(XSLTServlet.class);
    private String editorHome;
    private String xslFile;
    public static final String defContentType = "text/html; charset=UTF-8";
    public static final String contentTypeHTML = defContentType;
    public static final String errorTemplate = "+++ERRORS+++";
    private static HashMap cache;


    /**
     * Returns a short description of the servlet.
     *
     * @return - Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "responsible for showing the views to the user in betterForm XForms applications";
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.editorHome = config.getInitParameter("editorHome");
        this.xslFile = config.getInitParameter("xsltFile");


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
        ServletContext servletContext = getServletContext();

        String stylePath = null;
        try {
            stylePath = WebFactory.getRealPath(editorHome, servletContext);
        } catch (XFormsConfigException e) {
            throw new ServletException(e);
        }
        File styleFile = new File(stylePath,xslFile);
        if(styleFile == null){
            throw new ServletException("XSL stylesheet cannot be found: " + styleFile);
        }

        String contentType = null;
        Document doc = null;
        String xsl = null;

        contentType = (String)request.getAttribute("contenttype");
        if (contentType == null) contentType = defContentType;


        // The servlet returns HTML.
        response.setContentType(contentType);
        if (cache == null) cache = new HashMap();
        Transformer t = null;
        // Get the XML input document and the stylesheet.
//        Source xmlSource = new DOMSource(doc);
        String inputFile=request.getPathTranslated();
        File input = new File(inputFile);
        if(input == null){
            throw new ServletException("XML document cannot be found: " + inputFile);
        }
        Source xmlSource =  new StreamSource(new FileInputStream(input));
        // Perform the transformation, sending the output to the response.

        // XSL processing can be time consuming, but this is mainly due to the overhead
        // of compiling the XSL sheet.
        // By caching the compiled sheets, the process is speeded up dramatically.
        // Time saved on subsequent requests can be 99% or more (hundreds of milliseconds).
        try
        {
            // check if the XSL sheet was found in cache, and use that if available
//            if (cache.containsKey(xsl)) t = (Transformer)cache.get(xsl);
//            else
//            {
                // otherwise, load the XSL sheet from disk, compile it and store the compiled
                // sheet in the cache
                TransformerFactory tFactory = TransformerFactory.newInstance();
                Source xslSource = new StreamSource(new FileInputStream(styleFile));
                t = tFactory.newTransformer(xslSource);
                String contextName=request.getContextPath();

                t.setParameter("APP_CONTEXT",contextName);
                t.setParameter("EDITOR_HOME", stylePath.substring(stylePath.indexOf("/betterform/")) + "/");
                t.setParameter("filename", "file://" + inputFile);

//                cache.put(xsl, t);
//            }

            // perform the XSL transformation of the XML Document into the servlet outputstream
//            t.transform(xmlSource, new StreamResult(out));
            Document resultDoc = DOMUtil.newDocument(true,false);
            t.transform(xmlSource, new DOMResult(resultDoc));
            request.setAttribute(WebFactory.XFORMS_NODE, resultDoc);
        }
        catch (TransformerConfigurationException e)
        {
            e.printStackTrace();
            throw new ServletException(e);
        }
        catch (TransformerFactoryConfigurationError e)
        {
            e.printStackTrace();
            throw new ServletException(e);
        }
        catch (TransformerException e)
        {
            e.printStackTrace();
            throw new ServletException(e);
        }
    }

    private StringBuffer generateError(String error) throws IOException, XFormsConfigException {

		String path = WebFactory.getRealPath("forms/incubator/editor", getServletContext());
		File f = new File(path, "callerror.html");
		FileInputStream fs = new FileInputStream(f);
		BufferedInputStream bis = new BufferedInputStream(fs);
		int numBytes = bis.available();
		byte b[] = new byte[numBytes];
		// read the template into a StringBuffer (via a byte array)
		bis.read(b);
		StringBuffer buf = new StringBuffer();
		buf.append(b);
		int start = buf.indexOf(errorTemplate);
		int end = start + errorTemplate.length();
		// replace placeholder with errormessage
		// (will automatically adjust to take longer or shorter data into account)
		buf.replace(start, end, error);
		return buf;
	}
}
