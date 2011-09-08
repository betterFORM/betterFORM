/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.events;

import de.betterform.xml.events.impl.XercesXMLEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An Event Target mock-up.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: MockEventTarget.java 2797 2007-08-10 12:45:24Z joern $
 */
public class MockEventTarget implements EventTarget {

    private Map listeners;

    public void addEventListener(String type, EventListener listener, boolean useCapture) {
        if (this.listeners == null) {
            this.listeners = new HashMap();
        }
        List list = (List) this.listeners.get(type);
        if (list == null) {
            list = new ArrayList();
            this.listeners.put(type, list);
        }
        list.add(listener);
    }

    public void removeEventListener(String type, EventListener listener, boolean useCapture) {
        if (this.listeners == null) {
            return;
        }
        List list = (List) this.listeners.get(type);
        if (list == null) {
            return;
        }
        list.remove(listener);
    }

    public boolean dispatchEvent(Event event) throws EventException {
        boolean result = false;
        if (this.listeners == null) {
            return result;
        }

        List list = (List) this.listeners.get(event.getType());
        if (list == null) {
            return result;
        }

        EventListener listener;
        for (int i = 0; i < list.size(); i++) {
            listener = (EventListener) list.get(i);
            listener.handleEvent(event);

            // huh, ugly downcast
            // todo: write event mock-up
            if (((XercesXMLEvent) event).preventDefault) {
                result = true;
            }
        }

        return result;
    }
}
