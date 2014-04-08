/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model.bind;

import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Test cases for binding contexts.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: BindingTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class TypeCombineBindingExceptionTest extends XMLTestBase {
	static {
		org.apache.log4j.BasicConfigurator.configure();
	}

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document doc;

    public TypeCombineBindingExceptionTest(String name) {
        super(name);
    }


    public void testTypeCombineStandard() throws Exception{
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(this.doc);

        try{
            this.xformsProcesssorImpl.init();
        }catch (XFormsBindingException e){
            assertNotNull(e);
            assertEquals("xforms-binding-exception: property 'type' already present at model item::/html:html[1]/html:head[1]/xf:model[1]/xf:bind[2]",e.getMessage());
        }
    }

    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        this.doc = builder.parse(getClass().getResourceAsStream("TypeCombineBindingExceptionTest.xhtml"));
    }

    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }


}

// end of class
