/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.submission;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.ui.BindingElement;
import de.betterform.xml.xforms.ui.UIElementState;
import de.betterform.xml.xforms.ui.state.BoundElementState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Header extends  BindingElement {
    private static final Log LOGGER = LogFactory.getLog(Header.class);
    private String combine = APPEND;
    public static final String APPEND = "append";
    public static final String REPLACE = "replace";
    public static final String PREPEND = "prepend";
    
    private final ValueChild name;
    // Header might have multiple values
    private List<ValueChild> values;



    /**
     * Creates an header implementation.
     *
     * @param element the element.
     * @param model   the context model.
     */
    public Header(Element element, Model model) {
        super(element, model);
        name = new ValueChild((Element) DOMUtil.getFirstChildByTagNameNS(this.element, NamespaceConstants.XFORMS_NS, NAME), this.model);

        List<Element> headerChilds = DOMUtil.getChildElements(this.element);
        values = new ArrayList();

        for(Element childElement : headerChilds){
            if(VALUE.equals(childElement.getLocalName())){
                values.add(new ValueChild(childElement,this.model));
            }

        }
    }

    public void init() throws XFormsException {
        super.init();
        if (getXFormsAttribute(COMBINE) != null) {
            combine = getXFormsAttribute(COMBINE);
        }
        
        
        name.init();
        for(ValueChild value : values){
            value.init();
        }
    }
    
    @Override
    public String getBindingExpression() {
    	return getXFormsAttribute(NODESET_ATTRIBUTE);
    }

    public String getName() throws XFormsException {
        return this.name.getValue();
    }
    public String getValue() throws XFormsException {
        StringBuilder result = new StringBuilder();

        Iterator<ValueChild> valueIterator = values.iterator();
        while(valueIterator.hasNext()) {
            result.append(valueIterator.next().getValue());
            if(valueIterator.hasNext()) {
                result.append("," );
            }
        }
        return result.toString();
    }

    public RequestHeaders getHeaders() throws XFormsException {
    	RequestHeaders requestHeaders = new RequestHeaders(); 
        
    	updateXPathContext();

    	for (int i = 0; i < this.nodeset.size(); i++) {
    		this.name.setPosition(i + 1);

            for(ValueChild value : values){
                value.setPosition(i + 1);
            }
    		// this.value.setPosition(i + 1);
    		
    		final String headerName = this.name.getValue();
            if(headerName == null || "".equals(headerName)) {
                continue;
            }
    		final RequestHeader requestHeader = requestHeaders.getRequestHeader(headerName);

            String value = this.getValue();
    		if (requestHeader == null) {
    			requestHeaders.addHeader(headerName, value);
    		}
    		else if (APPEND.equals(this.combine)) {
    			requestHeader.appendValue(value);
    		}
    		else if (REPLACE.equals(this.combine)) {
    			requestHeader.setValue(value);
    		}
    		else if (PREPEND.equals(this.combine)) {
    			requestHeader.prependValue(value);
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
        if(hasBindingExpression()){
           return this.elementState = new BoundElementState();
        } else {
            return null;
        }        
    }
} // end of class

