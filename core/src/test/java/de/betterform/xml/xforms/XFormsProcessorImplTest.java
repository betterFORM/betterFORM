/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.ui.Text;
import junit.framework.TestCase;
import de.betterform.xml.dom.DOMComparator;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URI;

/**
 * Tests various functions of the betterForm XForms Processor. Most functions are
 * tested through XML in- and output-files.
 * <p/>
 * The tests in this class are 'stacked' that means they build on each other.
 * If init fails all others may fail too.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XFormsProcessorImplTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class XFormsProcessorImplTest extends TestCase {
/*
    static {
        org.apache.log4j.BasicConfigurator.configure();
    }
*/

    private XFormsProcessorImpl processor;
    private Document form;
    private String baseURI;

    /**
     * tests the dispatching of an event to an element.
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testDispatchEvent() throws Exception {
        processor.setXForms(getClass().getResourceAsStream("actions.xhtml"));
        processor.init();
        assertTrue(!processor.dispatch("setvalue-trigger", DOMEventNames.ACTIVATE));

        //test the side effect of dispatching the DOMActivate to a setvalue trigger -> the instance has a new value
        Document instance = processor.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument();
        assertEquals("Hello World", XPathUtil.evaluateAsString(instance, "//helloworld"));
    }

    /**
     * test, if setControlValue correctly changes the value and a value-changed event occurs on the relevant
     * control.
     *
     * @throws Exception
     */
    public void testUpdateControlValue1() throws Exception {
        processor.setXForms(getClass().getResourceAsStream("actions.xhtml"));
        processor.init();

        TestEventListener listener = new TestEventListener();

        Node node = XPathUtil.evaluateAsSingleNode(processor.getXForms(), "//*[@id='hello-input']");

        EventTarget eventTarget = (EventTarget) node;
        eventTarget.addEventListener(XFormsEventNames.VALUE_CHANGED, listener, false);

        processor.setControlValue("hello-input", "Hello new World");
        assertTrue(listener.getType().equals(XFormsEventNames.VALUE_CHANGED));
        assertTrue(listener.getId().equals("hello-input"));

        //test the side effect of dispatching the DOMActivate to a setvalue trigger -> the instance has a new value
        Document instance = processor.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument();
        assertEquals("Hello new World", XPathUtil.evaluateAsString(instance, "//helloworld"));
    }

    /**
     * test if setControlValue suppresses update for unchanged value.
     *
     * @throws Exception
     */
    public void testUpdateControlValue2() throws Exception {
        processor.setXForms(getClass().getResourceAsStream("actions.xhtml"));
        processor.init();

        TestEventListener listener = new TestEventListener();
        Node node = XPathUtil.evaluateAsSingleNode(this.processor.getXForms(), "//*[@id='hello-input']");

        EventTarget eventTarget = (EventTarget) node;
        eventTarget.addEventListener(XFormsEventNames.VALUE_CHANGED, listener, false);

        processor.setControlValue("hello-input", "Hello World");
        //value mustn't change cause value of node is 'Hello World' initially - test the case that setValue isn't called
        assertNull(listener.getType());
        assertNull(listener.getId());
    }

    public void testHasControlType() throws Exception {
        XFormsProcessorImpl xformsProcesssorImpl = new XFormsProcessorImpl();
        xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("XFormsProcessorImplTest.xhtml"));
        xformsProcesssorImpl.init();

        assertTrue(xformsProcesssorImpl.isFileUpload("input-1", "anyURI"));
        assertTrue(xformsProcesssorImpl.isFileUpload("input-1", "xs:anyURI"));
        assertTrue(xformsProcesssorImpl.isFileUpload("input-2", "integer"));
        assertTrue(xformsProcesssorImpl.isFileUpload("input-2", "xs:integer"));
        assertTrue(xformsProcesssorImpl.isFileUpload("input-3", "string"));
        assertTrue(xformsProcesssorImpl.isFileUpload("input-3", "xs:string"));
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testGetInstanceDocument() throws Exception {
        //configure Bean with it
        processor.setXForms(getClass().getResourceAsStream("buglet.xml"));
        processor.init();

        //get the instance-document
        Document out = processor.getContainer().getDefaultModel().getInstanceDocument("");
        assertNotNull(out);

        //get expected document
        Document expected = getXmlResource("buglet-instance.xml");
        assertTrue(getComparator().compare(processor.getContainer().getModel(null).getDefaultInstance()
            .getInstanceDocument().getDocumentElement(),
            expected.getDocumentElement()));
    }

    /**
     * tests processor initialization
     * <p/>
     * this may be taken as a blueprint for instanciating a XFormsProcessorImpl in an arbitrary environment
     *
     * @throws Exception
     */
    public void testInit() throws Exception {
        //determine path of the the xml testfile and use it as baseURI for processor
        String path = getClass().getResource("buglet.xml").getPath();
        //        System.out.println("path: " + path);
        //set the XForms document to process
        this.processor.setXForms(getClass().getResourceAsStream("buglet.xml"));

        //set the base URI for this process
        this.processor.setBaseURI("file://" + path);

        //initialize/bootstrap processor
        this.processor.init();

        //check, if default instance has input document
        assertTrue(getComparator().compare(processor.getContainer().getDefaultModel().getDefaultInstance()
            .getInstanceDocument(),
            getXmlResource("buglet-instance.xml")));
    }

    /**
     * Tests setting a XML container DOM.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetXMLContainerDOM() throws Exception {
        this.processor.setXForms(this.form);

        assertNotNull(this.processor.getContainer());
        assertTrue(this.processor.getContainer().getDocument() instanceof Document);
    }

    /**
     * Tests setting an absolute XML container URI.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetXMLContainerURIAbsolute() throws Exception {
        this.processor.setXForms(new URI(getClass().getResource("XFormsProcessorImplTest.xhtml").toExternalForm()));

        assertNotNull(this.processor.getContainer());
        assertTrue(this.processor.getContainer().getDocument() instanceof Document);
    }

    /**
     * Tests setting a relative XML container URI.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetXMLContainerURIRelative() throws Exception {
        this.processor.setBaseURI(this.baseURI);
        this.processor.setXForms(new URI("XFormsProcessorImplTest.xhtml"));

        assertNotNull(this.processor.getContainer());
        assertTrue(this.processor.getContainer().getDocument() instanceof Document);
    }

    /**
     * Tests setting a XML container input stream.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetXMLContainerInputStream() throws Exception {
        this.processor.setXForms(getClass().getResourceAsStream("XFormsProcessorImplTest.xhtml"));

        assertNotNull(this.processor.getContainer());
        assertTrue(this.processor.getContainer().getDocument() instanceof Document);
    }

    /**
     * Tests setting a XML container input source.
     *
     * @throws Exception in any error occurred during the test.
     */
    public void testSetXMLContainerInputSource() throws Exception {
        this.processor.setXForms(new InputSource(this.baseURI + "XFormsProcessorImplTest.xhtml"));

        assertNotNull(this.processor.getContainer());
        assertTrue(this.processor.getContainer().getDocument() instanceof org.apache.xerces.dom.DocumentImpl);
    }

/*
    public void testCreateUIElement() throws Exception{
        this.processor.setXForms(new InputSource(this.baseURI + "xfRoleTest.xhtml"));
        this.processor.init();

        this.processor.createUIElement("myInput","input","hello","world",null);

        XFormsElement input = this.processor.lookup("myInput");
        assertNotNull(input);
        assertTrue(input instanceof Text);
        assertTrue(((Text) input).getModel() instanceof Model);
        assertEquals("model-1",input.getModel().getId());
        DOMUtil.prettyPrintDOM(this.processor.getContainer().getDocument(),System.out);
    }
*/

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.form = builder.parse(getClass().getResourceAsStream("XFormsProcessorImplTest.xhtml"));

        String path = getClass().getResource("XFormsProcessorImplTest.xhtml").getPath();
        this.baseURI = "file://" + path.substring(0, path.lastIndexOf("XFormsProcessorImplTest.xhtml"));

        this.processor = new XFormsProcessorImpl();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.form = null;
        this.baseURI = null;
        this.processor = null;
    }

    private DOMComparator getComparator() {
        DOMComparator comparator = new DOMComparator();
        comparator.setIgnoreNamespaceDeclarations(true);
        comparator.setIgnoreWhitespace(true);
        comparator.setIgnoreComments(true);
        comparator.setErrorHandler(new DOMComparator.SystemErrorHandler());

        return comparator;
    }

    // ++++++++++++ oops, don't look at my private parts!  +++++++++++++++
    //helper - should be moved elsewhere...
    private Document getXmlResource(String fileName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        // Create builder.
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parse files.
        return builder.parse(getClass().getResourceAsStream(fileName));
    }
}
