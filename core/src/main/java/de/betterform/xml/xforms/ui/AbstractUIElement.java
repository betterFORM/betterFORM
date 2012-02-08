/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.Initializer;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This is the base class for all UI elements. It provides lifecycle methods for
 * component style control.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: AbstractUIElement.java 2959 2007-10-26 11:17:31Z lars $
 */
public abstract class AbstractUIElement extends XFormsElement {
     protected static Log LOGGER = LogFactory.getLog(AbstractUIElement.class);
    /**
     * The id of the containing repeat item if any.
     */
    protected String repeatItemId;

    /**
     * Creates a new abstract ui element.
     *
     * @param element the host document element.
     * @param model   the context model.
     */
    public AbstractUIElement(Element element, Model model) {
        super(element, model);
    }

    // repeat stuff

    /**
     * Sets the id of the repeat item this element is contained in.
     *
     * @param repeatItemId the id of the repeat item this element is contained
     *                     in.
     */
    public void setRepeatItemId(String repeatItemId) throws XFormsException {
        this.repeatItemId = repeatItemId;
    }

    /**
     * Returns the id of the repeat item this element is contained in.
     *
     * @return the id of the repeat item this element is contained in.
     */
    public String getRepeatItemId() {
        return this.repeatItemId;
    }

    /**
     * Checks wether this element is repeated.
     *
     * @return <code>true</code> if this element is contained in a repeat item,
     *         <code>false</code> otherwise.
     */
    public boolean isRepeated() {
        return this.repeatItemId != null;
    }

    // todo: extract interface
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

        initializeChildren();
    }

    /**
     * Performs element update.
     *
     * @throws XFormsException if any error occurred during update.
     */
    public void refresh() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " update");
        }

        updateChildren();
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

        disposeChildren();
        disposeSelf();
    }

    public String getTargetReference() {
        if(isRepeated()){
            return getTargetReference(this.element,this.id);
        }else{
            return this.id;
        }
    }

    protected String getTargetReference(Element node,String targetRef){

        if(node == null) return targetRef;
        if(!(node instanceof Element)) return targetRef;

        Object xfObject = ((Element)node).getUserData("");
        if(xfObject != null && xfObject instanceof RepeatItem){
            int position = ((RepeatItem)xfObject).getPosition();
            targetRef = "," + position+  "]/" + targetRef;

        } else if (xfObject != null && xfObject instanceof Repeat) {
            int position = ((Repeat)xfObject).getPosition();
            targetRef = "[" + position+  "" + targetRef;

        }
        Node parent = node.getParentNode();
        if (parent.getNodeType() == Node.DOCUMENT_NODE || parent.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE || parent == null) {
            return "/" + targetRef;
        }else if(parent.getNodeType() == Node.ELEMENT_NODE){
            return getTargetReference((Element) parent,targetRef);
        }else{
            LOGGER.warn("Unkown type: " + parent);
            return targetRef;
        }
    }

    // lifecycle template methods

    /**
     * Initializes all action children of this element.
     *
     * @throws XFormsException if any error occurred during init.
     */
    protected final void initializeActions() throws XFormsException {
        Initializer.initializeActionElements(this.model, this.element, this.repeatItemId);
    }

    /**
     * Initializes all ui children of this element.
     *
     * @throws XFormsException if any error occurred during init.
     */
    protected final void initializeChildren() throws XFormsException {
        Initializer.initializeUIElements(this.model, this.element, this.repeatItemId , this.element.getOwnerDocument().getDocumentElement().getAttributeNS(NamespaceConstants.BETTERFORM_NS,"evalAVTs"));
    }

    /**
     * Updates all ui children of this element.
     *
     * @throws XFormsException if any error occurred during update.
     */
    protected void updateChildren() throws XFormsException {
        Initializer.updateUIElements(this.element,this.model);
    }

    /**
     * Disposes all ui children of this element.
     *
     * @throws XFormsException if any error occurred during disposal.
     */
    protected final void disposeChildren() throws XFormsException {
        Initializer.disposeUIElements(this.element);
    }

    /**
     * Disposes this element.
     * <p/>
     * The element is detached from its DOM node and deregisters itself with the
     * container.
     */
    protected final void disposeSelf() {
        this.element.setUserData("",null,null);
        deregister();
    }

}

// end of class
