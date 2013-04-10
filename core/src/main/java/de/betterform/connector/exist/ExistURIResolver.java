/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.exist;

import de.betterform.connector.URIResolver;
import de.betterform.connector.http.AbstractHTTPConnector;
import de.betterform.xml.xforms.exception.XFormsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.exist.EXistException;
import org.exist.dom.BinaryDocument;
import org.exist.dom.DocumentImpl;
import org.exist.security.PermissionDeniedException;
import org.exist.security.xacml.AccessContext;
import org.exist.source.DBSource;
import org.exist.source.Source;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.storage.lock.Lock;
import org.exist.storage.serializers.Serializer;
import org.exist.util.MimeType;
import org.exist.xmldb.XmldbURI;
import org.exist.xquery.CompiledXQuery;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQuery;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.Item;
import org.exist.xquery.value.NodeValue;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.Type;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

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
     */
    public Object resolve() throws XFormsException {
        String uriString = getURI();
        // TODO: User checken
        // Credentials from Security Manager (Params, Header..)
        // Subject user = BrokerPool.getInstance().getSecurityManager().authenticate(String username, String pwd)
        // broker = pool.get(user) // authenticated broker that can be used for further requests
        //
        // Credentials from Session
        // Session Attribut (Subject): _eXist_xmldb_user



        Map appContext=this.getContext();
        Object test = appContext.get("key");
        BrokerPool pool = null;
        DBBroker broker = null;
        DocumentImpl xmlResource = null;
        try {
            URI dbURI = new URI(uriString);
            pool = BrokerPool.getInstance();
            broker = pool.get(pool.getSecurityManager().getGuestSubject());
            xmlResource = broker.getResource(XmldbURI.createInternal(dbURI.getRawPath()), Lock.READ_LOCK);

            Serializer serializer = broker.getSerializer();

            if(xmlResource == null) {
                throw new XFormsException("eXist XML Resource is null");
            }
            else if(xmlResource.getResourceType() == DocumentImpl.XML_FILE){
                return serializer.serialize(xmlResource);
            } else {
                xmlResource.getMetadata().getMimeType();
                if(MimeType.XQUERY_TYPE.getName().equals(xmlResource.getMetadata().getMimeType())){
                    Source source = new DBSource(broker, ((BinaryDocument)xmlResource),true);
                    XQuery xquery = broker.getXQueryService();
                    XQueryContext context = xquery.newContext(AccessContext.REST);
                    context.setModuleLoadPath(xmlResource.getCollection().getURI().toString());
                    context.setStaticallyKnownDocuments(new XmldbURI[]{xmlResource.getCollection().getURI()});
                    CompiledXQuery compiledXQuery = xquery.compile(context, source);
                    Sequence sequence = xquery.execute(compiledXQuery, Sequence.EMPTY_SEQUENCE);
                    if(sequence.getItemCount() != 1){
                        throw new XFormsException("XML is not well formed");
                    }else {
                         Item item = sequence.itemAt(0);
                        if(Type.subTypeOf(item.getType(), Type.NODE)) {
                            return serializer.serialize((NodeValue)item);
                        }else {
                            throw new XFormsException("The xquery did not return a valid XML node");
                        }
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

}

//end of class

