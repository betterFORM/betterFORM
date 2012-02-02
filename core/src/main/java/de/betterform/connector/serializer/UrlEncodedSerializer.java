/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.serializer;

import de.betterform.connector.InstanceSerializer;
import de.betterform.xml.xforms.model.submission.Submission;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URLEncoder;

/**
 * Serialize instance as application/x-www-urlencoded type.
 *
 * @author Peter Mikula <peter.mikula@digital-artefacts.fi>
 */
public class UrlEncodedSerializer implements InstanceSerializer {

    public void serialize(Submission submission, Node instance,
                          SerializerRequestWrapper wrapper, String defaultEncoding) throws Exception {
        if (instance == null)
            return;

        switch (instance.getNodeType()) {

            case Node.ELEMENT_NODE:
                break;

            case Node.DOCUMENT_NODE:
                instance = ((Document) instance).getDocumentElement();
                break;

            default:
                return;
        }

        String separator = "&";
        if (submission.getSeparator() != null) {
            separator = submission.getSeparator();
        }

        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(wrapper.getBodyStream(), defaultEncoding)));
        
        /* Traverse the tree */
        serializeNode(writer, instance, separator);
        writer.flush();
    }

    protected void serializeNode(Writer writer, Node node, String separator)
            throws Exception {

        String textValue = null;
        boolean ignoreTextNodes = false;

        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {

            Node n = list.item(i);
            switch (n.getNodeType()) {

                case Node.TEXT_NODE:
                    if (!ignoreTextNodes) {
                        // based on specs we should consider only
                        // elements that have one text node !                        
                        if (textValue != null) {
                            ignoreTextNodes = true;
                        } else {
                            textValue = n.getNodeValue();
                        }
                    }
                    break;

                case Node.ELEMENT_NODE:
                    ignoreTextNodes = true;
                    serializeNode(writer, n, separator);
                    break;

                default:
                    // ignore ...
            }
        }

        if (ignoreTextNodes == false && textValue != null && textValue.length() > 0) {
            // based on specs we have to use here UTF-8 encoding
            String name = node.getLocalName();
            if (name == null) {
                name = node.getNodeName();
            }
            writer.write(URLEncoder.encode(name, "UTF-8")
                    + "="
                    + URLEncoder.encode(textValue, "UTF-8")
                    + separator);
        }
        writer.flush();
    }
}

//end of class


