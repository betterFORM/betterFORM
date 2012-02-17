/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.textarea.HtmlEditor");


dojo.require("dijit.Editor");

dojo.declare(
        "betterform.ui.textarea.HtmlEditor",
        [betterform.ui.ControlValue, dijit.Editor],
{

   height:"100%;",

   buildRendering:function() {
        this.domNode = this.srcNodeRef;
        this.setCurrentValue(this.srcNodeRef.innerHTML);
        this._attachTemplateNodes(this.domNode);

   },
   postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
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
        var tmpValue = this.getValue(true);
        // remove unwanted markup inserted by the dojo editor implementation
        tmpValue = dojo.trim(tmpValue);
        tmpValue = tmpValue.replace("<br _moz_editor_bogus_node=\"TRUE\" />", "");
        return tmpValue;
    },


    _handleSetControlValue:function(value) {
        // console.debug("betterform.ui.textarea.SimpleTextarea._handleSetControlValue: Value: ", value);
        this.setValue(value);
    },

   applyState:function() {
        // console.debug("HTMLEditor.applyState (id:" + this.id +") isReadonly1: ",this.xfControl.isReadonly());
        this.set("readOnly",this.xfControl.isReadonly());
        this.set("disabled",this.xfControl.isReadonly());
    }
});


