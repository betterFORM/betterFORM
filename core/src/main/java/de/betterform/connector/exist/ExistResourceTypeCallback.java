/*
 * Copyright (c) 2013. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.exist;

import org.exist.collections.Collection;
import org.exist.dom.persistent.DocumentImpl;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.storage.txn.Txn;

// TODO please give me a better name 
public abstract class ExistResourceTypeCallback<T> extends ExistClientExecutable<T> {

  @Override
  public T execute(Txn tx, BrokerPool pool, DBBroker broker, DocumentImpl xmlResrouce) throws Exception {

    Collection collection = broker.getCollection(getXmlDbUri());
    DocumentImpl xmlResource = broker.getResource(getXmlDbUri(), getLock());
    switch (ExistUtil.getExistResourceType(pool, broker, xmlResource, collection, getQueryParameter(), getUriString())) {
    case COLLECTION:
      return onCollection(pool, broker, collection, tx);
    case XML:
      return onXMLResource(pool, broker, xmlResource, tx);
    case XQUERY:
      return onXQuery(pool, broker, xmlResource, tx);
    case XQUERY_MODULE:
      return onXQueryModule(pool, broker, xmlResource, tx);
    case BINARY:
      return onBinaryResource(pool, broker, xmlResource, tx);
    default:
      return onResourceNotFound(pool, broker, tx);
    }
  }
  
  public T defaultOperation(BrokerPool pool, DBBroker broker, Txn tx) throws Exception {
    return null;
  }
  
  public T onResourceNotFound(BrokerPool pool, DBBroker broker, Txn tx) throws Exception {
    return defaultOperation(pool, broker, tx);
  }

  public T onCollection(BrokerPool pool, DBBroker broker, Collection collection, Txn tx) throws Exception {
    return defaultOperation(pool, broker, tx);
  }

  public T onBinaryResource(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Txn tx) throws Exception {
    return defaultOperation(pool, broker, tx);
  }

  public T onXMLResource(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Txn tx) throws Exception {
    return defaultOperation(pool, broker, tx);
  }

  public T onXQueryModule(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Txn tx) throws Exception {
    return onBinaryResource(pool, broker, xmlResource, tx);
  }
  
  public T onXQuery(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Txn tx) throws Exception {
    return onBinaryResource(pool, broker, xmlResource, tx);
  }
}
