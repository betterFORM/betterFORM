/*
 * Copyright (c) 2013. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.exist;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exist.collections.Collection;
import org.exist.collections.IndexInfo;
import org.exist.dom.persistent.DocumentImpl;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.storage.lock.Lock;
import org.exist.storage.txn.Txn;
import org.exist.xmldb.XmldbURI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.betterform.connector.AbstractConnector;
import de.betterform.connector.SubmissionHandler;
import de.betterform.connector.serializer.SerializerRequestWrapper;
import de.betterform.connector.util.URIUtil;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;

/**
 * This handler submits instance data to an eXistdb instance via API.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ExistSubmissionHandler extends AbstractConnector implements SubmissionHandler {

  private static Log LOGGER = LogFactory.getLog(ExistSubmissionHandler.class);

  private ExistClient existClient;

  public ExistSubmissionHandler() {
    this.existClient = new ExistClient();
  }

  /**
   * {@inheritDoc}
   */
  public Map submit(Submission submission, Node instance) throws XFormsException {
    try {

      setContext(submission.getContainerObject().getProcessor().getContext());

      ExistConnectorMethod method = ExistConnectorMethod.valueOf(submission.getMethod().toUpperCase());

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

      boolean hasPayload = !"".equals(stream.toString());
      
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("submitting with method='" + method + "', has payload='" + hasPayload + "'");
      }

      switch (method) {
      case GET:
        return doGet(getURI(), mediatype, encoding);
      case EXECUTE:
        return doExecute(getURI(), mediatype, encoding, stream);
      case POST:
        return doPut(getURI(), mediatype, encoding, stream);
      case PUT:
        return doPut(getURI(), mediatype, encoding, stream);
      case DELETE:
        return doDelete(getURI(), mediatype, encoding);
      default:
        throw new XFormsException("submission method '" + method + "' not supported");
      }
    } catch (Exception e) {
      Map<String, Object> failure = new HashMap<String, Object>();
      failure.put("response-reason-phrase", e.getMessage());
      failure.put("error-type", "target-error");
      return failure;
    }
  }

  private Map<String, Object> doExecute(String uri, String mediatype, String encoding, final ByteArrayOutputStream stream) throws Exception {
    
    String resultString = ExistUtil.executeXQuery(uri, getContext(), mediatype, encoding, stream);
    
    Map<String, Object> result = new HashMap<String, Object>();
    result.put(XFormsProcessor.SUBMISSION_RESPONSE_DOCUMENT, DOMUtil.parseString(resultString, true, true));
    return result;
    
  }
  
  private Map<String, Object> doGet(String uri, String mediatype, String encoding) throws Exception {

    String response = null;
    if(URIUtil.hasFileExtension(uri, ExistUtil.EXTENSION_XQUERY, ExistUtil.EXTENSION_XQUERY_MODULE)) {
      response = ExistUtil.executeXQuery(uri, getContext(), mediatype, encoding, null);
    } else {
      response = ExistUtil.getExistResource(uri, getContext(), mediatype, encoding, null);
    }
    
    Document result = DOMUtil.parseString(response, true, true);
    Map<String, Object> resultMap = new HashMap<String, Object>();
    resultMap.put(XFormsProcessor.SUBMISSION_RESPONSE_DOCUMENT, result);
    return resultMap;
  }

  private Map<String, Object> doDelete(String uri, String mediatype, String encoding) throws Exception {

    Boolean result = existClient.execute(uri, Lock.WRITE_LOCK, getContext(), new ExistResourceTypeCallback<Boolean>() {

      @Override
      public Boolean onCollection(BrokerPool pool, DBBroker broker, Collection collection, Txn tx) throws Exception {
        broker.removeCollection(tx, collection);
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("collection removed: '" + collection.getURI() + "'");
        }
        return Boolean.TRUE;
      };

      @Override
      public Boolean onBinaryResource(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Txn tx) throws Exception {
        xmlResource.getCollection().removeBinaryResource(tx, broker, getXmlDbUri().lastSegment());
        return Boolean.TRUE;
      }

      @Override
      public Boolean onXMLResource(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Txn tx) throws Exception {
        xmlResource.getCollection().removeXMLResource(tx, broker, getXmlDbUri().lastSegment());
        return Boolean.TRUE;
      }

    });

    Document doc = DOMUtil.parseString("<operation type='delete' uri='" + uri + "' successful='" + result + "'/>", true, false);

    Map<String, Object> response = new HashMap<String, Object>();
    response.put(XFormsProcessor.SUBMISSION_RESPONSE_DOCUMENT, doc);

    return response;
  }
  

  private Map<String, Object> doPut(String uri, final String mediatype, final String encoding, final ByteArrayOutputStream stream) throws Exception {

    Map<String, Object> resultMap = new HashMap<String, Object>();
    
    // TODO we may want to use another approach to generate filenames
    if (ExistUtil.isExistCollection(uri, getContext())) {
      uri = uri + "/" + System.currentTimeMillis() + ".xml";
      resultMap.put("resource-uri", uri);
    }
    
    existClient.execute(uri, Lock.WRITE_LOCK, getContext(), new ExistClientExecutable<DocumentImpl>() {

      @Override
      public DocumentImpl execute(Txn tx, BrokerPool pool, DBBroker broker, DocumentImpl xmlResource) throws Exception {

        String file = URIUtil.getLastSegmentFromPath(getUri());
        String path = URIUtil.getPathWithoutLastSegment(getUri());

        DocumentImpl resource = broker.getResource(getXmlDbUri(), Lock.WRITE_LOCK);
        Collection collection = null;

        if (null == resource) {
          XmldbURI pathUri = XmldbURI.createInternal(path);
          collection = broker.getCollection(pathUri);

        } else {
          collection = resource.getCollection();
        }

        // try to avoid byte[]
        byte[] data = stream.toByteArray();
        String contents = new String(data, Charset.forName(encoding));
        
        IndexInfo info = collection.validateXMLResource(tx, broker, XmldbURI.createInternal(file), contents);
        collection.store(tx, broker, info, contents, false);
        
        return info.getDocument();
      }
    });
    
    return doGet(uri, mediatype, encoding);
  }
}
