/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms;

import de.betterform.connector.ConnectorFactory;
import de.betterform.session.DefaultSerializer;
import de.betterform.xml.config.Config;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEventService;
import de.betterform.xml.events.impl.DefaultXMLEventInitializer;
import de.betterform.xml.events.impl.DefaultXMLEventService;
import de.betterform.xml.events.impl.XercesXMLEventFactory;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.ui.AbstractFormControl;
import de.betterform.xml.xforms.ui.Repeat;
import de.betterform.xml.xforms.ui.Upload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.xforms.XFormsModelElement;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * This class is an implementation of a W3C XForms 1.0 conformant
 * XForms processor.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XFormsProcessorImpl.java 3492 2008-08-27 12:37:01Z joern $
 */
public class XFormsProcessorImpl implements XFormsProcessor, Externalizable{
    public static final String BETTERFORM_LOCALE = "betterform.locale";
    public static final String BETTERFORM_ENABLE_L10N = "enable.l10n";
    private static final Log LOGGER = LogFactory.getLog(XFormsProcessorImpl.class);
    private static String APP_INFO = null;

    private static final long serialVersionUID = 1L;

    /**
     * The document container object model.
     */
    private Container container = null;

    /**
     * The base URI for resolution of relative URIs.
     */
    private String baseURI = null;

    /**
     * The context map which stores application-specific parameters.
     */
    private Map context = null;

    /**
     * List to hold events that are used by the form author
     */
    private ArrayList eventList;

    /**
     * returns a list of events used by the form author. This list is build during init process in
     * AbstractAction.addListener.
     *
     * @return a list of events used by the form author
     */
    public ArrayList getEventList() {
        return eventList;
    }

    /**
     * Creates a new XFormsProcessorImpl object.
     */
    public XFormsProcessorImpl() {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug(getAppInfo());
        }
        this.context = new HashMap(10);
        this.context.put("versionInfo",getAppInfo());
        this.eventList = new ArrayList();
    }

    /**
     * Returns betterForm version string.
     *
     * @return betterForm version string.
     */
    public static String getAppInfo() {
        synchronized (XFormsProcessorImpl.class) {
            if (APP_INFO == null) {
                try {
                    BufferedInputStream stream = new BufferedInputStream(XFormsProcessorImpl.class.getResourceAsStream("/META-INF/version.info"));
                    StringBuffer buffer = new StringBuffer();
                    int c;

                    while ((c = stream.read()) > -1) {
                        if (c != 10 && c != 13) {
                            buffer.append((char) c);
                        }
                    }
                    stream.close();
                    APP_INFO = buffer.toString();
                } catch (IOException e) {
                    APP_INFO = "betterFORM";
                }
            }
            return APP_INFO;
        }
    }

    /**
     * Sets the config path.
     * <p/>
     * Checks existence of the config path and creates a config instance.
     *
     * todo: change to accept an URI
     * @param path the absolute path to the config file.
     */
    public void setConfigPath(String path) throws XFormsException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("set config: " + path);
        }

        if ((path == null) || (new File(path).exists() == false)) {
            throw new XFormsConfigException("path not found: " + path);
        }

        Config.getInstance(path);
    }

    /**
     * Sets the base URI.
     * <p/>
     * The base URI is used for resolution of relative URIs occurring in the
     * document, e.g. instance sources or submission actions.
     *
     * @param uri the base URI.
     */
    public void setBaseURI(String uri) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("set base uri: " + uri);
        }

        this.context.put(XFormsProcessor.BASE_URI,uri);
        this.baseURI = uri;
    }

    /**
     * Returns the base URI.
     *
     * @return the base URI.
     * @see #setBaseURI(String)
     */
    public String getBaseURI() {
        return this.baseURI;
    }

    /**
     * Allows to set a context map for storing application-specific parameters.
     *
     * @param context the context map to use.
     */
    public void setContext(Map context) {
        this.context = context;
    }

    /**
     * Returns the context map which stores application-specific parameters.
     *
     * @return the context map which stores application-specific parameters.
     */
    public Map getContext() {
        if (this.context == null) {
            this.context = new HashMap();
        }
        return this.context;
    }

    /**
     * get an object from the context map of the processor
     *
     * @param key the key to the object
     * @return the object associated with given key or null
     */
    public Object getContextParam(String key) {
        return getContext().get(key);
    }

    /**
     * removes an entry in the contextmap of BetterForm
     *
     * @param key the key denoting the entry to delete
     * @return the value of the deleted entry
     */
    public Object removeContextParam(String key) {
        return getContext().remove(key);
    }

    /**
     * adds an object to the context of the processor.
     *
     * @param key    the reference key for the object
     * @param object the object to store
     */
    public void setContextParam(String key, Object object) {
        getContext().put(key,object);
    }

    /**
     * Returns the document container associated with this processor.
     *
     * @return the document container associated with this processor.
     */
    public Container getContainer() {
        return this.container;
    }

    /**
     * Sets the containing document.
     * <p/>
     * A new document container is created.
     *
     * @param node Either the containing document as DOM Document or the root of
     * a DOM (sub)tree as DOM Element.
     * @throws XFormsException if the document container could not be created.
     */
    public void setXForms(Node node) throws XFormsException {
        ensureContainerNotInitialized();

        Document document = toDocument(node);
        createContainer().setDocument(document);
    }

    /**
     * Sets the containing document.
     * <p/>
     * A new document container is created.
     *
     * @param uri the containing document URI.
     * @throws XFormsException if the document container could not be created.
     */
    public void setXForms(URI uri) throws XFormsException {
        ensureContainerNotInitialized();

        // todo: refactor / fix uri resolution in connector factory to work without an init'd processor
        String absoluteURI = resolve(uri);
        ConnectorFactory connectorFactory = ConnectorFactory.getFactory();
        connectorFactory.setContext(getContext());
        Node node = (Node) connectorFactory.createURIResolver(absoluteURI, null).resolve();

        Document document = toDocument(node);
        createContainer().setDocument(document);
    }

    /**
     * Sets the containing document.
     * <p/>
     * A new document container is created.
     *
     * @param stream the containing document as input stream.
     * @throws XFormsException if the document container could not be created.
     */
    public void setXForms(InputStream stream) throws XFormsException {
        ensureContainerNotInitialized();

        Document document;
        try {
            document = getDocumentBuilder().parse(stream);
        }
        catch (Exception e) {
            throw new XFormsException("could not create document container", e);
        }

        createContainer().setDocument(document);
    }

    /**
     * Sets the containing document.
     * <p/>
     * A new document container is created.
     *
     * @param source the containing document as input source.
     * @throws XFormsException if the document container could not be created.
     */
    public void setXForms(InputSource source) throws XFormsException {
        ensureContainerNotInitialized();

        Document document;
        try {

            document = getDocumentBuilder().parse(source);
        }
        catch (Exception e) {
            throw new XFormsException("could not create document container", e);
        }

        createContainer().setDocument(document);
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
        ensureContainerPresent();
        return this.container.getModel(id);
    }

    /**
     * Returns the containing document as DOM.
     * <p/>
     * This returns the live DOM processed by betterForm internally. Changes will
     * affect internal state and may cause malfunction. Should we better be more
     * restrictive and return a clone to prevent this ?
     *
     * @return the containing document.
     * @throws XFormsException if no document container is present.
     */
    public Document getXForms() throws XFormsException {
        ensureContainerPresent();

        return this.container.getDocument();
    }

    /**
     * returns the DOM Document of the XForms Instance identified by id.
     * @param id identifier for Instance
     * @return the DOM Document of the XForms Instance identified by id
     * @throws DOMException in case the processor was not intialized or the wanted Instance does not exist
     */
    public Document getInstanceDocument(String id) throws DOMException {
        try {
            ensureContainerInitialized();
        } catch (XFormsException e) {
            throw new DOMException(DOMException.INVALID_STATE_ERR,"Processor is not intialized");
        }

        List models = container.getModels();

        Document instance=null;
        for (int i = 0; i < models.size(); i++) {
            Model model = (Model) models.get(i);
            instance = model.getInstanceDocument(id);
            if (instance != null){
                return instance;
            }
        }
        throw new DOMException(DOMException.NOT_FOUND_ERR,"Instance with id: '" + id + "' not found");
    }

    /**
     * Bootstraps processor initialization.
     * <p/>
     * Use this method after setXForms() and (optionally)
     * setInstanceData() have been called to actually start the processing.
     *
     * @throws XFormsException if no document container is present or an error
     * occurred during init.
     */
    public void init() throws XFormsException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("init");
        }

        ensureContainerPresent();
        ensureContainerNotInitialized();
        if(!getContext().containsKey(BETTERFORM_LOCALE)){
            setLocale();
        }
        this.container.init();
    }

    /**
     * Dispatches an event of the given type to the specified target.
     *
     * @param id the id of the event target.
     * @param event the event type.
     * @return <code>true</code> if the event has been cancelled during dispatch,
     * otherwise <code>false</code>.
     * @throws XFormsException if no document container is present or an error
     * occurred during dispatch.
     */
    public boolean dispatch(String id, String event) throws XFormsException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("dispatch: id: " + id + ", event: " + event);
        }

        ensureContainerPresent();
        ensureContainerInitialized();

        return this.container.dispatch(id, event);
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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("dispatch: id: " + targetId + ", event: " + eventType + ", contextinfo: " + info.toString());
        }

        ensureContainerPresent();
        ensureContainerInitialized();
        return this.container.dispatch(targetId,eventType,info,bubbles,cancelable);
    }

    public XFormsElement lookup(String id) {
        return this.container.lookup(id);
    }

    public void handleEventException(Exception e) {
        this.container.handleEventException(e);
    }

    /**
     * Checks wether the value of the specified form control might have changed.
     *
     * @param id the id of the form control.
     * @param value the value to check.
     * @return <code>true</code> if the given value differs from the specified
     * control's value, otherwise <code>false</code>.
     * @throws XFormsException if no document container is present or the
     * control is unknown.
     * @deprecated called by nobody
     */

    public final boolean hasControlChanged(String id, String value) throws XFormsException {
        ensureContainerPresent();
        ensureContainerInitialized();

        // sanity checks
        XFormsElement element = this.container.lookup(id);
        if (element == null || !(element instanceof AbstractFormControl)) {
            throw new XFormsException("id '" + id + "' does not identify a form control");
        }

        // check control value
        AbstractFormControl control = (AbstractFormControl) element;
        Object controlValue = control.getValue();
        if(controlValue == null) {
            // prevents controls being not bound or disabled from updates
            return false;
        }
        //todo: rather doubt that this equality check works correct
        return !controlValue.equals(value);
    }

    /**
     * Checks wether the datatype of the specified form control is of the given
     * type.
     *
     * @param id the id of the form control.
     * @param type the fully qualified type name.
     * @return <code>true</code> if the control's datatype equals to or is
     * derived by restriction from the specified type, otherwise <code>false</code>.
     * @throws XFormsException if no document container is present or the
     * control is unknown.
     */
    public final boolean isFileUpload(String id,String type) throws XFormsException{
        return hasControlType(id,type);
    }

    /**
     * Checks wether the datatype of the specified form control is of the given
     * type.
     *
     * @param id the id of the form control.
     * @param type the fully qualified type name.
     * @return <code>true</code> if the control's datatype equals to or is
     * derived by restriction from the specified type, otherwise <code>false</code>.
     * @throws XFormsException if no document container is present or the
     * control is unknown.
     */
    public final boolean hasControlType(String id, String type) throws XFormsException {
        ensureContainerPresent();
        ensureContainerInitialized();

        // sanity checks
        XFormsElement element = this.container.lookup(id);
        if (element == null || !(element instanceof AbstractFormControl)) {
            throw new XFormsException("id '" + id + "' does not identify a form control");
        }

        // get datatype restriction
        String datatype = ((AbstractFormControl) element).getDatatype();
        if (datatype != null) {
            return element.getModel().getValidator().isRestricted(type, datatype);
        }

        return false;
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
     * @param id the id of the control.
     * @param value the new value for the control.
     * @throws XFormsException if no document container is present, the control
     * is unknown or an error occurred during value update.
     */
    public final void setControlValue(String id, String value) throws XFormsException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("update control value: id: " + id + ", value: " + value);
        }

        ensureContainerPresent();
        ensureContainerInitialized();

        // sanity checks
        XFormsElement element = this.container.lookup(id);
        if (element == null || !(element instanceof AbstractFormControl)) {
            throw new XFormsException("id '" + id + "' does not identify a form control");
        }
        if (element instanceof Upload) {
            throw new XFormsException("upload cannot be updated with this method - at: " + DOMUtil.getCanonicalPath(element.getElement()));
        }

        // update control value
        AbstractFormControl control = (AbstractFormControl) element;
        control.setValue(value);
    }

    /**
     * This method updates the value of an upload control. Other controls cannot
     * be updated with this method.
     *
     * @param id the id of the control.
     * @param mediatype the mediatype of the uploaded resource.
     * @param filename the filename of the uploaded resource.
     * @param data the uploaded data as byte array.
     * @throws XFormsException if no document container is present, the control
     * is unknown or an error occurred during value update.
     */
    public final void setUploadValue(String id, String mediatype, String filename, byte[] data) throws XFormsException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("update control value: id: " + id + ", mediatype: " + mediatype + ", filename: " + filename + ", data: " + (data != null ? data.length + " bytes" : "null"));
        }

        ensureContainerPresent();
        ensureContainerInitialized();

        // sanity checks
        XFormsElement element = this.container.lookup(id);
        if (element == null || !(element instanceof Upload)) {
            throw new XFormsException("id '" + id + "' does not identify an upload control");
        }

        // update upload control
        Upload upload = (Upload) element;
        upload.setValue(data, filename, mediatype);
    }

    /**
     * Sets the specified Repeat's index.
     *
     * @param id the repeat id.
     * @param index the repeat index.
     * @throws XFormsException if no document container is present, the repeat
     * is unkown or an error occurred during index update.
     */
    public void setRepeatIndex(String id, int index) throws XFormsException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("set repeat index: id: " + id + ", index: " + index);
        }

        ensureContainerPresent();
        ensureContainerInitialized();

        // sanity checks
        XFormsElement element = this.container.lookup(id);
        if (element == null || !(element instanceof Repeat)) {
            throw new XFormsException("id '" + id + "' does not identify a repeat");
        }

        // update repeat index
        Repeat repeat = (Repeat) element;
        repeat.setIndex(index);

        // update model dependencies and UI
        Model model = repeat.getModel();
        this.container.dispatch(model.getTarget(), XFormsEventNames.REBUILD, null);
        this.container.dispatch(model.getTarget(), XFormsEventNames.RECALCULATE, null);
        this.container.dispatch(model.getTarget(), XFormsEventNames.REVALIDATE, null);
        this.container.dispatch(model.getTarget(), XFormsEventNames.REFRESH, null);
    }

    /**
     * Finishes processor operation.
     *
     * This operation degrades gracefully, i.e. neither subsequent calls will
     * lead to an exception nor an attempt to shutdown a non-initialized
     * processor.
     *
     * @throws XFormsException if an error occurred during shutdown.
     */
    public void shutdown() throws XFormsException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("shutdown");
        }

        if (this.container != null) {
            this.container.shutdown();
            this.container = null;
        }
        else {
            LOGGER.warn("shutdown: container not present");
        }
    }

    /**
     * creates a Locale object from the passed language code. This must be valid ISO-639 code. The validity of the
     * locale is not checked at this point.
     *
     * @param lang an ISO language code
     * @throws XFormsException if the language code is null an Exception is thrown
     *
     */
    public void setLocale(String lang) throws XFormsException{
        addLocaleToContext(lang);
        if(container != null) container.refresh();
    }

    /**
     * creates a Locale object from the value configured in BetterForms' config file. This just serves as a default when
     * nothing else is specified by calling setLocale(String langCode). Please note that no validity check of the locale
     * is made here.
     *
     */
    protected void setLocale() throws XFormsException {
        String configuredLocale = null;
        configuredLocale = Config.getInstance().getProperty("language");
        addLocaleToContext(configuredLocale);
    }

    private void addLocaleToContext(String lang){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("setting locale to: " + lang);
        }
        try{
            getContext().put(BETTERFORM_LOCALE,new Locale(lang));
            getContext().put("lang",lang);
        }catch(NullPointerException e){
            //fall back to default locale if nothing is configured
            LOGGER.warn("No configuration property for 'language' found - using default VM locale: " + Locale.getDefault().toString());
            addLocaleToContext(Locale.getDefault().getLanguage());
        }
    }

    private Container createContainer() {
        XMLEventService eventService = new DefaultXMLEventService();
        eventService.setXMLEventFactory(new XercesXMLEventFactory());
        eventService.setXMLEventInitializer(new DefaultXMLEventInitializer());

        this.container = new Container(this);
        this.container.setXMLEventService(eventService);

        return this.container;
    }

    private Document toDocument(Node node) throws XFormsException {
        // ensure xerces dom
        if (node instanceof Document) {
            return (Document) node;
        }

        Document document = getDocumentBuilder().newDocument();
        if (node instanceof Document) {
            node = ((Document) node).getDocumentElement();
        }
        document.appendChild(document.importNode(node, true));

        return document;
    }

    private DocumentBuilder getDocumentBuilder() throws XFormsException {
        // ensure xerces dom
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            // factory.setAttribute("http://apache.org/xml/properties/dom/document-class-name", "org.apache.xerces.dom.DocumentImpl");

            DocumentBuilder db = factory.newDocumentBuilder();
            // use an empty entity resolver to avoid that Xerces may try to
            // download the system DTD (can cause latency problems)
            db.setEntityResolver(new EntityResolver (){
            	public InputSource resolveEntity(String publicId,String systemId)
            		throws SAXException,IOException { return null; };
        }
            );
            return db;
        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    private void ensureContainerPresent() throws XFormsException {
        if (this.container == null) {
            throw new XFormsException("document container not present");
        }
    }

    private void ensureContainerInitialized() throws XFormsException {
        if (this.container == null || !this.container.isModelConstructDone()) {
            throw new XFormsException("document container not initialized");
        }
    }

    private void ensureContainerNotInitialized() throws XFormsException {
        if (this.container != null && this.container.isModelConstructDone()) {
            //check if we've been serialized
            if(this.getXForms().getDocumentElement().hasAttributeNS(NamespaceConstants.BETTERFORM_NS,"serialized")){
                LOGGER.debug("FORM WAS SERIALIZED");

            }
            //if so, remove model event listeners and return else exception
//            throw new XFormsException("document container already initialized");
        }
    }

    // todo: move this code away
    private String resolve(URI relative) throws XFormsException {
        if (relative.isAbsolute() || relative.isOpaque()) {
            return relative.toString();
        }

        if (this.baseURI == null) {
            throw new XFormsException("base uri not present");
        }

        try {
            return new URI(this.baseURI).resolve(relative).toString();
        }
        catch (URISyntaxException e) {
            throw new XFormsException(e);
        }
    }

    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("serializing XFormsFormsProcessorImpl");
        }
        try {
            if (getXForms().getDocumentElement().hasAttribute("bf:serialized")) {
                objectOutput.writeUTF(DOMUtil.serializeToString( getXForms()));
            }  else {
            String baseURI = (String) this.getContext().get("betterform.baseURI");

                getXForms().getDocumentElement().setAttributeNS(NamespaceConstants.BETTERFORM_NS,"bf:baseURI",baseURI);
                getXForms().getDocumentElement().setAttributeNS(NamespaceConstants.BETTERFORM_NS,"bf:serialized","true");


                DefaultSerializer serializer = new DefaultSerializer(this);
                Document serializedForm = serializer.serialize();

                StringWriter stringWriter = new StringWriter();
                Transformer transformer = null;
                StreamResult result = new StreamResult(stringWriter);
                try {
                    transformer = TransformerFactory.newInstance().newTransformer();
                    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                    transformer.transform(new DOMSource(serializedForm), result);
                } catch (TransformerConfigurationException e) {
                    throw new IOException("TransformerConfiguration invalid: " + e.getMessage());
                } catch (TransformerException e) {
                    throw new IOException("Error during serialization transform: " + e.getMessage());
                }
                objectOutput.writeUTF(stringWriter.getBuffer().toString());
            }
        } catch (XFormsException e) {
            throw new IOException("baseURI couldn't be set");
        }

        objectOutput.flush();
        objectOutput.close();

    }

    /**
     * reads serialized host document from ObjectInputStream and parses the resulting String
     * to a DOM Document. After that the host document is passed to the processor. init() is NOT yet
     * called on the processor to allow an using application to do its own configuration work (like
     * setting of baseURI and passing of context params).
     *
     * todo: rethink the question of the baseURI - is this still necessary when deaserializing? Presumably yes to further allow dynamic resolution.
     * @param objectInput
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("deserializing XForms host document");
        }
        String read = objectInput.readUTF();
        Document host=null;
        try {
            host = DOMUtil.parseString(read,true,false);
            String baseURI = host.getDocumentElement().getAttribute("bf:baseURI");
            setContextParam("betterform.baseURI",baseURI);
            setBaseURI(baseURI);
            setXForms(host.getDocumentElement());
        } catch (ParserConfigurationException e) {
            throw new IOException("Parser misconfigured: " + e.getMessage());
        } catch (SAXException e) {
            throw new IOException("Parsing failed: " + e.getMessage());
        } catch (XFormsException e) {
            throw new IOException("An XForms error occurred when passing the host document: " + e.getMessage());
        }
    }

}

// end of class

