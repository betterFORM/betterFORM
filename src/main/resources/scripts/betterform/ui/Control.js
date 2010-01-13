/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.Control");

dojo.require("dijit._Widget");
dojo.require("betterform.ui.UIElementFactory");

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

    buildRendering: function(){
            // we already have the srcNodeRef, so lets just
                    // keep it and use it as the domNode
            this.domNode = this.srcNodeRef;
            if(dojo.attr(this.domNode ,"tabindex")){
                this.tabindex = eval(dojo.attr(this.domNode ,"tabindex"));
            }
    },

    postCreate:function(){
        // ensure all needed classes for Control are in place
        // TODO: examine if this can be done in handleStateChanged
        betterform.ui.util.setDefaultClasses(this.domNode);

        if (this.controlValue == undefined) {
            // console.debug("Control.postCreate this.domNode:",this.domNode);
            // verify if controlValue node allready exist, if not create it
            var controlValueTemplate = dojo.query("*[id ='" + this.id + "-value']", this.domNode)[0];
            if(controlValueTemplate == undefined){
                controlValueTemplate = dojo.query(".xfValue", this.domNode)[0];
            }
            // ControlValue node does not exist
            if (controlValueTemplate == undefined) {
               // console.debug("Control.postCreate XFControl " + this.id + " has no value node! Value node is created based on ContextInfo: ", this.contextInfo, " domNode:",this.domNode);

                // prepare Control Node
                if(this.contextInfo.type != undefined && this.contextInfo.type !=""){
                    var cssDataType = betterform.ui.util.removeNamespace(this.contextInfo.type);
                    cssDataType = "xsd" + cssDataType.replace(/^[a-z]/,cssDataType.substring(0,1).toUpperCase())
                    if(dojo.hasClass(this.domNode, "xsd")){
                        betterform.ui.util.replaceClass(this.domNode, "xsd",cssDataType);
                    }else {
                        dojo.addClass(this.domNode, cssDataType);
                    }
                } else if(dojo.hasClass(this.domNode, "xsd")){
                    console.warn("Control.postCreate Control " + this.id + " has no type but xsd on it's prototype");
                }
                this._updateMIPClasses();
                controlValueTemplate = document.createElement("div");

                this.dataType = betterform.ui.util.removeNamespace(this.contextInfo.type);
                this.controlType = this.contextInfo.targetName;

                // add attributes
                dojo.attr(controlValueTemplate, "dataType", this.dataType);
                dojo.attr(controlValueTemplate, "controlType", this.controlType );
                if(this.contextInfo.value != undefined && this.dataType == "date") {
                    this.controlType = dojo.attr(controlValueTemplate, "schemaValue", this.contextInfo.schemaValue);
                }
                if(this.contextInfo.targetId != undefined) {
                    this.controlType = dojo.attr(controlValueTemplate, "id", this.contextInfo.targetId+"-value");
                }
                if(dojo.attr(this.domNode, "appearance")!=undefined) {
                    this.appearance = dojo.attr(this.domNode, "appearance");
                    dojo.attr(controlValueTemplate, "appearance", this.appearance);
                }
                if(dojo.attr(this.domNode, "mediatype")!=undefined) {
                    this.appearance = dojo.attr(this.domNode, "mediatype");
                    dojo.attr(controlValueTemplate, "mediatype", this.appearance);
                }

                // place value as child of valueNode
                if(this.contextInfo.targetName != "trigger") {
                    controlValueTemplate.innerHTML = this.contextInfo.value;
                }else {
                    // console.debug("this.contextInfo.targetName == trigger contextinfo: ", this.contextInfo, " controlValueTemplate: ",controlValueTemplate);
                    dojo.attr(controlValueTemplate,"label" ,this.srcNodeRef.innerHTML);
                    this.domNode.innerHTML = ""; 
                }

                // add classes
                dojo.addClass(controlValueTemplate, "xfValue");
                // insert ControlValue node
                dojo.place(controlValueTemplate,this.domNode);

                // incremental handling
                if(dojo.hasClass(this.domNode,"xfIncremental")) {
                    dojo.attr(controlValueTemplate,"incremental","true");
                }
                this.controlValue = fluxProcessor.factory.createWidget(controlValueTemplate, this.id);
                // console.debug("Created ControlValue:",this.controlValue);

            }
            // ControlValue node does allready exist
            else {
                this.dataType = betterform.ui.util.removeNamespace(dojo.attr(controlValueTemplate, "datatype"));
                this.controlType = dojo.attr(controlValueTemplate, "controltype");
                this.controlValue = fluxProcessor.factory.createWidget(controlValueTemplate, this.id);                
            }
            if(this.controlValue != undefined){
                this.controlValue.applyState();
            }

        }
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

        *//*
        ...
        *//*
    },*/

    isRequired:function(){
        // console.debug("Control.isRequired",this.domNode);
        if(dojo.hasClass(this.domNode,"xfOptional")){
            return false;
        }else if(dojo.hasClass(this.domNode,"xfRequired")){
            return true;
        }else {
            console.error("No required state found")
        }
    },

    isReadonly:function(){
        // console.debug("Control.isReadonly",this.domNode);
        if(dojo.hasClass(this.domNode,"xfReadWrite")){
            return false;
        }else if(dojo.hasClass(this.domNode,"xfReadOnly")){
            return true;
        }else {
            console.error("No readonly state found")
        }
    },

    isRelevant:function(){
        //console.debug("Control.isRelevant",this.domNode);
        if(dojo.hasClass(this.domNode,"xfDisabled")){
            return false;
        }else if(dojo.hasClass(this.domNode,"xfEnabled")){
            return true;
        }else {
            console.error("No relevant state found")
        }
    },

    isValid:function(){
        if(dojo.hasClass(this.domNode,"xfInvalid")){
            return false;
        }else if(dojo.hasClass(this.domNode,"xfValid")){
            return true;
        }else {
            console.error("No validate state found for " + this.id);
        }
    },


    handleStateChanged:function(contextInfo){        
        // console.debug("Control.handleStateChanged: ",contextInfo);

        if (contextInfo["parentId"]) {
            // console.debug("Control._handleHelperChanged: ",contextInfo);
            this._handleHelperChanged(contextInfo);
        }else{
            this.value =  contextInfo["value"];
            this.valid = contextInfo["valid"];
            this.readonly = contextInfo["readonly"];
            this.required = contextInfo["required"];
            this.relevant = contextInfo["enabled"];
            // console.debug("Control.handleStateChanged value:",this.value," valid:", this.valid, " readonly:",this.readonly," required:",this.required, " relevant:",this.relevant), " contextInfo:",contextInfo;
            
            if(contextInfo["targetName"]=="input" && this.value != null){
                var noNSType = betterform.ui.util.removeNamespace(contextInfo["type"]);
                this._changedDataType(noNSType, contextInfo);

                if (noNSType == "date" || noNSType == "dateTime") {
                    this._handleSetControlValue(contextInfo["schemaValue"]);
                } else {
                    this._handleSetControlValue(this.value);
                }

            }else if(this.value != null) {
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

    _onFocus:function(){
        /* console.debug("Control._onFocus()); */
    },

    _onBlur:function(){
        /* console.debug("Control._onBlur()); */
    },

    getControlValue:function(){
        if(this.controlValue != undefined){
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

            var controlValueNode = document.createElement("div");
            dojo.attr(controlValueNode, "dataType", dataType);
            dojo.attr(controlValueNode, "controlType", this.controlType);
            dojo.attr(controlValueNode, "id", this.id + "-value");

            dojo.addClass(controlValueNode, "xfValue");
            var formerTypeClass = "xsd" + this.dataType.replace(/^[a-z]/, this.dataType.substring(0, 1).toUpperCase());
            if(dojo.hasClass(this.domNode, formerTypeClass)){
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

    _handleSetControlValue:function(value){
        if(this.controlValue.currentValue != value) {
            this.controlValue.currentValue = value;
            this.controlValue._handleSetControlValue(value);
        }
        // dojo.publish("/xf/valueChanged",[this,value])
    },



    _handleSetValidProperty:function(){
        // console.debug("Control._handleSetValidProperty (id:"+this.id+")");
        if (this.valid == "true") {
            betterform.ui.util.replaceClass(this.domNode, "xfInvalid", "xfValid");
            this.controlValue.displayValidity(true);
            dojo.publish("/xf/valid",[this])
        }
        else {
            betterform.ui.util.replaceClass(this.domNode, "xfValid", "xfInvalid");
            this.controlValue.displayValidity(false);
            dojo.publish("/xf/invalid",[this])
        }

    },

    _handleSetReadonlyProperty: function(){
        if (eval(this.readonly) == false) {
            betterform.ui.util.replaceClass(this.domNode, "xfReadOnly", "xfReadWrite");
            // dojo.publish("/xf/readwrite",[this])
        }
        else {
            betterform.ui.util.replaceClass(this.domNode, "xfReadWrite", "xfReadOnly");
            // dojo.publish("/xf/readonly",[this])
        }
        this.controlValue.applyState();
    },

    _handleSetRequiredProperty:function(){
        if (this.required == "true") {
            betterform.ui.util.replaceClass(this.domNode, "xfOptional", "xfRequired");
            // dojo.publish("/xf/required",[this])
        }
        else {
            betterform.ui.util.replaceClass(this.domNode, "xfRequired", "xfOptional");
            if(dojo.hasClass(this.domNode, "xfRequiredEmpty")) {
                dojo.removeClass(this.domNode, "xfRequiredEmpty");
            }
            // dojo.publish("/xf/optional",[this])
        }
    },

    _handleSetEnabledProperty: function(){
        var targetId = this.id;
        var label = dojo.byId(targetId + "-label");

        if (this.relevant == "true") {
            betterform.ui.util.replaceClass(this.domNode, "xfDisabled", "xfEnabled");
            betterform.ui.util.replaceClass(label, "xfDisabled", "xfEnabled");
            if(!this.isValid()&& this.controlValue.focused == true){
                this.controlValue.showAlert();
            }
            // dojo.publish("/xf/enabled",[this])
        }
        else {
            betterform.ui.util.replaceClass(this.domNode, "xfEnabled", "xfDisabled");
            betterform.ui.util.replaceClass(label, "xfEnabled", "xfDisabled");
            this.controlValue.hideAlert();
            // dojo.publish("/xf/disabled",[this])
        }
    },

    _handleHelperChanged: function(properties){
        // console.debug("Control.handleHelperChanged: type='" + properties["type"] + "',  value='" + properties["value"] + "'");
        switch (properties["targetName"]) {
            case "label":
                this.controlValue._setLabel( properties["value"]);
                return;
            case "help":
                this._setHelp( properties["value"]);
                return;
            case "hint":
                this._setHint( properties["value"]);
                return;
            case "alert":
                this._setAlert( properties["value"]);
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

        if(this.contextInfo.enabled != undefined) {
            if(this.contextInfo.enabled == "true") {
                betterform.ui.util.replaceClass(this.domNode,"xfDisabled", "xfEnabled");
            }else {
                betterform.ui.util.replaceClass(this.domNode,"xfEnabled", "xfDisabled");
            }
        }
        if(this.contextInfo.readonly != undefined) {
            if (this.contextInfo.readonly == "true") {
                betterform.ui.util.replaceClass(this.domNode, "xfReadWrite", "xfReadOnly");
            } else {
                betterform.ui.util.replaceClass(this.domNode, "xfReadOnly", "xfReadWrite");
            }
        }
        if(this.contextInfo.required != undefined) {
            if(this.contextInfo.required == "true") {
                betterform.ui.util.replaceClass(this.domNode, "xfOptional", "xfRequired");
            }else {
                betterform.ui.util.replaceClass(this.domNode, "xfRequired", "xfOptional");
            }
        }
        if(this.contextInfo.valid != undefined) {
            if(this.contextInfo.valid == "true") {
                betterform.ui.util.replaceClass(this.domNode, "xfInvalid", "xfValid");
            }else {
                betterform.ui.util.replaceClass(this.domNode, "xfValid", "xfInvalid");
            }
        }

    },

    updateProgress:function(value) {
        this.controlValue.updateProgress(value);        
    },

    setControlValue:function(/* String */ value) {
        fluxProcessor.setControlValue(this.id, value);
    },

    _setHelp:function( value) {
        // console.warn("TBD: Control._setHelp value:"+ value);
        var helpNode = dojo.byId(this.id + "-help");
        if(helpNode != undefined) {
            helpNode.innerHTML = value;
        }
        else {
            console.warn("Failure updating help for Control '" +this.id + "-help' with value: " + value);
        }
    },

    _setHint:function( value) {
        var hintNode = dojo.byId(this.id + "-hint");
        if(hintNode != undefined) {
            hintNode.innerHTML = value;
        }
        else {
            console.error("Failure updating hint for Control '" +this.id + "-hint' with value: " + value);
        }
    },

    _setAlert:function( value) {
        var alertNode = dojo.byId(this.id + "-alert");
        if(alertNode != undefined) {
            alertNode.innerHTML = value;
        }
        else {
            console.error("Failure updating alert for Control '" +this.id + "-alert' with value: " + value);
        }

    },

    _setValueChild:function( value) {
        console.warn("TBD: Control._setValueChild value:"+ value);
    }
});


