/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xslt.impl;

import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xslt.TransformerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Provides a resource caching implementation of TransformerService. Special
 * care is taken for the inter-dependencies of the cached resources, i.e.
 * a resource is considered dirty if any dependant resource is dirty. This
 * means that a stylesheet will be reloaded even if just an included stylesheet
 * has been changed. 
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: CachingTransformerService.java 2922 2007-10-17 14:07:48Z lars $
 */
public class CachingTransformerService implements TransformerService, URIResolver {

    private static final Log LOGGER = LogFactory.getLog(CachingTransformerService.class);

    private HashMap resources;
    private HashMap<String,URI> name2URI;
    private ArrayList resolvers;
    private TransformerFactory transformerFactory;
    private boolean nocache = false;

    /**
     * Creates a new caching transformer service.
     */
    public CachingTransformerService() {
        this.resources = new HashMap();
        this.name2URI = new HashMap<String, URI>();
        this.resolvers = new ArrayList();
    }

    /**
     * Creates a new caching transformer service.
     *
     * @param resolver a resource resolver.
     */
    public CachingTransformerService(ResourceResolver resolver) {
        this();
        addResourceResolver(resolver);
    }

    // implementation of 'de.betterform.xml.xslt.TransformerService'

    /**
     * Returns the transformer factory.
     *
     * @return the transformer factory.
     */
    public TransformerFactory getTransformerFactory() {
        if (this.transformerFactory == null) {
            this.transformerFactory = new net.sf.saxon.TransformerFactoryImpl();
            this.transformerFactory.setURIResolver(this);
            this.transformerFactory.setErrorListener(new XmlStylesheetTransformerErrorListener());
        }

        return this.transformerFactory;
    }

    /**
     * Sets the transformer factory.
     *
     * @param factory the transformer factory.
     */
    public void setTransformerFactory(TransformerFactory factory) throws TransformerException {
        throw new TransformerException("You cannot set the TransformerFactory for this implementation. This service uses Saxon as XSLT engine.");
    }

    /**
     * Returns an identity transformer.
     *
     * @return an identity transformer.
     * @throws TransformerException if the transformer couldn't be created.
     */
    public Transformer getTransformer() throws TransformerException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("get transformer: identity transformer");
        }

        return getTransformerFactory().newTransformer();
    }

    public Transformer getTransformerByName(String xsltFileName) throws TransformerException {
        URI uri = this.name2URI.get(xsltFileName);
        return getTransformer(uri);
    }
    
    /**
     * Returns a transformer for the specified stylesheet.
     * <p/>
     * If the URI is null, an identity transformer is created.
     *
     * @param uri the URI identifying the stylesheet.
     * @return a transformer for the specified stylesheet.
     * @throws TransformerException if the transformer couldn't be created.
     */
    public Transformer getTransformer(URI uri) throws TransformerException {
        if (uri == null) {
            return getTransformer();
        }

        try {
            // lookup cache entry
            CacheEntry entry = (CacheEntry) this.resources.get(uri);
            if (nocache || entry == null || entry.isDirty()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("get transformer: cache " + (entry == null ? "miss" : "dirty") + " for " + uri);
                }

                 // load missing/dirty resource
                Resource resource = load(uri);
                if (resource == null) {
                    // complain if resource couldn't be loaded
                    throw new IllegalArgumentException(uri.toString());
                }

                // sync entry with resource
                entry = sync(entry, resource);

                // store entry in cache
                this.resources.put(uri, entry);
                this.name2URI.put(getXsltName(uri), uri);
            }
            else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("get transformer: cache hit for " + uri);
                }
            }

            if (entry.templates == null) {
                // create source and templates object (this might trigger uri resolution)
                Source source = new StreamSource(entry.resource.getInputStream());
                source.setSystemId(uri.toString());
                entry.templates = getTransformerFactory().newTemplates(source);
            }

            return entry.templates.newTransformer();
        }
        catch (Exception e) {
            throw new TransformerException(e);
        }
    }

    private String getXsltName(URI uri) {
        String s = uri.toString();
        return s.substring(s.lastIndexOf("/")+1);
    }

    public void setNoCache(boolean b){
        this.nocache = b;    
    }

    // implementation of 'javax.xml.transform.URIResolver'

    /**
     * Called by the processor when it encounters an xsl:include, xsl:import, or
     * document() function.
     *
     * @param href the href attribute, which may be relative or absolute.
     * @param base the base URI in effect when the href attribute was encountered.
     * @return a Source object, or null if the href cannot be resolved, and the
     * processor should try to resolve the URI itself.
     * @throws TransformerException if an error occurs when trying to resolve the URI.
     */
    public Source resolve(String href, String base) throws TransformerException {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("resolve: " + href + " against " + base);
            }

            // resolve uri
            URI uri = base != null ? new URI(base).resolve(href) : new URI(href);

            // lookup cache entry
            CacheEntry entry = (CacheEntry) this.resources.get(uri);
            if (nocache == true || entry == null || entry.isDirty() || !(entry.isTransform())) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("resolve: cache " + (entry == null ? "miss" : "dirty") + " for " + uri);
                }

                // load missing/dirty resource
                Resource resource = load(uri);
                if (resource == null) {
                    // return null to let the xslt processor attempt to resolve the uri
                    return null;
                }

                // sync entry with resource
                entry = sync(entry, resource);

                // store entry in cache
                this.resources.put(uri, entry);

                if (base != null) {
                    // add dependency to parent entry
                    CacheEntry parent = (CacheEntry) this.resources.get(new URI(base));
                    parent.dependencies.add(entry);
                }
            }
            else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("resolve: cache hit for " + uri);
                }
            }

            // create source
            Source source = entry.resource.getSource();
            source.setSystemId(uri.toString());
            return source;
        }
        catch (Exception e) {
            throw new TransformerException(e);
        }
    }

    // stylesheet loader

    /**
     * Adds a resource resolver.
     *
     * @param resolver the resource resolver to be added.
     */
    public void addResourceResolver(ResourceResolver resolver) {
        this.resolvers.add(resolver);
    }

    /**
     * Removes a resource resolver.
     *
     * @param resolver the resource resolver to be removed.
     */
    public void removeResourceResolver(ResourceResolver resolver) {
        this.resolvers.remove(resolver);
    }

    // helper

    private Resource load(URI uri) throws XFormsException {
        // todo: use connectors some day ?
        Resource resource;
        ResourceResolver resolver;

        for (int i = 0; i < this.resolvers.size(); i++) {
            resolver = (ResourceResolver) this.resolvers.get(i);
            resource = resolver.resolve(uri);
            if (resource != null) {
                return resource;
            }
        }

        return null;
    }

    private CacheEntry sync(CacheEntry entry, Resource resource) {
        if (entry == null) {
            // create entry
            entry = new CacheEntry(resource.lastModified(), resource);
        }
        else {
            // reset entry for reuse
            entry.lastModified = resource.lastModified();
            entry.resource = resource;
            entry.templates = null;
            entry.dependencies.clear();
        }

        return entry;
    }

    private static class CacheEntry {
        long lastModified;
        Resource resource;
        Templates templates;
        List dependencies;

        CacheEntry(long lastModified, Resource resource) {
            this.lastModified = lastModified;
            this.resource = resource;
            this.dependencies = new ArrayList();
        }

        boolean isDirty() {
            if (this.lastModified < this.resource.lastModified()) {
                return true;
            }

            for (int i = 0; i < this.dependencies.size(); i++) {
                if (((CacheEntry) this.dependencies.get(i)).isDirty()) {
                    return true;
                }
            }

            return false;
        }

        boolean isTransform() throws XFormsException {
            //todo: will only work with HttpResource
            if(this.resource.getSource().getSystemId()!=null && this.resource.getSource().getSystemId().endsWith(".xsl")){
                return true;
            }else{
                return false;
            }
        }
    }

    private static class XmlStylesheetTransformerErrorListener implements javax.xml.transform.ErrorListener {

        public void error(javax.xml.transform.TransformerException ex)
            throws javax.xml.transform.TransformerException
        {
    	        ex.printStackTrace();
            	throw ex;
}

        public void fatalError(javax.xml.transform.TransformerException ex)
            throws javax.xml.transform.TransformerException
        {
    	        ex.printStackTrace();
    	        throw ex;
        }

        public void warning(javax.xml.transform.TransformerException ex)
            throws javax.xml.transform.TransformerException
        {
        		ex.printStackTrace();
    	        throw ex;
        }
    }
}
