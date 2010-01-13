// Copyright 2010 betterForm
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
