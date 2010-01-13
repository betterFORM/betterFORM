/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector;

import java.util.Map;

/**
 * Connectors are used to read data from a URI or to write (sumbit) data to a URI.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Connector.java 2090 2006-03-16 09:37:00Z joernt $
 */
public interface Connector {

    /**
     * Sets the URI the connector is associated to.
     *
     * @param uri the URI the connector is associated to.
     */
    void setURI(String uri);

    /**
     * Returns the URI the connector is associated to.
     *
     * @return the connector is associated to.
     */
    String getURI();

    /**
     * Sets the context map.
     *
     * @param context the context map.
     */
    void setContext(Map context);

    /**
     * Returns the context map.
     *
     * @return the context map.
     */
    Map getContext();

}

// end of interface
