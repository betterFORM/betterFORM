/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.Initializer;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.bind.Binding;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of <b>8.2.2 The item Element</b>.
 * <p/>
 * When a position and an itemset element is provided, this element behaves as a
 * positional item inserting positional information during binding resolution
 * (just like a repeat item).
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @author Lars Windauer
 * @version $Id: Item.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class Item extends AbstractUIElement implements Binding {
    private static final Log LOGGER = LogFactory.getLog(Item.class);

    private int position;
    private Itemset itemset;
    
    private String instanceId;
    private boolean hasCopyChild = false;

    /**
     * Creates a new item element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Item(Element element, Model model) {
        super(element, model);
    }
    
    /**
     * Returns the id of the instance this element is bound to.
     * <p/>
     * The instance id is determined as follows: <ol> <li>If the location path
     * starts with a <code>instance()</code> function, the instance id is
     * obtained from the argument of that fuction.</li> <li>If there is a
     * default instance in the corresponding model, the instance id is obtained
     * from the default instance.</li> <li>The instance id is set empty, which
     * maps to the default instance.</li> </ol>
     *
     * @return the id of the instance this element is bound to.
     */
    public String getInstanceId() throws XFormsException {
        // lazy initialization
        if (this.instanceId == null) {
            this.instanceId = this.model.computeInstanceId(getLocationPath());
        }

        return this.instanceId;
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

        if (getXFormsAttribute(SELECTED_ATTRIBUTE) == null) {
            this.element.setAttributeNS(null, SELECTED_ATTRIBUTE, String.valueOf(false));
        }

        if (this.itemset != null) {
            //todo: not sure if the right thing happens here: repeat-id + repeat-item are not passed
            Initializer.initializeUIElements(this.model, this.element, this.id,this.element.getOwnerDocument().getDocumentElement().getAttributeNS(NamespaceConstants.BETTERFORM_NS,"evalAVTs"));
            Initializer.initializeActionElements(this.model, this.element, this.id);
            Element copy = DOMUtil.findFirstChildNS(this.element, NamespaceConstants.XFORMS_NS, COPY);
            if(copy != null) {
                hasCopyChild = true;
            }
            return;
        }

        initializeChildren();
        initializeActions();
    }

    public List getNodeset() {
	if (this.itemset != null) {
        	List repeatNodeset = itemset.getNodeset();
        	int localPosition = getPosition();
        	return repeatNodeset.size() >=localPosition?Collections.singletonList(repeatNodeset.get(localPosition - 1)):Collections.EMPTY_LIST;
	}
	return null;
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

    // implementation of 'de.betterform.xml.xforms.model.bind.Binding'

    /**
     * Returns the binding expression.
     *
     * @return the binding expression.
     */
    public String getBindingExpression() {
        if (this.itemset != null) {
            return this.itemset.getBindingExpression() + "[" + getPosition() + "]";
        }

        return null;
    }

    /**
     * Returns the id of the binding element.
     *
     * @return the id of the binding element.
     */
    public String getBindingId() {
        if (this.itemset != null) {
            return this.itemset.getBindingId();
        }

        return null;
    }

    /**
     * Returns the enclosing element.
     *
     * @return the enclosing element.
     */
    public Binding getEnclosingBinding() {
        if (this.itemset != null) {
            return this.itemset.getEnclosingBinding();
        }

        return null;
    }

    /**
     * Returns the location path.
     *
     * @return the location path.
     */
    public String getLocationPath() {
        if (this.itemset != null) {
            return this.itemset.getLocationPath() + "[" + getPosition() + "]";
        }

        return null;
    }

    /**
     * Returns the model id of the binding element.
     *
     * @return the model id of the binding element.
     */
    public String getModelId() {
        if (this.itemset != null) {
            return this.itemset.getModelId();
        }

        return null;
    }

    public boolean hasBindingExpression() {
        if(getBindingExpression() != null)
            return true;
        else
            return false;
    }

    // item specific methods

    /**
     * Checks wether this item is selected or not.
     *
     * @return <code>true</code> if this item is selected, otherwise
     *         <code>false</code>.
     */
    public boolean isSelected() {
        String selectedAttribute = getXFormsAttribute(SELECTED_ATTRIBUTE);
        return ("true").equals(selectedAttribute);
    }

    /**
     * Selects this item.
     */
    public void select() {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("selecting Item: " + this.id);
        }
        
        this.element.setAttributeNS(null, SELECTED_ATTRIBUTE, String.valueOf(true));
        
        if(this.itemset != null)
        {
            // todo: handle copy ?
        	
/*
        	XFormsElement parent = this.itemset.getParentObject();
        	if(parent instanceof Selector)
        	{
        		try
        		{
        			Element copyElem = findElement(this.element.getChildNodes(), NamespaceConstants.XFORMS_NS, "copy");
        			String copyId = copyElem.getAttribute("id");

        			//send the xforms:copy/@id into Selector.setValue() it knows what to do with it :-)
        			((Selector)parent).setValue(copyId);

        			//model.rebuild(); //todo: needed?
        		}
        		catch(XFormsException xfe)
        		{
        			System.err.println(xfe);
        		}
        	}
*/
        }
    }
    
    /**
     * Deselects this item.
     */
    public void deselect() {
        this.element.setAttributeNS(null, SELECTED_ATTRIBUTE, String.valueOf(false));
        if (this.itemset != null) {
            // todo: handle copy ?
        }
    }

    private Element findElement(NodeList nodes, String namespace, String localName)
    {
    	for(int i = 0; i < nodes.getLength(); i++)
    	{
    		Node child = nodes.item(i);
    		if(child instanceof Element)
    		{
    			if(child.getNamespaceURI().equals(namespace) && child.getLocalName().equals(localName))
    			{
    				return (Element)child;
    			}
    		}
    	}
    	return null;
    }
    
    /**
     * Returns the current value of a value element or <code>null</code> if
     * there is no value element.
     *
     * @return the current value of a value element or <code>null</code> if
     *         there is no value element.
     */
    public Object getValue() {

    	Element copy = DOMUtil.findFirstChildNS(this.element, NamespaceConstants.XFORMS_NS, COPY);
    	if(copy == null)
    	{
    		//no copy, look for value
    		Element value = DOMUtil.findFirstChildNS(this.element, NamespaceConstants.XFORMS_NS, VALUE);
            if (value != null)
            {
                return DOMUtil.getTextNodeAsString(value);
            }
    	}
    	else
    	{
    		//we have a copy, get the child of the bf:data node
    		Element betterformData = DOMUtil.findFirstChildNS(copy, NamespaceConstants.BETTERFORM_NS, "data");
    		if(betterformData != null)
    		{
    			Element firstChild = DOMUtil.getFirstChildElement(betterformData);
    			if(firstChild != null)
    			{
    				return firstChild;
    			}
    		}
    	}
    	
    	return null;
    }

    public Object getLabel() {
        Element label = DOMUtil.findFirstChildNS(this.element, NamespaceConstants.XFORMS_NS, LABEL);
        return DOMUtil.getTextNodeAsString(label);
    }

    /**
     * Returns the position of this item.
     *
     * @return the position of this item.
     */
    public int getPosition() {
        return this.position;
    }

    /**
     * Sets the position of this item.
     *
     * @param position the position of this item.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Returns the itemset this item belongs to.
     *
     * @return the itemset this item belongs to.
     */
    public Itemset getItemset() {
        return this.itemset;
    }

    /**
     * Sets the itemset this item belongs to.
     *
     * @param itemset the itemset this item belongs to.
     */
    public void setItemset(Itemset itemset) {
        this.itemset = itemset;
    }

    public boolean hasCopyChild() {
        return hasCopyChild;
    }

    // template methods

    /**
     * Returns the logger object.
     *
     * @return the logger object.
     */
    protected Log getLogger() {
        return LOGGER;
    }

}

// end of class
