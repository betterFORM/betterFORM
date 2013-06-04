/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.cache;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.manager.DefaultCacheManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/*
 * @author Tobi Krebs <tobias.krebs@betterform.de>
*/
public class BFCacheManager {
    private static DefaultCacheManager cacheManager;


    private static Configuration getConfiguration() {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.clustering().cacheMode(CacheMode.LOCAL);
        /* Default setting */
        configurationBuilder.loaders().addFileCacheStore().location(System.getProperty("java.io.tmpdir") + "/bfCache/defaultCache" );
        configurationBuilder.loaders().passivation(true);
         configurationBuilder.loaders().shared(false);
         configurationBuilder.loaders().preload(false);
         configurationBuilder.eviction().strategy(EvictionStrategy.LIRS);
         configurationBuilder.eviction().maxEntries(100);
        configurationBuilder.expiration().lifespan(-1);
        configurationBuilder.expiration().maxIdle(1, TimeUnit.DAYS);
        return  configurationBuilder.build();
    }
    private BFCacheManager() {
    }

    protected static DefaultCacheManager  getCacheManager() throws IOException {
        if (BFCacheManager.cacheManager == null) {
            BFCacheManager.initCacheManager();
        }

        return BFCacheManager.cacheManager;
    }

    protected static void initCacheManager() throws IOException {
        BFCacheManager.cacheManager = new DefaultCacheManager(BFCacheManager.getConfiguration());

    }

   protected static void stopCacheManager() {
       if(BFCacheManager.cacheManager != null) {
           cacheManager.stop();
       }
   }

    public static void main(String[] argv) {
        try {
            BFCacheManager.initCacheManager();
            System.out.println("Huhu");
            BFCacheManager.stopCacheManager();
        } catch (Exception e) {
              e.printStackTrace();
        }
    }
}
