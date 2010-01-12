// Copyright 2010 betterForm
/*
 *
 *    Artistic License
 *
 *    Preamble
 *
 *    The intent of this document is to state the conditions under which a Package may be copied, such that
 *    the Copyright Holder maintains some semblance of artistic control over the development of the
 *    package, while giving the users of the package the right to use and distribute the Package in a
 *    more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *    Definitions:
 *
 *    "Package" refers to the collection of files distributed by the Copyright Holder, and derivatives
 *    of that collection of files created through textual modification.
 *
 *    "Standard Version" refers to such a Package if it has not been modified, or has been modified
 *    in accordance with the wishes of the Copyright Holder.
 *
 *    "Copyright Holder" is whoever is named in the copyright or copyrights for the package.
 *
 *    "You" is you, if you're thinking about copying or distributing this Package.
 *
 *    "Reasonable copying fee" is whatever you can justify on the basis of media cost, duplication
 *    charges, time of people involved, and so on. (You will not be required to justify it to the
 *    Copyright Holder, but only to the computing community at large as a market that must bear the
 *    fee.)
 *
 *    "Freely Available" means that no fee is charged for the item itself, though there may be fees
 *    involved in handling the item. It also means that recipients of the item may redistribute it under
 *    the same conditions they received it.
 *
 *    1. You may make and give away verbatim copies of the source form of the Standard Version of this
 *    Package without restriction, provided that you duplicate all of the original copyright notices and
 *    associated disclaimers.
 *
 *    2. You may apply bug fixes, portability fixes and other modifications derived from the Public Domain
 *    or from the Copyright Holder. A Package modified in such a way shall still be considered the
 *    Standard Version.
 *
 *    3. You may otherwise modify your copy of this Package in any way, provided that you insert a
 *    prominent notice in each changed file stating how and when you changed that file, and provided that
 *    you do at least ONE of the following:
 *
 *        a) place your modifications in the Public Domain or otherwise make them Freely
 *        Available, such as by posting said modifications to Usenet or an equivalent medium, or
 *        placing the modifications on a major archive site such as ftp.uu.net, or by allowing the
 *        Copyright Holder to include your modifications in the Standard Version of the Package.
 *
 *        b) use the modified Package only within your corporation or organization.
 *
 *        c) rename any non-standard executables so the names do not conflict with standard
 *        executables, which must also be provided, and provide a separate manual page for each
 *        non-standard executable that clearly documents how it differs from the Standard
 *        Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    4. You may distribute the programs of this Package in object code or executable form, provided that
 *    you do at least ONE of the following:
 *
 *        a) distribute a Standard Version of the executables and library files, together with
 *        instructions (in the manual page or equivalent) on where to get the Standard Version.
 *
 *        b) accompany the distribution with the machine-readable source of the Package with
 *        your modifications.
 *
 *        c) accompany any non-standard executables with their corresponding Standard Version
 *        executables, giving the non-standard executables non-standard names, and clearly
 *        documenting the differences in manual pages (or equivalent), together with instructions
 *        on where to get the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    5. You may charge a reasonable copying fee for any distribution of this Package. You may charge
 *    any fee you choose for support of this Package. You may not charge a fee for this Package itself.
 *    However, you may distribute this Package in aggregate with other (possibly commercial) programs as
 *    part of a larger (possibly commercial) software distribution provided that you do not advertise this
 *    Package as a product of your own.
 *
 *    6. The scripts and library files supplied as input to or produced as output from the programs of this
 *    Package do not automatically fall under the copyright of this Package, but belong to whomever
 *    generated them, and may be sold commercially, and may be aggregated with this Package.
 *
 *    7. C or perl subroutines supplied by you and linked into this Package shall not be considered part of
 *    this Package.
 *
 *    8. The name of the Copyright Holder may not be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 *    9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package de.betterform.xml.xforms;

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
