/*
 * Copyright (c) 2013. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.exist;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.exist.dom.DocumentImpl;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.storage.txn.Txn;
import org.exist.xmldb.XmldbURI;
import org.exist.xquery.value.Item;
import org.exist.xquery.value.NodeValue;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.Type;

import de.betterform.xml.xforms.exception.XFormsException;

// TODO please give me a better name 
public abstract class ExistClientExecutable<T> {
  
  private Map<String, Object> context;
  private int lock;

  private URI uri;
  private XmldbURI xmldbUri;
  private ExistClient client;
  private Map<String, String> queryParameter;
  private BrokerPool pool;
  private DBBroker broker;
  
  public ExistClientExecutable() {
  }
  
  public ExistClientExecutable(Map<String, String> queryParameter) {
    this.setQueryParameter(queryParameter);
  }

  public T execute(Txn tx, BrokerPool pool, DBBroker broker, DocumentImpl xmlResource) throws Exception {
    return null;
  }
  
  // TODO setting a encoding at the submission has no effect
  protected String serialize(Sequence resultSequence) throws Exception {
    if (resultSequence == null || resultSequence.getItemCount() != 1) {
      throw new XFormsException("XML is not well formed");
    } else {
      Item item = resultSequence.itemAt(0);
      if (Type.subTypeOf(item.getType(), Type.NODE)) {
        return getDBBroker().getSerializer().serialize((NodeValue) item);
      } else if (Type.subTypeOf(item.getType(), Type.STRING)) {
        return item.getStringValue();
      } else {
        throw new XFormsException("The xquery did not return a valid XML node");
      }
    }
  }

  public Map<String, Object> getContext() {
    return context;
  }

  public void setContext(Map<String, Object> context) {
    this.context = context;
  }

  public String getUriString() {
    return uri.toString();
  }
  
  public URI getUri() throws URISyntaxException {
    return uri;
  }
  
  public XmldbURI getXmlDbUri() throws URISyntaxException {
    return xmldbUri;
  }

  public void setUriString(String uriString) throws URISyntaxException {
    this.uri = new URI(uriString);
    this.xmldbUri = XmldbURI.createInternal(uri.getRawPath());
  }

  public int getLock() {
    return lock;
  }

  public void setLock(int lock) {
    this.lock = lock;
  }
  
  public ExistClient getClient() {
    return client;
  }

  public void setClient(ExistClient client) {
    this.client = client;
  }

  public Map<String, String> getQueryParameter() {
    if (null == queryParameter) {
      this.queryParameter = new HashMap<String, String>();
    }
    return queryParameter;
  }

  public void setQueryParameter(Map<String, String> queryParameter) {
    this.queryParameter = queryParameter;
  }

  public void setBrokerPool(BrokerPool pool) {
    this.pool = pool;
  }
  
  public BrokerPool getPool() {
    return pool;
  }

  public void setDBBroker(DBBroker broker) {
    this.broker = broker;
  }
  
  public DBBroker getDBBroker() {
    return broker;
  }
}
