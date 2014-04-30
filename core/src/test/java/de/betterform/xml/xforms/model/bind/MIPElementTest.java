/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.model.bind;

import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
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
public class MIPElementTest extends XMLTestBase {
//	static {
//		org.apache.log4j.BasicConfigurator.configure();
//	}

    private XFormsProcessorImpl xformsProcesssorImpl;

    public MIPElementTest(String name) {
        super(name);
    }

    public void testMIPElements() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("aBind");
        assertNotNull(bind);
        assertEquals("string-length(.) gt 1 or ../b = 1",bind.getReadonly());
        assertEquals("true()",bind.getRequired());
        assertEquals("true()",bind.getRelevant());
        assertNull(bind.getCalculate());
    }

    public void testTypeCombination()throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("fBind");
        assertNotNull(bind);
        assertNull(bind.getDatatype());
    }

    public void testConstraintCombination() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("dBind");
        assertNotNull(bind);
        assertEquals("number(.) lt 20 and number(.) gt 5",bind.getConstraint());
    }


    public void testStandardSyntax() throws Exception{
        Bind bind = (Bind) xformsProcesssorImpl.getContainer().lookup("cBind");
        assertNotNull(bind);
        assertEquals("true()",bind.getReadonly());
        assertEquals("true()",bind.getRequired());
        assertEquals("true()",bind.getRelevant());
        assertEquals("true()",bind.getConstraint());
    }

    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(getClass().getResourceAsStream("MIPElementTest.xhtml"));

        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(document);
        this.xformsProcesssorImpl.init();
    }

    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }


}

// end of class
