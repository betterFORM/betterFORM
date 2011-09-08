/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.exception;

import org.w3c.dom.events.EventTarget;

// todo: Maybe this class should be called XFormsException and the class named
// XFormsException today should be called BetterFormException ? Or should this
// class and XFormsException melt together ?

/**
 * Base class for xforms error indications.
 * <p/>
 * Error indications are the events defined in [4.5 Error Indications]
 * as well as [4.4.19 The xforms-submit-error Event].
 * <p/>
 * Exceptions of this type are intended to be used in internal processing
 * only. They are designed to transport an error indication relevant for
 * event handling from its origin to the top level event handling or default
 * action routine. Once the corresponding error indications has been dispatched
 * as an event into the DOM, the exception has to be marked <code>handled</code>.
 * Then, the expception can be rethrown, but the event target and the context
 * information are not available anymore. This is both to ensure the event is only
 * dispatched once and to release the associated event target.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XFormsErrorIndication.java 3253 2008-07-08 09:26:40Z lasse $
 */
public abstract class XFormsErrorIndication extends XFormsException {
    public static String DEVIDER = "::";
    /**
     * The exception state.
     */
    private boolean handled = false;

    /**
     * The event target.
     */
    private EventTarget target = null;

    /**
     * The context information.
     */
    private Object info = null;

    /**
     * Creates a new xforms error indication.
     *
     * @param message the error message.
     * @param cause   the root cause.
     * @param target  the event target.
     * @param info    the context information.
     */
    public XFormsErrorIndication(String message, Exception cause, EventTarget target, Object info) {
        super(message, cause);
        this.target = target;
        this.info = info;
    }

    /**
     * Specifies wether this error indication is fatal or non-fatal.
     *
     * @return <code>true</code> if this error indication is fatal,
     *         otherwise <code>false</code>.
     */
    public abstract boolean isFatal();

    /**
     * Checks wether this error indication is handled or not.
     *
     * @return <code>true</code> if this error indication is handled,
     *         otherwise <code>false</code>.
     */
    public final boolean isHandled() {
        return this.handled;
    }

    /**
     * Sets the error indication state to <code>handled</code>.
     */
    public final void setHandled() {
        this.handled = true;
        this.target = null;
//        this.info = null;
    }

    /**
     * Returns the event type of this error indication.
     *
     * @return the event type of this error indication.
     */
    public final String getEventType() {
        return this.id;
    }

    /**
     * Returns the event target of this error indication.
     *
     * @return the event target of this error indication.
     */
    public final EventTarget getEventTarget() {
        return this.target;
    }

    /**
     * Returns the context information of this error indication.
     *
     * @return the context information of this error indication.
     */
    public final Object getContextInfo() {
        return this.info;
    }

}

//end of class
