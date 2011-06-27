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
    start:null,
    end:null,
    step:null,

    postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
        this.start = new Number(dojo.attr(this.srcNodeRef, "start"));
        this.step = new Number(dojo.attr(this.srcNodeRef, "step"));
        this.end =   new Number(dojo.attr(this.srcNodeRef, "end"));
        this.value = new Number(dojo.attr(this.srcNodeRef, "value"));
    },

    postCreate:function() {
        this.inherited(arguments);
        this.setCurrentValue();
        // console.debug("Slider.postCreate: start: ", this.start, " step: ", this.step, " end: ", this.end, " value: ",this.valueNode.value);
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
        // console.debug("Range.getControlValue value:",this.valueNode.value);
        return this.valueNode.value;
    },


    _setValueAttr: function(value, priorityChange) {
        // console.debug("Range._setValueAttr value: " + value + " valueNode:",this.valueNode.value);
        if (this.incremental && this.valueNode.value != value && this.valueNode.value != "" && this.valueNode.value != "NaN") {
            this.valueNode.value = value;
            this.setControlValue();
        }
        this.inherited(arguments);

    },

    _handleSetControlValue:function(value) {
        // console.debug("betterform.ui.range.Slider._handleSetControlValue: Value: ", value, " start: ", this.start, " end: ",this.end);
        value = eval(value);
        if(value != undefined && value != "" && value != "NaN"){
            if(value > this.end){
                this._setValueAttr(this.end);
            }
            else if(value < this.start) {
                this._setValueAttr(this.start);
            }else {
                this._setValueAttr(value);
            }
        }
    }
});

