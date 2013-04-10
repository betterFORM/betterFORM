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
import org.exist.dom.DocumentImpl;
import org.exist.security.PermissionDeniedException;
import org.exist.storage.BrokerPool;
import org.exist.storage.DBBroker;
import org.exist.storage.lock.Lock;
import org.exist.storage.serializers.Serializer;
import org.exist.xmldb.XmldbURI;
import org.xml.sax.SAXException;

import java.net.URI;
import java.net.URISyntaxException;

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

        BrokerPool pool = null;
        DBBroker broker = null;
        DocumentImpl xmlResource = null;
        try {
            URI dbURI = new URI(uriString);
            pool = BrokerPool.getInstance();
            broker = pool.get(pool.getSecurityManager().getGuestSubject());
            xmlResource = broker.getResource(XmldbURI.createInternal(dbURI.getRawPath()), Lock.READ_LOCK);
            if(xmlResource == null) {
                throw new XFormsException("eXist XML Resource is null");
            }
            else if(xmlResource.getResourceType() == DocumentImpl.XML_FILE){
                Serializer serializer = broker.getSerializer();
                String result = serializer.serialize(xmlResource);
                return result;
            }
        } catch (EXistException e) {
            throw new XFormsException("eXistException:", e);
        } catch (PermissionDeniedException e) {
            throw new XFormsException("PermissionDeniedException:", e);
        } catch (SAXException e) {
            throw new XFormsException("SaxException:", e);
        } catch (URISyntaxException e) {
            throw new XFormsException("invalid URI " + uriString);
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

