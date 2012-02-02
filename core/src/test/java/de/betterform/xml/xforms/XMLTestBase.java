/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms;

import de.betterform.xml.dom.DOMComparator;
import de.betterform.xml.dom.DOMUtil;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Base class for XML black box testing.
 *
 * @author Joern Turner
 * @version $Id: XMLTestBase.java 3477 2008-08-19 09:26:47Z joern $
 */
public abstract class XMLTestBase extends TestCase {

    public XMLTestBase() {
        super();
    }

    public XMLTestBase(String s) {
        super(s);
    }

    protected void dump(Node node){
        DOMUtil.prettyPrintDOM(node);
    }


    protected void dumpToFile(File f, Node node){
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(f);
            DOMUtil.prettyPrintDOM(node,stream);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        } catch (TransformerException e) {
            System.out.println("File couldn't be transformed: " + e.getMessage());
        }
    }

    protected Document getXmlResource(String fileName) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        // Create builder.
        DocumentBuilder builder = factory.newDocumentBuilder();

        // Parse files.
        return builder.parse(getClass().getResourceAsStream(fileName));
    }

    protected DOMComparator getComparator() {
        DOMComparator comparator = new DOMComparator();
        comparator.setIgnoreNamespaceDeclarations(true);
        comparator.setIgnoreWhitespace(true);
        comparator.setIgnoreComments(true);
        comparator.setErrorHandler(new DOMComparator.SystemErrorHandler());

        return comparator;
    }
}

// end of class
