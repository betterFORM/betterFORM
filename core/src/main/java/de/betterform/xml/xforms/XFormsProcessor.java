/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms;

import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Node;
import org.w3c.xforms.XFormsModelElement;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

/**
 * This is the basic interface for integrating the betterForm processor into
 * arbitrary java environments.
 * <p/>
 * XFormsProcessor implementations are responsible to handle the complete lifecycle
 * of the processor: initialization, interaction processing and shutdown of the processor.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XFormsProcessor.java 3493 2008-08-27 14:14:36Z joern $
 *
 */
public interface XFormsProcessor {
    public static final String BASE_URI = "betterform.baseURI";

    /**
     * Defines the key under which the submission response map may be accessed.
     * When a submission <code>replace="all"</code> happened, this map contains
     * the response of the submission target. There will be at least the
     * <code>SUBMISSION_RESPONSE_STREAM</code> property set.
     * <p/>
     * Additionally, there may be an arbitrary number of other properties set in
     * this map. In HTTP environments this could be the response headers.
     * @deprecated move back to de.betterform.xml.xforms.core.Submission
     */
    String SUBMISSION_RESPONSE = "betterform.submission.response";


        String SUBMISSION_RESPONSE_DOCUMENT = "betterform.submission.response.document";
    
    /**
     * Defines the key under which the submission reponse stream may be accessed
     * as a property of the <code>SUBMISSION_RESPONSE</code> map.
     * <p/>
     * In HTTP environments the XFormsProcessor implementation has to forward this
     * response to the user agent. Simply routing this response to the user
     * agent works for now, but is not completely right, since the user agent
     * stays connected to betterForm instead of the submission target. Maybe a smart
     * redirect/proxy combination could help to achieve this.
     * @deprecated move back to de.betterform.xml.xforms.core.Submission
     */
    String  SUBMISSION_RESPONSE_STREAM = "betterform.submission.response.stream";

    /**
     * Defines the key for an URI to be loaded. XFormsProcessor implementations are
     * free to choose any strategy to load the specified resource.
     * <p/>
     * In HTTP environments this would normally be a redirect.
     * @deprecated should not be used anymore
     */
    String LOAD_URI = "betterform.load.URI";

    /**
     * Defines the key for the target presentation context into which the URI
     * specified under <code>LOAD_URI</code> should be loaded. Possible values
     * are defined by the <code>load</code> action (currently
     * <code>replace</code> and <code>new</code>).
     * <p/>
     * In HTTP environments handling of <code>new</code> from the server side
     * might be achieved with some scripting only. For <code>replace</code> a
     * redirect should fit.
     * @deprecated should not be used anymore
     */
    String LOAD_TARGET = "betterform.load.target";

    /**
     * set the XForms to process. A complete host document embedding XForms syntax (e.g. html/xforms)
     * is expected as input.
     *
     * @param node a DOM Node containing the XForms
     */
    void setXForms(Node node) throws XFormsException;

    /**
     * set the XForms to process. A complete host document embedding XForms syntax (e.g. html/xforms)
     * is expected as input.
     *
     * @param uri a URI pointing to the XForms
     */
    void setXForms(URI uri) throws XFormsException;

    /**
     * set the XForms to process. A complete host document embedding XForms syntax (e.g. html/xforms)
     * is expected as input.
     *
     * @param stream an InputStream containing the XForms
     */
    void setXForms(InputStream stream) throws XFormsException;

    /**
     * returns the base URI that has been set for this processor instance
     * @return the base URI that has been set for this processor instance
     */
    String getBaseURI();

    /**
     * set the XForms to process. A complete host document embedding XForms syntax (e.g. html/xforms)
     * is expected as input.
     *
     * @param source use an InputSource for the XForms
     */
    void setXForms(InputSource source) throws XFormsException;


    /**
     * set the base URI used for resolution of relative URIs. Though the base URI can be determined from the
     * formURI in some cases it must be possible to set this explicitly cause forms may be loaded directly
     * via a stream or a Node and here no base URI can be determined.
     * <br/><br/>
     * The baseURI is essential for resolution of resource files such as Schemas, instance data, CSS-files and scripts.
     * Be sure to set it manually when working with dynamically generted files
     *
     * @param aURI the base URI of the processor
     */
    void setBaseURI(String aURI);

    /**
     * sets the path to the config-file. This must be an absolute pathname to the file.
     *
     * todo: change to accept URI
     * @param path the absolute path to the config-file
     */
    void setConfigPath(String path) throws XFormsException;

    /**
     * passes Map containing arbitrary context parameters to the Adapter.
     *
     * @param contextParams Map of arbitrary params passed to the processor
     */
    void setContext(Map contextParams);

    /**
     * adds an object to the context of the processor.
     *
     * @param key the reference key for the object
     * @param object the object to store
     */
    void setContextParam(String key, Object object);

    /**
     * get an object from the context map of the processor
     * @param key the key to the object
     * @return the object associated with given key or null
     */
    Object getContextParam(String key);

    /**
     * removes an entry in the contextmap of BetterForm
     *
     * @param key the key denoting the entry to delete
     * @return the value of the deleted entry
     */
    Object removeContextParam(String key);

    void setLocale(String locale) throws XFormsException;

    /**
     * Initialize the Adapter and thus the XForms Processor.
     *
     * @throws XFormsException if an error occurred during init.
     */
    void init() throws XFormsException;

    /**
     * Returns the complete host document.
     *
     * @return the complete host document.
     */
    Node getXForms() throws XFormsException;

    /**
     * Returns the XForms Model Element with given id.
     *
     * @param id the id of the XForms Model Element.
     * @return the XForms Model Element with given id
     * @throws XFormsException if no Model of given id can be found.
     */
    XFormsModelElement getXFormsModel(String id) throws XFormsException;

    /**
     * dispatches an Event to an Element specified by parameter 'id'.
     * @param id the id identifying the Element to dispatch to
     * @param event the type of Event to dispatch identified by a string
     * @return <code>true</code> if the event has been cancelled during dispatch,
     * otherwise <code>false</code>.
     * @throws XFormsException
     */
    boolean dispatch(String id, String event) throws XFormsException;

    /**
     * dispatches an Event to an Element specified by parameter 'id' and allows to set all events properties and
     * pass a context info
     *
     * @param targetId the id identifying the Element to dispatch to
     * @param eventType the type of Event to dispatch identified by a string
     * @param info an implementation-specific context info object
     * @param bubbles true if event bubbles
     * @param cancelable true if event is cancelable
     * @return <code>true</code> if the event has been cancelled during dispatch,
     * otherwise <code>false</code>.
     * @throws XFormsException
     */
    boolean dispatch(String targetId, String eventType, Object info, boolean bubbles, boolean cancelable)
            throws XFormsException;

    /**
     *
      * @param id of XFormsElement to receive
     * @return XFormsElement for given id
     */
    XFormsElement lookup(String id);

    void handleEventException(Exception e);

    /**
     * This method updates the value of an UI control. Upload controls cannot
     * be updated with this method.
     * <p/>
     * The value will be changed regardless wether there was a change.
     * Applications have to call this method to propagate their UI value
     * changes to the betterForm processor. They should check for changes via
     * hasControlChanged() before using this method.
     *
     * @param id the id of the control.
     * @param value the new value for the control.
     * @throws de.betterform.xml.xforms.exception.XFormsException if no document container is present, the control
     * is unknown or an error occurred during value update.
     */
    void setControlValue(String id, String value) throws XFormsException;

    /**
     * This method updates the value of an upload control. Other controls cannot
     * be updated with this method.
     *
     * @param id the id of the control.
     * @param mediatype the mediatype of the uploaded resource.
     * @param filename the filename of the uploaded resource.
     * @param data the uploaded data as byte array.
     * @throws de.betterform.xml.xforms.exception.XFormsException if no document container is present, the control
     * is unknown or an error occurred during value update.
     */
    void setUploadValue(String id, String mediatype, String filename, byte[] data) throws XFormsException;

    /**
     * Checks wether the datatype of the specified form control is of the given
     * type.
     *
     * @param id the id of the form control.
     * @param type the fully qualified type name.
     * @return <code>true</code> if the control's datatype equals to or is
     * derived by restriction from the specified type, otherwise <code>false</code>.
     * @throws de.betterform.xml.xforms.exception.XFormsException if no document container is present or the
     * control is unknown.
     */
    boolean isFileUpload(String id,String type) throws XFormsException;


    /**
     * Sets the specified Repeat's index.
     *
     * @param id the repeat id.
     * @param index the repeat index.
     * @throws de.betterform.xml.xforms.exception.XFormsException if no document container is present, the repeat
     * is unkown or an error occurred during index update.
     */
    void setRepeatIndex(String id, int index) throws XFormsException;

    /**
     * Terminates the XForms processing. Should perform resource cleanup.
     *
     * @throws XFormsException if an error occurred during shutdown.
     */
    void shutdown() throws XFormsException;



}
