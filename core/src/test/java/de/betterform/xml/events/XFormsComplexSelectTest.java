package de.betterform.xml.events;

import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.action.EventCountListener;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import de.betterform.xml.dom.DOMUtil;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.Document;


/**

 *
 * @author Joern Turner
 * @author Lars Windauer
 *

 */
public class XFormsComplexSelectTest extends BetterFormTestCase {
    static {
        org.apache.log4j.BasicConfigurator.configure();
    }
    
    private EventCountListener valueChangeCountListener;

    protected String getTestCaseURI() {
        return "XFormsComplexSelectTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.valueChangeCountListener = new EventCountListener(XFormsEventNames.VALUE_CHANGED);
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.addEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangeCountListener, true);


    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.removeEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangeCountListener, true);
        valueChangeCountListener = null;        
    }


    /**
     * Tests a modal message.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelect1ValueChangeListener() throws Exception {
        this.processor.dispatch("t-changeValue", DOMEventNames.ACTIVATE);
        XFormsProcessorImpl bean = (XFormsProcessorImpl) this.processor;
        Document instance = bean.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument();
         DOMUtil.prettyPrintDOM(instance);
        assertEquals(2, this.valueChangeCountListener.getCount());

        assertEquals("true",XPathUtil.evaluateAsString(instance,"//result/select1"));
        assertEquals("true",XPathUtil.evaluateAsString(instance,"//result/select2"));



    }


}
