// Copyright 2010 betterForm
package de.betterform.xml.xforms.model;


import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;

// end of class

/**
 * Test cases for the instance implementation.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: InstanceTest.java 3264 2008-07-16 12:26:54Z joern $
 */
public class InstanceMissingEmptyNSTest extends BetterFormTestCase {
//	static {
//		org.apache.log4j.BasicConfigurator.configure();
//	}

    private Instance instance;
    private EventTarget eventTarget;
    private TestEventListener nodeInsertedListener;
    private TestEventListener nodeDeletedListener;

    /**
     * Tests instance initialization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInit() throws Exception {
        assertNotNull(this.instance);
        assertEquals("instance", this.instance.getId());
        assertNotNull(this.instance.getInstanceDocument());
        assertEquals("data", this.instance.getInstanceDocument().getDocumentElement().getNodeName());
    }

    /**
     * Tests initial state.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testHasInitialInstance() throws Exception {
        assertEquals(true, this.instance.hasInitialInstance());
    }

    /**
     * Tests instance reset.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testReset() throws Exception {
        assertEquals(3, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        this.instance.deleteNode(evaluateInDefaultContextAsNode("/data/item[1]"), "/data/item[1]");
        this.instance.deleteNode(evaluateInDefaultContextAsNode("/data/item[1]"), "/data/item[1]");
        this.instance.deleteNode(evaluateInDefaultContextAsNode("/data/item[1]"), "/data/item[1]");

        assertEquals(0, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));


        this.instance.reset();
        reInitializeDefaultContext();

        assertEquals(3, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("first", evaluateInDefaultContextAsString("/data/item[1]"));
        assertEquals("between", evaluateInDefaultContextAsString("/data/item[2]"));
        assertEquals("last", evaluateInDefaultContextAsString("/data/item[3]"));
    }

    /**
     * Tests model item lookup.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testGetModelItem() throws Exception {
        ModelItem item1 = this.instance.getModelItem(evaluateInDefaultContextAsNode("/data/item[1]"));
        ModelItem item2 = this.instance.getModelItem(evaluateInDefaultContextAsNode("/data/item[2]"));
        ModelItem item3 = this.instance.getModelItem(evaluateInDefaultContextAsNode("/data/item[3]"));

        assertNotNull(item1);
        assertNotNull(item2);
        assertNotNull(item3);

        assertNotSame(item1, item2);
        assertNotSame(item2, item3);
        assertNotSame(item1, item3);

        assertNotNull(item1.getId());
        assertNotNull(item2.getId());
        assertNotNull(item3.getId());

        assertNotSame(item1.getId(), item2.getId());
        assertNotSame(item2.getId(), item3.getId());
        assertNotSame(item1.getId(), item3.getId());

        assertSame(item1, this.instance.getModelItem(evaluateInDefaultContextAsNode("/data[1]/item[1]")));
        assertSame(item2, this.instance.getModelItem(evaluateInDefaultContextAsNode("/data[1]/item[2]")));
        assertSame(item3, this.instance.getModelItem(evaluateInDefaultContextAsNode("/data[1]/item[3]")));
    }

    /**
     * Tests node creation.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCreateNode() throws Exception {
        //this.instance.createNode("/data/item[4]");
        this.instance.createNode(evaluateInDefaultContext("/data"), 1, "item");

        Document instance = getDefaultModel().getDefaultInstance().getInstanceDocument();

        assertEquals("4", XPathUtil.evaluateAsString(instance,"count(/data/item)"));
        assertEquals("4", XPathUtil.evaluateAsString(instance,"count(/data/item)"));
        assertEquals("first", XPathUtil.evaluateAsString(instance,"/data/item[1]"));
        assertEquals("between", XPathUtil.evaluateAsString(instance,"/data/item[2]"));
        assertEquals("last", XPathUtil.evaluateAsString(instance,"/data/item[3]"));
        assertEquals("", XPathUtil.evaluateAsString(instance,"/data/item[4]"));
    }

//    /**
//     * Tests node creation.
//     *
//     * @throws Exception if any error occurred during the test.
//     */
//    public void testCreateNodeExisting() throws Exception {
//        this.instance.createNode("/data/item[1]");
//
//        assertEquals("3", this.context.getValue("count(/data/item)", Integer.class).toString());
//        assertEquals("first", this.context.getValue("/data/item[1]"));
//        assertEquals("between", this.context.getValue("/data/item[2]"));
//        assertEquals("last", this.context.getValue("/data/item[3]"));
//    }
//
//    /**
//     * Tests node creation.
//     *
//     * @throws Exception if any error occurred during the test.
//     */
//    public void testCreateNodeNonExisting() throws Exception {
//        try {
//            this.instance.createNode("/data/itme[4]");
//            fail("exception expected");
//        }
//        catch (Exception e) {
//            assertTrue(e instanceof XFormsException);
//
//            assertEquals("3", this.context.getValue("count(/data/item)", Integer.class).toString());
//            assertEquals("first", this.context.getValue("/data/item[1]"));
//            assertEquals("between", this.context.getValue("/data/item[2]"));
//            assertEquals("last", this.context.getValue("/data/item[3]"));
//        }
//    }

    /**
     * Tests node insertion.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertNode() throws Exception {
        this.instance.insertNode(evaluateInDefaultContextAsNode("/data"), evaluateInDefaultContextAsNode("/data/item[last()]"), evaluateInDefaultContextAsNode("/data/item[2]"));

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("first", evaluateInDefaultContextAsString("/data/item[1]"));
        assertEquals("last", evaluateInDefaultContextAsString("/data/item[2]"));
        assertEquals("between", evaluateInDefaultContextAsString("/data/item[3]"));
        assertEquals("last", evaluateInDefaultContextAsString("/data/item[4]"));

        assertEquals("instance", this.nodeInsertedListener.getId());
        assertEquals("/*[1]/item", this.nodeInsertedListener.getContext("nodeset"));
        assertEquals("2", this.nodeInsertedListener.getContext("position"));
        assertEquals(null, this.nodeDeletedListener.getId());
        assertEquals(null, this.nodeDeletedListener.getContext("nodeset"));
        assertEquals(null, this.nodeDeletedListener.getContext("position"));
    }

    /**
     * Tests node insertion.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertNodeLast() throws Exception {
        this.instance.insertNode(evaluateInDefaultContextAsNode("/data"), evaluateInDefaultContextAsNode("/data/item[last()]"), evaluateInDefaultContextAsNode("/data/element"));

        assertEquals(4, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("first", evaluateInDefaultContextAsString("/data/item[1]"));
        assertEquals("between", evaluateInDefaultContextAsString("/data/item[2]"));
        assertEquals("last", evaluateInDefaultContextAsString("/data/item[3]"));
        assertEquals("last", evaluateInDefaultContextAsString("/data/item[4]"));

        assertEquals(5, (int)evaluateInDefaultContextAsDouble("count(/data/*)"));
        assertEquals("item", evaluateInDefaultContextAsString("name(/data/*[1])"));
        assertEquals("item", evaluateInDefaultContextAsString("name(/data/*[2])"));
        assertEquals("item", evaluateInDefaultContextAsString("name(/data/*[3])"));
        assertEquals("item", evaluateInDefaultContextAsString("name(/data/*[4])"));
        assertEquals("element", evaluateInDefaultContextAsString("name(/data/*[5])"));

        assertEquals("instance", this.nodeInsertedListener.getId());
        assertEquals("/*[1]/item", this.nodeInsertedListener.getContext("nodeset"));
        assertEquals("4", this.nodeInsertedListener.getContext("position"));
        assertEquals(null, this.nodeDeletedListener.getId());
        assertEquals(null, this.nodeDeletedListener.getContext("nodeset"));
        assertEquals(null, this.nodeDeletedListener.getContext("position"));
    }

    /**
     * Tests node insertion.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInsertNodeNonExistingOrigin() throws Exception {
        try {
            this.instance.insertNode(evaluateInDefaultContextAsNode("/data"), evaluateInDefaultContextAsNode("/data/itme"), evaluateInDefaultContextAsNode("/data/item[3]"));
            fail("exception expected");
        }
        catch (Exception e) {
            assertTrue(e instanceof XFormsException);

            assertEquals(3, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
            assertEquals("first", evaluateInDefaultContextAsString("/data/item[1]"));
            assertEquals("between", evaluateInDefaultContextAsString("/data/item[2]"));
            assertEquals("last", evaluateInDefaultContextAsString("/data/item[3]"));

            assertEquals(null, this.nodeInsertedListener.getId());
            assertEquals(null, this.nodeInsertedListener.getContext("nodeset"));
            assertEquals(null, this.nodeInsertedListener.getContext("position"));
            assertEquals(null, this.nodeDeletedListener.getId());
            assertEquals(null, this.nodeDeletedListener.getContext("nodeset"));
            assertEquals(null, this.nodeDeletedListener.getContext("position"));
        }
    }



    /**
     * Tests node deletion.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteNode() throws Exception {
        this.instance.deleteNode(evaluateInDefaultContextAsNode("/data/item[2]"), "/data/item[2]");

        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("first", evaluateInDefaultContextAsString("/data/item[1]"));
        assertEquals("last", evaluateInDefaultContextAsString("/data/item[2]"));

        assertEquals(null, this.nodeInsertedListener.getId());
        assertEquals(null, this.nodeInsertedListener.getContext("nodeset"));
        assertEquals(null, this.nodeInsertedListener.getContext("position"));
        assertEquals("instance", this.nodeDeletedListener.getId());
        assertEquals("/data/item", this.nodeDeletedListener.getContext("nodeset"));
        assertEquals("2", this.nodeDeletedListener.getContext("position"));
    }

    /**
     * Tests node deletion.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteNodeNonExisting() throws Exception {
        try {
            this.instance.deleteNode(evaluateInDefaultContextAsNode("/data/iteme"), "/data/iteme");
            fail("exception expected");
        }
        catch (Exception e) {

            assertEquals(3, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
            assertEquals("first", evaluateInDefaultContextAsString("/data/item[1]"));
            assertEquals("between", evaluateInDefaultContextAsString("/data/item[2]"));
            assertEquals("last", evaluateInDefaultContextAsString("/data/item[3]"));

            assertEquals(null, this.nodeInsertedListener.getId());
            assertEquals(null, this.nodeInsertedListener.getContext("nodeset"));
            assertEquals(null, this.nodeInsertedListener.getContext("position"));
            assertEquals(null, this.nodeDeletedListener.getId());
            assertEquals(null, this.nodeDeletedListener.getContext("nodeset"));
            assertEquals(null, this.nodeDeletedListener.getContext("position"));
        }
    }


    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.instance = getDefaultModel().getDefaultInstance();

        this.nodeInsertedListener = new TestEventListener();
        this.nodeDeletedListener = new TestEventListener();

        this.eventTarget = (EventTarget) this.processor.getXForms();
        this.eventTarget.addEventListener(BetterFormEventNames.NODE_INSERTED, this.nodeInsertedListener, true);
        this.eventTarget.addEventListener(BetterFormEventNames.NODE_DELETED, this.nodeDeletedListener, true);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.eventTarget.removeEventListener(BetterFormEventNames.NODE_INSERTED, this.nodeInsertedListener, true);
        this.eventTarget.removeEventListener(BetterFormEventNames.NODE_DELETED, this.nodeDeletedListener, true);
        this.eventTarget = null;

        this.nodeInsertedListener = null;
        this.nodeDeletedListener = null;

        super.tearDown();
    }


    protected String getTestCaseURI() {
        return "InstanceTestMissingEmptyNS.xhtml";  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
