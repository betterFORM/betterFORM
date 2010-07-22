/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.Initializer;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.bind.Binding;
import de.betterform.xml.xforms.model.bind.BindingResolver;
import de.betterform.xml.xpath.XPathUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.List;

/**
 * Helper class to wrap a single repeat item. In the internal DOM of the
 * processor xforms:group Elements are created for every repeat item. This
 * element has an additional bf:transient attribute which signals a
 * stylesheet writer, that this group was not part of the original form and can
 * be ignored for rendering. Adding these transient groups helps to wrap mixed
 * markup in the input document and also simplifies writing UI transformations
 * for repeated data.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RepeatItem.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class RepeatItem extends Group {
    private static final Log LOGGER = LogFactory.getLog(RepeatItem.class);

    private int contextPosition;
    private Repeat repeat;

    /**
     * Creates a new repeat item.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public RepeatItem(Element element, Model model) {
        super(element, model);
    }

    // implementation of 'de.betterform.xml.xforms.model.bind.Binding'

    /**
     * Returns the binding expression.
     *
     * @return the binding expression.
     */
    public String getBindingExpression() {
        // filter the enclosing repeat
        return this.repeat.getBindingExpression() + "[" + getPosition() + "]";
    }

    /**
     * Returns the id of the binding element.
     *
     * @return the id of the binding element.
     */
    public String getBindingId() {
        // filter the enclosing repeat
        return this.repeat.getBindingId();
    }

    /**
     * Returns the enclosing element.
     *
     * @return the enclosing element.
     */
    public Binding getEnclosingBinding() {
        // filter the enclosing repeat
        return this.repeat.getEnclosingBinding();
    }

    /**
     * Returns the location path.
     *
     * @return the location path.
     */
    public String getLocationPath() {
        // filter the enclosing repeat
        return this.repeat.getLocationPath() + "[" + getPosition() + "]";
    }

    /**
     * Returns the model id of the binding element.
     *
     * @return the model id of the binding element.
     */
    public String getModelId() {
        // filter the enclosing repeat
        return this.repeat.getModelId();
    }

    /**
     * Checks wether this element is bound to a model item.
     *
     * @return <code>true</code>.
     */
    public boolean isBound() {
        return true;
    }

    // repeat item specific methods


    /**
     * Sets the repeat item position.
     *
     * @param position the repeat item position.
     */
    public void setPosition(int position) throws XFormsException {
        this.position = position;

        // recompute context position which may differ from ui position in dynamic repeats
        String canonicalPath = BindingResolver.getCanonicalPath(this.element);
        this.contextPosition = Integer.parseInt(XPathUtil.getNodesetAndPredicates(canonicalPath)[1]);
    }

    /**
     * Returns the owning repeat.
     *
     * @return the owning repeat.
     */
    public Repeat getRepeat() {
        return this.repeat;
    }

    /**
     * Sets the owning repeat.
     *
     * @param repeat the owning repeat.
     */
    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
    }

    /**
     * Returns the context (repeat nodeset) position.
     *
     * @return the context (repeat nodeset) position.
     */
    public int getContextPosition() {
        return this.contextPosition;
    }

    /**
     * Checks wether this repeat item is selected.
     *
     * @return <code>true</code> if this repeat item is selected,
     *         <code>false</code> otherwise.
     */
    public boolean isSelected() {
        boolean selected = this.repeat.getIndex() == this.position;

        if (this.repeat.isRepeated()) {
            // check enclosing repeat item
            RepeatItem repeatItem = (RepeatItem) this.container.lookup(this.repeat.getRepeatItemId());
            selected = selected && repeatItem.isSelected();
        }

        return selected;
    }
    
    protected void updateXPathContext() throws XFormsException { }

    protected String getRelativeExpression() {
		return "node()[" + getPosition() + "]";
	}
	
	public List getNodeset()	{
      List repeatNodeset = repeat.getNodeset();
		int localPosition = getPosition();
		return repeatNodeset.size() >=localPosition?Collections.singletonList(repeatNodeset.get(localPosition - 1)):Collections.EMPTY_LIST;
	}
	
	@Override
	public Node getInstanceNode() throws XFormsException {
		return de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(getNodeset(), 1);
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
        updateXPathContext();
        initializeElementState();
        Initializer.initializeUIElements(this.model, this.element, this.id, this.element.getOwnerDocument().getDocumentElement().getAttributeNS(NamespaceConstants.BETTERFORM_NS,"evalAVTs"));
        Initializer.initializeActionElements(this.model, this.element, this.id);
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
        disposeElementState();
        disposeChildren();
        disposeSelf();

        this.repeat = null;
        this.position = 0;
    }

    // standard methods

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object.
     */
    public String toString() {
        return "[" + this.element.getNodeName() + "/repeatitem id='" + getId() + "']";
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
