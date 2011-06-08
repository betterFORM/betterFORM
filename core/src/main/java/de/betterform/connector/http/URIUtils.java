/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.connector.http;

public class URIUtils {

    /**
     * strips the fragment part  (the part that starts with '#') from the URI
     * @param uri
     * @return URI string with fragment cut off
     */
     public static String getURIWithoutFragment(String uri) {

        //if there is a query string we re-append that at the end
        String query=null;
        if(uri.indexOf("?") != -1){
            query = uri.substring(uri.indexOf("?"));
        }

        if (uri == null) {
            return null;
        }

        int fragmentIndex = uri.indexOf('#');

        if (fragmentIndex == -1) {
            return uri;
        }

        if(query != null){
            return uri.substring(0, fragmentIndex) + query;
        }

        return uri.substring(0, fragmentIndex);
    }
}
