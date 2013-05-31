/*
 * Copyright (c) 2013. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.util;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class URIUtil {

    /**
     * strips the fragment part  (the part that starts with '#') from the URI
     * @param uri
     * @return URI string with fragment cut off
     */
     public static String getURIWithoutFragment(String uri) {

        if (uri == null) {
          return null;
        }

        int fragmentIndex = uri.indexOf('#');
        if (fragmentIndex < 0) {
          return uri;
        }

        //if there is a query string we re-append that at the end
        int queryIndex = uri.indexOf('?');
        if (queryIndex >= 0 && queryIndex > fragmentIndex) {
          String query = uri.substring(queryIndex);
          if(query != null){
              return uri.substring(0, fragmentIndex) + query;
          }
        }
        
        return uri.substring(0, fragmentIndex);
    }
     
    public static boolean hasParameter(URI uri, String key, String value) {
      
      if (null == uri || null == key) {
        return false;
      }
      
      Map<String, String> params = getQueryParameters(uri);
      return hasParameterFromMap(params, key, value);
    }
    
    public static boolean hasParameter(URI uri, String key) {
      return hasParameter(uri, key, "*");
    }
    
    public static boolean hasParameterFromMap(Map<String, String> queryParameter, String key) {
      return hasParameterFromMap(queryParameter, key, "*");
    }
    
    public static boolean hasParameterFromMap(Map<String, String> queryParameters, String key, String value) {
      
      if (null == queryParameters || null == key) {
        return false;
      }
      
      if (queryParameters.containsKey(key)) {
        String v = queryParameters.get(key);
        if (null == v) {
          return null == value;
        }
        if ("*".equals(value)) {
          return true;
        }
        return v.equals(value);
      }
      
      return false;
    }
    
    public static Map<String, String> getQueryParameters(URI uri) {
      
      if (null == uri || null == uri.getQuery()) {
        return Collections.emptyMap();
      }
      
      Map<String, String> result = new LinkedHashMap<String, String>();
      for (String pair : uri.getQuery().split("&")){
        if (1 > pair.indexOf('=')) {
          result.put(pair, null);
        } else {
          String[] kv = pair.split("=");
          result.put(kv[0], kv[1]);
        }
      }
      
      return result;
    }

}
