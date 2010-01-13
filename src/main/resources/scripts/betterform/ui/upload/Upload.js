/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.upload.Upload");

dojo.require("betterform.ui.ControlValue");
/*dojo.require("dojox.form.FileUploader");*/
dojo.require("dijit.form.Button");

dojo.declare(
        "betterform.ui.upload.Upload",
/*        [betterform.ui.ControlValue,dojox.form.FileInputOverlay],*/
        betterform.ui.ControlValue,
{
    postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },

    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function() {
        this.inherited(arguments);
        //this.handleOnBlur();
    },

    getControlValue:function() {
        console.warn("betterform.ui.upload.Upload.getControlValue: Value: ");
    },

    _handleSetControlValue:function(value) {
        console.warn("betterform.ui.upload.Upload._handleSetControlValue: Value: ");
    }
});

