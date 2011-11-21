package de.betterform.agent.web.cache;

import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

import java.util.Properties;

/**
 * betterFORM Project
 * User: Tobi Krebs
 * Date: 15.11.11
 * Time: 13:31
 */
public class BFCacheManagerEventListenerFactory extends CacheEventListenerFactory {
    @Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        return new BFCacheEventListener();
    }
}
