package de.betterform.connector.exist;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exist.EXistException;
import org.exist.collections.Collection;
import org.exist.dom.BinaryDocument;
import org.exist.dom.DocumentImpl;
import org.exist.dom.QName;
import org.exist.security.Subject;
import org.exist.security.xacml.AccessContext;
import org.exist.source.DBSource;
import org.exist.source.Source;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.storage.lock.Lock;
import org.exist.storage.serializers.Serializer;
import org.exist.storage.txn.TransactionManager;
import org.exist.storage.txn.Txn;
import org.exist.util.MimeType;
import org.exist.xmldb.XmldbURI;
import org.exist.xquery.AnalyzeContextInfo;
import org.exist.xquery.CompiledXQuery;
import org.exist.xquery.ExternalModule;
import org.exist.xquery.FunctionCall;
import org.exist.xquery.Module;
import org.exist.xquery.UserDefinedFunction;
import org.exist.xquery.XQuery;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.FunctionReference;
import org.exist.xquery.value.Item;
import org.exist.xquery.value.NodeValue;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.StringValue;
import org.exist.xquery.value.Type;

import de.betterform.connector.util.URIUtils;
import de.betterform.xml.xforms.exception.XFormsException;

public class ExistUtils {
  
  private static Log LOGGER = LogFactory.getLog(ExistUtils.class);

  public static int calculateArity(Map<String, String> queryParameter) {
    int i = 0;
    for (String key : queryParameter.keySet()) {
      if (key.equals("type") || key.equals("function")) {
        continue;
      }
      ++i;
    }
    return i;
  }

  public static List<Sequence> getAsFunctionParameters(Map<String, String> queryParameter) {
    if (null == queryParameter) {
      return Collections.emptyList();
    }

    List<Sequence> results = new ArrayList<Sequence>();
    for (String key : queryParameter.keySet()) {
      if (key.equals("type") || key.equals("function")) {
        continue;
      }
      results.add(new StringValue(queryParameter.get(key)));
    }
    return results;
  }

  @SuppressWarnings("rawtypes")
  public static DBBroker getBroker(BrokerPool pool, Map context) throws EXistException {
    Subject subject = getSubject(pool, context);
    DBBroker broker = pool.get(subject);
    return broker;
  }

  @SuppressWarnings("rawtypes")
  private static Subject getSubject(BrokerPool pool, Map context) {
    Subject subject = (Subject) context.get("_eXist_xmldb_user");
    if (null != subject) {
      return subject;
    }
    return pool.getSecurityManager().getGuestSubject();
  }

  public static boolean deleteExistResource(String uriString, Map<String, Object> context) throws XFormsException {
    BrokerPool pool = null;
    DBBroker broker = null;
    DocumentImpl xmlResource = null;

    try {
      URI dbURI = new URI(uriString);
      pool = BrokerPool.getInstance();
      broker = ExistUtils.getBroker(pool, context);

      XmldbURI docUri = XmldbURI.createInternal(dbURI.getRawPath());
      xmlResource = broker.getResource(docUri, Lock.WRITE_LOCK);
      TransactionManager txManager = pool.getTransactionManager();
      Txn tx = null;
      
      try {
        tx = txManager.beginTransaction();

        Collection collection = broker.getCollection(docUri);
        if (collection != null) {
            broker.removeCollection(tx, collection);
            txManager.commit(tx);
            
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("collection removed: '" + collection.getURI() + "'");
            }
            return true;
        } else {
            if (xmlResource.getResourceType() == DocumentImpl.BINARY_FILE) {
              xmlResource.getCollection().removeBinaryResource(tx, broker, docUri.lastSegment());
            } else {
              xmlResource.getCollection().removeXMLResource(tx, broker, docUri.lastSegment());
            }
            
            txManager.commit(tx);

            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("document removed: '" + docUri.getURI() + "'");
            }
            
            return true;
          } 
      } catch (Exception e) {
        txManager.abort(tx);
        throw new EXistException(e.getMessage(), e);
      }
    } catch (Exception e) {
      throw new XFormsException(e.getMessage(), e);
    } finally {
      if (pool != null) {
        if (xmlResource != null) {
          xmlResource.getUpdateLock().release(Lock.WRITE_LOCK);
        }
        pool.release(broker);
      }
    }
  }

  public static Object getExistResource(String uriString, Map<String, Object> context) throws XFormsException {

    BrokerPool pool = null;
    DBBroker broker = null;
    DocumentImpl xmlResource = null;

    try {
      URI dbURI = new URI(uriString);
      pool = BrokerPool.getInstance();
      broker = ExistUtils.getBroker(pool, context);

      xmlResource = broker.getResource(XmldbURI.createInternal(dbURI.getRawPath()), Lock.READ_LOCK);
      Serializer serializer = broker.getSerializer();

      if (xmlResource == null) {
        throw new XFormsException("eXist XML Resource is null");
      } else if (xmlResource.getResourceType() == DocumentImpl.XML_FILE) {
        String serialized = serializer.serialize(xmlResource);
        return serialized;
      } else if (MimeType.XQUERY_TYPE.getName().equals(xmlResource.getMetadata().getMimeType())) {

        Sequence resultSequence = null;
        Map<String, String> queryParameter = URIUtils.getQueryParameters(dbURI);
        boolean isModule = URIUtils.hasParameterFromMap(queryParameter, "type", "module");

        if (isModule) {
          XQueryContext tempContext = new XQueryContext(pool, AccessContext.REST);
          tempContext.setModuleLoadPath(xmlResource.getCollection().getURI().toString());
          Module module = tempContext.importModule(null, null, "xmldb:exist://" + dbURI.getRawPath());

          if (!URIUtils.hasParameterFromMap(queryParameter, "function")) {
            throw new XFormsException("no function provided as query parameter");
          }

          String function = queryParameter.get("function");
          QName qname = new QName(function, module.getNamespaceURI(), module.getDefaultPrefix());
          int arity = ExistUtils.calculateArity(queryParameter);
          UserDefinedFunction udf = ((ExternalModule) module).getFunction(qname, arity, tempContext);

          final FunctionReference fnRef = new FunctionReference(new FunctionCall(tempContext, udf));
          fnRef.analyze(new AnalyzeContextInfo());
          Sequence[] parameters = ExistUtils.getAsFunctionParameters(queryParameter).toArray(new Sequence[] {});
          resultSequence = fnRef.evalFunction(null, null, parameters);
          udf.resetState(true);

        } else {
          Source source = new DBSource(broker, ((BinaryDocument) xmlResource), true);
          XQuery xquery = broker.getXQueryService();
          XQueryContext xqcontext = xquery.newContext(AccessContext.REST);
          xqcontext.setModuleLoadPath(xmlResource.getCollection().getURI().toString());
          xqcontext.setStaticallyKnownDocuments(new XmldbURI[] { xmlResource.getCollection().getURI() });
          CompiledXQuery compiledXQuery = xquery.compile(xqcontext, source);
          resultSequence = xquery.execute(compiledXQuery, Sequence.EMPTY_SEQUENCE);
        }

        if (resultSequence == null || resultSequence.getItemCount() != 1) {
          throw new XFormsException("XML is not well formed");
        } else {
          Item item = resultSequence.itemAt(0);
          if (Type.subTypeOf(item.getType(), Type.NODE)) {
            return serializer.serialize((NodeValue) item);
          } else {
            throw new XFormsException("The xquery did not return a valid XML node");
          }
        }
      }
    } catch (Exception e) {
      throw new XFormsException(e.getMessage(), e);
    } finally {
      if (pool != null) {
        if (xmlResource != null) {
          xmlResource.getUpdateLock().release(Lock.READ_LOCK);
        }
        pool.release(broker);
      }
    }

    return null;
  }

}
