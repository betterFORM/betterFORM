/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
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
import org.exist.dom.DocumentImpl;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.storage.lock.Lock;
import org.exist.storage.txn.Txn;
import org.exist.xmldb.XmldbURI;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

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

      boolean hasPayload = !"".equals(stream.toString());

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
  
  private Map<String, Object> doGet(String uri, String mediatype, String encoding) throws Exception {

    Document result = ExistUtils.getExistResourceAsDocument(uri, getContext());

    Map<String, Object> response = new HashMap<String, Object>();
    response.put(XFormsProcessor.SUBMISSION_RESPONSE_DOCUMENT, result);

    return response;
  }

  /**
   * put is used to create or update an named resource. to create a resource,
   * the client is responsible to create an unique resource name.
   */
  private Map<String, Object> doPut(final String uri, final String mediatype, final String encoding, final ByteArrayOutputStream stream) throws Exception {

    existClient.execute(uri, Lock.WRITE_LOCK, getContext(), new ExistClientExecutable<DocumentImpl>() {

      @Override
      public DocumentImpl execute(Txn tx, BrokerPool pool, DBBroker broker, DocumentImpl xmlResrouce) throws Exception {

        String uri = getUri().getRawPath();
        String file = uri.substring(uri.lastIndexOf('/') + 1);
        String path = uri.substring(0, uri.lastIndexOf('/'));

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

  /**
   * post is used to send payload to an server endpoint without knowing the
   * future resourcename. in this scenario, the client will only post the
   * payload to an collection endpoint. the server creates automatically an
   * resource with an unique resourcename . normally, this resourcename is
   * returned using a location header.
   */
  private Map<String, Object> doPost(String uri, String mediatype, String encoding, ByteArrayOutputStream stream) throws Exception {

    if (!ExistUtils.isExistCollection(uri, getContext())) {
      throw new XFormsException("POST is only eligible against collection urls");
    }
    uri = uri + "/" + System.currentTimeMillis() + ".xml";
    Map<String, Object> response = doPut(uri, mediatype, encoding, stream);
    
    // TODO
    // we need tell the new ur of this resource to the 
    // client (eg. using a location header)
    setURI(uri);
    
    return response;
  }

}
