/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web;

import de.betterform.xml.xforms.ui.Filename;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.connector.http.AbstractHTTPConnector;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.model.submission.RequestHeaders;
import de.betterform.xml.xslt.TransformerService;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Collection of static methods to be re-used by different Servlets/Filter implementations.
 *
 * @author Joern Turner
 */
public class WebUtil {
    private static final Log LOGGER = LogFactory.getLog(WebUtil.class);
    public static final String HTML_CONTENT_TYPE = "text/html;charset=UTF-8";
    public static final String HTTP_SESSION_ID = "httpSessionId";
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
            LOGGER.debug("--------------- session dump ---------------");
            Enumeration keys = session.getAttributeNames();
            if (keys.hasMoreElements()) {
                LOGGER.debug("--- existing keys in session --- ");
                while (keys.hasMoreElements()) {
                    String s = (String) keys.nextElement();
                    LOGGER.debug("existing sessionkey: " + s + ":" + session.getAttribute(s));
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
    public static WebProcessor getWebProcessor(HttpServletRequest request) {

//        String key = request.getParameter("sessionKey");
//        XFormsSessionManager manager = (XFormsSessionManager) session.getAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER);
//        XFormsSession xFormsSession = manager.getWebProcessor(key);
        String key = request.getParameter("sessionKey");
        if (key == null) {
            LOGGER.warn("Request " + request + " has no parameter session key");
            return null;
        } else {
            return getWebProcessor(key);
        }
    }

    /**
     * fetches the XFormsSession from the HTTP Session
     *
     * @param request the HTTP Request
     * @param session the HTTP Session
     * @return the xformsSession for the request
     */
    public static WebProcessor getWebProcessor(HttpServletRequest request, HttpSession session) {
//        String key = request.getParameter("sessionKey");
//        XFormsSessionManager manager = (XFormsSessionManager) session.getAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER);
//        XFormsSession xFormsSession = manager.getWebProcessor(key);
        return getWebProcessor(request);
    }

    public static WebProcessor getWebProcessor(String key) {
        if (key == null || key.equals("")) {
            LOGGER.warn("SessionKey is null");
            return null;
        }

        Cache cache = CacheManager.getInstance().getCache("xfSessionCache");
        if(cache == null || cache.get(key) == null) {
            LOGGER.warn("No xformsSession for key " + key + " in Cache");
            return null;
        }

        net.sf.ehcache.Element elem = cache.get(key);
        WebProcessor webProcessor = (WebProcessor) elem.getObjectValue();
        if (webProcessor == null) {
            LOGGER.warn("Cached WebProcessor for key '" + key + "' is null");
            return null;
        }
//        XStream xStream = new XStream();
//        String xml = xStream.toXML(webProcessor);
//        LOGGER.debug(xml);

        return webProcessor;
    }

    public static boolean removeSession(String key) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("removing key: '" + key + "' from cache");
        }
        Cache xfSessionCache = CacheManager.getInstance().getCache("xfSessionCache");
        boolean removedSession = false;
        if (xfSessionCache != null) {
            removedSession = xfSessionCache.remove(key);
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
    public static void storeCookies(HttpServletRequest request, XFormsProcessor processor) {
        javax.servlet.http.Cookie[] cookiesIn = request.getCookies();
        if (cookiesIn != null) {
            BasicClientCookie[] commonsCookies = new BasicClientCookie[cookiesIn.length];
            for (int i = 0; i < cookiesIn.length; i += 1) {
                javax.servlet.http.Cookie c = cookiesIn[i];
                commonsCookies[i] = new BasicClientCookie(c.getName(),c.getValue());
                commonsCookies[i].setDomain(c.getDomain());
                commonsCookies[i].setPath(c.getPath());
                commonsCookies[i].setAttribute(ClientCookie.MAX_AGE_ATTR, Integer.toString(c.getMaxAge()));
                commonsCookies[i].setSecure(c.getSecure());

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
            processor.setContextParam(AbstractHTTPConnector.REQUEST_COOKIE, commonsCookies);
        }
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
            String sessionkey) {
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

        //adding pathInfo to context - attention: this is only available when a servlet is requested
        String s1=request.getPathInfo();
        if(s1!=null){
            processor.setContextParam(WebProcessor.PATH_INFO, s1);
        }
        processor.setContextParam(WebProcessor.QUERY_STRING, (request.getQueryString() != null ? request.getQueryString() : ""));

        //storing the realpath for webapp
        String realPath = httpSession.getServletContext().getRealPath("");
        if (realPath == null) {
            realPath = httpSession.getServletContext().getRealPath(".");
        }
        File f = new File(realPath);
        URI fileURI = null;
        fileURI = f.toURI();

        processor.setContextParam(WebProcessor.REALPATH, fileURI.toString());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("real path of webapp: " + realPath);
        }

        //storing the TransformerService
        processor.setContextParam(TransformerService.TRANSFORMER_SERVICE, httpSession.getServletContext().getAttribute(TransformerService.class.getName()));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("TransformerService: " + httpSession.getServletContext().getAttribute(TransformerService.class.getName()));
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

    public static boolean isMediaTypeXML(String s) {
        boolean isXML;
        isXML = s.endsWith("+xml")
		|| ((s.equals("text")
		|| s.equals("application"))
		&& (s.equals("xml")
		|| s.equals("xml-external-parsed-entity")));

        return isXML;
    }

}
