/*
 * Copyright (c) 2013. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xpath;

import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author Joern Turner
 * @author Robert Netzschwitz
 */
public class XPathReferenceFinderBindsTest extends XMLTestBase {

  private XFormsProcessorImpl xformsProcesssorImpl;
  
//  public void testDocumentDump() throws Exception {
//    Document doc = this.xformsProcesssorImpl.getContainer().getDocument();
//    dump(doc);
//  }
  
  public void testSimpleConstraints() throws Exception {
    assertInvalid("a");
    assertInvalid("b");
    assertInvalid("c2");
    assertInvalid("c3");
    assertInvalid("d");
  }

  public void testUnionAndSequenceReferencesWithConstraints() throws Exception {
    assertInvalid("e");
    assertInvalid("f");
  }

  public void testQuantifiedAndForExpressionsAsConstraints() throws Exception {
    assertInvalid("i");
    assertInvalid("j");
    assertInvalid("n2");
  }
  
  public void testNodeComparisons() throws Exception {
    assertInvalid("k1");
    assertInvalid("l1");
    assertInvalid("m");
  }

    public void testNodeRelevant() throws Exception {
        assertRelevant("relevantTest");
    }

  private void assertInvalid(String input) throws XFormsException {
    Document doc = this.xformsProcesssorImpl.getContainer().getDocument();
    String valid = XPathUtil.evaluateAsString(doc, "//xf:input[@id='" + input + "']/bf:data/@valid");
    String msg = XPathUtil.evaluateAsString(doc, "//xf:input[@id='" + input + "']/xf:alert");
    assertNotNull("Cannot find '//xf:input[@id='" + input + "']", valid);
    assertFalse(msg, Boolean.parseBoolean(valid));
  }

   private void assertRelevant(String input) throws XFormsException {
        Document doc = this.xformsProcesssorImpl.getContainer().getDocument();
        String relevant = XPathUtil.evaluateAsString(doc, "//xf:input[@id='" + input + "']/bf:data/@enabled");
        assertEquals("true",  relevant);
   }

  protected void setUp() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(false);
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document document = builder.parse(getClass().getResourceAsStream("XPathReferenceFinderBindsTest.xhtml"));

    this.xformsProcesssorImpl = new XFormsProcessorImpl();
    this.xformsProcesssorImpl.setXForms(document);
    this.xformsProcesssorImpl.init();
  }

  protected void tearDown() throws Exception {
    this.xformsProcesssorImpl.shutdown();
    this.xformsProcesssorImpl = null;
  }
}
