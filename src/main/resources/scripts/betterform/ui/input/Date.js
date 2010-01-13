/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.input.Date");

dojo.require("betterform.ui.ControlValue");
dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dijit.form.DateTextBox");

dojo.declare(
        "betterform.ui.input.Date",
        [betterform.ui.ControlValue, dijit.form.DateTextBox],
{   
    
    postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
        /* incremental date does not make sense, if wanted anyway comment in the following line of code and implement _valueChange */
        // dojo.connect(this,"_onKeyPress", this,"_valueChanged");
    },
    postCreate:function() {
        this.inherited(arguments);
        this.setCurrentValue();
    },
    
    _onFocus:function() {
        this.inherited(arguments);
        //this.handleOnFocus(["above", "below"]);
        this.handleOnFocus();
    },

    _onBlur:function(){
        //console.debug("Date._onBlur");
        if(this.hideAlertOnFocus) {
            this.hideAlert();
        }
        this.focused = false;
        this.setControlValue();
        this.handleEmptyRequired();
        fluxProcessor.dispatchEventType(this.xfControl.id,"DOMFocusOut");
        this.inherited(arguments);
    },

    getControlValue:function(){
        // console.debug("chiab.ui.input.Date.getControlValue for Control "+ this.id +": ",this.getValue() + " attr: ",this.attr('value'));
        var currentDate;
        var notISODate = this.attr('value');
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

    _handleSetControlValue:function(date) {
        // console.debug("Date._handleSetControlValue date:",date);
        if(date == undefined || date == ""){
            this._setValueAttr("");    
        }
        else {
            this._setValueAttr(dojo.date.stamp.fromISOString(date,this.constraint));
        }
    }

});



