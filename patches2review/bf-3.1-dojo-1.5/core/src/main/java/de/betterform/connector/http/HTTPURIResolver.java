/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.connector.URIResolver;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * This class resolves <code>http</code> URIs. It treats the denoted
 * <code>http</code> resource as XML and returns the parsed response.
 * <p/>
 * If the specified URI contains a fragment part, the specified element
 * is looked up via the <code>getElementById</code>. Thus, the parsed
 * response must have an internal DTD subset specifiyng all ID attribute.
 * Otherwise the element would not be found.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: HTTPURIResolver.java 2873 2007-09-28 09:08:48Z lars $
 */
public class HTTPURIResolver extends AbstractHTTPConnector implements URIResolver {

    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(HTTPURIResolver.class);

    /**
     * Performs link traversal of the <code>http</code> URI and returns the result
     * as a DOM document.
     *
     * @return a DOM node parsed from the <code>http</code> URI.
     * @throws XFormsException if any error occurred during link traversal.
     */
    public Object resolve() throws XFormsException {
        URI uri = null;
        try {
            uri = new URI(getURI());
        } catch (URISyntaxException e) {
            throw new XFormsException(e);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getting '" + uri + "'");
        }

        get(getURIWithoutFragment());

        InputStream responseStream = getResponseBody();
        Map                 header = getResponseHeader();
        String         contentType = (String) header.get("Content-Type");
                       contentType = parseContentType(contentType);

        if (contentType.equalsIgnoreCase("text/plain") || contentType.equalsIgnoreCase("text/html")) {
            try {
                return inputStreamToString(responseStream);
            } catch (IOException e) {
                throw new XFormsException(e);
            }
        } else if (contentType.equalsIgnoreCase("application/xml") || contentType.equalsIgnoreCase("text/xml") || contentType.equalsIgnoreCase("application/xhtml+xml") ) {
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("converting response stream to XML");
                }
                return buildDocument(uri, responseStream);
            } catch (Exception e) {
                throw new XFormsException(e);
            }
        }else{
            LOGGER.warn("URI: " + uri + " couldn't be resolved");
            return null;
        }
    }

    private Object buildDocument(URI uri, InputStream responseStream) throws SAXException, IOException, ParserConfigurationException, XFormsException {
        return DOMUtil.getFragment(uri, responseStream);
    }


    private String inputStreamToString(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }

        bufferedReader.close();
        return stringBuilder.toString();
    }

    /**
     *
     * @param contentType is removed of encoding types when present
     * @return only the mime string without encoding type
     */
    private String parseContentType(String contentType)
    {
        int semicolonIndex = contentType.indexOf(';');
        if (semicolonIndex > 0) {
            contentType = contentType.substring(0, semicolonIndex);
        }
        return contentType;
    }
}

//end of class

