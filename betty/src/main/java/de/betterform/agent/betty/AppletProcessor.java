/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.betty;

import de.betterform.xml.config.Config;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.xforms.AbstractProcessorDecorator;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.AbstractFormControl;
import de.betterform.xml.xforms.ui.Repeat;
import de.betterform.xml.xforms.ui.Upload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
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
public class AppletProcessor extends AbstractProcessorDecorator {
    private static final Log LOG = LogFactory.getLog(AppletProcessor.class);

    private Betty betty;
    private ClassLoader contextClassLoader;
//    private EventTarget root;
    private String uploadDir;
    private List responseSequence = new ArrayList();
    private List deferredSelectors = new ArrayList();
    private StringBuffer initialEventBuffer=new StringBuffer();
    private boolean isReady=false;
//    private XFormsProcessor xformsProcessor;

    /**
     * Creates a new applet adapter.
     */
    public AppletProcessor() {
        super();
    }

    public void setReady(){
        this.isReady=true;
        this.betty.javascriptEval(initialEventBuffer.toString());
        this.initialEventBuffer=null;
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
     * @param betty the betterform applet.
     */
    public void setBetterFormApplet(Betty betty) {
        this.betty = betty;
    }

    /**
     * Sets the upload directory.
     *
     * @param uploadDir the upload directory.
     */
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @Override
    protected boolean isEventUsed(String eventName) {
        return true;
    }

    public void init() throws XFormsException {
        try {
//            ensureContextClassLoader();
            addEventListeners();
            /*
            Eventlistener for the events below MUST be registered always and cannot be optimized as in WebProcessor.
            They are used to update the client state and are not intended for form authors.
             */
            this.root.addEventListener(BetterFormEventNames.STATE_CHANGED, this, true);
            this.root.addEventListener(BetterFormEventNames.PROTOTYPE_CLONED, this, true);
            this.root.addEventListener(BetterFormEventNames.ID_GENERATED, this, true);
            this.root.addEventListener(BetterFormEventNames.ITEM_INSERTED, this, true);
            this.root.addEventListener(BetterFormEventNames.ITEM_DELETED, this, true);
            this.root.addEventListener(BetterFormEventNames.INDEX_CHANGED, this, true);
            this.root.addEventListener(BetterFormEventNames.SWITCH_TOGGLED, this, true);
            this.root.addEventListener(BetterFormEventNames.SCRIPT_ACTION, this, true);
            this.root.addEventListener(BetterFormEventNames.AVT_CHANGED, this, true);

            this.configuration = Config.getInstance();

            this.xformsProcessor.init();
//            this.root = (EventTarget) this.xformsProcessor.getXForms();
        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    @Override
    protected boolean isDebugOn() {
        return true;//todo - configurable somehow
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
                    this.betty.getAppletContext().showDocument(new URL(uri), "_blank");
                } else {
                    // load url in same window annd *do* terminate
                    this.betty.getAppletContext().showDocument(new URL(uri), "_self");
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
        removeEventListeners();
        if (this.root != null) {
            this.root.removeEventListener(BetterFormEventNames.STATE_CHANGED, this, true);
            this.root.removeEventListener(BetterFormEventNames.PROTOTYPE_CLONED, this, true);
            this.root.removeEventListener(BetterFormEventNames.ID_GENERATED, this, true);
            this.root.removeEventListener(BetterFormEventNames.ITEM_INSERTED, this, true);
            this.root.removeEventListener(BetterFormEventNames.ITEM_DELETED, this, true);
            this.root.removeEventListener(BetterFormEventNames.INDEX_CHANGED, this, true);
            this.root.removeEventListener(BetterFormEventNames.SWITCH_TOGGLED, this, true);
            this.root.removeEventListener(BetterFormEventNames.SCRIPT_ACTION, this, true);
            this.root.removeEventListener(BetterFormEventNames.AVT_CHANGED, this, true);
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
        ensureContextClassLoader();

        if (event instanceof XMLEvent) {
            try {
//                Object contextInfo = ((XMLEvent) event).getContextInfo();
//                String eventType = event.getType();
                Element targetElement = (Element) event.getTarget();
                String targetId = targetElement.getAttribute("id");

                XMLEvent xmlEvent = (XMLEvent) event;
                LOG.debug("AppletProcessor handling " + event.getType().toString());
                StringBuffer eventToEvaluate = new StringBuffer("fluxProcessor.applyChanges('");
                //eventToEvaluate.append(event.getType());
                eventToEvaluate.append(targetId);
                eventToEvaluate.append("',[{");
                eventToEvaluate.append("eventType:'");
                eventToEvaluate.append(event.getType());
                eventToEvaluate.append("'");

                Map defaultInfo = (Map) xmlEvent.getContextInfo();
                if (defaultInfo != null && defaultInfo.size() != 0) {
                    LOG.info("adding contextInfo");
                    LOG.info("defaultInfo " + defaultInfo);

                    eventToEvaluate.append(",");
                    // build the list of contextInfo properties
                    Iterator it = defaultInfo.keySet().iterator();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        String value = "" + defaultInfo.get(key);
                        //append
                        if (key.contains("-")) {
                            key = key.replace("-", "_");
                        }
                        eventToEvaluate.append(key + ":'");
                        eventToEvaluate.append(value + "");
                        eventToEvaluate.append("'");
                        if (it.hasNext()) {
                            eventToEvaluate.append(",");
                        }

                    }
                }

                eventToEvaluate.append("}]);");
                LOG.debug("string to eval: " + eventToEvaluate.toString());

                if(isReady){
                    //pass for execution
                    this.betty.javascriptEval(eventToEvaluate.toString());
                }else{
                    this.initialEventBuffer.append(eventToEvaluate);
                }
            }
            catch (Exception
                    e) {
                e.printStackTrace();
                this.xformsProcessor.handleEventException(e);
            }

        }

    }
    /*
                if (eventType.equals(BetterFormEventNames.BETTERFORM_REFRESH_DONE)) {
                    for (int i = 0; i < this.responseSequence.size(); i++) {
                        call.append(this.responseSequence.get(i));
                    }
                    for (int i = 0; i < this.deferredSelectors.size(); i++) {
                        call.append(this.deferredSelectors.get(i));
                    }
                    this.betty.javascriptEval(call.toString());
                    this.responseSequence.clear();
                    this.deferredSelectors.clear();
                    return;
                }
    */

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
            return this.xformsProcessor.dispatch(targetId, eventType, info, bubbles, cancelable);
        }
        catch (Exception e) {
            throw new XFormsException("failed to dispatch event '" + eventType + "' to target '" + targetId + "'", e);
        }

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

    @Override
    protected boolean eventOptimizationIsDisabled() {
        return true;
    }
// helper

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
