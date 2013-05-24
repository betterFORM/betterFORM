/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.exist;

import java.net.URI;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exist.EXistException;
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
import org.exist.xquery.value.Type;

import de.betterform.connector.URIResolver;
import de.betterform.connector.http.AbstractHTTPConnector;
import de.betterform.connector.util.URIUtils;
import de.betterform.xml.xforms.exception.XFormsException;

/**
 * This resolver is eXistdb-specific and allows resolving URIs when directly running within eXist. It does
 * not need to use a network and will retrieve XML directly from eXistdb via (XMLDB ?) API.
 *
 *
 */
public class ExistURIResolver extends AbstractHTTPConnector implements URIResolver {

    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(ExistURIResolver.class);

    /**
     * resolve URI against root of eXistdb instance e.g. '/db/data/someData/one.xml[//someXPath]'
     *
     * @return a DOM node or document from the URI resolution
     * @throws de.betterform.xml.xforms.exception.XFormsException if any error occurred during link traversal.
     * @throws EXistException 
     */
    public Object resolve() throws XFormsException {

        String uriString = getURI();
        BrokerPool pool = null;
        DBBroker broker = null;
        DocumentImpl xmlResource = null;
        
        try {
            URI dbURI = new URI(uriString);
            pool = BrokerPool.getInstance();
            broker = ExistUtils.getBroker(pool, getContext());

            xmlResource = broker.getResource(XmldbURI.createInternal(dbURI.getRawPath()), Lock.READ_LOCK);
            Serializer serializer = broker.getSerializer();

            if(xmlResource == null) {
                throw new XFormsException("eXist XML Resource is null");
            }
            else if(xmlResource.getResourceType() == DocumentImpl.XML_FILE){
                String serialized = serializer.serialize(xmlResource);
                if (LOGGER.isTraceEnabled()) {
                  LOGGER.trace(serialized);
                }
                return serialized;
            } else if(MimeType.XQUERY_TYPE.getName().equals(xmlResource.getMetadata().getMimeType())){
                
                Sequence resultSequence = null;
                Map<String, String> queryParameter = URIUtils.getQueryParameters(dbURI);
                boolean isModule = URIUtils.hasParameterFromMap(queryParameter, "type", "module");
                
                if(isModule){
                    XQueryContext tempContext = new XQueryContext(pool, AccessContext.REST);
                    tempContext.setModuleLoadPath(xmlResource.getCollection().getURI().toString());
                    Module module = tempContext.importModule(null, null, "xmldb:exist://"+dbURI.getRawPath());
                    
                    if (!URIUtils.hasParameterFromMap(queryParameter, "function")) {
                      throw new XFormsException("No Function provided as Query Parameter");
                    }
                    String function = queryParameter.get("function");
                    QName qname = new QName(function, module.getNamespaceURI(), module.getDefaultPrefix());
                    int arity = ExistUtils.calculateArity(queryParameter);
                    UserDefinedFunction udf = ((ExternalModule)module).getFunction(qname, arity, tempContext);

                    final FunctionReference fnRef = new FunctionReference(new FunctionCall(tempContext, udf));
                    fnRef.analyze(new AnalyzeContextInfo());
                    Sequence[] parameters = ExistUtils.getFunctionParameters(queryParameter).toArray(new Sequence[]{});
                    resultSequence = fnRef.evalFunction(null, null, parameters);
                    udf.resetState(true);
                    
                } else {
                    Source source = new DBSource(broker, ((BinaryDocument)xmlResource),true);
                    XQuery xquery = broker.getXQueryService();
                    XQueryContext context = xquery.newContext(AccessContext.REST);
                    context.setModuleLoadPath(xmlResource.getCollection().getURI().toString());
                    context.setStaticallyKnownDocuments(new XmldbURI[]{xmlResource.getCollection().getURI()});
                    CompiledXQuery compiledXQuery = xquery.compile(context, source);
                    resultSequence = xquery.execute(compiledXQuery, Sequence.EMPTY_SEQUENCE);
                }

                if(resultSequence == null || resultSequence.getItemCount() != 1){
                    throw new XFormsException("XML is not well formed");
                } else {
                    Item item = resultSequence.itemAt(0);
                    if(Type.subTypeOf(item.getType(), Type.NODE)) {
                        return serializer.serialize((NodeValue)item);
                    }else {
                        throw new XFormsException("The xquery did not return a valid XML node");
                    }
                }
            }

        } catch (Exception e) {
            throw new XFormsException(e.getMessage(), e);

        } finally {
            if(pool != null)  {
                if(xmlResource != null) {
                    xmlResource.getUpdateLock().release(Lock.READ_LOCK);
                }
                pool.release(broker);
            }
        }
        return null;
    }

}

//end of class

