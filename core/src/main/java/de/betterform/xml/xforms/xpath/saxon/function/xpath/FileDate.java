/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * @author <a href="mailto:tobias.krebs@betterform.de">tobi</a>
 * @version $Id: FileDate 21.10.2010 tobi $
 */
public class FileDate extends XFormsFunction {
    private static final Log LOGGER = LogFactory.getLog(FileDate.class);

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
    
    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
	     if (argument.length < 1 || argument.length > 2) {
            throw new XPathException("There must be 1 argument (filename)  or 2 arguments (filename, format) for this function");
        }

        final Expression filenameExpression = argument[0];
        final String filename = filenameExpression.evaluateAsString(xpathContext).toString();

        final String format;
        if (argument.length == 2) {
            final Expression formatExpression = argument[1];
            format = formatExpression.evaluateAsString(xpathContext).toString();
        } else {
            format = null;
        }

        return fileDate(xpathContext, filename, format);
    }
    
    private Item fileDate(XPathContext c, String filename, String format) {
        if (filename == null) {
            return null;
        }
        try {
            return new StringValue(formatDateString(new URI(getContainer(c).getProcessor().getBaseURI()).resolve(filename).toURL().openConnection().getLastModified(), format));
        } catch (Exception e) {
            LOGGER.error("Unable to retrieve change data.", e);
            return null;
        }
    }
    
    private String formatDateString(long modified, String format) {
        Calendar calendar = new GregorianCalendar(Locale.getDefault());
        calendar.setTimeInMillis(modified);
        SimpleDateFormat simple = null;
        String result;
        if (format == null || format.equals("")) {
            //default format
            simple = new SimpleDateFormat("dd.MM.yyyy H:m:s");
        } else {
            //custom format
            try {
                simple = new SimpleDateFormat(format);
            }
            catch (IllegalArgumentException e) {
//                result = "Error: illegal Date format string";
                //todo: do something better
            }
        }
        result = simple.format(calendar.getTime());
        return result;
    }
}