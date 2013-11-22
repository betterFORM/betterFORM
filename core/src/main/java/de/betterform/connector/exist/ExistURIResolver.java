/*
 * Copyright (c) 2013. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.exist;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.betterform.connector.URIResolver;
import de.betterform.connector.http.AbstractHTTPConnector;
import de.betterform.connector.util.URIUtil;
import de.betterform.xml.dom.DOMUtil;

/**
 * This resolver is eXistdb-specific and allows resolving URIs when directly running within eXist. It does
 * not need to use a network and will retrieve XML directly from eXistdb via (XMLDB ?) API.
 *
 *
 */
public class ExistURIResolver extends AbstractHTTPConnector implements URIResolver {

    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(ExistURIResolver.class);

    /**
     *
     * @return a DOM node or document from the URI resolution
     */
    @SuppressWarnings("unchecked")
    public Object resolve() {
      String uriString = getURI();
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("resolving uri '" + uriString + "'");
      }
      
      try {
        String s;
        if(URIUtil.hasFileExtension(uriString, ExistUtil.EXTENSION_XQUERY, ExistUtil.EXTENSION_XQUERY_MODULE)) {
          s = ExistUtil.executeXQuery(uriString, getContext(), null, null, null);
        } else {
          s = ExistUtil.getExistResource(uriString, getContext(),  null, null, null);
        }
        return DOMUtil.parseString(s, true, true);
      } catch (Exception e) {
        // TODO bad idea?
        return null;
      }
    }

}
