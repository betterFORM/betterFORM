/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.submission;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import org.w3c.dom.Element;

/**
 * @author Nick Van den Bleeken
 */
public class AttributeOrValueChild {
	private final Element parentEl;
	private final String name;
	
	private final ValueChild valueChild;
	private final String attrValue;
	
	/**
	 * @param parentEl
	 * @param model
	 * @param name
	 */
	public AttributeOrValueChild(Element parentEl, Model model, String name) {
		this.parentEl = parentEl;
		this.name = name;
		
		Element child = (Element) DOMUtil.getFirstChildByTagNameNS(this.parentEl, NamespaceConstants.XFORMS_NS, this.name);
		if (child != null) {
			valueChild = new ValueChild(child, model);
			attrValue = null;
		}
		else {
			valueChild = null;
			attrValue = XFormsElement.getXFormsAttribute(this.parentEl, this.name);
		}
	}
	
	public void init() throws XFormsException {
		if (this.valueChild != null) {
			this.valueChild.init();
		}
	}
	
	/**
	 * @return
	 */
	public boolean isAvailable() {
		return this.valueChild != null || this.attrValue != null;
	}
	
	/**
	 * @return
	 * @throws XFormsException
	 */
	public String getValue() throws XFormsException {
		return this.valueChild != null?this.valueChild.getValue():attrValue;
	}
	
}
