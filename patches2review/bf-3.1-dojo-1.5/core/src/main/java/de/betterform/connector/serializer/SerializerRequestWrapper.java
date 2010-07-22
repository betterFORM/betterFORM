package de.betterform.connector.serializer;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class SerializerRequestWrapper {
    private Map<String,String>     headers;
    private OutputStream           bodyStream = null;

    public SerializerRequestWrapper(OutputStream stream) {
      this.bodyStream = stream;                                                  
    }

    public OutputStream getBodyStream() {
        return bodyStream;
    }

    public Map<String,String> getHeaders() {
        if (headers == null) {
            headers = new HashMap<String,String>();
        }
        return headers;
    }

    public void addHeader(String key, String value) {
        getHeaders().put(key,value);
    }

    public String getHeader(String key) {
        return getHeaders().get(key);
    }
}
