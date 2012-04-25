/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/_base/declare", "dijit/_Widget","bf/XFBinding","dojo/dom", "dojo/dom-class","dojo/query",
        "dojo/dom-attr","dojo/_base/connect","dojo/dom-construct","dijit/registry","dojo/behavior","bf/util"],
    function(declare, _Widget, XFBinding, dom, domClass,query,domAttr,connect,domConstruct,registry,behavior){
        return declare([_Widget,XFBinding], {


    /**
     * All Rights Reserved.
     * @author Joern Turner
     *
     * XFControl represents a XForms control on the client. Instances of XFControl maintain the  MIP states,
     * the xforms appearance and some other information as CSS classes and acts as a proxy between concrete
     * controls and XForms controls. It always wraps a widget which is the concrete control the user interacts
     * with to input or change a value.
     *
     * An example DOM structure at runtime looks like this:
     * <span id="original id of XForms control on the server" class="xfControl (...)">
     *     <input type="text" class="xfValue".../>
     * </span>
     *
     * A XFControl always gets the id of the original XForms control. This id is used in all events to connect
     * the processor to the concrete UI widget used in the device (browser).
     *
     **/
        currentValue:null,
        incremental:false,

        /**
         * function to update the currentValue of XFControl (important
         * @param value
         */
        setCurrentValue:function(value){
            // console.debug("XFControl.setCurrentValue value:", value, " currentValue:",this.currentValue);
            this.currentValue = value;

        },

        postCreate:function() {
            if(this.isIncremental()){
                this.incremental = true;
            }
        },

        /*
         sends an updated value of a widget to the server
         */
        sendValue:function(/* String */ value, evt) {
            // console.debug("XFControl.sendValue: currentvalue:", this.currentValue, " - newValue:",value);
            if(this.isReadonly()){
                // console.debug("XFControl sendValue - control is readonly - ignoring event");
                return;
            }

            if(evt && evt.type == "blur"){
                // control has lost focus
                this.bfFocus = false;
            }

            if (value != undefined && this.currentValue != value) {
                //do not send update to server if in mode 'incremental' as value already has been passed
                // console.debug("XFControl: sendValue: evt.type:", evt ? evt.type:undefined, " - this.incremental:",this.incremental);
                if( evt == undefined || (!this.incremental && evt.type == "blur") || (this.incremental && evt.type == "keyup") || (this.incremental && evt.type == "change") || (this.incremental && evt.type == "click")){
                    //update internal value
                    this.currentValue = value;
                    //handle validity and dispatch events if necessary
                    if(this.isValid()){
                        connect.publish("xforms-valid",[this.id,"onBlur"]);
                    }else {
                        connect.publish("xforms-invalid",[this.id,"onBlur"]);
                    }
                    fluxProcessor.sendValue(this.id, value);
                }
                this._handleRequiredEmpty();
            }

            if(evt && evt.type == "blur"){
                //notify server of lost focus
                fluxProcessor.dispatchEventType(this.id,"DOMFocusOut");
            }

        },

        /*
        handles state changes (value and MIP changes) send by the server and applies them to the control. State
        changes are received from the client side xforms processor (XFProcessor) which handles all communication
        between client and server.
        Extends XFBinding.handleStateChanged
         */
        handleStateChanged:function(contextInfo) {
            this.inherited(arguments);
            if (this.value != null) {
                console.debug("XFControl.handleStateChange this.value:",this.value);
                this.currentValue = this.value;
                this.setValue(this.value, contextInfo["schemaValue"]);
            }

        },

        isIncremental:function(){
            return domClass.contains(this.domNode, "xfIncremental");
        },

        /*
         fetches the value from the widget
         */
        getControlValue:function() {
            if(this.currentValue != undefined){
                return this.currentValue;
            }
        },


        // TODO: Lars: implement new (if needed), controlValue does not exist anymore
        _checkForDataTypeChange:function(dataType) {
            // console.debug("_checkForDataTypeChange: old dataType: " + this.dataType + " new dataType: ", dataType, " contextInfo:",contextInfo);

            if (this.controlValue == undefined) {
                var controlValueTemplate = query("*[id ='" + this.id + "-value']", this.domNode)[0];
                if (controlValueTemplate == undefined) {
                    controlValueTemplate = query(".xfValue", this.domNode)[0];
                }
                if (controlValueTemplate == undefined) {
                    console.error("Control._checkForDataTypeChange Error: XFControl " + this.id + " has no ControlValue node");
                    return;
                }
                else {
                    domAttr.set(controlValueTemplate, "dataType", dataType);
                    domAttr.set(controlValueTemplate, "id", this.id + "-value");
    //                this.controlValue = fluxProcessor.factory.createWidget(controlValueTemplate, this.id);

                }

            } else if (this.dataType != dataType && !(this.dataType == "" && dataType == "string")) {
                // console.debug("datatype for existing dijit changed this.dataType: " , this.dataType + "  dataType: ", dataType);

                var controlValueNode = document.createElement("span");
                domAttr.set(controlValueNode, "dataType", dataType);
                domAttr.set(controlValueNode, "controlType", this.controlType);
                domAttr.set(controlValueNode, "id", this.id + "-value");

                domClass.add(controlValueNode, "xfValue");
                var formerTypeClass = "xsd" + this.dataType.replace(/^[a-z]/, this.dataType.substring(0, 1).toUpperCase());
                if (domClass.contains(this.domNode, formerTypeClass)) {
                    // console.debug("remove CSS Type " + formerTypeClass);
                    domClass.remove(this.domNode, formerTypeClass);
                }
                domClass.add(this.domNode, "xsd" + dataType.replace(/^[a-z]/, dataType.substring(0, 1).toUpperCase()));

                this.controlValue.destroy();
                this.controlValue = fluxProcessor.factory.createWidget(controlValueNode, this.id);
                domConstruct.place(this.controlValue.domNode, this.domNode);
            }
            this.dataType = dataType;
        },

            /**
             * Function to handle MIP properties / value and Label on parent
             * Extends XFBinding._handleHelperChanged
             * @param properties
             */
        _handleHelperChanged: function(properties) {
            // console.debug("Control.handleHelperChanged: this.id: "+this.id+ "type='" + properties["targetName"] + "',  value='" + properties["value"] + "'");
            this.inherited(arguments);
            if(properties["targetName"] == "value") {
                this.setValue(properties["value"]);
            }
        },

        /**
         * "Abstract" API Function to update the value of the corresponding Widget
         * @param value localized value
         * @param schemavalue optional / original schema value (not localized)
         */
        setValue:function(value, schemavalue) {
            console.error("XFControl.setValue must be overwritten by it's accoring ControlValue Widget [id:", this.id + "-value]");
        },

        _handleRequiredEmpty:function(){
            if (domClass.contains(this.domNode, "xfRequiredEmpty")) {
                domClass.remove(this.domNode, "xfRequiredEmpty");
            }
        },

        getWidget:function() {
            if(this.widget == undefined) {
                this.widget = dom.byId(this.id+"-value");
            }
            return this.widget;
        },

        handleOnFocus:function() {
            //storing current control id for handling help
            // console.debug("ControlValue.handleOnFocus storing current control id:", this.id, " value: ",this.currentValue);

            fluxProcessor.currentControlId = this.id;

            if (!this.bfFocus && fluxProcessor.usesDOMFocusIN) {
                // console.debug("ControlValue: dispatch DOMFocusIn to ",this.xfControl.id);
                fluxProcessor.dispatchEventType(this.id,"DOMFocusIn");
            }
            this.bfFocus = true;
            if(this.isValid()){
                dojo.publish("xforms-valid",[this.id,"onFocus"]);
            }else {
                dojo.publish("xforms-invalid",[this.id,"onFocus"]);
            }
        }

    });
});


