/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.xforms.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.state.ItemsetElementState;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of <b>9.3.3 The itemset Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @author Lars Windauer
 * @version $Id: Itemset.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class Itemset extends BindingElement {
    private static final Log LOGGER = LogFactory.getLog(Itemset.class);

    private Element prototype;
    private List items;

    /**
     * Creates a new itemset element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Itemset(Element element, Model model) {
        super(element, model);
    }

    // itemset specific methods

    /**
     * Returns the context size of this itemset.
     * <p/>
     * The context size is the size of the bound nodeset.
     *
     * @return the context size of this itemset.
     */
    public int getContextSize() throws XFormsException {
        if (isBound()) {
            return nodeset.size();
        }

        return 0;
    }

    /**
     * Returns the itemset prototype element.
     *
     * @return the itemset prototype element.
     */
    public Element getPrototype() {
        return this.prototype;
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
      

        initializeDefaultAction();
        initializeInstanceNode();
        updateXPathContext();
        initializePrototype();
        initializeElementState();
        initializeItemset();
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
        Node emptyNode = DOMUtil.getFirstChildElement(this.element);
        this.element.removeChild(emptyNode);

        updateXPathContext();
        updateItemset();
        updateElementState();
        updateChildren();

        Node firstNode  = DOMUtil.getFirstChildElement(this.element);
        this.element.insertBefore(emptyNode,firstNode);
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

        disposeDefaultAction();
        disposeChildren();
        disposeElementState();
        disposeItemset();
        disposeSelf();
    }

    // binding related methods

    /**
     * Checks wether this element has an ui binding.
     * <p/>
     * This element has an ui binding if it has a <code>nodeset</code>
     * attribute.
     *
     * @return <code>true</code> if this element has an ui binding, otherwise
     *         <code>false</code>.
     */
    public boolean hasUIBinding() {
        return getXFormsAttribute(NODESET_ATTRIBUTE) != null;
    }

    /**
     * Returns the binding expression.
     *
     * @return the binding expression.
     */
    public String getBindingExpression() {
	if (hasModelBinding()) {
            return getModelBinding().getBindingExpression();
        }
        return getXFormsAttribute(NODESET_ATTRIBUTE);
    }

    // lifecycle template methods

    /**
     * Initializes the prototype of this itemset.
     * <p/>
     * The itemset prototype is cloned and removed from the document.
     */
    protected void initializePrototype() {
        // create prototype element
        Document document = this.element.getOwnerDocument();
        this.prototype = document.createElementNS(NamespaceConstants.XFORMS_NS, (this.xformsPrefix!=null?this.xformsPrefix:NamespaceConstants.XFORMS_PREFIX) + ":" + XFormsConstants.ITEM);
        this.prototype.setAttributeNS(null, "id", this.container.generateId());

        // clone itemset prototype
        NodeList children = this.element.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            this.prototype.appendChild(children.item(index).cloneNode(true));
        }

        // remove itemset prototype
        DOMUtil.removeAllChildren(this.element);
    }

    /**
     * Initializes this itemset.
     * <p/>
     * For each node in the bound nodeset selector items are created and
     * initialized.
     */
    protected void initializeItemset() throws XFormsException {
        // initialize positional items
        int count = getContextSize();
        this.items = new ArrayList(count);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " init: initializing " + count + " selector item(s)");
        }
        for (int position = 1; position < count + 1; position++) {
            this.items.add(initializeSelectorItem(position));
        }
    }

    /**
     * Updates this itemset.
     * <p/>
     * The list of selector items is synchronized with the bound nodeset.
     */
    protected void updateItemset() throws XFormsException {
        // check nodeset count
        int count = this.nodeset.size();
        int size = this.items.size();

        if (count < size) {
            // remove obsolete selector items
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this + " update: disposing " + (size - count) + " selector item(s)");
            }
            for (int position = size; position > count; position--) {
                disposeSelectorItem((Item) this.items.remove(position - 1));
            }
        }

        if (count > size) {
            // add missing selector items
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this + " update: initializing " + (count - size) + " selector item(s)");
            }
            for (int position = size + 1; position <= count; position++) {
                this.items.add(initializeSelectorItem(position));
            }
        }
    }

    /**
     * Disposes this itemset.
     * <p/>
     * The list of selector items and the itemset prototype are freed.
     */
    protected void disposeItemset() {
        // free selector items and prototype
        this.items.clear();
        this.items = null;
        this.prototype = null;
    }

    // template methods

    /**
     * Factory method for the element state.
     *
     * @return an element state implementation or <code>null</code> if no state
     *         keeping is required.
     * @throws XFormsException if an error occurred during creation.
     */
    protected UIElementState createElementState() throws XFormsException {
        return isBound() ? new ItemsetElementState() : null;
    }

    /**
     * Returns the logger object.
     *
     * @return the logger object.
     */
    protected Log getLogger() {
        return LOGGER;
    }

    // helper

    private Item initializeSelectorItem(int position) throws XFormsException {
        // detect reference node
        Node before = DOMUtil.findNthChildNS(this.element, NamespaceConstants.XFORMS_NS, XFormsConstants.ITEM, position);
        if (before == null) {
            before = DOMUtil.findFirstChildNS(this.element, NamespaceConstants.BETTERFORM_NS, "data");
        }

        // create selector item
        Element element = (Element) this.prototype.cloneNode(true);
        this.element.insertBefore(element, before);

        if (this.model.isReady()) {
            // dispatch internal betterform event
            HashMap map = new HashMap();
            map.put("originalId", this.originalId != null ? this.originalId : this.id);
            map.put("prototypeId", this.prototype.getAttributeNS(null, "id"));
            this.container.dispatch(this.target, BetterFormEventNames.PROTOTYPE_CLONED, map);
        }

        // initialize selector item
        Item item = (Item) this.container.getElementFactory().createXFormsElement(element, this.model);
        item.setItemset(this);
        item.setPosition(position);
        item.setGeneratedId(this.container.generateId());
        item.registerId();
        item.init();

        if (this.model.isReady()) {
            // dispatch internal betterform event
            HashMap map = new HashMap();
            map.put("originalId", this.originalId != null ? this.originalId : this.id);
            map.put("position", String.valueOf(position));

            Element itemValueCopy = (Element) DOMUtil.getFirstChildByTagNameNS(element, NamespaceConstants.XFORMS_NS, "copy");
            if(itemValueCopy != null) {
                String copyValue = getXFormsAttribute(itemValueCopy, "id");
                map.put("value", copyValue);
            }

            Element itemLabel = (Element) DOMUtil.getFirstChildByTagNameNS(element, NamespaceConstants.XFORMS_NS, "label");
            if(itemLabel != null) {
                map.put("label", itemLabel.getTextContent());
            }
            this.container.dispatch(this.target, BetterFormEventNames.ITEM_INSERTED, map);
        }
        return item;
    }

    private void disposeSelectorItem(Item item) throws XFormsException {
        // dispose selector item
        Element element = item.getElement();
        int position = item.getPosition();
        item.dispose();
        this.element.removeChild(element);

        if (this.model.isReady()) {
            // dispatch internal betterform event
            HashMap map = new HashMap();
            map.put("originalId", this.originalId != null ? this.originalId : this.id);
            map.put("position", String.valueOf(position));
            this.container.dispatch(this.target, BetterFormEventNames.ITEM_DELETED, map);
        }
    }

}

// end of class
