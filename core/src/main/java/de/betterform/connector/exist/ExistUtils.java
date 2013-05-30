package de.betterform.connector.exist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.exist.collections.Collection;
import org.exist.dom.BinaryDocument;
import org.exist.dom.DocumentImpl;
import org.exist.dom.QName;
import org.exist.security.xacml.AccessContext;
import org.exist.source.DBSource;
import org.exist.source.Source;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.storage.lock.Lock;
import org.exist.storage.serializers.Serializer;
import org.exist.storage.txn.Txn;
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
import org.w3c.dom.Document;

import de.betterform.connector.util.URIUtils;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.exception.XFormsException;

public class ExistUtils {

  private static final String EXIST_PROTOCOLL = "xmldb:exist://";
  public static final String URL_PARAM_FUNCTION = "function";
  public static final String URL_PARAM_TYPE = "type";
  public static final String URL_PARAM_TYPE_MODULE = "module";

  public static int getXQueryFunctionArity(Map<String, String> queryParameter) {
    int i = 0;
    for (String key : queryParameter.keySet()) {
      if (isTypeOrFunctionParameter(key)) {
        continue;
      }
      ++i;
    }
    return i;
  }

  public static ExistResourceType getExistResourceType(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Collection collection) throws Exception {
    
    if (null == xmlResource && null == collection) {
      throw new XFormsException("eXist resource is null");
    }
    
    if (collection != null) {
      return ExistResourceType.COLLECTION;
    } else if (xmlResource.getResourceType() == DocumentImpl.XML_FILE) {
      return ExistResourceType.XML;
    } else if (xmlResource.getResourceType() == DocumentImpl.BINARY_FILE) {
      return ExistResourceType.BINARY;
    }
    throw new XFormsException("Undefined eXist resource");
  }

  public static List<Sequence> getAsFunctionParameters(Map<String, String> queryParameter) {
    if (null == queryParameter) {
      return Collections.emptyList();
    }

    List<Sequence> results = new ArrayList<Sequence>();
    for (String key : queryParameter.keySet()) {
      if (isTypeOrFunctionParameter(key)) {
        continue;
      }
      results.add(new StringValue(queryParameter.get(key)));
    }
    return results;
  }

  private static boolean isTypeOrFunctionParameter(String key) {
    return URL_PARAM_TYPE.equals(key) || URL_PARAM_FUNCTION.equals(key);
  }

  public static String getExistResource(String uri, Map<String, Object> context) throws XFormsException {

    ExistClient existClient = new ExistClient();
    String result = existClient.execute(uri, Lock.READ_LOCK, context, new ExistResourceTypeCallback<String>() {

      @Override
      public String onCollection(BrokerPool pool, DBBroker broker, Collection collection, Txn tx) throws Exception {
        // TODO return a directory listing
        throw new XFormsException("'" + getUriString() + "' is not a eXist-db resource");
      }

      @Override
      public String onXMLResource(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Txn tx) throws Exception {
        Serializer serializer = broker.getSerializer();
        String serialized = serializer.serialize(xmlResource);
        return serialized;
      }

      @Override
      public String onBinaryResource(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Txn tx) throws Exception {
        Sequence resultSequence = null;

        Map<String, String> queryParameter = URIUtils.getQueryParameters(getUri());
        boolean isModule = URIUtils.hasParameterFromMap(queryParameter, URL_PARAM_TYPE, URL_PARAM_TYPE_MODULE);

        if (isModule) {
          XQueryContext tempContext = new XQueryContext(pool, AccessContext.REST);
          tempContext.setModuleLoadPath(xmlResource.getCollection().getURI().toString());
          Module module = tempContext.importModule(null, null, EXIST_PROTOCOLL + getUri().getRawPath());

          if (!URIUtils.hasParameterFromMap(queryParameter, URL_PARAM_FUNCTION)) {
            throw new XFormsException("no function provided as query parameter");
          }

          String function = queryParameter.get(URL_PARAM_FUNCTION);
          QName qname = new QName(function, module.getNamespaceURI(), module.getDefaultPrefix());
          int arity = ExistUtils.getXQueryFunctionArity(queryParameter);
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
            Serializer serializer = broker.getSerializer();
            return serializer.serialize((NodeValue) item);
          } else {
            throw new XFormsException("The xquery did not return a valid XML node");
          }
        }
      }
    });
    return result;
  }

  public static Document getExistResourceAsDocument(String uri, Map<String, Object> context) throws Exception {
    String resultString = getExistResource(uri, context);
    Document result = DOMUtil.parseString(resultString, true, false);
    return result;
  }

  public static boolean isExistCollection(String uri, Map<String, Object> context) throws XFormsException {
    ExistClient existClient = new ExistClient();
    Boolean result = existClient.execute(uri, Lock.READ_LOCK, context, new ExistResourceTypeCallback<Boolean>() { 
      @Override
      public Boolean onCollection(BrokerPool posol, DBBroker broker, Collection collection, Txn tx) throws Exception {
        return Boolean.TRUE;
      }
    });
    return result;
  }
}
