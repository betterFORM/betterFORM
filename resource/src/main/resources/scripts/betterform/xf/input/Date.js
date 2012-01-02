/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.xf.input.Date");


dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dijit.form.DateTextBox");
dojo.require("betterform.xf.Widget");

dojo.declare(
        "betterform.xf.input.Date",
        [betterform.xf.Widget,dijit.form.DateTextBox],
{
    xfControl:null,
    value:null,

    postCreate:function() {
        this.inherited(arguments);
        console.debug("date: xfcontrol",this.xfControl);

    },

    /* overwritten function of SuperClass ValidationTextBox */
    validate: function(/*Boolean*/ isFocused){},

    getControlValue:function(){
        // console.debug("betterform.ui.input.Date.getControlValue for Control "+ this.id +": ",this.getValue() + " attr: ",this.attr('value'));
        var currentDate;
        var notISODate = this.get('value');
        if(notISODate == undefined){
           // console.debug("Empty (undefined) date: this: " , this);
           currentDate = this.focusNode.value;
        }else {
            currentDate = dojo.date.stamp.toISOString(notISODate,this.constraint);
        }
        if(currentDate.indexOf("T") != -1){
            currentDate = currentDate.split("T")[0];
        }
        return currentDate; 
    },

    setControlValue:function(date) {
        // console.debug("Date._handleSetControlValue date:",date);
        if(date == undefined || date == ""){
            dojo.attr(this.textbox, "value","");
        }
        else {
            this._setValueAttr(dojo.date.stamp.fromISOString(date,this.constraint));
            // this._setValueAttr(this.parse(date, this.constraints), false, date);
        }
    }


});



