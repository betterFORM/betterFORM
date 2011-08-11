/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.textarea.SimpleTextarea");


dojo.require("dijit.form.SimpleTextarea");

dojo.declare(
        "betterform.ui.textarea.SimpleTextarea",
        [betterform.ui.ControlValue, dijit.form.SimpleTextarea],
{
   postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },

    postCreate:function() {
        this.inherited(arguments);
        this.setCurrentValue();
    },

    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function() {
        this.inherited(arguments);
        this.handleOnBlur();
    },


    _onInput: function(e){
        this.inherited(arguments);
        // console.debug("betterform.ui.textarea.SimpleTextarea._valueChanged event: ", e);
        if(this.incremental){
            this.setControlValue();
        }
    },

    getControlValue:function(){
        return this._getValueAttr();
    },

    applyState:function() {
        // console.debug("betterform.ui.textarea.SimpleTextarea.applyState",this);
        if (this.xfControl.isReadonly()) {
            dojo.attr(this.domNode,"readonly","readonly");
        } else if(dojo.hasAttr(this.domNode,"readonly")) {
            dojo.removeAttr(this.domNode,"readonly");
        }else if(dojo.hasAttr(this.domNode,"disabled")) {
            dojo.removeAttr(this.domNode,"disabled");
        }
    },

    _handleSetControlValue:function(value) {
        // console.debug("betterform.ui.textarea.SimpleTextarea._handleSetControlValue: Value: ", value);
        this._setValueAttr(value);
    }
});
