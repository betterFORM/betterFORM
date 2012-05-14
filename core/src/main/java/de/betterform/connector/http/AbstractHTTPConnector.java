/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.http;

import de.betterform.connector.AbstractConnector;
import de.betterform.connector.ConnectorFactory;
import de.betterform.connector.http.ssl.KeyStoreSSLContext;
import de.betterform.xml.config.Config;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.exception.XFormsInternalSubmitException;
import de.betterform.xml.xforms.model.submission.RequestHeader;
import de.betterform.xml.xforms.model.submission.RequestHeaders;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.*;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;
import java.util.zip.GZIPInputStream;

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

    protected int statusCode = 0;
    protected String reasonPhrase = "";

    /*
     * Custom-SSL:
     * Key for storing custom SSL-protocol
     */
    public static final String SSL_CUSTOM_SCHEME = "ssl_custom_scheme";

    /*
     * Custom-SSL:
     * SSL-factory properties, see betterform-config.xml for description.
     */
    public static final String HTTPCLIENT_SSL_CONTEXT= "httpclient.ssl.context";
    public static final String HTTPCLIENT_SSL_CONTEXT_CUSTOMPORT= "httpclient.ssl.context.customPort";
    public static final String HTTPCLIENT_SSL_KEYSTORE_PATH= "httpclient.ssl.keystore.path";
    public static final String HTTPCLIENT_SSL_KEYSTORE_PASSWD= "httpclient.ssl.keystore.passwd";

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
        //HttpMethod httpMethod = new GetMethod(uri);
        HttpRequestBase httpRequestBase = new HttpGet(uri);
        try {
//            httpMethod.setRequestHeader(new Header("User-Agent", XFormsProcessorImpl.getAppInfo()));
            //execute(httpMethod);
            execute(httpRequestBase);

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
        HttpEntityEnclosingRequestBase httpMethod = new HttpPost(uri);
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
        HttpEntityEnclosingRequestBase httpMethod = new HttpPut(uri);
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
    	HttpRequestBase httpMethod = new HttpDelete(uri);
    	try {
    		execute(httpMethod);

    	} catch (XFormsException e) {
    		throw e;
    	} catch (Exception e) {
    		throw new XFormsException(e);
    	}
    }

    //protected void execute(HttpMethod httpMethod) throws Exception{
    protected void execute(HttpRequestBase httpRequestBase) throws Exception{
        //		(new HttpClient()).executeMethod(httpMethod);
        //HttpClient client = new HttpClient();
         HttpParams httpParams = new BasicHttpParams();


         DefaultHttpClient client = ConnectorFactory.getFactory().getHttpClient(httpParams);



        if (! getContext().containsKey(AbstractHTTPConnector.SSL_CUSTOM_SCHEME)) {
            LOGGER.debug("SSL_CUSTOM_SCHEME");
            LOGGER.debug("SSL_CUSTOM_SCHEME: Factory: " + Config.getInstance().getProperty(AbstractHTTPConnector.HTTPCLIENT_SSL_CONTEXT));
            String contextPath = Config.getInstance().getProperty(AbstractHTTPConnector.HTTPCLIENT_SSL_CONTEXT);
            if (contextPath != null) {
                initSSLScheme(contextPath);
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
        //httpMethod.setRequestHeader("betterform-internal","true");
        httpRequestBase.addHeader("betterform-internal", "true");

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
                //httpMethod.setRequestHeader(new Header(key,(String) headersToAdd.get(key)));
                httpRequestBase.setHeader(key, (String) headersToAdd.get(key));
                //httpRequestBase.addHeader(key, (String) headersToAdd.get(key));
            }
        }
        if (httpRequestBase.containsHeader("Content-Length")) {
            //remove content-length if present httpclient will recalucalte the value.
            httpRequestBase.removeHeaders("Content-Length");
        }
        if(username !=null && password!=null) {
            URI targetURI = null;
                //targetURI = httpMethod.getURI();
                targetURI = httpRequestBase.getURI();
                //client.getParams().setAuthenticationPreemptive(true);

                Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
                if(realm == null){
                    realm = AuthScope.ANY_REALM;
                }
               //client.getState().setCredentials(new AuthScope(targetURI.getHost(), targetURI.getPort(), realm), defaultcreds);
               client.getCredentialsProvider().setCredentials(new AuthScope(targetURI.getHost(), targetURI.getPort(), realm), defaultcreds);
               AuthCache authCache = new BasicAuthCache();
               BasicScheme basicAuth = new BasicScheme();

                authCache.put(new HttpHost(targetURI.getHost()), basicAuth);
                BasicHttpContext localContext = new BasicHttpContext();
                localContext.setAttribute(ClientContext.AUTH_CACHE, authCache);




            //Needed? httpMethod.setDoAuthentication(true);


        }
        //alternative method for non-tomcat servers
        if (getContext().containsKey(REQUEST_COOKIE)) {
            //HttpState state = client.getState();
            HttpParams state = client.getParams();

            //state.setCookiePolicy(CookiePolicy.COMPATIBILITY);
            state.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);

            if (getContext().get(REQUEST_COOKIE) instanceof Cookie[]) {
                Cookie[] cookiesIn = (Cookie[]) getContext().get(REQUEST_COOKIE);
                if (cookiesIn[0] != null) {
                    for (int i = 0; i < cookiesIn.length; i++) {
                        Cookie cookie = cookiesIn[i];
                        //state.addCookie(cookie);
                        client.getCookieStore().addCookie(cookie);
                    }
                    /*
                          Cookie[] cookies = state.getCookies();

                    Header cookieOut = new CookieSpecBase().formatCookieHeader(cookies);
                    httpMethod.setRequestHeader(cookieOut);
                    client.setState(state);
                          */
                    List<Cookie> cookies = client.getCookieStore().getCookies();
                    List<Header> cookieHeaders= new BrowserCompatSpec().formatCookies(cookies);
                    Header[] headers = cookieHeaders.toArray(new Header[0]);

                    for (int i = 0; i < headers.length; i++) {
                        httpRequestBase.addHeader(headers[i]);
                    }

                    client.setParams(state);
                }
            } else {
                throw new MalformedCookieException("Cookies must be passed as org.apache.commons.httpclient.Cookie objects.");
            }
        }

        if (getContext().containsKey(AbstractHTTPConnector.SSL_CUSTOM_SCHEME)) {
            LOGGER.debug("Using customSSL-Protocol-Handler");
            /*
                Iterator<Scheme> schemes = ((Vector<Scheme>) ).iterator();

                while (schemes.hasNext()) {
                    client.getConnectionManager().getSchemeRegistry().register(schemes.next());
                }
             */

            Scheme sslScheme = (Scheme) getContext().get(AbstractHTTPConnector.SSL_CUSTOM_SCHEME);
            client.getConnectionManager().getSchemeRegistry().register(sslScheme);
        }

        if(httpRequestBase.getURI().isAbsolute()){
            httpRequestBase.setHeader("host", httpRequestBase.getURI().getHost());
        }

        /*
            //TODO: handle more than one scheme, could look something like this:
            if (getContext().containsKey(AbstractHTTPConnector.SSL_CUSTOM_SCHEME)) {
                LOGGER.debug("Using customSSL-Protocol-Handler");
                Iterator<Scheme> schemes = ((Vector<Scheme>) getContext().get(AbstractHTTPConnector.SSL_CUSTOM_SCHEME)).iterator();
                boolean proceed = true;

                while (schemes.hasNext() && proceed) {
                    client.getConnectionManager().getSchemeRegistry().register(schemes.next());
                    HttpResponse httpResponse = client.execute(httpRequestBase);

                    statusCode = httpResponse.getStatusLine().getStatusCode();
                    reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();

                    if (statusCode != 404) {
                        proceed = false;
                    }
                }
            }

            try {
                if ( statusCode >= 300) {
                    // Allow 302 only
                    if (statusCode != 302) {
                        throw new XFormsInternalSubmitException(statusCode, reasonPhrase, EntityUtils.toString(httpResponse.getEntity()), XFormsConstants.RESOURCE_ERROR);
                    }
                }
                this.handleHttpMethod(httpResponse);
            }
            catch (Exception e) {

                LOGGER.trace("AbstractHTTPConnector Exception: ", e);
                try {
                    throw new XFormsInternalSubmitException(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase(), EntityUtils.toString(httpResponse.getEntity()), XFormsConstants.RESOURCE_ERROR);
                } catch (IOException e1) {
                    throw new XFormsInternalSubmitException(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase(), XFormsConstants.RESOURCE_ERROR);
                }
            }
         */

        HttpResponse httpResponse = client.execute(httpRequestBase);
        statusCode = httpResponse.getStatusLine().getStatusCode();
        reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
        try {
            if ( statusCode >= 300) {
                // Allow 302 only
                if (statusCode != 302) {
                    throw new XFormsInternalSubmitException(statusCode, reasonPhrase, EntityUtils.toString(httpResponse.getEntity()), XFormsConstants.RESOURCE_ERROR);
                }
            }
            this.handleHttpMethod(httpResponse);
        }
        catch (Exception e) {

            LOGGER.trace("AbstractHTTPConnector Exception: ", e);
            try {
                throw new XFormsInternalSubmitException(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase(), EntityUtils.toString(httpResponse.getEntity()), XFormsConstants.RESOURCE_ERROR);
            } catch (IOException e1) {
                throw new XFormsInternalSubmitException(httpResponse.getStatusLine().getStatusCode(), httpResponse.getStatusLine().getReasonPhrase(), XFormsConstants.RESOURCE_ERROR);
            }
        }

    }

    protected void handleHttpMethod(HttpResponse httpResponse) throws Exception {
        Header[] responseHeaders = httpResponse.getAllHeaders();
        this.responseHeader = new HashMap();

        for (int index = 0; index < responseHeaders.length; index++) {
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("response header :: " + responseHeaders[index].getName() + " : value = " + responseHeaders[index].getValue());
            }
            responseHeader.put(responseHeaders[index].getName(), responseHeaders[index].getValue());
        }
        if(httpResponse.getEntity() != null) {
            if (responseHeader.containsKey("Content-Encoding") && ((String) responseHeader.get("Content-Encoding")).equalsIgnoreCase("gzip") ) {
                this.responseBody =  new GZIPInputStream(httpResponse.getEntity().getContent());
            } else {
                this.responseBody = httpResponse.getEntity().getContent();
            }
        }

    }

    private void configureRequest(HttpEntityEnclosingRequestBase httpMethod, String body, String type, String encoding) throws UnsupportedEncodingException {
        HttpEntity entity = new StringEntity(body, type, encoding);
        httpMethod.setEntity(entity);
        //httpMethod.setHeader(new BasicHeader("Content-Length", String.valueOf(body.getBytes(encoding).length)));
    }


    private void initSSLScheme(String contextPath) throws Exception {
            LOGGER.debug("creating sslScheme ...");
            LOGGER.debug("KeyStoreSSLContext: " + contextPath);
            Class contextClass = Class.forName(contextPath);
            Object context = contextClass.newInstance();
            /* Multiple Schemes: This does not work!
                Vector<Scheme> schemes = new Vector<Scheme>();
            */
            Scheme sslScheme = null;

            if (context instanceof KeyStoreSSLContext) {
                final int httpSSLPort = 443;
                /* Multiple Schemes: This does not work!
                    int tomcatSSLPort = 8443;
                */

                SSLSocketFactory socketFactory = new SSLSocketFactory(((KeyStoreSSLContext)context).getSSLContext());
                if (Config.getInstance().getProperty(AbstractHTTPConnector.HTTPCLIENT_SSL_CONTEXT_CUSTOMPORT) != null) {
                    try {
                        int customPort = Integer.parseInt(Config.getInstance().getProperty(AbstractHTTPConnector.HTTPCLIENT_SSL_CONTEXT_CUSTOMPORT));
                        LOGGER.trace("CustomPort: " + customPort);
                        sslScheme = new Scheme("https", customPort, socketFactory);
                        /* Multiple Schemes: This does not work!
                            Scheme sslScheme = new Scheme("https", customPort, socketFactory);
                            schemes.add(sslScheme);
                        */
                    } catch (NumberFormatException nfe) {
                        LOGGER.warn("AbstractHTTPConnector: " + AbstractHTTPConnector.HTTPCLIENT_SSL_CONTEXT_CUSTOMPORT + " is not parsable as a number. Check your settings in betterform-config.xml!", nfe);
                        LOGGER.warn("AbstractHTTPConnector: Falling back to 443 as default.");

                        /* Multiple Schemes: This does not work!
                            Scheme sslScheme = new Scheme("https", httpSSLPort, socketFactory);
                            schemes.add(sslScheme);
                        */
                    }
                }

                /* Multiple Schemes: This does not work!
                    Scheme sslScheme2 = new Scheme("https", tomcatSSLPort, socketFactory);
                    schemes.add(sslScheme2);

                    getContext().put(AbstractHTTPConnector.SSL_CUSTOM_SCHEME, schemes);
                */

                if (sslScheme == null) {
                    LOGGER.info("AbstractHTTPConnector: using port 443 for ssl.");
                    sslScheme = new Scheme("https", httpSSLPort, socketFactory);
                }

                getContext().put(AbstractHTTPConnector.SSL_CUSTOM_SCHEME, sslScheme);
            }
    }
}

//end of class
