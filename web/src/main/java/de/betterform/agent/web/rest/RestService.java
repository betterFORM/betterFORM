/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.rest;

import de.betterform.agent.web.WebProcessor;
import de.betterform.agent.web.WebUtil;
import de.betterform.agent.web.event.DefaultUIEventImpl;
import de.betterform.agent.web.event.EventQueue;
import de.betterform.agent.web.event.UIEvent;
import de.betterform.agent.web.flux.FluxException;
import de.betterform.agent.web.flux.FluxProcessor;
import de.betterform.agent.web.flux.FluxUtil;
import de.betterform.agent.web.upload.UploadInfo;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.events.impl.XercesXMLEventFactory;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jboss.resteasy.annotations.GZIP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import java.util.*;

@Path("/")
public class RestService {
	// this is a custom event to activate a trigger in XForms.
	public static final String FLUX_ACTIVATE_EVENT = "flux-action-event";
	private static final Log LOGGER = LogFactory.getLog(RestService.class);
	// private HttpSession session;
	private @Context
	HttpServletRequest request;

	/**
	 * grabs the actual web from the session.
	 */
	public RestService() {
		// session = WebContextFactory.get().getSession(true);
	}
	
    /**
     * todo: implement XForms init via Ajax call
     *
     * @return update info to initially setup the browser page
     * @throws FluxException when everything goes wrong
     */
	@GET
	@Path("/init")
    public List<XMLEvent> init() throws RestException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("RestService init called on Facade");
        }
        
        //RestProcessor processor = createProcessor();
        RestProcessor processor = getProcessor();        
        return processor.getEventQueue().getEventList();
    }

	// todo: should be named 'dispatchActivateEvent'
	@GET
	@Path("/dispatchEvent/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	@GZIP
	public List<XMLEvent> dispatchEvent(@PathParam("id") String id)
			throws XFormsException {

		try {
			RestProcessor processor = getProcessor();
			// ##### event queue is not flushed here as FluxProcessor handles
			// this ####
			processor.dispatchEvent(id);
			return processor.getEventQueue().getEventList();
		} catch (RestException e) {
			return this.renderErrorMessage(e.getMessage());
		}

	}

	private List<XMLEvent> renderErrorMessage(String message) {
		LOGGER.warn("betterFORM Error Message: " + message);
		XMLEvent errorEvent = new XercesXMLEventFactory()
				.createXMLEvent(BetterFormEventNames.RENDER_MESSAGE);
		Map<String, String> params = new HashMap<String, String>(1);
		params.put("message", message);
		errorEvent.initXMLEvent(BetterFormEventNames.RENDER_MESSAGE, true,
				false, params);
		ArrayList<XMLEvent> list = new ArrayList<XMLEvent>();
		list.add(errorEvent);
		return list;
	}

	public List<XMLEvent> dispatchEventType(String id, String eventType,
			String sessionKey) throws XFormsException {
		return dispatchEventTypeWithContext(id, eventType, sessionKey, null);
	}

	public List<XMLEvent> dispatchEventTypeWithContext(String id,
			String eventType, String sessionKey, String contextInfo)
			throws XFormsException {

		try {
			RestProcessor processor = getProcessor();
			processor.getEventQueue().flush();

			if (contextInfo != null) {
				Map<String, String> params = new HashMap<String, String>(1);
				params.put("context-info", contextInfo);
				processor.dispatch(id, eventType, params, true, false);
			} else {
				processor.dispatch(id, eventType);
			}

			return processor.getEventQueue().getEventList();
		} catch (XFormsException e) {
			return this.renderErrorMessage(e.getMessage());
		} catch (RestException e) {
			return this.renderErrorMessage(e.getMessage());
		}

	}

	@GET
	@Path("/setControlValue/{id}/{value}")
	public List<XMLEvent> setControlValue(@PathParam("id") String id,
			@PathParam("value") String value) throws XFormsException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("RestService: " + this.toString());
		}
		UIEvent event = new DefaultUIEventImpl();
		event.initEvent("SETVALUE", id, value);
		List<XMLEvent> results = null;
		try {
			results = handleUIEvent(event);
		} catch (RestException e) {
			return this.renderErrorMessage(e.getMessage());
		}
		return results;

	}

	/**
	 * executes a trigger
	 * 
	 * @param id
	 *            the id of the trigger to execute
	 * @param sessionKey
	 *            the sessionKey identifying the user session
	 * @return the list of events that may result through this action
	 * @throws FluxException
	 *             raises exception if problem during processing occurs
	 */
	/*
	 * public List fireAction(String id, String sessionKey) throws FluxException
	 * { UIEvent uiActivateEvent = new DefaultUIEventImpl();
	 * uiActivateEvent.initEvent(FLUX_ACTIVATE_EVENT, id, null); return
	 * handleUIEvent(uiActivateEvent, sessionKey); }
	 */

	/**
	 * sets the value of a control in the processor.
	 * 
	 * @param id
	 *            the id of the control in the host document
	 * @param value
	 *            the new value
	 * @param sessionKey
	 *            the sessionKey identifying the user session
	 * @return the list of events that may result through this action
	 * @throws RestException
	 *             raises exception if problem during processing occurs
	 */
	// public List<XMLEvent> setXFormsValue(String id, String value, String
	// sessionKey) throws RestException {
	// if (LOGGER.isDebugEnabled()) {
	// LOGGER.debug("RestService instance: " + this.toString());
	// }
	// UIEvent event = new DefaultUIEventImpl();
	// event.initEvent("SETVALUE", id, value);
	// return handleUIEvent(event);
	// }

	/*
	 * public Element getNodeset(String id, String sessionKey) throws
	 * FluxException { try { XFormsProcessor processor =
	 * FluxUtil.getProcessor(sessionKey); XFormsElement element =
	 * processor.lookup(id); if (element instanceof BindingElement){ return
	 * ((BindingElement)element). } } catch (FluxException e) { throw new
	 * FluxException(e); }
	 * 
	 * }
	 */

	public org.w3c.dom.Element getXFormsDOM(String sessionKey)
			throws RestException {
		try {
			Element resultElem = ((Document) (getProcessor()).getXForms())
					.getDocumentElement();
			if (LOGGER.isDebugEnabled()) {
				DOMUtil.prettyPrintDOM(resultElem);
			}
			return resultElem;
		} catch (XFormsException e) {
			throw new RestException(e);
		}
	}

	public List<XMLEvent> setRepeatIndex(String id, String position)
			throws RestException {
		UIEvent event = new DefaultUIEventImpl();
		event.initEvent("SETINDEX", id, position);
		return handleUIEvent(event);
	}

	/**
	 * fetches the progress of a running upload.
	 * 
	 * @param id
	 *            id of the upload control in use
	 * @param filename
	 *            filename for uploaded data
	 * @param sessionKey
	 *            the sessionKey identifying the user session
	 * @return a array containing two elements for evaluation in browser. First
	 *         param is the upload control id and second will be the current
	 *         progress of the upload.
	 */

	public List<XMLEvent> fetchProgress(String id, String filename,
			String sessionKey) {
		String progress;
		UploadInfo uploadInfo;
		HttpSession session = getSession(request);
		if (session != null
				&& session.getAttribute(WebProcessor.ADAPTER_PREFIX
						+ sessionKey + "-uploadInfo") != null) {
			uploadInfo = (UploadInfo) session
					.getAttribute(WebProcessor.ADAPTER_PREFIX + sessionKey
							+ "-uploadInfo");

			if (uploadInfo.isInProgress()) {
				double p = uploadInfo.getBytesRead()
						/ uploadInfo.getTotalSize();

				progress = p + "";
				float total = uploadInfo.getTotalSize();
				float read = uploadInfo.getBytesRead();
				int iProgress = (int) Math.ceil((read / total) * 100);
				if (iProgress < 100) {
					progress = Integer.toString(iProgress);
				} else {
					uploadInfo.setStatus("completed");
					progress = "100";
				}
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Bytes total: " + uploadInfo.getTotalSize());
					LOGGER.debug("Bytes read: " + uploadInfo.getBytesRead());
					LOGGER.debug("elapsed time: " + uploadInfo.getElapsedTime());
					LOGGER.debug("status: " + uploadInfo.getStatus());
					LOGGER.debug("Percent completed: "
							+ Math.ceil((read / total) * 100));
				}
			} else {
				progress = "100";
			}
		} else {
			// if session info is not found for some reason return 100 for
			// safety which allows to exit
			// javascript polling of progress info
			progress = "100";
		}

		RestProcessor processor = null;
		try {
			processor = getProcessor();
		} catch (RestException r) {
			return this.renderErrorMessage(r.getMessage());
		}

		EventQueue eventQueue = processor.getEventQueue();
		XMLEvent progressEvent = eventQueue.add("upload-progress-event", id,
				"upload");
		eventQueue.addProperty(progressEvent, "progress", progress);

		List<XMLEvent> origEvents = eventQueue.getEventList();
		List<XMLEvent> eventList = new ArrayList<XMLEvent>();

		for (XMLEvent xmlEvent : origEvents) {
			eventList.add(xmlEvent);
		}
		eventQueue.flush();
		return eventList;
		// eventQueue.flush();

	}

	/**
	 * Note user typing activity (not value change), which extends session
	 * lifetime.
	 * 
	 * @param sessionKey
	 *            the sessionKey identifying the user session
	 */
	/*
	 * public void keepAlive(String sessionKey) { if (LOGGER.isDebugEnabled()) {
	 * LOGGER.debug("RestService keepAlive: " + sessionKey); }
	 * XFormsSessionManager manager = (XFormsSessionManager)
	 * session.getAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER);
	 * XFormsSession xFormsSession = manager.getWebProcessor(sessionKey);
	 * xFormsSession.updateLRU(); }
	 */
	public List<XMLEvent> setLocale(String locale, String sessionKey)
			throws RestException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("switching locale to '" + locale + "' for Session:'"
					+ sessionKey + "'");
		}
		/*
		 * XFormsSessionManager manager = (XFormsSessionManager)
		 * session.getAttribute(XFormsSessionManager.XFORMS_SESSION_MANAGER);
		 * XFormsSession xFormsSession = manager.getWebProcessor(sessionKey);
		 */

		RestProcessor processor = null;
		try {
			processor = getProcessor();
		} catch (RestException r) {
			return this.renderErrorMessage(r.getMessage());
		}

		if (processor == null) {
			XMLEvent errorEvent = new XercesXMLEventFactory()
					.createXMLEvent(BetterFormEventNames.RENDER_MESSAGE);
			Map<String, String> params = new HashMap<String, String>(1);
			params.put("message", "ERROR: RestService.setLocale(): session "
					+ sessionKey + " does not exist");
			errorEvent.initXMLEvent(BetterFormEventNames.RENDER_MESSAGE, true,
					false, params);
			ArrayList<XMLEvent> list = new ArrayList<XMLEvent>();
			list.add(errorEvent);
			return list;
		}

		Locale betterformlocale = new Locale(locale);
		processor.setContextParam(XFormsProcessorImpl.BETTERFORM_LOCALE,
				betterformlocale);

		UIEvent event = new DefaultUIEventImpl();
		event.initEvent("SETLOCALE", "locale", locale);
		return handleUIEvent(event);

	}

	/**
	 * Note page unload, which rapidly ages session.
	 * 
	 * @param sessionKey
	 *            the sessionKey identifying the user session
	 */
	@GET
	@Path("/close")
	public void close() {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("RestService close");
		}

		 try {
		
		 // don't use getWebProcessor to avoid needless error
		 
			 getProcessor().shutdown();
			 return;
		 } catch (XFormsException e) {
		 LOGGER.warn("RestService close: ", e);
		 } catch (RestException r) {
			 // Processor most likely already gone... ignore...
			 LOGGER.warn("RestService close: ", r);
		 } finally {
			 //WebUtil.removeSession(sessionKey);
		 }
	}

	private List<XMLEvent> handleUIEvent(UIEvent uiEvent) throws RestException {
		
		RestProcessor processor = getProcessor();

		try {
			processor.handleUIEvent(uiEvent);
		} catch (XFormsException e) {
			LOGGER.error(e.getMessage());
		}

		EventQueue eventQueue = processor.getEventQueue();
		List<XMLEvent> eventlog = eventQueue.getEventList();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Object ID: " + this);
			LOGGER.debug("EventLog: " + eventlog.toString());
			LOGGER.debug("FluxProcessor: " + processor);
		}
		return eventlog;
	}

	private HttpSession getSession(HttpServletRequest request) {
		return request.getSession(true);
	}

	private RestProcessor createProcessor() {
		RestProcessor processor = new RestProcessor();
		request.setAttribute("webprocessor", processor);
		return processor;
	}
	
	private RestProcessor getProcessor() throws RestException {
		WebProcessor webProcessor = WebUtil.getWebProcessor(request.getSession());

		if (webProcessor == null) {
			LOGGER.fatal("WebProcessor not found - stopping");
			throw new RestException(
					"Sorry your session expired. Press Reload to start over.");
		}
		return (RestProcessor) webProcessor;
	}

}

// end of class
