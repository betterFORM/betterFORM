/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.select1.ComboBox");





dojo.declare(
        "betterform.ui.select1.ComboBox",
        betterform.ui.ControlValue,
{


    buildRendering:function() {
        this.domNode = this.srcNodeRef;
    },
    postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
        var incremental = dojo.attr(this.srcNodeRef, "incremental");
        if(incremental == undefined || incremental == "" || incremental == "true"){
            this.incremental = true;
        }else {
            this.incremental = false;
        }
        
    },

    postCreate:function() {
        // console.debug("ComboBox.postCreate");
        dojo.connect(this.domNode,"onchange", this,"_onChange");
        this.setCurrentValue();
    },

    focus:function() {
        this.domNode.focus();
    },

    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function(){
        this.inherited(arguments);
        this.handleOnBlur();
    },

    _onChange:function() {
        // console.debug("ComboBox._onChange: this:",this);
        var selectedNode = this.domNode.options[this.domNode.selectedIndex];
        fluxProcessor.dispatchEventType(this.xfControl.id, "DOMActivate", dojo.attr(selectedNode,"id"));

        if(this.incremental){
            this.setControlValue();
        }
    },

    getControlValue:function() {
        if(this.domNode.selectedIndex != -1 &&  this.domNode.options != undefined) {
        var option = this.domNode.options[this.domNode.selectedIndex];
        return dojo.attr(option,"value");
        }else {
            return null;
        }
    },

    _handleSetControlValue:function(value){
        // console.debug("ComboBox._handleSetControlValue value: " +value + " this.domNode.options.length: ",this.domNode.options.length);
        for(i =0;i<this.domNode.options.length;i++){
            // console.debug("ComboBox._handleSetControlValue optValue: " + this.domNode.options[i].value + " selectIndex: " +this.domNode.selectedIndex);
            if(this.domNode.options[i].value == value){
                this.domNode.selectedIndex = i;
            }
        }
    },

    applyState:function(){
        if(this.xfControl.isReadonly()){
            dojo.attr(this.domNode,"disabled","disabled");
        }else{
             this.domNode.removeAttribute("disabled");
        }
    }
});



