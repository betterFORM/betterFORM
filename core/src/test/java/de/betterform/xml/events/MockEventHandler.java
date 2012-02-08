/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.events;

import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

import java.util.Collection;

/**
 * An Event Listener and Default Action mock-up.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: MockEventHandler.java 2969 2007-10-30 00:09:53Z joern $
 */
public class MockEventHandler implements EventListener, DefaultAction {

    private boolean preventDefault = false;

    private Event event;
    private long time;

    public void preventDefault() {
        this.preventDefault = true;
    }

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
            return ((XMLEvent) this.event).getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO);
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
        consume(event);
        if (this.preventDefault) {
            event.preventDefault();
        }
    }

    public void performDefault(Event event) {
        consume(event);
    }

    protected void consume(Event event) {
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
