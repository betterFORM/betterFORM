/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.xml.xforms.model.constraints;

import junit.framework.TestCase;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;

/**
 * Relevance selector test cases.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RelevanceSelectorTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class RelevanceSelectorTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;

        /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("RelevanceSelectorTest.xhtml"));
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

    /**
     * Test case 1 for relevance selection of submitted instance data.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelectRelevant1() throws Exception {
        Instance instance = this.xformsProcesssorImpl.getContainer().getDefaultModel().getDefaultInstance();
        Document document = RelevanceSelector.selectRelevant(instance);
        document.normalize();


        assertTrue("name(/*[1]) = 'root'", XPathUtil.evaluateAsString(document,"name(/*[1])").equals("root"));
        assertTrue("/root/@xmlns = ''", document.getDocumentElement().getAttribute("xmlns").equals(""));
//        assertTrue("/root/@xmlns:testutil = 'http://testutil.org/ns'", XPathUtil.evaluateAsString(document,"/root/@xmlns:testutil").equals("http://testutil.org/ns"));
//        assertEquals("http://testutil.org/ns",XPathUtil.evaluateAsString(document,"/root/@xmlns:testutil"));
        assertTrue("count(/root/*) = 3", XPathUtil.evaluateAsString(document,"count(/root/*)").equals("3"));

        assertTrue("name(/root/*[1]) = 'name'", XPathUtil.evaluateAsString(document,"name(/root/*[1])").toString().equals("name"));
        assertTrue("/root/name/@id = 'n-1'", XPathUtil.evaluateAsString(document,"/root/name/@id").toString().equals("n-1"));
        assertTrue("count(/root/name/*) = 0", XPathUtil.evaluateAsString(document,"count(/root/name/*)").equals("0"));

        assertTrue("name(/root/*[2]) = 'paragraph'", XPathUtil.evaluateAsString(document,"name(/root/*[2])").toString().equals("paragraph"));
        assertTrue("/root/paragraph[1]/@id = 'p-1'", XPathUtil.evaluateAsString(document,"/root/paragraph[1]/@id").toString().equals("p-1"));
        assertTrue("count(/root/paragraph[1]/*) = 1", XPathUtil.evaluateAsString(document,"count(/root/paragraph[1]/*)").equals("1"));

        assertTrue("name(/root/paragraph[1]/*[1]) = 'content'", XPathUtil.evaluateAsString(document,"name(/root/paragraph[1]/*[1])").toString().equals("content"));
        assertTrue("/root/paragraph[1]/content/@id = 'c-1'", XPathUtil.evaluateAsString(document,"/root/paragraph[1]/content/@id").toString().equals("c-1"));
        assertTrue("/root/paragraph[1]/content/text()[1] = 'some mixed content 1'", XPathUtil.evaluateAsString(document,"/root/paragraph[1]/content/text()[1]").replaceAll("[\n\r]", "").trim().equals("some mixed content 1"));
        assertTrue("/root/paragraph[1]/content/text()[2] = 'some more mixed content 1'", XPathUtil.evaluateAsString(document,"/root/paragraph[1]/content/text()[2]").replaceAll("[\n\r]", "").trim().equals("some more mixed content 1"));
        assertTrue("count(/root/paragraph[1]/content/*) = 1", XPathUtil.evaluateAsString(document,"count(/root/paragraph[1]/content/*)").equals("1"));

        assertTrue("name(/root/paragraph[1]/content/*[1]) = 'input'", XPathUtil.evaluateAsString(document,"name(/root/paragraph[1]/content/*[1])").toString().equals("input"));
        assertTrue("/root/paragraph[1]/content/input/@id = 'i-1'", XPathUtil.evaluateAsString(document,"/root/paragraph[1]/content/input/@id").toString().equals("i-1"));
        assertTrue("/root/paragraph[1]/content/input/text()[1] = 'input 1'", XPathUtil.evaluateAsString(document,"/root/paragraph[1]/content/input/text()[1]").toString().equals("input 1"));
        assertTrue("count(/root/paragraph[1]/content/input/*) = 0", XPathUtil.evaluateAsString(document,"count(/root/paragraph[1]/content/input/*)").equals("0"));

        assertTrue("name(/root/*[3]) = 'paragraph'", XPathUtil.evaluateAsString(document,"name(/root/*[3])").toString().equals("paragraph"));
        assertTrue("/root/paragraph[2]/@id = 'p-2'", XPathUtil.evaluateAsString(document,"/root/paragraph[2]/@id").toString().equals("p-2"));
        assertTrue("count(/root/paragraph[2]/*) = 1", XPathUtil.evaluateAsString(document,"count(/root/paragraph[2]/*)").equals("1"));

        assertTrue("name(/root/paragraph[2]/*[1]) = 'content'", XPathUtil.evaluateAsString(document,"name(/root/paragraph[2]/*[1])").toString().equals("content"));
        assertTrue("/root/paragraph[2]/content/@id = 'c-2'", XPathUtil.evaluateAsString(document,"/root/paragraph[2]/content/@id").toString().equals("c-2"));
        assertTrue("/root/paragraph[2]/content/text()[1] = 'some mixed content 2'", XPathUtil.evaluateAsString(document,"/root/paragraph[2]/content/text()[1]").replaceAll("[\n\r]", "").trim().equals("some mixed content 2"));
        assertTrue("/root/paragraph[2]/content/text()[2] = 'some more mixed content 2'", XPathUtil.evaluateAsString(document,"/root/paragraph[2]/content/text()[2]").replaceAll("[\n\r]", "").trim().equals("some more mixed content 2"));
        assertTrue("count(/root/paragraph[2]/content/*) = 1", XPathUtil.evaluateAsString(document,"count(/root/paragraph[2]/content/*)").equals("1"));

        assertTrue("name(/root/paragraph[2]/content/*[1]) = 'input'", XPathUtil.evaluateAsString(document,"name(/root/paragraph[2]/content/*[1])").toString().equals("input"));
        assertTrue("/root/paragraph[2]/content/input/@id = 'i-2'", XPathUtil.evaluateAsString(document,"/root/paragraph[2]/content/input/@id").toString().equals("i-2"));
        assertTrue("/root/paragraph[2]/content/input/text()[1] = 'input 2'", XPathUtil.evaluateAsString(document,"/root/paragraph[2]/content/input/text()[1]").toString().equals("input 2"));
        assertTrue("count(/root/paragraph[2]/content/input/*) = 0", XPathUtil.evaluateAsString(document,"count(/root/paragraph[2]/content/input/*)").equals("0"));
    }

    /**
     * Test case 2 for relevance selection of submitted instance data.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelectRelevant2() throws Exception {
        Instance instance = this.xformsProcesssorImpl.getContainer().getDefaultModel().getDefaultInstance();
        Document document = RelevanceSelector.selectRelevant(instance, "/root/paragraph");
        document.normalize();

        assertTrue("name(/*[1]) = 'paragraph'", XPathUtil.evaluateAsString(document,"name(/*[1])").equals("paragraph"));
        assertTrue("namespace-uri(/paragraph) = ''", XPathUtil.evaluateAsString(document,"namespace-uri(/paragraph)").equals(""));
        assertTrue("/paragraph/@id = 'p-1'", XPathUtil.evaluateAsString(document,"/paragraph/@id").equals("p-1"));
        assertTrue("count(/paragraph/*) = 1", XPathUtil.evaluateAsString(document,"count(/paragraph/*)").equals("1"));

        assertTrue("name(/paragraph/*[1]) = 'content'", XPathUtil.evaluateAsString(document,"name(/paragraph/*[1])").equals("content"));
        assertTrue("/paragraph/content/@id = 'c-1'", XPathUtil.evaluateAsString(document,"/paragraph/content/@id").equals("c-1"));
        assertTrue("/paragraph/content/text()[1] = 'some mixed content 1'", XPathUtil.evaluateAsString(document,"/paragraph/content/text()[1]").replaceAll("[\n\r]", "").trim().equals("some mixed content 1"));
        assertTrue("/paragraph/content/text()[2] = 'some more mixed content 1'", XPathUtil.evaluateAsString(document,"/paragraph/content/text()[2]").replaceAll("[\n\r]", "").trim().equals("some more mixed content 1"));
        assertTrue("count(/paragraph/content/*) = 1", XPathUtil.evaluateAsString(document,"count(/paragraph/content/*)").equals("1"));

        assertTrue("name(/paragraph/content/*[1]) = 'input'", XPathUtil.evaluateAsString(document,"name(/paragraph/content/*[1])").equals("input"));
        assertTrue("/paragraph/content/input/@id = 'i-1'", XPathUtil.evaluateAsString(document,"/paragraph/content/input/@id").equals("i-1"));
        assertTrue("/paragraph/content/input/text()[1] = 'input 1'", XPathUtil.evaluateAsString(document,"/paragraph/content/input/text()[1]").equals("input 1"));
        assertTrue("count(/paragraph/content/input/*) = 0", XPathUtil.evaluateAsString(document,"count(/paragraph/content/input/*)").equals("0"));
    }



}
