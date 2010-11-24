/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model;

import de.betterform.connector.ConnectorFactory;
import de.betterform.xml.config.Config;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DefaultAction;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.ns.NamespaceResolver;
import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.Initializer;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.action.UpdateHandler;
import de.betterform.xml.xforms.action.UpdateSequencer;
import de.betterform.xml.xforms.exception.XFormsComputeException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.exception.XFormsLinkException;
import de.betterform.xml.xforms.model.bind.Bind;
import de.betterform.xml.xforms.model.bind.RefreshView;
import de.betterform.xml.xforms.model.constraints.MainDependencyGraph;
import de.betterform.xml.xforms.model.constraints.SubGraph;
import de.betterform.xml.xforms.model.constraints.Validator;
import de.betterform.xml.xforms.model.constraints.Vertex;
import de.betterform.xml.xpath.XPathUtil;
import de.betterform.xml.xpath.impl.saxon.SaxonReferenceFinderImpl;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import net.sf.saxon.functions.FunctionLibrary;
import net.sf.saxon.om.StructuredQName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.xs.*;
import org.w3c.dom.*;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.events.Event;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.w3c.xforms.XFormsModelElement;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.*;


/**
 * encapsulates the model-part of a XForm: the schema, instance, submission and
 * Bindings and gives access to the individual parts.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @author Lars Windauer
 * @author Mark Dimon
 * @author Sophie Ramel
 * @version $Id: Model.java 3504 2008-08-29 14:52:26Z joern $
 */
public class Model extends XFormsElement implements XFormsModelElement, DefaultAction {
    private static Log LOGGER = LogFactory.getLog(Model.class);

    private List instances;
    private List modelBindings;
    private MainDependencyGraph mainGraph;
    private Validator validator;
    private Vector changed = new Vector();
    private List schemas;
    private boolean ready;
    private UpdateHandler updateHandler;
    private UpdateSequencer updateSequencer;
    private static int modelItemCounter = 0;

    private static XSModel defaultSchema = null;
    private List refreshedItems;

    /**
     * Creates a new Model object.
     *
     * @param element the DOM Element representing this Model
     */
    public Model(Element element) {
        super(element);
    }

    /**
     * Checks wether this model is ready or not.
     *
     * @return <code>true</code> if this model has received the
     *         <code>xforms-ready</event>, otherwise <code>false</code>.
     */
    public boolean isReady() {
        return this.ready;
    }

    /**
     * returns the list of changed Vertices
     *
     * @return the list of changed Vertices
     */
    public Vector getChanged() {
        return this.changed;
    }

    // children of this model can get acess to the container

    public Container getContainer() {
        return this.container;
    }

    /**
     * returns the default instance of this model. this is always the first in
     * document order regardless of its id-attribute.
     *
     * @return the default instance of this model
     */
    public Instance getDefaultInstance() {
        if (this.instances != null && this.instances.size() > 0) {
            return (Instance) this.instances.get(0);
        }

        return null;
    }

    /**
     * returns the instance-object for given id.
     *
     * @param id the identifier for instance
     * @return the instance-object for given id.
     */
    public Instance getInstance(String id) {
        if (this.instances == null) {
            return null;
        }
        if ((id == null) || "".equals(id)) {
            return getDefaultInstance();
        }

        for (int index = 0; index < this.instances.size(); index++) {
            Instance instance = (Instance) this.instances.get(index);

            if (id.equals(instance.getId())) {
                return instance;
            }
        }

        return null;
    }

    public List getInstances() {
        return this.instances;
    }

    /**
     * Computes the real instance id for the given path expression.
     * <p/>
     * If the path expression doesn't start with an instance function the id
     * of the default instance is returned. Otherwise the parameter of that
     * function is evaluated. The result is then used to lookup the instance
     * and if an instance is found, its real id is returned.
     *
     * @param path the path expression.
     * @return the real instance id for the given path expression.
     */
    public String computeInstanceId(String path) throws XFormsException {
        if (path == null) {
            return null;
        }

        // lookup expression (instance function parameter)
        String expression = XPathUtil.getInstanceParameter(path);
        if (expression != null) {
            // evaluate expression and lookup instance
            if (expression.equals("") || expression.equals("''")) {
                return getDefaultInstance().getId();
            } else {
                String value = XPathCache.getInstance().evaluateAsString(getDefaultInstance().getRootContext(), "string(" + expression + ")");
                String realId = null;
                Instance instance = getInstance(value);
                if (instance != null) {
                    realId = instance.getId();
                }

                return realId;
            }

        }
        Instance defaultInstance = getDefaultInstance();
        if (defaultInstance == null) {
            return "default";
        }

        // get real id of default instance
        return getDefaultInstance().getId();
    }

    /**
     * returns the Main-Calculation-Graph
     *
     * @return the Main-Calculation-Graph
     */
    public MainDependencyGraph getMainGraph() {
        return this.mainGraph;
    }

    /**
     * returns this Model object
     *
     * @return this Model object
     */
    public Model getModel() {
        return this;
    }

    /**
     * Returns the validator.
     *
     * @return the validator.
     */
    public Validator getValidator() {
        if (this.validator == null) {
            this.validator = new Validator();
            this.validator.setModel(this);
        }

        return this.validator;
    }

    /**
     * Returns a list of Schemas associated with this Model. <P> The list is
     * loaded at xforms-model-construct time and is ordered as follows: <ol>
     * <li>The XForms Datatypes Schema is always on top of the list.</li>
     * <li>All linked Schemas of this Model in order of their occurrence in the
     * <tt>xforms:schema</tt> attribute.</li> <li>All inline Schemas of this
     * Model in document order.</li> </ol>
     * <p/>
     * Note: use the schema's in a synchronized block on Model.class  (synchronized(Model.class ) { } )
     *
     * @return a list of Schemas associated with this Model.
     */
    public List getSchemas() {
        return this.schemas;
    }

    /**
     * adds a changed Vertex to the changelist. this happens every time a nodes
     * value has changed.
     * <p/>
     * this method has to be called after each change to the instance-data!
     *
     * @param changedNode - the Node whose value has changed
     */
    public void addChanged(Node changedNode) {
        if (this.mainGraph != null) {
            if (this.changed == null) {
                this.changed = new Vector();
            }

            Vertex vertex = this.mainGraph.getVertex(changedNode, Vertex.CALCULATE_VERTEX);

            if (vertex != null) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(this + " add changed: adding calculate vertex for " + changedNode);
                }

                this.changed.add(vertex);
            }
        }
    }

    /**
     * adds a new instance to this model.
     *
     * @param id the optional instance id.
     * @return the new instance.
     */
    public Instance addInstance(String id) throws XFormsException {
        // create instance node
        Element instanceNode = this.element.getOwnerDocument()
                .createElementNS(NamespaceConstants.XFORMS_NS, (this.xformsPrefix != null ? this.xformsPrefix : NamespaceConstants.XFORMS_PREFIX) + ":" + INSTANCE);

        // ensure id
        String realId = id;
        if (realId == null || realId.length() == 0) {
            realId = this.container.generateId();
        }

        instanceNode.setAttributeNS(null, "id", realId);
        instanceNode.setAttributeNS(NamespaceConstants.XMLNS_NS, "xmlns", "");
        this.element.appendChild(instanceNode);

        // create and initialize instance object
        createInstanceObject(instanceNode);
        return getInstance(id);
    }

    /**
     * adds a Bind object to this Model
     *
     * @param bind the Bind object to add
     */
    public void addBindElement(Bind bind) {
        if (this.modelBindings == null) {
            this.modelBindings = new ArrayList();
        }

        this.modelBindings.add(bind);
    }

    /**
     * Returns the update handler associated with this model.
     *
     * @return the update handler associated with this model.
     */
    public UpdateHandler getUpdateHandler() {
        return this.updateHandler;
    }

    /**
     * Associates the update handler with this model.
     *
     * @param updateHandler the update handler to be associated with this model.
     */
    public void setUpdateHandler(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    /**
     * Generates a model item id.
     *
     * @return a model item id.
     */
    public static String generateModelItemId() {
        // todo: build external id service
        return String.valueOf(++modelItemCounter);
    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace(this + " init");
        }
        this.refreshedItems = new ArrayList();

        this.updateSequencer = new UpdateSequencer(this);

        initializeDefaultAction();
        initializeExtensionFunctions();
    }

    /**
     * Performs element disposal.
     *
     * @throws XFormsException if any error occurred during disposal.
     */
    public void dispose() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " dispose");
        }
        this.instances = null;
        this.modelBindings = null;
        this.mainGraph = null;
        this.validator = null;
        this.changed = null;
        this.schemas = null;
        this.updateHandler = null;
        this.updateSequencer = null;

        disposeDefaultAction();
        this.element.setUserData("", null, null);
    }

    // lifecycle template methods

    /**
     * Initializes the default action.
     */
    protected void initializeDefaultAction() {
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.MODEL_CONSTRUCT, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.MODEL_CONSTRUCT_DONE, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.MODEL_DESTRUCT, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.READY, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.REFRESH, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.REVALIDATE, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.RECALCULATE, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.REBUILD, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.RESET, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.BINDING_EXCEPTION, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.LINK_EXCEPTION, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.LINK_ERROR, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.COMPUTE_EXCEPTION, this);
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.VERSION_EXCEPTION, this);
    }

    /**
     * this method checks for the existence of all functions listed on model and
     * throws a XFormsComputeException if one is not found (see 7.12 Extensions
     * Functions, XForms 1.0 Rec.) <br/><br/> Note: this method only checks if
     * the passed functions can be found in betterForm configuration file but doesn't
     * try to invoke them. It may still happen that runtime exceptions with
     * these functions occur in case the functions class does contain the
     * function in question or wrong parameter are used.
     *
     * @throws XFormsComputeException in case one of the listed functions cannot
     *                                be found or loaded
     */
    protected void initializeExtensionFunctions() throws XFormsComputeException {
        String functions = getXFormsAttribute(XFormsConstants.FUNCTIONS);
        if (functions != null && !functions.equals("")) {
            //check for availability of extension functions...
            StringTokenizer tokenizer = new StringTokenizer(functions);
            while (tokenizer.hasMoreTokens()) {
                String qname = tokenizer.nextToken();
                String prefix = "";
                String localName;
                if (qname.indexOf(":") == -1) {
                    localName = qname;
                } else {
                    prefix = qname.substring(0, qname.indexOf(":"));
                    localName = qname.substring(qname.indexOf(":") + 1);
                    String[] functionInfo = {""};
                }
                String namespaceURI = NamespaceResolver.getNamespaceURI(this.element, prefix);
                if (namespaceURI == null) namespaceURI = "";
                FunctionLibrary functionLibrary = XPathCache.getFgXFormsFunctionLibrary();
                if ((functionLibrary.getFunctionSignature(new StructuredQName(prefix, namespaceURI, localName), -1)) != null) {
                    throw new XFormsComputeException("Function '" + localName + "' cannot be found in Namespace: '" + namespaceURI + "'", this.target, null);
//                    Map<String, String> errorMsg = new HashMap<String, String>();
//                    errorMsg.put("error-message","XFormsComputeException: Function '" + localName + "' cannot be found in Namespace: '" + namespaceURI + "'");
//
//                    try {
//                        container.dispatch(this.target,XFormsEventNames.COMPUTE_EXCEPTION,errorMsg);
//                    } catch (XFormsException e) {
//                        throw new XFormsComputeException("XFormsComputeException: Function '" + localName + "' cannot be found in Namespace: '" + namespaceURI + "'", this.target, null);
//                    }
                }
            }
        }

    }

    /**
     * Disposes the default action.
     */
    protected void disposeDefaultAction() {
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.MODEL_CONSTRUCT, this);
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.MODEL_CONSTRUCT_DONE, this);
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.MODEL_DESTRUCT, this);
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.READY, this);
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.REFRESH, this);
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.REVALIDATE, this);
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.RECALCULATE, this);
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.REBUILD, this);
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.RESET, this);
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.BINDING_EXCEPTION, this);
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.LINK_EXCEPTION, this);
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.LINK_ERROR, this);
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.COMPUTE_EXCEPTION, this);
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.VERSION_EXCEPTION, this);
    }

    // implementation of org.w3c.xforms.XFormsModelElement

    /**
     * 7.3.1 The getInstanceDocument() Method.
     * <p/>
     * This method returns a DOM Document that corresponds to the instance data
     * associated with the <code>instance</code> element containing an
     * <code>ID</code> matching the <code>instance-id</code> parameter. If there
     * is no matching instance data, a <code>DOMException</code> is thrown.
     *
     * @param instanceID the instance id.
     * @return the corresponding DOM document.
     * @throws DOMException if there is no matching instance data.
     */
    public Document getInstanceDocument(String instanceID) throws DOMException {
        Instance instance = getInstance(instanceID);
        if (instance == null) {
            throw new DOMException(DOMException.NOT_FOUND_ERR, instanceID);
        }

        return instance.getInstanceDocument();
    }

    /**
     * 7.3.2 The rebuild() Method
     * <p/>
     * This method signals the XForms processor to rebuild any internal data
     * structures used to track computational dependencies within this XForms
     * Model. This method takes no parameters and raises no exceptions.
     * <p/>
     * Creates a MainDependencyGraph.
     */
    public void rebuild() {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " #################### REBUILD ####################");
            getLogger().debug(this);
        }

        try {
            if (this.updateSequencer.sequence(REBUILD)) {
                return;
            }

            if (this.modelBindings != null && this.modelBindings.size() > 0) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(this + " rebuild: creating main dependency graph for " +
                            this.modelBindings.size() + " bind(s)");
                }

                this.mainGraph = new MainDependencyGraph();

                for (int index = 0; index < this.modelBindings.size(); index++) {
                    Bind bind = (Bind) this.modelBindings.get(index);
                    bind.updateXPathContext();
                    this.mainGraph.buildBindGraph(bind, this);
                }

                this.changed = (Vector) this.mainGraph.getVertices().clone();
            }

            this.updateSequencer.perform();
        }
        catch (Exception e) {
            this.updateSequencer.reset();
            this.container.handleEventException(e);
        }
    }

    /**
     * 7.3.3 The recalculate() Method
     * <p/>
     * This method signals the XForms Processor to perform a full recalculation
     * of this XForms Model. This method takes no parameters and raises no
     * exceptions.
     * <p/>
     * Creates and recalculates a SubDependencyGraph.
     */
    public void recalculate() {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(" #################### RECALCULATE ####################");
            getLogger().debug(this);
        }

        try {
            if (this.updateSequencer.sequence(RECALCULATE)) {
                return;
            }

            if (this.changed != null && this.changed.size() > 0) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(this + " recalculate: creating sub dependency graph for " +
                            this.changed.size() + " node(s)");
                }

                SubGraph subGraph = new SubGraph();
                subGraph.constructSubDependencyGraph(this.changed);
                subGraph.recalculate();
                this.changed.clear();
            }

            this.updateSequencer.perform();
        }
        catch (Exception e) {
            this.updateSequencer.reset();
            this.container.handleEventException(e);
        }
    }

    /**
     * 7.3.4 The revalidate() Method
     * <p/>
     * This method signals the XForms Processor to perform a full revalidation
     * of this XForms Model. This method takes no parameters and raises no
     * exceptions.
     * <p/>
     * Revalidates all instances of this model.
     */
    public void revalidate() {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(" #################### REVALIDATE ####################");
            getLogger().debug(this);
        }

        try {
            if (this.updateSequencer.sequence(REVALIDATE)) {
                return;
            }

            if (this.instances != null && this.instances.size() > 0) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(this + " revalidate: revalidating " + this.instances.size() +
                            " instance(s)");
                }

                for (int index = 0; index < this.instances.size(); index++) {
                    getValidator().validate((Instance) this.instances.get(index));
                }
            }

            this.updateSequencer.perform();
        }
        catch (Exception e) {
            this.updateSequencer.reset();
            this.container.handleEventException(e);
        }
    }

    /**
     * 7.3.5 The refresh() Method
     * <p/>
     * This method signals the XForms Processor to perform a full refresh of
     * form controls bound to instance nodes within this XForms Model. This
     * method takes no parameters and raises no exceptions.
     */
    public void refresh() {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(" #################### START REFRESH Model " + this.id + " ####################");
            getLogger().debug(this);
        }
        try {
            if (this.updateSequencer.sequence(REFRESH)) {
                return;
            }

            Initializer.updateUIElements(this.container.getDocument().getDocumentElement(), this);

            if (this.instances != null) {
                Instance instance;
                Iterator iterator;
                ModelItem modelItem;
                for (int index = 0; index < this.instances.size(); index++) {
                    instance = (Instance) this.instances.get(index);

                    // resets state keeping on model items
                    iterator = instance.iterateModelItems();
                    while (iterator.hasNext()) {
                        modelItem = (ModelItem) iterator.next();
                        modelItem.getStateChangeView().reset();
                    }
                }
            }

            //reset refreshItems
            for (int index = 0; index < this.refreshedItems.size(); index++) {
                RefreshView refreshView = (RefreshView) this.refreshedItems.get(index);
                refreshView.reset();
            }
            this.refreshedItems.clear();

            this.updateSequencer.perform();

            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this + " #################### END REFRESH Model " + this.id + " ####################");
            }
        }
        catch (Exception e) {
            this.updateSequencer.reset();
            this.container.handleEventException(e);
        }
    }

    /**
     * Returns the logger object.
     *
     * @return the logger object.
     */
    protected Log getLogger() {
        return LOGGER;
    }

    // implementation of 'de.betterform.xml.events.DefaultAction'

    /**
     * Performs the implementation specific default action for this event.
     *
     * @param event the event.
     */
    public void performDefault(Event event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("\n\n>>>>>>>>>>>>>>>>>>>>>>>>> " + event.getType().toUpperCase() + " <<<<<<<<<<<<<<<<<<<<<<<<<\n\n");
        }
        try {
            if (event.getType().equals(XFormsEventNames.MODEL_CONSTRUCT)) {
                modelConstruct();
                return;
            }
            if (event.getType().equals(XFormsEventNames.MODEL_CONSTRUCT_DONE)) {
                modelConstructDone();
                return;
            }
            if (event.getType().equals(XFormsEventNames.MODEL_DESTRUCT)) {
                this.dispose();
                return;
            }
            if (event.getType().equals(XFormsEventNames.READY)) {
                ready();
                return;
            }
            if (event.getType().equals(XFormsEventNames.REFRESH)) {
                if (isCancelled(event)) {
                    getLogger().debug(this + event.getType() + " cancelled");
                    return;
                }

                refresh();
                return;
            }
            if (event.getType().equals(XFormsEventNames.REVALIDATE)) {
                if (isCancelled(event)) {
                    getLogger().debug(this + event.getType() + " cancelled");
                    return;
                }
                revalidate();
                return;
            }
            if (event.getType().equals(XFormsEventNames.RECALCULATE)) {
                if (isCancelled(event)) {
                    getLogger().debug(this + event.getType() + " cancelled");
                    return;
                }
                recalculate();
                return;
            }
            if (event.getType().equals(XFormsEventNames.REBUILD)) {
                if (isCancelled(event)) {
                    getLogger().debug(this + event.getType() + " cancelled");
                    return;
                }
                rebuild();
                return;
            }
            if (event.getType().equals(XFormsEventNames.RESET)) {
                if (isCancelled(event)) {
                    getLogger().debug(this + event.getType() + " cancelled");
                    return;
                }
                reset();
                return;
            }
            if (event.getType().equals(XFormsEventNames.BINDING_EXCEPTION)) {
                getLogger().error(this + " binding exception: " + ((XMLEvent) event).getContextInfo());
                return;
            }
            if (event.getType().equals(XFormsEventNames.LINK_EXCEPTION)) {
                getLogger().error(this + " link exception: " + ((XMLEvent) event).getContextInfo());
                return;
            }
            if (event.getType().equals(XFormsEventNames.LINK_ERROR)) {
                getLogger().warn(this + " link error: " + ((XMLEvent) event).getContextInfo());
                return;
            }
            if (event.getType().equals(XFormsEventNames.COMPUTE_EXCEPTION)) {
                getLogger().error(this + " compute exception: " + ((XMLEvent) event).getContextInfo());
                return;
            }
            if (event.getType().equals(XFormsEventNames.VERSION_EXCEPTION)) {
                getLogger().error(this + " version exception: " + ((XMLEvent) event).getContextInfo());
                this.container.shutdown();
                return;
            }
        }
        catch (Exception e) {
            // handle exception and stop event propagation
            this.container.handleEventException(e);
            event.stopPropagation();
        }
    }

    /**
     * Implements <code>xforms-model-construct</code> default action.
     */
    private void modelConstruct() throws XFormsException {
        // load schemas
        this.schemas = new ArrayList();
        loadDefaultSchema(this.schemas);
        loadLinkedSchemas(this.schemas);
        loadInlineSchemas(this.schemas);

        // The default schema is shared between all models of all forms, and isn't thread safe, so we need synchronization here.
        // We cache the default model bcz. it takes quite some time to construct it.
        synchronized (Model.class) {
            // set datatypes for validation
            getValidator().setDatatypes(getNamedDatatypes(this.schemas));
        }


        // build instances
        this.instances = new ArrayList();

        // todo: move to static method in initializer
        List<Element> instanceElements = getAllInstanceElements();
        int count = instanceElements.size();

        if (count > 0) {
            for (int index = 0; index < count; index++) {
                Element xformsInstance = instanceElements.get(index);
                createInstanceObject(xformsInstance);
                if(Config.getInstance().getProperty("betterform.debug-allowed").equals("true")){
                    Map contextInfo = new HashMap(1);
                    contextInfo.put("modelId",XFormsElement.getXFormsAttribute((Element) xformsInstance.getParentNode(),"id"));
                    contextInfo.put("instanceId",XFormsElement.getXFormsAttribute(xformsInstance,"id"));
                    this.container.dispatch(this.target, BetterFormEventNames.INSTANCE_CREATED, contextInfo);
                }
            }
        }

        // todo: initialize p3p ?
        // initialize binds and submissions (actions should be initialized already)
        Initializer.initializeBindElements(this, this.element, new SaxonReferenceFinderImpl());
        Initializer.initializeActionElements(this, this.element);
        Initializer.initializeSubmissionElements(this, this.element);

        rebuild();
        recalculate();
        revalidate();
    }

    /**
     * @return
     */
    private List<Element> getAllInstanceElements() {
        List<Element> result = new ArrayList<Element>();
        for (Node it = getElement().getFirstChild(); it != null; it = it.getNextSibling()) {
            if (it.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element el = (Element) it;
            if (NamespaceConstants.XFORMS_NS.equals(it.getNamespaceURI()) && XFormsConstants.INSTANCE.equals(el.getLocalName())) {
                result.add(el);
            }
        }
        return result;
    }

    private void createInstanceObject(Element xformsInstance) throws XFormsException {
        Instance instance = (Instance) this.container.getElementFactory().createXFormsElement(xformsInstance, this);
        instance.init();
        this.instances.add(instance);
    }


    /**
     * Implements <code>xforms-model-construct-done</code> default action.
     */
    private void modelConstructDone() throws XFormsException {

        if (getContainer().isModelConstructDone()) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this + " model construct done: already performed");
            }

            // process only once for all models
        } else {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this + " model construct done: starting ui initialization");
            }

            // initialize ui elements
            Initializer.initializeUIElements(this.container.getDocument().getDocumentElement());
            // We need to do an extra refresh because value and node ref expressions of UI controls can refer to for 
            // example the repeat-index of a repeat that is not yet initialized when the UI control is initialized
            // (the repeat is after the UI control in document order)  
//            refresh();
        }
    }

    /**
     * Implements <code>xforms-ready</code> default action.
     */
    private void ready() {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " ready");
        }

        if (this.instances != null) {
            Instance instance;
            Iterator iterator;
            ModelItem modelItem;
            for (int index = 0; index < this.instances.size(); index++) {
                instance = (Instance) this.instances.get(index);
                instance.storeInitialInstance();

                try {
                    // init state keeping on model items
                    iterator = instance.iterateModelItems();
                    while (iterator.hasNext()) {
                        modelItem = (ModelItem) iterator.next();
                        modelItem.getStateChangeView().reset();
                        modelItem.getRefreshView().reset();
                    }
                }
                catch (XFormsException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }

        this.ready = true;
    }

    /**
     * Implements <code>xforms-reset</code> default action.
     */
    private void reset() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " reset");
        }

        if (this.instances != null && this.instances.size() > 0) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this + " reset: resetting " + this.instances.size() + " instance(s)");
            }

            for (int index = 0; index < this.instances.size(); index++) {
                Instance instance = (Instance) this.instances.get(index);
                instance.reset();
            }
        }

        // dispatch xforms-rebuild, xforms-recalculate, xforms-revalidate
        // and xforms-refresh to model
        this.container.dispatch(this.target, XFormsEventNames.REBUILD, null);
        this.container.dispatch(this.target, XFormsEventNames.RECALCULATE, null);
        this.container.dispatch(this.target, XFormsEventNames.REVALIDATE, null);
        this.container.dispatch(this.target, XFormsEventNames.REFRESH, null);
    }

    private void loadDefaultSchema(List list) throws XFormsException {
        try {
            synchronized (Model.class) {
                if (this.defaultSchema == null) {
                    // todo: still a hack
                    InputStream stream = Config.class.getResourceAsStream(Config.getInstance().getProperty("xforms.schema"));
                    this.defaultSchema = loadSchema(stream);
                }

                if (this.defaultSchema == null) {
                    throw new NullPointerException("resource not found");
                }
                list.add(this.defaultSchema);
            }
        }
        catch (Exception e) {
            throw new XFormsLinkException("could not load default schema", e, this.target, null);
        }
    }

    private void loadLinkedSchemas(List list) throws XFormsException {
        String schemaURI = null;
        try {
            String schemaAttribute = getXFormsAttribute("schema");
            if (schemaAttribute != null) {
                StringTokenizer tokenizer = new StringTokenizer(schemaAttribute, " ");
                XSModel schema = null;

                while (tokenizer.hasMoreTokens()) {
                    schemaURI = tokenizer.nextToken();

                    if (schemaURI.startsWith("#")) {
                        // lookup fragment
                        String id = schemaURI.substring(1);
                        Element element = this.container.getElementById(id);
                        schema = loadSchema(element);
                    } else {
                        // resolve URI
                        schema = loadSchema(schemaURI);
                    }

                    if (schema == null) {
                        throw new NullPointerException("resource not found");
                    }
                    list.add(schema);
                }
            }
        }
        catch (Exception e) {
            throw new XFormsLinkException("could not load linked schema", e, this.target, schemaURI);
        }
    }

    private void loadInlineSchemas(List list) throws XFormsException {
        String schemaId = null;
        try {
            NodeList children = this.element.getChildNodes();

            for (int index = 0; index < children.getLength(); index++) {
                Node child = children.item(index);

                if (Node.ELEMENT_NODE == child.getNodeType() &&
                        NamespaceConstants.XMLSCHEMA_NS.equals(child.getNamespaceURI())) {
                    Element element = (Element) child;
                    schemaId = element.hasAttributeNS(null, "id")
                            ? element.getAttributeNS(null, "id")
                            : element.getNodeName();

                    XSModel schema = loadSchema(element);

                    if (schema == null) {
                        throw new NullPointerException("resource not found");
                    }
                    list.add(schema);
                }
            }
        }
        catch (Exception e) {
            throw new XFormsLinkException("could not load inline schema", e, this.target, schemaId);
        }
    }

    // todo: move to schema helper component

    public Map getNamedDatatypes(List schemas) {
        Map datatypes = new HashMap();

        // iterate schemas
        Iterator schemaIterator = schemas.iterator();
        while (schemaIterator.hasNext()) {
            XSModel schema = (XSModel) schemaIterator.next();
            XSNamedMap definitions = schema.getComponents(XSConstants.TYPE_DEFINITION);

            for (int index = 0; index < definitions.getLength(); index++) {
                XSTypeDefinition type = (XSTypeDefinition) definitions.item(index);

                // process named simple types being supported by XForms
                if (type.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE &&
                        !type.getAnonymous() &&
                        getValidator().isSupported(type.getName())) {
                    String name = type.getName();

                    // extract local name
                    int separator = name.indexOf(':');
                    String localName = separator > -1 ? name.substring(separator + 1) : name;

                    // build expanded name
                    String namespaceURI = type.getNamespace();
                    String expandedName = NamespaceResolver.expand(namespaceURI, localName);

                    if (NamespaceConstants.XFORMS_NS.equals(namespaceURI) ||
                            NamespaceConstants.XMLSCHEMA_NS.equals(namespaceURI)) {
                        // register default xforms and schema datatypes without namespace for convenience
                        datatypes.put(localName, type);
                    }

                    // register uniquely named type
                    datatypes.put(expandedName, type);
                }
            }
        }

        return datatypes;
    }

    public String getTargetNamespace(XSModel xsModel) {
        String namespace = xsModel.getComponents(XSConstants.TYPE_DEFINITION).item(0).getNamespace();
        return namespace;
    }

    public void addRefreshItem(RefreshView changed) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("add refreshView " + changed.toString());
        }
        this.refreshedItems.add(changed);
    }

    private XSModel loadSchema(String uri) throws XFormsException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        ConnectorFactory connectorFactory = this.container.getConnectorFactory();
        URI absoluteURI = connectorFactory.getAbsoluteURI(uri, this.element);

        return getSchemaLoader().loadURI(absoluteURI.toString());
    }

    private XSModel loadSchema(InputStream stream) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        LSInput input = new DOMInputImpl();
        input.setByteStream(stream);

        return getSchemaLoader().load(input);
    }

    private XSModel loadSchema(Element element) throws TransformerException, IllegalAccessException, InstantiationException, ClassNotFoundException {
        Element copy = (Element) element.cloneNode(true);
        NamespaceResolver.applyNamespaces(element, copy);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.transform(new DOMSource(copy), new StreamResult(stream));
        byte[] array = stream.toByteArray();

        return loadSchema(new ByteArrayInputStream(array));
    }


    private XSLoader getSchemaLoader() throws IllegalAccessException,
            InstantiationException, ClassNotFoundException {
        // System.setProperty(DOMImplementationRegistry.PROPERTY,
        // "org.apache.xerces.dom.DOMXSImplementationSourceImpl");
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        XSImplementation implementation = (XSImplementation) registry.getDOMImplementation("XS-Loader");
        XSLoader loader = implementation.createXSLoader(null);

        DOMConfiguration cfg = loader.getConfig();

        cfg.setParameter("resource-resolver", new LSResourceResolver() {
            public LSInput resolveResource(String type,
                                           String namespaceURI,
                                           String publicId,
                                           String systemId,
                                           String baseURI) {
                LSInput input = new LSInput() {
                    String systemId;

                    public void setSystemId(String systemId) {
                        this.systemId = systemId;
                    }

                    public void setStringData(String s) {
                    }

                    String publicId;

                    public void setPublicId(String publicId) {
                        this.publicId = publicId;
                    }

                    public void setEncoding(String s) {
                    }

                    public void setCharacterStream(Reader reader) {
                    }

                    public void setCertifiedText(boolean flag) {
                    }

                    public void setByteStream(InputStream inputstream) {
                    }

                    String baseURI;

                    public void setBaseURI(String baseURI) {
                        this.baseURI = baseURI;
                    }

                    public String getSystemId() {
                        return this.systemId;
                    }

                    public String getStringData() {
                        return null;
                    }

                    public String getPublicId() {
                        return this.publicId;
                    }

                    public String getEncoding() {
                        return null;
                    }

                    public Reader getCharacterStream() {
                        return null;
                    }

                    public boolean getCertifiedText() {
                        return false;
                    }

                    public InputStream getByteStream() {
                        if(LOGGER.isTraceEnabled()){
                            LOGGER.trace("Schema resource\n\t\t publicId '" + publicId + "'\n\t\t systemId '" + systemId + "' requested");
                        }
                        if ("http://www.w3.org/MarkUp/SCHEMA/xml-events-attribs-1.xsd".equals(systemId)) {
                            if(LOGGER.isTraceEnabled()){
                                LOGGER.trace("loading resource 'schema/xml-events-attribs-1.xsd'\n\n");
                            }
                            return Thread.currentThread().getContextClassLoader().getResourceAsStream("schema/xml-events-attribs-1.xsd");
                        } else if ("http://www.w3.org/2001/XMLSchema.xsd".equals(systemId)) {
                            if(LOGGER.isTraceEnabled()){
                                LOGGER.trace("loading resource 'schema/XMLSchema.xsd'\n\n");
                            }
                            return Thread.currentThread().getContextClassLoader().getResourceAsStream("schema/XMLSchema.xsd");
                        } else if ("-//W3C//DTD XMLSCHEMA 200102//EN".equals(publicId) ) {
                            if(LOGGER.isTraceEnabled()){
                                LOGGER.trace("loading resource 'schema/XMLSchema.dtd'\n\n");
                            }
                            return Thread.currentThread().getContextClassLoader().getResourceAsStream("schema/XMLSchema.dtd");
                        } else if ("datatypes".equals(publicId)) {
                            if(LOGGER.isTraceEnabled()){
                                LOGGER.trace("loading resource 'schema/datatypes.dtd'\n\n");
                            }
                            return Thread.currentThread().getContextClassLoader().getResourceAsStream("schema/datatypes.dtd");
                        } else if ("http://www.w3.org/2001/xml.xsd".equals(systemId)) {
                            if(LOGGER.isTraceEnabled()){
                                LOGGER.trace("loading resource 'schema/xml.xsd'\n\n");
                            }
                            return Thread.currentThread().getContextClassLoader().getResourceAsStream("schema/xml.xsd");
                        } else {
                            if(LOGGER.isTraceEnabled()){
                                LOGGER.trace("resource not known '" + systemId + "'\n\n");
                            }
                            return null;
                        }
                    }

                    public String getBaseURI() {
                        return this.baseURI;
                    }
                };
                input.setSystemId(systemId);
                input.setBaseURI(baseURI);
                input.setPublicId(publicId);
                return input;
            }
        });
        // END: Patch
        return loader;
    }


}

// end of class
