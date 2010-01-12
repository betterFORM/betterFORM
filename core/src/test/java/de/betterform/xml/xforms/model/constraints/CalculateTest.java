package de.betterform.xml.xforms.model.constraints;

import junit.framework.TestCase;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.ui.Text;

/**
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: CalculateTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class CalculateTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;

    /**
     * Tests plain calculation.
     *
     * @throws Exception if an error occurred during the test.
     */
    public void testCalculatePlain() throws Exception {
        Text text11 = (Text) this.xformsProcesssorImpl.getContainer().lookup("i-11");
        Text text13 = (Text) this.xformsProcesssorImpl.getContainer().lookup("i-13");

        assertValue("4", text11);
        assertValue("3", text13);
    }


    /**
     * Tests the calculation example from
     * <a href="http://www.w3.org/TR/xforms/sliceD.html#rpm-processing-recalc-example">
     * XForms 1.0 (Second Edition)</a>.
     *
     * @throws Exception if an error occurred during the test.
     */
    public void testCalculateExampleFromSpec() throws Exception {
        Text text31 = (Text) this.xformsProcesssorImpl.getContainer().lookup("i-31");
        Text text32 = (Text) this.xformsProcesssorImpl.getContainer().lookup("i-32");
        Text text33 = (Text) this.xformsProcesssorImpl.getContainer().lookup("i-33");
        Text text34 = (Text) this.xformsProcesssorImpl.getContainer().lookup("i-34");

        assertValue("10", text31);
        assertValue("10", text32);
        assertValue("100", text33);
        assertValid(true, text33);
        assertValue("20", text34);
        assertValid(true, text34);

        text31.setValue("11");

        assertValue("11", text31);
        assertValue("10", text32);
        assertValue("110", text33);
        assertValid(false, text33);
        assertValue("21", text34);
        assertValid(false, text34);
    }

    /**
     * Tests the calculation of a non-existing node.
     *
     * @throws Exception if an error occurred during the test.
     */
    public void testCalculationOfNonExistingNode() throws Exception {
        Text text41 = (Text) this.xformsProcesssorImpl.getContainer().lookup("i-41");

        assertValue("", text41);
    }

    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("CalculateTest.xhtml"));
        this.xformsProcesssorImpl.init();
    }

    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

    private static void assertValue(String value, Text text) {
        assertEquals(value, text.getValue());
    }

    private static void assertValid(boolean valid, Text text) throws XFormsException {
        ModelItem modelItem = text.getModel().getInstance(text.getInstanceId()).getModelItem(text.getInstanceNode());
        assertEquals(valid, modelItem.isValid());
    }

}
