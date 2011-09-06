/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xslt.impl;

import de.betterform.xml.xforms.exception.XFormsException;

import java.io.IOException;
import java.net.URI;

/**
 * Resolves resources by URIs.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ResourceResolver.java 2922 2007-10-17 14:07:48Z lars $
 */
public interface ResourceResolver {

    /**
     * Resolves the resource specified by the URI.
     * <p/>
     * If this resolver can't handle the URI it returns <code>null</code>.
     *
     * @param uri the URI denoting a resource.
     * @return the resource specified by the URI or <code>null</code> if this
     * resolver can't handle the URI.
     * @throws IOException if an error occurred during resolution.
     */
    Resource resolve(URI uri) throws XFormsException;
}
