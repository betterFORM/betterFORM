/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DefaultAction;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsComputeException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.model.bind.Bind;
import de.betterform.xml.xforms.model.bind.Binding;
import de.betterform.xml.xforms.model.bind.BindingResolver;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

import java.util.*;

/**
 * Bound elements are all elements that may have XForms binding expressions.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: BindingElement.java 3496 2008-08-28 09:58:15Z joern $
 */
public abstract class BindingElement extends AbstractUIElement implements Binding, DefaultAction {
    private static final Log LOGGER = LogFactory.getLog(BindingElement.class);

    /**
     * The id of the instance this element is bound to.
     */
    protected String instanceId = null;

    /**
     * The resolved path expression pointing to the bound node.
     */
    protected String locationPath = null;

    /**
     * The node set of this element
     */
    protected List nodeset;
    protected int position = 1;

    /**
     * The element state.
     */
    protected UIElementState elementState = null;


    protected Map attributeValueMap;
    private String allowedAttributes;

    /**
     * Creates a new BindingElement object.
     *
     * @param element the DOM Element.
     * @param model the Model to which this element is bound.
     */
    public BindingElement(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace(this + " init");
        }

        initializeDefaultAction();
        initializeInstanceNode();
        updateXPathContext();
        initializeElementState();
        initializeChildren();

        this.allowedAttributes = this.getElement().getOwnerDocument().getDocumentElement().getAttributeNS(NamespaceConstants.BETTERFORM_NS,"evalAVTs");
        String[] attrs = allowedAttributes.split(" ");
        for(String attribute : attrs){

            if(this.getElement().hasAttribute(attribute) && this.getElement().getAttribute(attribute).indexOf("{")!=-1){
                this.attributeValueMap = new HashMap(attrs.length);
                String expression = this.getElement().getAttribute(attribute);
                this.attributeValueMap.put(attribute,expression );

                if(LOGGER.isTraceEnabled()){
                    LOGGER.debug("evaluating AVT at: " + DOMUtil.getCanonicalPath(this.getElement()) + "/@" + attribute);
                    DOMUtil.prettyPrintDOM(this.element);
                }

                Object result=this.evalAttributeValueTemplates(expression,this.getElement());
                this.getElement().setAttribute(attribute,result.toString());
            }
        }
    }


    public String evalAttributeValueTemplates(String inputExpr,Element element) throws XFormsException {
        String toReplace = inputExpr;
        int start;
        int end;
        String valueTemplate;
        String value="";
        StringBuffer substitutedString = new StringBuffer();
        boolean hasTokens = true;
        while (hasTokens) {
            start = toReplace.indexOf("{");
            end = toReplace.indexOf('}');
            if (start == -1 || end == -1) {
                hasTokens = false; //exit
                substitutedString.append(toReplace);
            }
            else {
                substitutedString.append(toReplace.substring(0, start));
                valueTemplate = toReplace.substring(start + 1, end);

                //distinguish normal valueTemplate versus context $var
                if(valueTemplate.startsWith("$")){
                    //read key from context
                    valueTemplate = valueTemplate.substring(1);
                    if (this.model.getContainer().getProcessor().getContext().containsKey(valueTemplate)) {
                        value = this.model.getContainer().getProcessor().getContext().get(valueTemplate).toString();
                    }
                }else{

                     value = XPathCache.getInstance().evaluateAsString(this.getNodeset(), getPosition(), valueTemplate, getPrefixMapping(), xpathFunctionContext);
                }
                if (value == null) {
                    throw new XFormsComputeException("valueTemplate could not be evaluated.", (EventTarget) element, "");
                }
                if(value.equals("")){
                     LOGGER.warn("valueTemplate could not be evaluated. Replacing '" + valueTemplate + "' with empty string");
                }
                substitutedString.append(value);
                toReplace = toReplace.substring(end + 1);
            }
        }
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("inputExpr: '" + inputExpr + "' evaluated to: '" + substitutedString + "'");
        }
        return substitutedString.toString();
    }


    /**
     * Performs element update.
     *
     * @throws XFormsException if any error occurred during update.
     */
    public void refresh() throws XFormsException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("refresh" + this);
        }

        updateXPathContext();
        updateElementState();

        if(this.attributeValueMap != null) {
            Set keyset = this.attributeValueMap.keySet();
            for (Iterator iterator = keyset.iterator(); iterator.hasNext();) {
                String attribute = (String) iterator.next();
                Object result=this.evalAttributeValueTemplates((String) this.attributeValueMap.get(attribute),this.getElement());

                this.getElement().setAttribute(attribute,result.toString());

                final Map contextInfo = new HashMap(2);
                contextInfo.put("attribute",attribute);
                contextInfo.put("value",result);
                container.dispatch(this.getTarget(), BetterFormEventNames.AVT_CHANGED, contextInfo);

            }

        }

        updateChildren();
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
        disposeSelf();
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
        return getXFormsAttribute(REF_ATTRIBUTE);
    }

    /**
     * Returns the id of the binding element.
     *
     * @return the id of the binding element.
     */
    public String getBindingId() {
        Bind bind = getModelBinding();
        if (bind != null) {
            return bind.getId();
        }

        return this.id;
    }

    /**
     * Returns the enclosing element.
     *
     * @return the enclosing element.
     */
    public Binding getEnclosingBinding() {
        return getEnclosingBinding(this, true);
    }

    /**
     * Returns the location path.
     *
     * @return the location path.
     */
    public String getLocationPath() {
        if (!hasBindingExpression()) {
            return null;
        }

        if (isRepeated()) {
            // don't cache location path for repeated items
            return BindingResolver.getExpressionPath(this, getRepeatItemId());
        }

        if (this.locationPath == null) {
            // cache location path for non-repeated items
            this.locationPath = BindingResolver.getExpressionPath(this, null);
        }

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

    // implementation of 'de.betterform.xml.events.DefaultAction'

    /**
     * Performs the implementation specific default action for this event.
     *
     * @param event the event.
     */
    public void performDefault(Event event) {
        if (event.getType().equals(XFormsEventNames.BINDING_EXCEPTION)) {
            getLogger().error(this + " binding exception: " + ((XMLEvent) event).getContextInfo());
        }
    }

    // bound element methods

    /**
     * Checks wether this element has a binding expression to a model item.
     * <p/>
     *
     * @return <code>true</code> has a binding expression to a model item,
     *         otherwise <code>false</code>.
     */
    public boolean hasBindingExpression() {
        return hasModelBinding() || hasUIBinding();
    }

    /**
     * Returns the id of the instance this element is bound to.
     * <p/>
     * The instance id is determined as follows: <ol> <li>If the location path
     * starts with a <code>instance()</code> function, the instance id is
     * obtained from the argument of that fuction.</li> <li>If there is a
     * default instance in the corresponding model, the instance id is obtained
     * from the default instance.</li> <li>The instance id is set empty, which
     * maps to the default instance.</li> </ol>
     *
     * @return the id of the instance this element is bound to.
     */
    public String getInstanceId() throws XFormsException {
        // lazy initialization
        if (this.instanceId == null) {
            this.instanceId = this.model.computeInstanceId(getLocationPath());
        }

        return this.instanceId;
    }

    /**
     * Returns the value of the instance node this element is bound to.
     *
     * @return the value of the instance node this element is bound to.
     */
    public String getInstanceValue() throws XFormsException {
        return  XPathUtil.getAsString(getNodeset(), getPosition());
    }
    
    public Node getInstanceNode() throws XFormsException {
         return de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(getNodeset(), getPosition());
    }
    
    /**
     * Checks wether this element has an ui binding.
     * <p/>
     * This element has an ui binding if it has a <code>ref</code> attribute.
     *
     * @return <code>true</code> if this element has an ui binding, otherwise
     *         <code>false</code>.
     */
    protected boolean hasUIBinding() {
        return getXFormsAttribute(REF_ATTRIBUTE) != null;
    }

    /**
     * Checks wether this element has a model binding.
     * <p/>
     * This element has a model binding if it has a <code>bind</code>
     * attribute.
     *
     * @return <code>true</code> if this element has a model binding, otherwise
     *         <code>false</code>.
     */
    protected boolean hasModelBinding() {
        return getXFormsAttribute(BIND_ATTRIBUTE) != null;
    }

    /**
     * Returns the model binding of this element.
     *
     * @return the model binding of this element.
     */
    protected Bind getModelBinding() {
        String bindAttribute = getXFormsAttribute(BIND_ATTRIBUTE);
        if (bindAttribute != null) {
            return (Bind) this.container.lookup(bindAttribute);
        }

        return null;
    }

    // lifecycle template methods

    /**
     * Initializes the default action.
     */
    protected void initializeDefaultAction() {
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.BINDING_EXCEPTION, this);
    }

    /**
     * Initializes the bound instance node.
     * <p/>
     * This methods implements the behaviour defined in '4.2.2 The
     * xforms-model-construct-done Event'.
     *
     * @throws XFormsException if an error occurred during init.
     */
    protected final void initializeInstanceNode() throws XFormsException {
        if (hasBindingExpression()) {
            // 4.2.2 The xforms-model-construct-done Event
            // The default action for this event happens once, no matter how many XForms
            // Models are present in the containing document, and results in the following,
            // for each form control:
            // Processing can proceed in one of two different ways depending on whether an
            // instance in a model exists when the first form control is processed.
            // If the instance referenced on the form control existed when the first form
            // control was processed:
            // 1. The binding expression is evaluated to ensure that it points to a node
            //    that exists. If this is not the case then the form control should behave
            //    in the same manner as if it had bound to a model item with the relevant
            //    model item property resolved to false.
            // If the instance referenced on the form control did not exist when the first
            // form control for the same instance was processed:
            // 1. For the first reference to an instance a default instance is created by
            //    following the rules described below.
            //    a. A root instanceData element is created.
            //    b. An instance data element node will be created using the binding expression
            //       from the user interface control as the name. If the name is not a valid QName,
            //       processing halts with an exception (4.5.1 The xforms-binding-exception Event).
            // 2. For the second and subsequent references to an instance which was automatically
            //    created the following processing is performed:
            //    a. If a matching instance data node is found, the user interface control will be
            //       connected to that element.
            //    b. If a matching instance data node is not found, an instance data node will be
            //       created using the binding expression from the user interface control as the name.
            //       If the name is not a valid QName, processing halts with an exception (4.5.1 The
            //       xforms-binding-exception Event).
            String instanceId = getInstanceId();
            Instance instance = getModel().getInstance(instanceId);

            if (instance == null) {
                // create instance
                instance = this.model.addInstance(instanceId);
            }

            updateXPathContext();
            if (nodeset.size() == 0) {
                // instance node for this element doesn't exist
                if (instance.hasInitialInstance()) {
                    // disabling is handled by data element
                } else {
                    List parentNodeset = evalInScopeContext();
                    if (parentNodeset.size() == 0) {
                        instance.createRootElement("instanceData");
                        parentNodeset = evalInScopeContext();
                        instance.setLazyXPathContext(parentNodeset);
                    }
                    instance.createNode(parentNodeset, getPosition(), getBindingExpression());
                    updateXPathContext();
                }
            }
        }
    }


    /**
     * Initializes the element state.
     *
     * @throws XFormsException if an error occurred during init.
     */
    protected final void initializeElementState() throws XFormsException {
        this.elementState = createElementState();
        if (this.elementState != null) {
            this.elementState.setOwner(this);
            this.elementState.init();
        }
    }

    /**
     * Updates the childEvaluationContext
     *
     * @throws XFormsException in case an XPathException happens during evaluation
     */
    protected void updateXPathContext() throws XFormsException {
        List resultNodeset;
        // TODO: find better solution to handle bound nodes within nodes with ref attribute (see ScopedResolutionTest)
        if(this.hasModelBinding() && getModelBinding().getEnclosingBinding() != null && !isRepeated()){
            Binding binding = getModelBinding();
            binding.getEnclosingBinding().getNodeset();
            resultNodeset= binding.getEnclosingBinding().getNodeset();
        }else{
            resultNodeset =evalInScopeContext();
        }

//        if(LOGGER.isDebugEnabled()){
//            NodeWrapper info = (NodeWrapper) parentNodeset.get(0);
//            LOGGER.debug(this + " bound to Node:" + info.getUnderlyingNode());
//            LOGGER.debug("in scope xpath context for " + this + " = " + BindingResolver.getExpressionPath(this,repeatItemId));
//        }

        final String relativeExpr = getBindingExpression();
        try {
            if (relativeExpr != null)
                nodeset = XPathCache.getInstance().evaluate(resultNodeset, getPosition(), relativeExpr, getPrefixMapping(), xpathFunctionContext);
            else
                nodeset = resultNodeset;
        }
        catch (XFormsException e) {
            throw new XFormsBindingException(e.getMessage(), this.target, null);
        }
    }


    public List getNodeset() {
        return nodeset;
    }

    public int getPosition() {
        return position;
    }


    /**
     * Updates the element state.
     *
     * @throws XFormsException if an error occurred during update.
     */
    protected final void updateElementState() throws XFormsException {
        if(LOGGER.isTraceEnabled()){
            LOGGER.debug(">>> before update <<<");
            DOMUtil.prettyPrintDOM(this.element);
        }
        if (this.elementState != null) {
            this.elementState.update();
        }
        if(LOGGER.isTraceEnabled()){
            LOGGER.debug(">>> after update <<<");
            DOMUtil.prettyPrintDOM(this.element);
        }
    }

    /**
     * Disposes the element state.
     *
     * @throws XFormsException if an error occurred during disposal.
     */
    protected final void disposeElementState() throws XFormsException {
        if (this.elementState != null) {
            this.elementState.dispose();
            this.elementState = null;
        }
    }

    /**
     * Disposes the default action.
     */
    protected void disposeDefaultAction() {
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.BINDING_EXCEPTION, this);
    }

    public String getNodeValue() throws XFormsException {
        return this.model.getInstance(this.instanceId).getNodeValue(de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(getNodeset(), getPosition()));
    }

    public void setNodeValue(String value) throws XFormsException {
        this.model.getInstance(this.instanceId).setNodeValue(de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(getNodeset(), getPosition()), value);
    }

    public ModelItem getModelItem() {
        return this.model.getInstance(this.instanceId).getModelItem(de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(getNodeset(), getPosition()));
    }

    // template methods

    /**
     * Factory method for the element state.
     *
     * @return an element state implementation or <code>null</code> if no
     * state keeping is required.
     * @throws XFormsException if an error occurred during creation.
     */
    protected abstract UIElementState createElementState() throws XFormsException;

    public UIElementState getUIElementState() {
        return this.elementState;
    }
}

// end of class
