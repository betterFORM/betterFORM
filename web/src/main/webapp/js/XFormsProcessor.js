var XFormsProcessor = function(){};

XFormsProcessor.prototype = {
    debugInstance: false,
    debugLog: false,
    
    usesDOMFocusOUT: false,
    usesDOMFocusIN: false,
    useXFSelect: false, 
    
    isReady: false,

    enableDebug: function(msg) {
        this.debugInstance = msg;
        alert(this.debugInstance);
    },
    
    
	applyChanges: function(data) {
		
	        var validityEvents = [];
            var validityIndex = 0;
            
            //eventLog writing
            //var eventLog = $.byId("eventLog");

            $.each(data,
                function(index, xmlEvent) {
            	
            	try{
	            	switch (xmlEvent.type) {
	            	
	                	case "xforms-model-construct" :
		                	console.info("xforms-model-construct");	break;
		                case "xforms-model-construct-done" : 
		                	console.info("xforms-model-construct-done");
		                	xformsprocessor._buildUI();	break;
		                case "xforms-ready" : 
		                	console.info("xforms-ready");
		                	xformsprocessor.isReady = true;
		                	//connect.publish("xforms-ready", []);
		                	break; //not perfect - should be on XFormsModelElement
                        case "xforms-invalid" :
                        	xformsprocessor._handleInvalid(xmlEvent);
                        	break;
                        case "xforms-valid" : 
                        	validityEvents[validityIndex] = xmlEvent; validityIndex++;
                        	break;
                        case "xforms-value-changed" :
                        	console.debug("xforms-value-changed", xmlEvent); 
                        	break;
    	            	case "betterform-state-changed":
	            			xformsprocessor._handleBetterFormStateChanged(xmlEvent);
	            			break;
                        	
	            			
                        case "betterform-id-generated"       : break;
                        case "DOMActivate"                   : break;
                        case "xforms-select"                 : break;
                        case "xforms-deselect"               : break;
                        case "DOMFocusOut"                   : break;
		                /* Unknow XMLEvent: */
		                default : 
		                	console.error("Event " + xmlEvent.type + " unknown [Event:", xmlEvent, "]"); 
		                	break;
	            	}
            	} catch (ex) {
            		this._handleExceptions("An error occurred during applyChanges for" + xmlEvent, ex);
            	} 
            } 
            );
            
            try {
            if (validityEvents.length > 0) {
                this._handleValidity(validityEvents);
            }
            
		} catch(ex) {
                this._handleExceptions("An error occurred during handling of validityEvents after applyChanges ", ex);
        }
        //The pending request tournaround has been completed
        //fluxProcessor.requestPending = false;
        //Schedule the next FIFO-Read try in 0 ms
        //clearTimeout(fluxProcessor.fifoReaderTimer);
        //fluxProcessor.fifoReaderTimer = setTimeout("fluxProcessor.eventFifoReader()", 0);

	},
	
	_handleValidity:function(validityEvents) {
	        console.debug("XFProcessor._handleValidity validityEvents:",validityEvents);
	        try {
	        $.each(validityEvents, function(index, xmlEvent) {
	            var xfControl = $("#"+xmlEvent.contextInfo.targetId);
	            if (xfControl != undefined) {
	                if (xmlEvent.type == "xforms-valid") {
	                	xfControl.bfcontrol("setValid");
	                } else if (xmlEvent.type == "xforms-invalid") {
	                	xfControl.bfcontrol("setInvalid");
	                } else {
	                	console.error("Invalid event processsed in _handleValidity: " + xmlEvent.type);
	                }
	            }
	        });} catch (ex) {
	        	this._handleExceptions("An error occurred during _handleValidity ", ex);
	        }
	        
	    },
	
    _handleInvalid:function(xmlEvent){
        console.debug("XFProcessor._handleInvalid xmlEvent:",xmlEvent);
        var targetid = xmlEvent.contextInfo.targetId;
        var alertContainer = $("#"+targetid + "-alert");

        if(alertContainer != null){
            //remove old ones
            $(".bfAlertMsg",alertContainer).remove();


            //add incoming ones
            // buttons do not get an alert text send, even if defined... weird... 
            // dojo most likely accepts undefined arrays... jquery does not!
            if (!xmlEvent.contextInfo.alerts === undefined) { 
	            $.each(xmlEvent.contextInfo.alerts, function(index, alert) {
	                // console.debug("alert " + index + " is " + alert);
	                //domConstruct.create("span",{class:'bfAlertMsg',innerHTML:alert},alertContainer);
	            	$("<span class='bfAlertMsg'>"+alert+"</span>").appendTo(alertContainer);
	            });
	            //            connect.publish("xforms-invalid", [targetid,"invalid"]);
	            $(alertContainer).css("display", "inline-block");
            }
        }
    },
    
    
 
	
    _handleBetterFormStateChanged:function(/*XMLEvent*/ xmlEvent) {
        var contextInfo = xmlEvent.contextInfo;
        // IMPORTANT: DOJO/READY MUST NOT BE REMOVED!
        //  Otherwise _handleBetterFormStateChanged will be called before _handleBetterFORMInsert is finished!
        //require(["dojo/ready"], function(ready){
        //    ready(function(){
                console.debug("XFProcessor._handleBetterFormStateChanged: contextInfo: ", contextInfo);
                var parentId = contextInfo.parentId;
                var target;
                // if contextInfo.parentId is present jquery must publish to this id instead of targetid (e.g. used for value changes of labels)
                if(parentId) {
                    // console.debug("XFProcessor._handleBetterFormStateChanged: publish (parent): bf-state-change- ", parentId, " contextInfo:",contextInfo);
                    target = $("#"+contextInfo.parentId);
                }
                else {
                    // console.debug("XFProcessor._handleBetterFormStateChanged: publish: bf-state-change- ", contextInfo.targetId, " contextInfo:",contextInfo);
                    target = $("#"+contextInfo.targetId);
                }
                try {
                	target.trigger("bf-state-change", contextInfo);
                } catch(ex) {
                	this._handleExceptions("An error occurred during _handleBetterFormStateChanged ", ex);
                }
        //    });
        //})
    },
    
    /*
    Sends a value from a widget to the server. Will be called after any user interaction.
    */
   sendValue: function(controlId, value) {
       console.debug("XFProcessor.sendValue", controlId, value);
//       var newClientServerEvent = new ClientServerEvent();
//       newClientServerEvent.setTargetId(controlId);
//       newClientServerEvent.setValue(value);
//       newClientServerEvent.setCallerFunction("setControlValue");
//       this.eventFifoWriter(newClientServerEvent);
	   this._setControlValue(controlId, value);
   },

    _handleBetterFormItemChanged:function(/*XMLEvent*/ xmlEvent) {
        console.debug("XFProcessor._handleBetterFormItemChanged: targetId: " + xmlEvent.contextInfo.targetId , " xmlEvent: " , xmlEvent);
        var parentDOMNode = dom.byId(xmlEvent.contextInfo.parentId);
        try {
            while(!domClass.contains(parentDOMNode, "xfValue")){
                parentDOMNode = parentDOMNode.parentNode;
            }
            // console.debug("XFProcessor._handleBetterFormItemChanged: id: ", domAttr.get(parentDOMNode, "id"), " parentNode: ",parentDOMNode);
            var selectParentId = domAttr.get(parentDOMNode, "id");
            connect.publish("xforms-item-changed-"+ selectParentId, xmlEvent.contextInfo);
        } catch(e) {
            console.warn("XFProcessor._handleBetterFormItemChanged: Select(1) or  item [", xmlEvent.contextInfo.parentId, "] not found! Error:",e);
        }
    },
	
    _handleExceptions:function(msg, exception) {
        console.debug("XFProcessor._handleExceptions msg:",msg, " exception: ", exception);
        if (msg != undefined && exception != undefined) {
            console.error(msg, ' - Exception: ', exception.message);
        } else if (msg != undefined) {
            console.error(msg);
            alert(msg);
        } else {
            console.error("Unknown exception occured! arguments: ", arguments);
        }
    },
	
    dispatchEventType:function(targetId, eventType, contextInfo) {
        if((this.usesDOMFocusOUT == false && "DOMFocusOut" == eventType) ||
            (this.usesDOMFocusIN == false && "DOMFocusIn" == eventType) ||
            (this.useXFSelect == false && "xformsSelect" == eventType)){
            console.info("XFProcessor.dispatchEventType: event:",eventType, " is disabled in form!! targetId is: ",targetId);
            return;

        }
        // change clientside eventType xformsSelect to DOMActivate for the Java processor
        if(eventType == "xformsSelect"){
            eventType = "DOMActivate";
        }
        // console.debug("XFProcessor.dispatchEventType(",targetId,") this: ", this, " eventType:",eventType, " contextInfo:",contextInfo);
//        var newClientServerEvent = new ClientServerEvent();
//        newClientServerEvent.setTargetId(targetId);
//        newClientServerEvent.setEventType(eventType);
//        newClientServerEvent.setContextInfo(contextInfo);
//        newClientServerEvent.setCallerFunction("dispatchEventType");
//        this.eventFifoWriter(newClientServerEvent);
        this._dispatchEventType(targetId, eventType, contextInfo);
        
    },
    
	init: function() {
		$.getJSON("rest/init", {}, function(xmlEvents) {
			//alert("JSON Data: " + xmlEvents);
				xformsprocessor.applyChanges(xmlEvents);
			}).error(
					function(jqXHR, textStatus, errorThrown) {
						xformsprocessor._handleExceptions("Failure executing " + eventType + ": " + textStatus + "(" + errorThrown + ")"  );
				    }		
			
			);
	},
    
	_setControlValue: function(id, value) {
		
		$.getJSON("rest/setControlValue/"+id+"/"+value, {}, function(xmlEvents) {
			//alert("JSON Data: " + xmlEvents);
				xformsprocessor.applyChanges(xmlEvents);
			}).error(
					function(jqXHR, textStatus, errorThrown) {
						xformsprocessor._handleExceptions("Failure executing _setControlValue: " + textStatus + "(" + errorThrown + ")"  );
				    }		
			
			);
	},

    _dispatchEventType:function(id, eventType, contextInfo) {
        console.debug("XFProcessor._dispatchEventType(",id,") this: ", this, " eventType:",eventType, " contextInfo:",contextInfo);
        		
        		$.getJSON("rest/dispatchEventType/"+id+"/"+eventType, contextInfo, function(xmlEvents) {
        			//alert("JSON Data: " + xmlEvents);
        				xformsprocessor.applyChanges(xmlEvents);
        			}).error(
        					function(jqXHR, textStatus, errorThrown) {
        						xformsprocessor._handleExceptions("Failure executing _dispatchEventType (" + eventType + "): " + textStatus + "(" + errorThrown + ")"  );
        				    }		
        			
        			);
        	
            // call other method if contextInfo is not defined???
        	
        	// onerror: xformsprocessor._handleExceptions("Failure executing " + eventType);
        
    },
    
	
	_buildUI: function() {
		
		//this.mappingProcessor = new MappingProcessor();
		
//		var JS_FILE_NAME = "bf/bfinputtext";
//		var widgetName = "bfinputtext";
        // console.debug("FOUND: ", n);
        // console.debug("MappingProcessor: map to: ", JS_CLASS_NAME, " param: ", param);
//        require([JS_FILE_NAME],
//            function(){
//		if (typeof $.fn[widgetName] !== 'function') 
//        {
//            throw new Error("jqueryui binding doesn't recognize '" + widgetName 
//                + "' as jQuery UI widget");
//        }
        	//console.log(JS_CLASS_NAME);
        	//console.log(window[JS_CLASS_NAME]);
        	//console.log(window[JS_CLASS_NAME]() );
		try {
			$('.xfControl')['bfcontrol']();
			$('.xfInput:not(.xsdDate):not(.xsdDateTime):not(.xsdTime):not(.xsdBoolean) .xfValue')['bfinputtext']();
        	//$('.xfInput:not(.xsdDate):not(.xsdDateTime):not(.xsdTime):not(.xsdBoolean) .xfValue')['puiinputtext']();
			
        	$('.xfInput.xsdBoolean > * >  .xfValue')["bfcheckbox"]();
        	
            $('.xfTrigger:not(.aMinimal) .xfValue')["bfbutton"]();
        	//$('.xfInput.xsdBoolean > * >  .xfValue')["puicheckbox"]();

        //});
		
        	$('#formWrapper').fadeIn('slow');
		} catch (ex) {
			this._handleExceptions("Error building ui", ex);
		}

		
	},
    
	dummyFunction: function(id, value) {
		
		$.getJSON("rest/<command>/"+id+"/"+value, { jsonParam1: "JSONValue1", jsonParam2: "JSONValue2" }, function(json) {
			
			// json in the case of BF responses is an immediate array of xml events
			// So json[1] gives you the first event
			// json[0].contextInfo.message gives you the message (if present) 
			// The alert below wil show something like [Object object, Object object, ... ]
			alert("JSON Data: " + json);
			
			});
		
		
	}
};