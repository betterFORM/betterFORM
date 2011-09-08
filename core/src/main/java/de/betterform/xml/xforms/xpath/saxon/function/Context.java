/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import de.betterform.xml.dom.DOMUtil;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;

import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.action.AbstractAction;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.AbstractUIElement;
import de.betterform.xml.xforms.ui.BindingElement;
import de.betterform.xml.xforms.ui.RepeatItem;

import java.util.List;

/**
 * Implementation of the 7.10.2 The context() Function <p/> Returns the context
 * node used to initialize the evaluation of the containing XPath expression.
 *
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class Context extends XFormsFunction {
    /**
     *
     */
    private static final long serialVersionUID = -4093826587796615212L;

    /**
     * Get the static properties of this expression (other than its type). The result is
     * bit-signficant. These properties are used for optimizations. In general, if
     * property bit is set, it is true, but if it is unset, the value is unknown.
     */

    public int computeSpecialProperties() {
        return StaticProperty.CONTEXT_DOCUMENT_NODESET |
                StaticProperty.SINGLE_DOCUMENT_NODESET |
                StaticProperty.ORDERED_NODESET |
                StaticProperty.NON_CREATIVE;
    }

    /**
     * Pre-evaluate a function at compile time. Functions that do not allow
     * pre-evaluation, or that need access to context information, can override this method.
     *
     * @param visitor an expression visitor
     * @return the result of the early evaluation, or the original expression, or potentially
     *         a simplified expression
     */

    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
        return this;
    }

    /**
     * Evaluate in a general context
     */
    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
        XPathFunctionContext functionContext = getFunctionContext(xpathContext);
        XFormsElement xformsElement = functionContext.getXFormsElement();

        DOMUtil.prettyPrintDOM(xformsElement.getElement());

        int pos = 1;
        if (xformsElement instanceof BindingElement) {
            BindingElement bindingElement = ((BindingElement) xformsElement);
            pos = bindingElement.getPosition();
            return (Item) bindingElement.getNodeset().get(pos-1);
        }


        String id = null;
        if (xformsElement instanceof AbstractAction && ((AbstractAction)xformsElement).isRepeated()) {
            id = ((AbstractAction)xformsElement).getRepeatItemId();
        }
        else if(xformsElement instanceof AbstractUIElement && ((AbstractUIElement)xformsElement).isRepeated()) {
            id = ((AbstractUIElement)xformsElement).getRepeatItemId();
        }

        if(id != null && !(id.equals(""))){
/*
            RepeatItem item = (RepeatItem) xformsElement.getContainerObject().lookup(id);
            pos = item.getPosition();
*/

            XFormsElement element = xformsElement.getContainerObject().lookup(id);
            if (element != null) {
                if (element instanceof RepeatItem) {
                    RepeatItem item = (RepeatItem) element;
                    pos = item.getPosition();
                }
            }

        }

        try {
            List items =  xformsElement.evalInScopeContext();
            if(items.size() == 1){
                return (Item) xformsElement.evalInScopeContext().get(0);
            }else{
                return (Item) xformsElement.evalInScopeContext().get(pos-1);
            }
        } catch (XFormsException e) {
            throw new XPathException(e);
        }
    }

    /**
     * Determine the dependencies
     */

    public int getIntrinsicDependencies() {
        return StaticProperty.DEPENDS_ON_CURRENT_ITEM;
    }
}
