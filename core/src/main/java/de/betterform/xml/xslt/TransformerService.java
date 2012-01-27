/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xslt;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.net.URI;

/**
 * TransformerService provides a simple URI-based transformer lookup. Clients
 * should set the TransformerFactory because otherwise an implementation might
 * choose to use JAXP to get a TransformerFactory instance which is inherently
 * slow.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: TransformerService.java 3240 2008-07-02 10:02:39Z lasse $
 */
public interface TransformerService {
    public static final String TRANSFORMER_SERVICE = TransformerService.class.getName();
    /**
     * Returns the transformer factory.
     *
     * @return the transformer factory.
     */
    TransformerFactory getTransformerFactory() throws TransformerConfigurationException;

    /**
     * Sets the transformer factory.
     *
     * @param factory the transformer factory.
     */
    void setTransformerFactory(TransformerFactory factory) throws TransformerException;

    /**
     * Returns an identity transformer.
     *
     * @return an identity transformer.
     * @throws TransformerException if the transformer couldn't be created.
     */
    Transformer getTransformer() throws TransformerException;

    /**
     * Returns a transformer for the specified stylesheet.
     * <p/>
     * If the URI is null, an identity transformer is created.
     *
     * @param uri the URI identifying the stylesheet.
     * @return a transformer for the specified stylesheet.
     * @throws TransformerException if the transformer couldn't be created.
     */
    Transformer getTransformer(URI uri) throws TransformerException;
}
