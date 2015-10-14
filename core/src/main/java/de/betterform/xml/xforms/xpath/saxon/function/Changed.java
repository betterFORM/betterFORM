/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import net.sf.saxon.dom.DOMNodeWrapper;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Changed extends XFormsFunction {
    private static final long serialVersionUID = -8152654592543207734L;
    private static Log LOGGER = LogFactory.getLog(Changed.class);

    /**
     * Evaluate in a general context
     */
    @Override
    public Item evaluateItem(final XPathContext xpathContext) throws XPathException {
        final Item item = argument[0].evaluateItem(xpathContext);
        return changed(xpathContext, item);
    }

    public Sequence call(final XPathContext context,
                         final Sequence[] arguments) throws XPathException {
        return changed(context, arguments[0].head());
    }

    private BooleanValue changed(final XPathContext context, final Item item) throws XPathException {
        if (item != null) {
            Node node = (Node) ((DOMNodeWrapper) item).getUnderlyingNode();
            String xpath = DOMUtil.getCanonicalPath(node);
//            String currentValue = (String) argument[0].evaluateAsString(xpathContext);
            String currentValue = DOMUtil.getTextNodeAsString(node);

            de.betterform.xml.xforms.model.Instance owner = (de.betterform.xml.xforms.model.Instance) node.getOwnerDocument().getDocumentElement().getUserData("instance");
            String initialValue=null;
            try {
                Document initial = owner.getInitialInstance();
                if(LOGGER.isDebugEnabled()){
                    LOGGER.debug("Initial Instance: START:");
                    DOMUtil.prettyPrintDOM(initial);
                    LOGGER.debug("\nInitial Instance: STOP");
                }

                initialValue = XPathUtil.evaluateAsString(initial,xpath) ;
            } catch (XFormsException e) {
                throw new XPathException("initial Instance couldn't be fetched",e);
            }

            if(currentValue.equals(initialValue)){
                return BooleanValue.FALSE;
            }else{
                return BooleanValue.TRUE;
            }
        }else{
            LOGGER.warn("Item for " + context.toString() + " couldn't be found");
            return BooleanValue.FALSE; 
        }
    }
}
