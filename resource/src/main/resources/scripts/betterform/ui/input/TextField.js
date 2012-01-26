/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.input.TextField");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dijit.form.TextBox");
dojo.require("dojox.html.entities");


dojo.declare(
        "betterform.ui.input.TextField",
        [betterform.ui.ControlValue,dijit.form.TextBox],
{
    intermediateChanges:true,
    delay:300,

    postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
        if (dojo.attr(this.srcNodeRef, "delay") != undefined && dojo.attr(this.srcNodeRef, "delay") != "") {
            this.delay = eval(dojo.attr(this.srcNodeRef, "delay"));
        }

    },

    postCreate:function() {
        this.inherited(arguments);
        this.setCurrentValue();
        var inputDijit = this;
        dojo.connect(this.domNode, "onkeypress", function (evt){
            if(evt.keyCode){
                switch(evt.keyCode){
                    case dojo.keys.ENTER:
                        // console.debug("keyboard hit return");
                        inputDijit.setControlValue();
                        fluxProcessor.dispatchEvent(inputDijit.xfControlId);
                }
            }
        });
        this._handleSetControlValue(dojox.html.entities.decode(this._getValueAttr()));
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
        // console.debug("chiab.ui.input.TextField.getControlValue for Control "+ this.id +": ",this._getValueAttr() );
        return this._getValueAttr();
    },

    _handleSetControlValue:function(value) {
        this.focusNode.value = value;
        //this.setDisplayedValue(value);
        this.set('displayedValue',value);
    },


    /* function needed by InlineEditBox */
    setTextValue:function(/* String */ value) {
        this.setControlValue(this.getControlValue());
    },

    /* function needed by InlineEditBox */
    getTextValue:function() {
      // console.debug("TextField:getTextValue");
      return this.getControlValue();

    }

});

