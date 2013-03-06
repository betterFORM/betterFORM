/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


/**
 *
 */
package de.betterform.xml.xforms;

import de.betterform.xml.config.Config;
import de.betterform.xml.dom.DOMComparator;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import junit.framework.TestCase;
import net.sf.saxon.s9api.XdmItem;
import net.sf.saxon.s9api.XdmNode;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.NumericValue;
import net.sf.saxon.value.StringValue;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nick Van den Bleeken
 * @author Joern Turner
 * @version $Id$
 */
public abstract class BetterFormTestCase extends TestCase {
    protected static final Map kPREFIX_MAPPING;

    static {
        kPREFIX_MAPPING = new HashMap();
        kPREFIX_MAPPING.put(NamespaceConstants.BETTERFORM_PREFIX, NamespaceConstants.BETTERFORM_NS);
        kPREFIX_MAPPING.put(NamespaceConstants.XFORMS_PREFIX, NamespaceConstants.XFORMS_NS);
    }


    protected XFormsProcessor processor;
    protected XdmNode defaultContext;
    protected XPathFunctionContext defaultFunctionContext;
    protected List documentContext;

    protected String relativePath = "src/test/resources/XFormsTestSuite1.1E1/Edition1/";

    public BetterFormTestCase() {
        super();
    }

    public BetterFormTestCase(String s) {
        super(s);
    }


//    protected abstract InputStream getTestCaseDocumentAsStream();
    protected abstract String getTestCaseURI();

    protected abstract XPathFunctionContext getDefaultFunctionContext();

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        Config.getInstance(Config.class.getResource("default.xml").getPath());
        this.processor = new XFormsProcessorImpl();

        File f;
        String testCaseURI = getTestCaseURI();
        if(testCaseURI.startsWith("conform:")){
            testCaseURI = testCaseURI.substring(8);
            String relative2file = new File(relativePath, testCaseURI).toString();
            //todo: set a default config
            f = new File(relative2file);
        }else{
            f = new File(getClass().getResource(testCaseURI).getPath());
        }
        String s = f.getCanonicalPath();
        this.processor.setXForms(getXmlResource(f.toString()));
        processor.setBaseURI(f.getParentFile().toURI().toString());

        preInit();
        this.processor.init();

        this.defaultContext = getDefaultContext();
        this.defaultFunctionContext = getDefaultFunctionContext();
        this.documentContext = de.betterform.xml.xpath.impl.saxon.XPathUtil.getRootContext((Document) this.processor.getXForms(), this.processor.getBaseURI());
    }

    protected void reInitializeDefaultContext() throws XFormsException {
        this.defaultContext = getDefaultContext();
    }

    /**
     *
     */
    protected void preInit() {

    }

    protected XdmNode getDefaultContext() throws XFormsException {
        Instance defaultInstance = getDefaultModel().getDefaultInstance();
        if(defaultInstance != null){
            return (XdmNode) getDefaultModel().getDefaultInstance().getInstanceNodeset().get(0);
        }
        return null;
    }


    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during teardown.
     */
    protected void tearDown() throws Exception {
        this.processor.shutdown();
        this.processor = null;

        this.defaultContext = null;
    }

    public Model getDefaultModel() throws XFormsException {
        return (Model) this.processor.getXFormsModel(null);
    }


    protected List evaluateInDefaultContext(final String xpathExpression) throws XFormsException {
        return XPathCache.getInstance().evaluate(defaultContext, xpathExpression, kPREFIX_MAPPING, defaultFunctionContext);
    }


    protected boolean evaluateInDefaultContextAsBoolean(final String xpathExpression) throws XFormsException {
        List result = XPathCache.getInstance().evaluate(defaultContext, xpathExpression, kPREFIX_MAPPING, defaultFunctionContext);

        if ((result.size() != 1) || !(result.get(0) instanceof BooleanValue)) {
            throw new XFormsException("Could not convert resultset to boolean");
        }

        return ((BooleanValue) result.get(0)).effectiveBooleanValue();
    }

    protected String evaluateInDefaultContextAsString(final String xpathExpression) throws XFormsException {
        List result = XPathCache.getInstance().evaluate(defaultContext, xpathExpression, kPREFIX_MAPPING, defaultFunctionContext);

        if (result.size() == 0) {
            return null;
        }

        if (result.size() != 1) {
            throw new XFormsException("Could not convert resultset to string");
        }

        return ((XdmItem) result.get(0)).getStringValue();
    }

    protected double evaluateInDefaultContextAsDouble(final String xpathExpression) throws XFormsException {
        List result = XPathCache.getInstance().evaluate(defaultContext, xpathExpression, kPREFIX_MAPPING, defaultFunctionContext);

        if ((result.size() != 1) || !(result.get(0) instanceof NumericValue)) {
            throw new XFormsException("Could not convert resultset to double");
        }

        return ((NumericValue) result.get(0)).getDoubleValue();
    }

    protected Node evaluateInDefaultContextAsNode(final String xpathExpression) throws XFormsException {
        List result = XPathCache.getInstance().evaluate(defaultContext, xpathExpression, kPREFIX_MAPPING, defaultFunctionContext);

        if ((result.size() != 1) || !(result.get(0) instanceof XdmNode)) {
            throw new XFormsException("Could not convert resultset to XdmNode");
        }

        return (Node) ((XdmNode) result.get(0)).getUnderlyingNode();
    }


    protected String evaluateInDocumentContextAsString(final String xpathExpression) throws XFormsException {
        List result = XPathCache.getInstance().evaluate(documentContext, 1, xpathExpression, kPREFIX_MAPPING, defaultFunctionContext);

        if (result.size() == 0) {
            return null;
        }

        if ((result.size() != 1) || !(result.get(0) instanceof StringValue)) {
            throw new XFormsException("Could not convert resultset to string");
        }

        return ((StringValue) result.get(0)).getStringValue();
    }

    protected Double evaluateInDocumentContextAsDouble(final String xpathExpression) throws XFormsException {
        List result = XPathCache.getInstance().evaluate(documentContext, 1, xpathExpression, kPREFIX_MAPPING, defaultFunctionContext);

        if ((result.size() != 1) || !(result.get(0) instanceof NumericValue)) {
            throw new XFormsException("Could not convert resultset to double");
        }

        return ((NumericValue) result.get(0)).getDoubleValue();
    }

    protected Node evaluateInInstanceAsNode(final String instanceID, final String xpathExpression) throws XFormsException {
        List result = XPathCache.getInstance().evaluate(getInstanceNodeInfo(instanceID), xpathExpression, kPREFIX_MAPPING, defaultFunctionContext);

        if ((result.size() != 1) || !(result.get(0) instanceof XdmNode)) {
            throw new XFormsException("Could not convert resultset to XdmNode");
        }

        return (Node) ((XdmNode) result.get(0)).getUnderlyingNode();
    }

    protected String evaluateInInstanceAsString(final String instanceID, final String xpathExpression) throws XFormsException {
        List result = XPathCache.getInstance().evaluate(getInstanceNodeInfo(instanceID), xpathExpression, kPREFIX_MAPPING, defaultFunctionContext);

        if (result.size() == 0) {
            return null;
        }

        if ((result.size() != 1) || !(result.get(0) instanceof StringValue)) {
            throw new XFormsException("Could not convert resultset to string");
        }

        return ((StringValue) result.get(0)).getStringValue();
    }

    protected double evaluateInInstanceAsDouble(final String instanceID, final String xpathExpression) throws XFormsException {
        List result = XPathCache.getInstance().evaluate(getInstanceNodeInfo(instanceID), xpathExpression, kPREFIX_MAPPING, defaultFunctionContext);

        if ((result.size() != 1) || !(result.get(0) instanceof NumericValue)) {
            throw new XFormsException("Could not convert resultset to double");
        }

        return ((NumericValue) result.get(0)).getDoubleValue();
    }

    protected String evaluateInModelContextAsString(final String modelID, final String xpathExpression) throws XFormsException {
        List result = XPathCache.getInstance().evaluate(getDefaultInstanceNodeInfoOfModel(modelID), xpathExpression, kPREFIX_MAPPING, defaultFunctionContext);

        if (result.size() == 0) {
            return null;
        }

        if ((result.size() != 1) || !(result.get(0) instanceof StringValue)) {
            throw new XFormsException("Could not convert resultset to string");
        }

        return ((StringValue) result.get(0)).getStringValue();
    }

    protected void dump(Node node){
        DOMUtil.prettyPrintDOM(node);
    }


    protected void dumpToFile(File f, Node node){
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(f);
            DOMUtil.prettyPrintDOM(node,stream);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (TransformerException e) {
            System.out.println("File couldn't be transformed: " + e.getMessage());
        }
    }

    protected Document getXmlResource(String fileName) throws Exception {

        return DOMUtil.parseXmlFile(fileName,true,false);
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setNamespaceAware(true);
//        factory.setValidating(false);
//
//        // Create builder.
//        DocumentBuilder builder = factory.newDocumentBuilder();
//
//        // Parse files.
//        return builder.parse(getClass().getResourceAsStream(fileName));
    }

    protected DOMComparator getComparator() {
        DOMComparator comparator = new DOMComparator();
        comparator.setIgnoreNamespaceDeclarations(true);
        comparator.setIgnoreWhitespace(true);
        comparator.setIgnoreComments(true);
        comparator.setErrorHandler(new DOMComparator.SystemErrorHandler());

        return comparator;
    }

    
    /**
     * @param modelID
     * @return
     * @throws XFormsException
     */
    private XdmNode getDefaultInstanceNodeInfoOfModel(String modelID) throws XFormsException {
        Model model = (Model) this.processor.getXFormsModel(modelID);
        return (XdmNode) model.getDefaultInstance().getInstanceNodeset().get(0);
    }

    /**
     * @param instanceID
     * @return
     * @throws XFormsException
   */
  private XdmNode getInstanceNodeInfo(String instanceID) throws XFormsException {
      return (XdmNode) getDefaultModel().getInstance(instanceID).getInstanceNodeset().get(0);
  }

}
