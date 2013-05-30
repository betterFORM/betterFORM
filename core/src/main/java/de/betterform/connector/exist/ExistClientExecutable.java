package de.betterform.connector.exist;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.exist.dom.DocumentImpl;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.storage.txn.Txn;
import org.exist.xmldb.XmldbURI;

/**
 * for this executable either {@link #execute(Txn, BrokerPool, DBBroker)} or {@link #execute(Txn, BrokerPool, DBBroker, DocumentImpl)}
 * is called by {@link ExistClient}
 */
public abstract class ExistClientExecutable<T> {
  
  private Map<String, Object> context;
  private int lock;

  private URI uri;
  private XmldbURI xmldbUri;
  
  /**
   * this method is called, if xmlResouce is not null
   * @param tx
   * @param pool
   * @param broker
   * @param xmlResrouce
   * @return
   * @throws Exception
   */
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
