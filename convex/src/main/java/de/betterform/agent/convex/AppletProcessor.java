/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.agent.convex;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.AbstractFormControl;
import de.betterform.xml.xforms.ui.Repeat;
import de.betterform.xml.xforms.ui.UIElementState;
import de.betterform.xml.xforms.ui.Upload;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.xforms.XFormsModelElement;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * AppletAdapter links a Java applet to the betterForm XForms processor. It's
 * responsible for steering the interaction with the processor and provides
 * methods for configuring the processor, loading forms and manage its
 * lifecycle.
 *
 * @author Joern Turner
 * @version $Id: AppletProcessor.java 3351 2008-07-19 19:54:12Z joern $
 */
public class AppletProcessor implements XFormsProcessor, EventListener {
    private static final Log LOG = LogFactory.getLog(AppletProcessor.class);

    private Convex convex;
    private ClassLoader contextClassLoader;
    private EventTarget root;
    private String uploadDir;
    private List responseSequence = new ArrayList();
    private List deferredSelectors = new ArrayList();
    private XFormsProcessor xformsProcessor;

    /**
     * Creates a new applet adapter.
     */
    public AppletProcessor() {
        // this should happen in init() and not earlier
        this.xformsProcessor = new XFormsProcessorImpl();
    }

    /**
     * Sets the context class loader.
     * <p/>
     * This is needed to ensure that the right classloader is used for parsing
     * operations.
     *
     * @param contextClassLoader the context class loader.
     */
    public void setContextClassLoader(ClassLoader contextClassLoader) {
        this.contextClassLoader = contextClassLoader;
    }

    /**
     * Sets the betterform applet.
     *
     * @param convex the betterform applet.
     */
    public void setBetterFormApplet(Convex convex) {
        this.convex = convex;
    }

    /**
     * Sets the upload directory.
     *
     * @param uploadDir the upload directory.
     */
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    /**
     * set the XForms to process. A complete host document embedding XForms syntax (e.g. html/xforms)
     * is expected as input.
     *
     * @param node a DOM Node containing the XForms
     */
    public void setXForms(Node node) throws XFormsException {
        this.xformsProcessor.setXForms(node);
    }

    /**
     * set the XForms to process. A complete host document embedding XForms syntax (e.g. html/xforms)
     * is expected as input.
     *
     * @param uri a URI pointing to the XForms
     */
    public void setXForms(URI uri) throws XFormsException {
        this.xformsProcessor.setXForms(uri);
    }

    /**
     * set the XForms to process. A complete host document embedding XForms syntax (e.g. html/xforms)
     * is expected as input.
     *
     * @param stream an InputStream containing the XForms
     */
    public void setXForms(InputStream stream) throws XFormsException {
        this.xformsProcessor.setXForms(stream);
    }

    public String getBaseURI() {
        return this.xformsProcessor.getBaseURI();
    }

    /**
     * set the XForms to process. A complete host document embedding XForms syntax (e.g. html/xforms)
     * is expected as input.
     *
     * @param source use an InputSource for the XForms
     */
    public void setXForms(InputSource source) throws XFormsException {
        this.xformsProcessor.setXForms(source);
    }

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
    public void setBaseURI(String aURI) {
        this.xformsProcessor.setBaseURI(aURI);
    }

    /**
     * sets the path to the config-file. This must be an absolute pathname to the file.
     * <p/>
     * todo: change to accept URI
     *
     * @param path the absolute path to the config-file
     */
    public void setConfigPath(String path) throws XFormsException {
        this.xformsProcessor.setConfigPath(path);
    }

    /**
     * passes Map containing arbitrary context parameters to the Adapter.
     *
     * @param contextParams Map of arbitrary params passed to the processor
     */
    public void setContext(Map contextParams) {
        this.xformsProcessor.setContext(contextParams);
    }

    /**
     * adds an object to the context of the processor.
     *
     * @param key    the reference key for the object
     * @param object the object to store
     */
    public void setContextParam(String key, Object object) {
        this.xformsProcessor.setContextParam(key, object);
    }

    /**
     * get an object from the context map of the processor
     *
     * @param key the key to the object
     * @return the object associated with given key or null
     */
    public Object getContextParam(String key) {
        return this.xformsProcessor.getContextParam(key);
    }

    /**
     * removes an entry in the contextmap of betterForm
     *
     * @param key the key denoting the entry to delete
     * @return the value of the deleted entry
     */
    public Object removeContextParam(String key) {
        return this.xformsProcessor.removeContextParam(key);
    }

    public void setLocale(String locale) throws XFormsException {
        //TODO tbd
    }

    public void init() throws XFormsException {
        try {
//            ensureContextClassLoader();
            addEventListeners();


            this.root = (EventTarget) this.xformsProcessor.getXForms();
//            this.root.addEventListener(EventFactory.BETTERFORM_MESSAGE, this, true);

            this.xformsProcessor.init();

            // register for notification events
            this.root.addEventListener(BetterFormEventNames.STATE_CHANGED, this, true);
            this.root.addEventListener(BetterFormEventNames.PROTOTYPE_CLONED, this, true);
            this.root.addEventListener(BetterFormEventNames.ID_GENERATED, this, true);
            this.root.addEventListener(BetterFormEventNames.ITEM_INSERTED, this, true);
            this.root.addEventListener(BetterFormEventNames.ITEM_DELETED, this, true);
            this.root.addEventListener(BetterFormEventNames.INDEX_CHANGED, this, true);
            this.root.addEventListener(BetterFormEventNames.SWITCH_TOGGLED, this, true);
            this.root.addEventListener(BetterFormEventNames.SCRIPT_ACTION, this, true);
            this.root.addEventListener(XFormsEventNames.FOCUS, this, true);
            this.root.addEventListener(DOMEventNames.ACTIVATE, this, true);

        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    /**
     * Returns the complete host document.
     *
     * @return the complete host document.
     */
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

    public void executeHandler() throws XFormsException {
        try {
            // [1] check for load request
//            String uri = (String) this.context.get(XFormsProcessor.LOAD_URI);
            String uri = (String) getContextParam(XFormsProcessor.LOAD_URI);

//            String target = (String) this.context.get(XFormsProcessor.LOAD_TARGET);
            String target = (String) getContextParam(XFormsProcessor.LOAD_TARGET);

//            this.context.remove(XFormsProcessor.LOAD_URI);
            removeContextParam(XFormsProcessor.LOAD_URI);

//            this.context.remove(XFormsProcessor.LOAD_TARGET);
            removeContextParam(XFormsProcessor.LOAD_TARGET);

            if (uri != null) {
                if (target != null && target.equals("new")) {
                    // load url in new window and *don't* terminate
                    this.convex.getAppletContext().showDocument(new URL(uri), "_blank");
                } else {
                    // load url in same window annd *do* terminate
                    this.convex.getAppletContext().showDocument(new URL(uri), "_self");
                    return;
                }
            }

            // todo: [2] check for submission response !!!
        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    public void shutdown() throws XFormsException {
        try {
            this.root.removeEventListener(XFormsEventNames.READONLY, this, true);
            this.root.removeEventListener(XFormsEventNames.READWRITE, this, true);
            this.root.removeEventListener(XFormsEventNames.OPTIONAL, this, true);
            this.root.removeEventListener(XFormsEventNames.REQUIRED, this, true);
            this.root.removeEventListener(XFormsEventNames.ENABLED, this, true);
            this.root.removeEventListener(XFormsEventNames.DISABLED, this, true);
            this.root.removeEventListener(XFormsEventNames.VALID, this, true);
            this.root.removeEventListener(XFormsEventNames.INVALID, this, true);
            this.root.removeEventListener(XFormsEventNames.SELECT, this, true);
            this.root.removeEventListener(XFormsEventNames.DESELECT, this, true);
            this.root.removeEventListener(BetterFormEventNames.STATE_CHANGED, this, true);
            this.root.removeEventListener(BetterFormEventNames.PROTOTYPE_CLONED, this, true);
            this.root.removeEventListener(BetterFormEventNames.ID_GENERATED, this, true);
            this.root.removeEventListener(BetterFormEventNames.ITEM_INSERTED, this, true);
            this.root.removeEventListener(BetterFormEventNames.ITEM_DELETED, this, true);
            this.root.removeEventListener(BetterFormEventNames.INDEX_CHANGED, this, true);
//            this.root.removeEventListener(BetterFormEventNames.REFRESH_DONE, this, true);
            this.root.removeEventListener(XFormsEventNames.FOCUS, this, true);

            this.xformsProcessor.shutdown();
            this.xformsProcessor = null;

//            this.root.removeEventListener(EventFactory.BETTERFORM_MESSAGE, this, true);
            this.root = null;

            System.gc();
        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    // implementation of EventListener

    /**
     * This method is called whenever an event occurs of the type for which the
     * <code>EventListener</code> interface was registered.
     *
     * @param event The <code>Event</code> contains contextual information about
     *              the event. It also contains the <code>stopPropagation</code> and
     *              <code>preventDefault</code> methods which are used in determining the
     *              event's flow and default action.
     */
    public void handleEvent(Event event) {
            if (event instanceof XMLEvent) {
                try {
                Object contextInfo = ((XMLEvent) event).getContextInfo();
                String eventType = event.getType();
                Element targetElement = (Element) event.getTarget();
                String targetId = targetElement.getAttribute("id");

                //System.out.println("handling event '" + eventType + "' occurring at target '" + targetId + "' with context [" + contextInfo + "]");
                XMLEvent xmlEvent = (XMLEvent) event;
                StringBuffer call = new StringBuffer();


    /*
                if (eventType.equals(EventFactory.BETTERFORM_MESSAGE)) {
                    String value = escape(contextInfo.toString());
                    // todo: Responder.renderModelessMessage
                    // todo: Responder.renderEphemeralMessage
                    call.append("Responder.renderModalMessage('").append(targetId).append("','").append(value).append("');");
                    if (this.responseSequence.size() > 0) {
                        this.responseSequence.add(call.toString());
                    }
                    else {
                        // may happen without refresh
                        this.convex.javascriptEval(call.toString());
                    }
                    return;
                }
    */
                if (eventType.equals(XFormsEventNames.SELECT)) {
                    if (targetElement.getLocalName().equals(XFormsConstants.CASE)) {
                        call.append("Responder.selectCase('").append(targetId).append("');");
                        if (this.responseSequence.size() > 0) {
                            this.responseSequence.add(call.toString());
                        } else {
                            // may happen without refresh
                            this.convex.javascriptEval(call.toString());
                        }
                    }
                    return;
                }
                if (eventType.equals(XFormsEventNames.DESELECT)) {
                    if (targetElement.getLocalName().equals(XFormsConstants.CASE)) {
                        call.append("Responder.deselectCase('").append(targetId).append("');");
                        if (this.responseSequence.size() > 0) {
                            this.responseSequence.add(call.toString());
                        } else {
                            // may happen without refresh
                            this.convex.javascriptEval(call.toString());
                        }
                    }
                    return;
                }
                if (eventType.equals(XFormsEventNames.VALID)) {
                    call.append("Responder.setElementValid('").append(targetId).append("');");
                    this.responseSequence.add(call.toString());
                    return;
                }
                if (eventType.equals(XFormsEventNames.INVALID)) {
                    call.append("Responder.setElementInvalid('").append(targetId).append("');");
                    this.responseSequence.add(call.toString());
                    return;
                }
                if (eventType.equals(XFormsEventNames.READONLY)) {
                    call.append("Responder.setElementReadonly('").append(targetId).append("');");
                    this.responseSequence.add(call.toString());
                    return;
                }
                if (eventType.equals(XFormsEventNames.READWRITE)) {
                    call.append("Responder.setElementReadwrite('").append(targetId).append("');");
                    this.responseSequence.add(call.toString());
                    return;
                }
                if (eventType.equals(XFormsEventNames.REQUIRED)) {
                    call.append("Responder.setElementRequired('").append(targetId).append("');");
                    this.responseSequence.add(call.toString());
                    return;
                }
                if (eventType.equals(XFormsEventNames.OPTIONAL)) {
                    call.append("Responder.setElementOptional('").append(targetId).append("');");
                    this.responseSequence.add(call.toString());
                    return;
                }
                if (eventType.equals(XFormsEventNames.ENABLED)) {
                    call.append("Responder.setElementEnabled('").append(targetId).append("');");
                    this.responseSequence.add(call.toString());
                    return;
                }
                if (eventType.equals(XFormsEventNames.DISABLED)) {
                    call.append("Responder.setElementDisabled('").append(targetId).append("');");
                    this.responseSequence.add(call.toString());
                    return;
                }
                if (eventType.equals(BetterFormEventNames.STATE_CHANGED)) {
                    this.convex.handleStateChanged(targetId,
                                                   (String) xmlEvent.getContextInfo(UIElementState.VALUE),
                                                   (String) xmlEvent.getContextInfo(UIElementState.VALID_PROPERTY),
                                                   (String) xmlEvent.getContextInfo(UIElementState.READONLY_PROPERTY),
                                                   (String) xmlEvent.getContextInfo(UIElementState.REQUIRED_PROPERTY),
                                                   (String) xmlEvent.getContextInfo(UIElementState.ENABLED_PROPERTY),
                                                   (String) xmlEvent.getContextInfo(UIElementState.TYPE_ATTRIBUTE ));

                    return;
                }
                if (eventType.equals(BetterFormEventNames.PROTOTYPE_CLONED)) {
                    String localName = targetElement.getLocalName();
                    if (XFormsConstants.ITEMSET.equals(localName)) {
                        call.append("Responder.cloneSelectorPrototype('").append(targetId).append("','").append(contextInfo).append("');");
                    } else {
                        call.append("Responder.cloneRepeatPrototype('").append(targetId).append("','").append(contextInfo).append("');");
                    }
                    this.responseSequence.add(call.toString());
                    return;
                }
                if (eventType.equals(BetterFormEventNames.ID_GENERATED)) {
                    call.append("Responder.setGeneratedId('").append(targetId).append("','").append(contextInfo).append("');");
                    this.responseSequence.add(call.toString());
                    return;
                }
                if (eventType.equals(BetterFormEventNames.ITEM_INSERTED)) {
                    String localName = targetElement.getLocalName();
                    if (XFormsConstants.ITEMSET.equals(localName)) {
                        call.append("Responder.insertSelectorItem('").append(targetId).append("','").append(contextInfo).append("');");
                    } else {
                        call.append("Responder.insertRepeatItem('").append(targetId).append("','").append(contextInfo).append("');");
                    }
                    this.responseSequence.add(call.toString());
                    return;
                }
                if (eventType.equals(BetterFormEventNames.ITEM_DELETED)) {
                    String localName = targetElement.getLocalName();
                    if (XFormsConstants.ITEMSET.equals(localName)) {
                        call.append("Responder.deleteSelectorItem('").append(targetId).append("','").append(contextInfo).append("');");
                    } else {
                        call.append("Responder.deleteRepeatItem('").append(targetId).append("','").append(contextInfo).append("');");
                    }
                    this.responseSequence.add(call.toString());
                    return;
                }
                if (eventType.equals(BetterFormEventNames.INDEX_CHANGED)) {
                    call.append("Responder.setRepeatIndex('").append(targetId).append("','").append(contextInfo).append("');");
                    this.responseSequence.add(call.toString());
                    return;
                }
    /*
                if (eventType.equals(BetterFormEventNames.BETTERFORM_REFRESH_DONE)) {
                    for (int i = 0; i < this.responseSequence.size(); i++) {
                        call.append(this.responseSequence.get(i));
                    }
                    for (int i = 0; i < this.deferredSelectors.size(); i++) {
                        call.append(this.deferredSelectors.get(i));
                    }
                    this.convex.javascriptEval(call.toString());
                    this.responseSequence.clear();
                    this.deferredSelectors.clear();
                    return;
                }
    */
                if (eventType.equals(XFormsEventNames.FOCUS)) {
                    // hack to get the real target id when target is repeated
                    String realId = this.xformsProcessor.lookup(targetId).getId();
                    call.append("Responder.setFocus('").append(realId).append("');");
                    this.responseSequence.add(call.toString());
                    return;
                }

                //System.out.println("unhandled event '" + eventType + "'");
            }
            catch (Exception e) {
                e.printStackTrace();
                this.xformsProcessor.handleEventException(e);
            }

        }
    }

    // update api for applet

    /**
     * Dispatches an event into the internal DOM.
     *
     * @param id        the id of the target element.
     * @param eventType the type of event to be fired.
     * @throws XFormsException if any error occurred during event dispatching.
     */
    public boolean dispatch(String id, String eventType) throws XFormsException {
        try {
            ensureContextClassLoader();
            return this.xformsProcessor.dispatch(id, eventType);
        }
        catch (Exception e) {
            throw new XFormsException("failed to dispatch event '" + eventType + "' to target '" + id + "'", e);
        }
    }

    public boolean dispatch(String targetId, String eventType, Object info, boolean bubbles, boolean cancelable) throws XFormsException {
        try {
            ensureContextClassLoader();
            return this.xformsProcessor.dispatch(targetId, eventType,info,bubbles,cancelable);
        }
        catch (Exception e) {
            throw new XFormsException("failed to dispatch event '" + eventType + "' to target '" + targetId + "'", e);
        }

    }

    /**
     * @param id of XFormsElement to receive
     * @return XFormsElement for given id
     */
    public XFormsElement lookup(String id) {
        return this.xformsProcessor.lookup(id);
    }

    public void handleEventException(Exception e) {
        this.xformsProcessor.handleEventException(e);
    }

    /**
     * This method updates the value of an UI control. Upload controls cannot
     * be updated with this method.
     * <p/>
     * The value will be changed regardless wether there was a change.
     * Applications have to call this method to propagate their UI value
     * changes to the betterForm processor. They should check for changes via
     * hasControlChanged() before using this method.
     *
     * @param id    the id of the control.
     * @param value the new value for the control.
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if no document container is present, the control
     *          is unknown or an error occurred during value update.
     */
    public void setControlValue(String id, String value) throws XFormsException {
        this.xformsProcessor.setControlValue(id, value);
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

    /**
     * Updates a control's value in the internal DOM.
     *
     * @param id    the id of the control.
     * @param value the new value of the control.
     * @throws XFormsException if any error occurred during value updating.
     */
    public void setValue(String id, String value) throws XFormsException {
        try {
            ensureContextClassLoader();

            AbstractFormControl element = (AbstractFormControl) this.xformsProcessor.lookup(id);
            if (element instanceof Upload) {
                setValue(id, value, "", "");
            } else {
                this.xformsProcessor.setControlValue(id, value);
            }
        }
        catch (Exception e) {
            throw new XFormsException("failed to update value at '" + id + "'", e);
        }
    }

    /**
     * Updates an upload's value in the internal DOM. <P> The destination
     * parameter will be interpeted relative to the form base and is used as
     * follows: <ol> <li>If it is <code>null</code> or empty, the file will be
     * uploaded to the form base.</li> <li>If it denotes a non-existing file, a
     * corresponding directory will be created and the file will be uploaded to
     * that directory.</li> <li>If it denotes an existing directory, the file
     * will be uploaded to that directory.</li> <li>If it denotes an existing
     * file, the existing file will be overwritten with the uploaded file. Thus,
     * the name of the uploaded file is dropped.</li> </ol> Caution: The latter
     * option is a potential security risk !
     *
     * @param id          the id of the upload control
     * @param value       the absolute name of the file to be uploaded
     * @param type        the optional file mediatype
     * @param destination the optional file destination
     * @throws XFormsException if any error occurred during file uploading.
     */
    public void setValue(String id, String value, String type, String destination) throws XFormsException {
        try {
            ensureContextClassLoader();

            File source = new File(value);
            String mediatype = type == null ? "" : type;
            String filename = source.getName();
                //System.out.println("upload: " + source + ", mediatype='" + mediatype + "'");

            if (this.xformsProcessor.isFileUpload(id, "anyURI")) {
                // get base uri
                URI baseURI = new URI((String) this.xformsProcessor.getContextParam(XFormsProcessor.BASE_URI));
                File baseFile = new File(baseURI);
                if (!baseFile.isDirectory()) {
                    baseURI = baseFile.getParentFile().toURI();
                }

                // get target
                File target;
                if (destination != null && destination.length() > 0) {
                    target = new File(baseURI.resolve(destination));
                } else {
                    target = new File(baseURI.resolve(this.uploadDir));
                }

                // check directory
                if (!target.exists()) {
                    target.mkdirs();
                }
                if (target.isDirectory()) {
                    target = new File(target.getAbsolutePath(), filename);
                }

                // copy file
                    //System.out.println("upload: copying to " + target);
                FileInputStream fis = new FileInputStream(source);
                FileOutputStream fos = new FileOutputStream(target);
                byte[] buf = new byte[1024];
                int i;
                while ((i = fis.read(buf)) != -1) {
                    fos.write(buf, 0, i);
                }
                fis.close();
                fos.close();

                // generate relative uri
                URI generated = baseURI.relativize(target.toURI());
                    //System.out.println("upload: generated uri " + generated);

                // update control
                this.xformsProcessor.setUploadValue(id, mediatype, filename, generated.toString().getBytes("UTF-8"));
            } else {
                // read binary data into memory
                byte[] data = new byte[(int) source.length()];
                    //System.out.println("upload: inlining " + data.length + " bytes");

                FileInputStream stream = new FileInputStream(source);
                stream.read(data);
                stream.close();

                // update control
                this.xformsProcessor.setUploadValue(id, mediatype, filename, data);
            }
        }
        catch (Exception e) {
            throw new XFormsException("failed to upload at '" + id + "'", e);
        }
    }

    /**
     * Updates a repeat's index.
     *
     * @param id    the id of the repeat.
     * @param index the new repeat index.
     * @throws XFormsException if any error occurred during repeat index
     *                         updating.
     */
    public void setRepeatIndex(String id, int index) throws XFormsException {
        try {
            ensureContextClassLoader();

            Repeat repeat = (Repeat) this.xformsProcessor.lookup(id);
            repeat.setIndex(index);
        }
        catch (Exception e) {
            throw new XFormsException("failed to set repeat index at '" + id + "'", e);
        }
    }

    // helper
    private void addEventListeners() throws XFormsException {
        // get docuent root as event target in order to capture all events
        this.root = (EventTarget) this.xformsProcessor.getXForms();

        // interaction events my occur during init so we have to register before
        this.root.addEventListener(BetterFormEventNames.LOAD_URI, this, true);
        this.root.addEventListener(BetterFormEventNames.RENDER_MESSAGE, this, true);
        this.root.addEventListener(BetterFormEventNames.REPLACE_ALL, this, true);
        this.root.addEventListener(XFormsEventNames.BINDING_EXCEPTION, this, true);
        this.root.addEventListener(XFormsEventNames.LINK_EXCEPTION, this, true);
        this.root.addEventListener(XFormsEventNames.LINK_ERROR, this, true);
        this.root.addEventListener(XFormsEventNames.MODEL_CONSTRUCT, this, true);
        this.root.addEventListener(XFormsEventNames.MODEL_CONSTRUCT_DONE, this, true);
        this.root.addEventListener(XFormsEventNames.READY, this, true);
        this.root.addEventListener(XFormsEventNames.SUBMIT, this, true);
        this.root.addEventListener(XFormsEventNames.SUBMIT_DONE, this, true);
        this.root.addEventListener(XFormsEventNames.SUBMIT_ERROR, this, true);
        this.root.addEventListener(XFormsEventNames.VERSION_EXCEPTION, this, true);
    }

    private void ensureContextClassLoader() {
        AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
                return null;
            }
        });
    }

    private String escape(String string) {
        if (string == null) {
            return string;
        }

        StringBuffer buffer = new StringBuffer(string.length());
        char c;
        for (int index = 0; index < string.length(); index++) {
            c = string.charAt(index);
            switch (c) {
                case '\n':
                    buffer.append('\\').append('n');
                    break;
                case '\r':
                    buffer.append('\\').append('r');
                    break;
                case '\t':
                    buffer.append('\\').append('t');
                    break;
                case '\'':
                    buffer.append('\\').append('\'');
                    break;
                case '\"':
                    buffer.append('\\').append('\"');
                    break;
                default:
                    buffer.append(c);
                    break;
            }
        }

        return buffer.toString();
    }
}

// end of class
