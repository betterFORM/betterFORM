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
