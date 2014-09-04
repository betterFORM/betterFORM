/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms;

import de.betterform.xml.config.Config;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.action.EventCountListener;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;

import java.io.File;


/**

 *
 * @author Joern Turner
 * @author Lars Windauer
 *

 */
public class ModelProcessorNoSuccessTest extends BetterFormTestCase {
    private TestEventListener messageListener;
    private EventCountListener invalidCountListener;
    private TestEventListener invalidListener;


    protected String getTestCaseURI() {
        return "ModelProcessorNoSuccess.html";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        Config.getInstance(Config.class.getResource("default.xml").getPath());
        this.processor = new ModelProcessor();

        File f;
        String testCaseURI = getTestCaseURI();
        if(testCaseURI.startsWith("conform:")){
            testCaseURI = testCaseURI.substring(8);
            String relative2file = new File(relativePath, testCaseURI).toString();
            //todo: set a default config
            f = new File(relative2file);
        }else{
            f = new File(getClass().getResource(testCaseURI).getPath());
        }
        String s = f.getCanonicalPath();
        this.processor.setXForms(getXmlResource(f.toString()));
        processor.setBaseURI(f.getParentFile().toURI().toString());

        preInit();
        this.processor.init();

        this.defaultContext = getDefaultContext();
        this.defaultFunctionContext = getDefaultFunctionContext();
        this.documentContext = de.betterform.xml.xpath.impl.saxon.XPathUtil.getRootContext((Document) this.processor.getXForms(), this.processor.getBaseURI());
    }
    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {

    }

    /**
     * Tests a modal message.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidity() throws Exception {
        ModelProcessor mp = (ModelProcessor) this.processor;
        assertFalse(mp.isSuccess());
        assertTrue(mp.getErrors().size() != 0);


        assertTrue(mp.getErrors().get(0).getPath().equals("/data[1]/trackedDate[1]"));
        assertTrue(mp.getErrors().get(0).getErrorType() == ModelProcessor.ErrorInfo.DATATYPE_INVALID);

        assertTrue(mp.getErrors().get(1).getPath().equals("/data[1]/duration[1]"));
        assertTrue(mp.getErrors().get(1).getErrorType() == ModelProcessor.ErrorInfo.REQUIRED_INVALID);

        assertTrue(mp.getErrors().get(2).getPath().equals("/data[1]/project[1]"));
        assertTrue(mp.getErrors().get(2).getErrorType() == ModelProcessor.ErrorInfo.CONSTRAINT_INVALID);


    }


}
