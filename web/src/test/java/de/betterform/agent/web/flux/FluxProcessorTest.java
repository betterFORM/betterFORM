/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.flux;

import de.betterform.agent.web.session.SerializableObject;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import junit.framework.TestCase;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class FluxProcessorTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private FluxProcessor fluxProcessor;
    private static final Log LOGGER = LogFactory.getLog(FluxProcessorTest.class);
    private String baseURI;
    private String sessionKey;


    @Override
    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document form = builder.parse(getClass().getResourceAsStream("localization.xhtml"));

        String path = getClass().getResource("localization.xhtml").getPath();
        this.baseURI = "file://" + path.substring(0, path.lastIndexOf("localization.xhtml"));
        this.fluxProcessor = new FluxProcessor();
        this.fluxProcessor.setBaseURI(baseURI);
        this.fluxProcessor.setXForms(getClass().getResourceAsStream("localization.xhtml"));
        this.fluxProcessor.init();
        this.sessionKey = fluxProcessor.getKey();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }


    /**
     * put one processor into cache, fetch it again and interact (no serialization should interfere here)
     * @throws Exception
     */
    public void testEhcache1() throws Exception {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("####################################################################################");
            LOGGER.debug("####################################################################################");
            LOGGER.debug("####################################################################################");
        }
        CacheManager manager = CacheManager.create();
        Cache sessionCache = manager.getCache("xfTestConfigOneElementInMemory");

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("####################### put xformsProcessor into cache #############################");
        }
        Element e =new Element(this.sessionKey,this.fluxProcessor);
        sessionCache.put(e);
//        sessionCache.flush();

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("### 1 in mem: " + sessionCache.isElementInMemory(this.sessionKey));
            LOGGER.debug("### 1 on disk: " + sessionCache.isElementOnDisk(this.sessionKey));
        }

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("####################### get xformsProcessor from cache #############################");
            LOGGER.debug("####################### " + this.sessionKey);
        }


        FluxProcessor fluxProcessor1 = (FluxProcessor) sessionCache.get(this.sessionKey).getObjectValue();
        Document doc = fluxProcessor1.getXForms() ;
        String s = doc.getDocumentElement().getLocalName();
        assertEquals("html",s);

//        fluxProcessor1.init();
        fluxProcessor1.dispatchEvent("t-refresh");

//        Thread.sleep(1000);
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("### 1 in mem: " + sessionCache.isElementInMemory(this.sessionKey));
            LOGGER.debug("### 1 on disk: " + sessionCache.isElementOnDisk(this.sessionKey));
            LOGGER.debug("### 1 on disk: " + sessionCache.getStatistics());
        }



//        manager.shutdown();
    }

    public void testEhcachSerialization() throws Exception {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("####################################################################################");
            LOGGER.debug("####################################################################################");
            LOGGER.debug("####################################################################################");
        }
        CacheManager manager = CacheManager.create();
        Ehcache sessionCache = manager.getCache("xfTestConfigOneElementInMemory");

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("####################### put xformsProcessor into cache #############################");
        }
        Element e =new Element("fluxProcessor",this.fluxProcessor);
        sessionCache.put(e);
//        sessionCache.flush();

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("### 1: " + sessionCache.getStatistics());
            LOGGER.debug("### 1: " + sessionCache.isElementInMemory("fluxProcessor"));
            LOGGER.debug("### 1: " + sessionCache.isElementOnDisk("fluxProcessor"));
            LOGGER.debug("####################### put second object into cache - should serialize xformsProcessor");
        }

        Element e1 =new Element("2",new SerializableObject("foo"));
        sessionCache.put(e1);
        Element e2 =new Element("3",new SerializableObject("bar"));
        sessionCache.put(e2);
//        sessionCache.flush();

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("### 2: " + sessionCache.getStatistics());
            LOGGER.debug("### 2: " + sessionCache.isElementInMemory("2"));
            LOGGER.debug("### 2: " + sessionCache.isElementOnDisk("2"));
            LOGGER.debug("####################### access (serialized) xformsprocessor from cache");
        }

        FluxProcessor syncedProcessor = (FluxProcessor) sessionCache.get("fluxProcessor").getValue();
        assertNotNull(syncedProcessor);
        assertTrue(syncedProcessor.getContext()==null);
        syncedProcessor.init();

        if(LOGGER.isDebugEnabled()) LOGGER.debug("### 3: " + sessionCache.getStatistics());

        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("START PRETTY PRINT");
            DOMUtil.prettyPrintDOM(syncedProcessor.getXForms());
            LOGGER.debug("\nEND PRETTY PRINT");
            LOGGER.debug("\nInstance...");
            DOMUtil.prettyPrintDOM(syncedProcessor.getXformsProcessor().getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument());
        }

        SerializableObject value2 = (SerializableObject) sessionCache.get("2").getValue();
        assertEquals("foo", value2.getValue());
        SerializableObject value3 = (SerializableObject) sessionCache.get("3").getValue();
        assertEquals("bar", value3.getValue());

        LOGGER.debug("### 3: " + sessionCache.getStatistics());
        sessionCache.flush();


    }


    public void testGetAllUpdates() throws Exception{
        this.fluxProcessor.setLocale("de");
/*
        DOMUtil.prettyPrintDOM(((FluxProcessor)this.fluxProcessor).getEventLog().getLog());

        ((FluxProcessor)this.fluxProcessor).getEventLog().flush();
*/

//        DOMUtil.prettyPrintDOM(this.fluxProcessor.getXForms());
        this.fluxProcessor.setLocale("en");
//        DOMUtil.prettyPrintDOM(((FluxProcessor)this.fluxProcessor).getEventLog().getLog());
    }

}