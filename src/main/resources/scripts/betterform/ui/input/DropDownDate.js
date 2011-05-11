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
    rangeStart: 1900,
    rangeEnd:0,
    years:'',
    months:'',
    days:'',
    monthsArray: new Array("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"),

    postMixInProperties:function() {
        console.debug("DropDownDate.postMixInProperties");
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },

    postCreate:function() {
        console.debug("DropDownDate.postCreate this.domNode:", this.valueNode, " this.value: ", this.value);
        this.inherited(arguments);

        dojo.attr(this.valueNode, "value", this.value);
        this.applyValues(this.value);
        dojo.connect(dijit.byId(this.daysFacet.id), "onChange", this, "onDaysChanged");
        dojo.connect(dijit.byId(this.monthsFacet.id), "onChange", this, "onMonthsChanged");
        dojo.connect(dijit.byId(this.yearsFacet.id), "onChange", this, "onYearsChanged");
        //dojo.connect(dijit.byId(this.yearsFacet.id), "onBlur", this, "onYearsBlur");
    },

    applyValues:function(value) {
        console.debug("DropDownDate.applyValues value:",value);
        if (value != undefined) {

            var dropDownDateContainer = value.split("-");
            if (dropDownDateContainer.length != 3) {
                return;
            }
            console.debug("DropDownDate.postCreate this.timeContainer:", dropDownDateContainer);

            this.years = dropDownDateContainer[0];
            this.months = dropDownDateContainer[1];
            this.days = dropDownDateContainer[2];

            dijit.byId(this.daysFacet.id).set('value', this.days);
            dijit.byId(this.monthsFacet.id).set('value', this.monthsArray[parseInt(this.months, "10") - 1]);
            //dijit.byId(this.monthsFacet.id).set('displayValue', this.monthsArray[parseInt(months)-1]);
            dijit.byId(this.yearsFacet.id).set('value', this.years);
        }
    },

    onDaysChanged:function(evt) {
        console.debug("DropDownDate.onDaysChanged.");
        var selectedItem = dijit.byId(this.daysFacet.id).get('item');
        if (selectedItem != undefined) {
            this.days = selectedItem.value;
        } else {
            this.days = dijit.byId(this.daysFacet.id).getValue();
        }
        this.setCurrentDate();
    },

    onMonthsChanged:function(evt) {
        console.debug("DropDownDate.onMonthsChanged.");
        var selectedItem = dijit.byId(this.monthsFacet.id).get('item');
        var value;
        if (selectedItem != undefined) {
            console.debug("DropDownDate.onMonthsChanged() selectedItem defined: |", selectedItem.value, "|");
            value = parseInt(selectedItem.value, "10");
        } else {
            var month = dijit.byId(this.monthsFacet.id).getValue();
            if (isNaN(month)) {
                value = parseInt(dojo.indexOf(this.monthsArray,month) + 1, "10");
            } else {
                value = parseInt(month, "10");
            }
        }

        console.debug("DropDownDate.onMonthsChanged() current month value:", value);
        if (value < 10) {
            console.debug("DropDownDate.onMonthsChanged() adding leading zero to month.");
            value = "0" + value;
            console.debug("DropDownDate.onMonthsChanged() modified month value:", value);
        }

        this.months = value;
        this.setCurrentDate();
    },

    onYearsChanged:function(evt) {
        console.debug("DropDownDate.onYearsChanged.");
        var selectedItem = dijit.byId(this.yearsFacet.id).get('item');
        if (selectedItem != undefined) {
            this.years = selectedItem.value;
        } else {
            this.years = dijit.byId(this.yearsFacet.id).getValue();
        }
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

    onChange: function(/*anything*/ newValue, /*Boolean, optional*/ priorityChange) {
        console.debug("betterform.ui.input.DropDownDate.onChange");
    },

    _handleSetControlValue:function(value) {
        console.debug("betterform.ui.input.DropDownDate._handleSetControlValue value",value);
        this.applyValues(value);
    },


    getControlValue:function() {
        console.debug("betterform.ui.input.DropDownDate.getControlValue currentValue: ", this.value);
        return this.value;
    },

    setCurrentDate:function() {
        console.debug("betterform.ui.input.DropDownDate.setCurrentDate computeDate: ", this.years + "-" + this.months + "-" + this.days);
        var currentDate = this.years + "-" + this.months + "-" + this.days;
        console.debug("betterform.ui.input.DropDownDate.setCurrentDate currentDate: ", currentDate);
        dojo.attr(this.valueNode, "value", currentDate);
        this.value = currentDate;
        this.setControlValue();
    },

    /*
     only needs to check if XForms MIP readonly is true and disable control in that case. The value itself
     is already present and other MIPs are entirely managed through CSS.
     */
    applyState:function() {
        dijit.byId(this.daysFacet.id).attr("readOnly", this.xfControl.isReadonly());
        dijit.byId(this.monthsFacet.id).attr("readOnly", this.xfControl.isReadonly());
        dijit.byId(this.yearsFacet.id).attr("readOnly", this.xfControl.isReadonly());
    }
});