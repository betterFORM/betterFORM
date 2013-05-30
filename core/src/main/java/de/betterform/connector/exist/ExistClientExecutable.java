/*
 * Copyright (c) 2013. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.exist;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.exist.dom.DocumentImpl;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.storage.txn.Txn;
import org.exist.xmldb.XmldbURI;

// TODO please give me a better name 
public abstract class ExistClientExecutable<T> {
  
  private Map<String, Object> context;
  private int lock;

  private URI uri;
  private XmldbURI xmldbUri;
  
  public T execute(Txn tx, BrokerPool pool, DBBroker broker, DocumentImpl xmlResrouce) throws Exception {
    return null;
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
}
