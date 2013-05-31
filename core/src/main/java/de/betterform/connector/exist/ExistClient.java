/*
 * Copyright (c) 2013. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.exist;

import java.net.URI;
import java.util.Map;

import org.exist.dom.DocumentImpl;
import org.exist.security.Subject;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.storage.txn.TransactionManager;
import org.exist.storage.txn.Txn;
import org.exist.xmldb.XmldbURI;

import de.betterform.xml.xforms.exception.XFormsException;

public class ExistClient {

  private static final String EXIST_USER_KEY = "_eXist_xmldb_user";

  public <T> T execute(String uri, int lock, Map<String, Object> context, ExistClientExecutable<T> exec) throws XFormsException {
    BrokerPool pool = null;
    DBBroker broker = null;
    Txn tx = null;
    TransactionManager txManager = null;
    DocumentImpl xmlResource = null;

    try {
      pool = BrokerPool.getInstance();
      Subject subject = getSubject(pool, context);
      broker = pool.get(subject);
      txManager = pool.getTransactionManager();

      if (null != uri) {
        URI dbURI = new URI(uri);
        XmldbURI docURI = XmldbURI.createInternal(dbURI.getRawPath());
        xmlResource = broker.getResource(docURI, lock);
      }

      try {
        tx = txManager.beginTransaction();

        exec.setUriString(uri);
        exec.setLock(lock);
        exec.setContext(context);

        T result = exec.execute(tx, pool, broker, xmlResource);

        txManager.commit(tx);
        return result;

      } catch (Exception e) {
        if (null != txManager) {
          txManager.abort(tx);
        }
        throw new XFormsException(e);
      } finally {
        if (null != pool) {
          if (null != xmlResource) {
            xmlResource.getUpdateLock().release(lock);
          }
          pool.release(broker);
        }
      }
    } catch (Exception e) {
      throw new XFormsException(e);
    }
  }

  @SuppressWarnings("rawtypes")
  private Subject getSubject(BrokerPool pool, Map context) {
    Subject subject = (Subject) context.get(EXIST_USER_KEY);
    if (null != subject) {
      return subject;
    }
    return pool.getSecurityManager().getGuestSubject();
  }
}