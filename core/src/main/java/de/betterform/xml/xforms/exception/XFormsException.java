/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.exception;

// todo: rename to BetterFormException and move up in the
/**
 * Base class of all betterForm exceptions.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XFormsException.java 2090 2006-03-16 09:37:00Z joernt $
 */
public class XFormsException extends Exception {

    /**
     * The internal id of this exception, used for i18n.
     */
    protected String id = "xforms-exception";

    /**
     * Creates a new xforms exception.
     *
     * @param message the error message.
     */
    public XFormsException(String message) {
        super(message);
    }

    /**
     * Creates a new xforms exception.
     *
     * @param cause the root cause.
     */
    public XFormsException(Exception cause) {
        super(cause);
    }

    /**
     * Creates a new xforms exception.
     *
     * @param message the error message.
     * @param cause the root cause.
     */
    public XFormsException(String message, Exception cause) {
        super(message, cause);
    }

    /**
     * Creates a new xforms exception.
     * <p/>
     * Appends the <code>subid</code> to the ID of this XFormsException.
     *
     * @param message the error message.
     * @param cause the root cause.
     * @param subid the message subid.
     */
    public XFormsException(String message, Exception cause, String subid) {
        super(message, cause);
        this.id = this.id + "." + subid;
    }

    /**
     * Get the message id.
     *
     * @return the value of the message id property
     */
    public String getId() {
        return this.id;
    }

}

// end of class
