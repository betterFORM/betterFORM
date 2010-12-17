/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.select.MultiSelect");

dojo.require("dijit.form.FilteringSelect");

dojo.require("dijit.form.MultiSelect");


dojo.declare(
        "betterform.ui.select.MultiSelect",
        [betterform.ui.ControlValue,dijit.form.MultiSelect],
{
    size:"",
    value:"",
    multiple:"",
    freeTextDijit:null,
    freeTextValue:"",
    openSelection:false,
    isServerUpdate:false,

    postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
        if(dojo.attr(this.srcNodeRef, "incremental") == undefined || dojo.attr(this.srcNodeRef, "incremental") == "" || dojo.attr(this.srcNodeRef, "incremental") == "true"){
            this.incremental = true;
        }else {
            this.incremental = false;
        }
        if( dojo.attr(this.srcNodeRef, "selection") != undefined  && dojo.attr(this.srcNodeRef, "selection") == "open") {
            this.openSelection = true;
        }
       //  console.debug("MultiSelect.postMixInProperties openSelection = ",this.openSelection);
        
        this.xfControl = dijit.byId(this.xfControlId);
        if (this.size == "" || this.size < 1) {
            this.size = 5;
        }

    },
    postCreate:function() {
        this.inherited(arguments);
        this.setCurrentValue();
        if(this.openSelection) {
            dojo.addClass(this.xfControl.domNode, "xfSelectOpen");
            var freeTextNodeWrapper = dojo.doc.createElement("div");           
            dojo.addClass(freeTextNodeWrapper,"xfSelectFreeText");
            var freeTextNode = dojo.doc.createElement("div");
            dojo.place(freeTextNode,freeTextNodeWrapper);
            dojo.place(this.domNode,freeTextNodeWrapper);

            dojo.place(freeTextNodeWrapper,this.xfControl.domNode,1);
            this.freeTextDijit = new dijit.form.TextBox({},freeTextNode);
            dojo.connect(this.freeTextDijit, "_handleOnChange", this,"_textFieldValueChanged");
        }
    },

    _textFieldValueChanged:function(/*anything*/ newValue, /*Boolean, optional*/ priorityChange) {
        // console.debug("MulitSelect._textFieldValueChanged newValue: ", newValue);
        if(this.isServerUpdate) {
            return;
        }else {
            this.freeTextValue = newValue;
            this.setControlValue();
        }

    },
    
     _onFocus:function() {
        // console.debug("MultiSelect._onFocus() this: ",this);
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function() {
        // console.debug("MultiSelect._onBlur() this: ",this);
        this.inherited(arguments);
        this.handleOnBlur();
    },

    applyState:function() {
        if (this.xfControl.isReadonly()) {
            this.attr('disabled', true);
            if(this.openSelection) {
                dojo.attr(this.freeTextDijit.domNode,"disabled",true);
            }
        } else {
            this.attr('disabled', false);
            if(this.openSelection) {
                dojo.attr(this.freeTextDijit.domNode,"disabled",false);
            }
            }
    },

    getControlValue:function() {
        // console.debug("MultiSelect.getControlValue() this.getValue():",this.getValue());
        var returnvalue = this.attr('value').join(" ");
        if(this.openSelection) {
            if(this.freeTextValue == "" && returnvalue == undefined){
                returnvalue = "";
            }
            else if(this.freeTextValue != "" && returnvalue != undefined && returnvalue != ''){
                returnvalue = returnvalue + " " + this.freeTextValue;
            }else if(this.freeTextValue != ""){
                returnvalue = this.freeTextValue;
            }
        }
        // console.debug("MultiSelect.getControlValue  value: ", returnvalue );
        return returnvalue;

    },

    onChange: function(evt) {
        this.inherited(arguments);
        var selectedItems = this.getSelected();
        // console.debug("MultiSelect.onChange SelectedItems: ", selectedItems);
        var ids = undefined;
        dojo.forEach(selectedItems, function(item,index,array) {
                var selectedId = dojo.attr(item,"id");
                if(ids == undefined) {
                    ids = selectedId;
                }else {
                    ids = ids + ";" + selectedId;
                }
        });
        // console.debug("MultiSelect.onChange SelectedItem Ids: ", ids);
        if(ids == undefined) {ids = ""}
        fluxProcessor.dispatchEventType(this.xfControl.id, "DOMActivate", ids);
        if (this.incremental) {
            this.setControlValue();
        }
    },

    _handleSetControlValue:function(values) {
        // console.debug("MultiSelect._handleSetControlValue values: ", values);
        var valueArray = new Array();
        valueArray = values.split(' ');
        if(this.openSelection) {
            var selectOptions = dojo.query(".xfSelectorItem", this.domNode);
            var textFieldValue = "";
            dojo.forEach(valueArray,
                function(valueEntry, index, array) {
                    // console.debug("MulitSelect.handleSetControlValue Iterate Value index:",index, " array: ",array, " entry: ", valueEntry);
                    var isOptionValue = false;
                    dojo.forEach(selectOptions,
                            function(selectEntry, indexSelect, arraySelect) {
                                // console.debug("MulitSelect.handleSetControlValue Iterate Selections index:",indexSelect, " array: ",arraySelect, " entry: ", selectEntry , " value: ", valueEntry);
                                if(dojo.attr(selectEntry,"value")== valueEntry) {  isOptionValue = true; }
                            }
                    );
                    if(!isOptionValue) {
                        if(textFieldValue == undefined) {
                            textFieldValue = valueEntry;
                        }else {
                            textFieldValue = textFieldValue + " " + valueEntry;
                        }
                    }
                }
            );
            this.freeTextValue = textFieldValue;
            // prevent resending the new value to the server
            // be aware to keep the following three lines together
            this.isServerUpdate = true;
            this.freeTextDijit._setValueAttr(textFieldValue);
            this.isServerUpdate = false;

        }

        // console.debug("\n\"MulitSelect.handleSetControlValue testFieldValue: ", textFieldValue, "\n\n");
        this._setValueAttr(valueArray);
    }
});
