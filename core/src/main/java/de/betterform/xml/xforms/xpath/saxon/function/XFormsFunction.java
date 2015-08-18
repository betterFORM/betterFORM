/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import net.sf.saxon.dom.DOMNodeWrapper;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.functions.SystemFunction;
import net.sf.saxon.om.Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class is the base class for all the XForms functions. "XForms functions"
 * here means the XForms 1.0 functions.
 */

public abstract class XFormsFunction extends SystemFunction {
   private static final Log LOGGER = LogFactory.getLog(XFormsFunction.class);
    /**
     * @param xpathContext
     * @return
     */
    protected XPathFunctionContext getFunctionContext(XPathContext xpathContext) {
        XPathFunctionContext functionContext = (XPathFunctionContext) xpathContext.getController().getUserData(XFormsProcessorImpl.class.toString(),
                XPathFunctionContext.class.toString());
        return functionContext;
    }

//    /**
//     * Determine which aspects of the context the expression depends on. The result is
//     * a bitwise-or'ed value composed from constants such as XPathContext.VARIABLES and
//     * XPathContext.CURRENT_NODE
//     */
//
//     public int getIntrinsicDependencies() {
//         //int depend = StaticProperty.HAS_SIDE_EFFECTS;
//         return ( StaticProperty.DEPENDS_ON_CONTEXT_ITEM |
//                 StaticProperty.DEPENDS_ON_POSITION |
//                 StaticProperty.DEPENDS_ON_LAST );
//     }

    protected Container getContainer(XPathContext xpathContext) {
        Item item = xpathContext.getContextItem();
        Node n = (Node) ((DOMNodeWrapper) item).getUnderlyingNode();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("context node: " + n.getNodeName());
        }

        Element root = n.getOwnerDocument().getDocumentElement();
        Object container = root.getUserData("container");
        if (container instanceof Container) {
            return (Container) container;
        } else {
            return null;
        }
    }
}
