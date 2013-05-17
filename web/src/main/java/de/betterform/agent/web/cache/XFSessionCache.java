/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.cache;

import de.betterform.agent.web.WebProcessor;
import de.betterform.agent.web.flux.FluxProcessor;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.xforms.exception.XFormsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.infinispan.Cache;
import org.infinispan.manager.DefaultCacheManager;

import java.io.IOException;

/*
 * @author Tobi Krebs <tobias.krebs@betterform.de>
*/public class XFSessionCache {
   private static final Log LOGGER = LogFactory.getLog(XFSessionCache.class);
   private static Cache<String, FluxProcessor>  xfsessionCache;
    private static DefaultCacheManager cacheManager;

    public  XFSessionCache()  {
    }

    private  static void init() throws IOException {
        XFSessionCache.cacheManager = new DefaultCacheManager("infinispan.xml");
        XFSessionCache.xfsessionCache = XFSessionCache.cacheManager.getCache("xfTestConfigOneElementInMemory");
    }


    public static Cache<String, FluxProcessor> getCache() throws XFormsException {
        if(XFSessionCache.xfsessionCache == null) {
           try {
               XFSessionCache.init();
           } catch(IOException ioe) {
                throw new XFormsConfigException("Infinispan cache could not be inialized please check your configuration", ioe);
           }
        }

        return XFSessionCache.xfsessionCache;
    }



    public static void main(String argv[]) {
        try {
            Cache<String, FluxProcessor>  sessionCache = XFSessionCache.getCache();
        } catch (XFormsException xfce) {
            System.err.println("Booooooooooooooooooooooooooooo!");
        }

    }
}
