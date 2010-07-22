/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.submission;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.ui.BindingElement;
import de.betterform.xml.xforms.ui.UIElementState;
import de.betterform.xml.xforms.ui.state.BoundElementState;
import org.w3c.dom.Element;

public class Header extends  BindingElement {
    private static final Log LOGGER = LogFactory.getLog(Header.class);
    private String combine = APPEND;
    public static final String APPEND = "append";
    public static final String REPLACE = "replace";
    public static final String PREPEND = "prepend";
    
    private final ValueChild name;
    private final ValueChild value;


    /**
     * Creates an header implementation.
     *
     * @param element the element.
     * @param model   the context model.
     */
    public Header(Element element, Model model) {
        super(element, model);
        
        name = new ValueChild((Element) DOMUtil.getFirstChildByTagNameNS(this.element, NamespaceConstants.XFORMS_NS, NAME), this.model);
        value = new ValueChild((Element) DOMUtil.getFirstChildByTagNameNS(this.element, NamespaceConstants.XFORMS_NS, VALUE), this.model);
    }

    public void init() throws XFormsException {
        super.init();
        if (getXFormsAttribute(COMBINE) != null) {
            combine = getXFormsAttribute(COMBINE);
        }
        
        
        name.init();
        value.init();
    }
    
    @Override
    public String getBindingExpression() {
    	return getXFormsAttribute(NODESET_ATTRIBUTE);
    }

    public String getName() throws XFormsException {
        return this.name.getValue();
    }
    public String getValue() throws XFormsException {
        return this.value.getValue();    
    }

    public RequestHeaders getHeaders() throws XFormsException {
    	RequestHeaders requestHeaders = new RequestHeaders(); 
        
    	updateXPathContext();
    	
    	for (int i = 0; i < this.nodeset.size(); i++) {
    		this.name.setPosition(i + 1);
    		this.value.setPosition(i + 1);
    		
    		final String headerName = this.name.getValue();
    		final RequestHeader requestHeader = requestHeaders.getRequestHeader(headerName);
    		if (requestHeader == null) {
    			requestHeaders.addHeader(headerName, this.value.getValue());
    		}
    		else if (APPEND.equals(this.combine)) {
    			requestHeader.appendValue(this.value.getValue());
    		}
    		else if (REPLACE.equals(this.combine)) {
    			requestHeader.setValue(this.value.getValue());
    		}
    		else if (PREPEND.equals(this.combine)) {
    			requestHeader.prependValue(this.value.getValue());
    		}
		}
        return requestHeaders;
    }

    public void dispose() throws XFormsException {
    	super.dispose();
    }

    public String getCombine() {
        return this.combine;
    }

    protected Log getLogger() {
        return LOGGER;
    }

    protected UIElementState createElementState() throws XFormsException {
        if(isBound()){
           return this.elementState = new BoundElementState();
        } else {
            return null;
        }        
    }
} // end of class

