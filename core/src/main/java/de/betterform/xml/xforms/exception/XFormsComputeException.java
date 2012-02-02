/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.exception;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.XFormsEventNames;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;

/**
 * Signals an <code>xforms-compute-exception</code> error indication.
 *
 * @author Joern Turner, Ulrich Nicolas Liss&eacute;
 * @version $Id: XFormsComputeException.java 2090 2006-03-16 09:37:00Z joernt $
 */
public class XFormsComputeException extends XFormsErrorIndication {
    private static final String messagePre= XFormsEventNames.COMPUTE_EXCEPTION;

    /**
     * Creates a new <code>xforms-compute-exception</code> error indication.
     *
     * @param message the error message.
     * @param target  the event target.
     * @param info    the context information.
     */
    public XFormsComputeException(String message, EventTarget target, Object info) {
        this(message, null, target, info);
    }

    /**
     * Creates a new <code>xforms-compute-exception</code> error indication.
     *
     * @param message the error message.
     * @param cause   the root cause.
     * @param target  the event target.
     * @param info    the context information.
     */
    public XFormsComputeException(String message, Exception cause, EventTarget target, Object info) {
        super(messagePre + message  + XFormsErrorIndication.DEVIDER + DOMUtil.getCanonicalPath((Node) target), cause, target, info);
        this.id = XFormsEventNames.COMPUTE_EXCEPTION;
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
