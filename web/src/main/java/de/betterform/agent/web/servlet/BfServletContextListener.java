/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.servlet;

import net.sf.ehcache.CacheManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ServletListener to shutdown ehCache
 *
 */

public class BfServletContextListener implements ServletContextListener {
    private static final Log LOGGER = LogFactory.getLog(BfServletContextListener.class);

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("shutting down ehCache");
        }
        CacheManager.getInstance().shutdown();
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}