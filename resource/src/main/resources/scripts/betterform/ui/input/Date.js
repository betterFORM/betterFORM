/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.input.Date");


dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dijit.form.DateTextBox");

dojo.declare(
        "betterform.ui.input.Date",
        [betterform.ui.ControlValue, dijit.form.DateTextBox],
{
    constructor:function() {
       this.incremental = true;
    },

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
    
    onChange: function(/*anything*/ newValue, /*Boolean, optional*/ priorityChange){
        // console.debug("betterform.ui.input.Date.onChange");
        this.inherited(arguments);
        if(this.incremental){
            this.setControlValue();
        }
    },

    _onFocus:function() {
        //console.debug("betterform.ui.input.Date._onFocus: "+ this.id);
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function(){
        this.handleOnBlur();
        this.inherited(arguments);
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

    _handleSetControlValue:function(date) {
        // console.debug("Date._handleSetControlValue date:",date);
        if(date == undefined || date == ""){
            dojo.attr(this.textbox, "value","");
        }
        else {
            this._setValueAttr(dojo.date.stamp.fromISOString(date,this.constraint));
            // this._setValueAttr(this.parse(date, this.constraints), false, date);
        }
    },

    _handleDOMFocusIn:function() {
        //console.debug("Date._handleDOMFocusIn()");
        this.bfFocus = true;
        var control = dijit.byId(this.id);

        if (control != undefined ) {
            control.focus();
        }

    }

});



