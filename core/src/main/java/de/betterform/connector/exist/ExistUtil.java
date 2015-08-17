/*
 * Copyright (c) 2013. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.exist;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.exist.collections.Collection;
import org.exist.dom.persistent.BinaryDocument;
import org.exist.dom.persistent.DocumentImpl;
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
import org.exist.xquery.XPathException;
import org.exist.xquery.XQuery;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.FunctionReference;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.StringValue;

import de.betterform.connector.util.URIUtil;
import de.betterform.xml.xforms.exception.XFormsException;

public class ExistUtil {

  public static final String EXIST_PROTOCOLL = "xmldb:exist://";
  public static final String URL_PARAM_FUNCTION = "function";
  public static final String URL_PARAM_XPATH = "xpath";
  public static final String URL_PARAM_XSL = "xsl";
  
  public static final String EXTENSION_XQUERY = "xql";
  public static final String EXTENSION_XQUERY_MODULE = "xqm";
  
  private static final String XQUERY_CONTEXT_VARIABLE_NAME = "data";

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
    return URL_PARAM_FUNCTION.equals(key) || URL_PARAM_XPATH.equals(key) || URL_PARAM_XSL.equals(key);
  }

  public static ExistResourceType getExistResourceType(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Collection collection, Map<String, String> queryParameter, String uriString) throws Exception {

    if (null == xmlResource && null == collection) {
      return ExistResourceType.NOT_FOUND;
    }

    if (collection != null) {
      return ExistResourceType.COLLECTION;
    } else if (xmlResource.getResourceType() == DocumentImpl.XML_FILE) {
      return ExistResourceType.XML;
    } else if (xmlResource.getResourceType() == DocumentImpl.BINARY_FILE) {
      if (URIUtil.hasFileExtension(uriString, EXTENSION_XQUERY_MODULE)) {
        return ExistResourceType.XQUERY_MODULE;
      }
      if (URIUtil.hasFileExtension(uriString, EXTENSION_XQUERY)) {
        return ExistResourceType.XQUERY;
      }
      return ExistResourceType.BINARY;
    }
    throw new XFormsException("Undefined eXist resource");
  }

  public static boolean isExistCollection(String uri, Map<String, Object> context) throws XFormsException {
    ExistClient existClient = new ExistClient();
    Boolean result = existClient.execute(uri, Lock.READ_LOCK, context, new ExistResourceTypeCallback<Boolean>() {
      @Override
      public Boolean onCollection(BrokerPool posol, DBBroker broker, Collection collection, Txn tx) throws Exception {
        return Boolean.TRUE;
      }
      
      @Override
      public Boolean defaultOperation(BrokerPool pool, DBBroker broker, Txn tx) throws Exception {
        return Boolean.FALSE;
      }
    });
    return result;
  }

  public static XQueryContext getXQueryContext(XQuery xquery, DocumentImpl xmlResource, ByteArrayOutputStream data, String encoding) throws XPathException, UnsupportedEncodingException {
    XQueryContext xqcontext = xquery.newContext(AccessContext.REST);
    
    xqcontext.setModuleLoadPath(xmlResource.getCollection().getURI().toString());
    xqcontext.setStaticallyKnownDocuments(new XmldbURI[] { xmlResource.getCollection().getURI() });
    
    if (null != data) {
      QName qn = new QName(XQUERY_CONTEXT_VARIABLE_NAME, "");
      String value = new String(data.toByteArray(), encoding);
      xqcontext.declareVariable(qn, value);
    }
    
    return xqcontext;
  }

  public static String executeXQuery(String uri, Map<String, Object> context, String mediatype, final String encoding, final ByteArrayOutputStream stream) throws Exception {
    ExistClient existClient = new ExistClient();
    final Map<String, String> queryParameter = URIUtil.getQueryParameters(new URI(uri));
    return existClient.execute(uri, Lock.READ_LOCK, context, new ExistResourceTypeCallback<String>()  {
      
      @Override
      public String onXQuery(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Txn tx) throws Exception {
        Source source = new DBSource(broker, ((BinaryDocument) xmlResource), true);
        XQuery xquery = broker.getXQueryService();
        XQueryContext xqcontext = ExistUtil.getXQueryContext(xquery, xmlResource, stream, encoding);
        CompiledXQuery compiledXQuery = xquery.compile(xqcontext, source);
        Sequence resultSequence = xquery.execute(compiledXQuery, Sequence.EMPTY_SEQUENCE);
        return serialize(resultSequence);
      }
      
      @Override
      public String onXQueryModule(BrokerPool pool, DBBroker broker, DocumentImpl xmlResource, Txn tx) throws Exception {
        XQuery xquery = broker.getXQueryService();
        XQueryContext xqContext = ExistUtil.getXQueryContext(xquery, xmlResource, stream, encoding);
        
        Module module = xqContext.importModule(null, null, ExistUtil.EXIST_PROTOCOLL + getUri().getRawPath());
        String function = queryParameter.get(ExistUtil.URL_PARAM_FUNCTION);
        QName qname = new QName(function, module.getNamespaceURI(), module.getDefaultPrefix());
        int arity = ExistUtil.getXQueryFunctionArity(queryParameter);
        
        UserDefinedFunction udf = ((ExternalModule) module).getFunction(qname, arity, xqContext);
        final FunctionReference fnRef = new FunctionReference(new FunctionCall(xqContext, udf));
        fnRef.analyze(new AnalyzeContextInfo());
        Sequence[] parameters = ExistUtil.getAsFunctionParameters(queryParameter).toArray(new Sequence[] {});
        Sequence resultSequence = fnRef.evalFunction(null, null, parameters);
        udf.resetState(true);
        
        return serialize(resultSequence);
      }
    });
  }

  public static String getExistResource(String uri, Map<String, Object> context, String mediatype, String encoding, Object object) throws Exception {
    ExistClient client = new ExistClient();
    String result = client.execute(uri, Lock.READ_LOCK, context, new ExistClientExecutable<String>() {
      
      @Override
      public String execute(Txn tx, BrokerPool pool, DBBroker broker, DocumentImpl xmlResource) throws Exception {
        if (null == xmlResource) {
          throw new XFormsException("resource not found: '" + getUriString() + "'");
        }
        Serializer serializer = broker.getSerializer();
        String serialized = serializer.serialize(xmlResource);
        return serialized;
      }
    });
    return result;
  }
}

