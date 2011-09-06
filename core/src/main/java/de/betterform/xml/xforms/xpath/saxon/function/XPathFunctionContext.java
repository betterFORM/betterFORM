/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import de.betterform.xml.xforms.XFormsElement;

/**
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class XPathFunctionContext {
	
	private final XFormsElement element;
	
	
	
	/**
	 * Constructs an XPathFunctionContext.
	 * 
	 * @param element the XFormsElement that is the evaluation context of this function
	 */
	public XPathFunctionContext(XFormsElement element) {
		this.element = element;
	}


	
	/**
	 * Returns the <code>XFormsElement</code> associated with this XPath function context. 
	 * 
	 * @return the <code>XFormsElement</code> associated with this XPath function context. Can be <code>null</code>. 
	 */
	public XFormsElement getXFormsElement()
	{
		return element;
	}
}
