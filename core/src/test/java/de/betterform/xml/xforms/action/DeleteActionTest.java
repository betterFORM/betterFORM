/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.Document;

import java.util.Map;

/**
 * Test cases for the delete action.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: DeleteActionTest.java 3474 2008-08-15 22:29:43Z joern $
 */
public class DeleteActionTest extends BetterFormTestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private TestEventListener deleteListener;
    private TestEventListener rebuildListener;
    private TestEventListener recalulateListener;
    private TestEventListener revalidateListener;
    private TestEventListener refreshListener;

    /**
     * Tests deleting from an empty nodeset.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteFromEmptyNodeset() throws Exception {
        this.processor.dispatch("delete-from-empty-nodeset", DOMEventNames.ACTIVATE);

        assertEquals(3, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2", evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3", evaluateInDefaultContextAsString("string(/data/item[3])"));

        assertEquals(null, this.deleteListener.getId());
        assertEquals(null, this.deleteListener.getContext());
        assertEquals(null, this.rebuildListener.getId());
        assertEquals(null, this.recalulateListener.getId());
        assertEquals(null, this.revalidateListener.getId());
        assertEquals(null, this.refreshListener.getId());
        
        assertEquals("", evaluateInInstanceAsString("instance-event", "string(/data/delete-location)"));
        assertEquals("", evaluateInInstanceAsString("instance-event", "string(/data/nodes-deleted)"));
    }

    /**
     * Tests deleting from an empty nodeset.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteFromEmptyNodesetWithPredicate() throws Exception {
        this.processor.dispatch("delete-from-empty-nodeset-with-predicate", DOMEventNames.ACTIVATE);

        assertEquals(3, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2", evaluateInDefaultContextAsString("string(/data/item[2])"));
        assertEquals("My data 3", evaluateInDefaultContextAsString("string(/data/item[3])"));

        assertEquals(null, this.deleteListener.getId());
        assertEquals(null, this.deleteListener.getContext());
        assertEquals(null, this.rebuildListener.getId());
        assertEquals(null, this.recalulateListener.getId());
        assertEquals(null, this.revalidateListener.getId());
        assertEquals(null, this.refreshListener.getId());
        
        assertEquals("", evaluateInInstanceAsString("instance-event", "string(/data/delete-location)"));
        assertEquals("", evaluateInInstanceAsString("instance-event", "string(/data/nodes-deleted)"));
    }

    /**
     * Tests deleting at -1.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteAtNegative() throws Exception {
        this.processor.dispatch("delete-at-negative", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 2", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 3", evaluateInDefaultContextAsString("string(/data/item[2])"));

        assertEquals("instance-1", this.deleteListener.getId());
        assertEquals("/*[1]/item[1]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());

        Document eventInstance = this.processor.getXFormsModel("").getInstanceDocument("instance-event");

        Document instance1 = this.processor.getXFormsModel("").getInstanceDocument("instance-1");

        String deleteLocation = evaluateInInstanceAsString("instance-event", "string(/data/delete-location)");
        String nodesSelected = evaluateInInstanceAsString("instance-event", "string(/data/nodes-deleted)");
        assertEquals("1", deleteLocation);
        assertEquals("1", nodesSelected);
    }

    /**
     * Tests deleting at 0.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteAtZero() throws Exception {
        this.processor.dispatch("delete-at-zero", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 2", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 3", evaluateInDefaultContextAsString("string(/data/item[2])"));

        assertEquals("instance-1", this.deleteListener.getId());
        assertEquals("/*[1]/item[1]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
        
        assertEquals("1", evaluateInInstanceAsString("instance-event", "string(/data/delete-location)"));
        assertEquals("1", evaluateInInstanceAsString("instance-event", "string(/data/nodes-deleted)"));
    }
    
    /**
     * Tests deleting at 4.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteAtNonExisting() throws Exception {
        this.processor.dispatch("delete-at-non-existing", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2", evaluateInDefaultContextAsString("string(/data/item[2])"));

        assertEquals("instance-1", this.deleteListener.getId());
        assertEquals("/*[1]/item[3]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
        
        assertEquals("3", evaluateInInstanceAsString("instance-event", "string(/data/delete-location)"));
        assertEquals("1", evaluateInInstanceAsString("instance-event", "string(/data/nodes-deleted)"));

    }

    /**
     * Tests deleting at 1.5.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteAtFloat() throws Exception {
        this.processor.dispatch("delete-at-float", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 3", evaluateInDefaultContextAsString("string(/data/item[2])"));

        assertEquals("instance-1", this.deleteListener.getId());
        assertEquals("/*[1]/item[2]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
        
        assertEquals("2", evaluateInInstanceAsString("instance-event", "string(/data/delete-location)"));
        assertEquals(1d, evaluateInInstanceAsDouble("instance-event", "number(/data/nodes-deleted)"));
    }

    /**
     * Tests deleting at 'NaN'.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteAtNaN() throws Exception {
        this.processor.dispatch("delete-at-nan", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 2", evaluateInDefaultContextAsString("string(/data/item[2])"));

        assertEquals("instance-1", this.deleteListener.getId());
        assertEquals("/*[1]/item[3]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
        
        assertEquals("3", evaluateInInstanceAsString("instance-event", "string(/data/delete-location)"));
        assertEquals("1", evaluateInInstanceAsString("instance-event", "string(/data/nodes-deleted)"));
    }
    
    /**
     * Tests deleting without at.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteWithoutAt() throws Exception {
    	this.processor.dispatch("delete-whithout-at", DOMEventNames.ACTIVATE);
    	
    	assertEquals(0, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
    	
    	assertEquals("instance-1", this.deleteListener.getId());
    	assertEquals("/*[1]/item", ((Map)this.deleteListener.getContext()).get("path").toString());
    	assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
        
        assertEquals(Double.NaN, evaluateInInstanceAsDouble("instance-event", "number(/data/delete-location)"));
        assertEquals(3d, evaluateInInstanceAsDouble("instance-event", "number(/data/nodes-deleted)"));
    }

    /**
     * Tests deleting from another instance.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteFromOtherInstance() throws Exception {
        this.processor.dispatch("delete-from-other-instance", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInInstanceAsDouble("instance-2", "count(/data/item)"));
        assertEquals("My data 1", evaluateInInstanceAsString("instance-2", "string(/data/item[1])"));
        assertEquals("My data 3", evaluateInInstanceAsString("instance-2", "string(/data/item[2])"));

        assertEquals("instance-2", this.deleteListener.getId());
        assertEquals("instance('instance-2')/item[2]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
        
        assertEquals(2d, evaluateInInstanceAsDouble("instance-event", "number(/data/delete-location)"));
        assertEquals(1d, evaluateInInstanceAsDouble("instance-event", "number(/data/nodes-deleted)"));
    }
    
    /**
     * Tests deleting from another instance with context.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteFromOtherInstanceWithContext() throws Exception {
        this.processor.dispatch("delete-from-other-instance-with-context", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInInstanceAsDouble("instance-2", "count(/data/item)"));
        assertEquals("My data 1", evaluateInInstanceAsString("instance-2", "string(/data/item[1])"));
        assertEquals("My data 3", evaluateInInstanceAsString("instance-2", "string(/data/item[2])"));

        assertEquals("instance-2", this.deleteListener.getId());
        assertEquals("instance('instance-2')/item[2]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
        
        assertEquals(2d, evaluateInInstanceAsDouble("instance-event", "number(/data/delete-location)"));
        assertEquals(1d, evaluateInInstanceAsDouble("instance-event", "number(/data/nodes-deleted)"));
    }

    /**
     * Tests deleting with a predicate.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteWithPredicate() throws Exception {
        this.processor.dispatch("delete-with-predicate", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 3", evaluateInDefaultContextAsString("string(/data/item[2])"));

        assertEquals("instance-1", this.deleteListener.getId());
        assertEquals("/*[1]/item[.='My data 2'][1]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
        
        assertEquals(1d, evaluateInInstanceAsDouble("instance-event", "number(/data/delete-location)"));
        assertEquals(1d, evaluateInInstanceAsDouble("instance-event", "number(/data/nodes-deleted)"));
    }

    /**
     * Tests deleting with the index() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteWithIndexFunction() throws Exception {
        this.processor.setRepeatIndex("repeat", 2);
        this.processor.dispatch("delete-with-index-function", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 3", evaluateInDefaultContextAsString("string(/data/item[2])"));

        assertEquals("instance-1", this.deleteListener.getId());
        assertEquals("/*[1]/item[2]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
        
        assertEquals(2d, evaluateInInstanceAsDouble("instance-event", "number(/data/delete-location)"));
        assertEquals(1d, evaluateInInstanceAsDouble("instance-event", "number(/data/nodes-deleted)"));
    }

    /**
     * Tests deleting with the if() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteWithIfFunction() throws Exception {
        this.processor.dispatch("delete-with-if-function", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 2", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 3", evaluateInDefaultContextAsString("string(/data/item[2])"));

        assertEquals("instance-1", this.deleteListener.getId());
        assertEquals("/*[1]/item[1]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
        
        assertEquals(1d, evaluateInInstanceAsDouble("instance-event", "number(/data/delete-location)"));
        assertEquals(1d, evaluateInInstanceAsDouble("instance-event", "number(/data/nodes-deleted)"));

        this.deleteListener.clear();
        this.rebuildListener.clear();
        this.recalulateListener.clear();
        this.revalidateListener.clear();
        this.refreshListener.clear();

        this.processor.dispatch("delete-with-if-function", DOMEventNames.ACTIVATE);

        assertEquals(1, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 3", evaluateInDefaultContextAsString("string(/data/item[1])"));

        assertEquals("instance-1", this.deleteListener.getId());
        assertEquals("/*[1]/item[1]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
        
        assertEquals(1d, evaluateInInstanceAsDouble("instance-event", "number(/data/delete-location)"));
        assertEquals(1d, evaluateInInstanceAsDouble("instance-event", "number(/data/nodes-deleted)"));

        this.deleteListener.clear();
        this.rebuildListener.clear();
        this.recalulateListener.clear();
        this.revalidateListener.clear();
        this.refreshListener.clear();

        this.processor.dispatch("delete-with-if-function", DOMEventNames.ACTIVATE);

        assertEquals(0, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));

        assertEquals("instance-1", this.deleteListener.getId());
        assertEquals("/*[1]/item[1]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());        
    }

    /**
     * Tests deleting with a predicate and the last() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteWithPredicateAndLastFunction() throws Exception {
        this.processor.dispatch("delete-with-predicate-and-last-function", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInInstanceAsDouble("instance-2", "count(/data/item)"));
        assertEquals("My data 1", evaluateInInstanceAsString("instance-2", "string(/data/item[1])"));
        assertEquals("My data 3", evaluateInInstanceAsString("instance-2", "string(/data/item[2])"));

        assertEquals("instance-2", this.deleteListener.getId());
        assertEquals("instance('instance-2')/item[@mutable='1'][2]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
        
        assertEquals(2d, evaluateInInstanceAsDouble("instance-event", "number(/data/delete-location)"));
        assertEquals(1d, evaluateInInstanceAsDouble("instance-event", "number(/data/nodes-deleted)"));
    }

    /**
     * Tests deleting with a model binding.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDeleteWithBind() throws Exception {
        this.processor.dispatch("delete-with-bind", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/item)"));
        assertEquals("My data 1", evaluateInDefaultContextAsString("string(/data/item[1])"));
        assertEquals("My data 3", evaluateInDefaultContextAsString("string(/data/item[2])"));

        assertEquals("instance-1", this.deleteListener.getId());
        assertEquals("/*[1]/item[2]", ((Map)this.deleteListener.getContext()).get("path").toString());
        assertEquals("model-1", this.rebuildListener.getId());
        assertEquals("model-1", this.recalulateListener.getId());
        assertEquals("model-1", this.revalidateListener.getId());
        assertEquals("model-1", this.refreshListener.getId());
        
        assertEquals(2d, evaluateInInstanceAsDouble("instance-event", "number(/data/delete-location)"));
        assertEquals(1d, evaluateInInstanceAsDouble("instance-event", "number(/data/nodes-deleted)"));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.deleteListener = new TestEventListener();
        this.rebuildListener = new TestEventListener();
        this.recalulateListener = new TestEventListener();
        this.revalidateListener = new TestEventListener();
        this.refreshListener = new TestEventListener();

        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.addEventListener(XFormsEventNames.DELETE, this.deleteListener, true);
        eventTarget.addEventListener(XFormsEventNames.REBUILD, this.rebuildListener, true);
        eventTarget.addEventListener(XFormsEventNames.RECALCULATE, this.recalulateListener, true);
        eventTarget.addEventListener(XFormsEventNames.REVALIDATE, this.revalidateListener, true);
        eventTarget.addEventListener(XFormsEventNames.REFRESH, this.refreshListener, true);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.removeEventListener(XFormsEventNames.DELETE, this.deleteListener, true);
        eventTarget.removeEventListener(XFormsEventNames.REBUILD, this.rebuildListener, true);
        eventTarget.removeEventListener(XFormsEventNames.RECALCULATE, this.recalulateListener, true);
        eventTarget.removeEventListener(XFormsEventNames.REVALIDATE, this.revalidateListener, true);
        eventTarget.removeEventListener(XFormsEventNames.REFRESH, this.refreshListener, true);

        this.deleteListener = null;
        this.rebuildListener = null;
        this.recalulateListener = null;
        this.revalidateListener = null;
        this.refreshListener = null;

        super.tearDown();
    }
    
    protected String getTestCaseURI() {
        return "DeleteActionTest.xhtml";  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}

// end of class
