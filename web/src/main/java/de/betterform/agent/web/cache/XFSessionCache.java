/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.cache;

//import de.betterform.agent.web.flux.FluxProcessor;
import de.betterform.agent.web.flux.SocketProcessor;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.xforms.exception.XFormsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.Cache;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.DefaultCacheManager;

import java.io.IOException;

/*
 * @author Tobi Krebs <tobias.krebs@betterform.de>
 * @author Joern Turner
 */
public class XFSessionCache {
    private static final Log LOGGER = LogFactory.getLog(XFSessionCache.class);
    private static Cache<String, SocketProcessor> XFSESSIONCACHE;
    private static DefaultCacheManager cacheManager;
    private static String DEFAULT_CACHE = "xfSessionCache";

    protected XFSessionCache() {
    }

    protected static void initCache(String cacheName) throws IOException {
        XFSessionCache.cacheManager = new DefaultCacheManager("infinispan.xml");
        XFSessionCache.XFSESSIONCACHE = XFSessionCache.cacheManager.getCache(cacheName);
    }


    public static Cache<String, SocketProcessor> getCache() throws XFormsConfigException {
        if (XFSessionCache.XFSESSIONCACHE == null) {
            try {
                initCache(XFSessionCache.DEFAULT_CACHE);
            } catch (IOException e) {
                throw new XFormsConfigException("Cache configuration broken or missing on classpath");
            }
        }

        return XFSessionCache.XFSESSIONCACHE;
    }

    public static Cache<String, SocketProcessor> getCache(String cacheName) throws XFormsConfigException {
        try {
            initCache(cacheName);
        } catch (IOException e) {
            throw new XFormsConfigException("infinispan.xml file not found");
        }
        return XFSessionCache.XFSESSIONCACHE;
    }

    public static void unloadCache() {
        if (XFSESSIONCACHE != null) {
            ComponentStatus status = XFSESSIONCACHE.getStatus();
            if (status.equals(ComponentStatus.RUNNING)) {
                XFSESSIONCACHE.stop();
            }
            cacheManager.stop();
            XFSESSIONCACHE = null;
        }
    }

    public static void main(String argv[]) {
        try {
            Cache<String, SocketProcessor> sessionCache = XFSessionCache.getCache();
        } catch (XFormsException xfce) {
            System.err.println("Booooooooooooooooooooooooooooo!");
        }

    }
}
