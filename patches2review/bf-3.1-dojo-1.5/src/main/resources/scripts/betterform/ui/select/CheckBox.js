/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.select.CheckBox");

dojo.require("dijit.form.FilteringSelect");
dojo.require("betterform.ui.ControlValue");
dojo.require("dijit.form.CheckBox");


dojo.declare(
        "betterform.ui.select.CheckBox",
        [betterform.ui.ControlValue,dijit.form.CheckBox],
{
        selectWidgetId:"",
        value:"",
        selectWidget:null,
        templatePath: dojo.moduleUrl("betterform", "ui/templates/CheckBox.html"),


    postMixInProperties:function() {
        // console.debug("CheckBox.postMixInProperties");
        this.inherited(arguments);
        this.selectWidget = dijit.byId(this.selectWidgetId);
        // console.debug("CheckBox.postMixInProperties this.selectWidgetId:",this.selectWidget, " this.srcNodeRef:",this.srcNodeRef);
        if(this.srcNodeRef != undefined) {
            this.currentValue = dojo.attr(this.srcNodeRef,"value");
            // console.debug("CheckBox srcNodeRef",this.srcNodeRef, " currentValue:",this.currentValue);
        }

        
    },

    onClick: function(/*Event*/ evt){
        console.debug("betterform.ui.select.CheckBox.onClick");
        this.inherited(arguments);
        if(this.selectWidget == undefined) {
            this.selectWidget = dijit.byId(this.selectWidgetId);
        }
        if(this.selectWidget == undefined) {
            conosle.warn("CheckBox.onClick: Select (CheckBoxGroup) " + this.selectWidgetId + " could not be found");
            return;
        }
        this.selectWidget._setCheckBoxGroupValue();
    },

    getControlValue:function() {
        // console.debug("betterform.ui.select.CheckBox.getControlValue");
        // var value = this._getValueAttr();
        // console.debug("CheckBox.getControlValue: value: ",value);
        return this.currentValue;
    }

});


