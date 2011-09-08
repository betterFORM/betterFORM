/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.bind;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xpath.XPathUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The binding resolver implements the scoped resolution of model binding
 * expressions as well as ui binding expressions.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: BindingResolver.java 3477 2008-08-19 09:26:47Z joern $
 */
public class BindingResolver {


    /**
     * Returns the fully resolved expression path of the specified xforms
     * element.
     *
     * @param xFormsElement the xforms element.
     * @param repeatEntryId the id of the repeat entry which contains the xforms
     * element (optional).
     * @return the fully resolved expression path of the specified xforms
     * element.
     */
    public static String getExpressionPath(XFormsElement xFormsElement, String repeatEntryId) {
        String expressionPath;
        Binding bindingElement;
        Container container = xFormsElement.getContainerObject();
        boolean modelBinding = BindingResolver.hasModelBinding(xFormsElement.getElement());

        if (modelBinding) {
            String bindId = ((Binding) xFormsElement).getBindingId();
            bindingElement = (Binding) container.lookup(bindId);
        }
        else {
            bindingElement = (Binding) xFormsElement;
        }

        // UI controls as well as actions can have a repeatItemId. This id
        // references either a RepeatItem (in Repeats) or an Item (in Itemsets).
        if (repeatEntryId != null) {
            // lookup repeat id for relative resolution
            Binding repeatItem = (Binding) container.lookup(repeatEntryId);
            String relativeId = repeatItem.getBindingId();

            // resolve relatively to enclosing repeat
            String relativePath = container.getBindingResolver().resolve(bindingElement, relativeId);

            if (XPathUtil.isAbsolutePath(relativePath)) {
                // resolved to absolute path
                expressionPath = relativePath;
            }
            else {
                if (relativePath.length() == 0) {
                    // resolve enclosing repeat
                    expressionPath = repeatItem.getLocationPath();
                }
                else {
                    // resolve enclosing repeat and attach relative path
                    expressionPath = repeatItem.getLocationPath() + "/" + relativePath;
                }
            }
        }
        else {
            // resolve expression path
            expressionPath = container.getBindingResolver().resolve(bindingElement);
        }

        // return expression path
        return expressionPath;
    }

    /**
     * returns a canonical XPath locationpath for a given Node. Each step in the path will contain the positional
     * predicate of the Element. Example '/*[1]/a[1]/b[2]/c[5]/@d'. This would point to<br/>
     * to Attribute named 'd'<br/>
     * on 5th Element 'c"<br/>
     * on 2nd Element 'b'<br/>
     * on first Element a<br/>
     * which is a child of the Document Element.
     * @param node the Node where to start
     * @return canonical XPath locationPath for given Node
     * todo: this is a duplicate to DOMUtil.getCanonicalPath - should be resolved
     */
    public static String getCanonicalPath(Node node) {

        //add ourselves
        String canonPath = node.getNodeName();
        if(node.getNodeType() == Node.ATTRIBUTE_NODE){
            canonPath = "@" + canonPath;
        }else if (node.getNodeType() == Node.ELEMENT_NODE){
            int position = DOMUtil.getCurrentNodesetPosition(node);
            //append position if we are an Element
            if(node.getParentNode() != null && node.getParentNode().getNodeType() != Node.DOCUMENT_NODE){
                canonPath += "[" + position + "]";
            }else{
                canonPath = "*[1]";
            }
        }

        //check for parent - if there's none we're root
        Node parent = null;
        if(node.getNodeType() == Node.ELEMENT_NODE || node.getNodeType() == Node.TEXT_NODE){
            parent = node.getParentNode();
        }else if(node.getNodeType() == Node.ATTRIBUTE_NODE){
            parent = ((Attr)node).getOwnerElement();
        }

        if (parent == null || parent.getNodeType() == Node.DOCUMENT_NODE || parent.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE){
            canonPath = "/" + canonPath;
        }else{
            canonPath = BindingResolver.getCanonicalPath(parent) + "/" + canonPath;
        }
        return canonPath;
    }

    /**
     * Checks wether the specified binding element has a model binding.
     *
     * @param element the binding element.
     * @return <code>true</code> if the binding element has a model binding
     * reference, otherwise <code>false</code>.
     */
    public static boolean hasModelBinding(Element element) {
        return element.hasAttributeNS(NamespaceConstants.XFORMS_NS, XFormsConstants.BIND_ATTRIBUTE) ||
                element.hasAttributeNS(null, XFormsConstants.BIND_ATTRIBUTE) ||
                element.hasAttributeNS(NamespaceConstants.XFORMS_NS, XFormsConstants.REPEAT_BIND_ATTRIBUTE);
    }

    // public helper methods

    /**
     * Checks wether the specified binding element has a model binding
     * expression.
     *
     * @param element the binding element.
     * @return <code>true</code> if the binding element has a model binding
     * expression, otherwise <code>false</code>.
     */
    public static boolean hasModelBindingExpression(Element element) {
        return NamespaceConstants.XFORMS_NS.equals(element.getNamespaceURI()) &&
                element.getLocalName().equals(XFormsConstants.BIND) &&
                (element.hasAttributeNS(NamespaceConstants.XFORMS_NS, XFormsConstants.NODESET_ATTRIBUTE) ||
                element.hasAttributeNS(null, XFormsConstants.NODESET_ATTRIBUTE));
    }

    /**
     * Checks wether the specified binding element has a nodeset binding
     * expression.
     *
     * @param element the binding element.
     * @return <code>true</code> if the binding element has a nodeset binding
     * expression, otherwise <code>false</code>.
     */
    private static boolean hasNodesetBindingExpression(Element element) {
        return element.hasAttributeNS(NamespaceConstants.XFORMS_NS, XFormsConstants.NODESET_ATTRIBUTE) ||
                element.hasAttributeNS(null, XFormsConstants.NODESET_ATTRIBUTE) ||
                element.hasAttributeNS(NamespaceConstants.XFORMS_NS, XFormsConstants.REPEAT_NODESET_ATTRIBUTE);
    }

    /**
     * Checks wether the specified binding element has a single node binding
     * expression.
     *
     * @param element the binding element.
     * @return <code>true</code> if the binding element has a single node
     * binding expression, otherwise <code>false</code>.
     */
    private static boolean hasSingleNodeBindingExpression(Element element) {
        return element.hasAttributeNS(NamespaceConstants.XFORMS_NS, XFormsConstants.REF_ATTRIBUTE) ||
                element.hasAttributeNS(null, XFormsConstants.REF_ATTRIBUTE);
    }

    /**
     * Checks wether the specified binding element has an ui binding.
     *
     * @param element the binding element.
     * @return <code>true</code> if the binding element has an ui binding
     * expression, otherwise <code>false</code>.
     */
    public static boolean hasUIBinding(Element element) {
        return (!BindingResolver.hasModelBinding(element)) &&
                (BindingResolver.hasSingleNodeBindingExpression(element) ||
                BindingResolver.hasNodesetBindingExpression(element));
    }

    // scoped resolution

    /**
     * Returns the fully resolved location path.
     * <p/>
     * This method implements Scoped Resolution as defined in the <code>7.3
     * Evaluation Context</code> chapter of the spec.
     *
     * @param binding the binding object to be resolved.
     * @return the fully resolved location path.
     */
    public String resolve(Binding binding) {
        return resolve(binding, null);
    }

    /**
     * Returns the relatively resolved location path.
     * <p/>
     * This method implements partial resolution up to the specified binding
     * object. If no id is given or is not passed during resolution this method
     * implements Scoped resolution.
     *
     * @param binding the binding object to be resolved.
     * @param relativeId the id of the binding object where resolution stops.
     * This parameter is optional and is used for the stepwise resolution of
     * repeated elements.
     * @return the fully resolved location path.
     */
    private String resolve(Binding binding, String relativeId) {
    	// [0] get context expression
    	String expression = binding.getContextExpression();
    	
        // [1] get binding expression
        String bindingExpression = binding.getBindingExpression();
        if (bindingExpression != null) {
        	if ((expression == null) || (expression.length() == 0) || XPathUtil.isDotReference(expression) || XPathUtil.isAbsolutePath(bindingExpression)) {
        		expression = bindingExpression;
        	}
        	else {
        		expression = expression + "/" + bindingExpression;
        	}
        }

        // [2] check for null, empty, or dot expression
        if ((expression == null) || (expression.length() == 0) || XPathUtil.isDotReference(expression)) {
            // [2.1] check for enclosing element
            Binding enclosing = binding.getEnclosingBinding();

            if (enclosing == null) {
                // return outermost binding expression
                return XPathUtil.OUTERMOST_CONTEXT;
            }

            // [2.2] check for relative id
            if (enclosing.getBindingId().equals(relativeId)) {
                // return empty expression
                return "";
            }

            // return enclosing element's expression
            return resolve(enclosing, relativeId);
        }

        // [3] strip eventual self reference
        expression = XPathUtil.stripSelfReference(expression);

        // [5] check for absolute reference
        if (XPathUtil.isAbsolutePath(expression)) {
            // return absolute expression
            return expression;
        }

        // [6] check for enclosing binding element
        Binding enclosing = binding.getEnclosingBinding();

        if (enclosing != null) {
            // [6.1] check for relative id.
            if (enclosing.getBindingId().equals(relativeId)) {
                // return relative expression
                return expression;
            }

            // [6.2] check for empty parent expression
            String parent = resolve(enclosing, relativeId);
            if (parent.length() == 0) {
                // return relative expression
                return expression;
            }

            // return composed binding expression
            return parent + "/" + expression;
        }

        // [7] compose outermost binding expression
        return XPathUtil.OUTERMOST_CONTEXT + "/" + expression;
    }

}
