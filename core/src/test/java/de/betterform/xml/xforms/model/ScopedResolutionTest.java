package de.betterform.xml.xforms.model;

import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Element;


/**
 * Test cases for repeated model items.
 *
 * @author Joern Turner
 */
public class ScopedResolutionTest extends XMLTestBase {

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Element element;

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();

//        this.element = this.processor.getContainer().lookup("repeat-1").getElement();
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

    /**
     * Tests init.
     *
     * @throws Exception if any error occurrerd during the test.
     */
    public void testPlainInputBinding() throws Exception {
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("ScopedResolution-1-Test.xhtml"));
        this.xformsProcesssorImpl.init();

        assertEquals("1", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@id='group-1']/xf:input/bf:data"));
        assertEquals("1", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@id='group-2']/xf:input/bf:data"));
        assertEquals("1", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@id='group-3']/xf:input/bf:data"));
        assertEquals("1", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@id='group-4']/xf:input/bf:data"));
    }

    public void testRepeatBindingWithBind() throws Exception {
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("ScopedResolution-2-Test.xhtml"));
        this.xformsProcesssorImpl.init();

        assertEquals("1", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][1]/xf:input[1]/bf:data"));
        assertEquals("one", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][1]/xf:input[2]/bf:data"));
        assertEquals("one", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][1]/xf:input[3]/bf:data"));

        assertEquals("2", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][2]/xf:input[1]/bf:data"));
        assertEquals("two", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][2]/xf:input[2]/bf:data"));
        assertEquals("two", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][2]/xf:input[3]/bf:data"));

        assertEquals("3", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][3]/xf:input[1]/bf:data"));
        assertEquals("three", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][3]/xf:input[2]/bf:data"));
        assertEquals("three", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][3]/xf:input[3]/bf:data"));

        assertEquals("4", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][4]/xf:input[1]/bf:data"));
        assertEquals("four", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][4]/xf:input[2]/bf:data"));
        assertEquals("four", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][4]/xf:input[3]/bf:data"));

    }

    public void testRepeatBindingToNonDefaultInstance() throws Exception{
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("ScopedResolution-3-Test.xhtml"));
        this.xformsProcesssorImpl.init();
        // dump(xformsProcesssorImpl.getXForms().getDocumentElement());
                   
        assertEquals("1", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][1]/xf:input[1]/bf:data"));
        assertEquals("one", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][1]/xf:input[2]/bf:data"));
        assertEquals("one", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][1]/xf:input[3]/bf:data"));

        assertEquals("2", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][2]/xf:input[1]/bf:data"));
        assertEquals("two", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][2]/xf:input[2]/bf:data"));
        assertEquals("two", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][2]/xf:input[3]/bf:data"));

        assertEquals("3", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][3]/xf:input[1]/bf:data"));
        assertEquals("three", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][3]/xf:input[2]/bf:data"));
        assertEquals("three", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][3]/xf:input[3]/bf:data"));

        assertEquals("4", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][4]/xf:input[1]/bf:data"));
        assertEquals("four", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][4]/xf:input[2]/bf:data"));
        assertEquals("four", XPathUtil.evaluateAsString(xformsProcesssorImpl.getXForms().getDocumentElement(),"//xf:group[@appearance='repeated'][4]/xf:input[3]/bf:data"));



    }
  /*  public void testSetValueOnBoundAttributes() throws Exception{
        this.processor.setXForms(getClass().getResourceAsStream("ScopedResolution-4-Test.xhtml"));
        this.processor.init();
        dump(processor.getXForms().getDocumentElement());
        fail();                   

    }*/

    public void testNestedBinds() throws Exception{
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("nestedBinds.xhtml"));
        this.xformsProcesssorImpl.init();
        // dump(xformsProcesssorImpl.getXForms().getDocumentElement());

    }

}
