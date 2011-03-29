/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xslt.impl;

import de.betterform.xml.xforms.exception.XFormsException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resolves file resources from classpath
 *
 * @author Joern Turner
 */
public class ClasspathResourceResolver implements ResourceResolver {
    private static final Logger logger = Logger.getLogger(ClasspathResourceResolver.class.getName());

    private static final String RESOURCE_PATH = "/META-INF/resources/xslt/";
    private String realPath;

    public ClasspathResourceResolver(String realPath){
        this.realPath = realPath;
    }
    /**
     *
     * @param uri the URI denoting a resource.
     * @return the resource specified by the URI or <code>null</code> if this
     * resolver can't handle the URI.
     * @throws java.io.IOException if an error occurred during resolution.
     */
    public Resource resolve(URI uri) throws XFormsException {
        if (!uri.toString().endsWith(".xsl")) {
            return null;
        }
        String uriString = uri.toString();
        String filename = uriString.substring(uriString.lastIndexOf("/")+ 1);
        if (logger.isLoggable(Level.INFO)){
            logger.log(Level.INFO,"file: " + filename + " loaded from classpath");
        }

        InputStream inputStream = ClasspathResourceResolver.class.getResourceAsStream(RESOURCE_PATH + filename);
        return new ClassPathResource(inputStream);
    }
}
