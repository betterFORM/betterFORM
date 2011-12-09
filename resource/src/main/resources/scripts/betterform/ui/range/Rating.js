/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.range.Rating");

dojo.require("dojox.form.Rating");


dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare(
        "betterform.ui.range.Rating",
        [betterform.ui.ControlValue,dojox.form.Rating],
{
    postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },

    postCreate:function() {
        this.inherited(arguments);
        this.setCurrentValue();
    },

    // overwritten to implement readonly 
    _onMouse: function(evt){
        //console.debug("betterform.ui.range.Rating.onMouse: readonly: ", this.xfControl.isReadonly());
        /*
            if(!this.xfControl.isReadonly()){
                this.inherited(arguments);
            }
        */
    },

    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function(){
        this.inherited(arguments);
        this.handleOnBlur();
    },
   
    getControlValue:function() {
        return this.get('value');
    },
    
    onStarClick:function(/* Event */evt){
        if(!this.xfControl.isReadonly()){
            this.inherited(arguments);
            if(this.incremental){
                this.setControlValue();
            }
        }
    },


    _handleSetControlValue:function(value) {
        // console.debug("betterform.ui.range.Rating._handleSetControlValue: Value: ", value);
        this.set("value",value);
    }

});

