/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.flux;

import junit.framework.TestCase;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/*
 * @author Tobi Krebs <tobias.krebs@betterform.de>
*/
    public class FluxProcessorSerializerTest  extends TestCase {
    private static final Log LOGGER = LogFactory.getLog(FluxProcessorSerializerTest.class);
    private HashMap<String,FluxProcessor> fluxProcessors;

    @Override
    protected void setUp() throws Exception {
        String path = getClass().getResource("localization.xhtml").getPath();
        String  baseURI = "file://" + path.substring(0, path.lastIndexOf("localization.xhtml"));
        this.fluxProcessors  = new HashMap<String,FluxProcessor>(100);
        for (int i = 0; i < 100; i++) {
            FluxProcessor processor = new FluxProcessor();
            processor.setBaseURI(baseURI);
            processor.setXForms(getClass().getResourceAsStream("localization.xhtml"));
            processor.init();
            this.fluxProcessors.put(processor.getKey(), processor);
        }
    }

    @Override
   protected void tearDown() throws Exception {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }


   private Cache initCache(String cacheName) {
       CacheManager cacheManager = CacheManager.getInstance();
       cacheManager.clearAll();
        assertTrue(cacheManager.cacheExists(cacheName));
        Cache cache = cacheManager.getCache(cacheName);
        LOGGER.info("Stats: " + cache.getStatistics());
        return cache;
    }


   public void testCreateSingleCache() throws Exception {
        Cache oneElementInMemory =initCache("xfTestConfigOneElementInMemory");
       assertNotNull(oneElementInMemory);
   }

   public void testCreateMultipleCache() throws Exception {
        Cache oneElementInMemory1 =initCache("xfTestConfigOneElementInMemory");
        assertNotNull(oneElementInMemory1);
        Cache oneElementInMemory2 =initCache("xfTestConfigOneElementInMemory");
        assertNotNull(oneElementInMemory2);

        assertEquals(oneElementInMemory1, oneElementInMemory2);
    }

   public void testPutIntoCache() throws Exception {
       LOGGER.info("...::: testPutIntoCache :::...");
        Cache oneElementInMemory =initCache("xfTestConfigOneElementInMemory");
        Iterator<String> keys  =this.fluxProcessors.keySet().iterator();

       while(keys.hasNext()) {
           String key = keys.next();
           FluxProcessor processor  = this.fluxProcessors.get(key);
           assertEquals(key, processor.getKey());
           Element e =new Element(key, processor);
           oneElementInMemory.put(e);
       }
       LOGGER.info("Stats: " + oneElementInMemory.getStatistics());
   }

    public void testPutAndGetCache() throws Exception {
        LOGGER.info("...::: testPutAndGetCache :::...");
        Cache oneElementInMemory =initCache("xfTestConfigOneElementInMemory");
        Iterator<String> keys  =this.fluxProcessors.keySet().iterator();

        while(keys.hasNext()) {
            String key = keys.next();
            FluxProcessor processor  = this.fluxProcessors.get(key);
            assertEquals(key, processor.getKey());
            Element e =new Element(key, processor);
            oneElementInMemory.put(e);
        }

        keys  =this.fluxProcessors.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            Element  element = oneElementInMemory.get(key);
            assertNotNull(element);
        }

        LOGGER.info("Stats: " + oneElementInMemory.getStatistics());
    }

    public void testPutAndGetFluXProcessorsCache() throws Exception {
        LOGGER.info("...::: testPutAndGetFluXProcessorCache :::...");
        Cache oneElementInMemory =initCache("xfTestConfigOneElementInMemory");
        Iterator<String> keys  =this.fluxProcessors.keySet().iterator();

        while(keys.hasNext()) {
            String key = keys.next();
            FluxProcessor processor  = this.fluxProcessors.get(key);
            assertEquals(key, processor.getKey());
            Element e =new Element(key, processor);
            oneElementInMemory.put(e);
        }

        keys  =this.fluxProcessors.keySet().iterator();
        while(keys.hasNext()) {
            Exception exception = null;
            String key = keys.next();
            oneElementInMemory.acquireWriteLockOnKey(key);
            oneElementInMemory.acquireReadLockOnKey(key);
            try {
                Element  element = oneElementInMemory.get(key);
                assertNotNull(element);
                FluxProcessor fluxProcessor = (FluxProcessor) element.getObjectValue();
                assertNotNull(fluxProcessor);
                assertNull(fluxProcessor.getContext());
                Document document = fluxProcessor.getXForms() ;
                String localName = document.getDocumentElement().getLocalName();
                assertEquals("html", localName);
                fluxProcessor.init();


                assertEquals("schade",   fluxProcessor.getXFormsModel(null).getInstanceDocument("internal").getElementsByTagName("item").item(0).getTextContent());

                fluxProcessor.dispatchEvent("t-refresh");
            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
            } finally {
                oneElementInMemory.releaseWriteLockOnKey(key);
                oneElementInMemory.releaseReadLockOnKey(key);
            }

            assertNull(exception);
        }


        LOGGER.info("Stats: " + oneElementInMemory.getStatistics());

        LOGGER.info("Keys: "  + oneElementInMemory.getKeys().size());
    }


    public void testPutAndGetFluXProcessorCache() throws Exception {
        LOGGER.info("...::: testPutAndGetFluXProcessorCache :::...");
        Cache oneElementInMemory =initCache("xfTestConfigOneElementInMemory");
        Iterator<String> keys  =this.fluxProcessors.keySet().iterator();

        String key = null;
        if(keys.hasNext()) {
            key = keys.next();
            FluxProcessor processor  = this.fluxProcessors.get(key);
            assertEquals(key, processor.getKey());
            Element e =new Element(key, processor);
            oneElementInMemory.put(e);
        }


       if(key != null) {
           Exception exception = null;
           oneElementInMemory.acquireWriteLockOnKey(key);
           oneElementInMemory.acquireReadLockOnKey(key);
           try {
               Element  element = oneElementInMemory.get(key);
               assertNotNull(element);
               FluxProcessor fluxProcessor = (FluxProcessor) element.getObjectValue();
               assertNotNull(fluxProcessor);
               assertNull(fluxProcessor.getContext());
               Document document = fluxProcessor.getXForms() ;
               String localName = document.getDocumentElement().getLocalName();
               assertEquals("html", localName);
               fluxProcessor.init();
               assertEquals("schade",   fluxProcessor.getXFormsModel(null).getInstanceDocument("internal").getElementsByTagName("item").item(0).getTextContent());
               fluxProcessor.dispatchEvent("t-refresh");
           } catch (Exception e) {
               e.printStackTrace();
               exception = e;
           } finally {
               oneElementInMemory.releaseWriteLockOnKey(key);
               oneElementInMemory.releaseReadLockOnKey(key);
           }

           assertNull(exception);
        }


        LOGGER.info("Stats: " + oneElementInMemory.getStatistics());

        LOGGER.info("Keys: "  + oneElementInMemory.getKeys().size());
    }
}
