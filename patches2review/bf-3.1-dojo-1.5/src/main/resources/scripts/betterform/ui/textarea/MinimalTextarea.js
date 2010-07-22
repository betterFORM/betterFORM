/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.textarea.MinimalTextarea");

dojo.require("betterform.ui.ControlValue");

dojo.declare(
        "betterform.ui.textarea.MinimalTextarea",
        betterform.ui.ControlValue,
{
    rows:5,
    cols:40,
    templatePath: dojo.moduleUrl("betterform", "ui/templates/MinimalTextarea.html"),
    templateString: null,

   postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },

    postCreate:function() {
        this.inherited(arguments);
        this.inputNode.value = this.srcNodeRef.innerHTML;
        dojo.connect(this.domNode,"onkeypress", this,"_valueChanged");
    },

    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function(){
        this.inherited(arguments);
        this.handleOnBlur();
    },

    getControlValue:function(){
        return this.inputNode.value;
    },

    _valueChanged: function(evt){
        if(this.incremental){
            this.setControlValue();
        }
    },


    applyState:function() {
        // console.debug("betterform.ui.textarea.MinimalTextarea.applyState",this);
        if (this.xfControl.isReadonly()) {
            dojo.attr(this.inputNode,"disabled","disabled");            
        } else if(dojo.hasAttr(this.inputNode,"readonly")) {
            dojo.removeAttr(this.inputNode,"disabled");
        }else if(dojo.hasAttr(this.inputNode,"disabled")) {
            dojo.removeAttr(this.inputNode,"disabled");
        }
    },

    _handleSetControlValue:function(value) {
        // console.debug("betterform.ui.textarea.MinimalTextarea._handleSetControlValue: Value: ", value);
        this._setValueAttr(value);
    }
});
