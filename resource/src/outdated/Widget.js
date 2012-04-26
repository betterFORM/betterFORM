/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("bf.Widget");

dojo.require("dijit._Widget");
dojo.require("dijit.form._FormWidget");
/*dojo.require("dojo._base.fx");*/


/**
 * Abstract ControlValue Dijit, do not instantiate directly
 */
dojo.declare(
        "bf.Widget",
        dijit._Widget,
{
    id:null,
    name:"",
    xfControl:null,
    incremental:false,
    currentValue:"",    
    bfFocus:false,
    value:null,

    postCreate:function() {
        this.inherited(arguments);

        dojo.connect(this.xfControl, "handleStateChanged", function(contextInfo){
            console.debug("handleStateChanged for:  ",this.xfControl);
            if(contextInfo){
                console.debug("contextInfo",contextInfo);
            }
            //apply value to widget - handle required + readonly if necessary
            this.set("value","foo");
        });


        this.setCurrentValue();
    },

    setCurrentValue:function(value) {
        //console.debug("ControlValue.setCurrentValue value:",value);
        if (value != undefined) {
            this.currentValue = value;
        } else {
            this.currentValue = this.getControlValue();
        }
    },
    _handleDOMFocusIn:function() {
        // console.debug("ControlValue._handleDOMFocusIn()");
        this.bfFocus = true;
        this.domNode.focus();
    },


    handleOnFocus:function() {
        //storing current control id for handling help
        // console.debug("ControlValue.handleOnFocus storing current control id:", this.id, " value: ",this.currentValue);

        fluxProcessor.currentControlId = this.xfControl.id;

            if (!this.bfFocus && fluxProcessor.usesDOMFocusIN) {
                // console.debug("ControlValue: dispatch DOMFocusIn to ",this.xfControl.id);
                fluxProcessor.dispatchEventType(this.xfControl.id,"DOMFocusIn");
            }
        this.bfFocus = true;
        if(this.xfControl.isValid()){
            dojo.publish("/xf/valid",[this.xfControl.id,"onFocus"]);
        }else {
            dojo.publish("/xf/invalid",[this.xfControl.id,"onFocus"]);
        }        
    },

    /*
    * - show alert if any
    * - hide hint if any
    */
    /*former handle onBlur */

    handleOnBlur:function() {
        // console.debug("ControlValue.handleOnBlur");
        this.bfFocus = false;
        if(this.xfControl.isValid()){
            dojo.publish("/xf/valid",[this.xfControl.id,"onBlur"]);
        }else {
            dojo.publish("/xf/invalid",[this.xfControl.id,"onBlur"]);
        }


        if(!this.incremental){
            this.setControlValue();
        }
        fluxProcessor.dispatchEventType(this.xfControl.id,"DOMFocusOut");
    },
    
    // TODO: change handleEmpmtyRequired to activeCommonChild logic
/*    handleEmptyRequired:function() {
        // console.debug("handleEmptyRequired: isRequired=" + this.xfControl.isRequired(), " currentValue: ",this.currentValue, " getValue: ",this.getControlValue());
        if (this.xfControl.isRequired() && this.currentValue == "" && this.getControlValue() == "") {
            dojo.addClass(this.xfControl.domNode, "xfRequiredEmpty");

            var label = dojo.byId(this.xfControl.id + "-label");
            var content;
            if (label != undefined && label != "") {
                content = "The value for  '" + label.innerHTML + "' is required. Please fill in a value.";
                dijit.byId("betterformMessageToaster").setContent(content, 'message');
            }else{
                content = "This field is required. Please fill in a value.";
                dijit.byId("betterformMessageToaster").setContent(content, 'message');
            }
            dijit.byId("betterformMessageToaster").show();
            // for tesing
            if(fluxProcessor.webtest == 'true') {
                fluxProcessor.logTestMessage(content);
            }
        } else {
            dojo.removeClass(this.xfControl.domNode, "xfRequiredEmpty");
        }
    }*/


    /*
     only needs to check if XForms MIP readonly is true and disable control in that case. The value itself
     is already present and other MIPs are entirely managed through CSS.
     */

    applyState:function() {
        // console.debug("ControlValue.applyState (id:" + this.id +")");
        this.set("readOnly",this.xfControl.isReadonly());
    },

    setControlValue:function(value) {
        // console.debug("ControlValue.setControlValue: [id", this.id, " / value: ",value,"] currentValue: ",this.currentValue, " getControlValue", this.getControlValue());
        if (value != undefined && this.currentValue != value) {
            this.currentValue = value;
            this._handleSetControlValue(value,false);
        }
        var valueNew = this.getControlValue();
        // console.debug("bf.ui.ControlValue.setControlValue ControlId: "+ this.xfControl.id +" valueOld:'" + this.currentValue + "' valueNew:'" + valueNew + "' [update processor:'" + eval(this.currentValue != valueNew) + "']");
        if (this.currentValue != valueNew) {
            this.xfControl.setControlValue(valueNew);
            this.currentValue = valueNew;
        }
    },

    _setLabel:function(value) {
        var label = dojo.byId(this.xfControl.id + "-label");
        if (label != undefined && value != undefined) {
            label.innerHTML = value;
        }
    },


    /*
     ###########################################################################################################
     ####################                     Abstract Methods                              ####################
     ####################      Methods must be implemented by extending Classes             ####################
     ###########################################################################################################
     */
    _handleSetControlValue:function(value) {
        console.error("bf.ui.ControlValue: abstract methods _handleSetControlValue() must be implemented by extending class ", this);
    },
    getControlValue:function() {
        console.error("bf.ui.ControlValue: abstract methods getControlValue() must be implemented by extending class ", this);
    }


});


