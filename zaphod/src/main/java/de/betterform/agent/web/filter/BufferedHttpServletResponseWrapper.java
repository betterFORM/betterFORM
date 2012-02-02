/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.agent.web.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Matthijs Wensveen <m.wensveen@func.nl>
 */
public class BufferedHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream output = null;
    private BufferedServletOutputStream servletOutputStream = null;
    private PrintWriter printWriter = null;
    private boolean usingOutputStream = false;
    private boolean usingWriter = false;
    private int contentLength;
    private String strSubType = null;
    private String strMediaType = null;

    /** GenericResponseWrapper constructor
     *
     * @param response HttpServletResponse
     */
    public BufferedHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new ByteArrayOutputStream();
//        servletOutputStream = new BufferedServletOutputStream(output);
//        printWriter = new PrintWriter(servletOutputStream, true);
    }

    /**
     * getData get the data that would be written to the response as array of bytes
     * @return byte[] array of bytes
     */
    public byte[] getData() {
        try {
            if (servletOutputStream != null) {
                servletOutputStream.flush();
            }
            if (printWriter != null) {
                printWriter.flush();
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return output.toByteArray();
    }

    /**
     * getData get the data that would be written to the response as String
     * @return String String with output
     * @throws UnsupportedEncodingException
     */
    public String getDataAsString() throws UnsupportedEncodingException {
        return (new String(getData(), getCharacterEncoding()));
    }

    /** getOutputStream
     * overridden method to capture the output written to the ServletOutputStream
     * Referring to Java Servlet Specification 2.4 this method throws an IllegalStateException
     * if the @see getWriter method has been called.
     */
    public ServletOutputStream getOutputStream() throws IOException {

        if (usingWriter) {
            throw new IllegalStateException("getWriter() has already been called for this response");
        }

        usingOutputStream = true;
        if (servletOutputStream == null) {
            servletOutputStream = new BufferedServletOutputStream(output);
        }
        return servletOutputStream;
    }

    /** Gets the PrintWriter to write data to.
     * Referring to Java Servlet Specification 2.4 this method throws an IllegalStateException
     * if the @see getOutputStream method has been called. In addition the writer uses the character
     * encoding returned by getCharacterEncoding().
     *  @return PrintWriter
     */
    public PrintWriter getWriter() throws IOException {
        if (usingOutputStream) {
            throw new IllegalStateException("getOutputStream() has already been called for this response");
        }

        setCharacterEncoding(getCharacterEncoding());
        usingWriter = true;
        if (printWriter == null) {
            if (servletOutputStream == null) {
                servletOutputStream = new BufferedServletOutputStream(output, getCharacterEncoding());
            }
            printWriter = new PrintWriter(servletOutputStream);
        }
        return printWriter;
    }

    public void setContentLength(int length) {
        this.contentLength = length;
        super.setContentLength(length);
    }

    public int getContentLength() {
        return contentLength;
    }

    public void flushBuffer() throws IOException {
        //??? super.flushBuffer();
        if (servletOutputStream != null) {
            servletOutputStream.flush();
        }
        if (printWriter != null) {
            printWriter.flush();
        }
    }

    public String getMediaType() {

        // try to get the content type
        String strContentType = getContentType();
        if (strContentType == null) {
            return "";
        }
//        org.apache.commons.httpclient.HeaderElement[] aHeaderelementTmp = org.apache.commons.httpclient.HeaderElement.parseElements(strContentType);

//       javax.mail.internet.ContentType contenttype;

        //TODO: Use a correct mime type parser!!
        String aHeaderelementTmp = "application/xhtml+xml";

        strMediaType = null;
        strSubType = null;
        if (aHeaderelementTmp.length() >= 1) {
            // try to identify the content
            java.util.StringTokenizer stringtokenizerTmp = new java.util.StringTokenizer(aHeaderelementTmp);

            if (stringtokenizerTmp.hasMoreTokens()) {
                strMediaType = stringtokenizerTmp.nextToken("/");
            }
            if (stringtokenizerTmp.hasMoreTokens()) {
                // the "/;" is required, because the subtype is
                // surrounded
                // by this two separators.
                strSubType = stringtokenizerTmp.nextToken("/;");
            }
        }

        if (strMediaType != null && strSubType != null) {
            return (strMediaType + "/" + strSubType);
        } else {
            return "";
        }
    }

    public boolean hasXMLContentType() {

        String result = getMediaType(); //set attributes strMediaType and strSubType
        if ("".equals(result)) {
            return false;
        }

        // detect XML documents
        boolean isXML = false;
        strMediaType = strMediaType.toLowerCase();
        strSubType = strSubType.toLowerCase();
        // see RFC 3023 for details
        isXML = strSubType.endsWith("+xml") || ((strMediaType.equals("text") || strMediaType.equals("application")) && (strSubType.equals("xml") || strSubType.equals("xml-external-parsed-entity")));

        return isXML;
    }

    public InputStream getInputStream() {
      return new ByteArrayInputStream(this.getData());
    }

    public void debugPrint() {
        try {
            int x;
            InputStream inputStream = this.getInputStream();
            while ((x = inputStream.read()) != -1) {
                System.out.print((char) x);
            }
            inputStream.reset();
        } catch (IOException ex) {
            Logger.getLogger(BufferedHttpServletResponseWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
