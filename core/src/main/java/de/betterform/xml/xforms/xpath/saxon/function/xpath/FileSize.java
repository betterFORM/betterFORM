/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.FloatValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;

/**
 * @author <a href="mailto:tobias.krebs@betterform.de">tobi</a>
 * @version $Id: FileSize 21.10.2010 tobi $
 */
public class FileSize extends XFormsFunction {
    private static final Log LOGGER = LogFactory.getLog(FileSize.class);

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

    @Override
    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
	    if (argument.length != 1) {
            throw new XPathException("There must be 1 argument (filename) for this function");
        }

        final Expression keyExpression = argument[0];
        final String filename = keyExpression.evaluateAsString(xpathContext).toString();

        return fileSize(xpathContext, filename);
    }

    public Sequence call(final XPathContext context,
                         final Sequence[] arguments) throws XPathException {
        final String filename = arguments[0].head().getStringValue();
        return fileSize(context, filename);
    }

    private FloatValue fileSize(final XPathContext context, final String filename) {
        if (filename == null) {
            return new FloatValue(Float.NaN);
        }
        try {
            final Container container = getContainer(context);
            return new FloatValue(new URI(container.getProcessor().getBaseURI()).resolve(filename).toURL().openConnection().getContentLength());
        } catch (Exception e) {
            LOGGER.error("Unable to retrieve file size", e);
            return new FloatValue(Float.NaN);
        }
    }
}
