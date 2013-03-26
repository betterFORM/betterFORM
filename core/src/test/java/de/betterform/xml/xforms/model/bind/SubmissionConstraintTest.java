/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model.bind;

import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Test cases for submissions in combination with constraints
 *
 * @author Lars Windauer
 * @version $Id: SubmissionConstraintTest 3251 2013-03-26 09:26:03Z lasse $
 */
public class SubmissionConstraintTest extends XMLTestBase {
	static {
		org.apache.log4j.BasicConfigurator.configure();
	}

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document doc;

    public SubmissionConstraintTest(String name) {
        super(name);
    }

    public void testSubmissionConstraint() throws Exception{

        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(this.doc);

        try{
            this.xformsProcesssorImpl.init();
            Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("bind2check");
            assertNotNull(bind);

            assertEquals("string-length(.) > 3", bind.getConstraint());
            assertEquals("hallo" ,XPathUtil.evaluateAsString(doc, "//*[@id='input1']/bf:data") );
            assertEquals("true" ,XPathUtil.evaluateAsString(doc, "//*[@id='input1']/bf:data/@bf:valid") );



            this.xformsProcesssorImpl.dispatch("replaceInstance", "DOMActivate");
            assertEquals("h" ,XPathUtil.evaluateAsString(doc, "//*[@id='input1']/bf:data") );
            assertEquals("false" ,XPathUtil.evaluateAsString(doc, "//*[@id='input1']/bf:data/@bf:valid") );

        }catch (XFormsBindingException e){
            assertNotNull(e);
        }
    }

    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.doc = builder.parse(getClass().getResourceAsStream("SubmissionConstraint.xhtml"));

    }

    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }


}

// end of class
