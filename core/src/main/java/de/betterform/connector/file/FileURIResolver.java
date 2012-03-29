/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.file;

import de.betterform.connector.AbstractConnector;
import de.betterform.connector.URIResolver;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.exception.XFormsException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URI;

/**
 * This class resolves <code>file</code> URIs. It treats the denoted
 * <code>file</code> resource as XML and returns the parsed response.
 * <p/>
 * If the specified URI contains a fragment part, the specified element is
 * looked up via the <code>getElementById</code>. Thus, the parsed response must
 * have an internal DTD subset specifiyng all ID attributes. Otherwise the
 * element would not be found.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: FileURIResolver.java 2873 2007-09-28 09:08:48Z lars $
 */
public class FileURIResolver extends AbstractConnector implements URIResolver {

    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(FileURIResolver.class);

    /**
     * Performs link traversal of the <code>file</code> URI and returns the
     * result as a DOM document.
     *
     * @return a DOM node parsed from the <code>file</code> URI.
     * @throws XFormsException if any error occurred during link traversal.
     */
    public Object resolve() throws XFormsException {
        try {
            // create uri
            URI uri = new URI(getURI());

            // use scheme specific part in order to handle UNC names
            String fileName = uri.getSchemeSpecificPart();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("loading file '" + fileName + "'");
            }

            // create file
            File file = new File(fileName);

            // check for directory
            if (file.isDirectory()) {
                return FileURIResolver.buildDirectoryListing(file);
            }

            // parse file
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            Document document = factory.newDocumentBuilder().parse(file);


/*
            Document document = CacheManager.getDocument(file);
            if(document == null) {
                    document = DOMResource.newDocumentBuilder().parse(file);
                    CacheManager.putIntoFileCache(file, document);
            }
*/

            // check for fragment identifier
            if (uri.getFragment() != null) {
                return document.getElementById(uri.getFragment());
            }

            return document;
        }
        catch (Exception e) {
            //todo: improve error handling as files might fail due to missing DTDs or Schemas - this won't be detected very well
            throw new XFormsException(e);
        }
    }

    /**
     * Returns a plain file listing as a document.
     *
     * @param directory the directory to list.
     * @return a plain file listing as a document.
     */
    public static Document buildDirectoryListing(File directory) {
        Document dirList = DOMUtil.newDocument(false, false);
        Element root = dirList.createElement("dir");
        root.setAttribute("path", directory.toURI().toString());
        root.setAttribute("parentDir", directory.getParentFile().toURI().toString());

        File[] fileList = directory.listFiles();
        File file;
        Element element;
        for (int i = 0; i < fileList.length; i++) {
            file = fileList[i];

            if (file.isDirectory()) {
                element = dirList.createElement("dir");
            }
            else {
                element = dirList.createElement("file");
            }

            element.setAttribute("name", file.getName());
            element.setAttribute("path", file.toURI().toString());
            root.appendChild(element);
        }

        dirList.appendChild(root);
        return dirList;
    }

}

// end of class
