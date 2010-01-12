/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xslt.impl;

import de.betterform.xml.xforms.exception.XFormsException;

import javax.xml.transform.Source;
import java.io.InputStream;

/**
 * A file resource.
 *
 * @author Lars Windauer
 */
public class HttpResource implements Resource {
    private InputStream stream;
    private Source source;

    /**
     * Creates a new file resource.
     *
     * @param file the file.
     */
    public HttpResource(InputStream stream) {
        this.stream = stream;
    }

    public HttpResource(Source source) {
        this.source = source;
    }

    /**
     * Returns the last modified timestamp of this resource.
     *
     * @return the last modified timestamp of this resource.
     */
    public long lastModified() {
        return 0;
    }

    public InputStream getInputStream() throws XFormsException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Source getSource() throws XFormsException {
        return source;
    }
}