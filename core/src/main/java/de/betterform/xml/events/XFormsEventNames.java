/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.events;

/**
 * All event names defined by the XForms Spec.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XFormsEventNames.java 2969 2007-10-30 00:09:53Z joern $
 */
public interface XFormsEventNames {

    // XForms initialization events

    /**
     * XForms init event constant.
     */
    static final String MODEL_CONSTRUCT = "xforms-model-construct";

    /**
     * XForms init event constant.
     */
    static final String MODEL_CONSTRUCT_DONE = "xforms-model-construct-done";

    /**
     * XForms init event constant.
     */
    static final String READY = "xforms-ready";

    /**
     * XForms init event constant.
     */
    static final String MODEL_DESTRUCT = "xforms-model-destruct";

    // XForms interaction events

    /**
     * XForms interaction event constant.
     */
    static final String PREVIOUS = "xforms-previous";

    /**
     * XForms interaction event constant.
     */
    static final String NEXT = "xforms-next";

    /**
     * XForms interaction event constant.
     */
    static final String FOCUS = "xforms-focus";

    /**
     * XForms interaction event constant.
     */
    static final String HELP = "xforms-help";

    /**
     * XForms interaction event constant.
     */
    static final String HINT = "xforms-hint";

    /**
     * XForms interaction event constant.
     */
    static final String REBUILD = "xforms-rebuild";

    /**
     * XForms interaction event constant.
     */
    static final String REFRESH = "xforms-refresh";

    /**
     * XForms interaction event constant.
     */
    static final String REVALIDATE = "xforms-revalidate";

    /**
     * XForms interaction event constant.
     */
    static final String RECALCULATE = "xforms-recalculate";

    /**
     * XForms interaction event constant.
     */
    static final String RESET = "xforms-reset";

    /**
     * XForms interaction event constant.
     */
    static final String SUBMIT = "xforms-submit";
    
    /**
     * XForms interaction event constant.
     */
    static final String SUBMIT_SERIALIZE = "xforms-submit-serialize";

    // XForms notification events

    /**
     * XForms notification event constant.
     */
    static final String VALUE_CHANGED = "xforms-value-changed";

    /**
     * XForms notification event constant.
     */
    static final String SELECT = "xforms-select";

    /**
     * XForms notification event constant.
     */
    static final String DESELECT = "xforms-deselect";

    /**
     * XForms notification event constant.
     */
    static final String SCROLL_FIRST = "xforms-scroll-first";

    /**
     * XForms notification event constant.
     */
    static final String SCROLL_LAST = "xforms-scroll-last";

    /**
     * XForms notification event constant.
     */
    static final String INSERT = "xforms-insert";

    /**
     * XForms notification event constant.
     */
    static final String DELETE = "xforms-delete";

    /**
     * XForms notification event constant.
     */
    static final String VALID = "xforms-valid";

    /**
     * XForms notification event constant.
     */
    static final String INVALID = "xforms-invalid";

    /**
     * XForms notification event constant.
     */
    static final String READONLY = "xforms-readonly";

    /**
     * XForms notification event constant.
     */
    static final String READWRITE = "xforms-readwrite";

    /**
     * XForms notification event constant.
     */
    static final String REQUIRED = "xforms-required";

    /**
     * XForms notification event constant.
     */
    static final String OPTIONAL = "xforms-optional";

    /**
     * XForms notification event constant.
     */
    static final String ENABLED = "xforms-enabled";

    /**
     * XForms notification event constant.
     */
    static final String DISABLED = "xforms-disabled";

    /**
     * XForms notification event constant.
     */
    static final String IN_RANGE = "xforms-in-range";

    /**
     * XForms notification event constant.
     */
    static final String OUT_OF_RANGE = "xforms-out-of-range";

    /**
     * XForms notification event constant.
     */
    static final String OUTPUT_ERROR = "xforms-output-error";

    /**
     * XForms notification event constant.
     */
    static final String SUBMIT_DONE = "xforms-submit-done";

    /**
     * XForms notification event constant.
     */
    static final String SUBMIT_ERROR = "xforms-submit-error";

    // XForms error indications

    /**
     * XForms error indication event constant.
     */
    static final String BINDING_EXCEPTION = "xforms-binding-exception";

    /**
     * XForms error indication event constant.
     */
    static final String LINK_EXCEPTION = "xforms-link-exception";

    /**
     * XForms error indication event constant.
     */
    static final String LINK_ERROR = "xforms-link-error";

    /**
     * XForms error indication event constant.
     */
    static final String COMPUTE_EXCEPTION = "xforms-compute-exception";

    /**
     * XForms Version Exception constant.
     */
    static final String VERSION_EXCEPTION = "xforms-version-exception";

    /**
     * Constant representing event property as defined in 4.5.4 the xforms-link-exception Event
     */
    static final String RESOURCE_URI_PROPERTY="resource-uri";
}
