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
