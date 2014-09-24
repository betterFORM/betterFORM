/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */



package de.betterform.agent.web.filter;

import de.betterform.BetterFORMConstants;
import de.betterform.agent.web.WebFactory;
import de.betterform.agent.web.WebProcessor;
import de.betterform.agent.web.WebUtil;
import de.betterform.agent.web.cache.XFSessionCache;
import de.betterform.agent.web.event.DefaultUIEventImpl;
import de.betterform.agent.web.event.UIEvent;
//import de.betterform.agent.web.flux.FluxProcessor;
import de.betterform.agent.web.flux.SocketProcessor;
import de.betterform.html5.Preprocessor;
import de.betterform.xml.config.Config;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.ModelProcessor;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsErrorIndication;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xslt.TransformerService;
import de.betterform.xml.xslt.impl.CachingTransformerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.Cache;
import org.w3c.dom.Node;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A Servlet Filter to provide XForms functionality to existing Servlets
 *
 * @author Adam Retter <adam.retter@devon.gov.uk>, Joern Turner
 * @version 1.3
 * @serial 2007-02-19T13:51
 */
@SuppressWarnings({"JavadocReference"})
public class XFormsFilter implements Filter {
    private static final Log LOG = LogFactory.getLog(XFormsFilter.class);
    private static final String USERAGENT = "dojo";
    protected WebFactory webFactory;

    protected String defaultRequestEncoding = "UTF-8";
    private FilterConfig filterConfig;

    /**
     * Filter initialisation
     *
     * @see http://java.sun.com/j2ee/sdk_1.3/techdocs/api/javax/servlet/Filter.html#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;

        webFactory = new WebFactory();
        webFactory.setServletContext(filterConfig.getServletContext());
        try {
            webFactory.initConfiguration(XFormsFilter.USERAGENT);
            defaultRequestEncoding = webFactory.getConfig().getProperty("defaultRequestEncoding", defaultRequestEncoding);
            webFactory.initLogging(this.getClass());
            String realPath = WebFactory.getRealPath(".", this.filterConfig.getServletContext());
            webFactory.initTransformerService(realPath);
            // webFactory.initXFormsSessionCache(); // todo: still needed????

        } catch (XFormsConfigException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Filter shutdown
     *
     * @see http://java.sun.com/j2ee/sdk_1.3/techdocs/api/javax/servlet/Filter.html#destroy()
     */
    public void destroy() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("cleanups allocated resources");
        }
//        webFactory.destroyXFormsSessionManager();
    }


    /**
     * The actual filtering method
     * todo: add request attribute to set baseURI 
     *
     * @see http://java.sun.com/j2ee/sdk_1.3/techdocs/api/javax/servlet/Filter.html#doFilter(javax.servlet.ServletRequest,%20javax.servlet.ServletResponse,%20javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest srvRequest, ServletResponse srvResponse, FilterChain filterChain) throws IOException, ServletException {

        //ensure correct Request encoding
        if (srvRequest.getCharacterEncoding() == null) {
            srvRequest.setCharacterEncoding(defaultRequestEncoding);
        }

        HttpServletRequest  request  = (HttpServletRequest) srvRequest;
        HttpServletResponse response = (HttpServletResponse) srvResponse;
        HttpSession         session  = request.getSession(true);

        /*
        if header "betterform-internal is present this means that the internal http client of the processor is the
        originator of the incoming request. Since this may not be processed we skip these requests by
        simply calling the servlet chain.

        As the contenttype may not be present for such requests we have to determine the mimetype by the file ending
        of the requested resource.
        */

        //Check if  MEDIATYPE is set if so we will handle response as XForms
        if( request.getHeader(BetterFORMConstants.BETTERFORM_INTERNAL) != null ) {
            LOG.warn("Request from internal betterForm HTTP Client arrived in XFormsFilter");
            String requestURI = request.getRequestURI();

            String mimeType = webFactory.getServletContext().getMimeType(requestURI);

            if(LOG.isDebugEnabled()){
                LOG.debug("request URI: " + requestURI);
                LOG.debug("mimeType: " + mimeType);
            }

            if(mimeType != null){
                srvResponse.setContentType(mimeType);
            }else{
                LOG.warn("no contenttype set for internal request");
//                throw new ServletException("Contenttype of " + requestURI + " unknown. Please configure your webcontainer appropriately.");
            }

            filterChain.doFilter(srvRequest, srvResponse);
            return;
        }

        if(request.getParameter("source") != null ){
            srvResponse.setContentType("text/plain");
            // override setContentType to keep "text/plain" as content type.
            HttpServletResponseWrapper resp = new HttpServletResponseWrapper((HttpServletResponse) srvResponse) {
                public void setContentType(String s) {
                    return;
                }
            };
            filterChain.doFilter(srvRequest, resp);
            return;  
        }

        if(request.getParameter("isUpload") != null){
            //Got an upload...
            handleUpload(request,response,session);
        //TODO: XFORMS  PROCESSING: do we need to exit?
        }else if ("GET".equalsIgnoreCase(request.getMethod())  && request.getParameter(BetterFORMConstants.SUBMISSION_RESPONSE) != null) {
            doSubmissionReplaceAll(request, response);
        } else if ("GET".equalsIgnoreCase(request.getMethod())  && request.getParameter(BetterFORMConstants.SUBMISSION_RESPONSE_XFORMS) != null) {
            doSubmissionReplaceAllXForms(request, response,session);
        } else if (request.getContentType() != null && request.getContentType().contains("form") && "POST".equalsIgnoreCase(request.getMethod())) {
            //html form submit ? should be -> requires that forms get submitted by post
            String contentType = request.getContentType();

            if(contentType.contains("form")){
                // get referer document
                /*
                This might be a weak point: the referer header is not reliable in all cases or might get filtered by proxies. For this to work
                the referer must return the URL of the host document of the form.
                */
                String referer = request.getHeader("Referer");

                // parse and sanitize it -> xhtml
                // generate xforms from referer document, put into defined folder 'by the side' for caching/refining generated xforms
                // mix incoming form data into generated document or pass it as XML instance
                Enumeration<String> params = request.getParameterNames();
                StringBuffer formData = new StringBuffer();
                while(params.hasMoreElements()){
                    String name = params.nextElement();
                    String value = request.getParameter(name);
                    formData.append(name);
                    formData.append(":");
                    if(value != null){
                        formData.append(value);
                    }else{
                        formData.append("");
                    }
                    formData.append(";");
                }
                if(LOG.isDebugEnabled()){
                    LOG.debug("data send by form: " + formData.toString());
                }

                Node node=null;
                try {
                    node = Preprocessor.submit2Xforms(referer, (CachingTransformerService) this.filterConfig.getServletContext().getAttribute(TransformerService.TRANSFORMER_SERVICE), formData.toString());
                    DOMUtil.prettyPrintDOM(node);
                } catch (URISyntaxException e) {
                    returnErrorPage(request,response,session,e);
                } catch (TransformerException e) {
                    returnErrorPage(request, response, session, e);
                } catch (XFormsConfigException e) {
                    returnErrorPage(request, response, session, e);
                }


                try {
                    ModelProcessor mp = createXFormsModelProcessor(node);
                    boolean success = mp.isSuccess();

                    // instanciate processor (might even be a plain (non-web) processor)

                    if(success){

                        // processor will attach xforms-submit-error and xforms-submit-done listeners
                        // feed generated xforms
                        // call processor revalidate
                        // if submit-done pass request on unchanged
                        // if submit-error return input document with embedded error information (e.g. as a div as first or last child of body); option - redirect to error page
                        LOG.info("HTML form data is valid");
                        BufferedHttpServletResponseWrapper bufResponse = new BufferedHttpServletResponseWrapper((HttpServletResponse) srvResponse);
                        filterChain.doFilter(srvRequest, bufResponse);
                        LOG.info("Returned from Chain");
                        response(response,bufResponse);
                    }else {
                        //todo: return error information
                        List errors = mp.getErrors();

                        sendError(request, response, session);
                    }
                } catch (XFormsException e) {
                    sendError(request, response, session);
                }
            }
        } else {
            /* ########################### call filter chain / end point #################################### */
            /* ########################### call filter chain / end point #################################### */
            /* ########################### call filter chain / end point #################################### */
            LOG.info("Passing to Chain");
            BufferedHttpServletResponseWrapper bufResponse = new BufferedHttpServletResponseWrapper((HttpServletResponse) srvResponse);
            filterChain.doFilter(srvRequest, bufResponse);
            LOG.info("Returned from Chain");

            //handleResponse(srvResponse, request, response, session, bufResponse, webFactory);

            // response is contains no betterFORM relevant content so it is not buffered and cannot be processed
            if ( ! bufResponse.isBuffered()) {
                return;
            }

            // response is already committed to the client, so nothing is to
            // be done
            if (bufResponse.isCommitted()) {
                return;
            }

            //pass to request object
            request.setAttribute(WebFactory.USER_AGENT, XFormsFilter.USERAGENT);


            /*
            ############################################################
            Decide whether to process html, xhtml or other content

            - if incoming file has extension '.html' it will be send to JSoup for sanitizing the HTML. The resulting
            jsoup document is turned into a W3C document and fed into a transformer (html2xforms) that turns the HTML
            into XForms markup. The xforms document is then passed to the xforms processor but WITHOUT transforming it
            into HTML again. The original version of the HTML document is returned.

            - if incoming has extension '.xhtml' it will be parsed and fed into the processor. The processor will
            generate the UI using the configured default XSLT.

            - all other content will be copied through unmodified
            ############################################################
            */

            /* dealing with response from chain */
            boolean generateUI=true;
            if (handleResponseBody(request, bufResponse)) {
                if(request.getRequestURI().endsWith(".html")){
                    if(request.getRequestURI().contains("components/")){
                        response(response, bufResponse);
                    }else{
                        //html input processing
                        Node node = Preprocessor.html2Xforms(bufResponse.getDataAsString(), (CachingTransformerService) this.filterConfig.getServletContext().getAttribute(TransformerService.TRANSFORMER_SERVICE));
                        generateUI=false;
                        //store into request attribute that will be picked up during xforms processor init (setXForms)
                        request.setAttribute(WebFactory.XFORMS_NODE, node);
                        processXForms(request, response, session, false);
                        response(response, bufResponse);
                    }
                }else {
                    byte[] data = prepareData(bufResponse, request);
                    if (data.length > 0) {
                        request.setAttribute(WebFactory.XFORMS_INPUTSTREAM, new ByteArrayInputStream(data));
                    }
                }
            }

            if (handleRequestAttributes(request)) {
                bufResponse.getOutputStream().close();
                LOG.info("Start Filter XForm");
                processXForms(request, response, session, generateUI);

                LOG.info("End Render XForm");
            } else {
                response(response, bufResponse);
            }
        }
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws ServletException, IOException {
        session.setAttribute("betterform.referer", request.getRequestURL());
        String path = "/" + webFactory.getConfig().getProperty(WebFactory.ERROPAGE_PROPERTY);
        webFactory.getServletContext().getRequestDispatcher(path).forward(request,response);
    }

    private ModelProcessor createXFormsModelProcessor(Node xforms) throws ServletException, IOException, XFormsException {
        ModelProcessor modelProcessor = null;
        modelProcessor = new ModelProcessor();
        modelProcessor.setXformsProcessor(new XFormsProcessorImpl());
        modelProcessor.setXForms(xforms);
        modelProcessor.init();
        return modelProcessor;
    }

    private void response(HttpServletResponse response, BufferedHttpServletResponseWrapper bufResponse) throws IOException {
        response.getOutputStream().write(bufResponse.getData());
        response.getOutputStream().close();
    }

    private void processXForms( HttpServletRequest  request,  HttpServletResponse response, HttpSession  session, boolean handleRequest) throws IOException, ServletException {
        SocketProcessor webProcessor = null;
        try {
            webProcessor = new SocketProcessor();
            webProcessor.setXformsProcessor(new XFormsProcessorImpl());
            webProcessor.setRequest(request);
            webProcessor.setResponse(response);
            webProcessor.setHttpSession(session);
            webProcessor.setBaseURI(request.getRequestURL().toString());
            webProcessor.setContext(webFactory.getServletContext());
            webProcessor.configure();
            webProcessor.setXForms();
            webProcessor.init();
            webProcessor.handleRequest(handleRequest);

            //add new xforms session to cache
            Cache cache = XFSessionCache.getCache();
            String key = webProcessor.getKey();
            if(cache.containsKey(key)){
                //reload session
                LOG.warn("Session already exists - key: " + key);
//                     cache.remove(key);
            }
            if(LOG.isDebugEnabled()){
                LOG.debug("adding new session to cache. Key:" + key );
            }
            session.setAttribute("xfSessionKey",webProcessor.getKey());
            cache.put(key,webProcessor);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            if (webProcessor != null) {
                //reset xforms to state before init and serialize it to StreamResult
                //reparse with PositionalXMLReader for error summary

                if(e instanceof XFormsErrorIndication){
                    try {
                        session.setAttribute("betterform.hostDoc",webProcessor.getXForms());
                    } catch (XFormsException e1) {
                        e1.printStackTrace();
                    }
                }

                // attempt to shutdown processor
                try {
                    webProcessor.shutdown();
                } catch (XFormsException xfe) {
                    LOG.error("Could not shutdown Processor: Error: " + xfe.getMessage() + " Cause: " + xfe.getCause());
                }
                //remove session from XFormsSessionManager
                WebUtil.removeSession(webProcessor.getKey());
                // store exception

                returnErrorPage(request, response, session,e);
            }
        }
    }

    private void returnErrorPage(HttpServletRequest request, HttpServletResponse response,HttpSession session, Exception e) throws ServletException, IOException {
        session.setAttribute("betterform.exception", e);
        session.setAttribute("betterform.exception.message", e.getMessage());
        sendError(request, response, session);
    }

    private void handleUpload(HttpServletRequest request,HttpServletResponse response,HttpSession session) throws ServletException {
        response.setContentType("text/html");

        if(LOG.isDebugEnabled()){
            LOG.debug("*** FluxHelper ***");
        }

        WebProcessor webProcessor = WebUtil.getWebProcessor(request, response, session);
        try {
            if (webProcessor == null) {
                throw new ServletException(Config.getInstance().getErrorMessage("session-invalid"));
            }
            if(LOG.isDebugEnabled()){
                LOG.debug(this + ": xformssession:" +  webProcessor.getKey());
            }
            UIEvent uiEvent = new DefaultUIEventImpl();
            uiEvent.initEvent("http-request", null, request);
            webProcessor.handleUIEvent(uiEvent);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    protected byte[] prepareData(BufferedHttpServletResponseWrapper bufResponse, HttpServletRequest request) throws UnsupportedEncodingException {
        //remove DOCTYPE PI if it exists, Xerces in betterForm otherwise may try to download the system DTD (can cause latency problems)
        //byte[] data = removeDocumentTypePI(bufResponse.getData());
        //correct the <xforms:instance> xmlns="" problem (workaround for namespace problems in eXist)
        //return correctInstanceXMLNS(data);
        return correctInstanceXMLNS(bufResponse.getData());
    }

    /**
     * returns true if one of XFORMS_NODE, XFORMS_URI, XFORMS_INPUTSOURCE or XFORMS_NPUTSTREAM constants is found
     * in the request.
     *
     * @param srvRequest the Servlet request
     * @return true if one of the XFORMS_* constants was found, otherwise false
     */
    protected boolean handleRequestAttributes(HttpServletRequest request) {
        if (request.getAttribute(WebFactory.XFORMS_NODE) != null
                || request.getAttribute(WebFactory.XFORMS_URI) != null
                || request.getAttribute(WebFactory.XFORMS_INPUTSOURCE) != null
                || request.getAttribute(WebFactory.XFORMS_INPUTSTREAM) != null) {
            return true;
        }
        return false;
    }

    /**
     * Decides whether response body should be processed. For efficiency the parsing of the response body can be
     * turned off by setting betterform config property 'filter.ignoreResponseBody' to 'true'.<br/><br/>
     * <p/>
     * To overwrite the config setting (if set to 'true') on a per-request basis a request attribute of
     * PARSE_RESPONSE_BODY can be set.
     *
     * @param request     HttpServletRequest
     * @param bufResponse The buffered response
     * @return true if the response contains an XForm, false otherwise
     * @throws UnsupportedEncodingException
     */
    protected boolean handleResponseBody(HttpServletRequest request, BufferedHttpServletResponseWrapper bufResponse) throws UnsupportedEncodingException {

        //[1] check if body parsing is explicitly requested
        if (request.getAttribute(WebFactory.PARSE_RESPONSE_BODY) != null) return true;

        //[2] check if body parsing is explicitly unwanted
        if (request.getAttribute(WebFactory.IGNORE_RESPONSE_BODY) != null) return false;

        // s+c extension for enginframe (ef) response handling
        // responses containing xhtml but not necessarily xforms markup should be transformed to html, too.
        String acceptedContentType = webFactory.getConfig().getProperty(WebFactory.ACCEPT_CONTENTTYPE, "");
        if (!"".equals(acceptedContentType)) {
            if (acceptedContentType.equalsIgnoreCase(WebFactory.ALL_XML_TYPES)) {
                if (bufResponse.hasXMLContentType()) return true;
            } else {
                String contentType = bufResponse.getMediaType();
                if (java.util.regex.Pattern.matches(acceptedContentType, contentType)) return true;
            }
        }

        //[3] check betterform config if response body parsing is disabled
        if (disableReponseBodyParsing()) return false;

        //[4] otherwise check response body for XForms markup
        String strResponse = bufResponse.getDataAsString();

        //EXIST WORKAROUND: Test if String starts with "<" like <html or <DOCTYPE
        if (! strResponse.trim().startsWith("<")) return false;

        if (strResponse.trim().contains("fore-form")) return true;


        //[5]find the xforms namespace local name
        int xfNSDeclEnd = strResponse.indexOf("=\"" + NamespaceConstants.XFORMS_NS + "\"");
        if (xfNSDeclEnd != -1) {
            String temp = strResponse.substring(0, xfNSDeclEnd);
            int xfNSDeclStart = temp.lastIndexOf(':') + 1;
            String xfNSLocal = temp.substring(xfNSDeclStart);

            //check for xforms elements
            if (strResponse.contains('<' + xfNSLocal + ":")) {
                return true;
            }
        }
        //[6] if inclusion is configured we have to process anyway
        try {
            if(Config.getInstance().getProperty("webprocessor.doIncludes").equals("true") &&
                bufResponse.hasXMLContentType()){
            return true;
            }
        } catch (XFormsConfigException e) {
            return false;//if something goes wrong we don't process the response body
        }

        return false;
    }

    /**
     * turns on/off the parsing of the reponse body to determine the existence of some XForms markup in the response
     * stream. By default reponse body parsing is enabled unless it's turned of via configuration.
     *
     * @return true if configuration sets a value of 'true' for 'filter.ignoreResponseBody' in Chbia config and false
     *         in all other cases
     */
    protected boolean disableReponseBodyParsing() {
        boolean ignoreResponse = false;
        try {
            ignoreResponse = Config.getInstance().getProperty(WebFactory.IGNORE_RESPONSEBODY).equalsIgnoreCase("true");
        } catch (XFormsConfigException e) {
            ignoreResponse = false; //default
        }
        if (ignoreResponse) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the request is to update an XForm
     *
     * @param srvRequest The request
     * @return true if the request is to update an XForm, false otherwise
     */
    /*
    public boolean isXFormUpdateRequest(HttpServletRequest request) {

        //must be a POST request
        if (!request.getMethod().equals("POST"))
            return false;

        String key = request.getParameter("sessionKey");
        WebProcessor webProcessor = WebUtil.getWebProcessor(key);
        if (webProcessor == null) {
            return false;
        }

        String actionURL;
        if (request.getQueryString() != null) {
            actionURL = request.getRequestURL() + "?" + request.getQueryString();
        } else {
            actionURL = request.getRequestURL().toString();
        }

        //remove the sessionKey (if any) before comparing the action URL
        int posSessionKey = actionURL.indexOf("sessionKey");
        if (posSessionKey > -1) {
            char preSep = actionURL.charAt(posSessionKey - 1);
            if (preSep == '?') {
                if (actionURL.indexOf('&') > -1) {
                    actionURL = actionURL.substring(0, posSessionKey) + actionURL.substring(actionURL.indexOf('&') + 1);
                } else {
                    actionURL = actionURL.substring(0, posSessionKey - 1);
                }
            } else if (preSep == '&') {
                actionURL = actionURL.substring(0, posSessionKey - 1);
            }
        }

        //if the action-url in the adapters context param is the same as that of the action url then we know we are updating
        return actionURL.equals(webProcessor.getContextParam("action-url"));
    }

    */

    /**
     * Removes the DOCTYPE Processing Instruction from the content if it exists
     *
     * @param content The HTML page content
     * @return The content without the DOCTYPE PI
     * @throws UnsupportedEncodingException
     */
    private byte[] removeDocumentTypePI(byte[] content) throws UnsupportedEncodingException {
        String buf = new String(content, "ISO-8859-1");

        int iStartDoctype = buf.indexOf("<!DOCTYPE");
        if (iStartDoctype > -1) {
            int iEndDoctype = buf.indexOf('>', iStartDoctype);

            String newBuf = buf.substring(0, iStartDoctype - 1);
            newBuf += buf.substring(iEndDoctype + 1);
            return newBuf.getBytes("ISO-8859-1");
        }
        return content;
    }

    /**
     * Inserts the attribute xmlns="" on the xforms:instance node if it is missing
     *
     * @param content The HTML page content
     * @return The content with the corrected xforms:instance
     * @throws UnsupportedEncodingException
     */
    private byte[] correctInstanceXMLNS(byte[] content) throws UnsupportedEncodingException {
        String buffer = new String(content, "UTF-8");
        if (buffer.indexOf("<xforms:instance xmlns=\"\">") == -1) {
            String newBuf = buffer.replace("<xforms:instance>", "<xforms:instance xmlns=\"\">");
            return newBuf.getBytes("UTF-8");
        }

        return content;
    }

    /**
     * handle a Submission/@replace="all". The Processor will have an exitEvent in case a submisssion/@replace="all"
     * or load/@show="replace" happened. The exitEvent will have the response headers and response stream as
     * context info stored in a map under the keys "header" and "body"
     *
     * @param request  the Servlet request
     * @param response the Servlet response
     * @throws IOException if something basic goes wrong
     */
    protected void doSubmissionReplaceAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map submissionResponse = handleResponseReplaceAll(request, response);

        if (submissionResponse != null) {
            // copy body stream
            InputStream bodyStream = (InputStream) submissionResponse.get("body");
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            for (int b = bodyStream.read(); b > -1; b = bodyStream.read()) {
                outputStream.write(b);
            }

            // close streams
            bodyStream.close();
            outputStream.close();
            return;
        }

        response.sendError(HttpServletResponse.SC_FORBIDDEN, "no submission response available");
    }

    protected void doSubmissionReplaceAllXForms(HttpServletRequest request, HttpServletResponse response, HttpSession  session) throws IOException, ServletException {
        Map submissionResponse = handleResponseReplaceAll(request, response);

        if (submissionResponse != null) {
                //Set USERAGENT
                request.setAttribute(WebFactory.USER_AGENT, XFormsFilter.USERAGENT);
                request.setAttribute(WebFactory.XFORMS_INPUTSTREAM,  submissionResponse.get("body"));
                processXForms(request, response, session, false);
                return;
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "no submission response available");
    }


    private Map handleResponseReplaceAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map submissionResponse = null;

        HttpSession session = request.getSession(false);
        WebProcessor webProcessor = WebUtil.getWebProcessor(request, response, session);
        if (session != null && webProcessor != null) {
            if (LOG.isDebugEnabled()) {
                Enumeration keys = session.getAttributeNames();
                if (keys.hasMoreElements()) {
                    LOG.debug("--- existing keys in session --- ");
                }
                while (keys.hasMoreElements()) {
                    String s = (String) keys.nextElement();
                    LOG.debug("existing sessionkey: " + s + ":" + session.getAttribute(s));
                }
            }

            submissionResponse = webProcessor.checkForExitEvent().getContextInfo();
            if (submissionResponse != null) {

                if (LOG.isDebugEnabled()) {
                    LOG.debug("handling submission/@replace='all'");
                    Enumeration keys = session.getAttributeNames();
                    if (keys.hasMoreElements()) {
                        LOG.debug("--- existing keys in http session  --- ");
                        while (keys.hasMoreElements()) {
                            String s = (String) keys.nextElement();
                            LOG.debug("existing sessionkey: " + s + ":" + session.getAttribute(s));
                        }
                    } else {
                        LOG.debug("--- no keys left in http session  --- ");
                    }
                }

                // copy header fields
                Map headerMap = (Map) submissionResponse.get("header");
                String name;
                String value;
                Iterator iterator = headerMap.keySet().iterator();
                while (iterator.hasNext()) {
                    name = (String) iterator.next();
                    if (name.equalsIgnoreCase("Transfer-Encoding")) {
                        // Some servers (e.g. WebSphere) may set a "Transfer-Encoding"
                        // with the value "chunked". This may confuse the client since
                        // XFormsServlet output is not encoded as "chunked", so this
                        // header is ignored.
                        continue;
                    }

                    value = (String) headerMap.get(name);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("added header: " + name + "=" + value);
                    }

                    response.setHeader(name, value);
                }



                //kill XFormsSession
                WebUtil.removeSession(webProcessor.getKey());
            }
        }

        return submissionResponse;
    }
}
