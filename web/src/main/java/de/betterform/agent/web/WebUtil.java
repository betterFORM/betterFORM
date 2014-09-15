/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web;

import de.betterform.agent.web.cache.XFSessionCache;
import de.betterform.agent.web.flux.FluxProcessor;
import de.betterform.connector.http.AbstractHTTPConnector;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.RequestHeaders;
import de.betterform.xml.xslt.TransformerService;
import de.betterform.xml.xslt.impl.CachingTransformerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

/**
 * Collection of static methods to be re-used by different Servlets/Filter implementations.
 *
 * @author Joern Turner
 */
public class WebUtil {
    private static final Log LOGGER = LogFactory.getLog(WebUtil.class);
    public static final String HTML_CONTENT_TYPE = "text/html;charset=UTF-8";
    public static final String HTTP_SESSION_ID = "httpSessionId";
    public static final String EXISTDB_USER = "_eXist_xmldb_user";
    private static final String FILENAME = "fileName";
    private static final String PLAIN_PATH = "plainPath";
    private static final String CONTEXT_PATH = "contextPath";

    public static String getRequestURI(HttpServletRequest request) {
        StringBuffer buffer = new StringBuffer(request.getScheme());
        buffer.append("://");
        buffer.append(request.getServerName());
        buffer.append(":");
        buffer.append(request.getServerPort());
        buffer.append(request.getContextPath());
        return buffer.toString();
    }

    public static void printSessionKeys(HttpSession session) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("--------------- http session dump ---------------");
            Enumeration keys = session.getAttributeNames();
            if (keys.hasMoreElements()) {
                while (keys.hasMoreElements()) {
                    String s = (String) keys.nextElement();
                    LOGGER.debug("sessionkey: " + s + ":" + session.getAttribute(s));
                }
            } else {
                LOGGER.debug("--- no keys present in session ---");
            }
        }
    }

    public static void nonCachingResponse(HttpServletResponse response) {
        response.setHeader("Cache-Control", "private, no-store,  no-cache, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "-1");
    }

     /**
     *
     * @return String for use as root of URLs
     */
    public static String getContextRoot(HttpServletRequest request) {
        if (request.getAttribute(WebProcessor.ALTERNATIVE_ROOT) != null) {
            return (String) request.getAttribute(WebProcessor.ALTERNATIVE_ROOT);
        } else {
            return request.getContextPath();
        }
    }

    /**
     * fetches the XFormsSession from the HTTP Session
     *
     * @param request the HTTP Request
     * @return the xformsSession for the request
     */
    public static WebProcessor getWebProcessor(HttpServletRequest request, HttpServletResponse response, HttpSession session) {

//        String key = request.getParameter("sessionKey");
//        XFormsSessionManager manager = (XFormsSessionManager) session.getAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER);
//        XFormsSession xFormsSession = manager.getWebProcessor(key);
        String key = request.getParameter("sessionKey");
        if (key == null) {
            LOGGER.warn("Request " + request + " has no parameter session key");
            return null;
        } else {
            return getWebProcessor(key, request, response, session);
        }
    }

    public static WebProcessor getWebProcessor(String key,
                                               HttpServletRequest request,
                                               HttpServletResponse response,
                                               HttpSession session) {
        if (key == null || key.equals("")) {
            LOGGER.warn("SessionKey is null");
            return null;
        }

        org.infinispan.Cache<String, FluxProcessor> sessionCache;
        try {
            sessionCache = XFSessionCache.getCache();
        } catch (XFormsException xfe) {
              sessionCache = null;
        }

        if(sessionCache == null || !(sessionCache.containsKey(key)) ) {
            LOGGER.warn("No xformsSession for key " + key + " in Cache");
            return null;
        }
        WebProcessor processor  = sessionCache.get(key);

        if(processor.getContext()  != null){
            return  processor;
        } else{
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("Element is read from disk " +  processor.toString());
            }
            //re-initialize transient state
            processor.setRequest(request);
            processor.setResponse(response);
            processor.setHttpSession(session);
            processor.setKey(key);
            processor.getHttpRequestHandler();
            processor.setContext(session.getServletContext());

            try {
                processor.configure();
                processor.createUIGenerator();
                processor.init();
                return processor;
            } catch (Exception e) {
                LOGGER.error("Could not reload xformSession from disk.", e);
            }
        }

        return null;

    }

    /**
     * remove session with given key from infinispan cache
     *
     * @param key the entry identifier
     * @return true if session existed and could be removed
     */
    public static boolean removeSession(String key) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removing key: '" + key + "' from cache");
        }
        boolean removedSession = false;

        try {
            org.infinispan.Cache<String, FluxProcessor> sessionCache = XFSessionCache.getCache();
            if (sessionCache != null) {
                //TODO: rethink ...
                removedSession = (sessionCache.remove(key)   != null);
            }
        } catch (XFormsException xfe) {  }

        if (LOGGER.isDebugEnabled()) {
            String result = removedSession ? "successful":"unsuccessful";
            LOGGER.debug("Removal of session '" + key + "' was " + result);
        }

        return removedSession;
    }

    public static String decodeUrl(String formPath, HttpServletRequest request) throws UnsupportedEncodingException {
        String decoded = URLDecoder.decode(formPath, "UTF-8");
        if (decoded.startsWith("http://")) {
            return decoded;
        } else {
            String requestURI = getRequestURI(request);
            return requestURI + formPath;
        }
    }

    /**
     * stores cookies that may exist in request and passes them on to processor for usage in
     * HTTPConnectors. Instance loading and submission then uses these cookies. Important for
     * applications using auth.
     * <p/>
     * NOTE: this method should be called *before* the Adapter is initialized cause the cookies may
     * already be needed for setup (e.g. loading of XForms via Http)
     */
    public static void storeCookies(List<Cookie> requestCookies,  XFormsProcessor processor) {
        Vector<BasicClientCookie> commonsCookies = new Vector<BasicClientCookie>();

        if (requestCookies != null && requestCookies.size() > 0) {
            commonsCookies = saveAsBasicClientCookie(requestCookies.iterator(), commonsCookies );
        }

        /*
        if (responseCookies != null && responseCookies.size() > 0) {
            commonsCookies= saveAsBasicClientCookie(responseCookies.iterator(), commonsCookies);
        }
        */
        if(commonsCookies.size() == 0) {
            BasicClientCookie sessionCookie = new BasicClientCookie("JSESSIONID", ((WebProcessor)processor).httpSession.getId());
            sessionCookie.setSecure(false);
            sessionCookie.setDomain(null);
            sessionCookie.setPath(null);

            commonsCookies.add(sessionCookie);

        }
        processor.setContextParam(AbstractHTTPConnector.REQUEST_COOKIE, commonsCookies.toArray(new BasicClientCookie[0]));
    }


    private static Vector<BasicClientCookie> saveAsBasicClientCookie(Iterator iterator, Vector<BasicClientCookie> commonsCookies){
        while(iterator.hasNext()) {
            javax.servlet.http.Cookie c = (Cookie)iterator.next();
            BasicClientCookie commonsCookie = new BasicClientCookie(c.getName(),c.getValue());
            commonsCookie.setDomain(c.getDomain());
            commonsCookie.setPath(c.getPath());
            commonsCookie.setAttribute(ClientCookie.MAX_AGE_ATTR, Integer.toString(c.getMaxAge()));
            commonsCookie.setSecure(c.getSecure());

            commonsCookies.add(commonsCookie);

                if (WebUtil.LOGGER.isDebugEnabled()) {
                    WebUtil.LOGGER.debug("adding cookie >>>>>");
                    WebUtil.LOGGER.debug("name: " + c.getName());
                    WebUtil.LOGGER.debug("value: " + c.getValue());
                    WebUtil.LOGGER.debug("path: " + c.getPath());
                    WebUtil.LOGGER.debug("maxAge: " + c.getMaxAge());
                    WebUtil.LOGGER.debug("secure: " + c.getSecure());
                    WebUtil.LOGGER.debug("adding cookie done <<<<<");
                }
            }

        return commonsCookies;
        }




    /**
     * copy all http headers from client request into betterForm context map as map HTTP_HEADERS. This map
     * will be picked up in AbstractHttpConnector to configure a proper request for the internal http-client.
     */
    public static void copyHttpHeaders(HttpServletRequest request, XFormsProcessor processor) {
        RequestHeaders httpHeaders = new RequestHeaders();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            httpHeaders.addHeader(headerName, request.getHeader(headerName));
            if (WebUtil.LOGGER.isDebugEnabled()) {
                WebUtil.LOGGER.debug("keeping httpheader: " + headerName + " value: " + request.getHeader(headerName));
            }

        }
        processor.setContextParam(AbstractHTTPConnector.HTTP_REQUEST_HEADERS, httpHeaders);
    }

    /**
     * returns an URL which is passed as request param and decodes it if necessary
     *
     * @param request the HttpServletRequest
     * @return an unencoded absolute Url or a relative Url
     * @throws java.io.UnsupportedEncodingException
     *                                        in case URL is not correctly encoded
     * @throws java.net.MalformedURLException in case URL is not valid
     */
    public static String getFormUrlAsString(HttpServletRequest request) throws UnsupportedEncodingException, MalformedURLException {
        String formPath = request.getParameter(WebFactory.FORM_PARAM_NAME);
        return decodeUrl(formPath, request);
    }

    /**
     * this method is responsible for passing all context information needed by the Adapter and Processor from
     * ServletRequest to Context. Will be called only once when the form-session is inited (GET).
     * <p/>
     * <p/>
     * todo: better logging of context params
     *
     * @param request     the Servlet request to fetch params from
     * @param httpSession the Http Session context
     * @param processor   the XFormsProcessor which receives the context params
     * @param sessionkey  the key to identify the XFormsSession
     */
    public static void setContextParams(HttpServletRequest request,
            HttpSession httpSession,
            XFormsProcessor processor,
            String sessionkey) throws XFormsConfigException {
        Map servletMap = new HashMap();
        servletMap.put(WebProcessor.SESSION_ID, sessionkey);
        processor.setContextParam(XFormsProcessor.SUBMISSION_RESPONSE, servletMap);

        //adding requestURI to context
        processor.setContextParam(WebProcessor.REQUEST_URI, WebUtil.getRequestURI(request));

        //adding request URL to context
        String requestURL = request.getRequestURL().toString();
        processor.setContextParam(WebProcessor.REQUEST_URL, requestURL);

        // the web app name with an '/' prepended e.g. '/betterform' by default
        String contextRoot = WebUtil.getContextRoot(request);
        processor.setContextParam(WebProcessor.CONTEXTROOT, contextRoot);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("context root of webapp: " + processor.getContextParam(WebProcessor.CONTEXTROOT));
        }

        String requestPath = "";
        URL url=null;
        String plainPath ="";
        try {
            url = new URL(requestURL);
            requestPath = url.getPath();
        } catch (MalformedURLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if(requestPath.length() != 0){
            //adding request path e.g. '/betterform/forms/demo/registration.xhtml'
            processor.setContextParam(WebProcessor.REQUEST_PATH, requestPath);

            //adding filename of requested doc to context
            String fileName = requestPath.substring(requestPath.lastIndexOf('/')+1,requestPath.length());//FILENAME xforms
            processor.setContextParam(FILENAME, fileName);

            if(requestURL.contains(contextRoot)){ //case1: contextRoot is a part of the URL
	            //adding plainPath which is the part between contextroot and filename e.g. '/forms' for a requestPath of '/betterform/forms/Status.xhtml'
	            plainPath = requestPath.substring(contextRoot.length()+1,requestPath.length() - fileName.length());
	            processor.setContextParam(PLAIN_PATH, plainPath);
            }
            else{//case2: contextRoot is not a part of the URL take the part previous the filename.
            	String[] urlParts=requestURL.split("/");
            	plainPath=urlParts[urlParts.length-2];
            }

            //adding contextPath - requestPath without the filename
            processor.setContextParam(CONTEXT_PATH,contextRoot+"/"+plainPath);
         }


        //adding session id to context
        processor.setContextParam(HTTP_SESSION_ID, httpSession.getId());
        //adding context absolute path to context

        //EXIST-WORKAROUND: TODO triple check ...
        //TODO: triple check where this is used.
        if (request.isRequestedSessionIdValid()) {
            processor.setContextParam(EXISTDB_USER, httpSession.getAttribute(EXISTDB_USER));
        }

        //adding pathInfo to context - attention: this is only available when a servlet is requested
        String s1=request.getPathInfo();
        if(s1!=null){
            processor.setContextParam(WebProcessor.PATH_INFO, s1);
        }
        processor.setContextParam(WebProcessor.QUERY_STRING, (request.getQueryString() != null ? request.getQueryString() : ""));

        //storing the realpath for webapp

        String realPath = WebFactory.getRealPath(".", httpSession.getServletContext());
        File f = new File(realPath);
        URI fileURI = f.toURI();

        processor.setContextParam(WebProcessor.REALPATH, fileURI.toString());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("real path of webapp: " + realPath);
        }

        //storing the TransformerService
        processor.setContextParam(TransformerService.TRANSFORMER_SERVICE, httpSession.getServletContext().getAttribute(TransformerService.TRANSFORMER_SERVICE));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("TransformerService: " + httpSession.getServletContext().getAttribute(TransformerService.TRANSFORMER_SERVICE));
        }

        //[2] read any request params that are *not* betterForm params and pass them into the context map
        Enumeration params = request.getParameterNames();
        String s;
        while (params.hasMoreElements()) {
            s = (String) params.nextElement();
            //store all request-params we don't use in the context map of XFormsProcessorImpl
            String value = request.getParameter(s);
            processor.setContextParam(s, value);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("added request param '" + s + "' added to context");
                LOGGER.debug("param value'" + value);
            }
        }

    }

    public static boolean isMediaTypeXML(String mediatype) {
        boolean isXML;
        isXML = mediatype.startsWith("text") || mediatype.startsWith("application");
        if (isXML) {
            isXML = isXML && (mediatype.contains("xml") || mediatype.contains("xhtml+xml"));
        }

        return isXML;
    }

    /**
     * transforms an input document and writes it to the ServletOutputStream.
     *
     * @param context the servlet context
     * @param response the servlet response
     * @param input an DOM input document to transform
     * @param stylesheetName the name of the stylesheet to use. This must be preloaded in CachingTransformerService. See WebFactory
     * @param params transformation parameters as a piece of DOM if any. The params object is passed as param 'params' to the stylesheets
     * @throws java.io.IOException
     */
    public static void doTransform(ServletContext context,
                                   HttpServletResponse response,
                                   Document input,
                                   String stylesheetName,
                                   Object params) throws IOException {
        CachingTransformerService transformerService  = (CachingTransformerService) context.getAttribute(TransformerService.TRANSFORMER_SERVICE);
        Source xmlSource =  new DOMSource(input);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Transformer transformer = transformerService.getTransformerByName(stylesheetName);
            if(params != null){
                if(params instanceof Node){
                    transformer.setParameter("params",params);
                }
            }
            transformer.transform(xmlSource, new StreamResult(outputStream));

        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        response.setContentType(WebUtil.HTML_CONTENT_TYPE);
        response.setContentLength(outputStream.toByteArray().length);
        response.getOutputStream().write(outputStream.toByteArray());
        response.getOutputStream().close();
    }


}
