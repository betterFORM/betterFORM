/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xslt.impl;

import de.betterform.xml.xforms.exception.XFormsException;

import javax.xml.transform.Source;
import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a resolvable resource.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Resource.java 2922 2007-10-17 14:07:48Z lars $
 */
public interface Resource {

    /**
     * Returns the last modified timestamp of this resource.
     *
     * @return the last modified timestamp of this resource.
     */
    long lastModified();

    /**
     * Returns the input stream of this resource.
     *
     * @return the input stream of this resource.
     */
    InputStream getInputStream() throws XFormsException;
    Source getSource() throws XFormsException;
}
