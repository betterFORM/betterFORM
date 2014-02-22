/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.ui;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.action.XFormsAction;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;

/**
 * Tests the repeat index for repeat-nodesets.
 *
 * Tested Markup:
 * <table>
 *   <tbody xf:repeat-nodeset="">
 *       <tr>
 *           <td>
 *               <xf:repeat>
 *                  ....
 *
 * @author Tobi Krebs&eacute;
 * @version $Id: RepeatNodesetIndexTest3.java $
 */
public class RepeatNodesetIndexTest1 extends RepeatIndexTest {
    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("RepeatNodesetIndexTest1.xhtml"));
        this.xformsProcesssorImpl.init();

        Document host = this.xformsProcesssorImpl.getXForms();
        DOMUtil.prettyPrintDOM(host);
        //xf:repeat[bf:data][1]/@id
        this.enclosingRepeat = (Repeat) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "//html:tbody[@xf:repeat-nodeset]/@id"));
        this.nestedRepeat1 = (Repeat) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:repeat[bf:data][1]/@id"));
        this.nestedRepeat2 = (Repeat) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:repeat[bf:data][2]/@id"));
        this.nestedRepeat3 = (Repeat) this.xformsProcesssorImpl.getContainer().lookup(XPathUtil.evaluateAsString(host, "descendant::xf:repeat[bf:data][3]/@id"));
        


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
