/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.flux;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.Cache;
import org.infinispan.manager.CacheContainer;
import org.infinispan.manager.CacheManager;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.w3c.dom.Document;
import java.util.HashMap;
import java.util.Iterator;

/*
 * @author Tobi Krebs <tobias.krebs@betterform.de>
*/
    public class FluxProcessorSerializerTest  extends TestCase {
    private static final Log LOGGER = LogFactory.getLog(FluxProcessorSerializerTest.class);
    private HashMap<String,FluxProcessor> fluxProcessors;
    private Cache<String, FluxProcessor>  cache = null;
    private DefaultCacheManager cacheManager;

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

        if (this.cache == null) {
            this.cache= new DefaultCacheManager("infinispan.xml").getCache("xfTestConfigOneElementInMemory");
        }
    }

    @Override
   protected void tearDown() throws Exception {
        super.tearDown();
        this.cacheManager.removeCache("Test");
        /*
        CacheManager cacheManager = CacheManager.getInstance();
        Cache cache = cacheManager.getCache("xfTestConfigOneElementInMemory");
        cache.flush();
        cacheManager.shutdown();
        */
    }
/*
   private Cache initCache(String cacheName) {
       CacheManager cacheManager = CacheManager.getInstance();
       cacheManager.clearAll();
        assertTrue(cacheManager.cacheExists(cacheName));
        Cache cache = cacheManager.getCache(cacheName);
        LOGGER.info("Stats: " + cache.getStatistics());
        return cache;

    }*/

    private Cache initCache(String cacheName) {
        return this.cache;
    }

/*
    private XFSessionCache initCache(String cacheName) {
        return XFSessionCache.getInstance();
    } */

    /*
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
           oneElementInMemory.put(key, processor);
       }
      // LOGGER.info("Stats: " + oneElementInMemory.getStatistics());
   }

    public void testPutAndGetCache() throws Exception {
        LOGGER.info("...::: testPutAndGetCache :::...");
       Cache oneElementInMemory =initCache("xfTestConfigOneElementInMemory");
        Iterator<String> keys  =this.fluxProcessors.keySet().iterator();

        while(keys.hasNext()) {
            String key = keys.next();
            FluxProcessor processor  = this.fluxProcessors.get(key);
            assertEquals(key, processor.getKey());
            oneElementInMemory.put(key, processor);
        }

        keys  =this.fluxProcessors.keySet().iterator();
        while(keys.hasNext()) {
            String key = keys.next();
            FluxProcessor processor  = (FluxProcessor) oneElementInMemory.get(key);
            assertNotNull(processor);
        }

    }
    */

    public void testPutAndGetFluXProcessorsCache() throws Exception {
        System.err.println("" + System.currentTimeMillis());
        int errors = 0;
        LOGGER.info("...::: testPutAndGetFluXProcessorCache :::...");
        Cache oneElementInMemory =initCache("xfTestConfigOneElementInMemory");
        Iterator<String> keys  =this.fluxProcessors.keySet().iterator();

        while(keys.hasNext()) {
            String key = keys.next();
            FluxProcessor processor  = this.fluxProcessors.get(key);
            assertEquals(key, processor.getKey());
            //Element e =new Element(key, processor);
            oneElementInMemory.put(key, processor);
        }

        keys  =this.fluxProcessors.keySet().iterator();
        while(keys.hasNext()) {
            Exception exception = null;
            String key = keys.next();
            //oneElementInMemory.acquireWriteLockOnKey(key);
           //oneElementInMemory.acquireReadLockOnKey(key);
            try {
                FluxProcessor  fluxProcessor = (FluxProcessor)  oneElementInMemory.get(key);
                //assertNotNull(element);
                //FluxProcessor fluxProcessor = (FluxProcessor) element.getObjectValue();
                assertNotNull(fluxProcessor);
                assertNull(fluxProcessor.getContext());
                Document document = fluxProcessor.getXForms() ;
                String localName = document.getDocumentElement().getLocalName();
                assertEquals("html", localName);
                fluxProcessor.init();


                assertEquals("schade",   fluxProcessor.getXFormsModel(null).getInstanceDocument("internal").getElementsByTagName("item").item(0).getTextContent());

                fluxProcessor.dispatchEvent("t-refresh");

                assertEquals("hello",   fluxProcessor.getXFormsModel(null).getInstanceDocument("internal").getElementsByTagName("item").item(0).getTextContent());

            } catch (Exception e) {
                e.printStackTrace();
                exception = e;
                errors++;
            } finally {
               //oneElementInMemory.releaseWriteLockOnKey(key);
               //oneElementInMemory.releaseReadLockOnKey(key);
            }

            assertNull(exception);
        }


        //LOGGER.info("Stats: " + oneElementInMemory.getStatistics());

        //LOGGER.info("Keys: "  + oneElementInMemory.getKeys().size());
        LOGGER.info("Errors: "  + errors);
        System.err.println("" + System.currentTimeMillis());
    }

    /*
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
    */
}
