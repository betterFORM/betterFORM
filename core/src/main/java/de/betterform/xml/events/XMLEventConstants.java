/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.events;

/**
 * Constant names and values defined by <a href="http://www.w3.org/TR/xml-events/">XML Events</a>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XMLEventConstants.java 2283 2006-08-24 11:25:18Z unl $
 */
public interface XMLEventConstants {

    // element names
    String LISTENER_ELEMENT = "listener";

    // attribute names
    String DEFAULT_ACTION_ATTRIBUTE = "defaultAction";
    String EVENT_ATTRIBUTE = "event";
    String HANDLER_ATTRIBUTE = "handler";
    String ID_ATTRIBUTE = "id";
    String OBSERVER_ATTRIBUTE = "observer";
    String PHASE_ATTRIBUTE = "phase";
    String PROPAGATE_ATTRIBUTE = "propagate";
    String TARGET_ATTRIBUTE = "target";

    // attribute values
    String CANCEL_VALUE = "cancel";
    String CAPTURE_VALUE = "capture";
    String CONTINUE_VALUE = "continue";
    String DEFAULT_VALUE = "default";
    String PERFORM_VALUE = "perform";
    String STOP_VALUE = "stop";
}
