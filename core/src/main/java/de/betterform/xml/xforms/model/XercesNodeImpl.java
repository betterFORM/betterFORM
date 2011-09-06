/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.model.bind.DeclarationView;
import de.betterform.xml.xforms.model.bind.LocalUpdateView;
import de.betterform.xml.xforms.model.bind.RefreshView;
import de.betterform.xml.xforms.model.bind.StateChangeView;
import de.betterform.xml.xforms.model.bind.impl.DeclarationViewImpl;
import de.betterform.xml.xforms.model.bind.impl.LocalUpdateViewImpl;
import de.betterform.xml.xforms.model.bind.impl.RefreshViewImpl;
import de.betterform.xml.xforms.model.bind.impl.StateChangeViewImpl;
import org.w3c.dom.Node;

/**
 * ModelItem implementation based on Xerces' NodeImpl.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XercesNodeImpl.java 2354 2006-10-04 18:41:48Z unl $
 */
class XercesNodeImpl implements ModelItem {
    protected static Log LOGGER = LogFactory.getLog(XercesNodeImpl.class);

    private String id;
    protected Node node;
    private ModelItem parent;
    private DeclarationViewImpl declarationView;
    private LocalUpdateViewImpl localUpdateView;
    private StateChangeViewImpl stateChangeView;
    private RefreshView refreshView;
    private String filename;
    private String mediatype;

    /**
     * Creates a new Xerces NodeImpl based ModelItem implementation.
     *
     * @param id the id of this model item.
     */
    public XercesNodeImpl(String id) {
        this.id = id;
        this.declarationView = new DeclarationViewImpl();
        this.localUpdateView = new LocalUpdateViewImpl();
        this.stateChangeView = new StateChangeViewImpl(this);
        this.refreshView = new RefreshViewImpl(this);
    }

    /**
     * Returns the id of this model item.
     *
     * @return the id of this model item.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the node of this model item.
     *
     * @return the node of this model item.
     */
    public Object getNode() {
        return this.node;
    }

    /**
     * Stes the node of this model item.
     *
     * @param node the node of this model item.
     */
    public void setNode(Object node) {
        this.node = (Node) node;
    }

    /**
     * Returns the parent of this model item.
     *
     * @return the parent of this model item.
     */
    public ModelItem getParent() {
        return this.parent;
    }

    /**
     * Sets the parent of this model item.
     *
     * @param parent the parent of this model item.
     */
    public void setParent(ModelItem parent) {
        this.parent = parent;
    }

    /**
     * Returns the computed <code>readonly</code> state.
     * <p/>
     * A model item is readonly when its readonly property evaluates to
     * <code>true</code> <i>or</i> its parent item is readonly.
     *
     * @return the computed <code>readonly</code> state.
     */
    public boolean isReadonly() {
        if (this.parent != null) {
            return this.localUpdateView.isLocalReadonly() || this.parent.isReadonly();
        }

        return this.localUpdateView.isLocalReadonly();
    }

    /**
     * Returns the computed <code>required</code> state.
     * <p/>
     * A model item is required when its required property evaluates to
     * <code>true</code>.
     *
     * @return the computed <code>required</code> state.
     */
    public boolean isRequired() {
        return this.localUpdateView.isLocalRequired();
    }

    /**
     * Returns the computed <code>enabled</code> state.
     * <p/>
     * A model item is enabled when its relevant property evaluates to
     * <code>true</code> <i>and</i> its parent item is enabled.
     *
     * @return the computed <code>enabled</code> state.
     */
    public boolean isRelevant() {
        if (this.parent != null) {
            return this.localUpdateView.isLocalRelevant() && this.parent.isRelevant();
        }

        return this.localUpdateView.isLocalRelevant();
    }

    /**
     * Returns the computed <code>valid</code> state.
     * <p/>
     * A model item is valid when its constraint property evaluates to
     * <code>true</code> <i>and</i> the type property is satisfied.
     *
     * @return the computed <code>valid</code> state.
     */
    public boolean isValid() {
        return this.localUpdateView.isConstraintValid() && this.localUpdateView.isDatatypeValid();
    }

    /**
     * Returns the value of this model item.
     *
     * @return the value of this model item.
     */
    public String getValue() {
        return this.node.getNodeValue();
    }

    /**
     * Sets the value of this model item.
     *
     * @param value the value of this model item.
     */
    public boolean setValue(String value) {
        if (valueChanged(value)) {
            this.node.setNodeValue(value);
            return true;
        }
        return false;
    }

    /**
     * Returns the declaration view of this model item.
     *
     * @return the declaration view of this model item.
     */
    public DeclarationView getDeclarationView() {
        return this.declarationView;
    }

    /**
     * Returns the local update view of this model item.
     *
     * @return the local update view of this model item.
     */
    public LocalUpdateView getLocalUpdateView() {
        return this.localUpdateView;
    }

    /**
     * Returns the state change view of this model item.
     *
     * @return the state change view of this model item.
     */
    public StateChangeView getStateChangeView() {
        return this.stateChangeView;
    }

    public RefreshView getRefreshView() {
        return this.refreshView;
    }

    /**
     * Checks wether this model item is nillable.
     * <p/>
     * A model item is considered nillable if it is an element and has a
     * <code>xsi:nil</code> attribute with the value <code>true</code>.
     *
     * @return <code>true</code> if this model item is nillable, otherwise
     *         <code>false</code>.
     */
    public boolean isXSINillable() {
        return false;
    }

    /**
     * Returns the additional schema type declaration of this model item.
     * <p/>
     * A model item has an additional schema type declaration if it is an
     * element and has a <code>xsi:type</code> attribute.
     *
     * @return the additional schema type declaration of this model item or
     *         <code>null</code> if there is no such type declaration.
     */
    public String getXSIType() {
        return null;
    }

    // todo: file upload fixes, the following methods are needed for form-data serialization ...
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setMediatype(String mediatype) {
        this.mediatype = mediatype;
    }

    public String getMediatype() {
        return this.mediatype;
    }

    // helper

    /**
     * Check wether the new value differs from the current value. If so, the
     * state change view will be notified to set the corresponding flag.
     *
     * @param value the new value of this model item.
     * @return <code>true</code> if the new value differs from the current
     *         value, otherwise <code>false</code>
     */
    protected final boolean valueChanged(String value) {
        // check current value
        String current = getValue();
        if ((current == null && value == null) || (current != null && current.equals(value))) {
            return false;
        }

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug(DOMUtil.getCanonicalPath(this.node) + " changed: oldvalue='" + current + "' newvalue='" + value + "'");
        }
        // notify state change view
        this.stateChangeView.setValueChanged(); //todo: remove this once refreshview is in place
        this.refreshView.setValueChangedMarker();
        return true;
    }

    public Model getModel() {
        if(this.node != null) {
            Instance instance = (Instance) this.node.getOwnerDocument().getDocumentElement().getUserData("instance");
            if(instance != null) {
                return instance.getModel();
            }

        }
        LOGGER.warn("Node is probably null");
        return null;
    }

    public String toString() {
        return DOMUtil.getCanonicalPath(this.node);
    }

}
