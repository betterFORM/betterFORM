/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xslt.impl;

import de.betterform.xml.xforms.exception.XFormsException;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;

/**
 * A file resource.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: FileResource.java 2961 2007-10-26 14:20:08Z lars $
 */
public class FileResource implements Resource {
    private File file;

    /**
     * Creates a new file resource.
     *
     * @param file the file.
     */
    public FileResource(File file) {
        this.file = file;
    }

    /**
     * Returns the last modified timestamp of this resource.
     *
     * @return the last modified timestamp of this resource.
     */
    public long lastModified() {
        return this.file.lastModified();
    }

    /**
     * Returns the input stream of this resource.
     *
     * @return the input stream of this resource.
     */
    public InputStream getInputStream() throws XFormsException {
        try {
            return new FileInputStream(this.file);
        } catch (FileNotFoundException e) {
            throw new XFormsException(e);
        }
    }
    public Source getSource() throws XFormsException {
        return new StreamSource(file);
    }
}
