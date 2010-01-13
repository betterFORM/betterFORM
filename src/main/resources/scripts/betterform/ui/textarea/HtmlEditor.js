/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.textarea.HtmlEditor");

dojo.require("betterform.ui.ControlValue");
dojo.require("dijit.Editor");

dojo.declare(
        "betterform.ui.textarea.HtmlEditor",
        [betterform.ui.ControlValue, dijit.Editor],
{

   buildRendering:function() {
        this.domNode = this.srcNodeRef;
        this.setCurrentValue(this.srcNodeRef.innerHTML);
        this._attachTemplateNodes(this.domNode);

   },
   postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },
    postCreate:function() {
        this.inherited(arguments);
    },
    
    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function(){
        this.inherited(arguments);
        this.handleOnBlur();
   },


    onDisplayChanged: function(e){
        this.inherited(arguments);
        //console.debug("betterform.ui.textarea.HtmlEditor.onDisplayChanged event: ", e);
        if(this.incremental){
            this.setControlValue();
        }
    },


    getControlValue:function(){
        return this.getValue();
    },


    _handleSetControlValue:function(value) {
        // console.debug("betterform.ui.textarea.SimpleTextarea._handleSetControlValue: Value: ", value);
        this.setValue(value);
    }



});


