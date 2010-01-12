/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.connector.xmlrpc;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Hashtable;

public class DocTransformer {

    private Document doc = null;

    boolean namespaceAware = true;
    boolean validating = false;

    public DocTransformer() {
    };
    
    public DocTransformer(Document doc) {
        setSource(doc);
    }

    public DocTransformer(byte[] bytes) throws DocTransformerException {
        setSource(bytes);
    }

    public DocTransformer(String string) throws DocTransformerException {
        setSource(string);
    }

    public DocTransformer(File file) throws DocTransformerException {
        setSource(file);
    }

    public void setNamespaceAware(boolean in) {
        namespaceAware = in;
    }

    public void setValidating(boolean in) {
        validating = in;
    }

    public void setSource(byte[] bytes) throws DocTransformerException {
        try {
            DocumentBuilder builder = getDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(bytes));
        } catch (DocTransformerException e) {
            throw e;
        } catch (Exception e) {
            throw new DocTransformerException(e);
        }
    }

    public void setSource(String string) throws DocTransformerException {
        try {
            DocumentBuilder builder = getDocumentBuilder();
            doc = builder.parse(new ByteArrayInputStream(string.getBytes()));
        } catch (DocTransformerException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DocTransformerException(e.getMessage(), e);
        }
    }

    public void setSource(File file) throws DocTransformerException {
        try {
            DocumentBuilder builder = getDocumentBuilder();
            doc = builder.parse(file);
        } catch (DocTransformerException e) {
            throw e;
        } catch (Exception e) {
            throw new DocTransformerException(e);
        }
    }

    public void setSource(Document doc) {
        this.doc = doc;
    }


    public Document newDoc() throws DocTransformerException {
        try {
            DocumentBuilder builder = getDocumentBuilder();
            return builder.newDocument();
        } catch (DocTransformerException e) {
            throw e;
        } catch (Exception e) {
            throw new DocTransformerException(e);
        }
    }

    public Document getDoc() throws DocTransformerException {
        if (doc == null) {
            try {
                DocumentBuilder builder = getDocumentBuilder();
                doc = builder.newDocument();
            } catch (DocTransformerException e) {
                throw e;
            } catch (Exception e) {
                throw new DocTransformerException(e);
            }
        }
        return doc;
    }

    public byte[] getByteArray() throws DocTransformerException {
        try {
            Transformer trans = getTransformer(null);

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            trans.transform(new DOMSource(doc), new StreamResult(bs));
            return bs.toByteArray();
        } catch (DocTransformerException e) {
            throw e;
        } catch (Exception e) {
            throw new DocTransformerException(e);
        }
    }

    public String getString() throws DocTransformerException {
        try {
            Transformer trans = getTransformer(null);

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            trans.transform(new DOMSource(doc), new StreamResult(bs));
            return bs.toString();
        } catch (DocTransformerException e) {
            throw e;
        } catch (Exception e) {
            throw new DocTransformerException(e);
        }
    }

    public Hashtable getHash() throws DocTransformerException {
        return getHash(new Hashtable());
    }

    public Hashtable getHash(Hashtable h) throws DocTransformerException {
        Hashtable result = new Hashtable(h);
        if (doc == null) {
            result.put("status", "error");
            result.put("error", "Document is null returning hash");
            return result;
        }
        result.put("status", "ok");
        result.put("doc", getByteArray());
        return result;
    }

    private DocumentBuilder getDocumentBuilder() throws DocTransformerException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(namespaceAware);
            factory.setValidating(validating);
//            factory.setAttribute("http://apache.org/xml/properties/dom/document-class-name",
//                                    "de.betterform.xml.xforms.XFormsDocument");
            
            return factory.newDocumentBuilder();
        } catch (Exception e) {
            throw new DocTransformerException("Cannot get DocumentBuilder: " + e.getMessage(), e);
        }
    }

    private static Transformer getTransformer(Document doc) throws Exception {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        if (doc == null) {
            return transFactory.newTransformer();
        }
        return transFactory.newTransformer(new DOMSource(doc));
    }
}

