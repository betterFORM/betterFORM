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
package de.betterform.connector;

import junit.framework.TestCase;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.config.Config;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.exception.XFormsSubmitError;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;
import java.net.URISyntaxException;

/**
 * Tests the connector factory.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @author Eduardo Millan <emillan@users.sourceforge.net>
 * @version $Id: ConnectorFactoryTest.java 3479 2008-08-19 10:44:53Z joern $
 */
public class ConnectorFactoryTest extends TestCase {

//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private ConnectorFactory connectorFactory;
    private Map context;
    private XFormsProcessorImpl xformsProcesssorImpl;

    /**
     * Test case for submission handler creation.
     *
     * @throws Exception if any error occurred during testing.
     */
    public void testCreateSubmissionHandler() throws Exception {
        Element submission = xformsProcesssorImpl.getContainer().lookup("debug").getElement();
        Connector handler = this.connectorFactory.createSubmissionHandler("test:some-arbitrary-test-resource", submission);

        assertEquals("de.betterform.connector.ConnectorFactoryTestSubmissionHandler", handler.getClass().getName());
        assertEquals("test:some-arbitrary-test-resource", handler.getURI());
        assertEquals(this.context, handler.getContext());
    }

    public void testCreateSubmissionHandler2() throws Exception {
        try{
            this.xformsProcesssorImpl.dispatch("b-debug", DOMEventNames.ACTIVATE);
        }catch(Exception e){
            assertTrue(e instanceof XFormsSubmitError);
        }
    }

    /**
     * Test case for uri resolver creation.
     *
     * @throws Exception if any error occurred during testing.
     */
    public void testCreateURIResolver() throws Exception {
        Connector resolver = this.connectorFactory.createURIResolver("test:some-arbitrary-test-resource", null);

        assertEquals("de.betterform.connector.ConnectorFactoryTestURIResolver", resolver.getClass().getName());
        assertEquals("test:some-arbitrary-test-resource", resolver.getURI());
        assertEquals(this.context, resolver.getContext());
    }

    public void testGetAbsoluteURI() throws Exception {
        String uri = connectorFactory.getAbsoluteURI("test:some-arbitrary-test-resource", null).toString();
        assertTrue("URI is not absolute", uri.equals("test:some-arbitrary-test-resource"));
    }

    public void testGetAbsoluteURIwithContext() throws Exception {
        Element submission = xformsProcesssorImpl.getContainer().lookup("debug").getElement();
        String contextUri = connectorFactory.getAbsoluteURI("test:host:address/param1={$user}&param2={$pass}", submission).toString();
        assertTrue(contextUri.equals("test:host:address/param1=arni&param2=qwert"));

    }

    /**
     * test substitution of context params. Implementation has changed so evalAttributeValueTemplates is called
     * instead applyContextProperties.
     *
     * @throws Exception
     */
    public void testAVTVariables() throws Exception {
        String uriString = connectorFactory.evalAttributeValueTemplates("test:host/path?p1={$user}&p2={$pass}",null);
        assertTrue("uriString did not resolve", "test:host/path?p1=arni&p2=qwert".equals(uriString));

        uriString = connectorFactory.evalAttributeValueTemplates("test:host/path?p1={$foo}&p2={$bar}",null);
        assertTrue("uriString did not resolve", "test:host/path?p1=&p2=".equals(uriString));
    }

    public void testAVTXPath() throws Exception{
        Element submission = xformsProcesssorImpl.getContainer().lookup("s-other").getElement();
        String uriString = connectorFactory.evalAttributeValueTemplates("{instance('default')/foo}",submission);
        assertEquals("http://foo.bar",uriString);
        // System.out.println(uriString);

    }

    public void testAVTMultiInstance() throws Exception {
        Element submission = xformsProcesssorImpl.getContainer().lookup("s-other").getElement();
        String uriString = connectorFactory.evalAttributeValueTemplates("{instance('default')/foo}/{bar}/someString",submission);
        assertEquals("http://foo.bar/bar/someString",uriString);
        // System.out.println(uriString);
    }

    public void testAVTRootAttribute() throws Exception{
        Element submission = xformsProcesssorImpl.getContainer().lookup("s-other").getElement();
        String uriString = connectorFactory.evalAttributeValueTemplates("{instance('default')/foo}/{./@test}/someString:",submission);
        assertEquals("http://foo.bar/foo/someString:",uriString);
        // System.out.println(uriString);

    }

    public void testLoadTemplate()  {
        try{
            xformsProcesssorImpl.dispatch("b-load", DOMEventNames.ACTIVATE);
        }catch(Exception e){
            assertTrue(e instanceof XFormsException);
            assertTrue(e.getCause().getCause() instanceof URISyntaxException);
        }
    }

    /**
     * Test case for default factory creation
     *
     * @throws Exception in any error ocurring during testing
     */
    public void testCreateDefaultFactory() throws Exception {
        Config.getInstance(getClass().getResource("DefaultFactoryConfig.xml").getPath());
        ConnectorFactory connectorFactory = ConnectorFactory.getFactory();

        //Test connector factory required
        assertTrue("Default Connector factory wrong",
                connectorFactory instanceof de.betterform.connector.DefaultConnectorFactory);
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        // load config
        Config.getInstance(getClass().getResource("ConnectorFactoryTestConfig.xml").getPath());

        // create context
        Map context = new HashMap();
        context.put("user", "arni");
        context.put("pass", "qwert");

        Document inForm = getXmlResource("ConnectorTest.xhtml");
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(inForm);
        String path = getClass().getResource("ConnectorTest.xhtml").getPath();
        String baseURI = "file://" + path.substring(0, path.lastIndexOf("ConnectorTest.xhtml"));

        this.xformsProcesssorImpl.setContext(context);
        this.xformsProcesssorImpl.setBaseURI(baseURI);
        this.xformsProcesssorImpl.init();

        // create connector factory
        this.connectorFactory = xformsProcesssorImpl.getContainer().getConnectorFactory();
        this.context = xformsProcesssorImpl.getContext();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.connectorFactory = null;
        this.context = null;
        Config.unloadConfig();
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

// end of class
