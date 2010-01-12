/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.config;

import de.betterform.xml.xforms.exception.XFormsException;

/**
 * Signals configuration problems.
 *
 * @author Joern Turner, Ulrich Nicolas Liss&eacute;
 * @version $Id: XFormsConfigException.java 3495 2008-08-28 09:08:57Z joern $
 *
 * todo: should be renamed to simply 'ConfigException' as it has really not much to do with actual XForms processing.
 */
public class XFormsConfigException extends XFormsException {

    /**
     * Creates a new configuration exception.
     *
     * @param message the error message.
     */
    public XFormsConfigException(String message) {
        super(message);
        this.id = "configuration-exception";
    }

    /**
     * Creates a new configuration exception.
     *
     * @param cause the root cause.
     */
    public XFormsConfigException(Exception cause) {
        super(cause);
        this.id = "configuration-exception";
    }

    /**
     * Creates a new configuration exception.
     *
     * @param message the error message.
     * @param cause   the root cause.
     */
    public XFormsConfigException(String message, Exception cause) {
        super(message, cause);
        this.id = "configuration-exception";
    }
}

//end of class
