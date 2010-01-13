// Copyright 2010 betterForm
package de.betterform.xml.xforms.ui;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import de.betterform.xml.dom.DOMUtil;
import org.w3c.dom.Document;

// end of class

/**
 * Test cases for repeated model items.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RepeatedModelItemsTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class RepeatedItemsetTest extends BetterFormTestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    public void testInsert() throws Exception{
        Document xformDoc = (Document) this.processor.getXForms();
        assertEquals("must be the same", "3", XPathUtil.evaluateAsString(xformDoc, "count(//repeated/item)"));
        this.processor.dispatch("t-insertItem", DOMEventNames.ACTIVATE);

        Model model = (Model) this.processor.getXFormsModel("model1");
        assertNotNull(model);

        Document instanceDocument = model.getInstanceDocument("instance1");
        assertNotNull(instanceDocument);
        assertEquals("must be the same", "4", XPathUtil.evaluateAsString(instanceDocument, "count(//repeated/item)"));
        // DOMUtil.prettyPrintDOM(this.processor.getXForms());


    }


    protected void setUp() throws Exception {
        super.setUp();

/*
        this.messageCountListener = new EventCountListener(BetterFormEventNames.RENDER_MESSAGE);
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.addEventListener(BetterFormEventNames.RENDER_MESSAGE, this.messageCountListener, true);
*/
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
/*
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.removeEventListener(BetterFormEventNames.RENDER_MESSAGE, this.messageCountListener, true);
        this.messageCountListener = null;
*/
        super.tearDown();
    }

    protected String getTestCaseURI() {
        return "RepeatedItemsetTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }
}
