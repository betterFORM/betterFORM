/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.submission;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RequestHeaders {

    private List<RequestHeader> headers;
    private static Log LOGGER = LogFactory.getLog(RequestHeaders.class);

    public RequestHeaders() {
        this(10);
    }

    public RequestHeaders(int headerListSize) {
        headers = new ArrayList<RequestHeader>(headerListSize);
    }

    public void addHeader(String name, String value) {
        headers.add(new RequestHeader(name, value));
    }

    public void addHeader(RequestHeader header) {
        if (headers.contains(header)) {
            LOGGER.warn("Header " + header.getName() + " is already in list [value: '" + header.getValue() + "']");
        }
        headers.add(header);
    }

    public void addHeaders(Map<String, String> headersToAdd) {
        Set<String> headerNames = headersToAdd.keySet();

        while (headerNames.iterator().hasNext()) {
            String headerName = headerNames.iterator().next();
            if (containes(headerName)) {
                setHeader(headerName, headersToAdd.get(headerName));
            } else {
                addHeader(headerName, headersToAdd.get(headerName));
            }
        }

    }

    public RequestHeader getRequestHeader(String name) {
        for (int i = 0; i < headers.size(); i++) {
            RequestHeader requestHeader = headers.get(i);
            if (name.equals(requestHeader.getName())) {
                return requestHeader;
            }
        }

        return null;
    }

    public List<RequestHeader> getAllHeaders() {
        return this.headers;
    }

    public boolean containes(String name) {
        for (RequestHeader header : this.headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public String[] getHeaderValue(String name) {
        String result[] = null;
        ArrayList tmp = new ArrayList();
        for (RequestHeader header : this.headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                tmp.add(header.getValue());
            }
        }
        if (tmp.size() > 0) {
            result = new String[tmp.size()];
            tmp.toArray(result);
        }
        return result;
    }

    public void removeAllHeaders() {
        this.headers.clear();
    }

    public void removeHeader(String name) {
        for (RequestHeader header : this.headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                this.headers.remove(header);
            }
        }
    }

    public void setHeader(String name, String value) {
        for (RequestHeader header : this.headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                header.setValue(value);
            }
        }
    }

}
