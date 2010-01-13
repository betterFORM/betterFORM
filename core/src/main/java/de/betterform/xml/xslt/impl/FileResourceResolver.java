/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xslt.impl;

import de.betterform.xml.xforms.exception.XFormsException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

/**
 * Resolves file resources.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: FileResourceResolver.java 2922 2007-10-17 14:07:48Z lars $
 */
public class FileResourceResolver implements ResourceResolver {

    /**
     * Resolves file resources.
     *
     * @param uri the URI denoting a resource.
     * @return the resource specified by the URI or <code>null</code> if this
     * resolver can't handle the URI.
     * @throws IOException if an error occurred during resolution.
     */
    public Resource resolve(URI uri) throws XFormsException {
        if (!uri.getScheme().equals("file")) {
            return null;
        }

        File file = new File(uri);
        if (!file.exists()) {
            throw new XFormsException(new FileNotFoundException(uri.toString()));
        }

        return new FileResource(file);
    }
}
