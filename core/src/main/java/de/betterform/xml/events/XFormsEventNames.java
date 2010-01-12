/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
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
    String MODEL_CONSTRUCT = "xforms-model-construct";

    /**
     * XForms init event constant.
     */
    String MODEL_CONSTRUCT_DONE = "xforms-model-construct-done";

    /**
     * XForms init event constant.
     */
    String READY = "xforms-ready";

    /**
     * XForms init event constant.
     */
    String MODEL_DESTRUCT = "xforms-model-destruct";

    // XForms interaction events

    /**
     * XForms interaction event constant.
     */
    String PREVIOUS = "xforms-previous";

    /**
     * XForms interaction event constant.
     */
    String NEXT = "xforms-next";

    /**
     * XForms interaction event constant.
     */
    String FOCUS = "xforms-focus";

    /**
     * XForms interaction event constant.
     */
    String HELP = "xforms-help";

    /**
     * XForms interaction event constant.
     */
    String HINT = "xforms-hint";

    /**
     * XForms interaction event constant.
     */
    String REBUILD = "xforms-rebuild";

    /**
     * XForms interaction event constant.
     */
    String REFRESH = "xforms-refresh";

    /**
     * XForms interaction event constant.
     */
    String REVALIDATE = "xforms-revalidate";

    /**
     * XForms interaction event constant.
     */
    String RECALCULATE = "xforms-recalculate";

    /**
     * XForms interaction event constant.
     */
    String RESET = "xforms-reset";

    /**
     * XForms interaction event constant.
     */
    String SUBMIT = "xforms-submit";
    
    /**
     * XForms interaction event constant.
     */
    String SUBMIT_SERIALIZE = "xforms-submit-serialize";

    // XForms notification events

    /**
     * XForms notification event constant.
     */
    String VALUE_CHANGED = "xforms-value-changed";

    /**
     * XForms notification event constant.
     */
    String SELECT = "xforms-select";

    /**
     * XForms notification event constant.
     */
    String DESELECT = "xforms-deselect";

    /**
     * XForms notification event constant.
     */
    String SCROLL_FIRST = "xforms-scroll-first";

    /**
     * XForms notification event constant.
     */
    String SCROLL_LAST = "xforms-scroll-last";

    /**
     * XForms notification event constant.
     */
    String INSERT = "xforms-insert";

    /**
     * XForms notification event constant.
     */
    String DELETE = "xforms-delete";

    /**
     * XForms notification event constant.
     */
    String VALID = "xforms-valid";

    /**
     * XForms notification event constant.
     */
    String INVALID = "xforms-invalid";

    /**
     * XForms notification event constant.
     */
    String READONLY = "xforms-readonly";

    /**
     * XForms notification event constant.
     */
    String READWRITE = "xforms-readwrite";

    /**
     * XForms notification event constant.
     */
    String REQUIRED = "xforms-required";

    /**
     * XForms notification event constant.
     */
    String OPTIONAL = "xforms-optional";

    /**
     * XForms notification event constant.
     */
    String ENABLED = "xforms-enabled";

    /**
     * XForms notification event constant.
     */
    String DISABLED = "xforms-disabled";

    /**
     * XForms notification event constant.
     */
    String IN_RANGE = "xforms-in-range";

    /**
     * XForms notification event constant.
     */
    String OUT_OF_RANGE = "xforms-out-of-range";

    /**
     * XForms notification event constant.
     */
    String OUTPUT_ERROR = "xforms-output-error";

    /**
     * XForms notification event constant.
     */
    String SUBMIT_DONE = "xforms-submit-done";

    /**
     * XForms notification event constant.
     */
    String SUBMIT_ERROR = "xforms-submit-error";

    // XForms error indications

    /**
     * XForms error indication event constant.
     */
    String BINDING_EXCEPTION = "xforms-binding-exception";

    /**
     * XForms error indication event constant.
     */
    String LINK_EXCEPTION = "xforms-link-exception";

    /**
     * XForms error indication event constant.
     */
    String LINK_ERROR = "xforms-link-error";

    /**
     * XForms error indication event constant.
     */
    String COMPUTE_EXCEPTION = "xforms-compute-exception";

    /**
     * XForms Version Exception constant.
     */
    String VERSION_EXCEPTION = "xforms-version-exception";

    /**
     * Constant representing event property as defined in 4.5.4 the xforms-link-exception Event
     */
    String RESOURCE_URI_PROPERTY="resource-uri";
}
