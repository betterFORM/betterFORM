/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.ui;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import junit.framework.TestCase;
import org.w3c.dom.Document;

public class NestedCaseTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document host;


    public void testNestedSwitch() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("NestedCaseTest.xhtml"));
        this.xformsProcesssorImpl.init();
        this.xformsProcesssorImpl.dispatch("activate-case-1-2", DOMEventNames.ACTIVATE);
        assertEquals("true", XPathUtil.evaluateAsString(this.xformsProcesssorImpl.getXForms(), "//xf:switch[@id eq 'nested-switch']/xf:case[2]/bf:data/@selected"));


    }

}