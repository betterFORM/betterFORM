/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xpath.impl.saxon;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.saxon.dom.NodeWrapper;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.DoubleValue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.betterform.xml.ns.NamespaceResolver;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;

/**
 * Utility function working with the Saxon XPath implementation
 *
 * @author Nick Van den Bleeken
 * @author Joern Turner
 * @version $Id$
 */
public class XPathUtil {

    /**
     * returns item at position as if converted with the string() function
     * @param nodeset
     * @param position
     * @return
     */
    public static String getAsString(List nodeset, int position) {
        List resultAsNodeset = nodeset;
        if (resultAsNodeset.size() >= position)
            return ((XdmItem) resultAsNodeset.get(position - 1)).getStringValue();
        else
            return null;
    }

    public static boolean getAsBoolean(List nodeset, int position) {
        if (nodeset.size() > position) {
            return false;
        }

        XdmItem item = ((XdmItem) nodeset.get(position - 1));
        try {
        	if (item instanceof XdmAtomicValue) {
        		return ((XdmAtomicValue) item).getBooleanValue();
        	}
		} catch (SaxonApiException e) {
			
		}
        return false;
    }
    
    public static double getAsDouble(List nodeset, int position) {
        if (nodeset.size() > position) {
            return Double.NaN;
        }

        XdmItem item = ((XdmItem) nodeset.get(position - 1));
        try {
        	if (item instanceof XdmAtomicValue) {
        		return ((XdmAtomicValue) item).getDoubleValue();
        	}
		} catch (SaxonApiException e) {
			
		}
        return Double.NaN;
    }


    /**
     * returns a List of NodeInfo objects that represent the bound nodes of given bound element.
     *
     * @param bindingElement the bound element for which the list of bound nodes is returned
     * @return a List of NodeInfo objects that represent the bound nodes of given bound element.
     */
//    public static List evalInScopeContext(Binding bindingElement) {
//        List resultNodeset;
//        if (bindingElement != null && bindingElement instanceof BindingElement){
//            resultNodeset =  bindingElement.getNodeset();
//        }else{
//            XFormsElement xfElement = (XFormsElement)bindingElement;
//            resultNodeset = xfElement.getModel().getDefaultInstance().getInstanceNodeset();
//        }
//        return resultNodeset;
//    }

    /**
     * Checks if the specified node exists.
     *
     * @param contextNodeset    the nodeset relative to which the path is evaluated
     * @param positionInNodeset the position in the nodeset that will be used as the parent context
     * @param path              the path pointing to a node.
     * @return <code>true</code> if the specified node exists, otherwise
     *         <code>false</code>.
     * @throws net.sf.saxon.trans.XPathException
     */
    public static boolean existsNode(List contextNodeset, int positionInNodeset, String path, Map prefixMapping, XPathFunctionContext functionContext) throws XFormsException {
        return XPathCache.getInstance().evaluate(contextNodeset, positionInNodeset, path, prefixMapping, functionContext).size() > 0;
    }

    public static Node getAsNode(List nodeset, int position) {
        List resultAsNodeset = nodeset;
        if (resultAsNodeset.size() >= position) {
            XdmItem item = ((XdmItem) resultAsNodeset.get(position - 1));
            if (item instanceof XdmNode) {
                return (Node) ((XdmNode) item).getUnderlyingNode();
            }
        }
        return null;
    }

    /**
     * returns the NodeInfo object corresponding to the root element in doc wrapped in a List with a single entry.
     * @param doc the DOM Document in context
     * @param baseURI the base URI of the Document
     * @return
     */
     public static List getRootContext(Document doc,String baseURI) {
    	 final Element documentElement = doc.getDocumentElement();
    	 return getElementContext(documentElement, baseURI);
    }

    public static List getElementContext(Element element,String baseURI){
        if (element != null){
        	Processor processor = new Processor(false);
        	return Collections.singletonList(processor.newDocumentBuilder().wrap(element));
        } else
            return Collections.emptyList();
    }

    public static Node evaluateAsSingleNode(Document doc, String xpath,String baseURI) throws XFormsException {
        List context = XPathUtil.getRootContext(doc,baseURI);
        Map namespaces = NamespaceResolver.getAllNamespaces(doc.getDocumentElement());
       return XPathCache.getInstance().evaluateAsSingleNode(context, 1, xpath, namespaces, null);
    }

    /**
     * single shot XPath evaluation e.g. for use in testcases
     * todo: does not use baseURI - functions doing URI resolutions will fail!
     */
    public static Node evaluateAsSingleNode(Document doc, String xpath) throws XFormsException {
        return evaluateAsSingleNode(doc,xpath,"");
    }

    /**
     * todo: does not use baseURI - functions doing URI resolutions will fail!
     * @param doc
     * @param xpath
     * @return
     * @throws XFormsException
     */
    public static List evaluate(Document doc, String xpath) throws XFormsException {
        return XPathUtil.evaluate(doc.getDocumentElement(),xpath);
    }

    /**
     * todo: does not use baseURI - functions doing URI resolutions will fail!
     * @param element
     * @param xpath
     * @return
     */
    public static List evaluate(Element element, String xpath) throws XFormsException {
        List context = XPathUtil.getElementContext(element,null);
        Map namespaces = NamespaceResolver.getAllNamespaces(element);
        NodeInfo root = (NodeInfo) context.get(0);
        return XPathCache.getInstance().evaluate(root,xpath,namespaces,null);
    }

    /**
     * todo: does not use baseURI - functions doing URI resolutions will fail!
     * @param doc
     * @param xpath
     * @return
     * @throws XFormsException
     */
    public static String evaluateAsString(Document doc,String xpath) throws XFormsException {
        List context = XPathUtil.getRootContext(doc,null);
        NamespaceResolver.init(doc.getDocumentElement());
        Map namespaces = NamespaceResolver.getAllNamespaces(doc.getDocumentElement());
        return XPathCache.getInstance().evaluateAsString(context,1,xpath,namespaces,null);
    }

    /**
     * todo: does not use baseURI - functions doing URI resolutions will fail!
     * @param element
     * @param xpath
     * @return
     * @throws XFormsException
     */
    public static String evaluateAsString(Element element,String xpath) throws XFormsException {
       List context = XPathUtil.getElementContext(element,null);
       NamespaceResolver.init(element);
       Map namespaces = NamespaceResolver.getAllNamespaces(element);
       return XPathCache.getInstance().evaluateAsString(context,1,xpath,namespaces,null);
    }
   
}
