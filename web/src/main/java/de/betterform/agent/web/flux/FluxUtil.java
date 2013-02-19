/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.flux;

import javax.servlet.http.HttpSession;

import de.betterform.agent.web.WebProcessor;
import de.betterform.agent.web.WebUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


// end of class

/**
 * AJAX Facade class to hide the full functionality from the web-client and to avoid overloaded methods which are
 * not recommended by DWR.
 *
 * @author Joern Turner
 */
public class FluxUtil {
    private static final Log LOGGER = LogFactory.getLog(FluxUtil.class);

    public static FluxProcessor getProcessor(HttpSession session, String sessionKey) throws FluxException {
        WebProcessor webProcessor = WebUtil.getWebProcessor(session, sessionKey);
        if (webProcessor == null) {
            LOGGER.fatal("WebProcessor not found - stopping");
            throw new FluxException("Sorry your session expired. Press Reload to start over.");
        }
        return (FluxProcessor) webProcessor;
    }

}
