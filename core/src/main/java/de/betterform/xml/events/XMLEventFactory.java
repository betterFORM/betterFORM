/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.events;

import org.w3c.dom.events.DocumentEvent;


/**
 * XMLEventFactory provides a means to create any event used in XForms event
 * processing.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XMLEventFactory.java 2873 2007-09-28 09:08:48Z lars $
 */
public interface XMLEventFactory extends DocumentEvent {

    /**
     * Returns a new XML event of the specified type.
     *
     * @param type specifies the event type.
     * @return a new XML event of the specified type.
     */
    XMLEvent createXMLEvent(String type);
}
