/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.input.Time");


dojo.declare(
        "betterform.ui.input.Time",
        [betterform.ui.ControlValue],
{
    templateString: dojo.cache("betterform", "ui/templates/Time.html"),

    constructor:function() {
        console.debug("Time.constructor");

    },

    postMixInProperties:function() {
        console.debug("Time.postMixInProperties");
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },

    postCreate:function() {
        console.debug("Time.postCreate this.domNode:", this.domNode, " this.value: ", this.value);
        this.inherited(arguments);
        dojo.attr(this.domNode, "value", this.value);
        this.applyValues();
        dojo.connect(this.hoursFacet, "onkeyup", this, "onHoursChanged");
        dojo.connect(this.minutesFacet, "onkeyup", this, "onMinutesChanged");

    },
    applyValues:function() {
        console.debug("Time.applyValues value:",this.value);
        var timeContainer = this.value.split(":");
        if(timeContainer.length != 3) {
            return;
        }
        console.debug("Time.postCreate this.timeContainer:", timeContainer);
        dojo.attr(this.hoursFacet, "value", timeContainer[0]);
        dojo.attr(this.minutesFacet, "value", timeContainer[1]);
        dojo.attr(this.secondsFacet, "value", timeContainer[2]);
    },

    onHoursChanged:function(evt) {
        console.debug("Time.onHoursChanged: evt:",evt, " keyCode: ", evt.keyCode);
        var hours = dojo.attr(this.hoursFacet,"value");
        if(evt.keyCode != 16 && evt.keyCode != 9 && hours.length == "2"){
            this.minutesFacet.focus();
        }
    },

    onMinutesChanged:function(evt) {
        console.debug("Time.onMinutesChanged: evt:",evt, " keyCode: ", evt.keyCode);
        var minutes = dojo.attr(this.minutesFacet,"value");
        if(evt.keyCode != 16 && evt.keyCode != 9 && minutes.length == "2"){
            this.secondsFacet.focus();
        }
    },

    _onFocus:function() {
        console.debug("betterform.ui.input.Time._onFocus");
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function() {
        console.debug("betterform.ui.input.Time._onBlur");
        this.inherited(arguments);
        this.handleOnBlur();
    },


    onChange: function(/*anything*/ newValue, /*Boolean, optional*/ priorityChange){
        console.debug("betterform.ui.input.Time.onChange");
    },

    getControlValue:function(){

        var hours = dojo.attr(this.hoursFacet, "value");
        if(hours == undefined || hours == ""){
            hours = "00";
        }
        var minutes = dojo.attr(this.minutesFacet, "value");
        if(minutes == undefined || minutes == ""){
            minutes = "00";
        }
        var seconds = dojo.attr(this.secondsFacet, "value");
        if(seconds == undefined || seconds == ""){
            seconds = "00";
        }
        var currentTime = hours + ":" + minutes + ":" + seconds;
        console.debug("betterform.ui.input.Time.getControlValue currentValue: ", currentTime);
        return currentTime;
    },

    _handleSetControlValue:function(value) {
        console.debug("betterform.ui.input.Time._handleSetControlValue value: ",value);
    }
});
