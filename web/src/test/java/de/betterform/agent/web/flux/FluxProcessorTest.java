/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.flux;

import de.betterform.agent.web.session.SerializableObject;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import junit.framework.TestCase;
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

    private XFormsProcessor fluxProcessor;
    private static final Log LOGGER = LogFactory.getLog(FluxProcessorTest.class);
    private String baseURI;


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
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
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
        }



        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("####################### put second object into cache - should serialize xformsProcessor");
        }
        Element e1 =new Element("2",new SerializableObject("foo"));
        sessionCache.put(e1);
        sessionCache.flush();
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("### 2: " + sessionCache.getStatistics());
            LOGGER.debug("### 2: " + sessionCache.isElementInMemory("2"));
            LOGGER.debug("### 2: " + sessionCache.isElementOnDisk("2"));
        }


//        if(LOGGER.isDebugEnabled()) LOGGER.debug(sessionCache.getStatistics());

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("####################### access (serialized) xformsprocessor from cache");
        }

        Thread.sleep(1000);

        LOGGER.debug("Disk" + sessionCache.calculateOnDiskSize());
        LOGGER.debug("Mem" + sessionCache.calculateInMemorySize());
        FluxProcessor syncedProcessor = (FluxProcessor) sessionCache.get("fluxProcessor").getValue();
        assertNotNull(syncedProcessor);
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
//        if(LOGGER.isDebugEnabled()) LOGGER.debug(sessionCache.getStatistics());

        assertEquals("foo", value2.getValue());
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