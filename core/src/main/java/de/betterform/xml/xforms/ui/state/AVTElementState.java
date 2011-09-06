/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui.state;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.ui.BindingElement;
import de.betterform.xml.xforms.ui.UIElementState;
import de.betterform.xml.xforms.ui.AVTElement;
import de.betterform.xml.ns.NamespaceConstants;
import org.w3c.dom.Element;
import org.w3c.dom.events.EventTarget;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

// end of class

/**
 * State keeper for helper elements.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: HelperElementState.java 3492 2008-08-27 12:37:01Z joern $
 */
public class AVTElementState implements UIElementState {

    private BindingElement owner;
    private Element state;
    private String currentValue;
    private String allowedAttributes;
    private Map attributeValueMap;

    /**
     * Creates a new output value state.
     */
    public AVTElementState() {
        // NOP
    }

    /**
     * Sets the owning element.
     *
     * @param owner the owning element.
     */
    public void setOwner(BindingElement owner) {
        this.owner = owner;
    }

    // implementation of 'de.betterform.xml.xforms.ui.UIElementState'

    /**
     * Initializes this element state.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException if an error occurred during init.
     */
    public void init() throws XFormsException {
        this.allowedAttributes = this.owner.getElement().getOwnerDocument().getDocumentElement().getAttributeNS(NamespaceConstants.BETTERFORM_NS,"evalAVTs");
        String[] attrs = allowedAttributes.split(" ");
        for(String attribute : attrs){

            if(this.owner.getElement().hasAttribute(attribute) && this.owner.getElement().getAttribute(attribute).indexOf("{")!=-1){
                this.attributeValueMap = new HashMap(attrs.length);
                String expression = this.owner.getElement().getAttribute(attribute);
                this.attributeValueMap.put(attribute,expression );

                Object result=((AVTElement)this.owner).evalAttributeValueTemplates(expression,this.owner.getElement());
                this.owner.getElement().setAttribute(attribute,result.toString());
            }
        }

    }

    /**
     * Updates this element state.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException if an error occurred during update.
     */
    public void update() throws XFormsException {
        // get model item and its current properties
        ModelItem modelItem = UIElementStateUtil.getModelItem(this.owner);
        boolean[] properties = UIElementStateUtil.getModelItemProperties(modelItem);
        Container container = owner.getContainerObject();

        Set keyset = attributeValueMap.keySet();
        for (Iterator iterator = keyset.iterator(); iterator.hasNext();) {
            String attribute = (String) iterator.next();
            Object result=((AVTElement)this.owner).evalAttributeValueTemplates((String) attributeValueMap.get(attribute),this.owner.getElement());
            this.owner.getElement().setAttribute(attribute,result.toString());

            final Map contextInfo = new HashMap(2);
            contextInfo.put("attribute",attribute);
            contextInfo.put("value",result);
            container.dispatch(owner.getTarget(), BetterFormEventNames.AVT_CHANGED, contextInfo);

        }
    }

    /**
     * Disposes this element state.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException if an error occurred during disposal.
     */
    public void dispose() throws XFormsException {
        // remove any content and free resources
        DOMUtil.removeAllChildren(this.owner.getElement());
        this.owner = null;
    }

    /**
     * Returns the current value.
     *
     * @return the current value.
     */
    public String getValue() {
        return this.currentValue;
    }

    /**
     * Sets an arbitrary property.
     * <p/>
     * Does nothing in this implementation.
     *
     * @param name the property name.
     * @param value the property value.
     */
    public void setProperty(String name, Object value) {
    }

    public Object getSchemaValue(){
        return null;
    }


}
