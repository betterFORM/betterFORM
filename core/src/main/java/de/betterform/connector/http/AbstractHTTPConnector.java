/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.http;

import de.betterform.xml.config.Config;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.cookie.CookieSpecBase;
import org.apache.commons.httpclient.cookie.MalformedCookieException;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.connector.AbstractConnector;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.exception.XFormsInternalSubmitException;
import de.betterform.xml.xforms.model.submission.RequestHeader;
import de.betterform.xml.xforms.model.submission.RequestHeaders;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A simple base class for convenient HTTP connector interface implementation.
 *
 * @author <a href="mailto:unl@users.sourceforge.net">uli</a>
 * @version $Id: AbstractHTTPConnector.java 3461 2008-08-14 09:32:50Z joern $
 */
public class AbstractHTTPConnector extends AbstractConnector {
    private static Log LOGGER = LogFactory.getLog(AbstractHTTPConnector.class);
    public static final String REQUEST_COOKIE = "request-cookie";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String SSL_PROTOCOL = "ssl-protocol";

    /**
     * The response body.
     */
    private InputStream responseBody = null;

    /**
     * The response header.
     */
    private Map responseHeader = null;
    public static final String HTTP_REQUEST_HEADERS = "http-headers";

    /**
     * Returns the response body.
     *
     * @return the response body.
     */
    protected InputStream getResponseBody() {
        return responseBody;
    }

    /**
     * Returns the response header.
     *
     * @return the response header.
     */
    protected Map getResponseHeader() {
        return responseHeader;
    }

    /**
     * @deprecated submissionMap is not used from local code any more.
     */
    private Map submissionMap = null;

    /**
     * allows to pass arbitrary params to an application by storing them in the submission map
     *
     * @param map
     * @deprecated submissionMap is not used from local code any more.
     */
    protected void setSubmissionMap(Map map) {
        this.submissionMap = map;
    }

    /**
     * Performs a HTTP GET request.
     *
     * @param uri the request uri.
     * @throws XFormsException if any error occurred during the request.
     */
    protected void get(String uri) throws XFormsException {
        HttpMethod httpMethod = new GetMethod(uri);
        try {
//            httpMethod.setRequestHeader(new Header("User-Agent", XFormsProcessorImpl.getAppInfo()));
            execute(httpMethod);

        } catch (XFormsException e) {
        	throw e;
        } catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    /**
     * Performs a HTTP POST request.
     * <p/>
     * The content type is <code>application/xml</code>.
     *
     * @param uri      the request uri.
     * @param body     the request body.
     * @param encoding the encoding being used.
     * @throws XFormsException if any error occurred during the request.
     */
    protected void post(String uri, String body, String encoding) throws XFormsException {
        post(uri, body, "application/xml", encoding);
    }

    /**
     * Performs a HTTP POST request.
     *
     * @param uri      the request uri.
     * @param body     the request body.
     * @param type     the content type.
     * @param encoding the encoding being used.
     * @throws XFormsException if any error occurred during the request.
     */
    protected void post(String uri, String body, String type, String encoding) throws XFormsException {
        EntityEnclosingMethod httpMethod = new PostMethod(uri);
        try {
            configureRequest(httpMethod, body, type, encoding);

            execute(httpMethod);

        } catch (XFormsException e) {
        	throw e;
        } catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    /**
     * Performs a HTTP PUT request.
     * <p/>
     * The content type is <code>application/xml</code>.
     *
     * @param uri      the request uri.
     * @param body     the request body.
     * @param encoding the encoding being used.
     * @throws XFormsException if any error occurred during the request.
     */
    protected void put(String uri, String body, String encoding) throws XFormsException {
        put(uri, body, "application/xml", encoding);
    }

    /**
     * Performs a HTTP PUT request.
     *
     * @param uri      the request uri.
     * @param body     the request body.
     * @param type     the content type.
     * @param encoding the encoding being used.
     * @throws XFormsException if any error occurred during the request.
     */
    protected void put(String uri, String body, String type, String encoding) throws XFormsException {
        EntityEnclosingMethod httpMethod = new PutMethod(uri);
        try {
            configureRequest(httpMethod, body, type, encoding);

            execute(httpMethod);
            
        } catch (XFormsException e) {
        	throw e;
        } catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    /**
     * Performs a HTTP PUT request.
     *
     * @param uri      the request uri.
     * @throws XFormsException if any error occurred during the request.
     */
    protected void delete(String uri) throws XFormsException {
    	DeleteMethod httpMethod = new DeleteMethod(uri);
    	try {
    		execute(httpMethod);
    		
    	} catch (XFormsException e) {
    		throw e;
    	} catch (Exception e) {
    		throw new XFormsException(e);
    	}
    }
    
    protected void execute(HttpMethod httpMethod) throws Exception{
        //		(new HttpClient()).executeMethod(httpMethod);
        HttpClient client = new HttpClient();



        if (! getContext().containsKey(SSL_PROTOCOL)) {
            String protocolPath = Config.getInstance().getProperty("httpclient.ssl.factory");
            if (protocolPath != null) {
                initSSLProtocol(protocolPath);
            }
        }
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("context params>>>");
            Map map = getContext();
            Iterator keys = map.keySet().iterator();
            while (keys.hasNext()) {
                String key =  keys.next().toString();
                Object value =  map.get(key);
                if(value != null)
                    LOGGER.debug(key + "=" + value.toString());
            }
            LOGGER.debug("<<<end params");
        }
        String username = null;
        String password = null;
        String realm = null;

        //add custom header to signal XFormsFilter to not process this internal request
        httpMethod.setRequestHeader("betterform-internal","true");

        /// *** copy all keys in map HTTP_REQUEST_HEADERS as http-submissionHeaders
        if (getContext().containsKey(HTTP_REQUEST_HEADERS)) {
            RequestHeaders httpRequestHeaders = (RequestHeaders) getContext().get(HTTP_REQUEST_HEADERS);

            // Iterator it =
            Map headersToAdd = new HashMap();
            for(RequestHeader header: httpRequestHeaders.getAllHeaders()){
                String headername = header.getName();
                String headervalue = header.getValue();

                if(headername.equals("username")){
                    username = headervalue;
                }else if(headername.equals("password")){
                    password = headervalue;
                }else if(headername.equals("realm")){
                    realm = headervalue;
                }else {
                    if (headersToAdd.containsKey(headername)) {
                        String formerValue = (String) headersToAdd.get(headername);
                        headersToAdd.put(headername, formerValue + "," + headervalue);
                    } else {
                        if (headername.equals("accept-encoding")) {
                            // do nothing
                            LOGGER.debug("do not add accept-encoding:" + headervalue + " for request");
                        } else {
                            headersToAdd.put(headername, headervalue);
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.debug("setting header: " + headername + " value: " + headervalue);
                            }
                        }
                    }
                }
            }
            Iterator keyIterator = headersToAdd.keySet().iterator();
            while(keyIterator.hasNext()){
                String key = (String) keyIterator.next();                 
                httpMethod.setRequestHeader(new Header(key,(String) headersToAdd.get(key)));
            }
        }
        if(username !=null && password!=null) {
            URI targetURI = null;
            try {
                targetURI = httpMethod.getURI();
                client.getParams().setAuthenticationPreemptive(true);
                Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
                if(realm == null){
                    realm = AuthScope.ANY_REALM;
                }
                client.getState().setCredentials(new AuthScope(targetURI.getHost(), targetURI.getPort(), realm), defaultcreds);
            }
            catch (URIException e) {
                throw new XFormsException(e);
            }
            httpMethod.setDoAuthentication(true);

        }
        //alternative method for non-tomcat servers
        if (getContext().containsKey(REQUEST_COOKIE)) {
            HttpState state = client.getState();
            state.setCookiePolicy(CookiePolicy.COMPATIBILITY);

            if (getContext().get(REQUEST_COOKIE) instanceof Cookie[]) {
                Cookie[] cookiesIn = (Cookie[]) getContext().get(REQUEST_COOKIE);
                if (cookiesIn[0] != null) {
                    for (int i = 0; i < cookiesIn.length; i++) {
                        Cookie cookie = cookiesIn[i];
                        state.addCookie(cookie);
                    }
                    Cookie[] cookies = state.getCookies();
                    Header cookieOut = new CookieSpecBase().formatCookieHeader(cookies);
                    httpMethod.setRequestHeader(cookieOut);
                    client.setState(state);
                }
            } else {
                throw new MalformedCookieException("Cookies must be passed as org.apache.commons.httpclient.Cookie objects.");
            }
        }

        try {
            if (getContext().containsKey(SSL_PROTOCOL)) {
                LOGGER.trace("Using customSSL-Protocol-Handler");
                HostConfiguration hc = new HostConfiguration();
                hc.setHost(httpMethod.getURI().getHost(), httpMethod.getURI().getPort(), (Protocol) getContext().get(SSL_PROTOCOL));
                client.executeMethod(hc, httpMethod);
            } else {
                client.executeMethod(httpMethod);
            }
            if (httpMethod.getStatusCode() >= 300) {
                // Allow 302 only
                if (httpMethod.getStatusCode() != 302) {
                    throw new XFormsInternalSubmitException(httpMethod.getStatusCode(), httpMethod.getStatusText(), httpMethod.getResponseBodyAsString(), XFormsConstants.RESOURCE_ERROR);
                }
            }
            this.handleHttpMethod(httpMethod);
        }
        catch (Exception e) {
            try {
                throw new XFormsInternalSubmitException(httpMethod.getStatusCode(), httpMethod.getStatusText(), httpMethod.getResponseBodyAsString(), XFormsConstants.RESOURCE_ERROR);
            } catch (IOException e1) {
                throw new XFormsInternalSubmitException(httpMethod.getStatusCode(), httpMethod.getStatusText(), XFormsConstants.RESOURCE_ERROR); 
            }
        }

    }

    protected void handleHttpMethod(HttpMethod httpMethod) throws Exception {
        Header[] responseHeaders = httpMethod.getResponseHeaders();
        this.responseHeader = new HashMap();

        for (int index = 0; index < responseHeaders.length; index++) {
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("response header :: " + responseHeaders[index].getName() + " : value = " + responseHeaders[index].getValue());
            }
            responseHeader.put(responseHeaders[index].getName(), responseHeaders[index].getValue());
        }

        this.responseBody = httpMethod.getResponseBodyAsStream();

    }

    private void configureRequest(EntityEnclosingMethod httpMethod, String body, String type, String encoding) throws UnsupportedEncodingException {
        StringRequestEntity entity = new StringRequestEntity(body, type, encoding);
        httpMethod.setRequestEntity(entity);
        httpMethod.setRequestHeader(new Header("Content-Length", String.valueOf(body.getBytes(encoding).length)));
    }

    private void initSSLProtocol(String protocolPath) throws Exception {
        Protocol sslProtocol;
            LOGGER.trace("creating sslProtocol ...");
            LOGGER.trace("ProtocolPath: " + protocolPath);
            Class sslClass = Class.forName(protocolPath);
            Object sslObject = sslClass.newInstance();
            if (sslObject instanceof SecureProtocolSocketFactory) {
                int defaultPort;
                if (Config.getInstance().getProperty("httpclient.ssl.factory.defaultPort") != null) {
                    try {
                        defaultPort = Integer.parseInt(Config.getInstance().getProperty("httpclient.ssl.factory.defaultPort"));
                    } catch (NumberFormatException nfe) {
                        LOGGER.warn("httpclient.ssl.factory.defaultPort ist not parsable as number check your setting in betterform-config.xml!", nfe);
                        LOGGER.warn("Setting sslPort to 443");
                        //throw new XFormsConfigException(httpclient.ssl.factory.defaultPort ist not parseable as number check your setting in betterform-config.xml!", nfe);
                        defaultPort = 443;
                    }
                } else {
                    defaultPort = 443;
                }
                LOGGER.trace("DefaultPort: " + defaultPort);
                sslProtocol = new Protocol("https", (ProtocolSocketFactory) sslObject, defaultPort);
                Protocol.registerProtocol("https", sslProtocol);

                getContext().put(SSL_PROTOCOL, sslProtocol);
            }
    }
}

//end of class
