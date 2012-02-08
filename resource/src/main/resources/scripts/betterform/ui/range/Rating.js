/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
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
    maximum:null,
    minimum:null,

    buildRendering: function(){
        this.inherited(arguments);
        this.minimum = Number(dojo.attr(this.srcNodeRef, "start"));
        this.maximum = Number(dojo.attr(this.srcNodeRef, "end"));

        var initialValue = dojo.attr(this.srcNodeRef, "value");
        if(initialValue == "" || initialValue == undefined || isNaN(initialValue)){
            this.value = 0;
        }
        var label = dojo.query('label[id="'+this.id+'_label"]');
        if(label.length){
            label[0].id = (this.xfControlId+"-label");
            dijit.setWaiState(this.focusNode, "labelledby", label[0].id);
        }
        dijit.setWaiState(this.focusNode, "valuemin", this.minimum);
        dijit.setWaiState(this.focusNode, "valuemax", this.maximum);

    },
    postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },

    postCreate:function() {
        this.inherited(arguments);
        this.setCurrentValue();
        if(this.tabIndex != undefined){
            dojo.attr(this.domNode, "tabindex", this.tabIndex);
        }
        dojo.connect(this.domNode, "onkeypress", this, "_onKeyPress");


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
        // console.debug("Rating._onBlur");
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
        this.setAttribute("value",value);
    },

    _onKeyPress: function(/*Event*/ e){
        if(this.disabled || this.readOnly || e.altKey || e.ctrlKey || e.metaKey){ return; }
        switch(e.charOrCode){
            case dojo.keys.HOME:
                this._handleSetControlValue(this.minimum);
                if(this.incremental){
                    this.setControlValue();
                }

                break;
            case dojo.keys.END:
                this._handleSetControlValue(this.maximum);
                if(this.incremental){
                    this.setControlValue();
                }
                break;
            case (dojo.keys.RIGHT_ARROW):
            case (dojo.keys.UP_ARROW):
            case (dojo.keys.PAGE_UP):
                // increment value:
                // console.debug("decrement: maximum:", this.maximum," value:",this.get('value'));
                var value2increment = Number(this.get('value'));
                if(value2increment < this.maximum){
                    this._handleSetControlValue((value2increment+1));
                    if(this.incremental){
                        this.setControlValue();
                    }
                }
                break;
            case (dojo.keys.LEFT_ARROW):
            case (dojo.keys.DOWN_ARROW):
            case (dojo.keys.PAGE_DOWN):
                // this.decrement(e);
                // console.debug("decrement: minimum:", this.minimum," value:",this.get('value'));
                var value2decrement = Number(this.get('value'));
                if(value2decrement > this.minimum){
                    this._handleSetControlValue((value2decrement-1));
                    if(this.incremental){
                        this.setControlValue();
                    }
                }
                break;
            default:
                return;
        }
        dojo.stopEvent(e);
    }


});

