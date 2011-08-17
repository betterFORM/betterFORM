/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.select.CheckBox");

dojo.require("dijit.form.FilteringSelect");

dojo.require("dijit.form.CheckBox");


dojo.declare(
        "betterform.ui.select.CheckBox",
        [dijit.form.CheckBox],
{

    postMixInProperties:function() {
        //console.debug("CheckBox.postMixInProperties");
        if(this.srcNodeRef != undefined) {
            this.currentValue = dojo.attr(this.srcNodeRef,"value");
            // console.debug("CheckBox srcNodeRef",this.srcNodeRef, " currentValue:",this.currentValue);
        }
        // console.debug("dojo.attr(this.srcNodeRef,'checked'):",dojo.attr(this.srcNodeRef,"checked"));
        this.checked = dojo.attr(this.srcNodeRef,"checked");
        this.inherited(arguments);
        if(this.selectWidgetId==undefined){
            this.selectWidgetId = dojo.attr(this.srcNodeRef, "selectWidgetId");
        }
        this.selectWidget = dijit.byId(this.selectWidgetId);
        // console.debug("CheckBox.postMixInProperties this.selectWidgetId:", this.selectWidgetId, " this.selectWidget:",this.selectWidget, " this.srcNodeRef:",this.srcNodeRef);


    },

    _onClick: function(/*Event*/ e){
        // console.debug("betterform.ui.select.CheckBox.onClick");
        this.inherited(arguments);

        if(this.selectWidget == undefined) {
            var selectWidgetNode = this.domNode.parentNode;
            while(!dojo.hasClass(selectWidgetNode,"bfCheckBoxGroup")){
                selectWidgetNode = selectWidgetNode.parentNode;
            }
            this.selectWidget = dijit.byId(dojo.attr(selectWidgetNode,"id"));
        }
        if(this.selectWidget == undefined) {
            console.error("CheckBox.onClick: Select (CheckBoxGroup) " + this.selectWidgetId + " could not be found");
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


