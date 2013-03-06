/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xpath.impl.saxon;

import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.xpath.saxon.function.BetterFormFunctionLibrary;
import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunctionLibrary;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;

import de.betterform.xml.xpath.impl.saxon.sxpath.XPathEvaluator;
import de.betterform.xml.xpath.impl.saxon.sxpath.XPathExpression;
import net.sf.saxon.Configuration;
import net.sf.saxon.functions.ConstructorFunctionLibrary;
import net.sf.saxon.functions.FunctionLibraryList;
import net.sf.saxon.functions.SystemFunctionLibrary;
import net.sf.saxon.s9api.*;
import net.sf.saxon.sxpath.IndependentContext;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.xpath.XPathFunctionLibrary;
import org.w3c.dom.Node;

import java.util.*;

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
            return (Node) node.getUnderlyingNode();
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

        Processor processor = new Processor(false);
        XPathCompiler xPathCompiler = processor.newXPathCompiler();

        for( Object key : prefixMapping.keySet()) {
            xPathCompiler.declareNamespace((String) key, (String) prefixMapping.get(key));
        }

        DocumentBuilder builder = processor.newDocumentBuilder();
        XdmItem xdmItem =  builder.wrap(nodeset);
        try {
            XPathSelector xpathSelector = xPathCompiler.compile(xpathString).load();
            xpathSelector.setContextItem(xdmItem);

            for (XdmItem item: xpathSelector) {
                result.add(item);
            }
        } catch (SaxonApiException sae) {
            throw new XFormsException(sae.getMessage(), sae);
        }

        return result;
    }

    /**
     * @param xpathString
     * @param prefixMapping
     * @return
     * @throws XPathException
     */
    public XPathExpression getXPathExpression(String xpathString, Map prefixMapping, Configuration configuration) throws XPathException {
        XPathEvaluator xpe = new XPathEvaluator(configuration);

        IndependentContext independentContext = (IndependentContext) xpe.getStaticContext();
        independentContext.setDefaultFunctionNamespace(NamespaceConstants.XFORMS_NS);
        independentContext.setBackwardsCompatibilityMode(true);

        // XXX set base URI

        for (Iterator it = prefixMapping.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            independentContext.declareNamespace((String) entry.getKey(), (String) entry.getValue());
        }
        //independentContext.declareNamespace("bffn","java:de.betterform.xml.xforms.xpath.BetterFormXPathFunctions");
        // XXX declare variable

        independentContext.setFunctionLibrary(fgXFormsFunctionLibrary);


        XPathExpression exp = xpe.createExpression(xpathString);
        return exp;
    }

    /**
     * @param prefixMapping
     * @return
     */
    private IndependentContext createIndependentContext(Map prefixMapping) {
        final IndependentContext independentContext = new IndependentContext();
        independentContext.setDefaultFunctionNamespace(NamespaceConstants.XFORMS_NS);
        independentContext.setBackwardsCompatibilityMode(true);

        // XXX set base URI

        for (Iterator it = prefixMapping.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            independentContext.declareNamespace((String) entry.getKey(), (String) entry.getValue());
        }

        // XXX declare variable

        independentContext.setFunctionLibrary(fgXFormsFunctionLibrary);
        return independentContext;
    }
}
