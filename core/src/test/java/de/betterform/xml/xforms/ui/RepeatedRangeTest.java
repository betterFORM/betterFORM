package de.betterform.xml.xforms.ui;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.Document;

/**
 * betterFORM Project
 * User: Tobi Krebs
 * Date: 23.02.12
 * Time: 13:48
 */
public class RepeatedRangeTest extends BetterFormTestCase {
    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected String getTestCaseURI() {
        return "RepeatedRange.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

    public void test1() throws Exception{
        Document xformDoc = (Document) this.processor.getXForms();

        assertEquals(1, (int)evaluateInDefaultContextAsDouble("count(/data/repeat)"));
        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/repeat/range)"));

        ModelItem modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/repeat[1]/range[1]"));
        assertEquals("integer", modelItem.getDeclarationView().getDatatype());
        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/repeat[1]/range[2]"));
        assertEquals("integer", modelItem.getDeclarationView().getDatatype());

        this.processor.dispatch("trigger", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/repeat)"));
        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/repeat[1]/range)"));
        assertEquals(2, (int)evaluateInDefaultContextAsDouble("count(/data/repeat[2]/range)"));

        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/repeat[1]/range[1]"));
        assertEquals("integer", modelItem.getDeclarationView().getDatatype());
        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/repeat[1]/range[2]"));
        assertEquals("integer", modelItem.getDeclarationView().getDatatype());
        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/repeat[2]/range[1]"));
        assertEquals("integer", modelItem.getDeclarationView().getDatatype());
        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/repeat[2]/range[2]"));
        assertEquals("integer", modelItem.getDeclarationView().getDatatype());


        assertEquals(1, (int)evaluateInInstanceAsDouble("i-float", "count(/data/repeat)"));
        assertEquals(2, (int)evaluateInInstanceAsDouble("i-float", "count(/data/repeat/range)"));


        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInInstanceAsNode("i-float", "/data/repeat[1]/range[1]"));
        assertEquals("float", modelItem.getDeclarationView().getDatatype());
        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInInstanceAsNode("i-float", "/data/repeat[1]/range[2]"));
        assertEquals("float", modelItem.getDeclarationView().getDatatype());


        this.processor.dispatch("trigger-float", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInInstanceAsDouble("i-float", "count(/data/repeat)"));
        assertEquals(2, (int)evaluateInInstanceAsDouble("i-float", "count(/data/repeat[1]/range)"));
        assertEquals(2, (int)evaluateInInstanceAsDouble("i-float", "count(/data/repeat[2]/range)"));

        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInInstanceAsNode("i-float", "/data/repeat[1]/range[1]"));
        assertEquals("float", modelItem.getDeclarationView().getDatatype());
        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInInstanceAsNode("i-float", "/data/repeat[1]/range[2]"));
        assertEquals("float", modelItem.getDeclarationView().getDatatype());
        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInInstanceAsNode("i-float", "/data/repeat[2]/range[1]"));
        assertEquals("float", modelItem.getDeclarationView().getDatatype());
        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInInstanceAsNode("i-float", "/data/repeat[2]/range[2]"));
        assertEquals("float", modelItem.getDeclarationView().getDatatype());

        assertEquals(1, (int)evaluateInInstanceAsDouble("i-float-deeper", "count(/data/repeat/repeat)"));
        assertEquals(2, (int)evaluateInInstanceAsDouble("i-float-deeper", "count(/data/repeat/repeat/range)"));


        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInInstanceAsNode("i-float-deeper", "/data/repeat/repeat[1]/range[1]"));
        assertEquals("float", modelItem.getDeclarationView().getDatatype());
        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInInstanceAsNode("i-float-deeper", "/data/repeat/repeat[1]/range[2]"));
        assertEquals("float", modelItem.getDeclarationView().getDatatype());


        this.processor.dispatch("trigger-float-deeper", DOMEventNames.ACTIVATE);

        assertEquals(2, (int)evaluateInInstanceAsDouble("i-float-deeper", "count(/data/repeat/repeat)"));
        assertEquals(2, (int)evaluateInInstanceAsDouble("i-float-deeper", "count(/data/repeat/repeat[1]/range)"));
        assertEquals(2, (int)evaluateInInstanceAsDouble("i-float-deeper", "count(/data/repeat/repeat[2]/range)"));

        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInInstanceAsNode("i-float-deeper", "/data/repeat/repeat[1]/range[1]"));
        assertEquals("float", modelItem.getDeclarationView().getDatatype());
        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInInstanceAsNode("i-float-deeper", "/data/repeat/repeat[1]/range[2]"));
        assertEquals("float", modelItem.getDeclarationView().getDatatype());
        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInInstanceAsNode("i-float-deeper", "/data/repeat/repeat[2]/range[1]"));
        assertEquals("float", modelItem.getDeclarationView().getDatatype());
        modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInInstanceAsNode("i-float-deeper", "/data/repeat/repeat[2]/range[2]"));
        assertEquals("float", modelItem.getDeclarationView().getDatatype());

    }
}
