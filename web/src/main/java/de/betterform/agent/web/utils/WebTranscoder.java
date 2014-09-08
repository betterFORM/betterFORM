package de.betterform.agent.web.utils;

import de.betterform.agent.web.WebFactory;
import de.betterform.agent.web.WebUtil;
import de.betterform.generator.UIGenerator;
import de.betterform.generator.XSLTGenerator;
import de.betterform.xml.config.Config;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * WebTranscoder wraps XSLTGenerator to configure it with web specific params.
 *
 * author: joern turner
 */
public class WebTranscoder {

    public static final String XSL_PARAM_NAME = "xslt";
    /**
     * constant for relative location of resources (relative to web context).
     * Hardcoded as resources are considered betterFORM-internal and their loading should not be touched by users.
     */
    public static final String RESOURCE_DIR = "WEB-INF/classes/META-INF/resources/";
    public static final String ACTIONURL_PARAM_NAME = "action_url";


    private XFormsProcessor xformsProcessor;
    private Config configuration;
    private HttpServletRequest request;
    protected transient HttpServletResponse response;
    private ServletContext context;
    private HttpSession httpSession;

    private String useragent;
    private String xformsSessionKey;

    public WebTranscoder() {
    }

    public void setConfiguration(Config configuration) {
        this.configuration = configuration;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public void setContext(ServletContext context) {
        this.context = context;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    public void setXformsSessionKey(String xformsSessionKey) {
        this.xformsSessionKey = xformsSessionKey;
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
    public UIGenerator createUIGenerator() throws URISyntaxException, XFormsException {
        String relativeUris = configuration.getProperty(WebFactory.RELATIVE_URI_PROPERTY);

        URI uri = getTransformURI();
        XSLTGenerator generator = WebFactory.setupTransformer(uri,getContext());

        if (relativeUris.equals("true")) {
            generator.setParameter("contextroot", ".");
        } else {
            generator.setParameter("contextroot", WebUtil.getContextRoot(request));
        }
        generator.setParameter("sessionKey", this.xformsSessionKey);
        generator.setParameter("action-url", getActionURL());
        generator.setParameter("debug-enabled", String.valueOf(configuration.getProperty("betterform.debug-allowed").equals("true")));
        generator.setParameter("unloadingMessage", configuration.getProperty("betterform.unloading-message"));
        generator.setParameter("baseURI", this.xformsProcessor.getBaseURI());
        generator.setParameter("user-agent", request.getHeader("User-Agent"));

        String triggerPrefix = Config.getInstance().getProperty("betterform.web.triggerPrefix");
        generator.setParameter("trigger-prefix", triggerPrefix);
        return generator;
    }

    /**
     * decides which stylesheet to use. Uses this order of precedence:<br/>
     * 1. look for param 'xslt' on the request URI<br/>
     * 2. look for bf:transform attribute on requested document root element<br/>
     * 3. finally look up configuration (betterform-config.xml) entry for useragent
     * @return
     * @throws XFormsException
     * @throws URISyntaxException
     */
    private URI getTransformURI() throws XFormsException,URISyntaxException {
        URI uri;

        //if we find a xsl param on the request URI this takes precedence over all
        String xslFile = request.getParameter(XSL_PARAM_NAME);
        String xsltPath = RESOURCE_DIR + "/xslt";
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
        String configuredTransform = configuration.getStylesheet(this.useragent);
        if(configuredTransform != null){
            return new File(WebFactory.getRealPath(xsltPath, getContext())).toURI().resolve(new URI(configuredTransform));
        }

        throw new XFormsConfigException("There was no xslt stylesheet found on the request URI, the root element of the form or in the configfile");
    }

    /**
     * Makes sure to return the context in which the processor is running.
     * It uses the current httpSession if context is null. If the context
     * is explicitly set with setContext() it returns this context.
     *
     * @return the context in which the processor is running.
     */
    private ServletContext getContext() {
        // Return the betterform context when set
        if (this.context != null) {
            return context;
        }
        // otherwise get the context from the http session.
        return httpSession.getServletContext();
    }

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

        // encode the URL to allow for session id rewriting
        actionURL = response.encodeURL(actionURL);
        return actionURL;
    }

}
