/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.flux;

import de.betterform.agent.web.WebProcessor;
import de.betterform.agent.web.WebUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


// end of class

/**
 * AJAX Facade class to hide the full functionality from the web-client and to avoid overloaded methods which are
 * not recommended by DWR.
 *
 * @author Joern Turner
 * @deprecated replaced by SocketProcessor
 */
public class FluxUtil {
    private static final Log LOGGER = LogFactory.getLog(FluxUtil.class);

    public static FluxProcessor getProcessor(String sessionKey, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws FluxException {
        WebProcessor webProcessor = WebUtil.getWebProcessor(sessionKey, request, response, session);
        if (webProcessor == null) {
            LOGGER.fatal("WebProcessor not found - stopping");
            throw new FluxException("Sorry your session expired. Press Reload to start over.");
        }
        return (FluxProcessor) webProcessor;
    }

}
