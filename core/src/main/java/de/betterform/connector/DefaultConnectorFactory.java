/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.config.Config;
import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;

import java.net.URI;

/**
 * Creates connector instances according to the URI schema passed in.
 * <p/>
 * Only absolute URIs are supported. This means they must have a scheme that
 * identifies the protocol to be used.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: DefaultConnectorFactory.java 3463 2008-08-14 11:03:36Z joern $
 */
public class DefaultConnectorFactory extends ConnectorFactory {

    private static Log LOGGER = LogFactory.getLog(DefaultConnectorFactory.class);

    /**
     * Creates a new connector factory.
     */
    public DefaultConnectorFactory() {
    }

    /**
     * Creates a new submission handler for the specified URI.
     *
     * @param uri the relative or absolute URI string.
     * @param element the element to start with XML Base resolution for relative
     * URIs.
     * @return a new submission handler for the specified URI.
     * @throws XFormsException if a relative URI could not be resolved, no
     * submission handler is registered for the specified URI or any error
     * occurred during submission handler creation.
     */
    public SubmissionHandler createSubmissionHandler(final String uri, final Element element) throws XFormsException {
        URI uriObj = getAbsoluteURI(uri, element);

        String className = Config.getInstance().getSubmissionHandler(uriObj.getScheme());
        if (className == null) {
            throw new XFormsException("no submission handler registered for '" + uri + "'");
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating submission handler for '" + uriObj + "' : " + className);
        }

        Object instance = createInstance(className);
        if (!(instance instanceof SubmissionHandler)) {
            throw new XFormsException("object instance of '" + className + "' is no submission handler");
        }

        SubmissionHandler submissionHandler = (SubmissionHandler) instance;
        submissionHandler.setURI(uriObj.toString());
        submissionHandler.setContext(getContext());

        return submissionHandler;
    }

    /**
     * Creates a new URI resolver for the specified URI.
     *
     * @param uri the relative or absolute URI string.
     * @param element the element to start with XML Base resolution for relative
     * URIs.
     * @return a new URI resolver for the specified URI.
     * @throws XFormsException if a relative URI could not be resolved, if no
     * URI resolver is registered for the specified URI or any error occurred
     * during URI resolver creation.
     */
    public URIResolver createURIResolver(final String uri, final Element element) throws XFormsException {
        URI uriObj = getAbsoluteURI(uri, element);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("creating uri resolver for '" + uriObj + "'");
        }

        String className = Config.getInstance().getURIResolver(uriObj.getScheme());
        if (className == null) {
            throw new XFormsException("no uri resolver registered for '" + uri + "'");
        }

        Object instance = createInstance(className);
        if (!(instance instanceof URIResolver)) {
            throw new XFormsException("object instance of '" + className + "' is no uri resolver");
        }

        URIResolver uriResolver = (URIResolver) instance;
        uriResolver.setURI(uriObj.toString());
        uriResolver.setContext(getContext());

        return uriResolver;
    }

    /**
     * Creates a new object of the specifed class.
     *
     * @param className the class name.
     * @return a new object of the specifed class.
     * @throws XFormsException if any error occurred during object creation.
     */
    private Object createInstance(String className) throws XFormsException {
        try {
            Class clazz = Class.forName(className,true, DefaultConnectorFactory.class.getClassLoader());

            return clazz.newInstance();
        }
        catch (ClassNotFoundException cnfe) {
            throw new XFormsException(cnfe);
        }
        catch (InstantiationException ie) {
            throw new XFormsException(ie);
        }
        catch (IllegalAccessException iae) {
            throw new XFormsException(iae);
        }
    }

}

// end of class
