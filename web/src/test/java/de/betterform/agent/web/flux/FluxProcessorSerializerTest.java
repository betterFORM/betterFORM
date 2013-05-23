/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.flux;

import de.betterform.agent.web.cache.XFSessionCache;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/*
 * @author Tobi Krebs <tobias.krebs@betterform.de>
*/
    public class FluxProcessorSerializerTest  extends TestCase {
    private static final Log LOGGER = LogFactory.getLog(FluxProcessorSerializerTest.class);
    private List fluxProcessors;
    private Cache<String, FluxProcessor>  cache = null;
    private DefaultCacheManager cacheManager;
    private Cache<String, FluxProcessor> oneElementInMemory;

    @Override
    protected void setUp() throws Exception {
        LOGGER.debug(".....::::setting up tests::::::.....");
        String path = getClass().getResource("localization.xhtml").getPath();
        String  baseURI = "file://" + path.substring(0, path.lastIndexOf("localization.xhtml"));
        this.fluxProcessors  = new ArrayList<FluxProcessor>(100);
        for (int i = 0; i < 100; i++) {
            FluxProcessor processor = new FluxProcessor();
            processor.setBaseURI(baseURI);
            processor.setXForms(getClass().getResourceAsStream("localization.xhtml"));
            processor.setContext(new HashMap());
//            processor.setContextParam("key",i+"");
            processor.init();
            this.fluxProcessors.add(processor);
            LOGGER.debug(".....::::::::::..... " + i);
            LOGGER.debug(".....::::::::::..... " + processor.toString());

        }
        this.oneElementInMemory = XFSessionCache.getCache("xfTestConfigOneElementInMemory");
/*
        if (this.cache == null) {
            if (this.cacheManager == null) {
                this.cacheManager = new DefaultCacheManager("infinispan.xml");
            }
            this.cache = this.cacheManager.getCache("xfTestConfigOneElementInMemory");
        }
*/
        LOGGER.debug(".....::::setting up tests - done ::::::.....");
        LOGGER.debug(".....::::setting up tests - done ::::::.....");
        LOGGER.debug(".....::::setting up tests - done ::::::.....");

    }

    @Override
   protected void tearDown() throws Exception {
        super.tearDown();
        XFSessionCache.unloadCache();
        this.oneElementInMemory = null;
//        this.cacheManager.removeCache("xfTestConfigOneElementInMemory");
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
//        Cache oneElementInMemory =initCache("xfTestConfigOneElementInMemory");

        for (int i=0;i<100;i++){
            String key = i + "";
            FluxProcessor processor  = (FluxProcessor) this.fluxProcessors.get(i);
            //Element e =new Element(key, processor);
            processor.setContextParam("key",key);
            LOGGER.debug("putting ... " + processor.getContextParam("key"));
            oneElementInMemory.put(key, processor);
        }

        LOGGER.debug("####################### now reading");

        for (int i=0;i<100;i++){
            String key = i + "";
            LOGGER.debug("");
            LOGGER.debug("GETTING ... " + key);
            LOGGER.debug("inCache ... " + oneElementInMemory.containsKey(key));
            LOGGER.debug("inCache ... " + oneElementInMemory.containsKey(key));
            FluxProcessor processor = (FluxProcessor) oneElementInMemory.get(key);
            assertNotNull(processor);
            processor.init();
            assertEquals("schade",   processor.getXFormsModel(null).getInstanceDocument("internal").getElementsByTagName("item").item(0).getTextContent());

            processor.dispatchEvent("t-refresh");

            assertEquals("hello", processor.getXFormsModel(null).getInstanceDocument("internal").getElementsByTagName("item").item(0).getTextContent());

            LOGGER.debug("GET: key in processor: " + processor.getXForms().getDocumentElement().getAttribute("bf:serialized"));
            assertNotNull(processor);
        }

        LOGGER.info("Errors: " + errors);
    }



/*
    public void testPutAndGetFluXProcessorCache() throws Exception {
        LOGGER.info("...::: testPutAndGetFluXProcessorCache :::...");
        Cache oneElementInMemory =initCache("xfTestConfigOneElementInMemory");
        Iterator<String> keys  =this.fluxProcessors.keySet().iterator();

        String key1 = keys.next();
        String key2 = keys.next();
        
        FluxProcessor processor  = this.fluxProcessors.get(key1);
        assertEquals(key1, processor.getKey());
        oneElementInMemory.put(key1, processor);
        processor  = this.fluxProcessors.get(key2);
        assertEquals(key2, processor.getKey());
        oneElementInMemory.put(key2, processor);
       
           Exception exception = null;
           try {
                processor = (FluxProcessor) oneElementInMemory.get(key1);
               assertNotNull(processor);
               assertNotNull(processor);
               assertNull(processor.getContext());
               Document document = processor.getXForms() ;
               String localName = document.getDocumentElement().getLocalName();
               assertEquals("html", localName);
               processor.init();
               assertEquals("schade",   processor.getXFormsModel(null).getInstanceDocument("internal").getElementsByTagName("item").item(0).getTextContent());
               processor.dispatchEvent("t-refresh");

               processor = (FluxProcessor) oneElementInMemory.get(key2);
               assertNotNull(processor);
               assertNotNull(processor);
               assertNull(processor.getContext());
               document = processor.getXForms() ;
               localName = document.getDocumentElement().getLocalName();
               assertEquals("html", localName);
               processor.init();
               assertEquals("schade",   processor.getXFormsModel(null).getInstanceDocument("internal").getElementsByTagName("item").item(0).getTextContent());
               processor.dispatchEvent("t-refresh");
           } catch (Exception e) {
               e.printStackTrace();
               exception = e;
           }
           assertNull(exception);

    }              */

}
