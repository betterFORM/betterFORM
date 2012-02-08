/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.events;

import org.w3c.dom.events.Event;

/**
 * DefaultAction provides an interface for implementing an Event's default
 * action which is not available with standard DOM Level 2 Events (see
 * http://www.w3.org/TR/2000/REC-DOM-Level-2-Events-20001113/events.html#Events-flow-cancelation
 * for details).
 * <p/>
 * A Default Action has to be executed after the normal dispatch life-cycle.
 * XML Event adds a new phase to reflect this. The XML Event Service is supposed
 * to manage and execute Default Action implementations.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: DefaultAction.java 2308 2006-09-08 12:27:31Z unl $
 */
public interface DefaultAction {

    /**
     * Performs the implementation specific default action for this
     * event.
     *
     * TODO: exception handling
     *
     * @param event the event.
     */
    void performDefault(Event event);
}
