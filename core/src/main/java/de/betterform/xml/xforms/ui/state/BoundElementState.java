/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui.state;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.ui.BindingElement;
import de.betterform.xml.xforms.ui.UIElementState;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * State keeper for bound elements.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: BoundElementState.java 3496 2008-08-28 09:58:15Z joern $
 */
public class BoundElementState implements UIElementState {

    private boolean handleTypes;
    private boolean handleValue;
    private BindingElement owner;
    protected Element state;
    private boolean[] currentProperties;
    private Map<String, String> currentCustomProperties;
    private String currentType;
    private Object currentValue;
    private boolean dispatchValueChange;
    protected static Log LOGGER = LogFactory.getLog(BoundElementState.class);
    /**
     * Creates a new bound element state.
     */
    public BoundElementState() {
        this(true, true);
    }

    /**
     * Creates a new bound element state.
     *
     * @param handleTypes handle types or not.
     * @param handleValue handle value or not.
     */
    public BoundElementState(boolean handleTypes, boolean handleValue) {
        this.handleTypes = handleTypes;
        this.handleValue = handleValue;

        this.dispatchValueChange = true;
    }

    public String getType(){
        return this.currentType;
    }

    // implementation of 'de.betterform.xml.xforms.ui.UIElementState'

    /**
     * Sets the owning element.
     *
     * @param owner the owning element.
     */
    public void setOwner(BindingElement owner) {
        this.owner = owner;
    }

    /**
     * Initializes this element state.
     *
     * @throws XFormsException if an error occurred during init.
     */
    public void init() throws XFormsException {
        // create state element
        this.state = UIElementStateUtil.createStateElement(this.owner.getElement());

        // set model item properties and value only before xforms-ready (not during repeat processing)
        ModelItem modelItem = UIElementStateUtil.getModelItem(this.owner);
       if (!this.owner.getModel().isReady()) {
            // get model item and its current properties

            boolean[] properties = UIElementStateUtil.getModelItemProperties(modelItem);

            setProperties(properties);

            // keep properties
            this.currentProperties = properties;

            if (modelItem != null) {
                // set types
                if (this.handleTypes) {
                    String datatype = UIElementStateUtil.getDatatype(modelItem, this.owner.getElement());
                    UIElementStateUtil.setStateAttribute(this.state, TYPE_ATTRIBUTE, datatype);

                    // keep datatype
                    this.currentType = datatype;

                    //Set base-type
                    if (! KnownDataType.isKnownDataType(this.currentType, this.owner.getElement())) {
                        if (modelItem.getNode() instanceof Element) {
                            UIElementStateUtil.setStateAttribute(this.state, BASE_TYPE, UIElementStateUtil.getBaseType(modelItem.getModel().getSchemas(), this.currentType, ((Element) modelItem.getNode()).getNamespaceURI()));
                        } else {
                            UIElementStateUtil.setStateAttribute(this.state, BASE_TYPE, UIElementStateUtil.getBaseType(modelItem.getModel().getSchemas(), this.currentType, ((Node) modelItem.getNode()).getNamespaceURI()));
                        }
                    }
                }

                //set value
                if(this.handleValue)
                {
                	//attempt to store the subtree (xforms:copy)
                    if(modelItem.getNode() != null && modelItem.getNode() instanceof Element)
                    {
                    	this.currentValue = storeSubtree(modelItem);
                    	Element childElement = DOMUtil.getFirstChildElement((Element)modelItem.getNode());
                    	if(childElement != null)
                    		return;
                    }
                    
	            	//otherwise fallback to a string value
	                String value = modelItem.getValue();
                    UIElementStateUtil.setStateAttribute(this.state, SCHEMA_VALUE, value);
                    value = UIElementStateUtil.localiseValue(this.owner,this.state,this.currentType,value);
	                DOMUtil.setElementValue(this.state, value);
	                this.currentValue = value;
                }
                
                // There will normally not be that many custom MIPS so an initial value of 2 is good.
                // Adding 1 will not increase the size since the default loadfactor is 0.75
                this.currentCustomProperties = new HashMap<String, String>(2);
                this.currentCustomProperties.putAll(modelItem.getLocalUpdateView().getCustomMIPValues());
				if (this.currentCustomProperties != null) {
					for (String key : this.currentCustomProperties.keySet()) {
						UIElementStateUtil.setStateAttribute(this.state,
								key, this.currentCustomProperties.get(key));
					}
				}
            }
        } else {
           modelItem = UIElementStateUtil.getModelItem(this.owner);
           if (modelItem != null) {
               this.currentType = modelItem.getDeclarationView().getDatatype();
               //Set base-type
               if (! KnownDataType.isKnownDataType(this.currentType, this.owner.getElement())) {
                   if (modelItem.getNode() instanceof Element) {
                       UIElementStateUtil.setStateAttribute(this.state, BASE_TYPE, UIElementStateUtil.getBaseType(modelItem.getModel().getSchemas(), this.currentType, ((Element) modelItem.getNode()).getNamespaceURI()));
                   } else {
                       UIElementStateUtil.setStateAttribute(this.state, BASE_TYPE, UIElementStateUtil.getBaseType(modelItem.getModel().getSchemas(), this.currentType, ((Node) modelItem.getNode()).getNamespaceURI()));
                   }
               }
           } else {
               LOGGER.debug("ModelItem = null");
           }
       }

    }

    /**
     * stores an XML subtree as child of the bf:data element for controls that work with such
     * a structure instead of a simple value. This is especially interesting for e.g. output/@appearance="dojo:tree"
     * which allows to visualize an abritrary piece of an instance data tree.
     *
     * @param modelItem the modelItem which has a subtree as value
     * @return the stored node or null
     */
    protected Node storeSubtree(ModelItem modelItem)
    {
        Object o = modelItem.getNode();
        if(o instanceof Element)
        {
            if(DOMUtil.getFirstChildElement( (Element)o ) != null)
            {
                Node n = (Node) modelItem.getNode();
                if(n != null)
                {
                    Node imported = this.state.getOwnerDocument().importNode(n,true);
                    return this.state.appendChild(imported);
                }
            }
        }
        
        return null;
    }

    /**
     * Updates this element state.
     *
     * @throws XFormsException if an error occurred during update.
     */
    public void update() throws XFormsException
    {
        // get model item and its current properties
        ModelItem modelItem = UIElementStateUtil.getModelItem(this.owner);
        boolean[] properties = UIElementStateUtil.getModelItemProperties(modelItem);

        // update properties
        setProperties(properties);

        // update types
        String datatype = null;
        if(this.handleTypes){
            datatype = modelItem != null ? UIElementStateUtil.getDatatype(modelItem, this.owner.getElement()) : null;
            String p3ptype = modelItem != null ? modelItem.getDeclarationView().getP3PType() : null;
            UIElementStateUtil.setStateAttribute(this.state, TYPE_ATTRIBUTE, datatype);
            UIElementStateUtil.setStateAttribute(this.state, P3PTYPE_ATTRIBUTE, p3ptype);

            if (! KnownDataType.isKnownDataType(this.currentType, this.owner.getElement())) {
                if (modelItem != null && modelItem.getNode() != null && modelItem.getNode() instanceof Element) {
                    UIElementStateUtil.setStateAttribute(this.state, BASE_TYPE, UIElementStateUtil.getBaseType(modelItem.getModel().getSchemas(), this.currentType, ((Element) modelItem.getNode()).getNamespaceURI()));
                }
            }
        }

        // update value
        Object value = null;
        if(this.handleValue){
           // if(currentType == null) this.currentType = datatype;

        	//attempt to store the subtree (xforms:copy)
        	if(modelItem != null && modelItem.getNode() instanceof Element){
        		value = storeSubtree(modelItem);
        		Element childElement = DOMUtil.getFirstChildElement((Element)modelItem.getNode());
        		if(childElement == null){
                    //otherwise fallback to a string value
                    value = modelItem != null ? modelItem.getValue() : null;
                    UIElementStateUtil.setStateAttribute(this.state, SCHEMA_VALUE, (String) value);

                    value = UIElementStateUtil.localiseValue(this.owner, this.state, this.currentType, (String) value);
                    DOMUtil.setElementValue(this.state, (String) value);
                }
        	}
        	else{
                //otherwise fallback to a string value
                value = modelItem != null ? modelItem.getValue() : null;
                UIElementStateUtil.setStateAttribute(this.state, SCHEMA_VALUE, (String) value);
                value = UIElementStateUtil.localiseValue(this.owner, this.state, this.currentType, (String) value);
                DOMUtil.setElementValue(this.state, (String) value);
            }
        }

        // dispatch xforms events
        UIElementStateUtil.dispatchXFormsEvents(this.owner, modelItem);
        if(this.dispatchValueChange){
            //dispatch property, value, and type change events because the owner's external value change caused this update
            if(datatype==null){
                datatype = this.currentType;
            }
            UIElementStateUtil.dispatchBetterFormEvents(this.owner, this.currentProperties, this.currentValue, this.currentType, properties, value, datatype);
            this.currentType = datatype;
            //Set base-type
            if (! KnownDataType.isKnownDataType(this.currentType, this.owner.getElement())) {
                if ( modelItem != null && modelItem.getNode() != null) {
                    if (modelItem.getNode() instanceof Element) {
                        UIElementStateUtil.setStateAttribute(this.state, BASE_TYPE, UIElementStateUtil.getBaseType(modelItem.getModel().getSchemas(), this.currentType, ((Element) modelItem.getNode()).getNamespaceURI()));
                    } else {
                        UIElementStateUtil.setStateAttribute(this.state, BASE_TYPE, UIElementStateUtil.getBaseType(modelItem.getModel().getSchemas(), this.currentType, ((Node) modelItem.getNode()).getNamespaceURI()));
                    }
                }
            }
        }
        else{
            // don't dispatch value and type change events because the owner's external value change caused this update
            UIElementStateUtil.dispatchBetterFormEvents(this.owner, this.currentProperties, properties);

            // reset automatically to avoid incosistencies
            this.dispatchValueChange = true;
        }

        //store properties and value
        this.currentProperties = properties;
        this.currentValue = value;

		if (modelItem != null) {
			Map<String, String> customProperties = modelItem.getLocalUpdateView()
					.getCustomMIPValues();
			if (customProperties != null) {

				UIElementStateUtil.dispatchBetterFormCustomMIPEvents(
						this.owner, this.currentCustomProperties,
						customProperties);
				this.currentCustomProperties = new HashMap<String, String>();
				this.currentCustomProperties.putAll(customProperties);
			}
		}
    }

    /**
     * Disposes this element state.
     *
     * @throws XFormsException if an error occurred during disposal.
     */
    public void dispose() throws XFormsException {
        // remove any content and free resources
        this.state.getParentNode().removeChild(this.state);
        this.state = null;
        this.owner = null;
    }

    /**
     * Returns the current value.
     *
     * @return the current value.
     */
    public Object getValue() {
        return this.handleValue ? this.currentValue : null;
    }

    public Object getSchemaValue() {
        return UIElementStateUtil.getStateAttribute(this.state, SCHEMA_VALUE);
    }

    /**
     * Sets an arbitrary property.
     * <p/>
     * Only <code>dispatchValueChange</code> is reccognized.
     *
     * @param name the property name.
     * @param value the property value.
     */
    public void setProperty(String name, Object value) {
        if (("dispatchValueChange").equals(name)) {
            this.dispatchValueChange = Boolean.valueOf(String.valueOf(value)).booleanValue();
        } else {
            UIElementStateUtil.setStateAttribute(this.state, name, (String) value);
        }
    }

    private void setProperties(boolean[] properties) {
        // set properties
        UIElementStateUtil.setStateAttribute(this.state, VALID_PROPERTY, String.valueOf(properties[UIElementStateUtil.VALID]));
        UIElementStateUtil.setStateAttribute(this.state, READONLY_PROPERTY, String.valueOf(properties[UIElementStateUtil.READONLY]));
        UIElementStateUtil.setStateAttribute(this.state, REQUIRED_PROPERTY, String.valueOf(properties[UIElementStateUtil.REQUIRED]));
        UIElementStateUtil.setStateAttribute(this.state, ENABLED_PROPERTY, String.valueOf(properties[UIElementStateUtil.ENABLED]));
    }


}

// end of class
