/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.input.Time");


dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dijit.form.TimeTextBox");

dojo.declare(
    "betterform.ui.input.Time",
    [betterform.ui.ControlValue, dijit.form.TimeTextBox],
    {

        intermediateChanges:true,
        delay:300,
        constraints:null,

        postMixInProperties:function() {
            this.inherited(arguments);
            this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
            if (dojo.attr(this.srcNodeRef, "delay") != undefined && dojo.attr(this.srcNodeRef, "delay") != "") {
                this.delay = eval(dojo.attr(this.srcNodeRef, "delay"));
            }

        },

        postCreate:function() {
            //this.inherited(arguments);
            //this.setCurrentValue();

            this.inherited(arguments);
            dojo.attr(this.domNode, "value", this.value);

            // console.debug("DateTime.postCreate: value:",this.value);
            this.applyValues(this.value);
        },

        applyValues:function() {
            console.debug("Time.applyValues value:",this.value);
        },

        _onFocus:function() {
            this.inherited(arguments);
            this.handleOnFocus();
        },

        _onBlur:function() {
            this.inherited(arguments);
            this.handleOnBlur();
        },

        _delayTimer: undefined,

        onChange: function(/*anything*/ newValue, /*Boolean, optional*/ priorityChange){
            //this.inherited(arguments);

            if(this.incremental){
                if (this.delay > 0) {
                    if (this._delayTimer != undefined ) {
                        clearTimeout(this._delayTimer);
                    }
                    console.debug("Delay: ", this.delay);
                    this._delayTimer = setTimeout(dojo.hitch(this,"setControlValueDelayed"),this.delay);
                } else {
                    this.setControlValue();
                }
            }
        },

        // Not sure why this function is needed and a direct setControlValue in the onChange behaves strange...
        // Probably some dojo quirk or my lack of understanding
        setControlValueDelayed:function() {
            this.setControlValue();
        },

        getControlValue:function(){
            console.debug("betterform.ui.input.Time.getControlValue for Control "+ this.id +": ", this.get('value') + " attr: ",this.attr('value'));
            var currentDate;
            var notISODate = this.get('value');

            if(notISODate == undefined){
                // console.debug("Empty (undefined) date: this: " , this);
                currentDate = this.focusNode.value;
            }else {
                currentDate = dojo.date.stamp.toISOString(notISODate,this.constraints);
            }

            if(currentDate.indexOf("T") != -1){
                currentDate = currentDate.split("T")[1];
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
        }

    });



