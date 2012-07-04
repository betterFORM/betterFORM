/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.bind;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.DefaultAction;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.xforms.Initializer;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsComputeException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xpath.XPathReferenceFinder;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Implementation of XForms Model Bind Element.
 *
 * @version $Id: Bind.java 3479 2008-08-19 10:44:53Z joern $
 */
public class Bind extends XFormsElement implements Binding, DefaultAction {

    private static Log LOGGER = LogFactory.getLog(Bind.class);

    private String locationPath;
    private String instanceId;

    private String type;
    private String readonly;
    private String required;
    private String relevant;
    private String calculate;
    private String constraint;
    private String p3ptype;
    private Map<String,String> customMIPs;

    private XPathReferenceFinder referenceFinder;
    private Set readonlyReferences;
    private Set requiredReferences;
    private Set relevantReferences;
    private Set calculateReferences;
    private Set constraintReferences;
    private HashMap<String, Set> customMIPReferences;

    protected List nodeset;

    /**
     * Creates a new Bind object.
     *
     * @param element the DOM Element annotated by this object
     * @param model   the parent Model object
     */
    public Bind(Element element, Model model) {
        super(element, model);

        // register with model
        getModel().addBindElement(this);
    }
    
    /**
     * Updates the childEvaluationContext
     *
     * @throws XFormsException in case an XPathException happens during evaluation
     */
    public void updateXPathContext() throws XFormsException {

        List inscopeContext = evalInScopeContext();

        // if(LOGGER.isDebugEnabled()){
        // NodeWrapper info = (NodeWrapper) parentNodeset.get(0);
        // LOGGER.debug(this + " bound to Node:" + info.getUnderlyingNode());
        // LOGGER.debug("in scope xpath context for " + this + " = " +
        // BindingResolver.getExpressionPath(this,repeatItemId));
        // }

        final String relativeExpr = getBindingExpression();
        try {
            if (relativeExpr != null) {
                if (this.nodeset == null) {
                    this.nodeset = new ArrayList();
                } else {
                    // When rebuild is called we should clear the list before adding all the nodes again.
                    this.nodeset.clear();
                }
                for (int i = 0; i < inscopeContext.size(); ++i) {
                    this.nodeset.addAll(XPathCache.getInstance().evaluate(inscopeContext, i + 1, relativeExpr, getPrefixMapping(), xpathFunctionContext));
                }
            } else {
                this.nodeset = inscopeContext;
            }
        } catch (XFormsException e) {
            throw new XFormsComputeException(e.getMessage(), this.target, null);
        }
    }

    // implementation of 'de.betterform.xml.xforms.model.bind.Binding'
    public List getNodeset() {

        return this.nodeset;
    }
    
    
    public int getPosition() {
    	return 1;
    }

    /**
     * Returns the binding expression.
     *
     * @return the binding expression.
     */
    public String getBindingExpression() {
        return getXFormsAttribute(NODESET_ATTRIBUTE);
    }

    /**
     * Returns the id of the binding element.
     *
     * @return the id of the binding element.
     */
    public String getBindingId() {
        return this.id;
    }

    /**
     * Returns the enclosing element.
     *
     * @return the enclosing element.
     */
    public Binding getEnclosingBinding() {
        Element parentElement = (Element) this.element.getParentNode();

        if (parentElement.getLocalName().equals(XFormsConstants.MODEL)) {
            return null;
        }

        return (Binding) parentElement.getUserData("");
    }

    /**
     * Returns the location path.
     *
     * @return the location path.
     */
    public String getLocationPath() {
        return this.locationPath;
    }

    /**
     * Returns the model id of the binding element.
     *
     * @return the model id of the binding element.
     */
    public String getModelId() {
        return this.model.getId();
    }

    public boolean hasBindingExpression() {
        if (getBindingExpression() != null)
            return true;
        else
            return false;
    }

    // convenience

    /**
     * Returns the instance id of the binding element.
     *
     * @return the instance id of the binding element.
     */
    public String getInstanceId() {
        return this.instanceId;
    }

    // bind members

    /**
     * Returns the <code>type</code> attribute.
     *
     * @return the <code>type</code> attribute.
     */
    public String getDatatype() {
        return this.type;
    }

    /**
     * Returns the <code>readonly</code> attribute.
     *
     * @return the <code>readonly</code> attribute.
     */
    public String getReadonly() {
        return this.readonly;
    }

    /**
     * Returns the <code>required</code> attribute.
     *
     * @return the <code>required</code> attribute.
     */
    public String getRequired() {
        return this.required;
    }

    /**
     * Returns the <code>relevant</code> attribute.
     *
     * @return the <code>relevant</code> attribute.
     */
    public String getRelevant() {
        return this.relevant;
    }

    /**
     * Returns the <code>calculate</code> attribute.
     *
     * @return the <code>calculate</code> attribute.
     */
    public String getCalculate() {
        return this.calculate;
    }

    /**
     * Returns the <code>constraint</code> attribute.
     *
     * @return the <code>constraint</code> attribute.
     */
    public String getConstraint() {
        return this.constraint;
    }
    
    public Map<String,String> getCustomMIPs() {
    	return this.customMIPs;
    }

    /**
     * Returns the <code>p3ptype</code> attribute.
     *
     * @return the <code>p3ptype</code> attribute.
     */
    public String getP3PType() {
        return this.p3ptype;
    }

    /**
     * Assigns an XPath reference finder.
     *
     * @param referenceFinder the XPath reference finder.
     */
    public void setReferenceFinder(XPathReferenceFinder referenceFinder) {
        this.referenceFinder = referenceFinder;
    }

    /**
     * Returns the set of all node names referenced by the <code>readonly</code> expression.
     *
     * @return the set of all node names referenced by the <code>readonly</code> expression.
     */
    public Set getReadonlyReferences() {
        return this.readonlyReferences;
    }

    /**
     * Returns the set of all node names referenced by the <code>required</code> expression.
     *
     * @return the set of all node names referenced by the <code>required</code> expression.
     */
    public Set getRequiredReferences() {
        return this.requiredReferences;
    }

    /**
     * Returns the set of all node names referenced by the <code>relevant</code> expression.
     *
     * @return the set of all node names referenced by the <code>relevant</code> expression.
     */
    public Set getRelevantReferences() {
        return this.relevantReferences;
    }

    /**
     * Returns the set of all node names referenced by the <code>calculate</code> expression.
     *
     * @return the set of all node names referenced by the <code>calculate</code> expression.
     */
    public Set getCalculateReferences() {
        return this.calculateReferences;
    }

    /**
     * Returns the set of all node names referenced by the <code>constraint</code> expression.
     *
     * @return the set of all node names referenced by the <code>constraint</code> expression.
     */
    public Set getConstraintReferences() {
        return this.constraintReferences;
    }
    
    public Set getCustomMIPReferences(String customMIP) {
        return this.customMIPReferences.get(customMIP);
    }
    
    public Map<String, Set> getCustomMIPsReferences() {
        return this.customMIPReferences;
    }

    // implementation of 'de.betterform.xml.events.DefaultAction'

    /**
     * Performs the implementation specific default action for this event.
     *
     * @param event the event.
     */
    public void performDefault(Event event) {
        if (event.getType().equals(XFormsEventNames.BINDING_EXCEPTION)) {
            getLogger().error(this + " binding exception: " + ((XMLEvent) event).getContextInfo());
            return;
        }
    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        initializeDefaultAction();
        initializeBindingContext();
        initializeModelItems();
        Initializer.initializeBindElements(getModel(), getElement(), this.referenceFinder);
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
    }

    // lifecycle template methods

    /**
     * Initializes the default action.
     */
    protected void initializeDefaultAction() {
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.BINDING_EXCEPTION, this);
    }

    /**
     * Initializes the binding context.
     *
     * @throws XFormsException if any error occured during binding context init.
     */
    protected void initializeBindingContext() throws XFormsException {
        // resolve location path and instance id
        this.locationPath = this.container.getBindingResolver().resolve(this);
        this.instanceId = this.model.computeInstanceId(this.locationPath);
        if (this.instanceId == null) {
            throw new XFormsBindingException("wrong instance id", this.target, this.locationPath);
        }

        // get type attributes
        this.type = getXFormsAttribute(TYPE_ATTRIBUTE);
        this.p3ptype = getXFormsAttribute(P3PTYPE_ATTRIBUTE);

        try {
	        // get model item attributes and analyze path structure
	        this.readonly = getXFormsAttribute(READONLY_ATTRIBUTE);
	        if (this.readonly != null) {
	            this.readonlyReferences = this.referenceFinder.getReferences(this.readonly, getPrefixMapping(), this.container);
	        }
	
	        this.required = getXFormsAttribute(REQUIRED_ATTRIBUTE);
	        if (this.required != null) {
	            this.requiredReferences = this.referenceFinder.getReferences(this.required, getPrefixMapping(), this.container);
	        }
	
	        this.relevant = getXFormsAttribute(RELEVANT_ATTRIBUTE);
	        if (this.relevant != null) {
	            this.relevantReferences = this.referenceFinder.getReferences(this.relevant, getPrefixMapping(), this.container);
	        }
	
	        this.calculate = getXFormsAttribute(CALCULATE_ATTRIBUTE);
	        if (this.calculate != null) {
	            this.calculateReferences = this.referenceFinder.getReferences(this.calculate, getPrefixMapping(), this.container);
	        }
	
	        this.constraint = getXFormsAttribute(CONSTRAINT_ATTRIBUTE);
	        if (this.constraint != null) {
	            this.constraintReferences = this.referenceFinder.getReferences(this.constraint, getPrefixMapping(), this.container);
	        }

	        this.customMIPs = getBFAttributes();
	        if (!this.customMIPs.isEmpty()) {
	        	this.customMIPReferences = new HashMap<String, Set>();
	           	for (String key : this.customMIPs.keySet()) {
	            	this.customMIPReferences.put(key, this.referenceFinder.getReferences(this.customMIPs.get(key), getPrefixMapping(), this.container));
				}
	        }
	        	
	        updateXPathContext();
        }
        catch(XFormsComputeException e) {
        	throw e;
        }
        catch(XFormsException e) {
        	throw new XFormsComputeException(e.getMessage(), this.target, null);
        }
    }

    /**
     * Initializes all bound model items.
     *
     * @throws XFormsException if any error occured during model item init.
     */
    protected void initializeModelItems() throws XFormsException {
        Instance instance = getModel().getInstance(getInstanceId());
        List nodeset = getNodeset();
        if(nodeset != null && nodeset.size() > 0) {            
            Iterator iterator = instance.iterateModelItems(nodeset, false);
            if (iterator != null) {
                ModelItem modelItem;
                while (iterator.hasNext()) {
                    modelItem = (ModelItem) iterator.next();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(this + " init: model item for " + DOMUtil.getCanonicalPath((Node) modelItem.getNode()));
                    }

                    // 4.2.1 - 4.b applying model item properties to each node
                    initializeModelItemProperties(modelItem);
                }
            }
        }
    }

    /**
     * Initializes the model item properties of the specified model item.
     *
     * @param item the model item.
     * @throws XFormsException if any error occured during model item properties init.
     */
    protected void initializeModelItemProperties(ModelItem item) throws XFormsException {
        DeclarationView declaration = item.getDeclarationView();

        if (this.type != null) {
            if (declaration.getDatatype() != null) {
                throw new XFormsBindingException("property 'type' already present at model item", this.target, this.id);
            }

            if (!this.model.getValidator().isSupported(this.type)) {
                throw new XFormsBindingException("datatype '" + this.type + "' is not supported", this.target, this.id);
            }
            if (!this.model.getValidator().isKnown(this.type)) {
                throw new XFormsBindingException("datatype '" + this.type + "' is unknown", this.target, this.id);
            }

            declaration.setDatatype(this.type);
        }

        if (this.readonly != null) {
            if (declaration.getReadonly() != null) {
                throw new XFormsBindingException("property 'readonly' already present at model item", this.target, this.id);
            }

            declaration.setReadonly(this.readonly);
        }

        if (this.required != null) {
            if (declaration.getRequired() != null) {
                throw new XFormsBindingException("property 'required' already present at model item", this.target, this.id);
            }

            declaration.setRequired(this.required);
        }

        if (this.relevant != null) {
            if (declaration.getRelevant() != null) {
                throw new XFormsBindingException("property 'relevant' already present at model item", this.target, this.id);
            }

            declaration.setRelevant(this.relevant);
        }

        if (this.calculate != null) {
            if (declaration.getCalculate() != null) {
                throw new XFormsBindingException("property 'calculate' already present at model item", this.target, this.id);
            }

            declaration.setCalculate(this.calculate);
        }

        if (this.constraint != null) {
            if (declaration.getConstraint() != null) {
                throw new XFormsBindingException("property 'constraint' already present at model item", this.target, this.id);
            }

            declaration.setConstraint(this.constraint);
        }

        if (this.p3ptype != null) {
            if (declaration.getP3PType() != null) {
                throw new XFormsBindingException("property 'p3ptype' already present at model item", this.target, this.id);
            }

            declaration.setP3PType(this.p3ptype);
        }
    }

    /**
     * Disposes the default action.
     */
    protected void disposeDefaultAction() {
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.BINDING_EXCEPTION, this);
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
