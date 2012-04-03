/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.events.impl;

import de.betterform.xml.events.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;

/**
 * XMLEventInitializer ensures that pre-defined Events as in XForms are
 * initialized properly.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: DefaultXMLEventInitializer.java 2873 2007-09-28 09:08:48Z lars $
 */
public class DefaultXMLEventInitializer implements XMLEventInitializer {

    private static final Log LOGGER = LogFactory.getLog(DefaultXMLEventInitializer.class);

    private static final short EVENT_BUBBLES = 0;
    private static final short EVENT_CANCELABLE = 1;
    private static final short EVENT_CONTEXT = 2;

    private static final HashMap INITIALIZATION_RULES = new HashMap();
    static {
        // Initialization Events
        INITIALIZATION_RULES.put(XFormsEventNames.MODEL_CONSTRUCT, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.MODEL_CONSTRUCT_DONE, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.READY, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.MODEL_DESTRUCT, new boolean[]{true, false, false});

        // Interaction Events
        INITIALIZATION_RULES.put(XFormsEventNames.PREVIOUS, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.NEXT, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.FOCUS, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.HELP, new boolean[]{true, true, false});
        INITIALIZATION_RULES.put(XFormsEventNames.HINT, new boolean[]{true, true, false});
        INITIALIZATION_RULES.put(XFormsEventNames.REBUILD, new boolean[]{true, true, false});
        INITIALIZATION_RULES.put(XFormsEventNames.REFRESH, new boolean[]{true, true, false});
        INITIALIZATION_RULES.put(XFormsEventNames.REVALIDATE, new boolean[]{true, true, false});
        INITIALIZATION_RULES.put(XFormsEventNames.RECALCULATE, new boolean[]{true, true, false});
        INITIALIZATION_RULES.put(XFormsEventNames.RESET, new boolean[]{true, true, false});
        INITIALIZATION_RULES.put(XFormsEventNames.SUBMIT, new boolean[]{true, true, false});

        // Notification Events
        INITIALIZATION_RULES.put(DOMEventNames.ACTIVATE, new boolean[]{true, true, true});
        INITIALIZATION_RULES.put(XFormsEventNames.VALUE_CHANGED, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.SELECT, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.DESELECT, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.SCROLL_FIRST, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.SCROLL_LAST, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.INSERT, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(XFormsEventNames.DELETE, new boolean[]{true, false, true   });
        INITIALIZATION_RULES.put(XFormsEventNames.VALID, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.INVALID, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(DOMEventNames.FOCUS_IN, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(DOMEventNames.FOCUS_OUT, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.READONLY, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.READWRITE, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.REQUIRED, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.OPTIONAL, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.ENABLED, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.DISABLED, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.IN_RANGE, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.OUT_OF_RANGE, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.OUTPUT_ERROR, new boolean[]{true, false, false});
        INITIALIZATION_RULES.put(XFormsEventNames.SUBMIT_DONE, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(XFormsEventNames.SUBMIT_ERROR, new boolean[]{true, false, true});

        // Error Indications
        INITIALIZATION_RULES.put(XFormsEventNames.BINDING_EXCEPTION, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(XFormsEventNames.LINK_EXCEPTION, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(XFormsEventNames.LINK_ERROR, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(XFormsEventNames.COMPUTE_EXCEPTION, new boolean[]{true, false, true});

        // betterForm Events
        INITIALIZATION_RULES.put(BetterFormEventNames.LOAD_URI, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(BetterFormEventNames.RENDER_MESSAGE, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(BetterFormEventNames.REPLACE_ALL, new boolean[]{true, false, true});

        INITIALIZATION_RULES.put(BetterFormEventNames.STATE_CHANGED, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(BetterFormEventNames.NODE_INSERTED, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(BetterFormEventNames.NODE_DELETED, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(BetterFormEventNames.PROTOTYPE_CLONED, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(BetterFormEventNames.ID_GENERATED, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(BetterFormEventNames.ITEM_INSERTED, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(BetterFormEventNames.ITEM_DELETED, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(BetterFormEventNames.ITEM_CHANGED, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(BetterFormEventNames.INDEX_CHANGED, new boolean[]{true, false, true});
        INITIALIZATION_RULES.put(BetterFormEventNames.SWITCH_TOGGLED, new boolean[]{true, false, true});
    }

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
    public void initXMLEvent(XMLEvent event, String type, boolean bubbles, boolean cancelable, Object context) {
        boolean[] rules = (boolean[]) INITIALIZATION_RULES.get(type);

        if (rules != null) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("initializing event " + type + " from rule: bubbles=" + rules[EVENT_BUBBLES] + ", cancelable=" + rules[EVENT_CANCELABLE] + ", context=" + (rules[EVENT_CONTEXT] ? "yes" : "no"));
            }
            event.initXMLEvent(type, rules[EVENT_BUBBLES], rules[EVENT_CANCELABLE], rules[EVENT_CONTEXT] ? context : null);
        }
        else {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("initializing event " + type + " from parameters: bubbles=" + bubbles + ", cancelable=" + cancelable + ", context=" + (context != null ? "yes" : "no"));
            }
            event.initXMLEvent(type, bubbles, cancelable, context);
        }
    }
}
