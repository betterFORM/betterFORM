/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.ControlValue");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dojo._base.fx");


/**
 * Abstract ControlValue Dijit, do not instantiate directly
 */
dojo.declare(
        "betterform.ui.ControlValue",
        [dijit._Widget, dijit._Templated],
{
    //    tabIndex:0,
    id:null,
    name:"",
    xfControl:null,
    incremental:false,
    currentValue:"",
    alertTooltip:null,
    focused:false,
    hideAlertOnFocus:false,


    applyProperties:function(xfControl, node) {
        this.xfControl = xfControl;
        // console.debug("ControlValue.applyProperties: xfControl:",xfControl, " template node:",node);
        if (dojo.attr(node, "incremental") != undefined && dojo.attr(node, "incremental") != "") {
            this.incremental = eval(dojo.attr(node, "incremental"));
        }else {
            this.incremental = false;
        }
        if (dojo.attr(node, "tabIndex")) {
            this.tabIndex = eval(dojo.attr(node, "tabindex"));
        }
    },
    setCurrentValue:function(value) {
        if (value != undefined) {
            this.currentValue = value;
        } else {
            this.currentValue = this.getControlValue();
        }
    },

    _handleDOMFocusIn:function() {
        // console.debug("ControlValue._handleDOMFocusIn()");
        this.focused = true;
        this.domNode.focus();
    },

    handleOnFocus:function() {
        //storing current control id for handling help
        // console.debug("ControlValue.handleOnFocus storing current control id:", this.id);
        fluxProcessor.currentControlId = this.xfControl.id;
        if (!this.xfControl.isValid()) {
            dojo.addClass(this.focusNode, "caDisplayInvalid");
            this.showAlert();
        }

        if(!this.focused){  fluxProcessor.dispatchEventType(this.xfControl.id,"DOMFocusIn");}
        this.focused = true;
    },

    handleOnBlur:function() {
        // console.debug("ControlValue.handleOnBlur");
        if(this.hideAlertOnFocus){
            this.hideAlert();
        }
        this.focused = false;
        if(!this.incremental){
            this.setControlValue();
        }
        this.handleEmptyRequired();
        fluxProcessor.dispatchEventType(this.xfControl.id,"DOMFocusOut");

        //add 'xfRequiredEmpty' class
    },
    
    handleEmptyRequired:function() {
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
    },

    showAlert:function() {
        if (this.alertTooltip == undefined) {
            this.alertTooltip = new dijit._MasterTooltip();
        }
        var alert = dojo.byId(this.xfControl.id + '-alert');
        if (alert != undefined) {
            this.alertTooltip.show(alert.innerHTML, this.domNode);
        }
    },


    hideAlert:function() {
        if (this.alertTooltip != undefined) {
            this.alertTooltip.hide(this.domNode);
        }
    },

    /*
     only needs to check if XForms MIP readonly is true and disable control in that case. The value itself
     is already present and other MIPs are entirely managed through CSS.
     */
    displayValidity:function(/*Boolean*/ valid) {
        // console.debug("ControlValue.displayValidity (id:" + this.id +")");
        if (valid) {
            dojo.removeClass(this.domNode, "caDisplayInvalid");
            this.hideAlert();
        } else {
            dojo.addClass(this.domNode, "caDisplayInvalid");
            if(this.focused || !this.hideAlertOnFocus){
                this.showAlert();
            }
        }
    },

    applyState:function() {
        if (this.xfControl.isReadonly()) {
            this.attr('disabled', true);
        } else {
            this.attr('disabled', false);
        }
    },

    setControlValue:function(value) {
        console.debug("ControlValue.setControlValue: [id", this.id, " / value: ",value,"] currentValue: ",this.currentValue, " getControlValue", this.getControlValue());
        if (value != undefined && this.currentValue != value) {
            this.currentValue = value;
            this._handleSetControlValue(value);
        }
        var valueNew = this.getControlValue();
        console.debug("betterform.ui.ControlValue.setControlValue ControlId: "+ this.xfControl.id +" valueOld:'" + this.currentValue + "' valueNew:'" + valueNew + "' [update processor:'" + eval(this.currentValue != valueNew) + "']");
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
        console.error("betterform.ui.ControlValue: abstract methods _handleSetControlValue() must be implemented by extending class ", this);
    },
    getControlValue:function() {
        console.error("betterform.ui.ControlValue: abstract methods getControlValue() must be implemented by extending class ", this);
    }


});


