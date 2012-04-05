/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/_base/declare", "dijit/_Widget","dojo/dom", "dojo/dom-class","dojo/query",
        "dojo/dom-attr","dojo/_base/connect","dojo/dom-construct","dijit/registry","dojo/behavior","bf/util"],
    function(declare, _Widget, dom, domClass,query,domAttr,connect,domConstruct,registry,behavior){
        return declare(_Widget, {


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

        id:"",
        controlType:"",
        controlValue:null,
        currentValue:null,
        bfFocus:false,
        incremental:false,


        constructor:function() {
        },

        buildRendering: function() {
            this.domNode = this.srcNodeRef;
        },

        postCreate:function() {
            // console.debug("\nControl.postCreate; DOM Node:",this.domNode,"\n");
            // TODO: examine if this can be done in handleStateChanged
            // ensure all needed classes for Control are in place in case we have a dynamically created control
            bf.util.setDefaultClasses(this.domNode);

            if(this.isIncremental()){
                this.incremental = true;
            }

            /*
           Controls publish their validity state to the processor which will pass it to the selected alertHandler
            */
            if (this.isValid()) {
                connect.publish("xforms-valid", [this.id,"init"]);
            } else {
                connect.publish("xforms-invalid", [this.id,"init"]);
            }
            connect.subscribe("bf-state-change-"+ this.id, this, "handleStateChanged");

        },

        /*
         sends an updated value of a widget to the server
         */
        sendValue:function(/* String */ value, evt) {
            // console.debug("XFControl: sendValue: currentvalue:", this.currentValue, " - newValue:",value);
            if(this.isReadonly()){
                console.debug("XFControl sendValue - control is readonly - ignoring event");
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
         */
        handleStateChanged:function(contextInfo) {
            console.debug("XFControl.handleStateChanged: ",contextInfo);

            if (contextInfo["parentId"]) {
                // console.debug("Control._handleHelperChanged: ",contextInfo);
                this._handleHelperChanged(contextInfo);
            } else {
                this.value = contextInfo["value"];
                this.valid = contextInfo["valid"];
                this.readonly = contextInfo["readonly"];
                this.required = contextInfo["required"];
                this.relevant = contextInfo["enabled"];
                console.debug("Control.handleStateChanged value:",this.value," valid:", this.valid, " readonly:",this.readonly," required:",this.required, " relevant:",this.relevant, " targetName:",contextInfo["targetName"]," type:",contextInfo["type"], " contextInfo:",contextInfo);

                // check xsd type and adjust if needed
                if(domClass.contains(this.domNode, "bfPrototype")){
                    // console.warn("XFControl.handleStateChange widget not initialized yet");
                    domClass.remove(this.domNode, "bfPrototype");
                    var type = contextInfo["type"];
                    var xsdType = "xsd" + type.replace(/^[a-z]/, type.substring(0, 1).toUpperCase());
                    // console.debug("apply new type: ",xsdType, " to Control Widget");
                    domClass.add(this.domNode, xsdType);
                    behavior.apply();

                }

            // Set value handling
                if (this.value != null) {
                    this.currentValue = value;
                    this.setValue(this.value, contextInfo["schemaValue"]);
                }

                // Validity handling
                if(this.valid != undefined){
                    if (this.valid == "true") {
                        this.setValid();
                    }else if(!domClass.contains(this.domNode,"bfInvalidControl")){
                        /*
                         todo: got the feeling that this case should be handled elsewhere....
                         if a control is intially invalid it just has xfInvalid but not bfInvalidControl. This may happen
                         during init and somehow the subscriber won't be called then (too early???)

                         Ok, for now: if control is not valid (has 'xfInvalid' class) and not has 'bfInvalidControl' (which
                         actually shows an alert) it must nevertheless publish invalid event for the alerts to work correctly.
                         */
                        this.setInvalid();
                    }
                }
                if(this.readonly != undefined) {
                    if (this.readonly == "true") {
                        this.setReadonly();
                    }else {
                        this.setReadwrite();
                    }
                }

                if(this.required != undefined) {
                    if (this.required == "true") {
                        this.setRequired();
                    }else {
                        this.setOptional();
                    }
                }
                if(this.relevant != undefined) {
                    if (this.relevant == "true") {
                        this.setEnabled();
                    }else {
                        this.setDisabled();
                    }
                }

            }
        },

        isRequired:function() {
            // console.debug("Control.isRequired",this.domNode);
            if (domClass.contains(this.domNode, "xfOptional")) {
                return false;
            } else if (domClass.contains(this.domNode, "xfRequired")) {
                return true;
            } else {
                console.error("No required state found")
            }
        },

        isReadonly:function() {
            // console.debug("Control.isReadonly",this.domNode);
            if (domClass.contains(this.domNode, "xfReadWrite")) {
                return false;
            } else if (domClass.contains(this.domNode, "xfReadOnly")) {
                return true;
            } else {
                console.error("No readonly state found")
            }
        },

        isRelevant:function() {
            //console.debug("Control.isRelevant",this.domNode);
            if (domClass.contains(this.domNode, "xfDisabled")) {
                return false;
            } else if (domClass.contains(this.domNode, "xfEnabled")) {
                return true;
            } else {
                console.error("No relevant state found")
            }
        },

        isValid:function() {
            // console.debug("XFControl.isValid",this.domNode);

            if (domClass.contains(this.domNode, "xfInvalid")) {
                return false;
            } else if (domClass.contains(this.domNode, "xfValid")) {
                return true;
            } else {
                console.error("No validate state found for " + this.id);
            }
        },
        isIncremental:function(){
            return domClass.contains(this.domNode, "xfIncremental");
        },

        /*
         fetches the value from the widget
         */
        getControlValue:function() {
    //        if (this.controlValue != undefined) {
    //            return this.controlValue.getControlValue();
    //        }
            if(this.currentValue != undefined){
                return this.currentValue;
            }
        },



        _checkForDataTypeChange:function(dataType) {
            // console.debug("_checkForDataTypeChange: old dataType: " + this.dataType + " new dataType: ", dataType, " contextInfo:",contextInfo);

            if (this.controlValue == undefined) {
                var controlValueTemplate = query("*[id ='" + this.id + "-value']", this.domNode)[0];
                if (controlValueTemplate == undefined) {
                    controlValueTemplate = query(".xfValue", this.domNode)[0];
                }
                if (controlValueTemplate == undefined) {
                    console.error("Control.handleStateChanged Error: XFControl " + this.id + " has no ControlValue node");
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
         * "Abstract" API Function to update the value of the corresponding Widget
         * @param value localized value
         * @param schemavalue optional / original schema value (not localized)
         */
        setValue:function(value, schemavalue) {
            console.error("XFControl.setValue must be overwritten by it's accoring ControlValue Widget [id:", this.id + "-value]");
        },

        setValid:function() {
            bf.util.replaceClass(this.domNode, "xfInvalid", "xfValid");
            connect.publish("xforms-valid", [this.id,"applyChanges"]);

        },

        setInvalid:function() {
            bf.util.replaceClass(this.domNode, "xfValid", "xfInvalid");
            connect.publish("xforms-invalid", [this.id,"applyChanges"]);
        },

        setReadonly:function() {
            bf.util.replaceClass(this.domNode, "xfReadWrite", "xfReadOnly");
            domAttr.set(this.getWidget(), "readonly","readonly");
        },

        setReadwrite:function() {
            bf.util.replaceClass(this.domNode,"xfReadOnly","xfReadWrite");
            this.getWidget().removeAttribute("readonly");
        },

        setRequired:function() {
            bf.util.replaceClass(this.domNode, "xfOptional", "xfRequired");
        },

        setOptional:function() {
            bf.util.replaceClass(this.domNode, "xfRequired", "xfOptional");
        },

        setEnabled:function() {
            var label = dom.byId(this.id + "-label");
            if (label != undefined) {
                if (domClass.contains(label, "xfDisabled")) {
                    bf.util.replaceClass(label, "xfDisabled", "xfEnabled");
                } else {
                    domClass.add(label, "xfEnabled");
                }
            }
            bf.util.replaceClass(this.domNode, "xfDisabled", "xfEnabled");

            if (this.isValid()) {
                connect.publish("xforms-valid", [this.id, "xfDisabled"]);
            } else {
                connect.publish("xforms-invalid", [this.id, "xfDisabled"]);
            }
        },

        setDisabled:function() {
            var label = dom.byId(this.id + "-label");
            if (label != undefined) {
                if (domClass.contains(label, "xfEnabled")) {
                    bf.util.replaceClass(label, "xfEnabled", "xfDisabled");
                } else {
                    domClass.add(label, "xfDisabled");
                }
            }
            bf.util.replaceClass(this.domNode, "xfEnabled", "xfDisabled");
            if (this.isValid()) {
                connect.publish("xforms-valid", [this.id, "xfDisabled"]);
            } else {
                connect.publish("xforms-invalid", [this.id, "xfDisabled"]);
            }
        },

        _handleSetValidProperty:function(validity) {
            console.warn("XFControl._handleSetValidProperty was removed, use setValid / setInvalid instead");

        },
        _handleSetReadonlyProperty: function(/*Boolean*/ readonly) {
            console.warn("XFControl._handleSetReadonlyProperty was removed, use setReadonly/ setReadwrite instead");

        },

        _handleSetRequiredProperty:function() {
            console.warn("XFControl._handleSetRequiredProperty was removed, use setRequired/ setOptional instead");
        },

        _handleSetEnabledProperty:function(enabled) {
            console.warn("XFControl._handleSetEnabledProperty was removed, use setEnabled/ setDisabled instead");
        },

        _handleHelperChanged: function(properties) {
           console.debug("Control.handleHelperChanged: this.id: "+this.id+ "type='" + properties["targetName"] + "',  value='" + properties["value"] + "'");
            switch (properties["targetName"]) {
                case "label":
                    this.setLabel(properties["value"]);
                    return;
                case "help":
                    this.setHelp(properties["value"]);
                    return;
                case "hint":
                    this.setHint(properties["value"]);
                    return;
                case "alert":
                    this.setAlert(properties["value"]);
                    return;
                case "value":
                    this.setValue(properties["value"]);
                    return;

            }
        },

        setLabel:function(value) {
             console.debug("Control.setLabel value:"+ value);

            var labelNode = dom.byId(this.id + "-label");
            if (labelNode != undefined) {
                labelNode.innerHTML = value;
            }
            else {
                console.warn("Failure updating help for Control '" + this.id + "-help' with value: " + value);
            }
        },
        setHelp:function(value) {
            // console.warn("TBD: Control.setHelp value:"+ value);
            var helpNode = dom.byId(this.id + "-help");
            if (helpNode != undefined) {
                helpNode.innerHTML = value;
            }
            else {
                console.warn("Failure updating help for Control '" + this.id + "-help' with value: " + value);
            }
        },

        setHint:function(value) {
            // Container for storing the hint-node if it exists
            var hintNode = dom.byId(this.id + "-hint");
            // Container for storing the node which contains a title attribute
            var valueNode = registry.byId(this.id + "-value");

            // Value for: Is a title-Attribute availabel at the current DOM-structure
            var titleAttributeFound = false;

            // Value for: Is a hint Node available at the current DOM-structure
            var hintNodeFound;
            // Check if a hint-node is available and store that information
            hintNodeFound = hintNode != undefined;

            // Check if a title-attribute is available and store that information
            if (valueNode != undefined) {
                try {
                    // Try to retrieve the title attribute of the according value-node
                    var titleAttribute = valueNode.attr("title");
                    // Test if the retrieved title-attribute is defined and has a non-empty value
                    titleAttributeFound = titleAttribute != undefined && titleAttribute != "";
                }
                catch(exception) {
                    console.debug("title attribute for hint " +  this.id + "-hint" + " is empty");
                }
            }

            // If a hint-node was found
            if (hintNodeFound) {
                // Only update the hint-node's content
                hintNode.innerHTML = value;
            }

            // If a title-attribute was found
            if (titleAttributeFound) {
                // Update the title-attribute
                valueNode.attr("title", value);
            }

            // If no hint-node was found and no title-attribute was found
            if (!hintNodeFound && !titleAttributeFound) {
                // Print an error to the console
                console.warn("Failure updating hint for Control '" + this.id + " with value: " + value + " ... neither found '" + this.id + "-hint' nor '" + this.id + "-value");
            }
        },

        setAlert:function(value) {
            var alertNode = dom.byId(this.id + "-alert");
            if (alertNode != undefined) {
                alertNode.innerHTML = value;
            }
            else {
                console.error("Failure updating alert for Control '" + this.id + "-alert' with value: " + value);
            }

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
        }
    });
});


