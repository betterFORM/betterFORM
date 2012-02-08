/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;
import org.w3c.xforms.XFormsModelElement;

/**
 * Test cases for xf:load action with show=embedded including cross model submissions
 *
 * @author Joern Turner
 * @author Lars Windauer
 * @version $Id:
 */
public class LoadActionEmbedReplaceTest extends BetterFormTestCase {
     private EventCountListener messageCountListener;
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }


    public void testLoadEmbedForms() throws Exception{
        this.processor.dispatch("t-load", DOMEventNames.ACTIVATE);
        Document xformDoc = (Document) this.processor.getXForms();

        XFormsModelElement childModel = this.processor.getXFormsModel("m-child-1");
        assertNotNull(childModel);
        Document childInstance = childModel.getInstanceDocument("i-child-1");
        // DOMUtil.prettyPrintDOM(childInstance);
        assertNotNull(childInstance);

        //unloading form again
        this.processor.dispatch("t-unload",DOMEventNames.ACTIVATE);
        try{
            childModel = this.processor.getXFormsModel("m-child-1");
        }catch (XFormsException e){
            assertNotNull(e);
        }

    }


    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.messageCountListener = new EventCountListener(BetterFormEventNames.RENDER_MESSAGE);
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.addEventListener(BetterFormEventNames.RENDER_MESSAGE, this.messageCountListener, true);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.removeEventListener(BetterFormEventNames.RENDER_MESSAGE, this.messageCountListener, true);
        this.messageCountListener = null;
        super.tearDown();
    }

    protected String getTestCaseURI() {
        return "LoadActionEmbedReplaceTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
