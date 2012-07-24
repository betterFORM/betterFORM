/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/_base/declare","dojo/dom", "dojo/dom-class","dojo/query",
        "dojo/dom-attr","dojo/_base/connect","dojo/dom-construct","dijit/registry","dojo/behavior","bf/util"],
    function(declare, dom, domClass,query,domAttr,connect,domConstruct,registry,behavior){
        return declare(null, {


    /**
     * All Rights Reserved.
     * @author Joern Turner
     *
     * BoundElement is a superclass for all controls and container but also for those elements bound by an AVT. It
     * contains all functions dealing with state changes except the handling of the value.
     *
     **/

        id:"",
        bfFocus:false,

        constructor:function(properties, node){
            // console.debug("XFBinding.constructor properties:",properties, " node:" ,node);
            this.srcNodeRef = node;
            this.id = node.id;
            // console.debug("XFBinding.constructor setDefaultClasses");
            bf.util.setDefaultClasses(this.srcNodeRef);

            /*
             Controls publish their validity state to the processor which will pass it to the selected alertHandler
             */
            // console.debug("XFBinding.constructor handleValid");
            if (this.isValid()) {
                connect.publish("xforms-valid", [this.id,"init"]);
            } else {
                connect.publish("xforms-invalid", [this.id,"init"]);
            }
            // console.debug("XFBinding.constructor subscribe state change");
            // console.debug("XFBinding.constructor: connect.subscribe('bf-state-change-"+ this.id + "', this, 'handleStateChanged')");
            connect.subscribe("bf-state-change-"+ this.id, this, "handleStateChanged");
        },

        /*
        handles state changes (value and MIP changes) send by the server and applies them to the control. State
        changes are received from the client side xforms processor (XFProcessor) which handles all communication
        between client and server.
         */
        handleStateChanged:function(contextInfo) {
            // console.debug("BoundElement.handleStateChanged: ",contextInfo);

            if (contextInfo["parentId"]) {
                // console.debug("Control._handleHelperChanged: ",contextInfo);
                this._handleHelperChanged(contextInfo);
            } else {
                this.value = contextInfo["value"];
                this.valid = contextInfo["valid"];
                this.readonly = contextInfo["readonly"];
                this.required = contextInfo["required"];
                this.relevant = contextInfo["enabled"];
                this.type = contextInfo["type"];

                // console.debug("XFControl.handleStateChanged value:",this.value," valid:", this.valid, " readonly:",this.readonly," required:",this.required, " relevant:",this.relevant, " targetName:",contextInfo["targetName"]," type:",contextInfo["type"], " contextInfo:",contextInfo);

                // check xsd type and adjust if needed

                if(this.type != undefined && this.type != ""){
                    // console.warn("XFControl.handleStateChange type changed");
                    var xsdType = "xsd" + this.type.replace(/^[a-z]/, this.type.substring(0, 1).toUpperCase());
                    // TODO: existing types must be removed in case of type switch
                    // console.debug("apply new type: ",xsdType, " to Control Widget");
                    if(!domClass.contains(this.srcNodeRef, xsdType)){
                        // console.debug("XFBinding.handleStateChange behavior.apply");
                        domClass.add(this.srcNodeRef, xsdType);
                        behavior.apply();
                    }
                }

                // console.debug("XFBinding: this.value:",this.value, " this.readonly:",this.readonly);
                // Validity handling
                if(this.valid != undefined){
                    if (this.valid == "true") {
                        this.setValid();
                    }else if(!domClass.contains(this.srcNodeRef,"bfInvalidControl")){
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
            // console.debug("Control.isRequired",this.srcNodeRef);
            if (domClass.contains(this.srcNodeRef, "xfOptional")) {
                return false;
            } else if (domClass.contains(this.srcNodeRef, "xfRequired")) {
                return true;
            } else {
                console.error("XFBinding.isRequired No required state found")
            }
        },

        isReadonly:function() {
            // console.debug("Control.isReadonly",this.srcNodeRef);
            if (domClass.contains(this.srcNodeRef, "xfReadWrite")) {
                return false;
            } else if (domClass.contains(this.srcNodeRef, "xfReadOnly")) {
                return true;
            } else {
                console.error("XFBinding.isReadonly No readonly state found")
            }
        },

        isRelevant:function() {
            //console.debug("Control.isRelevant",this.srcNodeRef);
            if (domClass.contains(this.srcNodeRef, "xfDisabled")) {
                return false;
            } else if (domClass.contains(this.srcNodeRef, "xfEnabled")) {
                return true;
            } else {
                console.error("XFBinding.isRelevant: No relevant state found")
            }
        },

        isValid:function() {
            // console.debug("XFControl.isValid",this.srcNodeRef);

            if (domClass.contains(this.srcNodeRef, "xfInvalid")) {
                return false;
            } else if (domClass.contains(this.srcNodeRef, "xfValid")) {
                return true;
            } else {
                console.error("XFBinding.isValid No validate state found for " + this.id);
            }
        },

        setValid:function() {
            domClass.replace(this.srcNodeRef, "xfValid","xfInvalid");
            connect.publish("xforms-valid", [this.id,"applyChanges"]);

        },

        setInvalid:function() {
            domClass.replace(this.srcNodeRef, "xfInvalid", "xfValid");
            connect.publish("xforms-invalid", [this.id,"applyChanges"]);
        },

        setReadonly:function() {
            domClass.replace(this.srcNodeRef, "xfReadOnly", "xfReadWrite");
            domAttr.set(this.getWidget(), "readonly","readonly");
        },

        setReadwrite:function() {
            // console.debug("this.getWidget():",this.getWidget(), " this.srcNodeRef:",this.srcNodeRef);
            domClass.replace(this.srcNodeRef,"xfReadWrite", "xfReadOnly");
            this.getWidget().removeAttribute("readonly");
        },

        setRequired:function() {
            domClass.replace(this.srcNodeRef, "xfRequired", "xfOptional");
        },

        setOptional:function() {
            domClass.replace(this.srcNodeRef, "xfOptional", "xfRequired");
        },

        setEnabled:function() {
            var label = dom.byId(this.id + "-label");
            if (label != undefined) {
                if (domClass.contains(label, "xfDisabled")) {
                    domClass.replace(label, "xfEnabled", "xfDisabled");
                } else {
                    domClass.add(label, "xfEnabled");
                }
            }
            domClass.replace(this.srcNodeRef, "xfEnabled","xfDisabled");

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
                    domClass.replace(label,"xfDisabled", "xfEnabled");
                } else {
                    domClass.add(label, "xfDisabled");
                }
            }
            domClass.replace(this.srcNodeRef, "xfDisabled", "xfEnabled");
            if (this.isValid()) {
                connect.publish("xforms-valid", [this.id, "xfDisabled"]);
            } else {
                connect.publish("xforms-invalid", [this.id, "xfDisabled"]);
            }
        },

        _handleHelperChanged: function(properties) {
           // console.debug("Control.handleHelperChanged: this.id: "+this.id+ "type='" + properties["targetName"] + "',  value='" + properties["value"] + "'");
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
            }
        },

        setLabel:function(value) {
             // console.debug("Control.setLabel value:"+ value);

            var labelNode = dom.byId(this.id + "-label");
            if (labelNode != undefined) {
                labelNode.innerHTML = value;
            }
            else {
                console.warn("XFBinding.setLabel Failure updating label for Control '" + this.id + "-help' with value: " + value);
            }
        },
        setHelp:function(value) {
            // console.warn("TBD: Control.setHelp value:"+ value);
            var helpNode = dom.byId(this.id + "-help");
            if (helpNode != undefined) {
                helpNode.innerHTML = value;
            }
            else {
                console.warn("XFBinding.setHelp Failure updating help for Control '" + this.id + "-help' with value: " + value);
            }
        },

        setHint:function(value) {
            // Container for storing the hint-node if it exists
            var hintNode = dom.byId(this.id + "-hint");
            // Container for storing the node which contains a title attribute
            //todo: review valueNode reference
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
                    console.warn("XFBinding.setHint title attribute for hint " +  this.id + "-hint" + " is empty");
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
                console.warn("XFBinding.setHint Failure updating hint for Control '" + this.id + " with value: " + value + " ... neither found '" + this.id + "-hint' nor '" + this.id + "-value");
            }
        },

        setAlert:function(value) {
            var alertNode = dom.byId(this.id + "-alert");
            if (alertNode != undefined) {
                alertNode.innerHTML = value;
            }
            else {
                console.error("XFBinding.setAlert Failure updating alert for Control '" + this.id + "-alert' with value: " + value);
            }

        },

        getWidget:function() {
            if(domClass.contains(this.srcNodeRef,"xfContainer")){
                if(this.widget == undefined) {
                    this.widget = this.srcNodeRef;
                }
                return this.widget;

            }else {
                if(this.widget == undefined) {
                    this.widget = dom.byId(this.id+"-value");
                }
                return this.widget;
            }
        }
    });
});


