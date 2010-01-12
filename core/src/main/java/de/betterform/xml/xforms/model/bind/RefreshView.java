/* Copyright 2009 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.model.bind;

/**
 * Refresh Viewport to model items. Provides access to changes of a model
 * item's properties.
 *
 */
public interface RefreshView {

    void setValueChangedMarker();
    void setReadonlyMarker();
    void setReadWriteMarker();
    void setEnabledMarker();
    void setDisabledMarker();
    void setOptionalMarker();
    void setRequiredMarker();
    void setValidMarker();
    void setInvalidMarker();

    void reset();

    boolean isValueChangedMarked();
    boolean isValidMarked();
    boolean isInvalidMarked();
    boolean isReadonlyMarked();
    boolean isReadwriteMarked();
    boolean isRequiredMarked();
    boolean isOptionalMarked();
    boolean isEnabledMarked();
    boolean isDisabledMarked();
}
