/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.events;

import org.w3c.dom.events.Event;

import java.util.Collection;
import java.util.Map;

/**
 * XMLEvent adds a context information facility to DOM Level2 Events in order
 * to meet XForms requirements in event processing.
 * <p/>
 * These requirements are:
 * <ul>
 * <li>for XForms 1.0: associate a context information string to an event</li>
 * <li>for betterForm internals and XForms 1.1: associate an arbitrary number of context
 * information properties to an event</li>
 * </ul>
 * <p/>
 * The context information provided during event init should always be of type
 * java.util.Map. If not, a map will be created transparently and the specified
 * context information will be stored as default context information within that
 * map. The default context information is associated with the empty string as
 * property name.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XMLEvent.java 3570 2008-10-07 14:39:11Z lars $
 */
public interface XMLEvent extends Event, Cloneable {
    /**
     * Default (anonymous) key for event context information.
     * @see de.betterform.xml.events.impl.XercesXMLEvent
     */
    public static final String DIRTY_DEFAULT_INFO = "defaultinfo";

    /**
     * Additional event phase for default action.
     */
    short DEFAULT_ACTION = 4;
    
    Object clone() throws CloneNotSupportedException;

    /**
     * Returns the default contextual information for this event.
     * <p/>
     * Note: Calling this method has to yield the same result like calling
     * <code>getContextProperty(String)</code> with the empty string as property
     * name.
     *
     * @return the default contextual information for this event.
     */
    Map getContextInfo();

    /**
     * Returns the contextual information for this event denoted by the
     * specified property.
     * <p/>
     * If no such contextual information is available, this method returns
     * <code>null</code>.
     *
     * @param property the name of the context property.
     * @return contextual information for this event.
     */
    Object getContextInfo(String property);

    /**
     * Returns the set of property names used for contextual information.
     * <p/>
     * If no such contextual information is available, this method returns
     * <code>null</code>.
     *
     * @return the set of property names used for contextual information.
     */
    Collection getPropertyNames();

    void addProperty(String name,Object value);

    /**
     * Initializes this event.
     *
     * @param type specifies the event type.
     * @param bubbles specifies wether the event can bubble.
     * @param cancelable specifies wether the event's default action can be
     * prevented.
     * @param context optionally specifies contextual information.
     */
    void initXMLEvent(String type, boolean bubbles, boolean cancelable, Object context);

}
