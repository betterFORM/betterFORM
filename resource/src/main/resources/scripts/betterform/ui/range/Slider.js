/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
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
    // overwritten HorizontalSlider.buildRendering to fix handling of label
    buildRendering: function(){
        this.inherited(arguments);
        // revert label id changed by HorizontalSlider
        var label = dojo.query('label[id="'+this.id+'_label"]');
        if(label.length){
            label[0].id = (this.xfControlId+"-label");
            dijit.setWaiState(this.focusNode, "labelledby", label[0].id);
        }
        dijit.setWaiState(this.focusNode, "valuemin", this.minimum);
        dijit.setWaiState(this.focusNode, "valuemax", this.maximum);
    },


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
        // console.debug("Slider._setValueAttr: value: ", value);
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
        // console.debug("Slider._handleSetControlValue: value: ", value);
        var newValue  = parseInt(value, "10");
        if(newValue == undefined || newValue == "" || newValue == "NaN" || newValue < this.minimum){
            newValue = this.minimum;
        }else if(value > this.maximum){
            newValue = this.maximum;
        }
        this._setValueAttr(newValue,null,isServerUpdate);
    }
});

