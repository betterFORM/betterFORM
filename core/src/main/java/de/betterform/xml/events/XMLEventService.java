/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.events;

import org.w3c.dom.events.EventTarget;

/**
 * XMLEventService provides a dispatching facility for XML Events. Additionally,
 * default actions can be registered per Event Target and Event type.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XMLEventService.java 2309 2006-09-08 21:32:25Z unl $
 */
public interface XMLEventService {

    /**
     * Returns the XML Event Factory.
     *
     * @return the XML Event Factory.
     */
    XMLEventFactory getXMLEventFactory();

    /**
     * Sets the XML Event Factory.
     *
     * @param eventFactory the XML Event Factory.
     */
    void setXMLEventFactory(XMLEventFactory eventFactory);

    /**
     * Returns the XML Event Initializer.
     *
     * @return the XML Event Initializer.
     */
    XMLEventInitializer getXMLEventInitializer();

    /**
     * Sets the XML Event Initializer.
     *
     * @param eventInitializer the XML Event Initializer.
     */
    void setXMLEventInitializer(XMLEventInitializer eventInitializer);

    /**
     * Registers a default action for the specified event type occurring on
     * the given event target.
     * <p/>
     * Only one default action can be registered for a particular event type and
     * event target combination.
     *
     * @param target the event target.
     * @param type specifies the event type.
     * @param action the default action.
     */
    void registerDefaultAction(EventTarget target, String type, DefaultAction action);

    /**
     * Deregisters a default action for the specified event type occurring on
     * the given event target.
     *
     * @param target the event target.
     * @param type specifies the event type.
     * @param action the default action.
     */
    void deregisterDefaultAction(EventTarget target, String type, DefaultAction action);

    /**
     * Dispatches the specified event to the given target.
     *
     * TODO: exception handling
     *
     * @param target the event target.
     * @param type specifies the event type.
     * @param bubbles specifies wether the event can bubble.
     * @param cancelable specifies wether the event's default action can be prevented.
     * @param context optionally specifies contextual information.
     * @return <code>true</code> if one of the event listeners called
     * <code>preventDefault</code>, otherwise <code>false</code>.
     */
    boolean dispatch(EventTarget target, String type, boolean bubbles, boolean cancelable, Object context);
}
