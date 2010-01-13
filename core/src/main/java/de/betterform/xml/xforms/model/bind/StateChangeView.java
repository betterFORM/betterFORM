/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.xforms.model.bind;

/**
 * State Change viewport to model items. Provides access to changes of a model
 * item's properties.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: StateChangeView.java 2090 2006-03-16 09:37:00Z joernt $
 */
public interface StateChangeView {

    /**
     * Returns the valid change state of a model item.
     *
     * @return the valid change state of a model item.
     */
    boolean hasValidChanged();

    /**
     * Returns the readonly change state of a model item.
     *
     * @return the readonly change state of a model item.
     */
    boolean hasReadonlyChanged();

    /**
     * Returns the required change state of a model item.
     *
     * @return the required change state of a model item.
     */
    boolean hasRequiredChanged();

    /**
     * Returns the enabled change state of a model item.
     *
     * @return the enabled change state of a model item.
     */
    boolean hasEnabledChanged();

    /**
     * Returns the value change state of a model item.
     *
     * @return the value change state of a model item.
     */
    boolean hasValueChanged();

    /**
     * Resets all state changes so that no changes are reported.
     */
    void reset();

}
