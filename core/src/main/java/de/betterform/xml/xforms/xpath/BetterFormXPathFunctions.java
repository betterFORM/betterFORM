/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath;

import net.sf.saxon.dom.NodeWrapper;
import net.sf.saxon.expr.XPathContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.connector.http.AbstractHTTPConnector;
import de.betterform.xml.config.Config;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.model.submission.RequestHeader;
import de.betterform.xml.xforms.model.submission.RequestHeaders;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Provides betterform extension functions.
 *
 * @author Joern Turner
 */
public class BetterFormXPathFunctions {
    private static final Log LOGGER = LogFactory.getLog(BetterFormXPathFunctions.class);

    /*
        TOBI: Converted appContext() to XPathFunction 
     */
    /*
    public static Node appContext(XPathContext c, String key) {
        Container container = getContainer(c);

        Map appContext = container.getProcessor().getContext();

        String[] keys = key.split("\\/");
        for (int i = 0; i < keys.length - 1; ++i) {
            if (appContext.containsKey(keys[i])) {
                Object o = appContext.get(keys[i]);
                if (keys[i].equals(AbstractHTTPConnector.HTTP_REQUEST_HEADERS)) {
                    RequestHeader rh = ((RequestHeaders) o).getRequestHeader(keys[++i]);
                    if (rh != null) {
                        return wrap(c, rh.getValue());
                    }

                } else if (o instanceof Map) {
                    appContext = (Map) o;
                }else{
                    return null;
                }
            }

        }
        if (appContext.containsKey(keys[keys.length - 1])) {
            return wrap(c, appContext.get(keys[keys.length - 1]));
        }
        return null;
    }

    public static Node appContext(XPathContext c, String key, String defaultObject) {
        Node n = BetterFormXPathFunctions.appContext(c, key);
        if (n == null) {
            return wrap(c, defaultObject);
        } else {
            return n;
        }
    }
    */

     /*
        TOBI: Converted config() to XPathFunction
     */
    /*
    public static String config(String key) {
        try {
            return Config.getInstance().getProperty(key);
        } catch (XFormsConfigException e) {
            return "";
        }
    }
    */
    private static Map m_regexPatterns = new HashMap();

    /**
     * <code>Regexp</code> is a utility class providing the functionality
     * present within the EXSLT Regular Expressions definition (<a
     * href="http://www.exslt.org/regexp"
     * target="_top">http://www.exslt.org/regexp</a>). <br>
     * <br>
     * This is a contribution from Terence Jacyno. <p/> Todo: Move to
     * 'ExsltRegExpExtensionFunctions, rename to 'test', add 'replace' and
     * 'match'
     */
    public static boolean match(String input, String regex) {
        return match(input, regex, null);
    }

    /**
     * <code>Regexp</code> is a utility class providing the functionality
     * present within the EXSLT Regular Expressions definition (<a
     * href="http://www.exslt.org/regexp"
     * target="_top">http://www.exslt.org/regexp</a>). <br>
     * <br>
     * This is a contribution from Terence Jacyno. <p/> Todo: Move to
     * 'ExsltRegExpExtensionFunctions, rename to 'test', add 'replace' and
     * 'match'
     */
    public static boolean match(String input, String regex, String flags) {

        String regexKey = ((flags == null) || (flags.indexOf('i') == -1)) ? "s " + regex : "i " + regex;

        Pattern pattern = (Pattern) m_regexPatterns.get(regexKey);

        if (pattern == null) {
            pattern = (regexKey.charAt(0) == 'i') ? Pattern.compile(regex, Pattern.CASE_INSENSITIVE) : Pattern.compile(regex);
            m_regexPatterns.put(regexKey, pattern);
        }

        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    /**
     * custom extension function to get the size of a local file.
     *
     * @param c        the XPath resolution context
     * @param filename filename or path as value. The value will be resolved
     *                 against the baseURI of the processor to find the file.
     * @return the size of the file as String <p/> todo: revisit code structure -
     *         fileSize and fileDate functions only differ in one line of code
     */
    /*
    public static float fileSize(XPathContext c, String filename) {
        if (filename == null) {
            return Float.NaN;
        }
        try {
            return new URI(getContainer(c).getProcessor().getBaseURI()).resolve(filename).toURL().openConnection().getContentLength();
        } catch (Exception e) {
            LOGGER.error("Unable to retrieve file size", e);
            return Float.NaN;
        }
    }
     */
    /**
     * custom extension function to get the lastModified Date of a local file.
     *
     * @param c        the XPath resolution context
     * @param filename a filename or path as value. The value will be resolved
     *                 against the baseURI of the processor to find the file.
     * @return the formatted lastModified Date of the file
     * @see java.text.SimpleDateFormat
     */
    /*
    public static String fileDate(XPathContext c, String filename) {
        return fileDate(c, filename, null);
    }
    */


    /**
     * custom extension function to get the lastModified Date of a local file.
     *
     * @param c        the XPath resolution context
     * @param filename a filename or path as value. The value will be resolved
     *                 against the baseURI of the processor to find the file.
     * @param format   a format pattern conformant with to
     *                 java.text.SimpleDateFormat. If an empty string is passed
     *                 the format defaults to "dd.MM.yyyy H:m:s".
     * @return the formatted lastModified Date of the file
     * @see java.text.SimpleDateFormat
     */
    /*
    public static String fileDate(XPathContext c, String filename, String format) {
        if (filename == null) {
            return null;
        }
        try {
            return formatDateString(new URI(getContainer(c).getProcessor().getBaseURI()).resolve(filename).toURL().openConnection().getLastModified(), format);
        } catch (Exception e) {
            LOGGER.error("Unable to retrieve change data.", e);
            return null;
        }
    }
    */

    /**
     * custom extension function to get the content length of an uploaded file
     *
     * @param c the XPath resolution context
     * @return the length of the files content in bytes or -1 if we cannot get the content length
     */
    // TODO implement betterform extension uploadContentLength
//   public static long uploadContentLength(ExpressionContext expressionContext, List nodeset) throws XFormsException {
//       if ((nodeset != null) && (nodeset.size() == 1)) {
//           JXPathContext rootContext = expressionContext.getJXPathContext();
//           String xpath = rootContext.getContextPointer().asPath();
//           String uploadDataType = null;
//
//           //get the data type of the upload
//           while (rootContext != null) {
//               Object rootNode = rootContext.getContextBean();
//
//               if (rootNode instanceof Instance) {
//                   Instance instance = (Instance) rootNode;
//                   ModelItem uploadItem = instance.getModelItem(xpath);
//                   DeclarationView uploadItemDeclaration = uploadItem.getDeclarationView();
//
//                   uploadDataType = uploadItemDeclaration.getDatatype();
//               }
//               rootContext = rootContext.getParentContext();
//           }
//
//           //get the encoded content of the upload
//           String encodedUploadContent = (String) nodeset.get(0);
//
//           //decode and calculate the length of the uploaded content
//           if (encodedUploadContent != null && uploadDataType != null) {
//               byte[] decodedUploadContent = null;
//
//               if (uploadDataType.equals("base64Binary")) {
//                   //decode base64Binary
//                   decodedUploadContent = Base64.decodeBase64(encodedUploadContent.getBytes());
//               } else if (uploadDataType.equals("hexBinary")) {
//                   //decode hexBinary
//                   try {
//                       decodedUploadContent = Hex.decodeHex(encodedUploadContent.toCharArray());
//                   }
//                   catch (DecoderException e) {
//                       return -1;
//                   }
//               } else if (uploadDataType.equals("anyURI")) {
//                   //no decoding
//                   decodedUploadContent = encodedUploadContent.getBytes();
//               }
//
//               //get the length of the decoded content
//               if (decodedUploadContent != null) {
//                   return decodedUploadContent.length;
//               }
//           }
//       }
    /*
    private static Node wrap(XPathContext c, Object o) {
        if (o instanceof Node) {
            return (Node) o;
        }

        Document ownerDocument = ((Node) ((NodeWrapper) c.getCurrentIterator().current()).getUnderlyingNode()).getOwnerDocument();

        return ownerDocument.createTextNode(o.toString());

    }
    */
    /*
    private static String formatDateString(long modified, String format) {
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
    */
    /*
    public static Container getContainer(XPathContext c) {
        Node n = (Node) ((NodeWrapper) c.getContextItem()).getUnderlyingNode();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("context node: " + n.getNodeName());
        }

        Element root = n.getOwnerDocument().getDocumentElement();
        Object container = ((Element) root).getUserData("container");
        if (container instanceof Container) {
            return (Container) container;
        } else {
            return null;
        }
    }
    */
}
