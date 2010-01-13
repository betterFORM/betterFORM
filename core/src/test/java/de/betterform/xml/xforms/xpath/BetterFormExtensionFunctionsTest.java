// Copyright 2010 betterForm
package de.betterform.xml.xforms.xpath;

import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Tests betterform extension functions.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: BetterFormExtensionFunctionsTest.java 3492 2008-08-27 12:37:01Z joern $
 */
public class BetterFormExtensionFunctionsTest extends BetterFormTestCase {


    public void testContextNew() throws Exception {
//        assertEquals("hello", XPathUtil.evaluateAsString(processor.getXForms(),"bffn:foo()"));
        assertEquals("bar", evaluateInDefaultContextAsString("string(bffn:app-context('foo'))"));
        assertEquals("bar", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests the bf:context() extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContext() throws Exception {
        assertEquals("", evaluateInDefaultContextAsString("string(bffn:app-context('plain-property'))"));
        assertEquals("default", evaluateInDefaultContextAsString("string(bffn:app-context('plain-property', 'default'))"));
        assertEquals("default", evaluateInDefaultContextAsString("string(bffn:app-context('plain-property', /data/default))"));

        String plainProperty = String.valueOf(System.currentTimeMillis());
        this.processor.setContextParam("plain-property", plainProperty);

        assertEquals(plainProperty, evaluateInDefaultContextAsString("string(bffn:app-context('plain-property'))"));
        assertEquals("bar", evaluateInDefaultContextAsString("string(/data/item[1])"));

        getDefaultModel().rebuild();
        getDefaultModel().recalculate();

        assertEquals(plainProperty, evaluateInDefaultContextAsString("string(bffn:app-context('plain-property'))"));
        assertEquals(plainProperty, evaluateInDefaultContextAsString("string(/data/item[2])"));
    }

    /**
     * Tests the bf:context() extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextNested() throws Exception {
        assertEquals("", evaluateInDefaultContextAsString("string(bffn:app-context('map/nested-property'))"));
        assertEquals("default", evaluateInDefaultContextAsString("string(bffn:app-context('map/nested-property', 'default'))"));
        assertEquals("default", evaluateInDefaultContextAsString("string(bffn:app-context('map/nested-property', /data/default))"));

        String nestedProperty = String.valueOf(System.currentTimeMillis());
        HashMap map = new HashMap();
        map.put("nested-property", nestedProperty);
        this.processor.setContextParam("map", map);

        assertEquals(nestedProperty, evaluateInDefaultContextAsString("string(bffn:app-context('map/nested-property'))"));
        assertEquals("", evaluateInDefaultContextAsString("string(/data/item[3])"));

         getDefaultModel().rebuild();
         getDefaultModel().recalculate();

        assertEquals(nestedProperty, evaluateInDefaultContextAsString("string(bffn:app-context('map/nested-property'))"));
        assertEquals(nestedProperty, evaluateInDefaultContextAsString("string(/data/item[3])"));
    }

    /**
     * Tests the bf:context() extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextNodeset() throws Exception {
        String xml = "<?xml version='1.0' encoding='UTF-8'?><data><item>test</item></data>";
        InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
        document.getDocumentElement().getFirstChild().getFirstChild().getFirstChild();
        stream.close();
        this.processor.setContextParam("document", document);

        assertEquals("test", evaluateInDefaultContextAsString("string(bffn:app-context('document')/data/item)"));
        assertEquals("", evaluateInDefaultContextAsString("string(/data/item[4])"));

        getDefaultModel().rebuild();
        getDefaultModel().recalculate();

        assertEquals("test", evaluateInDefaultContextAsString("string(bffn:app-context('document')/data/item)"));
        assertEquals("test", evaluateInDefaultContextAsString("string(/data/item[4])"));
    }

    /**
     * Tests the bf:match() extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testMatch() throws Exception {
        assertEquals("true", evaluateInDocumentContextAsString("string(//xf:output[@id='i-match1']/bf:data)"));
        assertEquals("false", evaluateInDocumentContextAsString("string(//xf:output[@id='i-match2']/bf:data)"));
        assertEquals("true", evaluateInDocumentContextAsString("string(//xf:output[@id='i-match3']/bf:data)"));
    }


    /**
     * Tests the bf:fileSize() extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testFilesize() throws Exception {
        assertEquals(getClass().getResource("BetterFormExtensionFunctionsTest.xhtml").openConnection().getContentLength(), Long.parseLong(evaluateInDocumentContextAsString("string(//xf:output[@id='i-filesize']/bf:data/@bf:schema-value)")));
    }

    /**
     * Tests the bf:fileDate() extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testFileChangedate() throws Exception {
        assertEquals(new Date(getClass().getResource("BetterFormExtensionFunctionsTest.xhtml").openConnection().getLastModified()).toString(), new SimpleDateFormat("dd.MM.yyyy H:m:s").parse(evaluateInDocumentContextAsString("string(//xf:output[@id='i-filedate']/bf:data)")).toString());
    }


    protected void preInit() {
        processor.setContextParam("foo", "bar"); //sample app-context param

        processor.setBaseURI(getClass().getResource("BetterFormExtensionFunctionsTest.xhtml").toString());
    }


    protected XPathFunctionContext getDefaultFunctionContext() {
        try {
            return new XPathFunctionContext(getDefaultModel().getDefaultInstance());
        } catch (XFormsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }


    protected String getTestCaseURI() {
        return "BetterFormExtensionFunctionsTest.xhtml";
    }
}

// end of class
