// Copyright 2010 betterForm
/*
 *
 *    Artistic License
 *
 *    Preamble
 *
 *    The intent of this document is to state the conditions under which a
 *    Package may be copied, such that the Copyright Holder maintains some
 *    semblance of artistic control over the development of the package,
 *    while giving the users of the package the right to use and distribute
 *    the Package in a more-or-less customary fashion, plus the right to make
 *    reasonable modifications.
 *
 *    Definitions:
 *
 *    "Package" refers to the collection of files distributed by the
 *    Copyright Holder, and derivatives of that collection of files created
 *    through textual modification.
 *
 *    "Standard Version" refers to such a Package if it has not been
 *    modified, or has been modified in accordance with the wishes of the
 *    Copyright Holder.
 *
 *    "Copyright Holder" is whoever is named in the copyright or copyrights
 *    for the package.
 *
 *    "You" is you, if you're thinking about copying or distributing this Package.
 *
 *    "Reasonable copying fee" is whatever you can justify on the basis of
 *    media cost, duplication charges, time of people involved, and so
 *    on. (You will not be required to justify it to the Copyright Holder,
 *    but only to the computing community at large as a market that must bear
 *    the fee.)
 *
 *    "Freely Available" means that no fee is charged for the item itself,
 *    though there may be fees involved in handling the item. It also means
 *    that recipients of the item may redistribute it under the same
 *    conditions they received it.
 *
 *    1. You may make and give away verbatim copies of the source form of the
 *    Standard Version of this Package without restriction, provided that you
 *    duplicate all of the original copyright notices and associated
 *    disclaimers.
 *
 *    2. You may apply bug fixes, portability fixes and other modifications
 *    derived from the Public Domain or from the Copyright Holder. A Package
 *    modified in such a way shall still be considered the Standard Version.
 *
 *    3. You may otherwise modify your copy of this Package in any way,
 *    provided that you insert a prominent notice in each changed file
 *    stating how and when you changed that file, and provided that you do at
 *    least ONE of the following:
 *
 *        a) place your modifications in the Public Domain or otherwise make
 *        them Freely Available, such as by posting said modifications to
 *        Usenet or an equivalent medium, or placing the modifications on a
 *        major archive site such as ftp.uu.net, or by allowing the Copyright
 *        Holder to include your modifications in the Standard Version of the
 *        Package.
 *
 *        b) use the modified Package only within your corporation or
 *        organization.
 *
 *        c) rename any non-standard executables so the names do not conflict
 *        with standard executables, which must also be provided, and provide
 *        a separate manual page for each non-standard executable that
 *        clearly documents how it differs from the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    4. You may distribute the programs of this Package in object code or
 *    executable form, provided that you do at least ONE of the following:
 *
 *        a) distribute a Standard Version of the executables and library
 *        files, together with instructions (in the manual page or
 *        equivalent) on where to get the Standard Version.
 *
 *        b) accompany the distribution with the machine-readable source of
 *        the Package with your modifications.
 *
 *        c) accompany any non-standard executables with their corresponding
 *        Standard Version executables, giving the non-standard executables
 *        non-standard names, and clearly documenting the differences in
 *        manual pages (or equivalent), together with instructions on where
 *        to get the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    5. You may charge a reasonable copying fee for any distribution of this
 *    Package. You may charge any fee you choose for support of this
 *    Package. You may not charge a fee for this Package itself.  However,
 *    you may distribute this Package in aggregate with other (possibly
 *    commercial) programs as part of a larger (possibly commercial) software
 *    distribution provided that you do not advertise this Package as a
 *    product of your own.
 *
 *    6. The scripts and library files supplied as input to or produced as
 *    output from the programs of this Package do not automatically fall
 *    under the copyright of this Package, but belong to whomever generated
 *    them, and may be sold commercially, and may be aggregated with this
 *    Package.
 *
 *    7. C or perl subroutines supplied by you and linked into this Package
 *    shall not be considered part of this Package.
 *
 *    8. The name of the Copyright Holder may not be used to endorse or
 *    promote products derived from this software without specific prior
 *    written permission.
 *
 *    9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
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
