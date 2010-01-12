package de.betterform.xml.xforms.ui;

import junit.framework.TestCase;
import de.betterform.xml.xforms.XFormsProcessorImpl;

/**
 * Tests the input, secret, textarea elements.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: TextTest.java 3240 2008-07-02 10:02:39Z lasse $
 */
public class TextTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;

    /**
     * Tests line-break normalization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLineBreakNormalization() throws Exception {
        this.xformsProcesssorImpl.setControlValue("textarea-1", "line\nbreak");
        assertEquals("line\nbreak", ((Text) this.xformsProcesssorImpl.getContainer().lookup("textarea-1")).getValue());

        this.xformsProcesssorImpl.setControlValue("textarea-1", "line\r\nbreak");
        assertEquals("line\nbreak", ((Text) this.xformsProcesssorImpl.getContainer().lookup("textarea-1")).getValue());

        this.xformsProcesssorImpl.setControlValue("textarea-1", "line\rbreak");
        assertEquals("line\nbreak", ((Text) this.xformsProcesssorImpl.getContainer().lookup("textarea-1")).getValue());

        this.xformsProcesssorImpl.setControlValue("textarea-1", "line\n\rbreak");
        assertEquals("line\n\nbreak", ((Text) this.xformsProcesssorImpl.getContainer().lookup("textarea-1")).getValue());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("TextTest.xhtml"));
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
    }

}
