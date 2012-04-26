/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.input.DropDownTime");

dojo.require("dijit.form.ComboBox");

dojo.declare(
        "betterform.ui.input.DropDownTime",
        [betterform.ui.ControlValue],
{
    templateString: dojo.cache("betterform", "ui/templates/DropDownTime.html"),

    constructor:function() {
        //console.debug("DropDownTime.constructor");

    },

    postMixInProperties:function() {
        // console.debug("DropDownTime.postMixInProperties");
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);

    },

    postCreate:function() {
        // console.debug("DropDownTime.postCreate this.domNode:", this.domNode, " this.value: ", this.value);
        this.inherited(arguments);

        dojo.attr(this.domNode, "value", this.value);
        this.applyValues();

        this.hoursWidget =  new dijit.form.ComboBox({maxLength:2}, this.hoursFacet);
        this.minutesWidget =  new dijit.form.ComboBox({maxLength:2}, this.minutesFacet);
        this.secondsWidget =  new dijit.form.ComboBox({maxLength:2}, this.secondsFacet);

        dojo.connect(this.hoursWidget, "onChange", this, "onHoursChanged");
        dojo.connect(this.minutesWidget, "onChange", this, "onMinutesChanged");

    },
    applyValues:function() {
        // console.debug("DropDownTime.applyValues value:",this.value);
        var timeContainer = this.value.split(":");
        if(timeContainer.length != 3) {
            return;
        }
        // console.debug("DropDownTime.postCreate this.timeContainer:", timeContainer);
        dojo.attr(this.hoursWidget.focusNode, "value", timeContainer[0]);
        dojo.attr(this.minutesWidget.focusNode, "value", timeContainer[1]);
        dojo.attr(this.secondsWidget.focusNode, "value", timeContainer[2]);
    },

    onHoursChanged:function(evt) {
        // console.debug("DropDownTime.onHoursChanged: evt:",evt, " keyCode: ", evt.keyCode);
        var hours = dojo.attr(this.hoursWidget.focusNode,"value");
        // console.debug("constraint:", evt.keyCode != 16 && evt.keyCode != 9 && hours.length == "2");
        if(evt.keyCode != 16 && evt.keyCode != 9 && hours.length == "2"){
            this.minutesWidget.focusNode.focus();
        }
    },

    onMinutesChanged:function(evt) {
        // console.debug("DropDownTime.onMinutesChanged: evt:",evt, " keyCode: ", evt.keyCode);
        var minutes = dojo.attr(this.minutesWidget.focusNode,"value");
        if(evt.keyCode != 16 && evt.keyCode != 9 && minutes.length == "2"){
            this.secondsWidget.focus();
        }
    },

    _onFocus:function() {
        // console.debug("betterform.ui.input.DropDownTime._onFocus");
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function() {
        // console.debug("betterform.ui.input.DropDownTime._onBlur");
        this.inherited(arguments);
        this.handleOnBlur();
    },


    onChange: function(/*anything*/ newValue, /*Boolean, optional*/ priorityChange){
        // console.debug("betterform.ui.input.DropDownTime.onChange");
    },

    getControlValue:function(){
        var hours = dojo.attr(this.hoursWidget.focusNode, "value");
        if(hours == undefined || hours == ""){
            hours = "00";
        }
        var minutes = dojo.attr(this.minutesWidget.focusNode, "value");
        if(minutes == undefined || minutes == ""){
            minutes = "00";
        }
        var seconds = dojo.attr(this.secondsWidget.focusNode, "value");
        if(seconds == undefined || seconds == ""){
            seconds = "00";
        }
        var currentValue = hours + ":" + minutes + ":" + seconds;
        // console.debug("betterform.ui.input.DropDownTime.getControlValue currentValue: ", currentTime);
        return currentValue;
    },

    _handleSetControlValue:function(value) {
        // console.debug("betterform.ui.input.DropDownTime._handleSetControlValue value: ",value);
        if(value != this.value) {
            this.value = value;
            this.applyValues();
        }
    }
});
