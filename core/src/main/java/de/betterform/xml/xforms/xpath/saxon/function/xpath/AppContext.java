/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.connector.http.AbstractHTTPConnector;
import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.model.submission.RequestHeader;
import de.betterform.xml.xforms.model.submission.RequestHeaders;
import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.dom.NodeWrapper;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.ListIterator;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.sxpath.IndependentContext;
import net.sf.saxon.trans.XPathException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * @author <a href="mailto:tobias.krebs@betterform.de">tobi</a>
 * @version $Id: AppContext 21.10.2010 tobi $
 */
public class AppContext extends XFormsFunction {
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
    public SequenceIterator iterate(XPathContext xpathContext) throws XPathException {
        if (argument.length < 1 || argument.length > 2) {
            throw new XPathException("There must be 1 argument (key)  or 2 arguments (key, defaultObject) for this function");
        }

        final Expression keyExpression = argument[0];
        final String key = keyExpression.evaluateAsString(xpathContext).toString();

        final String defaultObject;
        if (argument.length == 2) {
            final Expression defaultObjectExpression = argument[1];
            defaultObject = defaultObjectExpression.evaluateAsString(xpathContext).toString();
        } else {
            defaultObject = null;
        }

        return appContext(xpathContext, key, defaultObject);
    }

    private SequenceIterator appContext(XPathContext xpathContext, String key, String defaultObject) {
        final Container container = getContainer(xpathContext);
        Map appContext = container.getProcessor().getContext();

        final String[] keys = key.split("/");
        for (int i = 0; i < keys.length - 1; ++i) {
            if (appContext.containsKey(keys[i])) {
                Object o = appContext.get(keys[i]);
                if (keys[i].equals(AbstractHTTPConnector.HTTP_REQUEST_HEADERS)) {
                    RequestHeader rh = ((RequestHeaders) o).getRequestHeader(keys[++i]);
                    if (rh != null) {
                        List list = new ArrayList();
                        list.add(wrap(xpathContext, rh.getValue()));
                        return new ListIterator(list);
                    }
                } else if (o instanceof Map) {
                    appContext = (Map) o;
                } else if (defaultObject != null) {
                    List list = new ArrayList();
                    list.add(wrap(xpathContext, defaultObject));
                    return new ListIterator(list);
                } else {
                    return new ListIterator(Collections.EMPTY_LIST);
                }
            }

        }

        if (appContext.containsKey(keys[keys.length - 1])) {
            List list = new ArrayList();
            list.add(wrap(xpathContext, appContext.get(keys[keys.length - 1])));
            return new ListIterator(list);
        } else if (defaultObject != null) {
            List list = new ArrayList();
            list.add(wrap(xpathContext, defaultObject));
            return new ListIterator(list);
        }

        return new ListIterator(Collections.EMPTY_LIST);
    }

    private NodeWrapper wrap(XPathContext xpathContext, Object o) {
        if (o instanceof Node) {
            return getDocumentElementContext((Document) o);
        } 

        Document ownerDocument = ((Node) ((NodeWrapper) xpathContext.getCurrentIterator().current()).getUnderlyingNode()).getOwnerDocument();
        DocumentWrapper documentWrapper = getDocumentElementContext(ownerDocument);
        return documentWrapper.wrap(ownerDocument.createTextNode(o.toString()));
    }


    private DocumentWrapper getDocumentElementContext(Document document) {
	    return new DocumentWrapper(document, "configuration.xml", new IndependentContext().getConfiguration());
	}
}
