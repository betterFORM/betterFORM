/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import de.betterform.xml.dom.DOMUtil;
import net.sf.saxon.dom.NodeWrapper;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

/**
 * Implementation of the 7.10.2 The current() Function <p/> Returns the context
 * node used to initialize the evaluation of the containing XPath expression.
 *
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class LocationPath extends XFormsFunction {
    /**
     *
     */
    private static final long serialVersionUID = -4093226587796615212L;
    private static Log LOGGER = LogFactory.getLog(LocationPath.class);


    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
        Item item = argument[0].evaluateItem(xpathContext);

        if (item != null) {
            Node node = (Node) ((NodeWrapper) item).getUnderlyingNode();
            String result = DOMUtil.getCanonicalPath(node);
            return new StringValue(result);
        }else{
            LOGGER.warn("Item for " + xpathContext.toString() + " couldn't be found");
            return StringValue.EMPTY_STRING;
        }
    }
}
