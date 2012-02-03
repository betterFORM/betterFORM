/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms;

import de.betterform.xml.events.XMLEvent;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

import java.util.Collection;

/**
 * A simple event listener implementation for testing purposes.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: TestEventListener.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class TestEventListener implements EventListener {
    private Event event;
    private long time;

    public String getId() {
        if (this.event != null && this.event.getTarget() instanceof Element) {
            return ((Element) this.event.getTarget()).getAttribute("id");
        }

        return null;
    }

    public String getType() {
        if (this.event != null) {
            return this.event.getType();
        }

        return null;
    }

    public Object getContext() {
        if (this.event != null && this.event instanceof XMLEvent) {
            return ((XMLEvent) this.event).getContextInfo();
        }

        return null;
    }

    public Object getContext(String property) {
        if (this.event != null && this.event instanceof XMLEvent) {
            return ((XMLEvent) this.event).getContextInfo(property);
        }

        return null;
    }

    public Collection getPropertyNames() {
        if (this.event != null && this.event instanceof XMLEvent) {
            return ((XMLEvent) this.event).getPropertyNames();
        }

        return null;
    }

    public long getTime() {
        return this.time;
    }

    public void clear() {
        this.time = 0L;
        this.event = null;
    }

    public void handleEvent(Event event) {
        this.event = event;

        // sleep a bit to get different times for each event
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.time = System.currentTimeMillis();
    }

}
