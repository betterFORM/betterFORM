/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.serializer;

import de.betterform.connector.InstanceSerializer;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.model.submission.Submission;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.Random;


/**
 * Serialize instance as multipart/form-data type.
 * <p/>
 * TODO:
 * <p/>
 * - CDATA sections, what to do with them, in specs only TEXT_NODEs are covered,
 * should be ignored ?
 *
 * @author Peter Mikula <peter.mikula@digital-artefacts.fi>
 * @version $Id: FormDataSerializer.java 2873 2007-09-28 09:08:48Z lars $
 */
public class FormDataSerializer implements InstanceSerializer {

    /**
     * Serialize instance into multipart/form-data stream as defined in
     * http://www.w3.org/TR/xforms/slice11.html#serialize-form-data
     *
     * @param submission
     * @param instance
     * @param wrapper
     * @param defaultEncoding
     * @throws Exception on error
     */
    public void serialize(Submission submission, Node instance, SerializerRequestWrapper wrapper, String defaultEncoding) throws Exception {
        // sanity checks
        if (instance == null) {
            return;
        }

        switch (instance.getNodeType()) {

            case Node.ELEMENT_NODE:case Node.TEXT_NODE:
                break;

            case Node.DOCUMENT_NODE:
                instance = ((Document) instance).getDocumentElement();
                break;

            default:
                return;
        }

        String encoding = defaultEncoding;
        if (submission.getEncoding() != null) {
            encoding = submission.getEncoding();
        }

        // generate boundary
        Random rnd = new Random(System.currentTimeMillis());
        String boundary = DigestUtils.md5Hex(getClass().getName() + rnd.nextLong());

        // serialize the instance
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(bos, encoding)));
        if (instance.getNodeType() == Node.ELEMENT_NODE) {
        	serializeElement(writer, (Element) instance, boundary, encoding);
        }
        else {
        	writer.print(instance.getTextContent());
        }
        writer.print("\r\n--" + boundary + "--");
        writer.flush();
        bos.writeTo(wrapper.getBodyStream());
        wrapper.addHeader("internal-boundary-mark", boundary);
    }

    protected void serializeElement(PrintWriter writer, Element element,
                                    String boundary, String charset) throws Exception {
        /* The specs http://www.w3.org/TR/2003/REC-xforms-20031014/slice11.html#serialize-form-data
         *
         *     Each element node is visited in document order.
         *
         *     Each element that has exactly one text node child is selected 
         *     for inclusion.
         *
         *     Element nodes selected for inclusion are as encoded as 
         *     Content-Disposition: form-data MIME parts as defined in 
         *     [RFC 2387], with the name parameter being the element local name.
         *
         *     Element nodes of any datatype populated by upload are serialized 
         *     as the specified content and additionally have a 
         *     Content-Disposition filename parameter, if available.
         *
         *     The Content-Type must be text/plain except for xsd:base64Binary, 
         *     xsd:hexBinary, and derived types, in which case the header 
         *     represents the media type of the attachment if known, otherwise 
         *     application/octet-stream. If a character set is applicable, the 
         *     Content-Type may have a charset parameter.
         *
         */
        String nodeValue = null;
        boolean isCDATASection = false;
        boolean includeTextNode = true;

        NodeList list = element.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            switch (n.getNodeType()) {

                /* CDATA sections are not mentioned ... ignore for now
                case Node.CDATA_SECTION_NODE:
                    isCDATASection = true;
                 */

                case Node.TEXT_NODE:
                    if (includeTextNode) {
                        if (nodeValue != null) {
                            /* only one text node allowed by specs */
                            includeTextNode = false;
                        }
                        else {
                            nodeValue = n.getNodeValue();
                        }
                    }
                    break;

                    /* Real ambiguity in specs, what if there's one text node and
                     * n elements ? Let's assume if there is an element, ignore the
                     * text nodes
                     */
                case Node.ELEMENT_NODE:
                    includeTextNode = false;
                    serializeElement(writer, (Element) n, boundary, charset);
                    break;

                default:
                    // ignore comments and other nodes...
            }
        }

        if (nodeValue != null && includeTextNode) {

            Object object = element.getUserData("");
            if (object != null && !(object instanceof ModelItem)) {
                throw new XFormsException("Unknown instance data format.");
            }
            ModelItem item = (ModelItem) object;

            writer.print("\r\n--" + boundary);

            String name = element.getLocalName();
            if (name == null) {
                name = element.getNodeName();
            }

            // mediatype tells about file upload
            if (item != null && item.getMediatype() != null) {
                writer.print("\r\nContent-Disposition: form-data; name=\""
                        + name + "\";");
                if (item.getFilename() != null) {
                    File file = new File(item.getFilename());
                    writer.print(" filename=\"" + file.getName() + "\";");
                }
                writer.print("\r\nContent-Type: " + item.getMediatype());

            }
            else {
                writer.print("\r\nContent-Disposition: form-data; name=\""
                        + name + "\";");
                writer.print("\r\nContent-Type: text/plain; charset=\""
                        + charset + "\";");
            }

            String encoding = "8bit";
            if (item != null && "base64Binary".equalsIgnoreCase(item.getDeclarationView().getDatatype())) {
                encoding = "base64";
            }
            else if (item != null && "hexBinary".equalsIgnoreCase(item.getDeclarationView().getDatatype())) {
                // recode to base64 because of MIME
                nodeValue = new String(Base64.encodeBase64(Hex.decodeHex(nodeValue.toCharArray()), true));
                encoding = "base64";
            }
            writer.print("\r\nContent-Transfer-Encoding: " + encoding);
            writer.print("\r\n\r\n" + nodeValue);
        }

        writer.flush();
    }
}

// end of class
