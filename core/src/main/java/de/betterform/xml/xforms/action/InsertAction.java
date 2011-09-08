/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import net.sf.saxon.om.Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsComputeException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;

/**
 * Implements the action as defined in <code>9.3.5 The insert Element</code>.
 * 
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: InsertAction.java 3457 2008-08-13 15:03:54Z joern $
 */
public class InsertAction extends AbstractBoundAction {
	private static final Log LOGGER = LogFactory.getLog(InsertAction.class);
	protected IInsertAction insertaction;

	/**
	 * Creates an insert action implementation.
	 * 
	 * @param element
	 *            the element.
	 * @param model
	 *            the context model.
	 */
	public InsertAction(Element element, Model model) {
		super(element, model);
	}

	// lifecycle methods

	/**
	 * Performs element init.
	 */
	public void init() throws XFormsException {
		super.init();

		if (this.container.getVersion().equals(Container.XFORMS_1_0)) {
			insertaction = new InsertAction10();
		}
		else {
			insertaction = new InsertAction11();
		}
		
		insertaction.init();
	}

	// implementation of 'de.betterform.xml.xforms.model.bind.Binding'

	/**
	 * Returns the binding expression.
	 * 
	 * @return the binding expression.
	 */
	public String getBindingExpression() {
		return this.insertaction.getBindingExpression();
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
	 * Performs the <code>insert</code> action.
	 * 
	 * @throws XFormsException
	 *             if an error occurred during <code>insert</code> processing.
	 */
	public void perform() throws XFormsException {
		updateXPathContext();
		
		this.insertaction.perform();
	}
	/**
	 * Returns the logger object.
	 * 
	 * @return the logger object.
	 */
	protected Log getLogger() {
		return LOGGER;
	}
	
	private interface IInsertAction {
		void init() throws XFormsException;
		String getBindingExpression();
		void perform() throws XFormsException;
	}

	private class InsertAction10 implements IInsertAction{
		protected String atAttribute;
		protected String positionAttribute;
		protected String originAttribute;


		// lifecycle methods

		/**
		 * Performs element init.
		 */
		public void init() throws XFormsException {
			this.atAttribute = getXFormsAttribute(AT_ATTRIBUTE);
			if (this.atAttribute == null) {
				throw new XFormsBindingException("missing at attribute at "
						+ DOMUtil.getCanonicalPath(InsertAction.this.getElement()),
						InsertAction.this.target, null);
			}

			this.positionAttribute = getXFormsAttribute(POSITION_ATTRIBUTE);
			if (this.positionAttribute == null) {
				throw new XFormsBindingException(
						"missing position attribute at "
								+ DOMUtil.getCanonicalPath(InsertAction.this.getElement()),
						InsertAction.this.target, null);
			}

			this.originAttribute = getXFormsAttribute(ORIGIN_ATTRIBUTE);
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

		// implementation of 'de.betterform.xml.xforms.action.XFormsAction'

		/**
		 * Performs the <code>insert</code> action.
		 * 
		 * @throws XFormsException
		 *             if an error occurred during <code>insert</code>
		 *             processing.
		 */
		public void perform() throws XFormsException {
			final int contextSize = InsertAction.this.nodeset.size();
			if (contextSize == 0 /* && not has context */) {
				getLogger().warn(
						InsertAction.this + " perform: nodeset '" + getLocationPath()
								+ "' is empty");
				return;
			}

			long insertPosition = computeInsertPosition(contextSize, true);

			// insert specified node and dispatch notification event
			final Node parentNode = de.betterform.xml.xpath.impl.saxon.XPathUtil
					.getAsNode(InsertAction.this.nodeset, getPosition()).getParentNode();
			final Node originNode;
			if (this.originAttribute == null) {
				originNode = de.betterform.xml.xpath.impl.saxon.XPathUtil
						.getAsNode(InsertAction.this.nodeset, Math.max(1, contextSize));
			} else {
				originNode = XPathCache.getInstance().evaluateAsSingleNode(
						nodeset, position, this.originAttribute,
						getPrefixMapping(), xpathFunctionContext);
			}
			Node beforeNode = insertPosition > contextSize ? (null)
					: de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(
							InsertAction.this.nodeset, (int) insertPosition);
			if (beforeNode == null && contextSize > 0) {
				beforeNode = de.betterform.xml.xpath.impl.saxon.XPathUtil
						.getAsNode(InsertAction.this.nodeset, contextSize).getNextSibling();
			}

			if (originNode == null) {
				getLogger().warn(
						InsertAction.this + " perform: origin nodeset of insert is empty");
				return;
			}

			final Instance instance = InsertAction.this.model.getInstance(getInstanceId());
			instance.insertNode(parentNode, originNode, beforeNode);
			InsertAction.this.container.dispatch(instance.getTarget(),
					XFormsEventNames.INSERT, getLocationPath() + "["
							+ insertPosition + "]");
			
			// update behaviour
			doRebuild(true);
			doRecalculate(true);
			doRevalidate(true);
			doRefresh(true);
		}

		protected int computeInsertPosition(int contextSize, boolean processPosition)
				throws XFormsComputeException {
			int insertPosition;
			if (this.atAttribute.equals("last()")) {
				insertPosition = contextSize;
				if (processPosition && this.positionAttribute.equals("after")) {
					insertPosition++;
				}
			} else {
				Double value;
				try {
					value = XPathCache.getInstance().evaluateAsDouble(InsertAction.this.nodeset,
							InsertAction.this.position, "round(number(" + this.atAttribute + "))", getPrefixMapping(),
							xpathFunctionContext);
				} catch (Exception e) {
					throw new XFormsComputeException(
							"invalid 'at' expression at " + InsertAction.this, e,
							InsertAction.this.target, this.atAttribute);
				}

				double d = value.doubleValue();
				if (Double.isNaN(d)) {
					insertPosition = contextSize;
				}
				else {
					insertPosition = (int) d;
					if (insertPosition < 1) {
						insertPosition = 1;
					}
					else if (insertPosition > contextSize) {
						insertPosition = contextSize;
					}
				}
				
				if (processPosition && positionAttribute.equals("after")) {
					insertPosition++;
				}
			}
			return insertPosition;
		}
	}
	
	private class InsertAction11 extends InsertAction10 {

		/**
		 * Performs element init.
		 */
		public void init() throws XFormsException {
			this.atAttribute = getXFormsAttribute(AT_ATTRIBUTE);
			if (this.atAttribute == null) {
				this.atAttribute = "last()";
                LOGGER.info("No 'at' attribute present, defaulting to last()");
			}

			this.positionAttribute = getXFormsAttribute(POSITION_ATTRIBUTE);
			if (this.positionAttribute == null) {
				this.positionAttribute ="after";
                LOGGER.info("No 'position' attribute present, defaulting to after");
			}

			this.originAttribute = getXFormsAttribute(ORIGIN_ATTRIBUTE);
		}
		
		/**
		 * Performs the <code>insert</code> action.
		 * 
		 * @throws XFormsException
		 *             if an error occurred during <code>insert</code>
		 *             processing.
		 */
		public void perform() throws XFormsException {
			if (evalInscopeContext.size() == 0) {
	            getLogger().warn(this + " perform: nodeset inscope evaluation context is empty");
	            return;
	        }

			final boolean hasNodeSetBinding = getBindingExpression() != null;
	        final List nodeSetBindingNodeSet = hasModelBinding()?InsertAction.this.nodeset:InsertAction.this.evalInscopeContext;
	        if(nodeSetBindingNodeSet.isEmpty() && (getContextExpression() == null|| (getContextExpression() != null && !(de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(InsertAction.this.evalInscopeContext, 1) instanceof Element)))) {
	        	getLogger().warn(this + " perform: nodeset '" + getLocationPath() + "' is empty");
	        	return;
	        }
	        
	        final int nrOfNodesInNodeSet = InsertAction.this.nodeset.size();
	        final List originNodeSet;
	        
	        if (this.originAttribute == null){
	        	originNodeSet = InsertAction.this.nodeset.isEmpty()?Collections.EMPTY_LIST:Collections.singletonList(InsertAction.this.nodeset.get(nrOfNodesInNodeSet - 1));
	        }
	        else {
	        	originNodeSet = XPathCache.getInstance().evaluate(InsertAction.this.evalInscopeContext, 1, this.originAttribute, getPrefixMapping(), xpathFunctionContext);
	        }
	        
	        if (originNodeSet.isEmpty()) {
	            getLogger().warn(this + " perform: origin nodeset of insert is empty");
	            return;
	        }
	        
	        int insertPositionFirstNode;
	        final Node insertLocationNode;
	        if (!hasNodeSetBinding || InsertAction.this.nodeset.isEmpty()) {
	        	insertLocationNode = de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(InsertAction.this.evalInscopeContext, 1);
	        	insertPositionFirstNode = 1;
	        }
	        else {
	        	// if no at attribute, init sets it to last()
	        	insertPositionFirstNode = computeInsertPosition(nrOfNodesInNodeSet, false);
	        	insertLocationNode = de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(InsertAction.this.nodeset, insertPositionFirstNode);
	        }
	        
	        final Instance instance = InsertAction.this.model.getInstance(getInstanceId());
	        
	        final List<Node> insertedNodes = new ArrayList<Node>();
	        Node beforeNode = null;
	        for(int i = 0; i < originNodeSet.size(); ++i) {
				Node originNode = de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(originNodeSet, i + 1);

                if(getContextExpression() == null && originNode.getNodeType() == Node.ATTRIBUTE_NODE) {
                    return;
                }

		        final Node parentNode;	
		        if (!hasNodeSetBinding || InsertAction.this.nodeset.isEmpty()) {
		        	parentNode = de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(InsertAction.this.evalInscopeContext, 1);
		        	
		        	if (insertLocationNode.getNodeType() != Node.ELEMENT_NODE) {
		        		continue;
		        	}
		        	if (parentNode.getNodeType() == Node.DOCUMENT_NODE && originNode.getNodeType() == Node.ELEMENT_NODE) {
		        		parentNode.removeChild(((Document)parentNode).getDocumentElement());
		        	}
		        	if (i == 0) {
	        			beforeNode = parentNode.getFirstChild();
	        		}
		        }
		        else {
		        	if (originNode.getNodeType() == Node.ATTRIBUTE_NODE) {
		        		parentNode = insertLocationNode;
		        	}
		        	else {
		        		parentNode = insertLocationNode.getParentNode();
		        		if (parentNode.getNodeType() == Node.DOCUMENT_NODE && originNode.getNodeType() == Node.ELEMENT_NODE) {
			        		parentNode.removeChild(((Document)parentNode).getDocumentElement());
		        		}
		        	}
		        	if (i == 0) {
		        		if ("after".equals(positionAttribute)) {
		        			beforeNode =  insertLocationNode.getNextSibling();
		        			++insertPositionFirstNode;
		        		}
		        		else {
		        			beforeNode =  insertLocationNode;
		        		}
		        		
	        		}
		        }
			
				insertedNodes.add(instance.insertNode((parentNode.getNodeType() != Node.ATTRIBUTE_NODE)?parentNode:((Attr)parentNode).getOwnerElement(), originNode, beforeNode));
			}
			
			InsertAction.this.container.dispatch(instance.getTarget(), XFormsEventNames.INSERT, createEventInfoObject(insertedNodes, originNodeSet, insertLocationNode, this.positionAttribute, getLocationPath() + "["
					+ (insertPositionFirstNode + originNodeSet.size() - 1) + "]"));
			
			// update behaviour
			doRebuild(true);
			doRecalculate(true);
			doRevalidate(true);
			doRefresh(true);
		}

		private Map<String, Object> createEventInfoObject(List<Node> insertedNodes, List<Item> originNodeSet, Node insertLocationNode, String positionAttribute, String locationPath) {
			Map<String, Object> result = new HashMap<String, Object>();
	    	
			List<Item> insertedItems = new ArrayList<Item>(insertedNodes.size());
			for (int i = 0; i < insertedNodes.size(); ++i) {
				final Node n = insertedNodes.get(i);
				insertedItems.add(InsertAction.this.container.getDocumentWrapper(n).wrap(n));
			}
			
	    	result.put("location-path", locationPath);
	    	result.put(INSERTED_NODES, insertedItems);
	    	result.put(ORIGIN_NODES, originNodeSet);
            //todo: fix this!!!
	    	result.put(INSERT_LOCATION_NODE, InsertAction.this.container.getDocumentWrapper(insertLocationNode).wrap(insertLocationNode));
	    	result.put(POSITION, positionAttribute);
	    	
	    	return result;
		}
	}
}

// end of class
