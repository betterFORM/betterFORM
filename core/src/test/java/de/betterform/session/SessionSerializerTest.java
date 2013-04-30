/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.session;

import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.session.SerializableObject;
import de.betterform.xml.dom.DOMUtil;
import org.w3c.dom.Document;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

/**
 *
 * @author Joern Turner
 */
public class SessionSerializerTest extends XMLTestBase {
    static {
        org.apache.log4j.BasicConfigurator.configure();
    }
    private static final Log LOGGER = LogFactory.getLog(SessionSerializerTest.class);

    private XFormsProcessorImpl processor;
    private String baseURI;


    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document form = builder.parse(getClass().getResourceAsStream("session.xhtml"));

        String path = getClass().getResource("session.xhtml").getPath();
        this.baseURI = "file://" + path.substring(0, path.lastIndexOf("session.xhtml"));

        this.processor = new XFormsProcessorImpl();
        this.processor.setBaseURI(baseURI);
        this.processor.setXForms(form);
        this.processor.init();
    }


    /**
     * first serializes the initialized XFormsProcessorImpl and then reads it back again.
     * @throws Exception
     */
    public void testSerialization() throws Exception{
        String fullPath = getClass().getResource("session.xhtml").getPath();
        String strippedPath = fullPath.substring(0, fullPath.lastIndexOf("session.xhtml"));
        File outputFile = new File(strippedPath,"foo.xml");
        this.processor.writeExternal(new ObjectOutputStream(new FileOutputStream(outputFile)));


        XFormsProcessorImpl xformsProcesssorImpl = new XFormsProcessorImpl();
        assertTrue(xformsProcesssorImpl.getContainer() == null);

        File inFile = new File(strippedPath,"foo.xml");
        xformsProcesssorImpl.readExternal(new ObjectInputStream(new FileInputStream(inFile)));

        Document xforms = xformsProcesssorImpl.getXForms();
        DOMUtil.prettyPrintDOM(xforms);

        xformsProcesssorImpl.init();
        assertNotNull(xformsProcesssorImpl.getContainer());
        assertEquals(xformsProcesssorImpl.getContext().get("betterform.baseURI"),this.baseURI);
    }

    public void testEhcachSerialization() throws Exception {
        CacheManager manager = CacheManager.create();
        Ehcache sessionCache = manager.getCache("xfTestConfigOneElementInMemory");
        Element elem1 = new Element("xformsProcesssorImpl",this.processor);
        sessionCache.put(elem1);
        sessionCache.flush();
        Element elem2 = new Element("2",new SerializableObject("foo"));
        sessionCache.put(elem2);
        sessionCache.flush();

        sessionCache.evictExpiredElements();
        Thread.sleep(1000);                                             
        System.out.println(sessionCache.getStatistics());

        Element elem = sessionCache.get("xformsProcesssorImpl");
        System.out.println("expired: " + elem.isExpired());

        XFormsProcessorImpl syncedProcessor = (XFormsProcessorImpl) sessionCache.get("xformsProcesssorImpl").getObjectValue();
        assertNotNull(syncedProcessor);

        syncedProcessor.init();

//        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug("START PRETTY PRINT");
            DOMUtil.prettyPrintDOM(syncedProcessor.getContainer().getDocument());
            LOGGER.debug("\nEND PRETTY PRINT");
//        }
        SerializableObject value2 = (SerializableObject) sessionCache.get("2").getObjectValue();
        System.out.println(sessionCache.getStatistics());

        assertEquals("foo", value2.getValue());
    }


    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.processor = null;
    }

}
