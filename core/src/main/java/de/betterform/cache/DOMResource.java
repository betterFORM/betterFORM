/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.cache;


import de.betterform.connector.file.FileURIResolver;
import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.net.URI;

/** 
 * @deprecated will be moved to DOMUtil*
 * @author: Joern Turner
 * @author: Lars Windauer
 * Date: Feb 21, 2007
 * TODO: Compare / Merge with DOMUtil
 */


public class DOMResource {
    private FileURIResolver fileURIResolver;
    private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private static DocumentBuilder documentBuilder;

    static {
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        //factory.setAttribute("http://apache.org/xml/properties/dom/document-class-name", "org.apache.xerces.dom.DocumentImpl");
        factory.setAttribute("http://apache.org/xml/properties/input-buffer-size", 8196);
    }

    /**
     * @deprecated will be moved to DOMUtil
     * @return
     * @throws XFormsException
     */
   public static synchronized Document newDocument() throws XFormsException {
        if (documentBuilder == null) {
            try {
                factory.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", Boolean.FALSE);
                factory.setExpandEntityReferences(false);
                documentBuilder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new XFormsException(e);
            }
        }
        return documentBuilder.newDocument();
    }

    /**
     * @deprecated will be moved to DOMUtil
     * @return
     * @throws XFormsException
     */
    public static synchronized DocumentBuilder newDocumentBuilder() throws XFormsException {
        try {

            return factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new XFormsException(e);
        }
    }

    public DOMResource(URI formURI){
        fileURIResolver = new FileURIResolver();
        fileURIResolver.setURI(formURI.toString());
    }

    /**
     * @deprecated will be moved to DOMUtil
     * @return
     * @throws XFormsException
     */
    public Node getResourceFromURI() throws XFormsException {
        Node node = (Node) fileURIResolver.resolve();
        //todo: caching
        return node;
    }

}
