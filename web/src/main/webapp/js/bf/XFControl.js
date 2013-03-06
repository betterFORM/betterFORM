/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

//define(['jquery', 'jquery-ui',"bf/XFBinding", "bf/util"],
define(['jquery', 'jquery-ui'],
    function($){
        $.widget("bf.bfcontrol", {


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
        id: "", // from XFBinding.js
        bfFocus:false, // from XFBinding.js
        srcNodeRef: null, // from XFBinding.js
        widget: null,
        	
        options : {	
        	incremental:false,
        	delay: 0
        },


        /**
         * function to update the currentValue of XFControl (important
         * @param value
         */
        setCurrentValue:function(value){
            // console.debug("XFControl.setCurrentValue value:", value, " currentValue:",this.currentValue);
            this.currentValue = value;

        },

        _create: function() {
        	
        
        	this.id = $(this.element).attr('id');
        	
        	if(this.element.attr('xf')) {
        		$('<input id="'+ this.id + '-value" type="text" class="xfValue" name=​d_"'+ this.id + '" placeholder value />').appendTo(this.element);
        	}
        	
            if(this.isIncremental()){
                this.incremental = true;
            }
            
            // from XFBinding.js (constructor)
            
            		this.srcNodeRef = this.element;
           			
            
           			setDefaultClasses(this.srcNodeRef);
           			/*
            		Controls publish their validity state to the processor which will pass it to the selected alertHandler
           			 */
           			// console.debug("XFBinding.constructor handleValid");
           			// Moved to init
           			// console.debug("XFBinding.constructor subscribe state change");
           			// console.debug("XFBinding.constructor: $("#"+this.id).on('bf-state-change', $.proxy(this.handleStateChanged, this)");
           			
           			var bfStateChangedHandle = $("#"+this.id).on('bf-state-change', $.proxy(this.handleStateChanged, this));
          
           			$.subscribe("xforms-ready", $.proxy(this._handleInit, this));
           
           			// xformsprocessor.addSubscriber(this.id, bfStateChangedHandle);

            
         // end from XFBinding.js
      
        },
        
        _handleInit:function() {
        	if (this.isValid()) {
   				$.publish("xforms-valid", [this.id, "init"]);
   			} else {
   				$.publish("xforms-invalid", [this.id, "init"]);
   			}	
        },

        /**
         * sends an updated value of a widget to the server
         * @param value - the current Widget value to be send to the server
         * @param changeFocus - notifies xfControl if DOMFocusOut must be fired
         */

        sendValue:function(value, /*Boolean*/ changeFocus) {
            // console.debug("XFControl.sendValue: currentvalue:", this.currentValue, " - newValue:",value);
            if(this.isReadonly()){
                // console.debug("XFControl sendValue - control is readonly - ignoring event");
                return;
            }
            
//            console.log("Focus CONTROL: ", $(this.element).has(":focus") > 0 );
//            if($(this.element).has(":focus").length > 0){
//                console.debug("XFControl sendValue - control still has focus (just not the input) - ignoring event");
//                return;
//            }

            if (value != undefined && this.currentValue != value) {
                //update internal value
                this.currentValue = value;
                xformsprocessor.sendValue(this.id, value);
                this._handleRequiredEmpty();
            }
            if(this.isValid()){
                $.publish("xforms-valid",[this.id,"onBlur"]);
            }else {
                $.publish("xforms-invalid",[this.id,"onBlur"]);
            }

            /*if(changeFocus && xformsprocessor.usesDOMFocusOUT){*/
            if(changeFocus){
                this.bfFocus = false;
                //notify server of lost focus
                xformsprocessor.dispatchEventType(this.id,"DOMFocusOut");
            }
        },

        /*
        handles state changes (value and MIP changes) send by the server and applies them to the control. State
        changes are received from the client side xforms processor (XFProcessor) which handles all communication
        between client and server.
        Extends XFBinding.handleStateChanged
         */
        handleStateChanged:function(ev, contextInfo) {
            // console.debug("XFControl.handleStateChanged this:", this, " contextInfo:",contextInfo);
            var tmpContextInfo = contextInfo;
            this.handleStateChangedSuper(contextInfo);

            if (contextInfo["parentId"] == undefined) {
                var self = this;
                //require(["dojo/ready"], function (ready) {
                //    ready(function () {
                        // console.debug("XFControl.handleStateChanged after super call self.value:",self.value, " tmpContextInfo['value']: ", tmpContextInfo["value"]);
                        var value = tmpContextInfo["value"];
                        if (value != null && contextInfo["targetName"] != "label") {
                            // console.debug("XFControl.handleStateChange value != null: contextInfo:", contextInfo);
                            self.currentValue = value;
                            // console.debug("XFControl.handleStateChanged: calling self.setValue with value:",self.value ," and this.schemavalue:",tmpContextInfo["schemaValue"]);
                            
                            console.debug("XFControl.handleStateChanged... change the code back at this log line");
                            //self.setValue(value, tmpContextInfo["schemaValue"]);
                            schemaValue = tmpContextInfo["schemaValue"] ? tmpContextInfo["schemaValue"] : value;
                            self.setValue(value, tmpContextInfo["schemaValue"]);
                        }
                //    })
                //});
            }
        },

        isIncremental:function(){
            return $(this.element).hasClass( "xfIncremental");
        },

        /*
         fetches the value from the widget
         */
        getControlValue:function() {
            if(this.currentValue != undefined){
                return this.currentValue;
            }else {
                return "";
            }
        },


        // TODO: Lars: implement new (if needed), controlValue does not exist anymore
        _checkForDataTypeChange:function(dataType) {
             // console.debug("_checkForDataTypeChange: old dataType: " + this.dataType + " new dataType: ", dataType, " contextInfo:",contextInfo);

            if (this.controlValue == undefined) {
                var controlValueTemplate = $("*[id ='" + this.id + "-value']", this.element)[0];
                if (controlValueTemplate == undefined) {
                    controlValueTemplate = $(".xfValue", this.element)[0];
                }
                if (controlValueTemplate == undefined) {
                    console.error("Control._checkForDataTypeChange Error: XFControl " + this.id + " has no ControlValue node");
                    return;
                }
                else {
                    domAttr.set(controlValueTemplate, "dataType", dataType);
                    domAttr.set(controlValueTemplate, "id", this.id + "-value");
    //                this.controlValue = xformsprocessor.factory.createWidget(controlValueTemplate, this.id);

                }

            } else if (this.dataType != dataType && !(this.dataType == "" && dataType == "string")) {
                // console.debug("datatype for existing dijit changed this.dataType: " , this.dataType + "  dataType: ", dataType);

                var controlValueNode = document.createElement("span");
                domAttr.set(controlValueNode, "dataType", dataType);
                domAttr.set(controlValueNode, "controlType", this.controlType);
                domAttr.set(controlValueNode, "id", this.id + "-value");

                domClass.add(controlValueNode, "xfValue");
                var formerTypeClass = "xsd" + this.dataType.replace(/^[a-z]/, this.dataType.substring(0, 1).toUpperCase());
                if ($(this.element).hasClass( formerTypeClass)) {
                    // console.debug("remove CSS Type " + formerTypeClass);
                	$(this.element).removeClass(formerTypeClass);
                }
                domClass.add(this.element, "xsd" + dataType.replace(/^[a-z]/, dataType.substring(0, 1).toUpperCase()));

                this.controlValue.destroy();
                this.controlValue = xformsprocessor.factory.createWidget(controlValueNode, this.id);
                domConstruct.place(this.controlValue.domNode, this.element);
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
            console.error("XFControl.setValue must be overwritten by it's accdoring ControlValue Widget "+ this.widget.widgetName +" [id:", this.id + "-value]");
        },

        _handleRequiredEmpty:function(){
            //if (this., "xfRequiredEmpty")) {
        	$(this.element).removeClass("xfRequiredEmpty");
            //}
        },
        

        getWidget:function() {
            if(this.widget == undefined) {
                this.widget = $('#'+this.id+"-value");
            }
            return this.widget;
        },
        
        setWidget: function(widget) {
        	this.widget = widget;
        },
        
        getId: function() {
        	return this.id;
        },

        handleOnFocus:function() {
            //storing current control id for handling help
            // console.debug("XFControl.handleOnFocus storing current control id:", this.id, " value: ",this.currentValue);

            xformsprocessor.currentControlId = this.id;

            /*if (!this.bfFocus && xformsprocessor.usesDOMFocusIN) {*/
            if (!this.bfFocus) {
                // console.debug("ControlValue: dispatch DOMFocusIn to ",this.xfControl.id);
                xformsprocessor.dispatchEventType(this.id,"DOMFocusIn");
            }
            this.bfFocus = true;
            if(this.isValid()){
                $.publish("xforms-valid",[this.id,"onFocus"]);
            }else {
                $.publish("xforms-invalid",[this.id,"onFocus"]);
            }
        },
        
        // 
        
        
        
        /*
        handles state changes (value and MIP changes) send by the server and applies them to the control. State
        changes are received from the client side xforms processor (XFProcessor) which handles all communication
        between client and server.
        */
       handleStateChangedSuper:function(contextInfo) {
    	   // console.debug("XFBinding.handleStateChangedSuper: ",contextInfo);

           if (contextInfo["parentId"]) {
        	   // console.debug("XFBinding.handleStateChanged: calling _handleHelperChanged");
               this._handleHelperChanged(contextInfo);
           } else {
        	   //console.debug("XFBinding.handleStateChanged: adjust properties");
               this.value = contextInfo["value"];
               this.valid = contextInfo["valid"];
               this.readonly = contextInfo["readonly"];
               this.required = contextInfo["required"];
               this.relevant = contextInfo["enabled"];
               var formerType = this.type;
               this.type = contextInfo["type"];

               // console.debug("XFBinding.handleStateChanged value:",this.value," valid:", this.valid, " readonly:",this.readonly," required:",this.required, " relevant:",this.relevant, " targetName:",contextInfo["targetName"]," type:",contextInfo["type"], " contextInfo:",contextInfo);

               // check xsd type and adjust if needed
               //console.debug("XFBinding.handleStateChanged this.type: ", this.type, " formerType:",formerType);
               if(this.type != undefined && this.type != "" && this.type != formerType){
                   var index = this.type.indexOf(":");
                   if (index != -1) {
                       this.type = this.type.substring(index+1, this.type.length);
                   }
                   //console.info("XFBinding.handleStateChange: removed namespace from type:" , this.type);
                   // console.warn("XFBinding.handleStateChange type changed");
                   var xsdType = "xsd" + this.type.replace(/^[a-z]/, this.type.substring(0, 1).toUpperCase());
                   // TODO: existing types must be removed in case of type switch
                   //console.debug("apply new type: ",xsdType, " to Control Widget");
                   if(!$(this.srcNodeRef).hasClass(xsdType)){
                       // console.debug("XFBinding.handleStateChange behavior.apply");
                       domClass.add(this.srcNodeRef, xsdType);
                       behavior.apply();
                   }
               }
               var self = this;
               //require(["dojo/ready"],function(ready){
              //     ready(function(){

                       // console.debug("XFBinding.handleStateChanged (ready): self.value:",self.value, " self.readonly:",self.readonly, " self.srcNodeRef:",self.srcNodeRef);
                       // Validity handling
                       // console.debug("XFBinding.handleStateChanged handle Valid");
                       if (self.valid != undefined) {
                           if (self.valid == "true") {
                               // console.debug("XFBinding.handleStateChanged setValid");
                               self.setValid();
                           }
                           else if (!$(self.srcNodeRef).hasClass("bfInvalidControl")) {
                               // console.debug("XFBinding.handleStateChanged setInvalid");
                               /*
                                todo: got the feeling that this case should be handled elsewhere....
                                if a control is intially invalid it just has xfInvalid but not bfInvalidControl. This may happen
                                during init and somehow the subscriber won't be called then (too early???)

                                Ok, for now: if control is not valid (has 'xfInvalid' class) and not has 'bfInvalidControl' (which
                                actually shows an alert) it must nevertheless publish invalid event for the alerts to work correctly.
                                */
                               self.setInvalid();
                           }
                       }
                       // console.debug("XFBinding.handleStateChanged handle ReadOnly");
                       if(self.readonly != undefined) {
                           if (self.readonly == "true") {
                               self.setReadonly();
                           }else {
                               self.setReadwrite();
                           }
                       }
                       // console.debug("XFBinding.handleStateChanged handle Required");
                       if(self.required != undefined) {
                           if (self.required == "true") {
                               self.setRequired();
                           }else {
                               self.setOptional();
                           }
                       }
                       // console.debug("XFBinding.handleStateChanged handle Relevant");
                       if(self.relevant != undefined) {
                           if (self.relevant == "true") {
                               self.setEnabled();
                           }else {
                               self.setDisabled();
                           }
                       }
                       // console.debug("XFBinding.handleStateChanged END ready()");
                  // });
              // })
           }
       },

       isRequired:function() {
           // console.debug("Control.isRequired",this.srcNodeRef);
           if ($(this.srcNodeRef).hasClass("xfOptional")) {
               return false;
           } else if ($(this.srcNodeRef).hasClass("xfRequired")) {
               return true;
           } else {
               console.error("XFBinding.isRequired No required state found")
           }
       },

       isReadonly:function() {
           // console.debug("Control.isReadonly",this.srcNodeRef);
           if ($(this.srcNodeRef).hasClass("xfReadWrite")) {
               return false;
           } else if ($(this.srcNodeRef).hasClass("xfReadOnly")) {
               return true;
           } else {
               console.error("XFBinding.isReadonly No readonly state found")
           }
       },

       isRelevant:function() {
           //console.debug("Control.isRelevant",this.srcNodeRef);
           if ($(this.srcNodeRef).hasClass("xfDisabled")) {
               return false;
           } else if ($(this.srcNodeRef).hasClass("xfEnabled")) {
               return true;
           } else {
               console.error("XFBinding.isRelevant: No relevant state found")
           }
       },

       isValid:function() {
           // console.debug("XFBinding.isValid",this.srcNodeRef);

           if ($(this.srcNodeRef).hasClass( "xfInvalid")) {
               return false;
           } else if ($(this.srcNodeRef).hasClass( "xfValid")) {
               return true;
           } else {
               console.error("XFBinding.isValid No validate state found for " + this.id);
           }
       },

       setValid:function() {
    	   this.widget.option("valid", true);
           $(this.srcNodeRef).addClass("xfValid").removeClass("xfInvalid");
           $.publish("xforms-valid", [this.id,"applyChanges"]);

       },

       setInvalid:function() {
    	   this.widget.option("valid", false);
           $(this.srcNodeRef).addClass("xfInvalid").removeClass("xfValid");
           $.publish("xforms-invalid", [this.id,"applyChanges"]);
       },

       setReadonly:function() {
           $(this.srcNodeRef).addClass("xfReadOnly").removeClass("xfReadWrite");
    	   this.widget.option("readonly", true);
           // console.debug("XFBinding.setReadonly widget:",this.getWidget());
       },

       setReadwrite:function() {
           $(this.srcNodeRef).addClass("xfReadWrite").removeClass("xfReadOnly");
    	   this.widget.option("readonly", false);
           // console.debug("XFBinding.setReadwrite widget:",this.getWidget());

       },

       setRequired:function() {
           $(this.srcNodeRef).addClass("xfRequired").removeClass("xfOptional");
       },

       setOptional:function() {
           $(this.srcNodeRef).addClass("xfOptional").removeClass("xfRequired");
       },

       setEnabled:function() {
           var label = $("#"+this.id + "-label");
           if (label != undefined) {
               if ($(label).hasClass("xfDisabled")) {
            	   $(label).addClass("xfEnabled").removeClass("xfDisabled");
               } else {
            	   $(label).addClass("xfEnabled");
               }
           }
           $(this.srcNodeRef).addClass("xfEnabled").removeClass("xfDisabled");

           if (this.isValid()) {
               $.publish("xforms-valid", [this.id, "xfDisabled"]);
           } else {
               $.publish("xforms-invalid", [this.id, "xfDisabled"]);
           }
       },

       setDisabled:function() {
           var label = $("#"+this.id + "-label");
           if (label != undefined) {
               if ($(label).hasClass("xfEnabled")) {
            	   $(label).addClass("xfDisabled").removeClass("xfEnabled");
               } else {
            	   $(label).addClass("xfDisabled");
               }
           }

           $(this.srcNodeRef).addClass("xfDisabled").addClass("xfEnabled");
           if (this.isValid()) {
               $.publish("xforms-valid", [this.id, "xfDisabled"]);
           } else {
               $.publish("xforms-invalid", [this.id, "xfDisabled"]);
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
           // console.debug("XFBinding.setLabel value:"+ value);

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
                   var titleAttribute = domAttr.get(valueNode, "title");
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
               domAttr.set(valueNode, "title", value);
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
           if($(this.srcNodeRef).hasClass("xfContainer")){
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


