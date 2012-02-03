/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.xf.XFControl");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");


/**
 * All Rights Reserved.
 * @author Joern Turner
 *
 *
 * Control is a generic wrapper for all XForms elements that have MiPs to maintain. One Component at runtime is a
 * one-to-one match to an XForms control in the XFormsProcessor. It has the same id as the original XForms control
 * and acts as a client-side proxy for it.
 **/

dojo.declare(
        "betterform.xf.XFControl",
        [dijit._Widget, dijit._Templated],
{
    id:"",
    controlType:"",
    controlValue:null,
    currentValue:null,
    subscriber:null,

    constructor:function() {
    },

    buildRendering: function() {
        this.domNode = this.srcNodeRef;
    },

    postCreate:function() {
        console.debug("\nControl.postCreate; DOM Node:",this.domNode,"\n");
        // TODO: examine if this can be done in handleStateChanged
        // ensure all needed classes for Control are in place in case we have a dynamically created control
        betterform.ui.util.setDefaultClasses(this.domNode);

        /*
       Controls publish their validity state to the processor which will pass it to the selected alertHandler
        */
        if (this.isValid()) {
            dojo.publish("/xf/valid", [this.id,"init"]);
        } else {
            dojo.publish("/xf/invalid", [this.id,"init"]);
        }
    },

    isRequired:function() {
        // console.debug("Control.isRequired",this.domNode);
        if (dojo.hasClass(this.domNode, "xfOptional")) {
            return false;
        } else if (dojo.hasClass(this.domNode, "xfRequired")) {
            return true;
        } else {
            console.error("No required state found")
        }
    },

    isReadonly:function() {
        // console.debug("Control.isReadonly",this.domNode);
        if (dojo.hasClass(this.domNode, "xfReadWrite")) {
            return false;
        } else if (dojo.hasClass(this.domNode, "xfReadOnly")) {
            return true;
        } else {
            console.error("No readonly state found")
        }
    },

    isRelevant:function() {
        //console.debug("Control.isRelevant",this.domNode);
        if (dojo.hasClass(this.domNode, "xfDisabled")) {
            return false;
        } else if (dojo.hasClass(this.domNode, "xfEnabled")) {
            return true;
        } else {
            console.error("No relevant state found")
        }
    },

    isValid:function() {
        if (dojo.hasClass(this.domNode, "xfInvalid")) {
            return false;
        } else if (dojo.hasClass(this.domNode, "xfValid")) {
            return true;
        } else {
            console.error("No validate state found for " + this.id);
        }
    },
    isIncremental:function(){
        if (dojo.hasClass(this.domNode,"xfIncremental")){
            return true;
        }else{
            return false;
        }
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

    getCurrentValue:function(){
        return this.currentValue;
    },

    isValueChanged:function(value){
        if (value != undefined && this.currentValue != value) {
            return true;
        }else{
            return false;
        }

    },
    /*
     sends updated value of a widget to the server
     */
    setControlValue:function(/* String */ value) {
        console.debug("XFControl: setControlValue: currentvalue:", this.currentValue, " - newValue:",value);

        if (value != undefined && this.currentValue != value) {
            this.currentValue = value;
            fluxProcessor.setControlValue(this.id, value);
            this._handleRequiredEmpty();
        }
    },


    /*
    handles state changes send by the server and applies them to the control
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
            //console.debug("Control.handleStateChanged value:",this.value," valid:", this.valid, " readonly:",this.readonly," required:",this.required, " relevant:",this.relevant, " contextInfo:",contextInfo);

            if (contextInfo["targetName"] == "input" && this.value != null) {
                var noNSType = betterform.ui.util.removeNamespace(contextInfo["type"]);
                this._checkForDataTypeChange(noNSType, contextInfo);

                if (noNSType == "date" || noNSType == "dateTime") {
                    this._handleSetControlValue(contextInfo["schemaValue"]);
                } else {
                    this._handleSetControlValue(this.value);
                }

            } else if (this.value != null) {
                this._handleSetControlValue(this.value);
            }

            if (this.valid != null ) {
                this._handleSetValidProperty(eval(this.valid));
            }else if(!this.isValid() && !dojo.hasClass(this.domNode,"bfInvalidControl")){
                /*
                 todo: got the feeling that this case should be handled elsewhere....
                 if a control is intially invalid it just has xfInvalid but not bfInvalidControl. This may happen
                 during init and somehow the subscriber won't be called then (too early???)

                 Ok, for now: if control is not valid (has 'xfInvalid' class) and not has 'bfInvalidControl' (which
                 actually shows an alert) it must nevertheless publish invalid event for the alerts to work correctly.
                 */
                this._handleSetValidProperty(false);
            }

            if(!this.isValid() && !dojo.hasClass(this.domNode,"bfInvalidControl")){
                this._handleSetValidProperty(false);
            }
            if (this.readonly != null) {
                this._handleSetReadonlyProperty(eval(this.readonly));
            }
            if (this.required != null) {
                this._handleSetRequiredProperty(eval(this.required));
            }
            if (this.relevant != null) {
                this._handleSetEnabledProperty(eval(this.relevant));
            }

        }
    },


    _checkForDataTypeChange:function(dataType, contextInfo) {
        console.debug("_checkForDataTypeChange: old dataType: " + this.dataType + " new dataType: ", dataType, " contextInfo:",contextInfo);

        if (this.controlValue == undefined) {
            var controlValueTemplate = dojo.query("*[id ='" + this.id + "-value']", this.domNode)[0];
            if (controlValueTemplate == undefined) {
                controlValueTemplate = dojo.query(".xfValue", this.domNode)[0];
            }
            if (controlValueTemplate == undefined) {
                console.error("Control.handleStateChanged Error: XFControl " + this.id + " has no ControlValue node");
                return;
            }
            else {
                dojo.attr(controlValueTemplate, "dataType", dataType);
                dojo.attr(controlValueTemplate, "id", this.id + "-value");
//                this.controlValue = fluxProcessor.factory.createWidget(controlValueTemplate, this.id);

            }

        } else if (this.dataType != dataType && !(this.dataType == "" && dataType == "string")) {
            // console.debug("datatype for existing dijit changed this.dataType: " , this.dataType + "  dataType: ", dataType);

            var controlValueNode = document.createElement("span");
            dojo.attr(controlValueNode, "dataType", dataType);
            dojo.attr(controlValueNode, "controlType", this.controlType);
            dojo.attr(controlValueNode, "id", this.id + "-value");

            dojo.addClass(controlValueNode, "xfValue");
            var formerTypeClass = "xsd" + this.dataType.replace(/^[a-z]/, this.dataType.substring(0, 1).toUpperCase());
            if (dojo.hasClass(this.domNode, formerTypeClass)) {
                // console.debug("remove CSS Type " + formerTypeClass);
                dojo.removeClass(this.domNode, formerTypeClass);
            }
            dojo.addClass(this.domNode, "xsd" + dataType.replace(/^[a-z]/, dataType.substring(0, 1).toUpperCase()));

            this.controlValue.destroy();
            this.controlValue = fluxProcessor.factory.createWidget(controlValueNode, this.id);
            dojo.place(this.controlValue.domNode, this.domNode);
        }
        this.dataType = dataType;
    },

    _handleSetControlValue:function(value) {
        console.debug("handleSetControlValue: " + this.currentValue + " value: " + value);
        if(this.currentValue != value) {
            this.currentValue = value;
            this._handleRequiredEmpty();
        }
        // dojo.publish("/xf/valueChanged",[this,value])
    },

    _handleSetValidProperty:function(validity) {
        console.debug("XFControl._handleSetValidProperty [id:"+this.id+ " valid: ",validity, "]");
        if (validity) {
            betterform.ui.util.replaceClass(this.domNode, "xfInvalid", "xfValid");
            dojo.publish("/xf/valid", [this.id,"applyChanges"]);
        }
        else {
            betterform.ui.util.replaceClass(this.domNode, "xfValid", "xfInvalid");
            dojo.publish("/xf/invalid", [this.id,"applyChanges"]);
        }

    },

    _handleSetReadonlyProperty: function() {
        if (!eval(this.readonly)) {
            betterform.ui.util.replaceClass(this.domNode, "xfReadOnly", "xfReadWrite");
        }
        else {
            betterform.ui.util.replaceClass(this.domNode, "xfReadWrite", "xfReadOnly");
        }
        this.controlValue.applyState();
    },

    _handleSetRequiredProperty:function() {
        if (this.required == "true") {
            betterform.ui.util.replaceClass(this.domNode, "xfOptional", "xfRequired");
        }
        else {
            betterform.ui.util.replaceClass(this.domNode, "xfRequired", "xfOptional");
        }
    },

    _handleSetEnabledProperty:function(enabled) {
        // console.debug("_handleSetEnabledProperty  enabled:",enabled, " domNode: ",this.domNode);
        var targetId = this.id;
        var label = dojo.byId(targetId + "-label");

        if (enabled) {
            if (label != undefined) {
                if (dojo.hasClass(label, "xfDisabled")) {
                    betterform.ui.util.replaceClass(label, "xfDisabled", "xfEnabled");
                } else {
                    dojo.addClass(label, "xfEnabled");
                }
            }

            betterform.ui.util.replaceClass(this.domNode, "xfDisabled", "xfEnabled");
        }
        else {
            if (label != undefined) {
                if (dojo.hasClass(label, "xfEnabled")) {
                    betterform.ui.util.replaceClass(label, "xfEnabled", "xfDisabled");
                } else {
                    dojo.addClass(label, "xfDisabled");
                }
            }

            betterform.ui.util.replaceClass(this.domNode, "xfEnabled", "xfDisabled");
            if (this.isValid()) {
                dojo.publish("/xf/valid", [this.id, "xfDisabled"]);
            } else {
                dojo.publish("/xf/invalid", [this.id, "xfDisabled"]);
            }
        }


    },

    _handleHelperChanged: function(properties) {
        // console.debug("Control.handleHelperChanged: type='" + properties["type"] + "',  value='" + properties["value"] + "'");
        switch (properties["targetName"]) {
            case "label":
                this.controlValue._setLabel(properties["value"]);
                return;
            case "help":
                this._setHelp(properties["value"]);
                return;
            case "hint":
                this._setHint(properties["value"]);
                return;
            case "alert":
                this._setAlert(properties["value"]);
                return;
            case "value":
                this.controlValue._handleSetControlValue(properties["value"]);
                return;
        }
    },

    _updateMIPClasses:function() {

        /*
         console.debug("betterform.ui.Control._checkMIP: contextInfo:",this.contextInfo,
         " enabled: " +this.contextInfo.enabled +
         " readonly: " + this.contextInfo.readonly+
         " required: " +this.contextInfo.required +
         " valid: " + this.contextInfo.valid);
         */

        if (this.contextInfo.enabled != undefined) {
            if (this.contextInfo.enabled == "true") {
                betterform.ui.util.replaceClass(this.domNode, "xfDisabled", "xfEnabled");
            } else {
                betterform.ui.util.replaceClass(this.domNode, "xfEnabled", "xfDisabled");
            }
        }
        if (this.contextInfo.readonly != undefined) {
            if (this.contextInfo.readonly == "true") {
                betterform.ui.util.replaceClass(this.domNode, "xfReadWrite", "xfReadOnly");
            } else {
                betterform.ui.util.replaceClass(this.domNode, "xfReadOnly", "xfReadWrite");
            }
        }
        if (this.contextInfo.required != undefined) {
            if (this.contextInfo.required == "true") {
                betterform.ui.util.replaceClass(this.domNode, "xfOptional", "xfRequired");
            } else {
                betterform.ui.util.replaceClass(this.domNode, "xfRequired", "xfOptional");
            }
        }
        if (this.contextInfo.valid != undefined) {
            if (this.contextInfo.valid == "true") {
                betterform.ui.util.replaceClass(this.domNode, "xfInvalid", "xfValid");
            } else {
                betterform.ui.util.replaceClass(this.domNode, "xfValid", "xfInvalid");
            }
        }

    },

    _setHelp:function(value) {
        // console.warn("TBD: Control._setHelp value:"+ value);
        var helpNode = dojo.byId(this.id + "-help");
        if (helpNode != undefined) {
            helpNode.innerHTML = value;
        }
        else {
            console.warn("Failure updating help for Control '" + this.id + "-help' with value: " + value);
        }
    },

    _setHint:function(value) {
        // Container for storing the hint-node if it exists
        var hintNode = dojo.byId(this.id + "-hint");
        // Container for storing the node which contains a title attribute
        var valueNode = dijit.byId(this.id + "-value");

        // Value for: Is a hint Node available at the current DOM-structure
        var hintNodeFound = false;
        // Value for: Is a title-Attribute availabel at the current DOM-structure
        var titleAttributeFound = false;

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

    _setAlert:function(value) {
        var alertNode = dojo.byId(this.id + "-alert");
        if (alertNode != undefined) {
            alertNode.innerHTML = value;
        }
        else {
            console.error("Failure updating alert for Control '" + this.id + "-alert' with value: " + value);
        }

    },

    _setValueChild:function(value) {
        console.warn("TBD: Control._setValueChild value:" + value);
    },

    _handleRequiredEmpty:function(){
        if (dojo.hasClass(this.domNode, "xfRequiredEmpty")) {
            dojo.removeClass(this.domNode, "xfRequiredEmpty");
        }
    }
});


