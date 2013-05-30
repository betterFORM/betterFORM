package de.betterform.connector.exist;

import org.exist.collections.Collection;
import org.exist.dom.DocumentImpl;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.storage.txn.Txn;

// TODO please give me a better name 
public class ExistResourceTypeCallback<T> extends ExistClientExecutable<T> {

  public T execute(Txn tx, BrokerPool pool, DBBroker broker, DocumentImpl xmlResrouce) throws Exception {

    Collection collection = broker.getCollection(getXmlDbUri());
    DocumentImpl xmlResource = broker.getResource(getXmlDbUri(), getLock());

    switch (ExistUtils.getExistResourceType(pool, broker, xmlResource, collection)) {
    case COLLECTION:
      return onCollection(pool, broker, collection, tx);
    case XML:
      return onXMLResource(pool, broker, xmlResource, tx);
    default:
      return onBinaryResource(pool, broker, xmlResource, tx);
    }
  }

  public T onCollection(BrokerPool posol, DBBroker broker, Collection collection, Txn tx) throws Exception {
    return null;
  }

  public T onBinaryResource(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Txn tx) throws Exception {
    return null;
  }

  public T onXMLResource(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Txn tx) throws Exception {
    return null;
  }
}
