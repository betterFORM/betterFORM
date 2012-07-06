/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.ns.NamespaceResolver;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.exception.XFormsLinkException;
import de.betterform.xml.xforms.model.bind.BindingResolver;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.XPathUtil;
import de.betterform.xml.xpath.impl.saxon.BetterFormXPathContext;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import net.sf.saxon.dom.NodeWrapper;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.trans.XPathException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.*;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * Implementation of XForms instance Element.
 *
 * @version $Id: Instance.java 3510 2008-08-31 14:39:56Z lars $
 */
public class Instance extends XFormsElement {
    private static Log LOGGER = LogFactory.getLog(Instance.class);
    private Document instanceDocument = null;
    private Element initialInstance = null;
    private BetterFormXPathContext xPathContext = null;
    private static String READONLY_INSTANCE="readonly";
    private boolean isReadonly=false;

    /**
     * Creates a new Instance object.
     *
     * @param element the DOM Element of this instance
     * @param model   the owning Model of this instance
     */
    public Instance(Element element, Model model) {
        super(element, model);
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
        if(getXFormsAttribute(Instance.READONLY_INSTANCE) != null &&
           getXFormsAttribute(Instance.READONLY_INSTANCE).equals("true")){
            this.isReadonly = true;
        }
        // load initial instance
        this.initialInstance = createInitialInstance();
        // create instance document
        this.instanceDocument = createInstanceDocument();
        storeContainerRef();

        registerId();
        initXPathContext();
    }

    private void initXPathContext() {
        this.xPathContext = new BetterFormXPathContext(getInstanceNodeset(),1,getPrefixMapping(),xpathFunctionContext);
    }
    
    public BetterFormXPathContext getRootContext() {
	return xPathContext;
    }

    boolean isReadOnly(){
        return this.isReadonly;
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

        this.initialInstance = null;
        this.instanceDocument = null;
    }


    // instance specific methods

    /**
     * this method lets you access the state of individual instance data nodes.
     * each node has an associated ModelItem object which holds the current
     * status of readonly, required, validity, relevant. it also annotates the
     * DOM node with type information.
     * <p/>
     * When an ModelItem does not exist already it will be created and attached
     * as useroject to its corresponding node in the instancedata.
     *
     * @param locationPath - an absolute location path pointing to some node in
     *                     this instance
     * @return the ModelItem for the specified node
     */
    public ModelItem getModelItem(String locationPath) throws XFormsException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(this + " get model item for locationPath '" + locationPath + "'");
        }

        // ensure that node exists since getPointer is buggy
        if (!existsNode(locationPath)) {
            return null;
        }

        Node node = getNode(locationPath);

        ModelItem item = (ModelItem) node.getUserData("");

        if (item == null) {
            // create item
            item = createModelItem(node);

//            if (LOGGER.isDebugEnabled()) {
//                LOGGER.debug(this + " get model item: created item for path '" + pointer + "'");
//            }
        }

        return item;
    }

    /**
     * Returns an iterator over all existing model items.
     *
     * @return an iterator over all existing model items.
     * @throws XFormsException
     */
    public Iterator iterateModelItems() throws XFormsException {
        return iterateModelItems(getInstanceNodeset(), 1, "/", Collections.EMPTY_MAP, null, true);
    }

    /**
     * Returns an iterator over the specified model items.
     *
     * @param nodeset from which the model items should be retrieved
     */
    public Iterator iterateModelItems(List nodeset, boolean deep) {
        // create list, fill and iterate it
        // todo: optimize with live iterator

        final List list = new ArrayList();

        for (int i = 0; i < nodeset.size(); ++i) {
            Node node =  de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(nodeset, i + 1);
            listModelItems(list, node, deep);
        }

        return list.iterator();
    }

    /**
     * Returns an iterator over the specified model items.
     *
     * @param path the path selecting a set of model items.
     * @param deep include attributes and children or not.
     * @return an iterator over the specified model items.
     * @throws XPathException
     */
    public Iterator iterateModelItems(List nodeset, int position, String path, Map prefixMapping, XPathFunctionContext functionContext, boolean deep) throws XFormsException {
        final List xpathResult = XPathCache.getInstance().evaluate(nodeset, position, path, prefixMapping, functionContext);

        return iterateModelItems(xpathResult, deep);
    }

    /**
     * Returns the value of the specified node.
     *
     * @param node the path pointing to a node.
     * @return the value of the specified node.
     */
    public String getNodeValue(Node node) throws XFormsException {
        final ModelItem modelItem = getModelItem(node);
        if (modelItem == null) {
            throw new XFormsException("model item for path '" + DOMUtil.getCanonicalPath(node) + "' does not exist");
        }

        return modelItem.getValue();
    }

    /**
     * Sets the value of the specified node.
     *
     * @param node  of which the value should be changed.
     * @param value the value to be set.
     */
    public void setNodeValue(Node node, String value) throws XFormsException {
        if(node != null){
            final ModelItem modelItem = getModelItem(node);
            if (!modelItem.isReadonly()) {
                modelItem.setValue(value);
                this.model.addChanged((Node) modelItem.getNode());
            } else {
                getLogger().warn(this + " set node value: attempt to set readonly value");
            }
        }
    }

    /**
     *
     * @param node
     * @return
     */
    public ModelItem getModelItem(Node node) {
        if (node == null) {
            return null;
        }

        ModelItem item = (ModelItem) node.getUserData("");

        if (item == null) {
            // create item
            item = Instance.createModelItem(node);

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(this + " get model item: created item for path " + BindingResolver.getCanonicalPath(node));
            }
        }

        return item;
    }

    public void setNode(Node parentNode, Element node) throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " set node value: " + BindingResolver.getCanonicalPath(parentNode) + "=" + node);
        }

        ModelItem item = getModelItem(parentNode);
        if (item == null) {
            throw new XFormsException("model item for path '" + BindingResolver.getCanonicalPath(parentNode) + "' does not exist");
        }

        if (!item.isReadonly()) {
            //todo: think this is wrong cause we want to attach ourselves as child element to object denoted by 'path'
            Node imported = this.instanceDocument.importNode(node, true);
            ((Element) item.getNode()).appendChild(imported);
//            item.setNode(node);
            this.model.addChanged((Node)item.getNode());
        } else {
            getLogger().warn(this + " set node value: attempt to set readonly value");
        }
    }

    /**
     * Checks if the specified node exists.
     *
     * @param path the path pointing to a node.
     * @return <code>true</code> if the specified node exists, otherwise
     *         <code>false</code>.
     */
    private boolean existsNode(String path) throws XFormsException {
        return getContextSize(path) > 0;
    }


    /**
     * Counts the number of nodes in the specified nodeset.
     *
     * @param path the path pointing to a nodeset.
     * @return the number of nodes in the specified nodeset.
     */
    private int getContextSize(String path) throws XFormsException {
        String cnt = XPathCache.getInstance().evaluateAsString(getInstanceNodeset(), 1, "count(" + path + ")", getPrefixMapping(), xpathFunctionContext);
        return Integer.parseInt(cnt);
    }

    public void createNode(List nodeset, int position, String qname) throws XFormsException {
        if (nodeset.size() < position) {
            return;
        }

        Node parentNode = (Node) ((NodeWrapper) nodeset.get(position - 1)).getUnderlyingNode();
        try {
        	parentNode.appendChild(createElement(qname));
        }
        catch(DOMException e) {
        	throw new XFormsBindingException(e.getMessage(), this.target, null);
        }
    }

    
    /**
     * Inserts the specified node.
     *
     * @param parentNode the path pointing to the origin node.
     * @param beforeNode the path pointing to the node before which a clone of the
     *               origin node will be inserted.
     * @return 
     */
    public Node insertNode(Node parentNode, Node originNode, Node beforeNode) throws XFormsException {
	// insert a deep clone of 'origin' node before 'before' node. if
        // 'before' node is null, the clone will be appended to 'parent' node.
        ModelItem miOrigin = getModelItem(originNode);
        Node insertedNode;
        if (originNode.getNodeType() == Node.ATTRIBUTE_NODE) {
        	insertedNode = this.instanceDocument.importNode(originNode, true);
        	((Element)parentNode).setAttributeNode((Attr)insertedNode);
        }
        else {
        	insertedNode = parentNode.insertBefore(this.instanceDocument.importNode(originNode, true), beforeNode);
        }
        String canonPath = DOMUtil.getCanonicalPath(insertedNode);

        ModelItem insertedModelItem = getModelItem(insertedNode);
        insertedModelItem.getDeclarationView().setDatatype(miOrigin.getDeclarationView().getDatatype());


        // get canonical path for inserted node
        String canonicalPath;
        if (beforeNode != null || originNode.getNodeType() == Node.ATTRIBUTE_NODE){
            canonicalPath = BindingResolver.getCanonicalPath(insertedNode);
        }
        else{
            Node lastChild = ((DocumentTraversal)instanceDocument).createTreeWalker(parentNode, NodeFilter.SHOW_ALL,null,false).lastChild();
            canonicalPath = BindingResolver.getCanonicalPath(lastChild);
        }

        String[] canonicalParts = XPathUtil.getNodesetAndPredicates(canonicalPath);

        if (originNode.hasChildNodes()) {
            setDatatypeOnChilds(originNode, insertedNode);
        }


        // dispatch internal betterform event (for instant repeat updating)
        HashMap map = new HashMap();
        map.put("nodeset", canonicalParts[0]);
        map.put("position", canonicalParts.length>1?canonicalParts[1]:"1");
        map.put("canonPath", canonPath);
        this.container.dispatch(this.target, BetterFormEventNames.NODE_INSERTED, map);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " insert node: instance data after manipulation" + toString(this.instanceDocument));
        }
        
        return insertedNode;
    }
    
    private void setDatatypeOnChilds(Node originNode, Node insertedNode) {
        NodeList originChilds = originNode.getChildNodes();
        NodeList insertedChilds = insertedNode.getChildNodes();
        if (insertedChilds.getLength() == originChilds.getLength()) {
            for (int i = 0; i < originChilds.getLength(); i++) {
                Node originChild = originChilds.item(i);
                Node insertedChild = insertedChilds.item(i);

                ModelItem modelItemOrigin = getModelItem(originChild);
                ModelItem modelItemInserted = getModelItem(insertedChild);
                modelItemInserted.getDeclarationView().setDatatype(modelItemOrigin.getDeclarationView().getDatatype());

                if (originChild.hasChildNodes()) {
                    setDatatypeOnChilds(originChild, insertedChild);
                }
            }
        } else {
            LOGGER.debug(this + " inserted node has fewer child than origin.");
        }
    }

    /**
     * returns true if current Node's modelitem is readonly or any of it's ancestors is readonly
     * @param n
     * @return true if any of the ancestors or the node itself is readonly false otherwise
     */
    private boolean isReadonly(Node n){
        if(n.getNodeType() == Node.DOCUMENT_NODE) return false;
        if(getModelItem(n).isReadonly()){
            return true;
        }else if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
            return isReadonly(((Attr) n).getOwnerElement());
        } else {
            return isReadonly(n.getParentNode());
        }
    }

    /**
     * Deletes the specified node.
     *
     * @param path the path pointing to the node to be deleted.
     */
    public boolean deleteNode(Node node, String path) throws XFormsException {
        String canonicalPath = DOMUtil.getCanonicalPath(node);
        if(node == null){
            LOGGER.warn("Node is null - delete is terminated with no effect.");
            return false;
        }

        //don't delete readonly nodes
        if(isReadonly(node)){
            LOGGER.warn("Node or one of it's parents is readonly - delete is terminated with no effect.");
            return false;
        }

        //don't delete content of a xmlns Attribute - not clear what Spec means by not allowing to delete a namespace node
        if(node.getNodeName().startsWith("xmlns")){
            LOGGER.warn("Node is Namespace declaration - delete is terminated with no effect.");
            return false;
        }
        
        //don't delete root nodes
        if (node.getNodeType() != Node.ATTRIBUTE_NODE && node.getParentNode().getNodeType() == Node.DOCUMENT_NODE ) {
            LOGGER.warn("Node is a root Node - delete is terminated with no effect.");
            return false;
        }

        //don't delete document nodes
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            LOGGER.warn("Node is a Document Node - delete is terminated with no effect.");
            return false;
        }

        Node canonNode = node;

        if (node.getNodeType() != Node.ATTRIBUTE_NODE) {
            node.getParentNode().removeChild(node);
        } else {
            Attr attr = (Attr) node;
            attr.getOwnerElement().removeAttributeNode(attr);
        }

        // dispatch internal betterform event (for instant repeat updating)
        String[] canonicalParts = XPathUtil.getNodesetAndPredicates(path);
        HashMap map = new HashMap();
        map.put("nodeset", canonicalParts[0]);
        map.put("position", canonicalParts[canonicalParts.length - 1]);
        map.put("canonPath",canonicalPath);
        this.container.dispatch(this.target, BetterFormEventNames.NODE_DELETED, map);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " delete node: instance data after manipulation" + toString(this.instanceDocument));
        }
        return true;
    }


    /**
     * Return the the 'root' nodeset of this instance, can be an empty list if the instance not yet contains a root element.
     *
     * @return
     */
    public List getInstanceNodeset() {
        String baseURI = container.getProcessor().getBaseURI();
        return de.betterform.xml.xpath.impl.saxon.XPathUtil.getRootContext(instanceDocument,baseURI);
    }

    /**
     * Sets the instance document.
     *
     * @param document the instance document.
     */
    public void setInstanceDocument(Document document) throws XFormsException {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("setInstanceDocument");
            LOGGER.debug("former instance:");
            DOMUtil.prettyPrintDOM(this.instanceDocument);
            LOGGER.debug("updated instance:");
            DOMUtil.prettyPrintDOM(document);
        }
        this.instanceDocument = document;

        storeContainerRef();
        initXPathContext();
        String refreshOriginal = getXFormsAttribute("refreshOriginal");
        if(refreshOriginal  != null && refreshOriginal.equals("true")){
            this.initialInstance = null;
            storeInitialInstance();
        }
    }

    /**
     * Returns the instance document.
     *
     * @return the instance document.
     */
    public Document getInstanceDocument() {
        return this.instanceDocument;
    }

    /**
     * returns the initial instance
     * @return the initial instance
     */
    public Document getInitialInstance(){
        if(this.initialInstance != null){
            Document doc =DOMUtil.newDocument(true,false);
            doc.appendChild(doc.importNode(this.initialInstance,true));
            return doc;
        }else{
            return null;
        }
    }

    /**
     * Checks wether the specified <code>instance</code> element has an initial
     * instance.
     * <p/>
     * The specified <code>instance</code> element is considered to have an
     * initial instance if it has either a <code>src</code> attribute or at
     * least one element child.
     *
     * @return <code>true</code> if the <code>instance</code> element has an
     *         initial instance, otherwise <code>false</code>.
     */
    public boolean hasInitialInstance() {
        return this.initialInstance != null;
    }

    /**
     * Stores the current instance data as initial instance if no original
     * instance exists.
     * <p/>
     * This is needed for resetting an instance to its initial state when no
     * initial instance exists.
     */
    void storeInitialInstance() throws XFormsException {
        if (this.initialInstance == null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this + " store initial instance");
                // DOMUtil.prettyPrintDOM((this.instanceDocument.getDocumentElement()));
            }
            try{
                this.initialInstance = (Element) this.instanceDocument.getDocumentElement().cloneNode(true);                
            }catch(Exception e){
                throw new XFormsException("Instance '" + this.id + "' has no root element",e);
            }
            NamespaceResolver.applyNamespaces(this.element, this.initialInstance);

/*
            if(getLogger().isDebugEnabled()) {
                getLogger().debug("initial instance:");
                DOMUtil.prettyPrintDOM(initialInstance);

            }*/
        }
    }

    /**
     * Performs element reset.
     *
     * @throws XFormsException if any error occurred during reset.
     */
    public void reset() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " reset");
        }

        // recreate instance document
        this.instanceDocument = createInstanceDocument();
        storeContainerRef();
        initXPathContext();

    }


    // XXX remove when geteModelItem is rewritten
    private Node getNode(String xpath) throws XFormsException{
        NodeInfo nodeInfo = (NodeInfo) XPathCache.getInstance().evaluate(this.xPathContext,xpath).get(0); 
        return (Node) ((NodeWrapper)nodeInfo).getUnderlyingNode();
    }

    /**
     * Returns the logger object.
     *
     * @return the logger object.
     */
    protected Log getLogger() {
        return LOGGER;
    }

    // private helper methods

    /**
     * Creates the root element of the instance data.
     *
     * @param qname the qualified name of the root element.
     */
    public void createRootElement(String qname) {
        if (this.instanceDocument.getDocumentElement() != null) {
            return;
        }

        this.instanceDocument.appendChild(createElement(qname));
    }

    /**
     * @param qname
     * @return
     */
    private Element createElement(String qname) {
        Element root;
        int separator = qname.indexOf(':');
        if (separator > -1) {
            String prefix = qname.substring(0, separator);
            String uri = NamespaceResolver.getNamespaceURI(this.element, prefix);
            root = this.instanceDocument.createElementNS(uri, qname);
        } else {
            root = this.instanceDocument.createElement(qname);
        }

        NamespaceResolver.applyNamespaces(this.element, root);
        root.setAttribute("xmlns", "");

        return root;
    }

    /**
     * Returns the original instance.
     * <p/>
     * If this instance has a <code>src</code> attribute, it will be resolved
     * and the resulting document element is used. Otherwise the first child
     * element of this instance is used.
     *
     * @return the original instance.
     */
    private Element createInitialInstance() throws XFormsException {
        String srcAttribute = getXFormsAttribute(SRC_ATTRIBUTE);
        String resourceUri;
        //@src takes precedence
        if (srcAttribute != null) {
            resourceUri = srcAttribute;
            return fetchData(srcAttribute);
        }else{
            resourceUri = "#" + this.getId();
        }

        // if inline content is given this takes precedence over @resource
        List childs = DOMUtil.getChildElements(this.element);
        if(childs.size() > 1) {
            Map contextInfo = new HashMap();
            contextInfo.put("resource-uri",resourceUri);
            contextInfo.put("resource-error","multiple root elements found in instance");
            this.container.dispatch(this.model.getTarget(), XFormsEventNames.LINK_EXCEPTION, contextInfo);
        }
        Element child = DOMUtil.getFirstChildElement(this.element);
        if(child != null){
            return child;
        }

        //finally if @resource is given take it
        String resourceAttribute = getXFormsAttribute(RESOURCE_ATTRIBUTE);
        if (resourceAttribute != null){
            return fetchData(resourceAttribute);
        }
        return null;
        //todo: FIXME: need to provide contextinfo 'resource-uri'
        //throw new XFormsLinkException("Failed to fetch external data", this.model.getTarget(), null);
    }

    private Element fetchData(String srcAttribute) throws XFormsLinkException {
        Object result;
        try {
            result = this.container.getConnectorFactory().createURIResolver(srcAttribute, this.element).resolve();
        }
        catch (Exception e) {
            String msg;
            if(e.getCause()!=null){
                msg=e.getCause().getMessage();
            }else{
                msg=e.getMessage();
            }
            
            HashMap<String,String> map = new HashMap<String, String>(2);
            map.put("resource-uri",srcAttribute);
            map.put("detailMessage",msg);
            throw new XFormsLinkException("uri resolution failed for '" + srcAttribute + "' at Instance id: '" + this.getId() + "'", e, this.model.getTarget(), map);
//            throw new XFormsLinkException("uri resolution failed for '" + srcAttribute + "' at Instance id: '" + this.getId() + "'", e, this.model.getTarget(), s
// rcAttribute);
        }

            if (result instanceof Document) {
                return ((Document) result).getDocumentElement();
            }

            if (result instanceof Element) {
                return (Element) result;
            }
        
            if (result instanceof String) {
                try {
                    return DOMUtil.parseString((String) result, true, false).getDocumentElement();
                } catch (Exception e) {
                    throw new XFormsLinkException("object model not supported", this.model.getTarget(), srcAttribute);
                }
            }

        throw new XFormsLinkException("object model not supported", this.model.getTarget(), srcAttribute);
    }

    /**
     * Returns a new created instance document.
     * <p/>
     * If this instance has an original instance, it will be imported into this
     * new document. Otherwise the new document is left empty.
     *
     * @return a new created instance document.
     * @throws de.betterform.xml.xforms.exception.XFormsException
     */
    private Document createInstanceDocument() throws XFormsException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setValidating(false);
            Document document = factory.newDocumentBuilder().newDocument();

            if (this.initialInstance != null) {
                Node imported =document.importNode(this.initialInstance.cloneNode(true), true);
                document.appendChild(imported);

                if(getXFormsAttribute(SRC_ATTRIBUTE)==null && getXFormsAttribute(RESOURCE_ATTRIBUTE)==null){
                    NamespaceResolver.applyNamespaces(this.element, document.getDocumentElement());
                }
            }

            return document;
        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    public static ModelItem createModelItem(Node node) {
        String id = Model.generateModelItemId();
        ModelItem modelItem;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            modelItem = new XercesElementImpl(id);
        }
        else {
            modelItem = new XercesNodeImpl(id);
        }
        modelItem.setNode(node);

        Node parentNode;
        if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
            parentNode = ((Attr) node).getOwnerElement();
        }
        else {
            parentNode = node.getParentNode();
        }
        if (parentNode != null) {
            ModelItem parentItem = (ModelItem) parentNode.getUserData("");
            if (parentItem == null) {
                parentItem = createModelItem(parentNode);
            }

            modelItem.setParent(parentItem);
        }

        node.setUserData("", modelItem,null);
        return modelItem;
    }

    private void listModelItems(List list, Node node, boolean deep) {
        ModelItem modelItem = (ModelItem) node.getUserData("");
        if (modelItem == null) {
            modelItem = createModelItem(node);
        }
        list.add(modelItem);

        if (deep) {
            NamedNodeMap attributes = node.getAttributes();
            for (int index = 0; attributes != null && index < attributes.getLength(); index++) {
                listModelItems(list, attributes.item(index), deep);
            }
            if(node.getNodeType() !=  Node.ATTRIBUTE_NODE){
                NodeList children = node.getChildNodes();
                for (int index = 0; index < children.getLength(); index++) {
                    listModelItems(list, children.item(index), deep);
                }
            }            
        }
    }

    void storeContainerRef() {
        if(instanceDocument.getDocumentElement() != null){
            instanceDocument.getDocumentElement().setUserData("container",this.model.getContainer(),null);
            instanceDocument.getDocumentElement().setUserData("instance",this,null);
        }
    }


    private String toString(Node node) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            DOMUtil.prettyPrintDOM(node, stream);
        }
        catch (TransformerException e) {
            // nop
        }
        return System.getProperty("line.separator") + stream;
    }

    public void setLazyXPathContext(List parentNodeset) {
        this.xPathContext = new BetterFormXPathContext(parentNodeset,1,getPrefixMapping(),xpathFunctionContext);
    }
}

// end of class
