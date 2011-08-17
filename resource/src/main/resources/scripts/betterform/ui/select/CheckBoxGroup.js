/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.select.CheckBoxGroup");

dojo.require("dijit._Widget");

dojo.require("betterform.ui.select.CheckBox");

/**
 All Rights Reserved.
 @author Joern Turner
 @author Lars Windauer

 CheckBoxGroup "Value Diit" represents a Select with appearance="full"
 Controller class for checkboxes (betterform.util.select.CheckBox) within Select Control

 **/

dojo.declare(
        "betterform.ui.select.CheckBoxGroup",
        [betterform.ui.ControlValue],
{
    widgetsInTemplate:true,

    buildRendering:function() {
        // console.debug("CheckBoxGroup.buildRendering");
        this.domNode = this.srcNodeRef;
    },

    postMixInProperties:function() {
        // console.debug("CheckBoxGroup.postMixInProperties this.xfControlId:",this.xfControlId);
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
        var incrementalValue = dojo.attr(this.srcNodeRef, "incremental");
        // set incremental to true if not explicitly set to false
        this.incremental =  incrementalValue || incrementalValue == undefined || incrementalValue == "" || incrementalValue == "true" ;
        var checkBoxChilds = dojo.query(".xfSelectorItem .xfCheckBoxValue",this.srcNodeRef);
        dojo.forEach(checkBoxChilds, function(item,index,array){
            var itemId = dojo.attr(item,"id");
            fluxProcessor.factory.createWidget(item, itemId.split("-value")[0]);
        })
        
    },

    _onFocus:function() {
       //console.debug("CheckBoxGroup._onFocus()");
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function() {
        // console.debug("CheckBoxGroup._onBlur()");
    	this.inherited(arguments);
	    this.handleOnBlur();
    },

    getControlValue:function() {
        // console.debug("CheckBoxGroup.getControlValue");
        var tmpValue = "";
        var checkBoxList = dojo.query(".dijitCheckBoxChecked .dijitCheckBoxInput", this.domNode);
        for(var index = checkBoxList.length-1; index >=0 ; index--){
            var checkBoxNode = checkBoxList[index];
            var checkBoxDijit = dijit.byId(dojo.attr(checkBoxNode,"id"));
            tmpValue = checkBoxDijit.getControlValue() + " " + tmpValue;
        }
        // console.debug("\ntmpValue: ",tmpValue, "\n");
        return tmpValue.replace(/\s+$/g, "");
    },

    _handleSetControlValue:function(values) {
        // console.debug("CheckBoxGroup._handleSetControlValue values: ", values);
        var valueArray = new Array();
        valueArray = values.split(' ');
        // console.debug("dojo.query('.dijitCheckBox', this.domNode):",  dojo.query(".dijitCheckBoxInput", this.domNode));
        dojo.query(".dijitCheckBoxInput", this.domNode).forEach(
            function(entry) {
				//console.debug("CheckBoxGroup._handleSetControlValue entry: ", entry, " entry.value", entry.value , " values: ", valueArray, " indexOf: ", dojo.indexOf(valueArray, dijit.byId(entry.id).currentValue));
                if (dojo.indexOf(valueArray, dijit.byId(entry.id).currentValue) != -1) {
                    dijit.byId(entry.id).set("checked", true);
                } else {
                    dijit.byId(entry.id).set("checked", false);
                }
            }
        );
    },
    /* update processor after value change by UI */
    _setCheckBoxGroupValue: function() {
        // console.debug("CheckBoxGroup._setCheckBoxGroupValue");
        var selectedItems = dojo.query(".dijitCheckBoxChecked .dijitCheckBoxInput", this.domNode);
        var ids = undefined;
        dojo.forEach(selectedItems, function(item, index, array) {
            var id = dojo.attr(item, "id");
            id = id.substring(0,id.length-6);
            // console.debug("calculating existing ids: " + ids, " new id: ",id);
            if(ids == undefined) {
                ids = id;
            }else {
                ids = ids + ";" + id;
            }
        });
        if(ids == undefined) {
            ids = "";
        }
        // console.debug("CheckBoxGroup._setCheckBoxGroupValue selectedItems: ", ids);
        fluxProcessor.dispatchEventType(this.xfControl.id, "DOMActivate", ids);

        if (this.incremental) {
            this.xfControl.setControlValue(this.getControlValue());
        }
    },

    /* update UI after value change by processor */

    applyState:function() {
        if (this.xfControl.isReadonly()) {
            this.setDisabled(true);
        } else {
            this.setDisabled(false);
        }
    },


    /* disable this Select */

    setDisabled:function(/*Boolean*/ disabled) {
        // console.debug("CheckBoxGroup.setDisabled disable control: " + this.id + ": " + disabled);        
        dojo.forEach(dojo.query(".xfSelectorItem", this.domNode),
            function(entry) {
                var selectorId  =dojo.attr(entry, "id");
                var checkBoxChild = dijit.byId(selectorId +"-value");
                if(checkBoxChild != undefined) {
                    checkBoxChild.set('disabled',disabled);
                } else {
                    // initially the CheckBox child is not initialized and the DOM is manipulated directly
                    checkBoxChild = dojo.byId(selectorId +"-value");
                    if(disabled){ dojo.attr(checkBoxChild, "disabled",disabled); }
                    else        { dojo.removeAttr(checkBoxChild, "disabled"); }
                }
            }
        );
    }
});


