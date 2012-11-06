/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.ns.NamespaceResolver;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.bind.Binding;
import de.betterform.xml.xforms.model.bind.BindingResolver;
import de.betterform.xml.xforms.ui.*;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.XPathUtil;
import de.betterform.xml.xpath.impl.saxon.XPathCache;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventTarget;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Superclass for all XForms elements. This includes either all elements from
 * the XForms namespace and all bound elements which may be from foreign
 * namespaces but wear XForms binding attributes. Custom elements also extend
 * this class.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XFormsElement.java 3483 2008-08-20 10:16:24Z joern $
 */
public abstract class XFormsElement implements XFormsConstants {
    /**
     * the Container object holding the whole DOM Document of this form
     */
    protected Container container = null;

    /**
     * the annotated DOM Element
     */
    protected Element element = null;

    /**
     * the DOM EventTarget
     */
    protected EventTarget target = null;

    /**
     * the Model object of this XFormsElement
     */
    protected Model model = null;

    /**
     * the id of this Element
     */
    protected String id;

    /**
     * the original id of this Element (when repeated)
     */
    protected String originalId;

    /**
     * the xforms prefix used in this Document
     */
    protected String xformsPrefix = null;
    protected final Map prefixMapping;

    protected final XPathFunctionContext xpathFunctionContext;


    /**
     * Creates a new XFormsElement object.
     *
     * @param element the DOM Element annotated by this object
     */
    public XFormsElement(Element element) {
        this.element = element;
        this.target = (EventTarget) element;

        this.container = getContainerObject();
        this.xformsPrefix = NamespaceResolver.getPrefix(this.element, NamespaceConstants.XFORMS_NS);

        prefixMapping = NamespaceResolver.getAllNamespaces(this.element); 


        xpathFunctionContext = new XPathFunctionContext(this);

        // todo: perform id mgmt and registration explicitely in a component builder
        if (this.element.hasAttributeNS(null, "id")) {
            this.id = this.element.getAttributeNS(null, "id");
        } else {
            this.id = this.container.generateId();
            this.element.setAttributeNS(null, "id", this.id);
        }
        registerId();
    }

    /**
     * Creates a new XFormsElement object.
     *
     * @param element the DOM Element annotated by this object
     * @param model   the Model object of this XFormsElement
     */
    public XFormsElement(Element element, Model model) {
        this(element);
        this.model = model;
    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public abstract void init() throws XFormsException;

    /**
     * Performs element disposal.
     *
     * @throws XFormsException if any error occurred during disposal.
     */
    public abstract void dispose() throws XFormsException;


    /**
     * returns the Container object of this Element.
     *
     * @return Container object of this Element
     */
    public Container getContainerObject() {
        return (Container)this.element.getOwnerDocument().getDocumentElement().getUserData("");
    }

    /**
     * Returns the DOM element of this element.
     *
     * @return the DOM element of this element.
     */
    public Element getElement() {
        return this.element;
    }

    // member access methods

    /**
     * Returns the global id of this element.
     *
     * @return the global id of this element.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the context model of this element.
     *
     * @return the context model of this element.
     */
    public Model getModel() {
        return this.model;
    }

    /**
     * returns the parent XFormsElement object of the DOM parent Node if any or
     * null otherwise.
     *
     * @return the parent XFormsElement object of the DOM parent Node if any or
     *         null otherwise.
     */
    public XFormsElement getParentObject() {
        return (XFormsElement) this.element.getParentNode().getUserData("");
    }

    /**
     * Returns the DOM event target of this element.
     *
     * @return the DOM event target of this element.
     */
    public EventTarget getTarget() {
        return this.target;
    }

    /**
     * returns the value of a given XForms attribute
     *
     * @param name the localname of the attribute
     * @return the value of the attribute as string or null if attribute is not found
     */
    public String getXFormsAttribute(String name) {
        return getXFormsAttribute(this.element, name);
    }

    /**
     * returns the value of a given XForms attribute. First tries to fetch it from
     * the XForms namespace. If not successful tries to find it in the null namespace.
     *
     * @param name the localname of the attribute
     * @return the value of the attribute as string or null if attribute is not found
     */
    public static String getXFormsAttribute(Element element, String name) {
        if (element.hasAttributeNS(NamespaceConstants.XFORMS_NS, name)) {
            return element.getAttributeNS(NamespaceConstants.XFORMS_NS, name);
        }
        if (element.hasAttributeNS(null, name)) {
            return element.getAttributeNS(null, name);
        }
        return null;
    }
    
    /**
     * returns the value of a given BetterForm attribute
     *
     * @param name the localname of the attribute
     * @return the value of the attribute as string or null if attribute is not found
     */
    public String getBFAttribute(String name) {
        return getBFAttribute(this.element, name);
    }
    
    /**
     * returns the list of all attributes that are not in 'known' namespaces and do not have the null (default?) namespace
     *
     * 
     * @return the key-value-pair of the attributes
     */
    public Map<String, String> getCustomMIPAttributes() {
    	
    	 HashMap<String, String> customMIPAttributes = new HashMap<String, String>(); 
    	 NamedNodeMap nnm = element.getAttributes();
    	 for (int i = 0; i < nnm.getLength(); i++) {
			Node attribute = nnm.item(i);
		   	if (attribute.getNamespaceURI() != null &&
		   			!NamespaceConstants.BETTERFORM_NS.equals(attribute.getNamespaceURI()) &&
		   			!NamespaceConstants.XFORMS_NS.equals(attribute.getNamespaceURI()) &&
		   			!NamespaceConstants.XHTML_NS.equals(attribute.getNamespaceURI()) &&
		   			!NamespaceConstants.XMLNS_NS.equals(attribute.getNamespaceURI()) &&
		   			!NamespaceConstants.XMLSCHEMA_INSTANCE_NS.equals(attribute.getNamespaceURI()) &&
		   			!NamespaceConstants.XMLEVENTS_NS.equals(attribute.getNamespaceURI())
		   			) {
		   		customMIPAttributes.put(attribute.getPrefix() + WordUtils.capitalize(attribute.getLocalName()), attribute.getTextContent());
		   	}
		}
    	 return customMIPAttributes;
    	 
    }

    /**
     * returns the value of a given BetterForm attribute. First tries to fetch it from
     * the XForms namespace. If not successful tries to find it in the null namespace.
     *
     * @param name the localname of the attribute
     * @return the value of the attribute as string or null if attribute is not found
     */
    public static String getBFAttribute(Element element, String name) {
        if (element.hasAttributeNS(NamespaceConstants.BETTERFORM_NS, name)) {
            return element.getAttributeNS(NamespaceConstants.BETTERFORM_NS, name);
        }
        return null;
    }

    /**
     * @return the an unmodifiable map with the prefix namespace mapping for this element.
     */
    public Map getPrefixMapping() {
        return Collections.unmodifiableMap(prefixMapping);
    }

    /**
     * @return the the XPathFunctionContext for this element.
     */
    public XPathFunctionContext getXPathFunctionContext() {
        return xpathFunctionContext;
    }

    // id handling

    /**
     * Registers this element with the container.
     */
    public void registerId() {
        this.container.register(this);

        if (this.originalId != null) {
            String tmpId = this.id;
            this.id = this.originalId;
            this.container.register(this);
            this.id = tmpId;
        }
    }

    /**
     * Deregisters this element from the container.
     */
    public void deregister() {
        this.container.deregister(this);
    }

    /**
     * Stores the original id and sets the generated id as new id.
     *
     * @param generatedId the generated id.
     */
    public void setGeneratedId(String generatedId) throws XFormsException {
        this.originalId = this.id;
        this.id = generatedId;
        this.element.setAttributeNS(null, "id", this.id);

        if (this.model.isReady()) {
            // dispatch internal betterform event
            HashMap map = new HashMap();
            map.put("originalId", this.originalId);
            this.container.dispatch(this.target, BetterFormEventNames.ID_GENERATED, map);
        }
    }

    // standard methods

    /**
     * Check wether this object and the specified object are equal.
     *
     * @param object the object in question.
     * @return <code>true</code> if this object and the specified object are
     *         equal, <code>false</code> otherwise.
     */
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (object == this) {
            return true;
        }

        if (!(object instanceof XFormsElement)) {
            return false;
        }

        return ((XFormsElement) object).getId().equals(getId());
    }

    /**
     * Returns a string representation of this object.
     *
     * @return a string representation of this object.
     */
    public String toString() {
        return DOMUtil.getCanonicalPath(getElement()) + "/@id[.='" + getId() + "']";
    }

    /**
     * Returns the logger object.
     *
     * @return the logger object.
     */
     protected abstract Log getLogger();

    /**
     * returns true if the current events' default processing is cancelled by
     * some listener on the element itself e.g.
     * <pre>
     *   &lt;model ev:event="xforms-revalidate" ev:defaultAction="cancel"&gt;
     * </pre>
     * would cancel all revalidation (the default action for this event) for
     * this model.
     *
     * @param event the event to investigate
     * @return true if the current events' default processing is cancelled
     * @deprecated
     */
    protected boolean isCancelled(Event event) {
        if (event.getCancelable()) {

            // todo: check parents with phase='capture' for this event
            if (element.hasAttributeNS(NamespaceConstants.XMLEVENTS_NS, "event")) {
                String s = element.getAttributeNS(NamespaceConstants.XMLEVENTS_NS, "event");

                if (s.equals(event.getType())) {
                    if (element.getAttributeNS(NamespaceConstants.XMLEVENTS_NS, "defaultAction").equals("cancel")) {
                        return true; //performDefault gets cancelled by cancelling listener on this model
                    }
                }
            }
        }
        return false;
    }

    /**
     * determines the context path for given XFormsElement
     *
     * @param start the XFormsElement for which the parentContextPath will be calculated
     * @return the parent context path or null if no parent exists
     */
/*
    protected String getParentContextPath(Node start) {
        String result = null;
        NodeImpl parent = (NodeImpl) start.getParentNode();
        if (parent == null) return null;

        Object o = parent.getUserData();
        if (o instanceof XFormsElement) {
            XFormsElement xCurrent = (XFormsElement) parent.getUserData();
            if (xCurrent == null) {
                getParentContextPath(parent);
            }

            if (xCurrent instanceof BindingElement) {
                if (((BindingElement) xCurrent).hasBindingExpression()) {
                    String locationPath = ((BindingElement) xCurrent).getLocationPath();
                    return locationPath;
                } else {
                    result = getParentContextPath(parent.getParentNode());
                }
            }
        } else {
            return getParentContextPath(parent);
        }
        return result;
    }
*/

    /**
     * evaluates the 'in scope evaluation context' and returns a List of NodeInfo objects representing the
     * bound nodes. If no binding element is found on the ancestor axis the default instance for this model
     * is returned.
     * <p/>
     * Why is it here and not further down the hierarchy? It can be convenient to have an in-scope context always e.g.
     * to support AVTs on any XForms element. In this case it would be intuitive for the author to get the same
     * XPath evaluation behavior (taking scope into account) as always in XForms.
     *
     * @return a List of NodeInfo objects representing the bound nodes of this XFormsElement
     * @throws XFormsException 
     */
    public List evalInScopeContext() throws XFormsException {
        final List resultNodeset;
    	final Binding parentBoundElement = getEnclosingBinding(this, false);

        if (parentBoundElement != null) {
        	resultNodeset = parentBoundElement.getNodeset();
        } else if (this instanceof BindingElement && XPathUtil.isAbsolutePath(((BindingElement)this).getLocationPath())){
            BindingElement binding = (BindingElement) this;
            resultNodeset = this.model.getInstance(binding.getInstanceId()).getInstanceNodeset();    
        } else if(this.model.getDefaultInstance() != null){
                resultNodeset = this.model.getDefaultInstance().getInstanceNodeset();
        }  else {
                resultNodeset = Collections.EMPTY_LIST;
        }

        if (resultNodeset == null) {
            if (parentBoundElement == null) {
                return null;
                // throw new XFormsException("Impossible case happened - go away quickly");
            } else {
                getLogger().info("Context ResultSet is null for element: " + DOMUtil.getCanonicalPath(((XFormsElement) parentBoundElement).getElement()));
                return Collections.EMPTY_LIST;
            }
        }

        final String contextExpression = getContextExpression();
        if (contextExpression == null || getXFormsAttribute(BIND_ATTRIBUTE) != null) {
        	return resultNodeset;
        }
        
        final List contextResultNodeSet = XPathCache.getInstance().evaluate(resultNodeset, 1, contextExpression, getPrefixMapping(), xpathFunctionContext);
        if ((contextResultNodeSet == null) || (contextResultNodeSet.size() == 0)) return contextResultNodeSet;
        else return Collections.singletonList(contextResultNodeSet.get(0));
    }


    /**
     * Returns the enclosing binding element of the specified xforms element.
     *
     * @param xFormsElement the xforms element.
     * @param returnBind if true returns the bind that corresponds to the UI control instead of the UI control itself
     * @return the enclosing binding element of the specified xforms element or
     * <code>null</code> if there is no enclosing binding element.
     */

    public Binding getEnclosingBinding(XFormsElement xFormsElement, boolean returnBind){
        Binding enclosingBinding = null;
        Container container = xFormsElement.getContainerObject();
        Node currentNode = xFormsElement.getElement();
        String modelId = xFormsElement.getModel().getId();

        while (true) {
            Node parentNode = currentNode.getParentNode();

            if (parentNode == null) {
                break;
            }

            if (!(parentNode instanceof Element)) {
                break;
            }

            Element elementImpl = (Element) parentNode;
            Object o = elementImpl.getUserData("");

            if (BindingResolver.hasModelBinding(elementImpl)) {
                Binding binding = (Binding) o;

                if (binding.getModelId().equals(modelId)) {
                    String bindId = binding.getBindingId();
                    if (returnBind) {
                        enclosingBinding = (Binding) container.lookup(bindId);
                    }
                    else {
                        enclosingBinding = binding;
                    }
                    break;
                }
            }

            Binding enclosingUIBinding = getEnclosingUIBinding(elementImpl, o,modelId);
            if(enclosingUIBinding != null) {
                enclosingBinding = enclosingUIBinding;
                break;
            }
            currentNode = parentNode;
        }

        return enclosingBinding;
    }

    public Binding getEnclosingUIBinding(Element elementImpl, Object binding, String modelId) {
        if(!(binding instanceof Binding)){
            return null;
        }
        
        Binding bindingElem = (Binding) binding;
        if (BindingResolver.hasUIBinding(elementImpl)|| (bindingElem instanceof Group && bindingElem.hasBindingExpression() || bindingElem instanceof RepeatItem) || (bindingElem instanceof Item) && (((Item)bindingElem).getItemset() != null)) {
             if (bindingElem.getModelId().equals(modelId)) {
                 return bindingElem;
             }
         }
        return null;
    }



    public XFormsElement getEnclosingXFormsContainer() {
        Node currentNode = this.getElement();
        XFormsElement enclosingContainer = null;
        while (true) {
            Node parentNode = currentNode.getParentNode();

            if (parentNode == null) {
                break;
            }

            if (!(parentNode instanceof Element)) {
                break;
            }

            Element elementImpl = (Element) parentNode;
            Object containerObject = elementImpl.getUserData("");
            if(containerObject instanceof Group || containerObject instanceof Switch || containerObject instanceof Repeat){
                enclosingContainer = (XFormsElement) containerObject;
                break;
            }
            currentNode = parentNode;
        }
        return enclosingContainer;


    }



    /**
     * Returns the context expression.
     * This method is introduced on {@link XFormsElement} to make it future proof. In XForms 1.2 context will be allowed on all XForms elements,
     * and this will allow us to implement the context attribute on all XForms elements by just returning context expression here.
     *
     * @return the context expression.
     */
    public String getContextExpression() {
    	return null;
    	//return getXFormsAttribute(CONTEXT_ATTRIBUTE); // Uncomment this when XForms 1.2 allows context everywhere
    }

}

// end of class
