/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.exist;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exist.EXistException;
import org.exist.dom.BinaryDocument;
import org.exist.dom.DocumentImpl;
import org.exist.dom.QName;
import org.exist.security.PermissionDeniedException;
import org.exist.security.Subject;
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
import org.exist.xquery.XPathException;
import org.exist.xquery.XQuery;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.FunctionReference;
import org.exist.xquery.value.Item;
import org.exist.xquery.value.NodeValue;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.Type;
import org.xml.sax.SAXException;

import de.betterform.connector.URIResolver;
import de.betterform.connector.http.AbstractHTTPConnector;
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
            broker = getBroker(pool);

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
                boolean isModule = true;
                
                // check if the referenced resource is an XQuery
                // TODO: parse URI to check if we have to handle an XQuery or XQuery module
                if(isModule){

                    XQueryContext tempContext = new XQueryContext(pool, AccessContext.REST);
                    tempContext.setModuleLoadPath(xmlResource.getCollection().getURI().toString());
                    Module module = tempContext.importModule(null, null, "xmldb:exist://"+dbURI.getRawPath());

                    // UserDefinedFunction udf = tempContext.resolveFunction(new QName("test", module.getNamespaceURI(),module.getDefaultPrefix()),0);
                    // TODO: replace 0 with calculated quantity
                    UserDefinedFunction udf = ((ExternalModule)module).getFunction(new QName("test", module.getNamespaceURI(), module.getDefaultPrefix()), 0, tempContext);

                    final FunctionReference fnRef = new FunctionReference(new FunctionCall(tempContext, udf));
                    fnRef.analyze(new AnalyzeContextInfo());
                    // fill new Sequence[]{} with function parameters in correct order
                    // can be only
                    // new StringValue("hallo")
                    resultSequence = fnRef.evalFunction(null, null, new Sequence[]{});
                    // TODO: reset should be called on fnRef
                    udf.resetState(true);

                }else {
                    Source source = new DBSource(broker, ((BinaryDocument)xmlResource),true);
                    XQuery xquery = broker.getXQueryService();
                    XQueryContext context = xquery.newContext(AccessContext.REST);
                    context.setModuleLoadPath(xmlResource.getCollection().getURI().toString());
                    context.setStaticallyKnownDocuments(new XmldbURI[]{xmlResource.getCollection().getURI()});
                    CompiledXQuery compiledXQuery = xquery.compile(context, source);
                    resultSequence = xquery.execute(compiledXQuery, Sequence.EMPTY_SEQUENCE);
                }
                // verify results
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
        } catch (EXistException e) {
            throw new XFormsException("eXistException:", e);
        } catch (PermissionDeniedException e) {
            throw new XFormsException("PermissionDeniedException:", e);
        } catch (SAXException e) {
            throw new XFormsException("SaxException:", e);
        } catch (URISyntaxException e) {
            throw new XFormsException("invalid URI " + uriString);
        } catch (XPathException e) {
            throw new XFormsException("XPathException: " + uriString);
        } catch (IOException e) {
            throw new XFormsException("IOException: " + uriString);
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

    private DBBroker getBroker(BrokerPool pool) throws EXistException {
      Subject subject = getSubject(pool);
      DBBroker broker = pool.get(subject);
      return broker;
    }

    private Subject getSubject(BrokerPool pool) {
      Subject subject = (Subject) this.getContext().get("_eXist_xmldb_user");
      if (null != subject) {
        return subject;
      }
      return pool.getSecurityManager().getGuestSubject();
    }

}

//end of class

