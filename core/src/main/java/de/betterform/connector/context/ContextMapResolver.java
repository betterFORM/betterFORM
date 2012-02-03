/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.connector.context;

import de.betterform.connector.AbstractConnector;
import de.betterform.connector.URIResolver;
import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Node;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * This Resolver retrieves java objects from the betterformContext map. it uses a opaque URI notation like this:
 * context:KEY1
 * where 'KEY1' is the key under which the java object is stored in the context map.
 *
 * @author joern turner
 */

public class ContextMapResolver extends AbstractConnector implements URIResolver {

    public ContextMapResolver() {
    }

    /**
     * will return the Node for the specified key. The map entry will be removed from context by this operation.
     *
     * @return a java object representing the link traversal result.
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if any error occurred during link traversal.
     */
    public Object resolve() throws XFormsException {

        String uriString = getURI();
        String key = getContextKeyFormURI(uriString);
        return getNodeFromContext(key, getContext());
    }

    /**
     * fetches a DOM Node from the contextmap of the XFormsProcessor and removes the entry from the map.
     *
     * @param key the key under which the Node is stored in the context map
     * @param contextMap the context map of the XFormsProcessor
     * @return a Node as an Object
     * @throws XFormsException
     */
    public static Object getNodeFromContext(String key, Map contextMap) throws XFormsException {
        if (contextMap.containsKey(key)) {
            Object o = contextMap.remove(key);
            if (!(o instanceof Node)) {
                throw new XFormsException("Object stored under key'" + key + "' must be a DOM Node");
            }
            return o;
        } else {
            throw new XFormsException("Cannot resolve context-param '" + key + "'");
        }
    }

    /**
     * extracts a key name from an URI string
     * @param uriString the URI given as a string
     * @return the key name extracted from the URI
     * @throws XFormsException
     */
    public static String getContextKeyFormURI(String uriString) throws XFormsException {
        URI uri;
        try {
            uri = new URI(uriString);
        } catch (URISyntaxException e) {
            throw new XFormsException("invalid URI syntax: '" + uriString +"'", e);
        }
        return uri.getSchemeSpecificPart();
    }

}
