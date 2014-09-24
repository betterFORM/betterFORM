/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web;

import de.betterform.BetterFORMConstants;
import de.betterform.agent.web.event.DefaultUIEventImpl;
import de.betterform.agent.web.event.UIEvent;
import de.betterform.agent.web.flux.FluxProcessor;
import de.betterform.agent.web.servlet.HttpRequestHandler;
import de.betterform.agent.web.servlet.XFormsPostServlet;
import de.betterform.generator.UIGenerator;
import de.betterform.generator.XSLTGenerator;
import de.betterform.xml.config.Config;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.AbstractProcessorDecorator;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.xml.sax.InputSource;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.Cookie;

/**
 * Superclass for Adapters used in web applications. Does minimal event listening on the processor and provides
 * a common base to build webadapers.
 *
 * @author Joern Turner
 * @version $Id: WebAdapter.java 2875 2007-09-28 09:43:30Z lars $
 * @see de.betterform.agent.web.flux.FluxProcessor
 * @see de.betterform.agent.web.servlet.PlainHtmlProcessor
 */
public class WebProcessor extends AbstractProcessorDecorator {

    /**
     * Defines the key for accessing (HTTP) session ids.
     */
    // todo: harmonize keys should be: REQUEST_URI="REQUEST_URI"; to have the same name when accessing from form with {$REQUEST_URI}
    public static final String REQUEST_URI = "requestURI";
    public static final String REQUEST_URL = "requestURL";
    public static final String PATH_INFO = "pathInfo";
    public static final String QUERY_STRING = "queryString";
    public static final String CONTEXTROOT = "contextroot";
    public static final String SESSION_ID = "betterform.session.id";
    public static final String REALPATH = "webapp.realpath";
    public static final String XSL_PARAM_NAME = "xslt";
    public static final String ACTIONURL_PARAM_NAME = "action_url";
    public static final String UIGENERATOR = "betterform.UIGenerator";
    public static final String REFERER = "betterform.referer";
    public static final String FORWARD_URL = "betterform.base.url";
    public static final String ADAPTER_PREFIX = "A";
    public static final String REQUEST_PATH = "requestpath";


    /**
     * constant for relative location of resources (relative to web context).
     * Hardcoded as resources are considered betterFORM-internal and their loading should not be touched by users.
     */
    public static final String RESOURCE_DIR = "WEB-INF/classes/META-INF/resources/";

    public static final String ALTERNATIVE_ROOT = "ResourcePath";

    //todo:review - can be deleted when ehcache is in place
    String KEEPALIVE_PULSE = "keepalive";
    protected transient HttpRequestHandler httpRequestHandler;
    protected transient XMLEvent exitEvent = null;
    protected String contextRoot;
    private String key = null;
    protected transient HttpServletRequest request;
    protected transient HttpServletResponse response;
    protected transient HttpSession httpSession;
    protected transient ServletContext context;
//    protected boolean isXFormsPresent = false;
    private static final Log LOGGER = LogFactory.getLog(SocketProcessor.class);
    protected String uploadDestination;
    protected String useragent;
    protected String uploadDir;
    protected transient UIGenerator uiGenerator;

    public WebProcessor() {
        super();
    }

    public void configure() throws XFormsException {
        initConfig();
        Cookie[] cookies = request.getCookies();
        List<Cookie> cookiesList;

        if(cookies != null) {
            cookiesList = Arrays.asList(request.getCookies());
        } else {
            cookiesList = Collections.EMPTY_LIST;
        }

        WebUtil.storeCookies(cookiesList, this);
        WebUtil.setContextParams(request, httpSession, this, getKey());
        WebUtil.copyHttpHeaders(request, this);
        setLocale();
        configureUpload();
    }

    /**
     * the string identifier generated by this XFormsSession for use in the client
     *
     * @return the string identifier generated by this XFormsSession for use in the client
     */
    public String getKey() {
        if (this.key == null) {
            this.key = generateXFormsSessionKey();
        }
        return this.key;
    }

    protected void setKey(String key) {
        LOGGER.debug("Settting key: " + key );
        this.key = key;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    /**
     * passes an XForms document to the betterForm Adapter. It supports 4 different ways of passing
     * a XForms:<br/>
     * a. passing a request param with a URI e.g. form=/forms/foo.xhtml<br/>
     * b. passing a URI as request attribute<br/>
     * c. passing a request attribute of 'XFORMS_NODE' with a value containing the input document<br/>
     * d. passing an inputstream containing the document<br/>
     * e. passing a SAX inputsource containing the document.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *
     */
    public void setXForms() throws XFormsException {
        if (this.xformsProcessor == null) {
            throw new XFormsException("Adapter has not yet been initialized");
        }
        if (request.getParameter(WebFactory.FORM_PARAM_NAME) != null) {

            try {
                String formURI = WebUtil.getFormUrlAsString(this.request);
                this.xformsProcessor.setXForms(new URI(formURI));
                //set the base URI explicitly here cause it must match the path of the loaded form
                setBaseURI(formURI);
            } catch (URISyntaxException e) {
                throw new XFormsException("URI is malformed: " + e);
            } catch (MalformedURLException e) {
                throw new XFormsException("URL is malformed: " + e);
            } catch (UnsupportedEncodingException e) {
                throw new XFormsException("Encoding of form Url is not supported: " + e);
            }
        } else if (request.getAttribute(WebFactory.XFORMS_URI) != null) {
            String uri = (String) request.getAttribute(WebFactory.XFORMS_URI);

            try {
                this.xformsProcessor.setXForms(new URI(WebUtil.decodeUrl(uri, request)));
            } catch (URISyntaxException e) {
                throw new XFormsException("URI is malformed: " + e);
            } catch (UnsupportedEncodingException e) {
                throw new XFormsException("Encoding of form Url is not supported: " + e);
            }
        } else if (request.getAttribute(WebFactory.XFORMS_NODE) != null) {
            Node node = (Node) request.getAttribute(WebFactory.XFORMS_NODE);
            this.xformsProcessor.setXForms(node);
        } else if (request.getAttribute(WebFactory.XFORMS_INPUTSTREAM) != null) {
            InputStream inputStream = (InputStream) request.getAttribute(WebFactory.XFORMS_INPUTSTREAM);
            this.xformsProcessor.setXForms(inputStream);
        } else if (request.getAttribute(WebFactory.XFORMS_INPUTSOURCE) != null) {
            InputSource inputSource = (InputSource) request.getAttribute(WebFactory.XFORMS_INPUTSOURCE);
            this.xformsProcessor.setXForms(inputSource);
        } else {
            throw new XFormsException("no XForms input document found - init failed");
        }
//        isXFormsPresent = true;

        if (this.configuration.getProperty("webprocessor.doIncludes").equals("true")) {
            doIncludes();
        }
    }


    //todo: parse accept-language header to decide about locale
    public void setLocale() throws XFormsException {
        if (Config.getInstance().getProperty(XFormsProcessorImpl.BETTERFORM_ENABLE_L10N).equals("true")) {

            //[1] check for request param 'lang' - todo: might need refinement later to not clash with using apps
            //String locale = request.getParameter("lang");
            if (request.getParameter("lang") != null) {
                if (WebProcessor.LOGGER.isDebugEnabled()) {
                    WebProcessor.LOGGER.debug("using 'lang' Url parameter: " + request.getParameter("lang"));
                }
                this.setLocale(request.getParameter("lang"));
            } else if ((String) request.getAttribute("lang") != null) {
                if (WebProcessor.LOGGER.isDebugEnabled()) {
                    WebProcessor.LOGGER.debug("using request Attribute 'lang': " + request.getParameter("lang"));
                }
                this.setLocale((String) request.getAttribute("lang"));
            } else if (StringUtils.isNotBlank(Config.getInstance().getProperty("preselect-language"))) {
                if (WebProcessor.LOGGER.isDebugEnabled()) {
                    WebProcessor.LOGGER.debug("using configured lang setting from Config: " + Config.getInstance().getProperty("preselect-language"));
                }
                this.setLocale(Config.getInstance().getProperty("preselect-language"));
            } else if (request.getHeader("accept-language") != null) {
                if (WebProcessor.LOGGER.isDebugEnabled()) {
                    WebProcessor.LOGGER.debug("using accept-language header: " + request.getHeader("accept-language"));
                }
                //getLocale takes the one with the highest priority
                setLocale(request.getLocale().getLanguage());
            }
        } else {
            //fallback default
            this.setLocale("en");
        }
    }

    /**
     * Makes sure to return the context in which the processor is running.
     * It uses the current httpSession if context is null. If the context
     * is explicitly set with setContext() it returns this context.
     *
     * @return the context in which the processor is running.
     */
    public ServletContext getContext() {
        // Return the betterform context when set
        if (this.context != null) {
            return context;
        }

        if(httpSession != null){
            // otherwise get the context from the http session.
            return httpSession.getServletContext();
        }
        return null;
    }

    /**
     * Overwrites the (servlet) context to use. If not set
     * the http session is used to get the servlet context.
     *
     * @param context in which this processor is executed.
     */
    public void setContext(ServletContext context) {
        this.context = context;
    }

    /**
     * initialize the Adapter. This is necessary cause often the using
     * application will need to configure the Adapter before actually using it.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *
     */
    public void init() throws XFormsException {
//        if (noHttp()) {
//            throw new XFormsException("request, response and session object are undefined");
//        }
        this.configuration = Config.getInstance();
        addEventListeners();

        // init processor
        this.xformsProcessor.init();
    }

    public XMLEvent checkForExitEvent() {
        return this.exitEvent;
    }

    /**
     * Dispatch a UIEvent to trigger some XForms processing such as updating
     * of values or execution of triggers.
     *
     * @param event an application specific event
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *
     */
    public void handleUIEvent(UIEvent event) throws XFormsException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Event " + event.getEventName() + " dispatched");
            LOGGER.debug("Event target: " + event.getId());
        }
    }

    /**
     * listen to processor and add a DefaultUIEventImpl object to the
     * EventQueue.
     *
     * @param event the handled DOMEvent
     */
    public void handleEvent(Event event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling Event " + event.getType());
        }
    }
    /**
     * set the upload location. This string represents the destination (data-sink) for uploads.
     *
     * @param destination a String representing the location where to store uploaded files/data.
     */
    public void setUploadDestination(String destination) {
        this.uploadDestination = destination;
    }

    /**
     * processes the request after init.
     *
     * @throws java.io.IOException
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *
     * @throws java.net.URISyntaxException
     */
    public synchronized void handleRequest() throws XFormsException {
        boolean updating = false; //this will become true in case PlainHtmlProcessor is in use
        WebUtil.nonCachingResponse(response);

        /*
        todo: extract behavior that checks for exit events and move to init().
        */
        try {
            //EXIST-WORKAROUND: TODO triple check ...
            if (request.getMethod().equalsIgnoreCase("POST") && request.getAttribute(XFormsPostServlet.INIT_BY_POST) == null && request.getAttribute("org.exist.forward" ) == null ) {
                updating = true;
                // updating ... - this is only called when PlainHtmlProcessor is in use or an upload happens
                UIEvent uiEvent = new DefaultUIEventImpl();
                uiEvent.initEvent("http-request", null, request);
                handleUIEvent(uiEvent);
            }

            XMLEvent exitEvent = checkForExitEvent();
            if (exitEvent != null) {
                handleExit(exitEvent);
            } else {
                String referer = null;

                if (updating) {
                    //todo: check if this code is still needed (used by XFormsPostServlet?)
                    referer = (String) getContextParam(REFERER);
                    setContextParam("update", "true");
                    String forwardTo = request.getContextPath() + "/view?sessionKey=" + getKey() + "&referer=" + referer;
                    response.sendRedirect(response.encodeRedirectURL(forwardTo));
                } else {
                    //initing ...
                    referer = request.getQueryString();
                    String userAgent = request.getHeader("User-Agent");
                    String xuaCompatible = request.getHeader("X-UA-Compatible");
                    if(xuaCompatible == null && (userAgent.contains("IE8") || userAgent.contains("MSIE 8"))){
                        response.addHeader("X-UA-Compatible", "IE8");
                    }
                    else if(xuaCompatible == null && (userAgent.contains("IE9") || userAgent.contains("MSIE 9"))){
                        response.addHeader("X-UA-Compatible", "IE9");
                    }

                    response.setContentType(WebUtil.HTML_CONTENT_TYPE);
                    //we got an initialization request (GET) - the session is not registered yet
                    createUIGenerator();
                    //store queryString as 'referer' in XFormsSession
                    setContextParam(REFERER, request.getContextPath() + request.getServletPath() + "?" + referer);
                    //todo:check if it's still necessary to set an attribute to the session
                    //EXIST-WORKAROUND: TODO triple check ...
                    if(request.isRequestedSessionIdValid()) {
                        httpSession.setAttribute("TimeStamp", System.currentTimeMillis());
                    }

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("WebProcessor: Begin Form: \n");
                        DOMUtil.prettyPrintDOM(this.xformsProcessor.getXForms());
                        LOGGER.debug("\nWebProcessor: End Form");
                    }
                    generateUI(this.xformsProcessor.getXForms(), outputStream);

                    response.setContentLength(outputStream.toByteArray().length);
                    response.getOutputStream().write(outputStream.toByteArray());
                }
            }
        } catch (IOException e) {
            throw new XFormsException(e);
        } catch (URISyntaxException e) {
            throw new XFormsException(e);
        }
        WebUtil.printSessionKeys(this.httpSession);
    }

    protected void generateUI(Object input, Object output) throws XFormsException {
        uiGenerator.setInput(input);
        uiGenerator.setOutput(output);
        uiGenerator.generate();
    }

    /**
     * Handles XForms exist events. There are only 2 situations when this occurs. A load action or a submission/@replace='all'
     * firing during XForms Model init.
     *
     * @param exitEvent the XMLEvent representing the exit condition
     * @throws java.io.IOException occurs if the redirect fails
     */
    public void handleExit(XMLEvent exitEvent) throws IOException {
        if (BetterFormEventNames.REPLACE_ALL.equals(exitEvent.getType())) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/" + BetterFORMConstants.SUBMISSION_RESPONSE  + "?sessionKey=" + getKey()));
        } else if (BetterFormEventNames.REPLACE_ALL_XFORMS.equals(exitEvent.getType()) ) {
            WebUtil.removeSession(getKey());
            response.sendRedirect(response.encodeRedirectURL((String) exitEvent.getContextInfo(BetterFORMConstants.SUBMISSION_REDIRECT_XFORMS)));
        } else if (BetterFormEventNames.LOAD_URI.equals(exitEvent.getType())) {
            //todo: this check seems insufficient - should be a load show="replace"
            if (exitEvent.getContextInfo("show") != null) {
                String loadURI = (String) exitEvent.getContextInfo("uri");

                //kill XFormsSession
                WebUtil.removeSession(getKey());
                if (WebProcessor.LOGGER.isDebugEnabled()) {
                    WebProcessor.LOGGER.debug("loading: " + loadURI);
                }
                response.sendRedirect(response.encodeRedirectURL(loadURI));
            }
        }
        WebProcessor.LOGGER.debug("************************* EXITED DURING XFORMS MODEL INIT *************************");
    }

    /**
     * close the XFormsSession in case of an exception. This will close the WebAdapter holding the betterForm Processor instance,
     * remove the XFormsSession from the Manager and redirect to the error page.
     *
     * @param e the root exception causing the close
     * @throws java.io.IOException
     */
    public void close(Exception e) throws IOException {
        // attempt to shutdown processor
        if (this.xformsProcessor != null) {
            try {
                this.xformsProcessor.shutdown();
            } catch (XFormsException xfe) {
                WebProcessor.LOGGER.error("Message: " + xfe.getMessage() + " Cause: " + xfe.getCause());
            }
        }

        // store exception
        httpSession.setAttribute("betterform.exception", e);

        //remove session from XFormsSessionManager
        // getManager().deleteXFormsSession(this.key);
        WebUtil.removeSession(getKey());

        // redirect to error page (after encoding session id if required)
        response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/" +
                configuration.getProperty(WebFactory.ERROPAGE_PROPERTY)));
    }

    /**
     * passes the betterform-defaults.xml config file to betterForm Processor.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *
     */
    protected void initConfig() throws XFormsException {
        final String initParameter = getContext().getInitParameter(WebFactory.BETTERFORM_CONFIG_PATH);
        String configPath = WebFactory.getRealPath(initParameter, getContext());
        if ((configPath != null) && !(configPath.equals(""))) {
            this.xformsProcessor.setConfigPath(configPath);
            this.configuration = Config.getInstance();
        }
    }

    //todo: might be pushed up
    private String generateXFormsSessionKey() {
        StringBuffer key = new StringBuffer(String.valueOf(System.currentTimeMillis()));
        key.append(String.valueOf(Math.random()));
        return DigestUtils.sha512Hex(key.toString());
    }

    protected HttpRequestHandler getHttpRequestHandler() {
        if (this.httpRequestHandler == null) {
            this.httpRequestHandler = new HttpRequestHandler(this);
            this.httpRequestHandler.setUploadRoot(this.uploadDestination);
            this.httpRequestHandler.setSessionKey(this.getKey());
        }

        return this.httpRequestHandler;
    }

    private URI getTransformURI() throws XFormsException,URISyntaxException {
        URI uri;

        //if we find a xsl param on the request URI this takes precedence over all
        String xslFile = request.getParameter(XSL_PARAM_NAME);
        String xsltPath = RESOURCE_DIR + "xslt";
        if(xslFile != null){
            return new File(WebFactory.getRealPath(xsltPath, getContext())).toURI().resolve(new URI(xslFile));
        }

        //if we find a 'bf:transform' attribute on the root element of a form this takes priority over the global configuration in betterform-config.xml
        Element e  = this.xformsProcessor.getXForms().getDocumentElement();
        if (e.hasAttributeNS(NamespaceConstants.BETTERFORM_NS, "transform")) {
            String transformValue = e.getAttributeNS(NamespaceConstants.BETTERFORM_NS, "transform");
            return new URI(WebUtil.getRequestURI(request) + transformValue);
        }

        //finally use the configuration
//        String configuredTransform = configuration.getStylesheet(this.useragent);
        String configuredTransform = configuration.getProperty("ui-transform");

        //todo: this forces to load the transform from filesystem - should be changed
        if(configuredTransform != null){
            return new File(WebFactory.getRealPath(xsltPath, getContext())).toURI().resolve(new URI(configuredTransform));
        }

        throw new XFormsConfigException("There was no xslt stylesheet found on the request URI, the root element of the form or in the configfile");
    }
    /**
     * creates and configures a UIGenerator that transcodes the XHTML/XForms document into the desired target format.
     * <p/>
     * todo: make configuration of xsl file more flexible                                                                              3
     * todo: add baseURI as stylesheet param
     *
     * @return an instance of an UIGenerator
     * @throws java.net.URISyntaxException
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *
     */
    protected void createUIGenerator() throws URISyntaxException, XFormsException {
        String relativeUris = configuration.getProperty(WebFactory.RELATIVE_URI_PROPERTY);

        URI uri = getTransformURI();
//        XSLTGenerator generator = setupTransformer(uri);
        XSLTGenerator generator = WebFactory.setupTransformer(uri,getContext());
        generator.setParameter("sessionKey", getKey());
        generator.setParameter("baseURI", getBaseURI());
        String realPath = WebFactory.getRealPath("/", context);
        generator.setParameter("realPath", realPath);
        generator.setParameter("locale", locale);
        generator.setParameter("user-agent", request.getHeader("User-Agent"));
        generator.setParameter("action-url", getActionURL()); //todo: check this

        if (relativeUris.equals("true")) {
            generator.setParameter("contextroot", ".");
        } else {
            generator.setParameter("contextroot", WebUtil.getContextRoot(request));
        }

        //todo: keep-alive should be deprecated and removed everywhere
        if (getContextParam(KEEPALIVE_PULSE) != null) {
            generator.setParameter("keepalive-pulse", getContextParam(KEEPALIVE_PULSE));
        }

        generator.setParameter("debug-enabled", String.valueOf(configuration.getProperty("betterform.debug-allowed").equals("true")));
        generator.setParameter("unloadingMessage", configuration.getProperty("betterform.unloading-message"));

        String selectorPrefix = Config.getInstance().getProperty(HttpRequestHandler.SELECTOR_PREFIX_PROPERTY,
                HttpRequestHandler.SELECTOR_PREFIX_DEFAULT);
        generator.setParameter("selector-prefix", selectorPrefix);
        String removeUploadPrefix = Config.getInstance().getProperty(HttpRequestHandler.REMOVE_UPLOAD_PREFIX_PROPERTY,
                HttpRequestHandler.REMOVE_UPLOAD_PREFIX_DEFAULT);
        generator.setParameter("remove-upload-prefix", removeUploadPrefix);
        String dataPrefix = Config.getInstance().getProperty("betterform.web.dataPrefix");
        generator.setParameter("data-prefix", dataPrefix);


        String triggerPrefix = Config.getInstance().getProperty("betterform.web.triggerPrefix");
        generator.setParameter("trigger-prefix", triggerPrefix);


        this.uiGenerator = generator;
        //store UIGenerator in context map of processor for use with load embed
        setContextParam(UIGENERATOR, this.uiGenerator);

    }

    private void doIncludes() {
        try {
            Node input = getXForms();
            String xsltPath = RESOURCE_DIR + "xslt/";
            URI styleURI = new File(WebFactory.getRealPath(xsltPath, getContext())).toURI().resolve(new URI("include.xsl"));
            XSLTGenerator xsltGenerator = WebFactory.setupTransformer(styleURI,getContext());
            String baseURI = getBaseURI();
            String uri = baseURI.substring(0, baseURI.lastIndexOf("/") + 1);

            xsltGenerator.setParameter("root", uri);
            DOMResult result = new DOMResult();
            DOMSource source = new DOMSource(input);
            xsltGenerator.setInput(source);
            xsltGenerator.setOutput(result);
            xsltGenerator.generate();
            setXForms(result.getNode());

        } catch (XFormsException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

//    private XSLTGenerator setupTransformer(URI uri) throws URISyntaxException {
//        return WebFactory.setupTransformer(uri,getContext());
//    }

    /**
     * determines the value for the HTML form/@action attribute in the transcoded page
     *
     * @return the action url to be used in the HTML form
     */
    private String getActionURL() {
        String defaultActionURL = this.request.getRequestURI();
        String encodedDefaultActionURL = response.encodeURL(defaultActionURL);
        int sessIdx = encodedDefaultActionURL.indexOf(";jsession");
        String sessionId = null;
        if (sessIdx > -1) {
            sessionId = encodedDefaultActionURL.substring(sessIdx);
        }
        String actionURL = request.getParameter(ACTIONURL_PARAM_NAME);
        if (null == actionURL) {
            actionURL = encodedDefaultActionURL;
        } else if (null != sessionId) {
            actionURL += sessionId;
        }

        WebProcessor.LOGGER.debug("actionURL: " + actionURL);
        // encode the URL to allow for session id rewriting
        actionURL = response.encodeURL(actionURL);
        return actionURL;
    }

    private void configureUpload() throws XFormsConfigException {
        //allow absolute paths otherwise resolve relative to the servlet context
        this.uploadDir = Config.getInstance().getProperty("uploadDir");
        if (uploadDir == null) {
            throw new XFormsConfigException("upload dir is not set in betterform-config.xml");
        }
        if (!new File(uploadDir).isAbsolute()) {
            uploadDir = WebFactory.getRealPath(uploadDir, getContext());
        }

        setUploadDestination(new File(uploadDir).getAbsolutePath());
    }

    private boolean noHttp() {
        if (this.request != null && this.response != null && this.httpSession != null) {
            return false;
        } else {
            return true;
        }
    }


}
