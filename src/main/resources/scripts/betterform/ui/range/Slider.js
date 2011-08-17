/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.range.Slider");

dojo.require("dijit.form.HorizontalSlider");


dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare(
        "betterform.ui.range.Slider",
        [betterform.ui.ControlValue,dijit.form.HorizontalSlider],
{

    postMixInProperties:function() {
         this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },

    postCreate:function() {
        this.inherited(arguments);
        this.setCurrentValue();
    },

    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function() {
        this.inherited(arguments);
        this.handleOnBlur();
    },

    getControlValue:function() {
        return dojo.attr(this.valueNode, "value");
    },

    _setValueAttr: function(value, priorityChange,isServerUpdate) {
        if(!isServerUpdate){
            var tmpValue = dojo.attr(this.valueNode, "value");
            // console.debug("Slider._setValueAttr value: " + value + " valueNode.value:", tmpValue);
            if (this.incremental && tmpValue != value && tmpValue != "" && tmpValue != "NaN") {
                this.setControlValue(value);
            }
        }
        this.inherited(arguments);
    },

    _handleSetControlValue:function(value, isServerUpdate) {
        var newValue  = parseInt(value, "10");
        if(newValue == undefined || newValue == "" || newValue == "NaN" || newValue < this.minimum){
            newValue = this.minimum;
        }else if(value > this.maximum){
            newValue = this.maximum;
        }
        this._setValueAttr(newValue,null,isServerUpdate);
    }
});

