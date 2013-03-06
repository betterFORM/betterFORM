/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xpath.impl.saxon;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.saxon.Configuration;
import net.sf.saxon.expr.StaticContext;
import net.sf.saxon.functions.ConstructorFunctionLibrary;
import net.sf.saxon.functions.ExtensionFunctionCall;
import net.sf.saxon.functions.ExtensionFunctionDefinition;
import net.sf.saxon.functions.FunctionLibrary;
import net.sf.saxon.functions.FunctionLibraryList;
import net.sf.saxon.functions.SystemFunctionLibrary;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XPathCompiler;
import net.sf.saxon.s9api.XPathExecutable;
import net.sf.saxon.s9api.XPathSelector;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.sxpath.XPathExpression;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.xpath.XPathEvaluator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.xpath.XPathFunctionLibrary;

import org.w3c.dom.Node;

import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.xpath.saxon.function.BetterFormFunctionLibrary;
import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunctionLibrary;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;

/**
 * A XPath cache that caches the XPath expressions.
 *
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class XPathCache {
	
    private static final Configuration kCONFIG = new Configuration();

	private static final FunctionLibraryList fgXFormsFunctionLibrary;

    private static final XPathCache fgXPathCache = new XPathCache();

    static {
        fgXFormsFunctionLibrary = new FunctionLibraryList();
        fgXFormsFunctionLibrary.addFunctionLibrary(SystemFunctionLibrary.getSystemFunctionLibrary(Configuration.XPATH));
        fgXFormsFunctionLibrary.addFunctionLibrary(new ConstructorFunctionLibrary(XPathCache.kCONFIG));
        fgXFormsFunctionLibrary.addFunctionLibrary(new XPathFunctionLibrary());
        fgXFormsFunctionLibrary.addFunctionLibrary(new XFormsFunctionLibrary());
        fgXFormsFunctionLibrary.addFunctionLibrary(new BetterFormFunctionLibrary());
//        fgXFormsFunctionLibrary.addFunctionLibrary(new JavaExtensionLibrary(XPathCache.kCONFIG));

    }

    public static XPathCache getInstance() {
        return fgXPathCache;
    }

    private XPathCache() {
    }

    public static FunctionLibraryList getFgXFormsFunctionLibrary() {
        return fgXFormsFunctionLibrary;
    }

    public String evaluateAsString(List nodeset, int position, String xpathString, Map prefixMapping, XPathFunctionContext functionContext)
        throws XFormsException {
        return XPathUtil.getAsString(evaluate(nodeset, position, xpathString, prefixMapping, functionContext), 1);
    }

    public String evaluateAsString(BetterFormXPathContext context, String xpathString)
        throws XFormsException {
        return XPathUtil.getAsString(evaluate(context.getNodeset(), context.getPosition(), xpathString, context.getPrefixMapping(), context.getXPathFunctionContext()), 1);
    }

    public Boolean evaluateAsBoolean(BetterFormXPathContext context, String xpathString)
        throws XFormsException {
        return XPathUtil.getAsBoolean(evaluate(context.getNodeset(), context.getPosition(), xpathString, context.getPrefixMapping(), context.getXPathFunctionContext()), 1);
    }

    public Boolean evaluateAsBoolean(List nodeset, int position, String xpathString, Map prefixMapping, XPathFunctionContext functionContext)
        throws XFormsException {
        return XPathUtil.getAsBoolean(evaluate(nodeset, position, xpathString, prefixMapping, functionContext), 1);
    }
    
    public double evaluateAsDouble(List nodeset, int position, String xpathString, Map prefixMapping, XPathFunctionContext functionContext)
	    throws XFormsException {
	    return XPathUtil.getAsDouble(evaluate(nodeset, position, xpathString, prefixMapping, functionContext), 1);
	}
	
	public double evaluateAsDouble(BetterFormXPathContext context, String xpathString)
	    throws XFormsException {
	    return XPathUtil.getAsDouble(evaluate(context.getNodeset(), context.getPosition(), xpathString, context.getPrefixMapping(), context.getXPathFunctionContext()), 1);
	}



    public List evaluate(BetterFormXPathContext context, String xpathString)
        throws XFormsException {
        return evaluate(context.getNodeset(), context.getPosition(), xpathString, context.getPrefixMapping(), context.getXPathFunctionContext());
    }

    public List evaluate(XdmNode contextNode, String xpathString, Map prefixMapping, XPathFunctionContext functionContext)
        throws XFormsException {
        return evaluate(Collections.singletonList(contextNode), 1, xpathString, prefixMapping, functionContext);
    }


    public Node evaluateAsSingleNode(List nodeset, int position, String xpath, Map prefixes, XPathFunctionContext functionContext) throws XFormsException {
        XdmNode node = (XdmNode) XPathCache.getInstance().evaluate(nodeset,1, xpath,prefixes,functionContext).get(0);
            return (Node) node.getUnderlyingNode();
    }

    public Node evaluateAsSingleNode(BetterFormXPathContext context,String xpath) throws XFormsException {

        List nodeList =  XPathCache.getInstance().evaluate(context, xpath);
        if(nodeList != null && nodeList.size() >= 1){
            XdmNode node = (XdmNode)nodeList.get(0);
            return (Node) node.getExternalNode();
         }else {
            return null;
        }
    }
    /**
     *
     */
    public List evaluate(List nodeset, int position, String xpathString, Map prefixMapping, XPathFunctionContext functionContext)
        throws XFormsException {
        LinkedList result = new LinkedList();

        if (nodeset != null && nodeset.size() < position) {
            return Collections.EMPTY_LIST;
        }

        Processor processor = XPathUtil.getProcessor();
        XPathCompiler xPathCompiler = processor.newXPathCompiler();

        for(Object key : prefixMapping.keySet()) {
            xPathCompiler.declareNamespace((String) key, (String) prefixMapping.get(key));
        }

        DocumentBuilder builder = processor.newDocumentBuilder();
        for (Object o : nodeset) {
	        try {
	        	XdmNode xdmNode; 
	        
	        	if (o instanceof XdmNode){
	        		xdmNode = (XdmNode) o;
	        	} else {
	        		xdmNode = builder.wrap(nodeset.get(0)); 
	        	}
	            XPathSelector xpathSelector = xPathCompiler.compile(xpathString).load();
	            xpathSelector.setContextItem(xdmNode);
	
	            for (XdmItem xdmItem : xpathSelector) {
	                result.add(xdmItem);
	            }
	        } catch (SaxonApiException sae) {
	            throw new XFormsException(sae.getMessage(), sae);
	        }
        }
        return result;
    }

    /**
     * @param xpathString
     * @param prefixMapping
     * @return
     * @throws XPathException
     */
    public XPathExpression getXPathExpression(String xpathString, Map prefixMapping, Processor processor) throws XPathException {
        
    	// TODO register in container
    	registerExtensionFunctions(processor);
    	
        XPathCompiler xPathCompiler = processor.newXPathCompiler();
        xPathCompiler.setBackwardsCompatible(true);
        //independentContext.setDefaultFunctionNamespace(NamespaceConstants.XFORMS_NS);
        
        for(Object key : prefixMapping.keySet()) {
            xPathCompiler.declareNamespace((String) key, (String) prefixMapping.get(key));
        }
        
		try {
			XPathExecutable compiledExpression = xPathCompiler.compile(xpathString);
			XPathExpression exp = compiledExpression.getUnderlyingExpression();
	        return exp;
		} catch (SaxonApiException e) {
			throw new XPathException(e.getMessage(), e);
		}
    }

	private void registerExtensionFunctions(Processor processor) {
//		for (function : functions){
//			processor.registerExtensionFunction(function);
//		}
	}

}
