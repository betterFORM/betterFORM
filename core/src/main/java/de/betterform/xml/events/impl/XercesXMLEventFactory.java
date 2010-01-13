/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.events.impl;

import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.events.XMLEventFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.events.Event;

/**
 * XMLEventFactory implementation based on Xerces' DOM event implementation.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XercesXMLEventFactory.java 2307 2006-09-07 13:58:02Z unl $
 */
public class XercesXMLEventFactory implements XMLEventFactory {

    /**
     * Default constructor.
     */
    public XercesXMLEventFactory() {
        // NOP
    }

    // implementation of 'org.w3c.dom.events.DocumentEvent'

    /**
     * Returns a new event of the specified type.
     *
     * @param type specifies the event type.
     * @return a new event of the specified type.
     * @throws org.w3c.dom.DOMException if the event type is not supported
     * (<code>DOMException.NOT_SUPPORTED_ERR</code>).
     */
    public Event createEvent(String type) throws DOMException {
        return createXMLEvent(type);
    }

    // implementation of de.betterform.xml.events.XMLEventFactory'

    /**
     * Returns a new XML event of the specified type.
     *
     * @param type specifies the event type.
     * @return a new XML event of the specified type.
     * @throws org.w3c.dom.DOMException if the event type is not supported
     * (<code>DOMException.NOT_SUPPORTED_ERR</code>).
     */
    public XMLEvent createXMLEvent(String type) throws DOMException {
        return new XercesXMLEvent(type);
    }
}
