/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.cache;

import de.betterform.xml.xforms.exception.XFormsException;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.io.InputStream;
/**
 * @author: Lars Windauer
 */

public class CacheManager {
    private static final Log LOGGER = LogFactory.getLog(CacheManager.class);
    private static Cache xfFileCache;

    static {
        net.sf.ehcache.CacheManager.create();
        xfFileCache = net.sf.ehcache.CacheManager.getInstance().getCache("xfFileCache");
    }

    /**
     *
     * @param file cache key
     * @return document cache value 
     * @throws XFormsException
     */
    public static  Document getDocument(File file) throws XFormsException {
        Element elem = getElementFromFileCache(file);
        if (elem == null) { return null; }

        Document cachedDoc = (Document) elem.getObjectValue();
        org.w3c.dom.Element elment = cachedDoc.getDocumentElement();
        Document document = DOMResource.newDocument();
        //document.appendChild(document.adoptNode(elment.cloneNode(true)));
        document.appendChild(document.importNode(elment,true));
        return document;
    }

    public static InputStream getInputStream(File file) throws XFormsException {
        Element elem = getElementFromFileCache(file);
        if (elem == null) return null;
        return (InputStream) elem.getObjectValue();
    }

    public static Element getElementFromFileCache(File file) {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("search xfFileCache for key: " + file.getAbsolutePath() + file.lastModified());
        }
        Element elem = xfFileCache.get(file.getAbsolutePath() + file.lastModified());

        if(LOGGER.isDebugEnabled()) {
            LOGGER.debug(xfFileCache.getStatistics());
            if(elem == null) {
                LOGGER.debug("no value found for key " + file.getAbsolutePath() + file.lastModified());
            }else {
                LOGGER.debug("found value for key " + file.getAbsolutePath() + file.lastModified());        
            }
        }
        return elem;
    }

    /**
     *
     * @param file cache key
     * @param  object W3C DOM Docuemt (Cache value)
     */
    public static void putIntoFileCache(File file, Object object) {
        xfFileCache.put(new Element(file.getAbsolutePath() + file.lastModified(), object));
    }
}
