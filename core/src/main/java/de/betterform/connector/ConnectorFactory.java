/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector;


import de.betterform.xml.base.XMLBaseResolver;
import de.betterform.xml.config.Config;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.action.AbstractBoundAction;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.exception.XFormsInternalSubmitException;
import de.betterform.xml.xforms.model.submission.Submission;
import de.betterform.xml.xpath.XPathUtil;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;


/**
 * Creates connector instances according to the URI schema passed in.
 * <p/>
 * Only absolute URIs are supported. This means they must have a scheme that
 * identifies the protocol to be used.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @author Eduardo Millan <emillan@users.sourceforge.net>
 * @version $Id: ConnectorFactory.java 3463 2008-08-14 11:03:36Z joern $
 */
public abstract class ConnectorFactory {

    private static Log LOGGER = LogFactory.getLog(ConnectorFactory.class);
    private Map context = null;

    /**
     * Creates a new submission handler for the specified URI.
     *
     * @param uri     the relative or absolute URI string.
     * @param element the element to start with XML Base resolution for relative
     *                URIs.
     * @return a new submission handler for the specified URI.
     * @throws XFormsException if a relative URI could not be resolved, no
     *                         submission handler is registered for the specified URI or any error
     *                         occurred during submission handler creation.
     */
    public abstract SubmissionHandler createSubmissionHandler(final String uri, final Element element) throws XFormsException;

    /**
     * Creates a new URI resolver for the specified URI.
     *
     * @param uri     the relative or absolute URI string.
     * @param element the element to start with XML Base resolution for relative
     *                URIs.
     * @return a new URI resolver for the specified URI.
     * @throws XFormsException if a relative URI could not be resolved, if no
     *                         URI resolver is registered for the specified URI or any error occurred
     *                         during URI resolver creation.
     */
    public abstract URIResolver createURIResolver(final String uri, final Element element) throws XFormsException;

    public void setContext(Map context) {
        this.context = context;
    }

    public Map getContext() {
        return this.context;
    }

    /**
     * Returns a new connector factory loaded from configuration
     *
     * @return A new connector factory
     * @throws XFormsConfigException If a configuration error occurs
     */
    public static ConnectorFactory getFactory()
            throws XFormsConfigException {
        ConnectorFactory factory;
        String className = Config.getInstance().getConnectorFactory();

        if (className == null || className.equals("")) {
            factory = new DefaultConnectorFactory();
        } else {
            try {
                Class clazz = Class.forName(className, true, ConnectorFactory.class.getClassLoader());

                factory = (ConnectorFactory) clazz.newInstance();
            } catch (ClassNotFoundException cnfe) {
                throw new XFormsConfigException(cnfe);
            } catch (ClassCastException cce) {
                throw new XFormsConfigException(cce);
            } catch (InstantiationException ie) {
                throw new XFormsConfigException(ie);
            } catch (IllegalAccessException iae) {
                throw new XFormsConfigException(iae);
            }
        }

        return factory;

    }

    /**
     * resolves URI starting with given URI and start Element.<br> [1] tries to
     * resolve input URI by climbing up the tree and looking for xml:base
     * Attributes<br> [2] if that fails the URI is resolved against the baseURI
     * of the processor
     *
     * @param uri     the URI string to start with
     * @param element the start Element
     * @return an evaluated URI object
     * @throws XFormsException if the URI has syntax errors
     */
    public URI getAbsoluteURI(String uri, Element element) throws XFormsException {

        String uriString = evalAttributeValueTemplates(uri, element);
        try {
            // resolve xml base
            String baseUri = XMLBaseResolver.resolveXMLBase(element, uriString);

            if (baseUri.equals(uriString) && !(new URI(baseUri).isAbsolute())) {
                // resolve betterform base uri
                XFormsProcessorImpl processor = getProcessor(element);
                if (processor.getBaseURI() != null) {
                    return new URI(processor.getBaseURI()).resolve(uriString);
                }

                throw new XFormsException("no base uri present");
            }

            // return resolved xml base uri
            return new URI(baseUri);
        } catch (URISyntaxException e) {
            throw new XFormsException(e);
        }
    }

    /**
     * evaluates Attribute values that contain 'Attribute Value Templates' as known from XSL. Evaluation
     * will recognize two syntax variants:<br>
     * - Variable name starting with '$' enclosed by curly brackets e.g. {$foo}<br>
     * - XPath statements e.g. {/root/somepath[@id]}<br>
     *
     * @param uri
     * @param element
     * @return
     * @throws XFormsException
     */
    public String evalAttributeValueTemplates(String uri, Element element) throws XFormsException {
        String toReplace = uri;
        int start;
        int end;
        String valueTemplate;
        String value = "";
        StringBuffer substitutedString = new StringBuffer();
        boolean hasTokens = true;
        while (hasTokens) {
            start = toReplace.indexOf("{");
            end = toReplace.indexOf('}');
            if (start == -1 || end == -1) {
                hasTokens = false; //exit
                substitutedString.append(toReplace);
            } else {
                substitutedString.append(toReplace.substring(0, start));
                valueTemplate = toReplace.substring(start + 1, end);

                //distinguish normal valueTemplate versus context $var
                if (valueTemplate.startsWith("$")) {
                    //read key from context
                    valueTemplate = valueTemplate.substring(1);
                    if (this.context.containsKey(valueTemplate)) {
                        value = this.context.get(valueTemplate).toString();
                    }
                } else {
                    value = evalXPath(element, valueTemplate);
                }
                if (value.equals("")) {
                    LOGGER.warn("valueTemplate could not be evaluated. Replacing '" + valueTemplate + "' with empty string");
                }
                substitutedString.append(value);
                toReplace = toReplace.substring(end + 1);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Uri: " + uri + " evaluated to: " + substitutedString);
        }
        return substitutedString.toString();
    }

    private String evalXPath(Element element, String valueTemplate) throws XFormsException {
        XFormsElement xElement = (XFormsElement) element.getUserData("");
        String xpath;

        if (XPathUtil.hasInstanceFunction(valueTemplate)) {
            xpath = valueTemplate;
        } else {
            String path;
            if (xElement instanceof AbstractBoundAction) {
                path = ((AbstractBoundAction) xElement).getLocationPath();
            } else if (xElement instanceof Submission) {
                path = ((Submission) xElement).getLocationPath();
            } else {
                LOGGER.warn("no AVT processing for this element implemented - returning default instance root as context path");
                path = "instance('default')";
            }
            xpath = evalJoinedXPath(path, valueTemplate);
        }
        return XPathCache.getInstance().evaluateAsString(xElement.getModel().getDefaultInstance().getRootContext(), xpath);
    }

    private String evalJoinedXPath(String path, String valueTemplate) {
        String[] steps = {path, valueTemplate};
        return XPathUtil.joinPathExpr(steps);
    }


    /**
     * parses the URI string for tokens which are defined to start with '{' and end with '}'. The contents found between
     * the brackets is interpreted as key for the XFormsProcessorImpl context map. Tokens will be replaced with their value
     * in context map and the resulting URI is returned for further processing.
     *
     * @param uri
     * @return string with keys substituted by their values in Context.
     */
    public String applyContextProperties(String uri) throws XFormsException {
        String toReplace = uri;
        int start;
        int end;
        String key;
        String value;
        StringBuffer substitutedString = new StringBuffer();
        boolean hasTokens = true;
        while (hasTokens) {
            start = toReplace.indexOf("{$");
            end = toReplace.indexOf('}');
            if (start == -1 || end == -1) {
                hasTokens = false; //exit                                    ›
                substitutedString.append(toReplace);
            } else {
                substitutedString.append(toReplace.substring(0, start));
                key = toReplace.substring(start + 2, end);

                if (this.context.containsKey(key)) {
                    value = this.context.get(key).toString();
                } else {
                    value = "";
                    LOGGER.warn("replaced non-existing key '" + key + "' with empty string");
                }

                substitutedString.append(value);
                toReplace = toReplace.substring(end + 1);
            }
        }

        return substitutedString.toString();
    }

    public InputStream getHTTPResourceAsStream(URI uri) throws XFormsException {
        try {
            HttpRequestBase httpMethod = new HttpGet(uri.toString());
            HttpParams httpParams = new BasicHttpParams();

            HttpClient client = getHttpClient(httpParams);
            HttpResponse response = client.execute(httpMethod);
            if (response.getStatusLine().getStatusCode() >= 300) {
                // Allow 302 only
                if (response.getStatusLine().getStatusCode() != 302) {
                    throw new XFormsInternalSubmitException(response.getStatusLine().getStatusCode(), response.getStatusLine().getReasonPhrase(), EntityUtils.toString(response.getEntity()), XFormsConstants.RESOURCE_ERROR);
                }
            }
            return response.getEntity().getContent();

        } catch (IOException e) {
            throw new XFormsException(e);
        } catch (XFormsInternalSubmitException e) {
            throw new XFormsException(e);
        }
    }

    protected XFormsProcessorImpl getProcessor(Element e) {


        Object o = e.getOwnerDocument().getDocumentElement().getUserData("");
        if (o instanceof Container) {
            return ((Container) o).getProcessor();
        }
        return null;
    }

    public DefaultHttpClient getHttpClient(HttpParams httpParams) {
          return new DefaultHttpClient(httpParams);
    }
}
