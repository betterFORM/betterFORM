/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.exception;

import de.betterform.xml.dom.DOMUtil;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;

/**
 * Signals an <code>xforms-link-exception</code> error indication.
 *
 * @author Joern Turner, Ulrich Nicolas Liss&eacute;
 * @version $Id: XFormsLinkException.java 2090 2006-03-16 09:37:00Z joernt $
 */
public class XFormsLinkException extends XFormsErrorIndication {
    private static final String messagePre="xforms-link-exception: ";

    /**
     * Creates a new <code>xforms-link-exception</code> error indication.
     *
     * @param message the error message.
     * @param target  the event target.
     * @param info    the context information.
     */
    public XFormsLinkException(String message, EventTarget target, Object info) {
        this(message, null, target, info);
    }

    /**
     * Creates a new <code>xforms-link-exception</code> error indication.
     *
     * @param message the error message.
     * @param cause   the root cause.
     * @param target  the event target.
     * @param info    the context information.
     */
    public XFormsLinkException(String message, Exception cause, EventTarget target, Object info) {
        super(messagePre + message + XFormsErrorIndication.DEVIDER + DOMUtil.getCanonicalPath((Node) target), cause, target, info);
        this.id = "xforms-link-exception";
    }

    /**
     * Specifies wether this error indication is fatal or non-fatal.
     *
     * @return <code>true</code>.
     */
    public boolean isFatal() {
        return true;
    }
}

//end of class
