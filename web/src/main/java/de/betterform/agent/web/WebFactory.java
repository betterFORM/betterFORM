/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web;

import de.betterform.generator.XSLTGenerator;
import de.betterform.xml.config.Config;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xslt.TransformerService;
import de.betterform.xml.xslt.impl.CachingTransformerService;
import de.betterform.xml.xslt.impl.ClasspathResourceResolver;
import de.betterform.xml.xslt.impl.FileResourceResolver;
import de.betterform.xml.xslt.impl.HttpResourceResolver;
import net.sf.ehcache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;


/**
 * @author Joern Turner
 */
public class WebFactory {
    private static final Log LOGGER = LogFactory.getLog(WebFactory.class);
    public static final String BETTERFORM_CONFIG_PATH = "betterform.configfile";
    public static final String LOG_CONFIG = "log4j.file";
    public static final String BETTERFORM_SUBMISSION_RESPONSE = "betterform.submission.response";
    public static final String XSLT_CACHE_PROPERTY = "xslt.cache.enabled";

    public static final String UPLOADDIR_PROPERTY = "uploadDir";
    public static final String RELATIVE_URI_PROPERTY = "forms.uri.relative";
    public static final String ERROPAGE_PROPERTY = "error.page";

    public static final String XFORMS_NODE = "XFormsInputNode";
    public static final String XFORMS_INPUTSTREAM = "XFormsInputStream";
    public static final String XFORMS_INPUTSOURCE = "XFormsInputSource";
    public static final String XFORMS_URI = "XFormsInputURI";
    public static final String FORM_PARAM_NAME = "form";
    public static final String IGNORE_RESPONSEBODY = "filter.ignoreResponseBody";

    private ServletContext servletContext;
    private Config config;
    public static final String USER_AGENT = "useragent";
    public static final String PARSE_RESPONSE_BODY = "betterform.filter.parseResponseBody";
    public static final String IGNORE_RESPONSE_BODY = "betterform.filter.ignoreResponseBody";
    public static final String ACCEPT_CONTENTTYPE = "acceptContentTypePattern";
    public static final String ALL_XML_TYPES = "all_xml";
    private String userAgentId;
    private static final String DO_INIT_LOGGING = "initLogging";


    public WebFactory() {
    }


    public ServletContext getServletContext(){
        return this.servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public Config getConfig() {
        return this.config;
    }

    /**
     * factory method to create a WebProcessor which itself is a Decorator of XFormsProcessor
     * @param request
     * @return
     * @throws XFormsConfigException
     */
    public static WebProcessor createWebProcessor(HttpServletRequest request) throws XFormsConfigException {
        String useragent;
        if (request.getParameter(WebFactory.USER_AGENT) != null) {
            //passed as request param
            useragent = request.getParameter(WebFactory.USER_AGENT);
        } else if (request.getHeader(WebFactory.USER_AGENT) != null) {
            //passed as http header
            useragent = request.getHeader(WebFactory.USER_AGENT);
        } else if (request.getAttribute(WebFactory.USER_AGENT) != null) {
            useragent = (String) request.getAttribute(WebFactory.USER_AGENT);
        } else {
            throw new XFormsConfigException("Useragent has not been set.");
        }

        WebProcessor processor;
        Map useragents = Config.getInstance().getUserAgents();
        String className="";
        if(useragents.containsKey(useragent)){
            try {
                className = (String) useragents.get(useragent);
                Class clazz = Class.forName(className,true, WebFactory.class.getClassLoader());
                Object o = clazz.newInstance();
                if (!(o instanceof XFormsProcessor)) {
                    throw new XFormsConfigException("This useragent is not configured properly: '" + useragent + "'");
        }
                processor = (WebProcessor) o;
                processor.setUseragent(useragent);
                return processor;
            }
            catch (ClassNotFoundException cnfe) {
                throw new XFormsConfigException(cnfe);
            }
            catch (ClassCastException cce) {
                throw new XFormsConfigException(cce);
            }
            catch (InstantiationException ie) {
                throw new XFormsConfigException(ie);
            }
            catch (IllegalAccessException iae) {
                throw new XFormsConfigException(iae);
            }

        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("request URI: " + request.getRequestURI());
            LOGGER.info("using user agent: " + useragent + " : " + useragent);
        }
        throw new XFormsConfigException("Processor class : '" + className + "' cannot be instanciated");
    }

    /**
     * reads the the context param 'betterform.configfile' to get the location of the configuration file. Then
     * the configuration will be initialized and stored within the servlet context. It may be accessed by using the constant key
     * AbstractXFormsServlet.BETTERFORM_CONFIGURATION.
     *
     * @param userAgentIdent a valid user agent identifier (either 'dojo' or 'html' at the moment)
     * @throws javax.servlet.ServletException if something goes terribly wrong with accessing the servlet context.
     */
    public void initConfiguration(String userAgentIdent) throws XFormsConfigException {
        LOGGER.info("--------------- initing betterForm... ---------------");
        this.userAgentId = userAgentIdent;
        String configPath = servletContext.getInitParameter("betterform.configfile");
        if (configPath == null) {
            throw new XFormsConfigException("Parameter 'betterform.configfile' not specified in web.xml");
        }

        this.config = Config.getInstance(resolvePath(configPath, servletContext));
    }

    /**
     * initializes a XSLT Transformer service. Currently an implementation of CachingTransformerService is
     * instanciated. Future versions may make this configurable. This is the place to preload transoformations
     * that are used throughout the application.
     *
     * @throws XFormsConfigException a Config exception will occur in case there's no valid setting for XSLT_CACHE_PROPERTY,XSLT_DEFAULT_PROPERTY or
     *                               XSLT_PATH_PROPERTY
     */
    public void initTransformerService(String realPath) throws XFormsConfigException {
        CachingTransformerService transformerService = new CachingTransformerService();

        transformerService.addResourceResolver(new FileResourceResolver());
        transformerService.addResourceResolver(new ClasspathResourceResolver(realPath));
        transformerService.addResourceResolver(new HttpResourceResolver());

        
        boolean xsltCacheEnabled = Config.getInstance().getProperty(WebFactory.XSLT_CACHE_PROPERTY).equalsIgnoreCase("true");

        String xsltPath = WebProcessor.RESOURCE_DIR + "xslt/";
        String xsltDefault = Config.getInstance().getStylesheet(this.userAgentId);

        if (xsltCacheEnabled) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("initializing xslt cache");
            }

            try {
                // load default stylesheet
                URI defaultTransformUri = getXsltURI(xsltPath, xsltDefault);
                transformerService.getTransformer(defaultTransformUri);
                
                URI errorTransformer = getXsltURI(xsltPath,"error.xsl");
                transformerService.getTransformer(errorTransformer);
                
                URI highlightingErrorTransformer = getXsltURI(xsltPath,"highlightError.xsl");
                transformerService.getTransformer(highlightingErrorTransformer);

                if(Config.getInstance().getProperty("webprocessor.doIncludes").equals("true")){
                    URI includeTransformer = getXsltURI(xsltPath,"include.xsl");
                    transformerService.getTransformer(includeTransformer);
                }

                if(Config.getInstance().getProperty("betterform.debug-allowed").equals("true")){
                    URI highlightingDocument = getXsltURI(xsltPath,"highlightDocument.xsl");
                    transformerService.getTransformer(highlightingDocument);
                }
            }
            catch (Exception e) {
                throw new XFormsConfigException(e);
            }
        }

        // store service in servlet context
        // todo: contemplate about transformer service thread-safety
        servletContext.setAttribute(TransformerService.TRANSFORMER_SERVICE, transformerService);
    }

     public static XSLTGenerator setupTransformer(String xsltPath, String xslFile, ServletContext context) throws URISyntaxException {
        TransformerService transformerService = (TransformerService) context.getAttribute(TransformerService.TRANSFORMER_SERVICE);
        URI uri = new File(WebFactory.resolvePath(xsltPath, context)).toURI().resolve(new URI(xslFile));

        XSLTGenerator generator = new XSLTGenerator();
        generator.setTransformerService(transformerService);
        generator.setStylesheetURI(uri);
        return generator;
    }

    public URI getXsltURI(String xsltPath, String xsltDefault) throws URISyntaxException {
        return new File(resolvePath(xsltPath, servletContext)).toURI().resolve(new URI(xsltDefault));
    }


    public void initLogging(Class theClass) throws XFormsConfigException {
        String initLogging = this.config.getProperty(WebFactory.DO_INIT_LOGGING);
        if(initLogging.equals("true")){
            String logConfig = this.config.getProperty(WebFactory.LOG_CONFIG);

            DOMConfigurator.configure(resolvePath(logConfig, servletContext));
            Log logger = LogFactory.getLog(theClass);
            if (logger.isDebugEnabled()) {
                logger.debug("Logger initialized");
            }
        }
    }



    /**
     * allow absolute paths otherwise resolve relative to the servlet context
     *
     * @param path XPath locationpath
     * @return the absolute path or path relative to the servlet context
     */
    public static final String resolvePath(String path, ServletContext servletContext) {
        String tmpPath;
        if (!new File(path).isAbsolute()) {
            tmpPath = servletContext.getRealPath(path);
            if (tmpPath == null) {
                tmpPath =  servletContext.getRealPath(".") + "/" + path;
            }

            path = tmpPath;
        }

        return path;
    }


    public void initXFormsSessionCache() {
        CacheManager.create();
    }
}
