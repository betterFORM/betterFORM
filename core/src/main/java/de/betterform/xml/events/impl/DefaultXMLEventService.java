/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.events.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.events.*;
import de.betterform.xml.xforms.XFormsElement;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;


/**
 * XMLEventService provides a dispatching facility for XML Events. Additionally,
 * default actions can be registered per Event Target and Event type.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: DefaultXMLEventService.java 2873 2007-09-28 09:08:48Z lars $
 */
public class DefaultXMLEventService implements XMLEventService {

    private static final Log LOGGER = LogFactory.getLog(DefaultXMLEventService.class);

    private XMLEventFactory eventFactory;
    private XMLEventInitializer eventInitializer;
    private Map defaultActions;

    /**
     * Returns the XML Event Factory.
     *
     * @return the XML Event Factory.
     */
    public XMLEventFactory getXMLEventFactory() {
        return this.eventFactory;
    }

    /**
     * Sets the XML Event Factory.
     *
     * @param eventFactory the XML Event Factory.
     */
    public void setXMLEventFactory(XMLEventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    /**
     * Returns the XML Event Initializer.
     *
     * @return the XML Event Initializer.
     */
    public XMLEventInitializer getXMLEventInitializer() {
        return this.eventInitializer;
    }

    /**
     * Sets the XML Event Initializer.
     *
     * @param eventInitializer the XML Event Initializer.
     */
    public void setXMLEventInitializer(XMLEventInitializer eventInitializer) {
        this.eventInitializer = eventInitializer;
    }

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
    public void registerDefaultAction(EventTarget target, String type, DefaultAction action) {
        // check parameters, throw exception ?
        if (target == null || type == null || action == null) {
            return;
        }

        // check master map
        if (this.defaultActions == null) {
            this.defaultActions = new HashMap();
        }

        // check sub-map
        Map types = (Map) this.defaultActions.get(target);
        if (types == null) {
            types = new HashMap();
            this.defaultActions.put(target, types);
        }

        // store type/action association
        types.put(type, action);
    }

    /**
     * Deregisters a default action for the specified event type occurring on
     * the given event target.
     *
     * @param target the event target.
     * @param type specifies the event type.
     * @param action the default action.
     */
    public void deregisterDefaultAction(EventTarget target, String type, DefaultAction action) {
        // check parameters, throw exception ?
        if (target == null || type == null || action == null) {
            return;
        }

        // check master map
        if (this.defaultActions == null) {
            return;
        }

        // check sub-map
        Map types = (Map) this.defaultActions.get(target);
        if (types == null) {
            return;
        }

        // check default action
        DefaultAction current = (DefaultAction) types.get(type);
        if (current == null || !current.equals(action)) {
            return;
        }

        // remove type/action association
        types.remove(type);

        // memory clean-up
        if (types.isEmpty()) {
            this.defaultActions.remove(target);

            if (this.defaultActions.isEmpty()) {
                this.defaultActions = null;
            }
        }
    }

    /**
     * Dispatches the specified event to the given target.
     *
     * @param target the event target.
     * @param type specifies the event type.
     * @param bubbles specifies wether the event can bubble.
     * @param cancelable specifies wether the event's default action can be prevented.
     * @param context optionally specifies contextual information.
     * @return <code>true</code> if one of the event listeners called
     * <code>preventDefault</code>, otherwise <code>false</code>.
     */
    public boolean dispatch(EventTarget target, String type, boolean bubbles, boolean cancelable, Object context) {
        if (LOGGER.isDebugEnabled()) {
            if(target instanceof Node){
                Node node = (Node) target;
                Object object = node.getUserData("");
                if(object instanceof XFormsElement){
                    //Note: the cast seems pointless here but makes sure the right toString method is called
                    LOGGER.debug("dispatch event: " + type + " to " + ((XFormsElement)object).toString()) ;
                }
            } else{
                LOGGER.debug("dispatch event: " + type + " to " + target);
            }
        }

        XMLEvent event = this.eventFactory.createXMLEvent(type);
        this.eventInitializer.initXMLEvent(event, type, bubbles, cancelable, context);

        long start;
        long end;

        start = System.currentTimeMillis();
        boolean preventDefault = target.dispatchEvent(event);
        end = System.currentTimeMillis();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("dispatch event: " + type + " handling needed " + (end - start) + " ms");
        }

        if (!event.getCancelable() || !preventDefault) {
            DefaultAction action = lookup(target, type);

            if (action != null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("dispatch event: " + type + " default action at " + action);
                }

                // todo: set event phase without downcast ?
                ((XercesXMLEvent) event).eventPhase = XMLEvent.DEFAULT_ACTION;
                start = System.currentTimeMillis();
                action.performDefault(event);
                end = System.currentTimeMillis();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("dispatch event: " + type + " default action needed " + (end - start) + " ms");
                }
            }
        }

        return preventDefault;
    }


    protected DefaultAction lookup(EventTarget target, String type) {
        // check master map
        if (this.defaultActions == null) {
            return null;
        }

        // check sub-map
        Map types = (Map) this.defaultActions.get(target);
        if (types == null) {
            return null;
        }

        // check default action
        return (DefaultAction) types.get(type);
    }
}
