/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector;

import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods for connectors.
 *
 * @author Ulrich Nicolas Liss&eacute;
 */
public class ConnectorHelper {

    public static URI resolve(String baseURI, String connectorURI) throws URISyntaxException {
        return new URI(baseURI).resolve(new URI(connectorURI).getSchemeSpecificPart());
    }

    public static Map getURLParameters(URI uri) {
        if (uri == null) {
            return null;
        }

        String query = uri.getQuery();
        if (query == null) {
            return null;
        }

        Map parameters = new HashMap();
        String[] pairs = query.split("&");
        String[] tokens;

        for (int index = 0; index < pairs.length; index++) {
            tokens = pairs[index].split("=");
            parameters.put(tokens[0], tokens.length > 1 ? tokens[1] : "");
        }

        return parameters;
    }

    public static URI removeURLParameters(URI uri) throws URISyntaxException {
        String query = uri.getQuery();
        if (query == null) {
            return uri;
        }

        String string = uri.toString();
        return new URI(string.substring(0, string.indexOf('?')));
    }

    public static InputStream createInputStream(Node node) throws TransformerException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        TransformerFactory.newInstance().newTransformer().transform(new DOMSource(node), new StreamResult(stream));
        return new ByteArrayInputStream(stream.toByteArray());
    }

}
