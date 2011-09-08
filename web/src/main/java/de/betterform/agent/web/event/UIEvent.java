/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.event;

/**
 * Implementations of this interface represents Events send to the betterForm XForms processor and
 * represent actions triggered by user interaction or browser events.
 *
 * @author Joern Turner
 * @version $Id: UIEvent.java 3083 2008-01-21 11:29:21Z joern $
 */
public interface UIEvent {
    /**
     * initializes event.
     *
     * @param eventName   the type of event
     * @param id          identifier of the target element for this id
     * @param contextInfo additional app-specific info. The receiving Adapter has to know how to process this info.
     */
    void initEvent(String eventName, String id, Object contextInfo);

    /**
     * returns the name of the Event
     *
     * @return the name of the Event
     */
    String getEventName();

    String getId();

    /**
     * returns the contextinfo object passed with this event
     *
     * @return the contextinfo object passed with this event
     */
    Object getContextInfo();

    boolean equals(Object o);
}
