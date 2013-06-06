/*
 * Copyright (c) 2013. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class URIUtil {

  private static final String HASH = "#";
  private static final String EMPTY = "";
  private static final String SLASH = "/";
  private static final String QMARK = "?";
  private static final String ASTERISK = "*";
  private static final String AMPF = "&";
  private static final String EQ = "=";
  
  /**
   * strips the fragment part (the part that starts with '#') from the URI
   * 
   * @param uri
   * @return URI string with fragment cut off
   */
  public static String getURIWithoutFragment(String uri) {

    if (uri == null) {
      return null;
    }

    int fragmentIndex = uri.indexOf(HASH);
    if (fragmentIndex < 0) {
      return uri;
    }

    // if there is a query string we re-append that at the end
    int queryIndex = uri.indexOf(QMARK);
    if (queryIndex >= 0 && queryIndex > fragmentIndex) {
      String query = uri.substring(queryIndex);
      if (query != null) {
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
    return hasParameter(uri, key, ASTERISK);
  }

  public static boolean hasParameterFromMap(Map<String, String> queryParameter, String key) {
    return hasParameterFromMap(queryParameter, key, ASTERISK);
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
      if (ASTERISK.equals(value)) {
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
    for (String pair : uri.getQuery().split(AMPF)) {
      if (1 > pair.indexOf(EQ)) {
        result.put(pair, null);
      } else {
        String[] kv = pair.split(EQ);
        result.put(kv[0], kv[1]);
      }
    }

    return result;
  }

  public static String getLastSegmentFromPath(URI uri) {
    
    if (null == uri) {
      return null;
    }
    
    String rawpath = uri.getRawPath();
    if (EMPTY.trim().equals(rawpath) || rawpath.lastIndexOf(SLASH) <= 0) {
      return null;
    }
    return rawpath.substring(rawpath.lastIndexOf(SLASH) + 1);
  }

  public static String getPathWithoutLastSegment(URI uri) {
    if (null == uri) {
      return null;
    }
    
    String rawpath = uri.getRawPath();
    if (EMPTY.trim().equals(rawpath) || rawpath.lastIndexOf(SLASH) < 0) {
      return rawpath;
    }
    
    return rawpath.substring(0, rawpath.lastIndexOf(SLASH));
  }

  public static boolean hasFileExtension(String uri, String ... fileExtensions) throws URISyntaxException {
    if (null == fileExtensions || null == uri) {
      return false;
    }
    String file = getLastSegmentFromPath(new URI(uri));
    if (null == file || EMPTY.trim().equals(file)) {
      return false;
    }
    
    for (String ext : fileExtensions) {
      if (file.endsWith(ext)) {
        return true;
      }
    }
    return false;
  }
}
