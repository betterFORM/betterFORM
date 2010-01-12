/* Copyright 2009 - Joern Turner */
/* Licensed under the terms of BSD and Apache 2 Licenses */

package de.betterform.agent.web.flux;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.agent.web.event.DefaultUIEventImpl;
import de.betterform.agent.web.event.UIEvent;
import de.betterform.agent.web.event.EventQueue;
import de.betterform.agent.web.WebUtil;
import de.betterform.agent.web.WebProcessor;
import de.betterform.agent.web.upload.UploadInfo;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.events.impl.XercesXMLEventFactory;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import org.directwebremoting.WebContextFactory;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.xforms.XFormsModelElement;

import javax.servlet.http.HttpSession;
import java.util.*;


// end of class

/**
 * AJAX Facade class to hide the full functionality from the web-client and to avoid overloaded methods which are
 * not recommended by DWR.
 *
 * @author Joern Turner
 */
public class FluxUtil {
    private static final Log LOGGER = LogFactory.getLog(FluxUtil.class);
    private HttpSession session;

    public static FluxProcessor getProcessor(String sessionKey) throws FluxException {
        WebProcessor webProcessor = WebUtil.getWebProcessor(sessionKey);
        if (webProcessor == null) {
            LOGGER.fatal("WebProcessor not found - stopping");
            throw new FluxException("Sorry your session expired. Press Reload to start over.");
        }
        return (FluxProcessor) webProcessor;
    }

}