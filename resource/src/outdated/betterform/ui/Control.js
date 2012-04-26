/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.Control");

dojo.require("dijit._Widget");

/* dojo.Dialog and Button are needed to render helps  */
dojo.require("dijit.Dialog");
dojo.require("dijit.form.Button");


/**
 * All Rights Reserved.
 * @author Joern Turner
 * @author Lars Windauer
 *
 * Control is a generic wrapper for all XForms elements that have MiPs to maintain. One Component at runtime is a
 * one-to-one match to an XForms control in the XFormsProcessor. It has the same id as the original XForms control
 * and acts as a client-side proxy for it.
 **/

dojo.declare(
        "betterform.ui.Control",
        [dijit._Widget, dijit._Templated],
{
    id:"",
    _earlyTemplatedStartup:true,
    widgetsInTemplate:true,

    target: null,

    value:"",

    dataType:"",
    controlType:"",
    valid:true,

    readonly:false,

    required:false,

    relevant:true,
    controlValue:null,
    contextInfo:null,
    tabindex:0,
    appearance:"",

    buildRendering: function() {
        // we already have the srcNodeRef, so lets just
        // keep it and use it as the domNode
        this.domNode = this.srcNodeRef;
        // console.debug("\nControl.buildRendering; DOM Node:",this.domNode,"\n");
        if (dojo.attr(this.domNode, "tabindex")) {
            this.tabindex = eval(dojo.attr(this.domNode, "tabindex"));
        }
    },

    postCreate:function() {
        // ensure all needed classes for Control are in place
        // console.debug("\nControl.postCreate; DOM Node:",this.domNode,"\n");
        // TODO: examine if this can be done in handleStateChanged
        betterform.ui.util.setDefaultClasses(this.domNode);

        if (this.controlValue == undefined) {
            // console.debug("Control.postCreate this.domNode:",this.domNode);
            // verify if controlValue node allready exist, if not create it
            var controlValueTemplate = dojo.query("*[id ='" + this.id + "-value']", this.domNode)[0];
            if (controlValueTemplate == undefined) {
                controlValueTemplate = dojo.query(".xfValue", this.domNode)[0];
            }
            // Child node CntrolValue does not exist, ControlValueTemplate is created dynamicly
            if (controlValueTemplate == undefined) {
                controlValueTemplate = this._createControlValueTemplate();
            }
            // ControlValue node exists and a dijit is created
            else {
                this.dataType = betterform.ui.util.removeNamespace(dojo.attr(controlValueTemplate, "datatype"));
                this.controlType = dojo.attr(controlValueTemplate, "controltype");
            }

            this.controlValue = fluxProcessor.factory.createWidget(controlValueTemplate, this.id);

            if (this.controlValue != undefined) {
                // apply MIP states
                this.controlValue.applyState();
            } else {
                console.error("ControlValue for Control " + this.id + " could not be created");
            }
        }
        if (this.isValid()) {
            dojo.publish("/xf/valid", [this.id,"init"]);
        } else {
            dojo.publish("/xf/invalid", [this.id,"init"]);
        }
        // console.debug("\nControl.postCreate; DOM Node:",this.domNode,"\n");

    },

    /**
     * Create a ControlValue template of properties taken from Control
     */
    _createControlValueTemplate:function() {
        // console.debug("Control.createControlValueTemplate XFControl " + this.id + " has no value node! Value node is created based on ContextInfo: ", this.contextInfo, " domNode:",this.domNode);

        // prepare Control Node
        if (this.contextInfo.type != undefined && this.contextInfo.type != "") {
            var cssDataType = betterform.ui.util.removeNamespace(this.contextInfo.type);
            cssDataType = "xsd" + cssDataType.replace(/^[a-z]/, cssDataType.substring(0, 1).toUpperCase());
            if (dojo.hasClass(this.domNode, "xsd")) {
                betterform.ui.util.replaceClass(this.domNode, "xsd", cssDataType);
            } else {
                dojo.addClass(this.domNode, cssDataType);
            }
        } else if (dojo.hasClass(this.domNode, "xsd")) {
            console.warn("Control.postCreate Control " + this.id + " has no type but xsd on it's prototype");
        }
        this._updateMIPClasses();
        // verify that span is ok
        // var controlValueTemplate = document.createElement("div");
        var controlValueTemplate = document.createElement("span");

        this.dataType = betterform.ui.util.removeNamespace(this.contextInfo.type);
        this.controlType = this.contextInfo.targetName;

        // add attributes
        dojo.attr(controlValueTemplate, "dataType", this.dataType);
        dojo.attr(controlValueTemplate, "controlType", this.controlType);
        if (this.contextInfo.value != undefined && this.dataType == "date") {
            this.controlType = dojo.attr(controlValueTemplate, "schemaValue", this.contextInfo.schemaValue);
        }
        if (this.contextInfo.targetId != undefined) {
            this.controlType = dojo.attr(controlValueTemplate, "id", this.contextInfo.targetId + "-value");
        }
        if (dojo.attr(this.domNode, "appearance") != undefined) {
            this.appearance = dojo.attr(this.domNode, "appearance");
            dojo.attr(controlValueTemplate, "appearance", this.appearance);
        }
        if (dojo.attr(this.domNode, "mediatype") != undefined) {
            this.appearance = dojo.attr(this.domNode, "mediatype");
            dojo.attr(controlValueTemplate, "mediatype", this.appearance);
        }

        // place value as child of valueNode
        if (this.contextInfo.targetName != "trigger") {
            controlValueTemplate.innerHTML = this.contextInfo.value;
        } else {
            // console.debug("this.contextInfo.targetName == trigger contextinfo: ", this.contextInfo, " controlValueTemplate: ",controlValueTemplate);
            dojo.attr(controlValueTemplate, "label", this.srcNodeRef.innerHTML);
            this.domNode.innerHTML = "";
        }

        // add classes
        dojo.addClass(controlValueTemplate, "xfValue");
        // insert ControlValue node
        var bfValueWrapper = dojo.query(".bfValueWrapper", this.domNode)[0];
        // console.debug("control to insert: ", controlValueTemplate);
        if(bfValueWrapper != undefined) {
            dojo.place(controlValueTemplate, bfValueWrapper);
        }else {
            dojo.place(controlValueTemplate, this.domNode);
        }


        // incremental handling
        if (dojo.hasClass(this.domNode, "xfIncremental")) {
            dojo.attr(controlValueTemplate, "incremental", "true");
        }
        // incremental delay handling
        if (dojo.hasAttr(this.domNode, "delay")) {
            dojo.attr(controlValueTemplate, "delay", dojo.attr(this.domNode, "delay"));
        }
        return controlValueTemplate;
    },



    /*    is called initially when controls initialize to read their state from
     the CSS class attribute of the XForms shadow control (div wrapper element)

     This approach is at least questionable as it leaves open the question who is
     controlling the state actually. If the CSS classes serve the initial state but
     this is maintained later in JS classes this opens two roads to state handling.
     At least the master (and direction of updating) should be defined more clearly.

     If classes are considered the master (being initially set by processor and updated by
     XMLEvents from the processor) they should be seen as readonly. The implementation
     of the scripts should never assume correctness of

     initFromClasses:function(){
     console.debug("initFormClasses id: ", this.id);

     var targetNode = this._getControlWrapper();

     if(dojo.hasClass(targetNode,"readonly")){
     console.debug(this, " is readonly");
     }
     },*/

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
        // console.debug("Control.isValid ", dojo.hasClass(this.domNode, "xfValid"))
        if (dojo.hasClass(this.domNode, "xfInvalid")) {
            return false;
        } else if (dojo.hasClass(this.domNode, "xfValid")) {
            return true;
        } else {
            console.error("No validate state found for " + this.id);
        }
    },


    handleStateChanged:function(contextInfo) {
        // console.debug("Control.handleStateChanged: ",contextInfo);

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
                this._changedDataType(noNSType, contextInfo);

                if (noNSType == "date" || noNSType == "dateTime") {
                    this._handleSetControlValue(contextInfo["schemaValue"]);
                } else {
                    this._handleSetControlValue(this.value);
                }

            } else if (this.value != null) {
                this._handleSetControlValue(this.value);
            }

            if (this.valid != null) {
                this._handleSetValidProperty(eval(this.valid));
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

    getControlValue:function() {
        if (this.controlValue != undefined) {
            return this.controlValue.getControlValue();
        }
    },


    _changedDataType:function(dataType, contextInfo) {
        // console.debug("check if dataType changed for ui control: old dataType: " + this.dataType + " new dataType: ", dataType, " contextInfo:",contextInfo);

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
                this.controlValue = fluxProcessor.factory.createWidget(controlValueTemplate, this.id);

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
        // console.debug("handleSetControlValue: " + this.controlValue.currentValue + " value: " + value);
        if(this.controlValue.currentValue != value) {
            this.controlValue.currentValue = value;
            this.controlValue._handleSetControlValue(value, true);
            this._handleRequiredEmpty();
        }
        // dojo.publish("/xf/valueChanged",[this,value])
    },



    _handleSetValidProperty:function(validity) {
        // console.debug("Control._handleSetValidProperty [id:"+this.id+ " valid: ",validity, "]");
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

    updateProgress:function(value) {
        this.controlValue.updateProgress(value);
    },

    setControlValue:function(/* String */ value) {
        fluxProcessor.setControlValue(this.id, value);
        this._handleRequiredEmpty();
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


