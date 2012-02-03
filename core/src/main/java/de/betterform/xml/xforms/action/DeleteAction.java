/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.exception.XFormsComputeException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements the action as defined in <code>9.3.6 The delete Element</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: DeleteAction.java 3474 2008-08-15 22:29:43Z joern $
 */
public class DeleteAction extends AbstractBoundAction {
    private static final Log LOGGER = LogFactory.getLog(DeleteAction.class);
    private String atAttribute;

    /**
     * Creates a delete action implementation.
     *
     * @param element the element.
     * @param model the context model.
     */
    public DeleteAction(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     */
    public void init() throws XFormsException {
        super.init();

        this.atAttribute = getXFormsAttribute(AT_ATTRIBUTE);
    }

    // implementation of 'de.betterform.xml.xforms.model.bind.Binding'

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
    
    /**
     * Returns the context expression.
     *
     * @return the context expression.
     */
    public String getContextExpression() {
    	return getXFormsAttribute(CONTEXT_ATTRIBUTE);
    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>delete</code> action.
     *
     * @throws XFormsException if an error occurred during <code>delete</code>
     * processing.
     */
    public void perform() throws XFormsException {
    	updateXPathContext();

        final int nrOfNodesInNodeset = this.nodeset.size();
    	if (nrOfNodesInNodeset == 0) {
            getLogger().warn(this + " perform: nodeset '" + getLocationPath() + "' is empty");
            return;
        }

    	if (this.atAttribute == null) {
    		final Instance instance = this.model.getInstance(getInstanceId());
            final String locationPath = getLocationPath();
	        final String path = locationPath + "[1]";
		    
    		for (int i = 0; i < this.nodeset.size(); i++) {
                //evaluate each node and return in case no nodes were deleted
                if(!(instance.deleteNode(de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(this.nodeset, i + 1), path))) return;
			}
    		this.container.dispatch(instance.getTarget(), XFormsEventNames.DELETE, constructEventInfo(Double.valueOf(Double.NaN), this.nodeset, locationPath));
    	}
    	else {
	        final Node target;
	        final String positionInNodeset;
	        final List deleteNodes;
	        if (this.atAttribute.equals("last()")) {
	        	deleteNodes = Collections.singletonList(this.nodeset.get(nrOfNodesInNodeset - 1));
	        	target = (Node) de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(this.nodeset, nrOfNodesInNodeset);
	            positionInNodeset = Integer.toString(nrOfNodesInNodeset);
	        }
	        else {
	            Double d;
	            try{
	                d = XPathCache.getInstance().evaluateAsDouble(this.nodeset, this.position, "round(number(" + this.atAttribute + "))", getPrefixMapping(), xpathFunctionContext);
	            }catch(Exception e){
	                throw new XFormsComputeException("invalid 'at' expression at " + this, e, this.target, this.atAttribute);
	            }
	
	            if(LOGGER.isDebugEnabled()){
	                LOGGER.debug("bound to: " + getBindingExpression());
	                LOGGER.debug("value attribute evaluated to: " + d);
	            }
	            
	            
	            long position = Math.round(d);
	
	            if (Double.isNaN(d) || position > nrOfNodesInNodeset) {
	            	position = nrOfNodesInNodeset;
	            }
	            else if(position < 1) {
	            	position = 1;
	            }
	            
	            deleteNodes = Collections.singletonList(this.nodeset.get((int)position - 1));
	            target = (Node) de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(this.nodeset, (int)position);
	            positionInNodeset = Long.toString(position);
	        }
	
	        // delete specified node and dispatch notification event
	        final Instance instance = this.model.getInstance(getInstanceId());
	        final String path = getLocationPath() + "[" + positionInNodeset + "]";

            if(!(instance.deleteNode(target, path))) return;
		    this.container.dispatch(instance.getTarget(), XFormsEventNames.DELETE, constructEventInfo(Double.valueOf(positionInNodeset), deleteNodes, path));
    	}

        // update behaviour
        doRebuild(true);
        doRecalculate(true);
        doRevalidate(true);
        doRefresh(true);
    }

	/**
	 * @param positionInNodeset
	 * @param deleteNodes
	 * @param path
	 * @return
	 */
	private Map<String, Object> constructEventInfo(final Double positionInNodeset, final List deleteNodes, final String path) {
		Map<String, Object> eventInfo = new HashMap<String, Object>();
		eventInfo.put("path", path);
		eventInfo.put(XFormsConstants.DELETE_LOCATION, positionInNodeset);
		eventInfo.put(XFormsConstants.DELETE_NODES, deleteNodes);
		return eventInfo;
	}

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
