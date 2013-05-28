/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.exist;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.betterform.connector.AbstractConnector;
import de.betterform.connector.SubmissionHandler;
import de.betterform.connector.serializer.SerializerRequestWrapper;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;

/**
 * This handler submits instance data to an eXistdb instance via API.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ExistSubmissionHandler extends AbstractConnector implements SubmissionHandler {

  private static Log LOGGER = LogFactory.getLog(ExistSubmissionHandler.class);
  
  /**
   * {@inheritDoc}
   */
  public Map submit(Submission submission, Node instance) throws XFormsException {
    try {

      setContext(submission.getContainerObject().getProcessor().getContext());

      Method method = Method.valueOf(submission.getMethod().toLowerCase());

      String mediatype = "application/xml";
      if (submission.getMediatype() != null) {
        mediatype = submission.getMediatype();
      }
      
      String encoding = submission.getEncoding();
      if (submission.getEncoding() == null) {
        encoding = getDefaultEncoding();
      }

      SerializerRequestWrapper wrapper = new SerializerRequestWrapper(new ByteArrayOutputStream());
      serialize(submission, instance, wrapper);
      ByteArrayOutputStream stream = (ByteArrayOutputStream) wrapper.getBodyStream();

      boolean hasPayload = ! "".equals(stream.toString());

      switch (method) {
        case get:
          if (hasPayload && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Ignoring submission with payload for method " + Method.get);
          }
          return doGet(getURI(), mediatype, encoding);
        case post:
          if (!hasPayload && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Ignoring submission using method " + Method.post + " without payload.");
          }
          doPost(getURI(), mediatype, encoding, stream);
        case put:
          if (!hasPayload && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Ignoring submission using method " + Method.put + " without payload.");
          }
          return doPut(getURI(), mediatype, encoding, stream);
        case delete:
          if (hasPayload && LOGGER.isDebugEnabled()) {
            LOGGER.debug("Ignoring submission with payload for method " + Method.delete);
          }
          return doDelete(getURI(), mediatype, encoding);  
        default:
          throw new XFormsException("submission method '" + method + "' not supported");
      }
    } catch (Exception e) {
      throw new XFormsException(e);
    }
  }

  private Map<String, Object> doDelete(String uri, String mediatype, String encoding) throws Exception {
    boolean result = ExistUtils.deleteExistResource(uri, getContext());
    Document doc = DOMUtil.parseString("<" + result + "/>", true, false);
    
    Map<String, Object> response = new HashMap<String, Object>();
    response.put(XFormsProcessor.SUBMISSION_RESPONSE_DOCUMENT, doc);
    
    return response;
  }

  private Map<String, Object> doPut(String uri, String mediatype, String encoding, ByteArrayOutputStream stream) {
    return null;
  }

  private Map<String, Object> doPost(String uri, String mediatype, String encoding, ByteArrayOutputStream stream) {
    return null;
  }

  private Map<String, Object> doGet(String uri, String mediatype, String encoding) throws Exception{
    
    Object result = ExistUtils.getExistResource(uri, getContext());
    Document doc = DOMUtil.parseString((String) result, true, false);
    
    Map<String, Object> response = new HashMap<String, Object>();
    response.put(XFormsProcessor.SUBMISSION_RESPONSE_DOCUMENT, doc);
    
    return response;
  }

}
