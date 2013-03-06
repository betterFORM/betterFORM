/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xpath.impl.saxon.sxpath;
import net.sf.saxon.AugmentedSource;
import net.sf.saxon.Configuration;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionTool;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.instruct.Executable;
import net.sf.saxon.instruct.SlotManager;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.om.NamespaceResolver;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.sxpath.IndependentContext;
import net.sf.saxon.sxpath.XPathStaticContext;
import net.sf.saxon.sxpath.XPathVariable;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.Whitespace;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

/**
 * This class provide a native Saxon API for free-standing evaluation of XPath expressions. Unlike the
 * JAXP API offered by {@link net.sf.saxon.xpath.XPathEvaluator} it exposes Saxon classes and interfaces
 * and thus provides a more strongly-typed interface with greater control over the detailed behaviour.
 *
  * @author Michael H. Kay
  */

public class XPathEvaluator {

    private XPathStaticContext staticContext;
    private boolean stripSpace = false;

    /**
    * Default constructor. Creates an XPathEvaluator with a default configuration and name pool.
     * Note that any source documents used by an XPath expression under this XPathEvaluator must
     * be built using the {@link Configuration} that is implicitly created by this constructor,
     * which is accessible using the {@link #getConfiguration} method.
    */

    public XPathEvaluator() {
        this(new Configuration());
    }

    /**
     * Construct an XPathEvaluator with a specified configuration.
     * @param config the configuration to be used. If the XPathEvaluator is
     * to be used to run schema-aware XPath expressions this must be an instance
     * of {@link com.saxonica.validate.SchemaAwareConfiguration}
     */
    public XPathEvaluator(Configuration config) {
        staticContext = new IndependentContext(config);
    }

    /**
     * Get the Configuration in use.
     * @return the Saxon configuration
     */

    public Configuration getConfiguration() {
        return staticContext.getConfiguration();
    }

    /**
    * Indicate whether all whitespace text nodes in source documents are to be
    * removed. This affects the action of the {@link #build} method, and of all
     * other methods that take a Source as input.
    * @param strip True if all whitespace text nodes are to be stripped from the source document,
    * false otherwise. The default if the method is not called is false.
     * @deprecated since 8.9. The preferred way to build a source document is to use
     * {@link Configuration#buildDocument}
    */

    public void setStripSpace(boolean strip) {
        stripSpace = strip;
    }

    /**
    * Build a source document.
     * <p><i>This method is retained for backwards compability. The preferred way to build a document
     * tree is to call the method {@link Configuration#buildDocument}</i></p>
     * @param source a JAXP Source object. This may be any implementation of Source that Saxon recognizes:
     * not only the standard kinds of source such as StreamSource, SAXSource, and DOMSource, but also for
     * example a JDOM or XOM DocumentWrapper. For the way in which the source document is built, see
     *      {@link net.sf.saxon.Configuration#buildDocument}
     * @return the NodeInfo representing the root of the constructed tree.
     * @throws XPathException if, for example, XML parsing fails.
     * @deprecated since 8.9. The preferred way to build a source document is to use
     * {@link Configuration#buildDocument}
    */

    public NodeInfo build(Source source) throws XPathException {
        if (stripSpace) {
            AugmentedSource as = AugmentedSource.makeAugmentedSource(source);
            as.setStripSpace(Whitespace.ALL);
            source = as;
        } else if (source instanceof NodeInfo) {
            return (NodeInfo)source;
        }
        return getConfiguration().buildDocument(source);
    }

    /**
     * Declare a variable, making it available for use within a subsequently-compiled XPath expression.
     * The variable may subsequently be assigned a value using the method
     * {@link XPathDynamicContext#setVariable(XPathVariable, net.sf.saxon.om.ValueRepresentation)}.
     * Any external variable used in an XPath expression must be declared before the XPath expression
     * is compiled.
     * @param uri The namespace URI of the variable name. Use "" for the null namespace.
     * @param localName The local part of the variable name.
     * @return an object representing the variable
     */

    public XPathVariable declareVariable(String uri, String localName) {
        return staticContext.declareVariable(uri, localName);
    }

    /**
    * Set the static context for compiling XPath expressions. This provides more detailed control over the
    * environment in which the expression is compiled, for example it allows namespace prefixes to
    * be declared, variables to be bound and functions to be defined. For most purposes, the static
    * context can be defined by providing and tailoring an instance of the {@link IndependentContext} class.
    * Until this method is called, a default static context is used, in which no namespaces are defined
    * other than the standard ones (xml, xslt, and saxon), and no variables or functions (other than the
    * core XPath functions) are available.
     * @param context the XPath static context
     * <p>Setting a new static context clears any variables and namespaces that have previously been declared.</p>
    */

    public void setStaticContext(XPathStaticContext context) {
        staticContext = context;
    }

    /**
     * Get the current static context. This will always return a value; if no static context has been
     * supplied by the user, the system will have created its own. A system-created static context
     * will always be an instance of {@link IndependentContext}
     * @return the static context object
    */

    public XPathStaticContext getStaticContext() {
        return staticContext;
    }

    /**
     * Get the executable
     * @return the executable. This holds details of function bindings and collations.
     */

    public Executable getExecutable() {
        return staticContext.getExecutable();
    }
   

    /**
    * Prepare (compile) an XPath expression for subsequent evaluation.
    * @param expression The XPath expression to be compiled, supplied as a string.
    * @return an XPathExpression object representing the prepared expression
    * @throws XPathException if the syntax of the expression is wrong, or if it references namespaces,
    * variables, or functions that have not been declared.
    */

    public XPathExpression createExpression(String expression) throws XPathException {
        Expression exp = ExpressionTool.make(expression, staticContext, 0, -1, 1, false);
        exp.setContainer(staticContext);
        ExpressionVisitor visitor = ExpressionVisitor.make(staticContext);
        visitor.setExecutable(getExecutable());
        exp = visitor.typeCheck(exp, Type.ITEM_TYPE);
        SlotManager map = staticContext.getStackFrameMap();
        int numberOfExternalVariables = map.getNumberOfVariables();
        ExpressionTool.allocateSlots(exp, numberOfExternalVariables, map);
        XPathExpression xpe = new XPathExpression(this, exp);
        xpe.setStackFrameMap(map, numberOfExternalVariables);
        return xpe;
    }

    /**
     * Set the external namespace resolver to be used. The NamespaceResolver is stored
     * as part of the static context. It overrides any namespaces declared directly
     * using declareNamespace on the staticContext object
     * @param namespaceResolver The namespace resolver, which maintains a mapping of prefixes to URIs.
     * Any namespace prefix used in the XPath expression is resolved using this namespaceResolver.
     */

    public void setNamespaceResolver(NamespaceResolver namespaceResolver) {
        staticContext.setNamespaceResolver(namespaceResolver);
    }

    /**
     * Get the external namespace resolver, if one has been set using {@link #setNamespaceResolver}
     * @return the namespace resolver supplied by the user if set, or a system-defined namespace resolver
     * otherwise. By default, the {@link IndependentContext} object used as the {@link XPathStaticContext}
     * also acts as the {@link NamespaceResolver}.
     */

    public NamespaceResolver getNamespaceResolver() {
        return staticContext.getNamespaceResolver();
    }

    /**
     * Set the default namespace for elements and types
     * @param uri The namespace to be used to qualify unprefixed element names and type names appearing
     * in the XPath expression.
     */

    public void setDefaultElementNamespace(String uri) {
        staticContext.setDefaultElementNamespace(uri);
    }

    /**
     * For testing only
     */

    public static void main(String[] args) throws Exception {
        XPathEvaluator xpe = new XPathEvaluator();
//        XPathVariable in = xpe.declareVariable("", "in");
        XPathExpression exp = xpe.createExpression(
                "for $v in distinct-values(tokenize($in, '/')) return concat(' +', $v)");
        NodeInfo doc = xpe.getConfiguration().buildDocument(new StreamSource(new File(args[0])));
        XPathDynamicContext context = exp.createDynamicContext(doc);
        //context.setVariable(in, new StringValue(args[1]));
        SequenceIterator results = exp.iterate(context);
        while (true) {
            Item item = results.next();
            if (item == null) break;
            System.err.println(item);
        }
//        if (args.length != 2) {
//            System.err.println("format: java XPathEvaluator source.xml \"expression\"");
//            return;
//        }
//        XPathEvaluator xpe = new XPathEvaluator();
//        XPathExpression exp = xpe.createExpression(args[1]);
//        List results = exp.evaluate(new StreamSource(new File(args[0])));
//        for (int i = 0; i < results.size(); i++) {
//            Object o = results.get(i);
//            System.err.println(o);
//        }
    }

}

//
// The contents of this file are subject to the Mozilla Public License Version 1.0 (the "License");
// you may not use this file except in compliance with the License. You may obtain a copy of the
// License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS IS" basis,
// WITHOUT WARRANTY OF ANY KIND, either express or implied.
// See the License for the specific language governing rights and limitations under the License.
//
// The Original Code is: all this file.
//
// The Initial Developer of the Original Code is Michael H. Kay
//
// Contributor(s):
//
