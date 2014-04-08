/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.servlet;

import de.betterform.agent.web.cache.XFSessionCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * ServletListener to shutdown ehCache
 *
 */

public class BfServletContextListener implements ServletContextListener {
    private static final Log LOGGER = LogFactory.getLog(BfServletContextListener.class);

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("shutting down infinispan");
        }
        XFSessionCache.unloadCache();
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}