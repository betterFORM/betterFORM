/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.select1.ComboBoxOpen");



dojo.require("dijit.form.ComboBox");
dojo.require("betterform.ui.select1._ComboBoxMenu");


dojo.declare(
        "betterform.ui.select1.ComboBoxOpen",
        [betterform.ui.ControlValue,dijit.form.ComboBox],
{
    options:null,
    optgroups:null,

    postMixInProperties:function() {
        // console.debug("betterform.ui.select1.ComboBoxOpen.postMixInProperties srcNode",this.srcNodeRef, " domNode:",this.domNode);
        this.optgroups = dojo.query(".xfOptGroup",this.srcNodeRef);
        var index = 0;
        for(index;index < this.optgroups.length; index++){
            var optgroup = this.optgroups[index];
            var options = dojo.query(".xfSelectorItem",optgroup);
            var sourceNode = this.srcNodeRef;
            dojo.forEach(options, function(item) {
                dojo.place(item, sourceNode, "last");
            });
            this.srcNodeRef = sourceNode;

        }
        this.options = dojo.query("*[value]",this.srcNodeRef);
        var selectOpenPrototype = dojo.clone(this.srcNodeRef);
        // console.debug("cloned selectOpenPrototype: ",selectOpenPrototype);
        dojo.attr(selectOpenPrototype, "id", this.id + "-proto");
        dojo.style(selectOpenPrototype, "display","none");
        // console.debug("changed id to ", dojo.attr(selectOpenPrototype,"id"));
        dojo.place(selectOpenPrototype, dojo.body(),"last");
        // console.dirxml(this.srcNodeRef);
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
        if(dojo.attr(this.srcNodeRef, "incremental") == undefined || dojo.attr(this.srcNodeRef, "incremental") == "" || dojo.attr(this.srcNodeRef, "incremental") == "true"){
            this.incremental = true;
        }else {
            this.incremental = false;
        }
        
    },

    postCreate:function() {
        // console.debug("betterform.ui.select1.ComboBoxOpen.postCreate this.srcNodeRef:",this.srcNodeRef);
        this.inherited(arguments);
        var selected = dojo.query("*[selected]",this.srcNodeRef)[0];
        // console.debug("betterform.ui.select1.ComboBoxOpen.postCreate selected ", selected);
        if(selected != undefined){
            var value  = dojo.attr(selected,"value");
            var label = selected.innerHTML;
            this.setCurrentValue(value);
            this.focusNode.value= label;
        }else {
            var valueLabel = dojo.attr(this.srcNodeRef, "schemavalue");
            this.setCurrentValue(valueLabel);
            this.focusNode.value= valueLabel;
        }
    },


    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function() {
        this.inherited(arguments);
        this.handleOnBlur();
    },

    getControlValue:function() {
        // console.debug("ComboBoxOpen.getControlValue");
        var selectedValue;
        var displayedValue = this.focusNode.value;
        dojo.forEach(this.options,
                function(entry) {
                    // console.debug("Option: ",entry, " value: ",dojo.attr(entry,"value"), " label:",entry.innerHTML, " displayed: ",displayedValue);
                    if(entry.innerHTML == displayedValue){
                        // console.debug("found value: ",dojo.attr(entry,"value"), "for option:",entry);
                        selectedValue = dojo.attr(entry,"value");
                    }
                }
        );
        if(selectedValue != undefined){
            return selectedValue;
        }else if(this.focusNode.value != undefined){
            return this.focusNode.value;
        }else {
            return "";
        }
    },


    onChange: function(evt) {
        // console.debug("ComboBoxOpen.onChange evt: ",evt);
        this.inherited(arguments);
        var selectedNode;
        var displayedValue = this.focusNode.value;
        dojo.forEach(this.options, function(entry) {
                    if(entry.innerHTML == displayedValue){
                        //console.debug("found value: ",dojo.attr(entry,"value"), "for option:",entry);
                        selectedNode = entry
                    }
                }
        );
        // trigger of dispatching select / deselect events only in case of an item is selected but not if a value is inserted by user
        if(selectedNode != undefined){
            fluxProcessor.dispatchEventType(this.xfControl.id, "DOMActivate", dojo.attr(selectedNode,"id"));    
        }

        if (this.incremental) {
            this.setControlValue();
        }
    },

    applyState:function(){
        // console.debug("ComboBoxOpen.applyState START");
        if(this.xfControl.isReadonly()){
            dojo.attr(this.domNode,"disabled","disabled");
        }else{
             this.domNode.removeAttribute("disabled");
        }
    },

    _handleSetControlValue:function(value){
        // console.debug("ComboBoxOpen._handleSetControlValue value",value);
        var labelForValue;
        dojo.forEach(this.options,
                function(entry) {
                    // console.debug("ComboBoxOpen: Option: ",entry, " value: ",dojo.attr(entry,"value"), " label:",entry.innerHTML, " value: ",value);
                    if(dojo.attr(entry, "value") == value){
                        labelForValue = entry.innerHTML;
                    }
                }
        );
        if(labelForValue != undefined) {
            this.focusNode.value = labelForValue;
        }else if(value != undefined){
            this.focusNode.value = value;
        }else {
            this.focusNode.value = "";
        }
    }
});



