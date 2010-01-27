/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import net.sf.saxon.om.NodeInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.dom.DOMComparator;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.*;
import java.util.*;

/**
 * Implementation of both <b>8.1.10 The select Element</b> and <b>8.1.11 The
 * select1 Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @author Lars Windauer
 * @version $Id: Selector.java 3510 2008-08-31 14:39:56Z lars $
 */
public class Selector extends AbstractFormControl {
    private static final Log LOGGER = LogFactory.getLog(Selector.class);
    private boolean copySkeleton = false;
    private boolean multiple;
    private final Map extendedPrefixMapping;
    private boolean selectHasCopyChilds = false;

    /**
     * Creates a new selector element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Selector(Element element, Model model) {
        super(element, model);
        
        
        extendedPrefixMapping = new HashMap(prefixMapping);
        extendedPrefixMapping.put(NamespaceConstants.BETTERFORM_PREFIX, NamespaceConstants.BETTERFORM_NS);
        extendedPrefixMapping.put(NamespaceConstants.XFORMS_PREFIX, NamespaceConstants.XFORMS_NS);
        this.container.getXMLEventService().registerDefaultAction(this.target, DOMEventNames.ACTIVATE, this);
    }

    @Override
    public void dispose() throws XFormsException {
        this.container.getXMLEventService().deregisterDefaultAction(this.target, DOMEventNames.ACTIVATE, this);
        super.dispose();
    }

    @Override
    public void performDefault(Event event) {
        super.performDefault(event);
        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("Selector: perform default event:" + event.getType());
        }
        XMLEvent ev = (XMLEvent) event;
        String contextInfo = (String) ev.getContextInfo().get("context-info");
        ArrayList list = new ArrayList();
        if(contextInfo !=null) {
            StringTokenizer tokens = new StringTokenizer(contextInfo,";");
            while (tokens.hasMoreTokens()) {
                list.add(tokens.nextElement());
            }
        }
		String itemId;
		Item item;

		Iterator iterator = null;
        List elemContext = de.betterform.xml.xpath.impl.saxon.XPathUtil.getElementContext(getElement(), container.getProcessor().getBaseURI());
        String xformsPrefix = null;
        if (this.xformsPrefix != null){
            xformsPrefix = this.xformsPrefix;
        }
        else {
            xformsPrefix = NamespaceConstants.XFORMS_PREFIX;
        }

        String xpath = "(" +  xformsPrefix + ":" + XFormsConstants.ITEM + " | *//" + xformsPrefix + ":" + XFormsConstants.ITEM + "[not(ancestor::" + NamespaceConstants.BETTERFORM_PREFIX + ":data)])/@id";

		try {
			iterator = XPathCache.getInstance().evaluate(elemContext, 1, xpath , extendedPrefixMapping, xpathFunctionContext).iterator();
        } catch (XFormsException e) {
            LOGGER.error("Error evaluating xpath " + xpath);
        }

		List<EventTarget> selectList = new ArrayList<EventTarget>();
		List<EventTarget> deselectList = new ArrayList<EventTarget>();


		while (iterator.hasNext()) {
			itemId = ((NodeInfo)iterator.next()).getStringValue();
			item = (Item) this.container.lookup(itemId);

			if(list.contains(item.getId()) && !item.isSelected()){
				item.select();
				selectList.add(item.getTarget());
			}else if(!list.contains(item.getId()) && item.isSelected()){
				item.deselect();
				deselectList.add(item.getTarget());
			}
		}

        for(EventTarget evt : deselectList){
            try {
                this.container.dispatch(evt, XFormsEventNames.DESELECT, null);
            } catch (XFormsException e) {
                LOGGER.error("Error dispatching " + XFormsEventNames.DESELECT + "  to target " + evt.toString());
            }
        }
        deselectList.clear();

        for(EventTarget evt : selectList){
            try {
                this.container.dispatch(evt, XFormsEventNames.SELECT, null);
            } catch (XFormsException e) {
                LOGGER.error("Error dispatching " + XFormsEventNames.SELECT + "  to target " + evt.toString());
            }
        }            
        selectList.clear();
    }

    /**
     * Checks wether this is a multiple selector or not.
     *
     * @return <code>true</code> if this is a multiple selector, otherwise
     *         <code>false</code>.
     */
    public boolean isMultiple() {
        return this.multiple;
    }

    /**
     * Specifies the behaviour of this selector.
     *
     * @param multiple specifies wether to allow multiple selections.
     */
    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }


    public List getNodeset() {
        try {
            updateXPathContext();
        } catch (XFormsException e) {
            LOGGER.error("Could not update XPathContext for Selector " +this.getBindingId());
        }
        return nodeset;
    }
    /**
     * Sets the value of this form control.
     * <p/>
     * The bound instance data is updated and the event sequence for this
     * control is executed. Event sequences are described in Chapter 4.6 of
     * XForms 1.0 Recommendation.
     *
     * @param value the value to be set.
     */
    public void setValue(String value) throws XFormsException
    {
        if(isBound()) {
            //test for an xforms:copy id as the value
            String values[] = value.split(" ");
            de.betterform.xml.xforms.XFormsElement xfe = null;
            if (values.length == 1) {
                xfe = container.lookup(value);
            } else {
                xfe = container.lookup(values[0]);
            }

            if (xfe != null) {
                if (xfe instanceof Copy) {

                    //found an xforms:copy
                    DOMUtil.removeAllChildren((Node) model.getInstance(getInstanceId()).getModelItem(getInstanceNode()).getNode());

                    //copy the node from each selected xforms:copy into the instance
                    for (int i = 0; i < values.length; i++) {
                        Copy xfCopy = (Copy) xfe;
                        Object xfCopyInstanceNode = xfCopy.getInstanceNode();
                        Instance instance = model.getInstance(getInstanceId());
                        Node instanceNode = getInstanceNode();
                        if (instanceNode != null && xfCopyInstanceNode != null && xfCopyInstanceNode instanceof Element) {
                            try {
                                instance.setNode(instanceNode, (Element) xfCopyInstanceNode);
                            } catch (Exception e) {
                                this.container.dispatch(this.getTarget(), XFormsEventNames.BINDING_EXCEPTION, null);
                                throw new XFormsBindingException("Node to copy does not exist", e, this.getTarget(), null);

                            }
                        } else {
                            throw new XFormsException("error during Selector.setValue: instance: " + instance + " node to copy: " + xfCopyInstanceNode);
                        }

                        dispatchSelectionWithoutValueChange(xfCopyInstanceNode);

                        if (i + 1 < values.length) {
                            xfe = container.lookup(values[i + 1]);
                            if (!(xfe instanceof Copy)) {
                                throw new XFormsException("Control with the id = '" + value + "' is not an xforms:copy");
                            }
                        }
                    }
                } else {
                    setControlValue(value);
                    LOGGER.warn("Control with the id = '" + value + "' is not an xforms:copy");
                }
            } else {

                if (isMultiple() && this.selectHasCopyChilds) {
                    Node instanceNode = getInstanceNode();
                    DOMUtil.removeAllChildren(instanceNode);
                } else {
                    setControlValue(value);
                }
            }
            dispatchValueChangeSequence();
        }
        }

    private void setControlValue(String value) throws XFormsException {
        //standard string value selection
        dispatchSelectionWithoutValueChange(value);
        setNodeValue(value);

    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " init");
        }
        // DOMUtil.prettyPrintDOM(this.element);
        if(this.element.hasAttribute("copySkeleton")){
            this.copySkeleton = true;
        }
        super.init();
        initializeSelection();
        // initializeActions();
    }

    /**
     * Performs element update.
     *
     * @throws XFormsException if any error occurred during update.
     */
    public void refresh() throws XFormsException {
        super.refresh();
        updateSelection();
    }

    // lifecycle template methods

    /**
     * Initializes all items' selection state.
     * <p/>
     * The selection state of all items is set according to the bound instance
     * value of this select.
     */
    protected void initializeSelection() throws XFormsException {
        if (isBound()) {
            setSelection(true, false, getValue());
        }
    }

    /**
     * Updates all items' selection state.
     * <p/>
     * The selection state of any items which is not set according to the bound
     * instance value of this select is updated.
     */
    protected void updateSelection() throws XFormsException {
        if (isBound()) {
            setSelection(false, false, getValue());
        }
    }

    /**
     * Updates all items' selection state and dispatches the appropriate
     * <code>xforms-select</code> and <code>xforms-deselect events</code>.
     * <p/>
     * The selection state of any items which is not set according to the bound
     * instance value of this select is updated.
     *
     * @param value the value used to compute selection states.
     */
    protected void dispatchSelectionWithoutValueChange(Object value) throws XFormsException {
        if (isBound()) {
            setSelection(false, true, value);
        }
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

    // helper

    private void setSelection(boolean force, boolean dispatch, Object value) throws XFormsException {

        Item item;
        String itemId;
        boolean selectable = true;
        List elemContext = de.betterform.xml.xpath.impl.saxon.XPathUtil.getElementContext(getElement(), container.getProcessor().getBaseURI());

        String xfNamespacePrefix = this.xformsPrefix;
        if (xfNamespacePrefix == null) {
            xfNamespacePrefix = NamespaceConstants.XFORMS_PREFIX;
        }
        String xPath = "(" + xfNamespacePrefix + ":" + XFormsConstants.ITEM + " | *//" + xfNamespacePrefix + ":" + XFormsConstants.ITEM + "[not(ancestor::" + NamespaceConstants.BETTERFORM_PREFIX + ":data)])/@id";
        List items = XPathCache.getInstance().evaluate(elemContext, 1,xPath , extendedPrefixMapping, xpathFunctionContext);
        Iterator iterator = items.iterator();
        Boolean inRange = false;

        while (iterator.hasNext()) {
            itemId = ((NodeInfo) iterator.next()).getStringValue();
            item = (Item) this.container.lookup(itemId);

            if(item.hasCopyChild()){
                this.selectHasCopyChilds = true;
            }
            if (selectable && isInRange(value, item.getValue())) {
                if (force || !item.isSelected()) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug(this + " selecting item " + itemId);
                    }

                    item.select();

                    if (item.hasCopyChild()) {
                        Map map = new HashMap();
                        map.put("copyItem", "true");
                        map.put("selectedItem", item.getId());
                        this.container.dispatch(this.getTarget(), BetterFormEventNames.STATE_CHANGED, map);
                    }
                }
                inRange = true;
                // allow only one first selection for non-multiple selectors
                selectable = isMultiple();
            } else {
                if (force || item.isSelected()) {
                    if (getLogger().isDebugEnabled()) {
                        getLogger().debug(this + " deselecting item " + itemId);
                    }
                    item.deselect();
                }
            }

        }

        if(!inRange){
            this.container.dispatch(this.target, XFormsEventNames.OUT_OF_RANGE, null);
        }
    }

    private boolean isInRange(Object boundValue, Object itemValue)
    {	
    	//todo: Joern suggests that this function could be simplified using DOMComparator - Adam
    	
        if(boundValue == null || itemValue == null)
        {
            return false;
        }

        if(boundValue == itemValue)
        {
            return true;
        }

        if(boundValue instanceof String && itemValue instanceof String)
        {
        
	        if (isMultiple())
	        {
	            String bound = " " + (String)boundValue + " ";
	            String item = " " + (String)itemValue + " ";
	            return bound.indexOf(item) > -1;
	        }
        	return boundValue.equals(itemValue);
        }
        
        if(boundValue instanceof Element && itemValue instanceof Element)
        {
        	DOMComparator domComparator = new DOMComparator();
        	
        	if(DOMUtil.hasElementChildren((Element)boundValue))
        	{
        		List boundChildElements = DOMUtil.getChildElements((Element)boundValue);
        		
        		for(Iterator itBoundChildElements = boundChildElements.iterator(); itBoundChildElements.hasNext();)
        		{
        			Element childElement = (Element)itBoundChildElements.next();
        			if(domComparator.compare(childElement, (Element)itemValue))
        			{
        				return true;
        			}
        		}
        	}
        	else
        	{
        		if(domComparator.compare((Element)boundValue, (Element)itemValue))
        		{
        			return true;
        		}
        	}
            if(copySkeleton){
                return compareSkeletons((Element)itemValue, (Element)boundValue);
            }            
        }
        return false;
    }

    private boolean compareSkeletons(Element itemValue, Element boundValue) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("Compare Selector Skeleton itemValue: " + itemValue + " boundValue: " + boundValue);
/*
            LOGGER.debug("Compare Selector Skeleton START");
            DOMUtil.prettyPrintDOM(itemValue);
            LOGGER.debug("\nPrototype Skeleton END");

            LOGGER.debug("\nData Prototype: START");
            DOMUtil.prettyPrintDOM(boundValue);
            LOGGER.debug("\nData Prototype: END");
*/

        }
        DOMComparator comparator = new DOMComparator();

        List boundChildElements = DOMUtil.getChildElements(boundValue);
        for(Iterator itBoundChildElements = boundChildElements.iterator(); itBoundChildElements.hasNext();) {
            Element childElement = (Element)itBoundChildElements.next();
            if(comparator.compareSkeleton(itemValue,childElement)){
                return true;
            }
        }
        return false;
    }
}
 // end of class
