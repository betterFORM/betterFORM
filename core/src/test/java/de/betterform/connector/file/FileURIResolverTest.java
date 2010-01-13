package de.betterform.connector.file;

import junit.framework.TestCase;
import de.betterform.xml.dom.DOMComparator;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.connector.URIResolver;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URI;

/**
 * Test cases for the file URI resolver.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: FileURIResolverTest.java 2797 2007-08-10 12:45:24Z joern $
 */
public class FileURIResolverTest extends TestCase {

    private DOMComparator comparator;
    private Document instance;

    /**
     * Tests the resolution of an absolute file URI.
     *
     * @throws Exception if any error occurred during testing.
     */
    public void testResolve() throws Exception {
        // create resolver with URI
        String uri = new URI(getClass().getResource("FileURIResolverTestInstance.xml").toString()).toString();
        URIResolver resolver = new FileURIResolver();
        resolver.setURI(uri);

        // resolve resource
        Object result = resolver.resolve();

        // assertions
        assertTrue("resolved object is not a DOM document", result instanceof org.w3c.dom.Document);
        assertTrue("resolved document is wrong", this.comparator.compare(this.instance, (Document) result));
    }

    public void testResolveDir() throws Exception {
        // create resolver with URI
        String uri = new File(getClass().getResource("FileURIResolverTestInstance.xml").getPath()).getParentFile().toURI().toString();
        URIResolver resolver = new FileURIResolver();
        resolver.setURI(uri);

        // resolve resource
        Document result = (Document) resolver.resolve();

        // assertions
        assertEquals("dir", result.getDocumentElement().getNodeName());
        assertEquals("file", DOMUtil.getFirstChildElement(result.getDocumentElement()).getNodeName());
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
        this.instance = builder.parse(getClass().getResourceAsStream("FileURIResolverTestInstance.xml"));

        this.comparator = new DOMComparator();
        this.comparator.setIgnoreNamespaceDeclarations(true);
        this.comparator.setIgnoreWhitespace(true);
        this.comparator.setIgnoreComments(true);
        this.comparator.setErrorHandler(new DOMComparator.SystemErrorHandler());
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during teardown.
     */
    protected void tearDown() throws Exception {
        this.instance = null;
        this.comparator = null;
    }

}
