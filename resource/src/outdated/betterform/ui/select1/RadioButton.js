/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.select1.RadioButton");



dojo.require("dijit.form.CheckBox");


dojo.declare(
        "betterform.ui.select1.RadioButton",
        [betterform.ui.ControlValue,dijit.form.RadioButton],
{
    select1Dijit:null,

    buildRendering:function() {
         // console.debug("RadioButton.buildRendering");
        if(dojo.attr(this.srcNodeRef, "selected") == undefined ||dojo.attr(this.srcNodeRef, "selected") == "") {
            dojo.attr(this.srcNodeRef, "selected", "false");
        }
        if(dojo.attr(this.srcNodeRef, "datatype") == undefined ||dojo.attr(this.srcNodeRef, "datatype") == "") {
            dojo.attr(this.srcNodeRef, "datatype", "radio");
        }
        // console.dirxml(this.srcNodeRef);

        
        this.currentValue = dojo.attr(this.srcNodeRef, "value");
         // console.debug("RadioButton.buildRendering currentValue: ",this.currentValue);

        if(dojo.attr(this.srcNodeRef,"parentId") != undefined && dojo.attr(this.srcNodeRef,"parentId") != "" ) {
            this.parentId = dojo.attr(this.srcNodeRef,"parentId");
        }

        var selected= dojo.attr(this.srcNodeRef, "selected");
        // console.debug("RadioButton.buildRendering: parentId: "+ this.parentId + " value: " + this.currentValue + " selected:" + this.selected);
        this.inherited(arguments);
        if(selected != undefined && selected == "true"){
            this.set('checked', true);
        }
    },
    postMixInProperties:function() {
        // console.debug("RadioButton.postMixInProperties", this.srcNodeRef);
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
        if(dojo.attr(this.srcNodeRef, "incremental") == undefined || dojo.attr(this.srcNodeRef, "incremental") == "" || dojo.attr(this.srcNodeRef, "incremental") == "true"){
            this.incremental = true;
        }else {
            this.incremental = false;
        }
        
    },


    postCreate:function() {
        this.inherited(arguments);
        // console.debug("RadioButton.postCreate: connect _setRadioGroupValue");
        if(dijit.byId(this.parentId+"-value") == undefined && this.parentId != undefined) {
            console.warn("RadioButton.postCreate: Parent Select1 Dijit undefined, will be created; ParentId is: ",this.parentId);
            dojo.hitch(this, new betterform.ui.Control({},this.parentId));
        }
        this.select1Dijit = dijit.byId(this.parentId+"-value");
        // console.debug("RadioButton: selectDijit = ",this.select1Dijit, " parentId: ",this.parentId);
        dojo.connect(this, "_onClick", this.select1Dijit, "_setRadioGroupValue");
        // console.debug("END RadioButton.postCreate");
    },


    getControlValue:function(){
        // console.debug("RadioButton.getControlValue currentValue: " , this.currentValue);
        return this.currentValue;
    },
  
    _handleSetControlValue:function(/*String */ value) {
        // console.debug("RadioButton._handleSetControlValue value: ",value, " this.select1Dijit: ", this.select1Dijit);
        this.currentValue = value;
        dojo.attr(this.focusNode, "value",value);
        if(this.select1Dijit != undefined && this.select1Dijit.currentValue == value) {
            this.set("checked", true);
        }
    }
});



