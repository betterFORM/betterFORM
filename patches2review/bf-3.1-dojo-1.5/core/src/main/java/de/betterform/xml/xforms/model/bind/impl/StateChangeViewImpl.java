/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.bind.impl;

import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.model.bind.StateChangeView;

/**
 * State Change viewport implementation.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: StateChangeViewImpl.java 3083 2008-01-21 11:29:21Z joern $
 */
public class StateChangeViewImpl implements StateChangeView {

    private ModelItem modelItem;

    private boolean valid;
    private boolean readonly;
    private boolean required;
    private boolean enabled;
    private boolean valueChanged;

    /**
     * Creates a new state change viewport implementation.
     *
     * @param modelItem the owner model item.
     */
    public StateChangeViewImpl(ModelItem modelItem) {
        this.modelItem = modelItem;

        this.valid = true;
        this.readonly = false;
        this.required = false;
        this.enabled = true;

        this.valueChanged = false;
    }

    // implementation of 'de.betterform.xml.xforms.model.bind.StateChangeView'

    /**
     * Returns the valid change state of a model item.
     *
     * @return the valid change state of a model item.
     */
    public boolean hasValidChanged() {
        return this.valid != this.modelItem.isValid();
    }

    /**
     * Returns the readonly change state of a model item.
     *
     * @return the readonly change state of a model item.
     */
    public boolean hasReadonlyChanged() {
        return this.readonly != this.modelItem.isReadonly();
    }

    /**
     * Returns the required change state of a model item.
     *
     * @return the required change state of a model item.
     */
    public boolean hasRequiredChanged() {
        return this.required != this.modelItem.isRequired();
    }

    /**
     * Returns the enabled change state of a model item.
     *
     * @return the enabled change state of a model item.
     */
    public boolean hasEnabledChanged() {
        return this.enabled != this.modelItem.isRelevant();
    }

    /**
     * Returns the value change state of a model item.
     *
     * @return the value change state of a model item.
     */
    public boolean hasValueChanged() {
        return this.valueChanged;
    }

    /**
     * Resets all state changes so that no changes are reported.
     */
    public void reset() {
        this.valid = this.modelItem.isValid();
        this.readonly = this.modelItem.isReadonly();
        this.required = this.modelItem.isRequired();
        this.enabled = this.modelItem.isRelevant();

        this.valueChanged = false;
    }

    // member access

    /**
     * Sets the value change state dirty.
     */
    public void setValueChanged() {
        this.valueChanged = true;
    }

}
