/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.resources;

import de.betterform.agent.web.WebFactory;
import de.betterform.agent.web.resources.stream.DefaultResourceStreamer;
import de.betterform.agent.web.resources.stream.ResourceStreamer;
import de.betterform.xml.config.XFormsConfigException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ResourceServlet is responsible for streaming resources like css, script, images and etc to the client.
 * Streaming is done via ResourceStreamers and resources are forced to be cached indefinitely using convenient response headers.
 */
public class ResourceServlet extends HttpServlet {

    private static final Log LOG = LogFactory.getLog(ResourceServlet.class);
    private static Map<String, String> mimeTypes;
    private List<ResourceStreamer> resourceStreamers;
    private boolean caching;
    private boolean exploded = false;
    private long oneYear = 31363200000L;

    /**
     * RESOURCE_FOLDER refers to the location in the classpath where resources are found.
     */
    public final static String RESOURCE_FOLDER = "/META-INF/resources";

    /**
     * RESOURCE_PATTERN is the string used in URLs requesting resources. This value is hardcoded for now - meaning
     * that all requests to internal betterFORM resources like CSS, images, scripts and XSLTs have to use
     * 'bfResources'.
     *
     * Example:
     * http://somehost.com/betterform/bfResources/images/image.gif"
     * will try to load an image 'image.gif' from /META-INF/resources/images/image.gif
     *
     */
    public final static String RESOURCE_PATTERN = "bfResources";
    private long lastModified = 0;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        if("false".equals(config.getInitParameter("caching"))){
            caching=false;
            if(LOG.isTraceEnabled()){
                LOG.trace("Caching of Resources is disabled");
            }
        }else {
            caching=true;

            if(LOG.isTraceEnabled()){
                LOG.trace("Caching of Resources is enabled - resources are loaded from classpath");
            }
        }
        this.lastModified = getLastModifiedValue();
        String path = null;
        try {
            path = WebFactory.getRealPath("WEB-INF/classes/META-INF/resources", config.getServletContext());
        } catch (XFormsConfigException e) {
            throw new ServletException(e);
        }
        if (path != null && new File(path).exists()) {
            exploded = true;
        }

        initMimeTypes();
        initResourceStreamers();
    }

    // todo: shouldn't we move these definitions to web.xml and use servletContext.getMimeType?
    private void initMimeTypes() {
        mimeTypes = new HashMap<String, String>();
        mimeTypes.put("css", "text/css");
        mimeTypes.put("js", "text/javascript");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("html", "text/html");
        mimeTypes.put("swf", "application/x-shockwave-flash");
        mimeTypes.put("xsl","application/xml+xslt");
    }

    private void initResourceStreamers() {
        resourceStreamers = new ArrayList<ResourceStreamer>();
        resourceStreamers.add(new DefaultResourceStreamer());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestUri = req.getRequestURI();
        String resourcePath = RESOURCE_FOLDER + getResourcePath(requestUri);
        URL url = ResourceServlet.class.getResource(resourcePath);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Request URI: " + requestUri);
            LOG.trace("resource fpath: " + resourcePath);
        }

        if (url == null) {
            boolean error = true;

            if (requestUri.endsWith(".js")) {
                //try optimized version first
                if (requestUri.contains("scripts/betterform/betterform-")) {
                    if (ResourceServlet.class.getResource(resourcePath) == null) {
                        resourcePath = resourcePath.replace("betterform-", "BfRequired");
                        if (ResourceServlet.class.getResource(resourcePath) != null) {
                            error = false;
                        }
                    }
                }
            }

            if (error) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Resource "+ resourcePath + " not found");
                }
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource " + resourcePath + " not found" );
                return;
            }

        }

        if (LOG.isTraceEnabled()){
            LOG.trace("Streaming resource " + resourcePath);
        }

        InputStream inputStream = null;

        try {
            if(exploded){
                String path = ResourceServlet.class.getResource(resourcePath).getPath();
                inputStream = new FileInputStream(new File(path));
                if(LOG.isTraceEnabled()){
                    LOG.trace("loading reources form file: " + path);
                }
            }else{
                inputStream = ResourceServlet.class.getResourceAsStream(resourcePath);
            }

            String mimeType = getResourceContentType(resourcePath);
            if(mimeType == null){
                mimeType = getServletContext().getMimeType(resourcePath);
            }

            if (mimeType == null) {
                if(LOG.isTraceEnabled()){
                    LOG.trace("MimeType for "+ resourcePath + " not found. Sending 'not found' response" );
                }
                resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "MimeType for " + resourcePath + " not found. Sending 'not found' response");
                return;
            }

            resp.setContentType(mimeType);
            resp.setStatus(HttpServletResponse.SC_OK);
            setCaching(req, resp);
            streamResource(req, resp, mimeType, inputStream);

            if(LOG.isTraceEnabled()){
                LOG.trace( "Resource "+ resourcePath + " streamed succesfully");
            }
        } catch (Exception exception) {
            LOG.error("Error in streaming resource "+ resourcePath + ". Exception is " + exception.getMessage());
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

            resp.getOutputStream().flush();
            resp.getOutputStream().close();
        }
    }

    private void streamResource(HttpServletRequest req, HttpServletResponse resp, String mimeType, InputStream inputStream) throws IOException {
        for (ResourceStreamer streamer : resourceStreamers) {
            if (streamer.isAppropriateStreamer(mimeType))
                streamer.stream(req, resp, inputStream);
        }
    }

    /**
     * set the caching headers for the resource response. Caching can be disabled by adding and init-param
     * of 'caching' with value 'false' to web.xml
     *
     * @param request  the http servlet request
     * @param response the http servlet response
     */
    protected void setCaching(HttpServletRequest request, HttpServletResponse response) {
        long now = System.currentTimeMillis();
        if (caching) {
            response.setHeader("Cache-Control", "max-age=3600, public");
            response.setDateHeader("Date", now);
            response.setDateHeader("Expires", now + this.oneYear);
            response.setDateHeader("Last-Modified", this.getLastModifiedValue());
        } else {
            // Set to expire far in the past.
            response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
            // Set standard HTTP/1.1 no-cache headers.
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
            response.addHeader("Cache-Control", "post-check=0, pre-check=0");
            // Set standard HTTP/1.0 no-cache header.
            response.setHeader("Pragma", "no-cache");
        }
    }

    protected String getResourcePath(String requestURI) {
        int jsessionidIndex = requestURI.toLowerCase().indexOf(";jsessionid");
        if (jsessionidIndex != -1) {
            requestURI = requestURI.substring(0, jsessionidIndex);
        }
        int patternIndex = requestURI.indexOf(RESOURCE_PATTERN);
        return requestURI.substring(patternIndex + RESOURCE_PATTERN.length(), requestURI.length());
    }

    protected String getResourceContentType(String resourcePath) {
        String resourceFileExtension = getResourceFileExtension(resourcePath);
        return mimeTypes.get(resourceFileExtension);
    }

    protected String getResourceFileExtension(String resourcePath) {
        String parsed[] = resourcePath.split("\\.");

        return parsed[parsed.length - 1];
    }

    private long getLastModifiedValue() {
        if(this.lastModified == 0){
            long bfTimestamp;
            try {
                String path = WebFactory.getRealPath("/WEB-INF/betterform-version.info", this.getServletContext());
                StringBuilder versionInfo = new StringBuilder();
                String NL = System.getProperty("line.separator");
                Scanner scanner = new Scanner(new FileInputStream(path), "UTF-8");
                try {
                    while (scanner.hasNextLine()){
                        versionInfo.append(scanner.nextLine() + NL);
                    }
                }
                finally{
                    scanner.close();
                }
                if(LOG.isDebugEnabled()){
                    LOG.debug("VersionInfo: " + versionInfo);
                }
                // String APP_NAME = APP_INFO.substring(0, APP_INFO.indexOf(" "));
                // String APP_VERSION = APP_INFO.substring(APP_INFO.indexOf(" ") + 1, APP_INFO.indexOf("-") - 1);
                String timestamp = versionInfo.substring(versionInfo.indexOf("Timestamp:")+10,versionInfo.length());
                String df = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(df);
                Date date = sdf.parse(timestamp);
                bfTimestamp = date.getTime();
            } catch (Exception e) {
                LOG.error("Error setting HTTP Header 'Last Modified', could not parse the given date.");
                bfTimestamp = 0;
            }
            this.lastModified = bfTimestamp;
        }
        return lastModified;
    }

    protected long getLastModified(HttpServletRequest req) {
        return this.getLastModifiedValue();
    }
}
