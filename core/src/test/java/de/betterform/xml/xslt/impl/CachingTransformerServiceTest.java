/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */
package de.betterform.xml.xslt.impl;

import junit.framework.TestCase;
import de.betterform.xml.xforms.exception.XFormsException;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests the Caching transformer service.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: CachingTransformerServiceTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class CachingTransformerServiceTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private CachingTransformerService transformerService;
    private TestResourceResolver resourceResolver;
    private File parent;

    /**
     * Tests obtaining a transformer object from the cache.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetTransformer() throws Exception {

        // make URI and get transformer loaded
        URI simpleURI = new File(this.parent, "CachingTransformerServiceSimpleTest.xsl").toURI();
        Transformer simpleTransformer = this.transformerService.getTransformer(simpleURI);

        assertNotNull(simpleTransformer);
        assertEquals(1, this.resourceResolver.resources.keySet().size());
        assertEquals(true, this.resourceResolver.resources.keySet().contains(simpleURI));

        // store resource for later, clear test resources and get transformer from cache
        TestResource resource = (TestResource) this.resourceResolver.resources.get(simpleURI);
        this.resourceResolver.resources.clear();
        simpleTransformer = this.transformerService.getTransformer(simpleURI);

        assertNotNull(simpleTransformer);
        assertEquals(0, this.resourceResolver.resources.keySet().size());

        // modify resource, clear test resources and get transformer reloaded
        resource.lastModified = System.currentTimeMillis();
        this.resourceResolver.resources.clear();
        simpleTransformer = this.transformerService.getTransformer(simpleURI);

        assertNotNull(simpleTransformer);
        assertEquals(1, this.resourceResolver.resources.keySet().size());
        assertEquals(true, this.resourceResolver.resources.keySet().contains(simpleURI));
    }
    public void testSetTransformer() throws Exception{
        try{
            this.transformerService.setTransformerFactory(TransformerFactory.newInstance());
        }catch(TransformerException e){
            assertNotNull(e);
        }
    }

    /**
     * Tests obtaining a transformer object from the cache.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetTransformerWithInclude() throws Exception {
        // make URIs and get transformers loaded
        URI includeURI = new File(this.parent, "CachingTransformerServiceIncludeTest.xsl").toURI();
        URI simpleURI = new File(this.parent, "CachingTransformerServiceSimpleTest.xsl").toURI();
        Transformer includeTransformer = this.transformerService.getTransformer(includeURI);
        Transformer simpleTransformer = this.transformerService.getTransformer(simpleURI);

        assertNotNull(includeTransformer);
        assertNotNull(simpleTransformer);
        assertEquals(2, this.resourceResolver.resources.keySet().size());
        assertEquals(true, this.resourceResolver.resources.keySet().contains(includeURI));
        assertEquals(true, this.resourceResolver.resources.keySet().contains(simpleURI));

        // store resource for later, clear test resources and get transformers from cache
        TestResource resource = (TestResource) this.resourceResolver.resources.get(simpleURI);
        this.resourceResolver.resources.clear();
        includeTransformer = this.transformerService.getTransformer(includeURI);
        simpleTransformer = this.transformerService.getTransformer(simpleURI);

        assertNotNull(includeTransformer);
        assertNotNull(simpleTransformer);
        assertEquals(0, this.resourceResolver.resources.keySet().size());

        // modify resource, clear test resources and get transformers reloaded
        resource.lastModified = System.currentTimeMillis();
        this.resourceResolver.resources.clear();
        includeTransformer = this.transformerService.getTransformer(includeURI);
        simpleTransformer = this.transformerService.getTransformer(simpleURI);

        assertNotNull(includeTransformer);
        assertNotNull(simpleTransformer);
        assertEquals(2, this.resourceResolver.resources.keySet().size());
        assertEquals(true, this.resourceResolver.resources.keySet().contains(includeURI));
        assertEquals(true, this.resourceResolver.resources.keySet().contains(simpleURI));
    }

    /**
     * Tests obtaining a transformer object from the cache.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetTransformerWithNestedInclude() throws Exception {
        // make URIs and get transformers loaded
        URI nestedURI = new File(this.parent, "CachingTransformerServiceNestedTest.xsl").toURI();
        URI includeURI = new File(this.parent, "CachingTransformerServiceIncludeTest.xsl").toURI();
        URI simpleURI = new File(this.parent, "CachingTransformerServiceSimpleTest.xsl").toURI();
        Transformer nestedTransformer = this.transformerService.getTransformer(nestedURI);
        Transformer includeTransformer = this.transformerService.getTransformer(includeURI);
        Transformer simpleTransformer = this.transformerService.getTransformer(simpleURI);

        assertNotNull(nestedTransformer);
        assertNotNull(includeTransformer);
        assertNotNull(simpleTransformer);
        assertEquals(3, this.resourceResolver.resources.keySet().size());
        assertEquals(true, this.resourceResolver.resources.keySet().contains(nestedURI));
        assertEquals(true, this.resourceResolver.resources.keySet().contains(includeURI));
        assertEquals(true, this.resourceResolver.resources.keySet().contains(simpleURI));

        // store resource for later, clear test resources and get transformers from cache
        TestResource resource = (TestResource) this.resourceResolver.resources.get(simpleURI);
        this.resourceResolver.resources.clear();
        nestedTransformer = this.transformerService.getTransformer(nestedURI);
        includeTransformer = this.transformerService.getTransformer(includeURI);
        simpleTransformer = this.transformerService.getTransformer(simpleURI);

        assertNotNull(nestedTransformer);
        assertNotNull(includeTransformer);
        assertNotNull(simpleTransformer);
        assertEquals(0, this.resourceResolver.resources.keySet().size());

        // modify resource, clear test resources and get transformers reloaded
        resource.lastModified = System.currentTimeMillis();
        this.resourceResolver.resources.clear();
        nestedTransformer = this.transformerService.getTransformer(nestedURI);
        includeTransformer = this.transformerService.getTransformer(includeURI);
        simpleTransformer = this.transformerService.getTransformer(simpleURI);

        assertNotNull(nestedTransformer);
        assertNotNull(includeTransformer);
        assertNotNull(simpleTransformer);
        assertEquals(3, this.resourceResolver.resources.keySet().size());
        assertEquals(true, this.resourceResolver.resources.keySet().contains(nestedURI));
        assertEquals(true, this.resourceResolver.resources.keySet().contains(includeURI));
        assertEquals(true, this.resourceResolver.resources.keySet().contains(simpleURI));
    }

    /**
     * Tests obtaining a transformer object from the cache.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetTransformerNonExisting() throws Exception {
        try {
            URI invalidURI = new File(this.parent, "CachingTransformerServiceInvalidTest.xsl").toURI();
            this.transformerService.getTransformer(invalidURI);

            fail();
        }
        catch (Exception e) {
            assertTrue(e instanceof TransformerException);
        }

    }

    /**
     * Tests obtaining a transformer object from the cache.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetIdentityTransformer() throws Exception {
        Transformer transformer = this.transformerService.getTransformer();

        assertNotNull(transformer);
    }

    /**
     * Tests obtaining a transformer object from the cache.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetIdentityTransformerByNullURI() throws Exception {
        Transformer transformer = this.transformerService.getTransformer(null);

        assertNotNull(transformer);
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.parent = new File(getClass().getResource("CachingTransformerServiceTest.class").getPath()).getParentFile();
        this.resourceResolver = new TestResourceResolver(new FileResourceResolver());
        this.transformerService = new CachingTransformerService(this.resourceResolver);
//        this.transformerService.setTransformerFactory(TransformerFactory.newInstance());
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.parent = null;
        this.resourceResolver = null;
        this.transformerService = null;
    }

    private static class TestResource implements Resource {
        long lastModified;
        Resource resource;

        TestResource(Resource resource) {
            this.lastModified = resource.lastModified();
            this.resource = resource;
        }

        public long lastModified() {
            return this.lastModified;
        }

        public InputStream getInputStream() throws XFormsException {
            return this.resource.getInputStream();
        }

        public Source getSource() throws XFormsException {
            return new StreamSource(getInputStream());
        }
    }

    private static class TestResourceResolver implements ResourceResolver {
        Map resources = new HashMap();
        ResourceResolver resolver;

        TestResourceResolver(ResourceResolver resolver) {
            this.resolver = resolver;
        }

        public Resource resolve(URI uri) throws XFormsException {
            TestResource resource = new TestResource(this.resolver.resolve(uri));
            this.resources.put(uri, resource);
            return resource;
        }
    }
}
