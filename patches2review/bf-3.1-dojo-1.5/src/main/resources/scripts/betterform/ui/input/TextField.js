/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.input.TextField");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dijit.form.TextBox");
dojo.require("betterform.ui.ControlValue");

dojo.declare(
        "betterform.ui.input.TextField",
        [betterform.ui.ControlValue,dijit.form.TextBox],
{
    intermediateChanges:true,
    
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

	onChange: function(/*anything*/ newValue, /*Boolean, optional*/ priorityChange){
        this.inherited(arguments);

        if(this.incremental){
            this.setControlValue();
        }

    },
    getControlValue:function(){
        // console.debug("chiab.ui.input.TextField.getControlValue for Control "+ this.id +": ",this._getValueAttr() );
        return this._getValueAttr();
    },

    _handleSetControlValue:function(value) {
        this.focusNode.value = value;
        //this.setDisplayedValue(value);
        this.attr('displayedValue',value);
    },


    /* function needed by InlineEditBox */
    setTextValue:function(/* String */ value) {
        this.setControlValue(this.getControlValue());
    },

    applyState:function() {
        // console.debug("betterform.ui.input.TextField.applyState",this);
        if (this.xfControl.isReadonly()) {
            dojo.attr(this.domNode,"readonly","readonly");
        } else if(dojo.hasAttr(this.domNode,"readonly")) {
            dojo.removeAttr(this.domNode,"readonly");
        }else if(dojo.hasAttr(this.domNode,"disabled")) {
            dojo.removeAttr(this.domNode,"disabled");
        }
    },


    /* function needed by InlineEditBox */
    getTextValue:function() {
      // console.debug("TextField:getTextValue");
      return this.getControlValue();

    }

});

