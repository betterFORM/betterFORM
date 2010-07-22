/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.functions.SystemFunction;
import de.betterform.xml.xforms.XFormsProcessorImpl;

/**
 * This class is the base class for all the XForms functions. "XForms functions"
 * here means the XForms 1.0 functions.
 */

public abstract class XFormsFunction extends SystemFunction {

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
}
