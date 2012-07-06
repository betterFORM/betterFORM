/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.events;

/**
 * All event names used by betterForm internally.
 *
 * @author Ulrich Nicolas Liss&eacute;, Joern Turner
 * @author Ronald van Kuijk
 * @version $Id: BetterFormEventNames.java 2341 2006-09-27 14:52:05Z joernt $
 */
public interface BetterFormEventNames {

    // betterForm interaction events

    /**
     * dispatched by a <code>load</code> action as request to the useragent to load a new URI according to the given
     * the load 'show' and 'url' parameters. This event has no counterpart in XForms itself. A useragent processor
     * must handle this event to make a 'load' happen in the context of its device.
     *
     */
    String LOAD_URI = "betterform-load-uri";

    
    /**
     * dispatched by a <code>message</code> Action as request to the client UI to display a message to the user.
     * This event has no counterpart in XForms itself. A useragent must handle this event to implement a message
     * on the specific platform.
     */
    String RENDER_MESSAGE = "betterform-render-message";

    /**
     * dispatched after execution of a Submission as request to the useragent to replace its content with the new one.
     * This event has no counterpart in XForms and must be handled by a Useragent.
     * betterForm interaction event constant.
     */
    String REPLACE_ALL = "betterform-replace-all";

    // betterForm notification events

    /**
     * betterForm notification event constant.
     * todo: this event is deprecated and will likely be replaced by usage of standard xforms events + 1 'type' event
     */
    String STATE_CHANGED = "betterform-state-changed";

    /**
     * betterForm notification event constant.
     */
    String NODE_INSERTED = "betterform-node-inserted";

    /**
     * betterForm notification event constant.
     */
    String NODE_DELETED = "betterform-node-deleted";

    /**
     * betterForm notification event constant.
     */
    String PROTOTYPE_CLONED = "betterform-prototype-cloned";

    /**
     * betterForm notification event constant.
     */
    String ID_GENERATED = "betterform-id-generated";

    /**
     * betterForm notification event constant.
     */
    String ITEM_INSERTED = "betterform-item-inserted";

    /**
     * betterForm notification event constant.
     */
    String ITEM_DELETED = "betterform-item-deleted";

    /**
     * betterForm notification event constant.
     */
    String ITEM_CHANGED = "betterform-item-changed";

    /**
     * betterForm notification event constant.
     */
    String INDEX_CHANGED = "betterform-index-changed";

    /**
     * dispatched by a <code>toggle</code> Action as request to the useragent to switch to another case. This event
     * must be handled by a useragent to react on a toggle.
     */
    String SWITCH_TOGGLED = "betterform-switch-toggled";

    /**
     * betterForm notification event constant.
     */
    String SCRIPT_ACTION = "betterform-script-action";

    String EMBED = "betterform-embed";
    String EMBED_DONE =  "betterform-embed-done";

    /**
     * betterFORM exception event constant.
     */
    String EXCEPTION = "betterform-exception";

    String VARIABLE_CHANGED = "betterform-variable-changed";

    /**
     * Interaction event constant, might move to XFormsEventNames for XForms 1.2
     */
    String HIDE = "betterform-dialog-close";

    /**
     * Interaction event constant, might move to XFormsEventNames for XForms 1.2
     */
    String SHOW = "betterform-dialog-open";


    String AVT_CHANGED = "betterform-AVT-changed";
    String INSTANCE_CREATED="betterform-instance-created";
    static final String MODEL_REMOVED = "betterform-model-removed";

}
