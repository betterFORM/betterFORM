/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms;

import de.betterform.xml.config.Config;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.xforms.XFormsModelElement;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * This is the superclass for all processors that integrate XFormsProcessorImpl in a certain environment like webapp,
 * applet or standalone. All Processors are decorators of XFormsProcessorImpl and inherit the common code for all processors
 * like the implementation of most wrapped methods including the EventListener interface. Therefore AbstractProcessorDecorator
 * is only designed for processors that use XML Events for communication between XFormsProcessor and environment.
 *
 * @author joern turner
 */
public abstract class AbstractProcessorDecorator implements XFormsProcessor, EventListener {
    protected XFormsProcessor xformsProcessor;
    protected EventTarget root;
    protected Config configuration;
    protected String locale = "en";

    public AbstractProcessorDecorator() {
        this.xformsProcessor = new XFormsProcessorImpl();
    }

    // ##### Event-related methods #####
    // ##### Event-related methods #####
    // ##### Event-related methods #####
    /**
     * check wether an Event is used in the form being processed. Will return true if any action registers
     * the Event in question. Will also return true if debug is enabled.
     *
     * @param eventName the event to check
     * @return true if event is used in form or if debug has been switched on
     */
    protected boolean isEventUsed(String eventName) {
        List eventsUsed = ((XFormsProcessorImpl) xformsProcessor).getEventList();
        if (isDebugOn() || eventsUsed.contains(eventName)) {
            return true;
        }
        return false;
    }

    /**
     * attach all DOM event listeners. It will be checked if the event in scope will be used
     * within the form. If not no listener will be attached.
     */
    protected void addEventListeners() throws XFormsException {
        // get docuent root as event target in order to capture all events
        this.root = (EventTarget) this.xformsProcessor.getXForms();

        // interaction events my occur during init so we have to register before
        if (isEventUsed(DOMEventNames.ACTIVATE)) {
            this.root.addEventListener(DOMEventNames.ACTIVATE, this, true);
        }
        if (isEventUsed(XFormsEventNames.BINDING_EXCEPTION)) {
            this.root.addEventListener(XFormsEventNames.BINDING_EXCEPTION, this, true);
        }
        if (isEventUsed(XFormsEventNames.COMPUTE_EXCEPTION)) {
            this.root.addEventListener(XFormsEventNames.COMPUTE_EXCEPTION, this, true);
        }
        /*
        this.root.addEventListener(XFormsEventNames.DISABLED, this, true);
        this.root.addEventListener(XFormsEventNames.ENABLED, this, true);
         */

        if (isEventUsed(XFormsEventNames.FOCUS)) {
            this.root.addEventListener(XFormsEventNames.FOCUS, this, false);
        }
        if (isEventUsed(DOMEventNames.FOCUS_IN)) {
            this.root.addEventListener(DOMEventNames.FOCUS_IN, this, true);
        }
        if (isEventUsed(DOMEventNames.FOCUS_OUT)) {
            this.root.addEventListener(DOMEventNames.FOCUS_OUT, this, true);
        }
        if (isEventUsed(XFormsEventNames.HELP)) {
            this.root.addEventListener(XFormsEventNames.HELP, this, true);
        }
        if (isEventUsed(XFormsEventNames.HINT)) {
            this.root.addEventListener(XFormsEventNames.HINT, this, true);
        }
        if (isEventUsed(XFormsEventNames.INVALID)) {
            this.root.addEventListener(XFormsEventNames.INVALID, this, true);
        }
        if (isEventUsed(XFormsEventNames.IN_RANGE)) {
            this.root.addEventListener(XFormsEventNames.IN_RANGE, this, true);
        }
        if (isEventUsed(XFormsEventNames.OUT_OF_RANGE)) {
            this.root.addEventListener(XFormsEventNames.OUT_OF_RANGE, this, true);
        }
        //betterform notification event must be passed always
        this.root.addEventListener(BetterFormEventNames.LOAD_URI, this, true);

        if (isEventUsed(XFormsEventNames.LINK_EXCEPTION)) {
            this.root.addEventListener(XFormsEventNames.LINK_EXCEPTION, this, true);
        }
        if (isEventUsed(XFormsEventNames.LINK_ERROR)) {
            this.root.addEventListener(XFormsEventNames.LINK_ERROR, this, true);
        }

        this.root.addEventListener(XFormsEventNames.MODEL_CONSTRUCT, this, true);
        this.root.addEventListener(XFormsEventNames.MODEL_CONSTRUCT_DONE, this, true);

        if (isEventUsed(XFormsEventNames.NEXT)) {
            this.root.addEventListener(XFormsEventNames.NEXT, this, true);
        }
        if (isEventUsed(XFormsEventNames.PREVIOUS)) {
            this.root.addEventListener(XFormsEventNames.PREVIOUS, this, true);
        }
        this.root.addEventListener(XFormsEventNames.READY, this, true);

        //betterform notification event must be passed always
        this.root.addEventListener(BetterFormEventNames.RENDER_MESSAGE, this, true);
        //betterform notification event must be passed always
        this.root.addEventListener(BetterFormEventNames.REPLACE_ALL, this, true);

        if (isEventUsed(XFormsEventNames.SUBMIT)) {
            this.root.addEventListener(XFormsEventNames.SUBMIT, this, true);
        }
        this.root.addEventListener(XFormsEventNames.SUBMIT_DONE, this, true);
        this.root.addEventListener(XFormsEventNames.SUBMIT_ERROR, this, true);

        if (isEventUsed(XFormsEventNames.VERSION_EXCEPTION)) {
            this.root.addEventListener(XFormsEventNames.VERSION_EXCEPTION, this, true);
        }
        if (isEventUsed(XFormsEventNames.VALUE_CHANGED)) {
            this.root.addEventListener(XFormsEventNames.VALUE_CHANGED, this, true);
        }
        if (isEventUsed(XFormsEventNames.VALID)) {
            this.root.addEventListener(XFormsEventNames.VALID, this, true);
        }
        if (isEventUsed(XFormsEventNames.SELECT)) {
            this.root.addEventListener(XFormsEventNames.SELECT, this, true);
        }
        if (isEventUsed(XFormsEventNames.DESELECT)) {
            this.root.addEventListener(XFormsEventNames.DESELECT, this, true);
        }
        if (isEventUsed(BetterFormEventNames.HIDE)) {
            this.root.addEventListener(BetterFormEventNames.HIDE, this, true);
        }
        if (isEventUsed(BetterFormEventNames.SHOW)) {
            this.root.addEventListener(BetterFormEventNames.SHOW, this, true);
        }

        if(isDebugOn()){
            this.root.addEventListener(BetterFormEventNames.INSTANCE_CREATED, this, true);
            this.root.addEventListener(BetterFormEventNames.MODEL_REMOVED, this, true);
        }

    }

    /**
     * remove all eventlisteners from XFormsProcessor. Removal will be tried for all event types regardless
     * if registered before or not. But as removeEventListener fails silently when no associated listener is found
     * this check is not necessary.
     */
    protected void removeEventListeners() {
        // deregister for interaction events if any
        if (this.root != null) {
            this.root.removeEventListener(DOMEventNames.ACTIVATE, this, true);
            this.root.removeEventListener(XFormsEventNames.BINDING_EXCEPTION, this, true);
            this.root.removeEventListener(XFormsEventNames.COMPUTE_EXCEPTION, this, true);
            /*
            this.root.removeEventListener(XFormsEventNames.DISABLED, this, true);
            this.root.removeEventListener(XFormsEventNames.ENABLED, this, true);
             */
            this.root.removeEventListener(XFormsEventNames.FOCUS, this, false);
            this.root.removeEventListener(DOMEventNames.FOCUS_IN, this, true);
            this.root.removeEventListener(DOMEventNames.FOCUS_OUT, this, true);
            this.root.removeEventListener(XFormsEventNames.HELP, this, true);
            this.root.removeEventListener(XFormsEventNames.HINT, this, true);
            this.root.removeEventListener(XFormsEventNames.INVALID, this, true);
            this.root.removeEventListener(XFormsEventNames.IN_RANGE, this, true);
            this.root.removeEventListener(XFormsEventNames.OUT_OF_RANGE, this, true);
            this.root.removeEventListener(BetterFormEventNames.LOAD_URI, this, true);
            this.root.removeEventListener(XFormsEventNames.LINK_EXCEPTION, this, true);
            this.root.removeEventListener(XFormsEventNames.LINK_ERROR, this, true);
            this.root.removeEventListener(XFormsEventNames.MODEL_CONSTRUCT, this, true);
            this.root.removeEventListener(XFormsEventNames.MODEL_CONSTRUCT_DONE, this, true);
            this.root.removeEventListener(XFormsEventNames.NEXT, this, true);
            this.root.removeEventListener(XFormsEventNames.PREVIOUS, this, true);
            this.root.removeEventListener(XFormsEventNames.READY, this, true);
            this.root.removeEventListener(BetterFormEventNames.RENDER_MESSAGE, this, true);
            this.root.removeEventListener(BetterFormEventNames.REPLACE_ALL, this, true);
            this.root.removeEventListener(XFormsEventNames.SUBMIT, this, true);
            this.root.removeEventListener(XFormsEventNames.SUBMIT_DONE, this, true);
            this.root.removeEventListener(XFormsEventNames.SUBMIT_ERROR, this, true);
            this.root.removeEventListener(XFormsEventNames.VALUE_CHANGED, this, true);
            this.root.removeEventListener(XFormsEventNames.VERSION_EXCEPTION, this, true);
            this.root.removeEventListener(XFormsEventNames.VALID, this, true);
            this.root.removeEventListener(XFormsEventNames.SELECT, this, true);
            this.root.removeEventListener(XFormsEventNames.DESELECT, this, true);
            this.root.removeEventListener(BetterFormEventNames.HIDE, this, true);
            this.root.removeEventListener(BetterFormEventNames.SHOW, this, true);
            this.root.removeEventListener(BetterFormEventNames.INSTANCE_CREATED, this, true);
            this.root.removeEventListener(BetterFormEventNames.MODEL_REMOVED, this, true);
            this.root = null;
        }
    }

    public abstract void handleEvent(Event event);


    // ##### XFormsProcessor implementation #####
    // ##### XFormsProcessor implementation #####
    // ##### XFormsProcessor implementation #####
    public void setLocale(String locale) throws XFormsException {
        this.xformsProcessor.setLocale(locale);
        this.locale = locale;
    }

    public abstract void init() throws XFormsException ;



    public void setXForms(Node node) throws XFormsException {
        this.xformsProcessor.setXForms(node);
    }

    public void setXForms(URI uri) throws XFormsException {
        this.xformsProcessor.setXForms(uri);
    }

    public void setXForms(InputStream inputStream) throws XFormsException {
        this.xformsProcessor.setXForms(inputStream);
    }

    public String getBaseURI() {
        return this.xformsProcessor.getBaseURI();
    }

    public void setXForms(InputSource inputSource) throws XFormsException {
        this.xformsProcessor.setXForms(inputSource);
    }

    /**
     * set the base URI used for resolution of relative URIs. Though the base URI can be determined from the
     * formURI in some cases it must be possible to set this explicitly cause forms may be loaded directly
     * via a stream or a Node and here no base URI can be determined.
     * <br/><br/>
     * The baseURI is essential for resolution of resource files such as Schemas, instance data, CSS-files and scripts.
     * Be sure to set it manually when working with dynamically generted files
     *
     * @param aURI the base URI of the processor as String
     */
    public void setBaseURI(String aURI) {
        this.xformsProcessor.setBaseURI(aURI);
    }

    public void setConfigPath(String s) throws XFormsException {
        this.xformsProcessor.setConfigPath(s);
    }

    public void setContext(Map map) {
        this.xformsProcessor.setContext(map);
    }

    public void setContextParam(String s, Object o) {
        this.xformsProcessor.setContextParam(s, o);
    }

    public Object getContextParam(String s) {
        return this.xformsProcessor.getContextParam(s);
    }

    public Object removeContextParam(String s) {
        return this.xformsProcessor.removeContextParam(s);
    }

    public Node getXForms() throws XFormsException {
        return this.xformsProcessor.getXForms();
    }

    /**
     * Returns the XForms Model Element with given id.
     *
     * @param id the id of the XForms Model Element.
     * @return the XForms Model Element with given id
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if no Model of given id can be found.
     */
    public XFormsModelElement getXFormsModel(String id) throws XFormsException {
        return this.xformsProcessor.getXFormsModel(id);
    }

    /**
     * terminates the XForms processing. right place to do cleanup of
     * resources.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *
     */
    public void shutdown() throws XFormsException {
        // shutdown processor
        if (this.xformsProcessor != null) {
            this.xformsProcessor.shutdown();
            this.xformsProcessor = null;
        }
        removeEventListeners();


    }

    protected boolean isDebugOn() {
        return configuration.getProperty("betterform.debug-allowed").equals("true");
    }


    public boolean dispatch(String id, String event) throws XFormsException {
        return this.xformsProcessor.dispatch(id, event);
    }

    /**
     * dispatches an Event to an Element specified by parameter 'id' and allows to set all events properties and
     * pass a context info
     *
     * @param targetId   the id identifying the Element to dispatch to
     * @param eventType  the type of Event to dispatch identified by a string
     * @param info       an implementation-specific context info object
     * @param bubbles    true if event bubbles
     * @param cancelable true if event is cancelable
     * @return <code>true</code> if the event has been cancelled during dispatch,
     *         otherwise <code>false</code>.
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *
     */
    public boolean dispatch(String targetId, String eventType, Object info, boolean bubbles, boolean cancelable) throws XFormsException {
        return this.xformsProcessor.dispatch(targetId, eventType, info, bubbles, cancelable);
    }

    public XFormsElement lookup(String s) {
        return this.xformsProcessor.lookup(s);
    }

    public void handleEventException(Exception e) {
        this.xformsProcessor.handleEventException(e);
    }

    public void setControlValue(String s, String s1) throws XFormsException {
        this.xformsProcessor.setControlValue(s, s1);
    }

    /**
     * This method updates the value of an upload control. Other controls cannot
     * be updated with this method.
     *
     * @param id        the id of the control.
     * @param mediatype the mediatype of the uploaded resource.
     * @param filename  the filename of the uploaded resource.
     * @param data      the uploaded data as byte array.
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if no document container is present, the control
     *          is unknown or an error occurred during value update.
     */
    public void setUploadValue(String id, String mediatype, String filename, byte[] data) throws XFormsException {
        this.xformsProcessor.setUploadValue(id, mediatype, filename, data);
    }

    /**
     * Checks wether the datatype of the specified form control is of the given
     * type.
     *
     * @param id   the id of the form control.
     * @param type the fully qualified type name.
     * @return <code>true</code> if the control's datatype equals to or is
     *         derived by restriction from the specified type, otherwise <code>false</code>.
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if no document container is present or the
     *          control is unknown.
     */
    public boolean isFileUpload(String id, String type) throws XFormsException {
        return this.xformsProcessor.isFileUpload(id, type);
    }

    public void setRepeatIndex(String s, int i) throws XFormsException {
        this.xformsProcessor.setRepeatIndex(s, i);
    }
}
