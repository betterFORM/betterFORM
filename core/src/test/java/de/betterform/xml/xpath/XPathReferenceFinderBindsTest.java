/*
 * Copyright (c) 2013. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xpath;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;

/**
 * @author Joern Turner
 * @author Robert Netzschwitz
 */
public class XPathReferenceFinderBindsTest extends XMLTestBase {

  private XFormsProcessorImpl xformsProcesssorImpl;

  public void testSimpleConstraints() throws Exception {
    assertInvalid("a");
    assertInvalid("b");
    assertInvalid("c");
    assertInvalid("d");
  }

  public void testUnionAndSequenceReferencesWithConstraints() throws Exception {
    assertInvalid("e");
    assertInvalid("f");
  }

  public void testQuantifiedExpressionsAsConstraints() throws Exception {
    assertInvalid("i");
    assertInvalid("j");
  }

  private void assertInvalid(String input) throws XFormsException {
    Document doc = this.xformsProcesssorImpl.getContainer().getDocument();
    String valid = XPathUtil.evaluateAsString(doc, "//xf:input[@id='" + input + "']/bf:data/@bf:valid").trim();
    String msg = XPathUtil.evaluateAsString(doc, "//xf:input[@id='" + input + "']/xf:alert").trim();
    assertFalse(msg, Boolean.getBoolean(valid));
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
