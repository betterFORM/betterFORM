/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.select.CheckBoxGroup");

dojo.require("dijit._Widget");
dojo.require("betterform.ui.ControlValue");
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
        betterform.ui.ControlValue,
{

    buildRendering:function() {
        // console.debug("CheckBoxGroup.buildRendering");
        this.domNode = this.srcNodeRef;
    },

    postMixInProperties:function() {
        // console.debug("CheckBoxGroup.postMixInProperties");
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
        if(dojo.attr(this.srcNodeRef, "incremental") == undefined || dojo.attr(this.srcNodeRef, "incremental") == "" || dojo.attr(this.srcNodeRef, "incremental") == "true" ){
            this.incremental = true;
        }else {
            this.incremental = false;
        }
    },

    postCreate:function() {
        // console.debug("CheckBoxGroup.postCreate");
        this.inherited(arguments);
        var tmpValue = "";
        dojo.query("*[checked]", this.domNode).forEach(
            function(entry) {
                tmpValue += entry.value + " ";
            }
        );


        if(tmpValue != ''){
            tmpValue = tmpValue.replace(/\s+$/g,"");
        }        
        this.setCurrentValue(tmpValue);
    },


    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function() {
        this.inherited(arguments);
        if (!this.incremental) {
              this.handleOnBlur();
        }
    },

    getControlValue:function() {
        // console.debug("CheckBoxGroup.getControlValue");
        var tmpValue = "";
        dojo.query(".dijitCheckBoxChecked .dijitCheckBoxInput", this.domNode).forEach(
            function(entry) {
                tmpValue = dijit.byId(dojo.attr(entry,"id")).getControlValue() + " " + tmpValue;
            }
        );
        return tmpValue.replace(/\s+$/g, "");
    },

    _handleSetControlValue:function(values) {
        // console.debug("CheckBoxGroup._handleSetControlValue values: ", values);
        var valueArray = new Array();
        valueArray = values.split(' ');

        dojo.query(".dijitCheckBoxInput", this.domNode).forEach(
            function(entry) {
				//console.debug("CheckBoxGroup._handleSetControlValue entry: ", entry, " entry.value", entry.value , " values: ", valueArray, " indexOf: ", dojo.indexOf(valueArray, dijit.byId(entry.id).currentValue));
                if (dojo.indexOf(valueArray, dijit.byId(entry.id).currentValue) != -1) {
                    dijit.byId(entry.id).setChecked(true);
                } else {
                    dijit.byId(entry.id).setChecked(false);
                }
            }
        );
    },
    /* update processor after value change by UI */
    _setCheckBoxGroupValue: function() {
        var selectedItems = dojo.query(".dijitCheckBoxChecked .dijitCheckBoxInput", this.domNode);
        var ids = undefined;
        dojo.forEach(selectedItems, function(item, index, array) {
            var id = dojo.attr(item, "id");
            id = id.substring(0,id.length-6);
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
        dojo.forEach(dojo.query(".dijitCheckBoxInput", this.domNode),
            function(entry) {
                var entryId = dojo.attr(entry, "id");
                var tmpDijit = dijit.byId(entryId);
                if(tmpDijit != undefined) {
                    tmpDijit.attr('disabled',disabled);
                } else {
                    tmpDijit = new betterform.ui.select.CheckBox({},entryId);
                    tmpDijit.attr('disabled',disabled);
                }
            }
        );
    }
});


