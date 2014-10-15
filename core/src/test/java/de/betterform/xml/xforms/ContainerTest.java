/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventTarget;

import java.util.Map;

/**
 * @author joern turner - <joernt@betterform.sourceforge.net>
 * @version $Id: ContainerTest.java 3471 2008-08-15 21:55:23Z joern $
 */
public class ContainerTest extends TestCase {
/*
    static {
        org.apache.log4j.BasicConfigurator.configure();
    }
*/

    private XFormsProcessorImpl processor;
    private TestEventListener versionEventListener;

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testGetDOM() throws Exception {
        processor.setXForms(getClass().getResourceAsStream("model-test.xml"));

        Document dom = processor.getContainer().getDocument();
        assertTrue(dom != null);
    }

//    public void testGetModels() throws Exception{
//        processor.setXForms(getXmlResource("model-test.xml"));
//        processor.init();
//        assertTrue(processor.getContainer().getModelList().size()==2);
//    }

    public void testGetDefaultModel() throws Exception {
        processor.setXForms(getClass().getResourceAsStream("model-test.xml"));
        processor.init();

        Model m = processor.getContainer().getDefaultModel();
        assertTrue(m.getId().equals("C1"));
    }

    /**
     * test that XForms 1.1 will be returned as version if there's no version attribute on model
     *
     * @throws Exception
     */
    public void testGetVersionNull() throws Exception{
        String path = getClass().getResource("buglet3.xml").getPath();
        this.processor.setXForms(getClass().getResourceAsStream("buglet3.xml"));
        this.processor.setBaseURI("file://" + path);
        this.processor.init();
        String version = this.processor.getContainer().getVersion();
        assertEquals(Container.XFORMS_1_1,version);
        this.processor.shutdown();

    }

    public void testGetVersion11()throws Exception{
        String path = getClass().getResource("buglet2.xml").getPath();
        this.processor.setXForms(getClass().getResourceAsStream("buglet2.xml"));
        this.processor.setBaseURI("file://" + path);

        this.versionEventListener = new TestEventListener();
        ((EventTarget)this.processor.getXForms().getDocumentElement()).addEventListener(XFormsEventNames.VERSION_EXCEPTION, this.versionEventListener, true);

        //initialize/bootstrap processor
        this.processor.init();
        assertNull(this.versionEventListener.getType());

        // unregister listener
        ((EventTarget)this.processor.getXForms().getDocumentElement()).removeEventListener(XFormsEventNames.VERSION_EXCEPTION, this.versionEventListener, true);
    }

    public void testVersionInvalid()throws Exception{

        String path = getClass().getResource("buglet7.xml").getPath();
        this.processor.setXForms(getClass().getResourceAsStream("buglet7.xml"));
        this.processor.setBaseURI("file://" + path);

        this.versionEventListener = new TestEventListener();
        ((EventTarget)this.processor.getXForms().getDocumentElement()).addEventListener(XFormsEventNames.VERSION_EXCEPTION, this.versionEventListener, true);

        assertNull(this.versionEventListener.getType());

        Exception exception = null;

        try{
            this.processor.init();
        }catch(XFormsException e){
            exception = e;
        }

        assertEquals("xforms-version-exception", this.versionEventListener.getType());

        assertNotNull(exception);
        assertEquals("xforms-version-exception: version exception: version setting of default model not supported: '2011.12'::/envelope[1]/xforms:model[1]", exception.getMessage());

        // unregister listener
        ((EventTarget)this.processor.getXForms().getDocumentElement()).removeEventListener(XFormsEventNames.VERSION_EXCEPTION, this.versionEventListener, true);
    }

    public void testDefaulModelHasLowerVersion()throws Exception{
        String path = getClass().getResource("buglet8.xml").getPath();
        this.processor.setXForms(getClass().getResourceAsStream("buglet8.xml"));
        this.processor.setBaseURI("file://" + path);

        this.versionEventListener = new TestEventListener();
        ((EventTarget)this.processor.getXForms().getDocumentElement()).addEventListener(XFormsEventNames.VERSION_EXCEPTION, this.versionEventListener, true);

        assertNull(this.versionEventListener.getType());

        Exception exception = null;

        try{
            this.processor.init();
        }catch(XFormsException e){
            exception = e;
        }

        assertEquals("xforms-version-exception", this.versionEventListener.getType());

        assertNotNull(exception);
        assertEquals("xforms-version-exception: version exception: Incompatible version setting: 1.1 on model: /envelope[1]/xforms:model[2]::/envelope[1]/xforms:model[1]", exception.getMessage());

        // unregister listener
        ((EventTarget)this.processor.getXForms().getDocumentElement()).removeEventListener(XFormsEventNames.VERSION_EXCEPTION, this.versionEventListener, true);
    }

    public void testGetVersion10_11()throws Exception{
        String path = getClass().getResource("buglet4.xml").getPath();
        this.processor.setXForms(getClass().getResourceAsStream("buglet4.xml"));
        this.processor.setBaseURI("file://" + path);

        //initialize/bootstrap processor
        this.processor.init();
        String version = this.processor.getContainer().getVersion();
        assertEquals(Container.XFORMS_1_1,version);
        this.processor.shutdown();
    }

    public void testGetVersionIncompatible() throws Exception{
        String path = getClass().getResource("buglet5.xml").getPath();
        this.processor.setXForms(getClass().getResourceAsStream("buglet5.xml"));
        this.processor.setBaseURI("file://" + path);

        this.versionEventListener = new TestEventListener();
        ((EventTarget)this.processor.getXForms().getDocumentElement()).addEventListener(XFormsEventNames.VERSION_EXCEPTION, this.versionEventListener, true);

        Exception exception = null;
        
        try{
            this.processor.init();
        }catch(XFormsException e){
            exception = e;
        }

        assertNotNull(exception);
        assertEquals("xforms-version-exception: version exception: version setting of default model not supported: '1.2'::/envelope[1]/xforms:model[1]", exception.getMessage());

        // unregister listener
        ((EventTarget)this.processor.getXForms().getDocumentElement()).removeEventListener(XFormsEventNames.VERSION_EXCEPTION, this.versionEventListener, true);
    }

    public void testInitNoModel() throws Exception{
        String path = getClass().getResource("buglet6.xml").getPath();
        this.processor.setXForms(getClass().getResourceAsStream("buglet6.xml"));
        this.processor.setBaseURI("file://" + path);

        try{
        this.processor.init();
        }catch (XFormsException e){
            assertEquals("No XForms Model found",e.getMessage());
        }

    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testGetModel() throws Exception {
        processor.setXForms(getClass().getResourceAsStream("model-test.xml"));
        processor.init();

        Model m = processor.getContainer().getModel("");
        assertTrue(m != null);
        assertTrue(m.getElement().getLocalName().equals("model"));

        m = processor.getContainer().getModel("messages");
        assertTrue(m != null);
        assertTrue(m.getElement().getLocalName().equals("model"));

        m = processor.getContainer().getModel(null);
        assertTrue(m != null);
    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testInit() throws Exception {
        this.processor.setXForms(getClass().getResourceAsStream("controls-broken.xml"));

        TestEventListener errorListener = new TestEventListener();
        EventTarget eventTarget = (EventTarget) this.processor.getXForms().getDocumentElement();
        eventTarget.addEventListener("xforms-binding-exception", errorListener, true);

        try {
            this.processor.init();
            fail("exception expected");
        }
        catch (XFormsException e) {
            assertTrue("wrong fatal error", e instanceof XFormsBindingException);
            assertTrue("wrong error event type", ("xforms-binding-exception").equals(errorListener.getType()));
            assertTrue("wrong error event target", ("text-input").equals(errorListener.getId()));
            Map errorMap= (Map) errorListener.getContext();
            assertTrue("wrong error context info", ("wrong").equals(errorMap.get("defaultinfo")));

            // unregister listener
            eventTarget.removeEventListener("xforms-binding-exception", errorListener, true);
        }
    }

    public void testGetElementById() throws Exception{
        this.processor.setXForms(getClass().getResourceAsStream("BindingTest.xhtml"));
        this.processor.init();
        String name = processor.getContainer().getElementById("input-1").getLocalName();
        assertEquals("input",name);
    }


    public void testInclude() throws Exception{
        this.processor.setXForms(getClass().getResourceAsStream("include.xml"));
        String path = getClass().getResource("XFormsProcessorImplTest.xhtml").getPath();
        String baseURI = "file://" + path.substring(0, path.lastIndexOf("XFormsProcessorImplTest.xhtml"));
        this.processor.setBaseURI(baseURI);

        DOMUtil.prettyPrintDOM(this.processor.getContainer().getDocument());
    }

    /**
     *
     */
    @Override
    protected void setUp() throws Exception {
        processor = new XFormsProcessorImpl();
    }

    /**
     * __UNDOCUMENTED__
     */
    @Override
    protected void tearDown() {
        this.processor = null;
    }

}
