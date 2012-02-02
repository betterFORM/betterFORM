/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.submission;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RequestHeaders {

    private List<RequestHeader> headers;
    private static Log LOGGER = LogFactory.getLog(RequestHeaders.class);

    public RequestHeaders() {
        this(10);
    }

    public RequestHeaders(int headerListSize) {
        headers = Collections.synchronizedList(new ArrayList<RequestHeader>(headerListSize));
    }

    public void addHeader(String name, String value) {
        synchronized (headers) {
            headers.add(new RequestHeader(name, value));
        }

    }

    public void addHeader(RequestHeader header) {
        synchronized (headers){
            if (headers.contains(header)) {
                LOGGER.warn("Header " + header.getName() + " is already in list [value: '" + header.getValue() + "']");
            }
            headers.add(header);
        }
    }

    public RequestHeader getRequestHeader(String name) {
        synchronized (headers) {
            for (int i = 0; i < headers.size(); i++) {
                RequestHeader requestHeader = headers.get(i);
                if (name.equals(requestHeader.getName())) {
                    return requestHeader;
                }
            }
        }
        return null;
    }

    public synchronized List<RequestHeader> getAllHeaders() {
        return Collections.synchronizedList(headers);
    }

    public boolean containes(String name) {
        synchronized(headers){
            for (RequestHeader header : headers) {
                if (header.getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeHeader(String name) {
        synchronized (headers){
            for (int i = 0; i < headers.size(); i++) {
                RequestHeader header = headers.get(i);
                if (header.getName().equalsIgnoreCase(name)) {
                    headers.remove(header);
                }
            }
        }
    }


    // TODO: remove?!
/*
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

    public String[] getHeaderValue(String name) {
        String result[] = null;
        ArrayList tmp = new ArrayList();
        for (RequestHeader header : headers) {
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
        headers.clear();
    }

    public void setHeader(String name, String value) {
        for (RequestHeader header : headers) {
            if (header.getName().equalsIgnoreCase(name)) {
                header.setValue(value);
            }
        }
    }
*/

}
