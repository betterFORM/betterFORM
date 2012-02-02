/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.ui;

import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.action.XFormsAction;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import junit.framework.TestCase;
import org.w3c.dom.Document;

/**
 * Tests the repeat index.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RepeatIndexTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class RepeatIndexTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private Repeat enclosingRepeat;
    private Repeat nestedRepeat1;
    private Repeat nestedRepeat2;
    private Repeat nestedRepeat3;
    private Text input11;
    private Text input12;
    private Text input21;
    private Text input22;
    private Text input31;
    private Text input32;
    private XFormsAction action11;
    private XFormsAction action12;
    private XFormsAction action21;
    private XFormsAction action22;
    private XFormsAction action31;
    private XFormsAction action32;

    /**
     * Tests setting the enclosing repeat index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetEnclosingIndex1() throws Exception {
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).setIndex(1);

        assertEquals(1, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).getIndex());
        assertEquals(1, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).getIndex());
        assertEquals(this.enclosingRepeat, this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat"));
        assertEquals(this.nestedRepeat1, this.xformsProcesssorImpl.getContainer().lookup("nested-repeat"));
        assertEquals(this.input11, this.xformsProcesssorImpl.getContainer().lookup("input"));
        assertEquals(this.action11, this.xformsProcesssorImpl.getContainer().lookup("action"));
    }

    /**
     * Tests setting the enclosing repeat index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetEnclosingIndex2() throws Exception {
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).setIndex(2);

        assertEquals(2, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).getIndex());
        assertEquals(1, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).getIndex());
        assertEquals(this.enclosingRepeat, this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat"));
        assertEquals(this.nestedRepeat2, this.xformsProcesssorImpl.getContainer().lookup("nested-repeat"));
        assertEquals(this.input21, this.xformsProcesssorImpl.getContainer().lookup("input"));
        assertEquals(this.action21, this.xformsProcesssorImpl.getContainer().lookup("action"));
    }

    /**
     * Tests setting the enclosing repeat index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetEnclosingIndex3() throws Exception {
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).setIndex(3);

        assertEquals(3, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).getIndex());
        assertEquals(1, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).getIndex());
        assertEquals(this.enclosingRepeat, this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat"));
        assertEquals(this.nestedRepeat3, this.xformsProcesssorImpl.getContainer().lookup("nested-repeat"));
        assertEquals(this.input31, this.xformsProcesssorImpl.getContainer().lookup("input"));
        assertEquals(this.action31, this.xformsProcesssorImpl.getContainer().lookup("action"));
    }

    /**
     * Tests setting the nested repeat index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetNestedIndex11() throws Exception {
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).setIndex(1);
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).setIndex(1);

        assertEquals(1, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).getIndex());
        assertEquals(1, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).getIndex());
        assertEquals(this.enclosingRepeat, this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat"));
        assertEquals(this.nestedRepeat1, this.xformsProcesssorImpl.getContainer().lookup("nested-repeat"));
        assertEquals(this.input11, this.xformsProcesssorImpl.getContainer().lookup("input"));
        assertEquals(this.action11, this.xformsProcesssorImpl.getContainer().lookup("action"));
    }

    /**
     * Tests setting the nested repeat index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetNestedIndex12() throws Exception {
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).setIndex(1);
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).setIndex(2);

        assertEquals(1, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).getIndex());
        assertEquals(2, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).getIndex());
        assertEquals(this.enclosingRepeat, this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat"));
        assertEquals(this.nestedRepeat1, this.xformsProcesssorImpl.getContainer().lookup("nested-repeat"));
        assertEquals(this.input12, this.xformsProcesssorImpl.getContainer().lookup("input"));
        assertEquals(this.action12, this.xformsProcesssorImpl.getContainer().lookup("action"));
    }

    /**
     * Tests setting the nested repeat index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetNestedIndex21() throws Exception {
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).setIndex(2);
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).setIndex(1);

        assertEquals(2, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).getIndex());
        assertEquals(1, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).getIndex());
        assertEquals(this.enclosingRepeat, this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat"));
        assertEquals(this.nestedRepeat2, this.xformsProcesssorImpl.getContainer().lookup("nested-repeat"));
        assertEquals(this.input21, this.xformsProcesssorImpl.getContainer().lookup("input"));
        assertEquals(this.action21, this.xformsProcesssorImpl.getContainer().lookup("action"));
    }

    /**
     * Tests setting the nested repeat index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetNestedIndex22() throws Exception {
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).setIndex(2);
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).setIndex(2);

        assertEquals(2, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).getIndex());
        assertEquals(2, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).getIndex());
        assertEquals(this.enclosingRepeat, this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat"));
        assertEquals(this.nestedRepeat2, this.xformsProcesssorImpl.getContainer().lookup("nested-repeat"));
        assertEquals(this.input22, this.xformsProcesssorImpl.getContainer().lookup("input"));
        assertEquals(this.action22, this.xformsProcesssorImpl.getContainer().lookup("action"));
    }

    /**
     * Tests setting the nested repeat index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetNestedIndex31() throws Exception {
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).setIndex(3);
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).setIndex(1);

        assertEquals(3, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).getIndex());
        assertEquals(1, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).getIndex());
        assertEquals(this.enclosingRepeat, this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat"));
        assertEquals(this.nestedRepeat3, this.xformsProcesssorImpl.getContainer().lookup("nested-repeat"));
        assertEquals(this.input31, this.xformsProcesssorImpl.getContainer().lookup("input"));
        assertEquals(this.action31, this.xformsProcesssorImpl.getContainer().lookup("action"));
    }

    /**
     * Tests setting the nested repeat index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetNestedIndex32() throws Exception {
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).setIndex(3);
        ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).setIndex(2);

        assertEquals(3, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat")).getIndex());
        assertEquals(2, ((Repeat) this.xformsProcesssorImpl.getContainer().lookup("nested-repeat")).getIndex());
        assertEquals(this.enclosingRepeat, this.xformsProcesssorImpl.getContainer().lookup("enclosing-repeat"));
        assertEquals(this.nestedRepeat3, this.xformsProcesssorImpl.getContainer().lookup("nested-repeat"));
        assertEquals(this.input32, this.xformsProcesssorImpl.getContainer().lookup("input"));
        assertEquals(this.action32, this.xformsProcesssorImpl.getContainer().lookup("action"));
    }

    /**
     * Tests setting the enclosing index implicitely by setting the nested repeat index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetImplicitIndex1() throws Exception {
        this.nestedRepeat1.setIndex(1);

        assertEquals(1, this.enclosingRepeat.getIndex());
    }

    /**
     * Tests setting the enclosing index implicitely by setting the nested repeat index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetImplicitIndex2() throws Exception {
        this.nestedRepeat2.setIndex(1);

        assertEquals(2, this.enclosingRepeat.getIndex());
    }

    /**
     * Tests setting the enclosing index implicitely by setting the nested repeat index.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSetImplicitIndex3() throws Exception {
        this.nestedRepeat3.setIndex(1);

        assertEquals(3, this.enclosingRepeat.getIndex());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("RepeatIndexTest.xhtml"));
        this.xformsProcesssorImpl.init();

        Document host = this.xformsProcesssorImpl.getXForms();
        this.enclosingRepeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "//xf:repeat[bf:data][1]/@id"));
        this.nestedRepeat1 = (Repeat) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:repeat[2][bf:data]/@id"));
        this.nestedRepeat2 = (Repeat) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:repeat[3][bf:data]/@id"));
        this.nestedRepeat3 = (Repeat) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:repeat[4][bf:data]/@id"));


        this.input11 = (Text) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:input[1]/@id"));
        this.input12 = (Text) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:input[2]/@id"));

        this.input21 = (Text) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:input[4]/@id"));
        this.input22 = (Text) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:input[5]/@id"));

        this.input31 = (Text) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:input[7]/@id"));
        this.input32 = (Text) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:input[8]/@id"));

        this.action11 = (XFormsAction) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:action[1]/@id"));
        this.action12 = (XFormsAction) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:action[2]/@id"));

        this.action21 = (XFormsAction) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:action[4]/@id"));
        this.action22 = (XFormsAction) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:action[5]/@id"));

        this.action31 = (XFormsAction) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:action[7]/@id"));
        this.action32 = (XFormsAction) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:action[8]/@id"));
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.action11 = null;
        this.action12 = null;
        this.action21 = null;
        this.action22 = null;
        this.action31 = null;
        this.action32 = null;

        this.input11 = null;
        this.input12 = null;
        this.input21 = null;
        this.input22 = null;
        this.input31 = null;
        this.input32 = null;

        this.enclosingRepeat = null;
        this.nestedRepeat1 = null;
        this.nestedRepeat2 = null;
        this.nestedRepeat3 = null;

        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

}

// end of class
