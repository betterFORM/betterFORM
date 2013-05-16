/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * betterFORM Project
 * User: Tobi Krebs
 * Date: 15.11.11
 * Time: 13:32
 */
public class BFCacheEventListener implements CacheEventListener {
    private static final Log LOGGER = LogFactory.getLog(BFCacheEventListener.class);


    public Object clone() {
       return null;
    }

    public void notifyElementRemoved(Ehcache ehcache, Element element) throws CacheException {
        log("REMOVED", ehcache, element);

    }

    public void notifyElementPut(Ehcache ehcache, Element element) throws CacheException {
        log("PUT", ehcache, element);
    }

    public void notifyElementUpdated(Ehcache ehcache, Element element) throws CacheException {
        log("UPDATE", ehcache, element);
    }

    public void notifyElementExpired(Ehcache ehcache, Element element) {
        log("EXPIRED", ehcache, element);

    }

    public void notifyElementEvicted(Ehcache ehcache, Element element) {
        log("EVICTED", ehcache, element);
    }

    public void notifyRemoveAll(Ehcache ehcache) {
        LOGGER.debug("REMOVE ALL: Cache:" + ehcache.toString());
    }

    public void dispose() {
        LOGGER.debug("DISPOSE");
    }

    private void log(String status, Ehcache ehcache, Element element) {
        LOGGER.info(status.toUpperCase() + ": Element: " + element.toString() + " Object " + element.getObjectValue() + " Cache: " + ehcache.toString());
        LOGGER.info(status.toUpperCase() + ": Cache Size : " + ehcache.getSize() + " Memory Size: " + ehcache.getMemoryStoreSize());
        LOGGER.info(status.toUpperCase() + ": Time : " + System.currentTimeMillis());
    }
}
