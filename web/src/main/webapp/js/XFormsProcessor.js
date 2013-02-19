var XFormsProcessor = function(){};

XFormsProcessor.prototype = {
    debugInstance: false,
    debugLog: false,
    
    isReady: false,

    enableDebug: function(msg) {
        this.debugInstance = msg;
        alert(this.debugInstance);
    },
    
    
	applyChanges: function(data) {
		try {
	        var validityEvents = [];
            var index = 0;
            
            //eventLog writing
            //var eventLog = $.byId("eventLog");

            $.each(data,
                function(index, xmlEvent) {
            	
	            	switch (xmlEvent.type) {
	            	
	            	case "betterform-state-changed":
	            			xformsprocessor._handleBetterFormStateChanged(xmlEvent); break;
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
		            	
		                /* Unknow XMLEvent: */
		                default : 
		                	console.error("Event " + xmlEvent.type + " unknown [Event:", xmlEvent, "]"); 
		                	break;
	            	}
            	}
            );
		} catch(ex) {
                //fluxProcessor._handleExceptions("An error occurred during applyChanges ", ex);
        }
        //The pending request tournaround has been completed
        //fluxProcessor.requestPending = false;
        //Schedule the next FIFO-Read try in 0 ms
        //clearTimeout(fluxProcessor.fifoReaderTimer);
        //fluxProcessor.fifoReaderTimer = setTimeout("fluxProcessor.eventFifoReader()", 0);

	},
	
    _handleBetterFormStateChanged:function(/*XMLEvent*/ xmlEvent) {
        var contextInfo = xmlEvent.contextInfo;
        // IMPORTANT: DOJO/READY MUST NOT BE REMOVED!
        //  Otherwise _handleBetterFormStateChanged will be called before _handleBetterFORMInsert is finished!
        //require(["dojo/ready"], function(ready){
        //    ready(function(){
                console.debug("XFProcessor._handleBetterFormStateChanged: contextInfo: ", contextInfo);
                var parentId = contextInfo.parentId;

                // if contextInfo.parentId is present dojo must publish to this id instead of targetid (e.g. used for value changes of labels)
                if(parentId) {
                    // console.debug("XFProcessor._handleBetterFormStateChanged: publish (parent): bf-state-change- ", parentId, " contextInfo:",contextInfo);
                    $.event.trigger("bf.bf-state-change-"+ parentId, contextInfo);

                }
                else {
                    // console.debug("XFProcessor._handleBetterFormStateChanged: publish: bf-state-change- ", contextInfo.targetId, " contextInfo:",contextInfo);
                    var target = $("#"+contextInfo.targetId);
                    target.trigger("bf.bf-state-change");
                    $.trigger("bf.bf-state-change-"+contextInfo.targetId);
                }
        //    });
        //})
    },
    
	setControlValue: function(id, value) {
		
		$.getJSON("rest/setControlValue/"+id+"/"+value, {}, function(xmlEvents) {
			//alert("JSON Data: " + xmlEvents);
				xformsprocessor.applyChanges(xmlEvents);
			});
		
		
	},
	
	init: function() {
		$.getJSON("rest/init", {}, function(xmlEvents) {
			//alert("JSON Data: " + xmlEvents);
				xformsprocessor.applyChanges(xmlEvents);
			}).error(function() { alert("error")});
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
            console.error(msg, ' - Exception: ', exception);
        } else if (msg != undefined) {
            console.error(msg);
            alert(msg);
        } else {
            console.error("Unknown exception occured! arguments: ", arguments);
        }
    },
	

	
	_buildUI: function() {
		//$('.xfInput.xsdString input').bfinputtext();
		$('#input1-value').bfinputtext();
		$('#formWrapper').fadeIn('slow');
		//$('#formWrapper').css('display', 'inline-block');
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