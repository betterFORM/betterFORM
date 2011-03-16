/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.input.DropDownDate");

dojo.declare(
        "betterform.ui.input.DropDownDate",
        [betterform.ui.ControlValue],
{
    templateString: dojo.cache("betterform", "ui/templates/DropDownDate.html"),
    widgetsInTemplate:true,
    years:0,
    months:0,
    days:0,

    constructor:function() {
        console.debug("DropDownDate.constructor");
    },

    postMixInProperties:function() {
        console.debug("DropDownDate.postMixInProperties");
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },

    postCreate:function() {
        console.debug("DropDownDate.postCreate this.domNode:", this.valueNode, " this.value: ", this.value);
        this.inherited(arguments);
        dojo.attr(this.valueNode, "value", this.value);
        this.applyValues();

        dojo.connect(dijit.byId(this.daysFacet.id), "onChange", this, "onDaysChanged");
        dojo.connect(dijit.byId(this.monthsFacet.id), "onChange", this, "onMonthsChanged");
        dojo.connect(dijit.byId(this.yearsFacet.id), "onChange", this, "onYearsChanged");
        dojo.connect(dijit.byId(this.yearsFacet.id), "onBlur", this, "onYearsBlur");
    },

    applyValues:function() {
        console.debug("DropDownDate.applyValues value:",this.value);
        if (this.value != undefined) {
            var dropDownDateContainer = this.value.split("-");
            if(dropDownDateContainer.length != 3) {
                return;
            }
            console.debug("DropDownDate.postCreate this.timeContainer:", dropDownDateContainer);

            years = dropDownDateContainer[0];
            months = dropDownDateContainer[1];
            days = dropDownDateContainer[2];

            dijit.byId(this.daysFacet.id).setValue(days);
            dijit.byId(this.monthsFacet.id).setValue(months);
            dijit.byId(this.yearsFacet.id).setValue(years);
         }
    },

    onDaysChanged:function(evt) {
        console.debug("DropDownDate.onDaysChanged.");
        this.days = dijit.byId(this.daysFacet.id).getValue();
        this.setCurrentDate();
    },

    onMonthsChanged:function(evt) {
        console.debug("DropDownDate.onMonthsChanged.");
        this.months = dijit.byId(this.monthsFacet.id).getValue();
        this.setCurrentDate();
    },

    onYearsChanged:function(evt) {
        console.debug("DropDownDate.onYearsChanged.");
        this.years = dijit.byId(this.yearsFacet.id).getValue();
        this.setCurrentDate();
    },

    onYearsBlur:function(evt) {
        console.debug("DropDownDate.onYearsBlur.");
        this.setControlValue();
    },

    _onFocus:function() {
        console.debug("betterform.ui.input.DropDownDate._onFocus");
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function() {
        console.debug("betterform.ui.input.DropDownDate._onBlur");
        this.inherited(arguments);
        this.handleOnBlur();
    },

    onChange: function(/*anything*/ newValue, /*Boolean, optional*/ priorityChange){
        console.debug("betterform.ui.input.DropDownDate.onChange");
    },

    getControlValue:function(){
        console.debug("betterform.ui.input.DropDownDate.getControlValue currentValue: ", this.value);
        return this.value;
    },

     setCurrentDate:function(){
         var currentDate = this.years + "-" + this.months + "-" + this.days;
         console.debug("betterform.ui.input.DropDownDate.setCurrentDate currentDate: ", currentDate);
         dojo.attr(this.valueNode, "value", currentDate);
         this.value = currentDate;
     }
});
