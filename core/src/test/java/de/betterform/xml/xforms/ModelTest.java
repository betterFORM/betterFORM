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
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.Model;
import org.apache.xerces.xs.XSModel;

import java.util.List;


/**
 * Test cases for the model implementation.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ModelTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class ModelTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private String baseURI;
    private XFormsProcessorImpl xformsProcesssorImpl;

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        String path = getClass().getResource("ModelTest.xhtml").getPath();
        this.baseURI = "file://" + path.substring(0, path.lastIndexOf("ModelTest.xhtml"));

        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setBaseURI(this.baseURI);
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("ModelTest.xhtml"));
        this.xformsProcesssorImpl.init();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
        this.baseURI = null;
    }

    /**
     * Tests default instance retrieval.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetDefaultInstance() throws Exception {
        Model model = this.xformsProcesssorImpl.getContainer().getModel("model-1");
        Instance defaultInstance = model.getDefaultInstance();
        Instance identifiedInstance = model.getInstance("instance-1");

        assertSame(identifiedInstance, defaultInstance);
        assertEquals("instance-1", defaultInstance.getId());
    }

    /**
     * Tests default instance retrieval.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetDefaultInstanceAllModels() throws Exception {
        Model model1 = this.xformsProcesssorImpl.getContainer().getModel("model-1");
        Instance defaultInstance1 = model1.getDefaultInstance();

        Model model2 = this.xformsProcesssorImpl.getContainer().getModel("model-2");
        Instance defaultInstance2 = model2.getDefaultInstance();

        assertNotSame(defaultInstance1, defaultInstance2);
        assertEquals("instance-1", defaultInstance1.getId());
        assertEquals("instance-3", defaultInstance2.getId());
    }

    /**
     * Tests lazy instance creation.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testAddInstance() throws Exception {
        String id = String.valueOf(System.currentTimeMillis());
        Model model = this.xformsProcesssorImpl.getContainer().getModel("model-1");
        Instance instance = model.addInstance(id);

        assertNotNull(instance);
        assertEquals(id, instance.getId());
        assertEquals(instance, model.getInstance(id));
        assertEquals(instance, model.getContainer().lookup(id));
    }

    /**
     * Tests lazy instance creation.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testAddInstanceDefault() throws Exception {
        Model model = this.xformsProcesssorImpl.getContainer().getModel("model-empty");
        Instance instance = model.addInstance("");

        assertNotNull(instance);
        assertFalse(instance.getId().equals(""));
        assertEquals(instance, model.getDefaultInstance());
        assertEquals(instance, model.getInstance(""));
        assertEquals(instance, model.getInstance(instance.getId()));
        assertEquals(instance, model.getContainer().lookup(instance.getId()));
    }

    /**
     * Tests instance id computation.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testComputeInstanceId() throws Exception {
        Model model = this.xformsProcesssorImpl.getContainer().getModel("model-1");

        assertEquals(null, model.computeInstanceId(null));
        assertEquals("instance-1", model.computeInstanceId(""));
        assertEquals("instance-1", model.computeInstanceId("/data/item"));
        assertEquals("instance-1", model.computeInstanceId("/data/item[number(.) = number(instance('instance-2')/item - 1)]"));
        assertEquals("instance-1", model.computeInstanceId("instance('')/item"));
        assertEquals("instance-1", model.computeInstanceId("instance('instance-1')/item"));
        assertEquals("instance-1", model.computeInstanceId("instance(concat('instance-', format-number(instance('instance-2')/item - 1, '0')))/item"));
        assertEquals(null, model.computeInstanceId("instance('foo')/item"));
    }

    /**
     * Tests schema loading.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetSchemas() throws Exception {
        Model model = this.xformsProcesssorImpl.getContainer().getModel("model-1");
        List schemas = model.getSchemas();

        assertEquals(1, schemas.size());
        assertEquals(NamespaceConstants.XFORMS_NS, model.getTargetNamespace((XSModel) schemas.get(0)));
    }

    /**
     * Tests schema loading.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetSchemasExternal() throws Exception {
        Model model = this.xformsProcesssorImpl.getContainer().getModel("model-2");
        List schemas = model.getSchemas();

        assertEquals(2, schemas.size());
        assertEquals(NamespaceConstants.XFORMS_NS, model.getTargetNamespace((XSModel) schemas.get(0)));
        assertEquals("http://test.org/extern", model.getTargetNamespace((XSModel) schemas.get(1)));
    }

    /**
     * Tests schema loading.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetSchemasInternal() throws Exception {
        Model model = this.xformsProcesssorImpl.getContainer().getModel("model-3");
        List schemas = model.getSchemas();

        assertEquals(2, schemas.size());
        assertEquals(NamespaceConstants.XFORMS_NS, model.getTargetNamespace((XSModel) schemas.get(0)));
        assertEquals("http://test.org/intern", model.getTargetNamespace((XSModel) schemas.get(1)));
    }

    /**
     * Tests schema loading.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetSchemasInline() throws Exception {
        Model model = this.xformsProcesssorImpl.getContainer().getModel("model-4");
        List schemas = model.getSchemas();

        assertEquals(2, schemas.size());
        assertEquals(NamespaceConstants.XFORMS_NS, model.getTargetNamespace((XSModel) schemas.get(0)));
        assertEquals("http://test.org/inline", model.getTargetNamespace((XSModel) schemas.get(1)));
    }



}

// end of class
