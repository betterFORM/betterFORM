/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.events;

/**
 * XMLEventInitializer ensures that pre-defined Events such as in XForms are
 * initialized properly.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XMLEventInitializer.java 2297 2006-09-05 15:55:09Z unl $
 */
public interface XMLEventInitializer {

    /**
     * Initializes the given event. If the event type is known to the initializer, the
     * event will be initialized according to the initializer's internal configuration.
     * Otherwise the specified parameters are used.
     *
     * @param event the event.
     * @param type specifies the event type.
     * @param bubbles specifies wether the event can bubble.
     * @param cancelable specifies wether the event's default action can be prevented.
     * @param context optionally specifies contextual information.
     */
    void initXMLEvent(XMLEvent event, String type, boolean bubbles, boolean cancelable, Object context);
}
