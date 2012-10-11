define(["dojo/_base/declare",
    "bf/XFormsProcessor",
    "bf/ClientServerEvent",
    "dojo/dom",
    "dojo/query",
    "dojo/dom-class",
    "dojo/_base/window",
    "dojo/dom-style",
    "dojo/dom-attr",
    "dojo/_base/connect",
    "dojo/_base/lang",
    "dojo/dom-construct",
    "dojo/_base/array",
    "dijit/registry",
    "dojo/has",
    "dojo/_base/json"], function(declare, XFormsProcessor,ClientServerEvent,
                                 dom,query,domClass,win,domStyle,domAttr,connect,lang,domConstruct,array,registry,has, json){
    return declare("bf.XFProcessor",XFormsProcessor, {

        /**
         All Rights Reserved.
         @author Joern Turner
         @author Lars Windauer

         This class represents the interface to the remote XForms processor (aka 'betterForm Web') with DWR. It is the only class
         actually having dependency on DWR to handle the AJAX part of things and calling remote Java methods on
         de.betterform.web.betterform.FluxFacade.
         **/

        sessionKey:dojo.config.bf.sessionkey,
        skipshutdown:false,
        isDirty:false,
        currentControlId:"", // todo: only used for help, refactor later
        unloadMsg:"You are about to leave this XForms application",
        isReady:false,
        contextroot:dojo.config.bf.contextroot,
        defaultAlertHandler:null,//todo: change to use behavior
        subscribers:[], //todo:see line above
        clientServerEventQueue:[],
        requestPending:false,
        fifoReaderTimer:null,
        lastServerClientFocusEvent:null,
        usesDOMFocusIN:dojo.config.bf.useDOMFocusIN,
        usesDOMFocusOUT:dojo.config.bf.useDOMFocusOUT,
        useXFSelect:dojo.config.bf.useXFSelect,
        logEvents:dojo.config.bf.logEvents,
        mappingProcessor:null,
        _uiReady:false,
        initialEvents:new Array(),
        bfDialogs:[],


        /*
         keepAlive: function() {
         if(dwr.engine){
         dwr.engine.setErrorHandler(this._handleExceptions);
         dwr.engine.setOrdered(true);
         Flux.keepAlive(this.sessionKey);
         }
         },
         */

        constructor:function() {
            // console.debug("XFProcessor.constructor sessionKey:",this.sessionKey);

            // initialize DWR

            Flux._path = dojo.config.bf.fluxPath;
            this.unloadMsg = dojo.config.bf.unloadingMessage;
            // console.debug("calling Flux.init");
            Flux.init(dojo.config.bf.sessionkey, dojo.hitch(this,this.applyChanges));

            // This is used for referencing this object from within ajax-callback-functions
            this.indicatorContainer = dom.byId('bfLoading');
            this.indicatorImage = dom.byId('indicator');
            this.indicatorImage.className = 'xfDisabled';
            // Initialize the clientServerEventQueue for immediately being able to append Elements
            this.clientServerEventQueue = new Array();

            connect.connect(window, "onbeforeunload", this, "handleUnload");
            connect.connect(window, "onunload", this, "close");
            this.skipshutdown = false;

            // Browser Detection
            this.userAgent = navigator.userAgent;
        },


        handleUnload:function(evt) {
            // console.debug("XFProcessor.handleUnload Event: ", evt);
            if (this.isDirty && !this.skipshutdown) {
                event.stopEvent(evt);
                // console.debug(this.unloadMsg);
                // For IE
                evt.returnValue = this.unloadMsg;
                // For all others
                return this.unloadMsg;
            }
        },

        close:function() {
            var tmpSkipShutdown = lang.hitch(this, fluxProcessor.skipShutdown).skipshutdown;
            if (!tmpSkipShutdown) {
                fluxProcessor.closeSession();
            }
        },

        closeSession: function() {
            try {
                dwr.engine.setErrorHandler(this._handleExceptions);
                dwr.engine.setOrdered(true);
                Flux.close(this.sessionKey);
            }
            catch(ex) {
                fluxProcessor._handleExceptions("Failure executing Flux.closeSession ", ex);
            }
        },

        ignoreExceptions: function (msg) {
            console.warn("XFProcessor.ignoreExceptions():");
        },


        /* Tries to sequentially process pending Events, as long as there is no other roundtrip in progress (Client-Server-Client)
         * Initiates a roundtrip the following conditions apply:
         * 1) The related Objects still exist:  Dijit + Dojo/DOM
         * 2) The Control is NOT read-only (only validated if it CAN be read-only)
         * 3) If an appended Attribute is a reference, the target (dijit/dojo) still needs to exist.
         * ... changed references are updated. (e.g. xf:repeat items)
         */
        eventFifoReader: function() {
            // console.debug("XFProcessor.eventFifoReader: this.clientServerEventQueue:",this.clientServerEventQueue);
            var nextPendingClientServerEvent = null;
            var dojoObject = null;
            var dijitObject = null;

            //Loop as long as Pending Events are being skipped (as long as no Request is being initiated)
            while ((!this.requestPending) && (this.clientServerEventQueue.length != 0)) {
                var setRepeatIndexEvent = undefined;
                for(var i = 0; i < this.clientServerEventQueue.length;i++) {
                    var clientServerEvent = this.clientServerEventQueue[i];
                    if(clientServerEvent.callerFunction=="setRepeatIndex"){
                        setRepeatIndexEvent = clientServerEvent;
                        this.clientServerEventQueue.splice(i, 1);
                        break;
                    }
                }
                if(setRepeatIndexEvent != undefined) {
                    nextPendingClientServerEvent = setRepeatIndexEvent;
                }else {
                    nextPendingClientServerEvent = this.clientServerEventQueue.shift();
                }

                // console.debug("nextPendingClientServerEvent: ",nextPendingClientServerEvent);
                var callerFunction = nextPendingClientServerEvent.getCallerFunction();
                var nextPendingTargetId = nextPendingClientServerEvent.getTargetId();

                //*****************************************************************************
                // START: skip this pending Event, if one of the following conditions occurred:
                //*****************************************************************************

                dojoObject = dom.byId(nextPendingTargetId);
                // console.debug("EventFifoReader dojoObject:",dojoObject, " targetId: ",nextPendingTargetId);
                if (dojoObject == null) {
                    console.warn("Event (Client to Server) for Dojo Control " + dojoObject + " skipped. CAUSE: OBJECT is NULL",nextPendingTargetId);
                    continue;
                }

                if(callerFunction != "setRepeatIndex"){
                    dijitObject = registry.byId(nextPendingTargetId);
                }

                // console.debug("EventFifoReader dijitObject:",dijitObject, " targetId: ",nextPendingTargetId);

                if (dijitObject == null && callerFunction != "setRepeatIndex") {
                    console.warn("XFProcessor.eventFifoReader: Event (Client to Server) for Dijit Control " + dijitObject + " skipped. CAUSE: OBJECT is NULL");
                    continue;

                }else if(callerFunction != "setRepeatIndex"){
                    // Test if this dijit-control has an isReadonly() method
                    // console.debug("XFProcessor.eventFifoReader: check if Object is readonly:",dijitObject, " reaondly: ",dijitObject.isReadonly());

                    if (dijitObject && dijitObject.isReadonly()) {
                        console.warn("XFProcessor.eventFifoReader: Event (Client to Server) for Dijit Control " + dijitObject + " skipped. CAUSE: READ-ONLY");
                        continue;
                    }

                }


                // Test if the Control's event was a setControlValue
                if (callerFunction == "setControlValue") {
                    // Test if the next Control-Value-Change originates from the same Control as this Control-Value-Change
                    if (this.clientServerEventQueue[0] != null) {
                        // Test if the targetId of this event and the next one are equal
                        if (this.clientServerEventQueue[0].getTargetId() == nextPendingTargetId) {
                            // Test if the CallerFunction of the next Event is also setControlValue
                            if (this.clientServerEventQueue[0].getCallerFunction() == "setControlValue") {
                                // console.debug("XFProcessor.eventFifoReader: Event (Client to Server) for Dijit Control " + dijitObject + " skipped. CAUSE: superseeded by following value-change of same Control");
                                continue;
                            }
                            else {
                                //console.debug("Nothing to skip. CAUSE: Following Event's CallerFunction differs from setControlValue");
                            }
                        }
                        else {
                            // console.debug("Nothing to skip. CAUSE: Next Event's ID was different from this Event's ID");
                        }
                    }
                    else {
                        // console.debug("Nothing to skip. CAUSE: Buffer was empty");
                    }
                }
                else {
                    // console.debug("Nothing to skip. CAUSE: No setControlValue found");
                }

                // Further processing of setRepeatIndex events
                // TODO: check if really not needed anymore
                /*
                if (callerFunction == "setRepeatIndex") {
                 var repeatItems = query("#" + nextPendingClientServerEvent.targetId +" > tbody > .xfRepeatItem");
                 console.debug("repeatItems: " ,repeatItems," repeatitem position: ",repeatItems[(nextPendingClientServerEvent.getValue -1)]);

                 if (nextPendingClientServerEvent.getRepeatItem() == null) {
                         console.warn("Event (Client to Server) for Dijit Control " + nextPendingTargetId + " skipped. CAUSE: Repeat-Item for being selected has disappeared");
                         continue;
                     }

                     if (nextPendingClientServerEvent.getValue() != dijit.byNode(nextPendingClientServerEvent.getRepeatItem())._getXFormsPosition()) {
                         console.warn("Original Position: " + nextPendingClientServerEvent.getValue + " New Position: " + nextPendingClientServerEvent.getRepeatItem()._getXFormsPosition());
                         // Update the changed Position of this XForms-Repeat-Item
                        nextPendingclientServerEvent.setValue(dijit.byNode(nextPendingClientServerEvent.getRepeatItem())._getXFormsPosition());
                     }
                 }
                 */

                //*****************************************************************************
                // END:   skip this pending Event, if one of the following conditions occurred:
                //*****************************************************************************

                if (dojoObject != null) {
                    this._useLoadingMessage(dojoObject);
                }

                // console.debug("XFProcessor.dispatch event for ",callerFunction);
                switch (callerFunction) {
                    case "dispatchEvent":                this.requestPending = true; this._dispatchEvent(nextPendingTargetId); break;
                    case "dispatchEventType":        this.requestPending = true; this._dispatchEventType(nextPendingTargetId, nextPendingClientServerEvent.getEventType(), nextPendingClientServerEvent.getContextInfo()); break;
                    case "setControlValue":            this.requestPending = true; this._setControlValue(nextPendingTargetId, nextPendingClientServerEvent.getValue()); break;
                    //Re-transform the dojo-Id to repeat-Id
                    case "setRepeatIndex":            this.requestPending = true; this._setRepeatIndex(nextPendingTargetId, nextPendingClientServerEvent.getValue()); break;
                    default:                                        break;
                }
            }
            // console.debug("XFProcessor after Event is dispatched ");
            //Check if there are still more events pending
            if (this.clientServerEventQueue.length != 0) {
                // console.debug("XFProcessor after: this.clientServerEventQueue: ",this.clientServerEventQueue);
                clearTimeout(this.fifoReaderTimer);
                // Just to be sure, that the FIFO Buffer is being checked even in case, that an AJAX-response got lost
                this.fifoReaderTimer = setTimeout("fluxProcessor.eventFifoReader()", 2000);
            }
            else {
                //the last Request has been sent ... stop the timer
                clearTimeout(this.fifoReaderTimer);
            }
        },

        /*
         * Appends the provided clientServerEvent to the Pending-Event-FIFO-Buffer
         * Triggers the FIFO-Reader for trying to process the next pending events at the FIFO-Buffer.
         */
        eventFifoWriter: function(clientServerEvent) {
            // console.debug("XFProcessor.eventFifoWriter clientServerEvent:",clientServerEvent);

            //insert the new clientServerEvent at the beginning of the Buffer
            this.clientServerEventQueue.push(clientServerEvent);
            switch (clientServerEvent) {
                case "dispatchEvent":      console.info("FIFO-WRITE: dispatchEvent(" + clientServerEvent.getTargetId() + ")"); break;
                case "dispatchEventType":  console.info("FIFO-WRITE: dispatchEventType(" + clientServerEvent.getTargetId() + ", " + clientServerEvent.getEventType() + ", " + clientServerEvent.getContextInfo() + ")"); break;
                case "setControlValue":    console.info("FIFO-WRITE: setControlValue(" + clientServerEvent.getTargetId() + ", " + clientServerEvent.getValue() + ")"); break;
                case "setRepeatIndex":     console.info("FIFO-WRITE: setRepeatIndex(" + clientServerEvent.getTargetId() + ", " + clientServerEvent.getValue() + ")"); break;
                default:                   break;
            }
            //schedule the next try for reading the next pending Event of the FIFO-Buffer
            clearTimeout(this.fifoReaderTimer);
            this.fifoReaderTimer = setTimeout("fluxProcessor.eventFifoReader()", 0);
        },

        //eventually an 'activate' method still makes sense to provide a simple DOMActivate of a trigger Element
        dispatchEvent: function (targetId) {
            // console.debug("XFProcessor.dispatchEvent targetId:",targetId);

            var newClientServerEvent = new ClientServerEvent();
            newClientServerEvent.setTargetId(targetId);
            newClientServerEvent.setCallerFunction("dispatchEvent");
            this.eventFifoWriter(newClientServerEvent);
        },

        //eventually an 'activate' method still makes sense to provide a simple DOMActivate of a trigger Element
        _dispatchEvent: function (targetId) {
            // console.debug("XFProcessor.dispatch(",targetId,") this.xfProcessor: ", this);
            try {
                dwr.engine.setErrorHandler(this._handleExceptions);
                dwr.engine.setOrdered(true);
                Flux.dispatchEvent(targetId, this.sessionKey, this.applyChanges);
            } catch(ex) {
                fluxProcessor._handleExceptions("Failure executing Flux.dispatchEvent", ex);
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
            var newClientServerEvent = new ClientServerEvent();
            newClientServerEvent.setTargetId(targetId);
            newClientServerEvent.setEventType(eventType);
            newClientServerEvent.setContextInfo(contextInfo);
            newClientServerEvent.setCallerFunction("dispatchEventType");
            this.eventFifoWriter(newClientServerEvent);
        },

        _dispatchEventType:function(targetId, eventType, contextInfo) {
            // console.debug("XFProcessor._dispatchEventType(",targetId,") this: ", this, " eventType:",eventType, " contextInfo:",contextInfo);
            try {
                dwr.engine.setErrorHandler(this._handleExceptions);
                dwr.engine.setOrdered(true);
                if (contextInfo == undefined) {
                    Flux.dispatchEventType(targetId, eventType, this.sessionKey, lang.hitch(this, this.applyChanges));
                } else {
                    Flux.dispatchEventTypeWithContext(targetId, eventType, this.sessionKey, contextInfo, lang.hitch(this, this.applyChanges));
                }
            }
            catch(ex) {
                fluxProcessor._handleExceptions("Failure executing Flux.dispatchEventType", ex);
            }
        },

        /*
         Sends a value from a widget to the server. Will be called after any user interaction.
         */
        sendValue: function(id, value) {
            // console.debug("XFProcessor.sendValue", id, value);
            var newClientServerEvent = new ClientServerEvent();
            newClientServerEvent.setTargetId(id);
            newClientServerEvent.setValue(value);
            newClientServerEvent.setCallerFunction("setControlValue");
            this.eventFifoWriter(newClientServerEvent);
        },

        _setControlValue: function (id, value) {
            console.debug("XFProcessor.setControlValue", id, value);
            this.isDirty = true;
            try {
                dwr.engine.setErrorHandler(this._handleExceptions);
                dwr.engine.setOrdered(true);
                dwr.engine.setErrorHandler(this._handleExceptions);
                //        Flux.setUIControlValue(id, value, this.sessionKey,this.changeManager.applyChanges);
                Flux.setUIControlValue(id, value, this.sessionKey, this.applyChanges);
            }
            catch(ex) {
                fluxProcessor._handleExceptions("Failure executing Flux.setControlValue", ex);
            }
        },

        setRepeatIndex: function(/*String*/repeatId, /*String*/targetPosition) {
            console.debug("FluxProcessor.setRepeatIndex repeatId:",repeatId, " targetPosition:",targetPosition);
            var newClientServerEvent = new ClientServerEvent();
            newClientServerEvent.setTargetId(repeatId);
            newClientServerEvent.setValue(targetPosition);
            newClientServerEvent.setCallerFunction("setRepeatIndex");
            this.eventFifoWriter(newClientServerEvent);
        },

        _setRepeatIndex:function(/*String*/repeatId, /*String*/targetPosition) {
            // console.debug("XFProcessor.setRepeatIndex for Repeat "+ repeatId + " to position " + targetPosition);
            try {
                dwr.engine.setErrorHandler(this._handleExceptions);
                dwr.engine.setOrdered(true);
                Flux.setRepeatIndex(repeatId, targetPosition, this.sessionKey, this.applyChanges);
            } catch(ex) {
                fluxProcessor._handleExceptions("Failure executing Flux.setRepeatIndex", ex);
            }
        },


        //################################################################################################
        //################################################################################################
        //################################################################################################

        /*
         setRange: function (id, value) {
         dwr.engine.setErrorHandler(this._handleExceptions);
         Flux.setXFormsValue( id, value, this.sessionKey,this.changeManager.applyChanges);
         },


         */

        indicatorObjectTimer: null,
        indicatorContainer: null,
        indicatorImage: null,
        indicatorTargetObject: null,


        _fifoProcessingFinished: function() {
            // console.debug("XFProcessor._fifoProcessingFinished");
            domClass.remove(this.indicatorTargetObject, "bfPending");
            // Don't iterate through all items ... only use the last one and skip the rest
            var currentItem = this.lastServerClientFocusEvent;
            if (currentItem != undefined) {
                if (currentItem != null) {
                    currentItem.postponedFunction(currentItem.postponedXmlEvent);
                    this.lastServerClientFocusEvent = null;
                }
            }


            fluxProcessor.indicatorImage.className = 'xfDisabled';
        },

        _useLoadingMessage:function(dojoObject) {
            // console.debug("XFProcessor._useLoadingMessage dojoObject:", dojoObject);
            if (fluxProcessor.indicatorObjectTimer) {
                clearTimeout(fluxProcessor.indicatorObjectTimer);
            }
            if (this.indicatorTargetObject) {
                domClass.remove(this.indicatorTargetObject, "bfPending");
            }

            this.indicatorTargetObject = dojoObject;

            domClass.add(dojoObject, "bfPending");

            try {
                dwr.engine.setPreHook(function() {
                    fluxProcessor.indicatorImage.className = 'xfEnabled';
                    return false;
                });
                dwr.engine.setPostHook(function() {
                    fluxProcessor.indicatorObjectTimer = setTimeout('fluxProcessor._fifoProcessingFinished()', 500);
                    return false;
                });
            }
            catch(ex) {
                fluxProcessor._handleExceptions("Failure executing Flux._useLoadingMessage", ex);
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

        applyChanges: function(data) {
            try {
                var validityEvents = new Array();
                var index = 0;

                //eventLog writing
                var eventLog = dom.byId("eventLog");

                array.forEach(data,
                    function(xmlEvent) {
                        // *** DO NOT COMMENT THIS OUT !!! ***
                        if(!fluxProcessor._uiReady && xmlEvent.type != "xforms-model-construct" &&
                            xmlEvent.type != "betterform-instance-created" &&
                            xmlEvent.type != "xforms-model-construct-done" &&
                            xmlEvent.type != "xforms-ready"){
                            fluxProcessor.initialEvents.push(xmlEvent);
                        }else {
                            console.debug("XFProcessor.applyChanges:", xmlEvent.type, " [", xmlEvent.contextInfo, "]");

                            /*
                             if 'logEvents' is true the eventlog from the server will be written
                             to DOM and can be viewed in a separate expandable section in the window.
                             */
                            //todo: the following code should be made a behavior only if debugging is available
                            //probably we have to add a private _applyChanges method to do the actual work to
                            //allow us to hook to this function and connect to it for outputting debug output.
                            if(fluxProcessor.logEvents){
                                //iterate contextinfo
                                var contextInfo = xmlEvent.contextInfo;
                                var tableCells = "";

                                for (dataItem in contextInfo){
                                    var funcArg = contextInfo[dataItem];

                                    //suppressing empty default info
                                    if(funcArg != null) {
                                        if(dataItem == "targetId" &&
                                            (xmlEvent.type == "betterform-state-changed" ||
                                                xmlEvent.type == "xforms-value-changed" ||
                                                xmlEvent.type == "xforms-valid" ||
                                                xmlEvent.type == "xforms-invalid" ||
                                                xmlEvent.type == "xforms-readonly" ||
                                                xmlEvent.type == "xforms-readwrite" ||
                                                xmlEvent.type == "xforms-required" ||
                                                xmlEvent.type == "xforms-optional" ||
                                                xmlEvent.type == "xforms-enabled" ||
                                                xmlEvent.type == "DOMFocusOut" ||
                                                xmlEvent.type == "DOMActivate" ||
                                                xmlEvent.type == "betterform-AVT-changed"
                                                )
                                            ){
                                            tableCells += "<tr><td class='propName'>"+ dataItem + "</td><td class='propValue'><a href='#' onclick='bf.devtool.reveal(this);'>" + contextInfo[dataItem] + "</a></td></tr>"
                                        }else if(dataItem == "targetElement" && xmlEvent.type == "betterform-load-uri"){
                                            var targetElement = contextInfo.xlinkTarget;
                                            tableCells += "<tr><td class='propName'>"+ dataItem + "</td><td class='propValue'><a href='#' onclick='bf.devtool.reveal(this);'>" + targetElement + "</a></td></tr>"
                                        }
                                        else {
                                            tableCells += "<tr><td class='propName'>"+ dataItem + "</td><td class='propValue'>" +  contextInfo[dataItem] + "</td></tr>"
                                        }
                                    }
                                }
                                //create output
                                domConstruct.create("li", {
                                    innerHTML: "<a href='#' onclick='bf.devtool.toggleEntry(this);'><span>"+xmlEvent.type+"</span></a><table class='eventLogTable'>" + tableCells + "</table>"
                                }, eventLog);
                            }

                            switch (xmlEvent.type) {
                                case "betterform-index-changed"      : fluxProcessor._handleBetterFormIndexChanged(xmlEvent); break;
                                case "betterform-insert-itemset"     : fluxProcessor._handleBetterFormInsertItemset(xmlEvent); break;
                                case "betterform-insert-repeatitem"  : fluxProcessor._handleBetterFormInsertRepeatItem(xmlEvent); break;
                                case "betterform-item-deleted"       : fluxProcessor._handleBetterFormItemDeleted(xmlEvent); break;
                                case "betterform-load-uri"           : fluxProcessor._handleBetterFormLoadURI(xmlEvent); break;
                                case "betterform-render-message"     : fluxProcessor._handleBetterFormRenderMessage(xmlEvent); break;
                                case "betterform-replace-all"        : fluxProcessor._handleBetterFormReplaceAll(); break;
                                case "betterform-state-changed"      : fluxProcessor._handleBetterFormStateChanged(xmlEvent); break;
                                case "betterform-item-changed"      : fluxProcessor._handleBetterFormItemChanged(xmlEvent); break;
                                case "betterform-dialog-open"        : fluxProcessor._handleBetterFormDialogOpen(xmlEvent); break;
                                case "betterform-dialog-close"       : fluxProcessor._handleBetterFormDialogClose(xmlEvent); break;
                                case "betterform-AVT-changed"        : fluxProcessor._handleAVTChanged(xmlEvent);break;
                                case "betterform-instance-created"   : fluxProcessor._handleInstanceCreated(xmlEvent);break;
                                case "betterform-model-removed"      : fluxProcessor._handleModelRemoved(xmlEvent);break;
                                case "betterform-exception"          : fluxProcessor._handleBetterformException(xmlEvent); break;
                                case "upload-progress-event"         : fluxProcessor._handleUploadProgressEvent(xmlEvent); break;
                                case "xforms-focus"                  : fluxProcessor._handleXFormsFocus(xmlEvent); break;
                                case "xforms-help"                   : fluxProcessor._handleShowHelp(xmlEvent); break;
                                case "xforms-hint"                   : fluxProcessor._handleXFormsHint(xmlEvent); break;
                                case "xforms-link-exception"         : fluxProcessor._handleLinkException(xmlEvent); break;
                                case "betterform-switch-toggled"     : connect.publish("bf-switch-toggled-" + xmlEvent.contextInfo.targetId, xmlEvent.contextInfo); break;
                                case "betterform-script-action"      : eval(xmlEvent.contextInfo["script"]); break;
                                case "xforms-value-changed"          : /* console.debug(xmlEvent); */ break;
                                case "xforms-version-exception"      : fluxProcessor._handleVersionException(xmlEvent); break;
                                case "xforms-binding-exception"      : fluxProcessor._handleBindingException(xmlEvent);break;
                                case "xforms-submit-error"           : fluxProcessor._handleSubmitError(xmlEvent); break;
                                case "DOMFocusIn"                    : fluxProcessor.lastServerClientFocusEvent = {postponedFunction:fluxProcessor._handleDOMFocusIn, postponedXmlEvent:xmlEvent}; break;    //cache the xmlEvent for being processed later
                                case "xforms-out-of-range"           : fluxProcessor._handleOutOfRange(xmlEvent);break;
                                case "xforms-in-range"               : fluxProcessor._handleInRange(xmlEvent);break;
                                case "xforms-invalid"                : break;
                                case "xforms-valid"                  : validityEvents[index] = xmlEvent; index++;break;
                                case "betterform-id-generated"       : break;
                                case "DOMActivate"                   : break;
                                case "xforms-select"                 : break;
                                case "xforms-deselect"               : break;
                                case "DOMFocusOut"                   : break;
                                case "xforms-model-construct"        : /*console.info("xforms-model-construct-done")*/;break;
                                case "xforms-model-construct-done"   : /*console.info("xforms-model-construct-done");*/fluxProcessor._buildUI();  break;
                                case "xforms-ready"                  : /*console.info("xforms-ready");*/fluxProcessor.isReady = true;connect.publish("xforms-ready", []);break; //not perfect - should be on XFormsModelElement
                                case "xforms-submit"                 : break;
                                case "xforms-submit-done"            : fluxProcessor._handleSubmitDone(xmlEvent);break;
                                /* Unknow XMLEvent: */
                                default                              : console.error("Event " + xmlEvent.type + " unknown [Event:", xmlEvent, "]"); break;
                            }

                        }
                    }
                );

                if(fluxProcessor.logEvents){
                    // add a devider for eventLogViewer
                    domConstruct.create("li", {
                        innerHTML: "<span class='logDevider'/>"
                    }, eventLog);
                }

                if (validityEvents.length > 0) {
                    fluxProcessor._handleValidity(validityEvents);
                }
            }
            catch(ex) {
                fluxProcessor._handleExceptions("An error occurred during applyChanges ", ex);
            }
            //The pending request tournaround has been completed
            fluxProcessor.requestPending = false;
            //Schedule the next FIFO-Read try in 0 ms
            clearTimeout(fluxProcessor.fifoReaderTimer);
            fluxProcessor.fifoReaderTimer = setTimeout("fluxProcessor.eventFifoReader()", 0);
        },

        _buildUI : function(){
            require(["bf/MappingProcessor","dojo/behavior", "dojo/domReady!"],function(MappingProcessor,behavior){
                if (this.mappingProcessor == undefined) {
                    this.mappingProcessor = new MappingProcessor();
                }
                behavior.apply();
            });

            // fire remaining initial events
            require(["dojo/ready"], function (ready) {
                ready(function () {
                    // console.debug("missing initial Events:",fluxProcessor.initialEvents);
                    fluxProcessor._uiReady = true;
                    fluxProcessor.applyChanges(fluxProcessor.initialEvents);
                    fluxProcessor.initialEvents = null;
                    var formWrapper = dom.byId("formWrapper");
                    // console.debug("formWrapper:",formWrapper);
                    domStyle.set(formWrapper,"display","block");
                });
            });
        },

        _handleAVTChanged:function(xmlEvent){
            // console.debug("XFProcessor._handleAVTChanged xmlEvent:",xmlEvent);
            domAttr.set(xmlEvent.contextInfo.targetId, xmlEvent.contextInfo.attribute, xmlEvent.contextInfo.value);
        },

        _handleInstanceCreated:function(xmlEvent){
            // console.debug("XFProcessor._handleInstanceCreated xmlEvent:",xmlEvent);
            // TODO: Lars: add animation again
            // dojo.require("dojox.fx");
            var debugPane = dom.byId("bfDebugLinks");

            if(debugPane != null){
                var contextroot = domAttr.get(dom.byId("bfDebug"),"context");
                var newLink = document.createElement("a");
                domAttr.set(newLink,"href",contextroot + xmlEvent.contextInfo.modelId + "/" + xmlEvent.contextInfo.instanceId);
                domAttr.set(newLink,"target","_blank");
                domAttr.set(newLink,"modelId",xmlEvent.contextInfo.modelId);
                var linkText = document.createTextNode("Model:" + xmlEvent.contextInfo.modelId + " :: " + "Instance:" + xmlEvent.contextInfo.instanceId);
                newLink.appendChild(linkText);
                debugPane.appendChild(newLink);
                // dojox.fx.highlight({node:newLink, color:'#999999', duration:600}).play()
            }
        },

        _handleModelRemoved:function(xmlEvent){
            // console.debug("XFProcessor._handleModelRemoved xmlEvent:",xmlEvent);
            var modelId = xmlEvent.contextInfo.modelId;
            require(["dojo/query", "dojo/NodeList-manipulate"], function(query){
                query("#bfDebug a[modelId='" + modelId +"']").remove();
            });
        },

        _handleValidity:function(validityEvents) {
            // console.debug("XFProcessor._handleValidity validityEvents:",validityEvents);
            array.forEach(validityEvents, function(xmlEvent) {
                var control = registry.byId(xmlEvent.contextInfo.targetId);
                if (control != undefined) {
                    if (xmlEvent.type == "xforms-valid") {
                        control.setValid();
                    } else {
                        control.setInvalid();
                    }
                }
            });
        },


        /*
         ******************************************************************************************************
         * handles XForms binding exception
         ******************************************************************************************************
         */
        _handleBindingException:function(xmlEvent) {
            //todo: must be reviewed completely
            console.debug("XFProcessor._handleBindingException xmlEvent:",xmlEvent);
            console.warn("xforms-binding-exception at " + xmlEvent.contextInfo.targetId + " - " + xmlEvent.contextInfo.defaultinfo);
        },

        /*
         ******************************************************************************************************
         * handles XForms Version exception
         ******************************************************************************************************
         */
        _handleVersionException:function(xmlEvent) {
            //todo: must be reviewed completely
            console.error(xmlEvent.contextInfo.errorinformation);
        },

        /*
         ******************************************************************************************************
         * handles arbitrary exception occuring on server-side and write them our to DOM
         ******************************************************************************************************
         */
        _handleBetterformException:function(xmlEvent) {
            console.debug("XFProcessor._handleBetterformException xmlEvent:",xmlEvent);
            var description = xmlEvent.contextInfo.message;
            console.error(xmlEvent.contextInfo.message);
            var exception = dom.byId('betterFORM-exception');
            var log;
            var exceptionText;
            if (!exception) {
                log = document.createElement('div');
                log.id = 'betterFORM-exceptionLog';
                document.body.appendChild(log);
                exception = document.createElement('exception');
                exception.id = 'betterFORM-exception';
                exceptionText = document.createTextNode(description);
                exception.appendChild(exceptionText);
                log.appendChild(exception);
            } else {
                exception.removeChild(exception.firstChild);
                exceptionText = document.createTextNode(description);
                exception.appendChild(exceptionText);
            }
        },

        /*
         ******************************************************************************************************
         * handles XForms submit error by publishing xforms-invalid to all invalid controls and adding
         * a class 'xfRequiredEmpty' to required controls that have no value
         ******************************************************************************************************
         */
        _handleSubmitError:function(xmlEvent) {
            // console.warn("xforms-submit-error at ", xmlEvent.contextInfo);
            query(".xfInvalid", win.body()).forEach(function(control) {
                // console.debug("_handleSubmitError: invalid control: ", control);
                connect.publish("xforms-invalid", [domAttr.get(control, "id"),"submitError"]);
            });
            query(".xfRequired", win.body()).forEach(function(control) {
                //if control has no value add CSS class xfRequiredEmpty
                // console.debug("check required: control: ", control);
                var xfControl = registry.byId(control.id);
                // console.debug("found Control Widget: xfControl: ", xfControl, " typeof xfControl.getControlValue  == 'function'': ", typeof xfControl.getControlValue == 'function');
                if(xfControl != undefined && typeof xfControl.getControlValue == 'function'){
                    var xfValue = xfControl.getControlValue();
                    if(xfValue == undefined || xfValue == ''){
                        domClass.add(xfControl.domNode,"xfRequiredEmpty");
                    }
                }
            });
        },


        /*
         ******************************************************************************************************
         * handles the XForms load actions including the embedding of subforms.
         ******************************************************************************************************
         */
        _handleBetterFormLoadURI:function(/*XMLEvent*/ xmlEvent) {
            console.debug("XFProcessor._handleBetterFormLoadURI xmlEvent:",xmlEvent);

            // xf:load show=replace
            if (xmlEvent.contextInfo.show == "replace") {
                fluxProcessor.skipshutdown = true;
                window.location.href = xmlEvent.contextInfo.uri;
            }
            // xf:load show=new
            else if (xmlEvent.contextInfo.show == "new") {
                window.open(xmlEvent.contextInfo.uri, '_betterform', 'menubar=yes,toolbar=yes,location=yes,directories=yes,fullscreen=no,titlebar=yes,hotkeys=yes,status=yes,scrollbars=yes,resizable=yes');

            }
            /* xf:load show=embed
             to embed an existing form into the running form
             */
            else if (xmlEvent.contextInfo.show == "embed") {
//            console.debug("xmlEvent.contextInfo.show='embed'", this);
                // getting target from event - can be either the value of a 'name' or 'id' Attribute
                var xlinkTarget = xmlEvent.contextInfo.xlinkTarget;

                //determine the DOM Element in the client DOM which is the target for embedding
                var targetid;
                if (dom.byId(xlinkTarget) != undefined) {
                    targetid = xlinkTarget;
                } else {
                    // if we reach here the xlinkTarget is no idref but the value of a name Attrbute that needs resolving
                    // to an id.
                    var tmp = query("*[name='" + xlinkTarget + "']")[0];
                    targetid = tmp.id;
                    console.debug("target id for embedding is: ", targetid);
                }

                this._unloadDOM(targetid);

                //get original Element in master DOM
                var htmlEntryPoint = dom.byId(targetid);
                htmlEntryPoint.innerHTML = xmlEvent.contextInfo.targetElement;
                domAttr.set(htmlEntryPoint, "id", xlinkTarget + "Old");
                var nodesToEmbed = dom.byId(targetid);

//            require("dojo/parser", function(parser){
//                parser.parse(htmlEntryPoint);
//            });
                // dojo.parser.parse(htmlEntryPoint);

                domConstruct.place(nodesToEmbed, htmlEntryPoint, "before");
//            dojo.fx.wipeIn({node: nodesToEmbed,duration: 500}).play();
                domStyle.set(nodesToEmbed,"display","block");

                //copy classes from mountpoint
                var classes = domAttr.get(htmlEntryPoint, "class");
                domAttr.set(nodesToEmbed, "class", classes);

                htmlEntryPoint.parentNode.removeChild(htmlEntryPoint);

                var self = this;
                // console.debug("\n\nTarget ID: ",targetid);
                require(["dojo/behavior"],function(behavior) {
                    // console.debug("htmlEntryPoint:",nodesToEmbed);
                    self.bfDialogs[targetid] = new Array();
                    query(".bfcDialog", nodesToEmbed).forEach(function(item) {
                        // console.debug("\n\nAdd Dialog:",domAttr.get(item,"id"));
                        var id = domAttr.get(item,"id");
                        self.bfDialogs[targetid].push(id);
                        if (registry.byId(id) !=  undefined) {
                            item.parentNode.removeChild(item);
                        }
                    });
                    behavior.apply();
                });
                var contextInfo = xmlEvent.contextInfo;
                require(["dojo/ready"], function (ready) {
                    ready(function () {
                        var utilizedEvents = contextInfo.utilizedEvents;
                        // console.debug("xmlEvent.contextInfo.utilizedEvents:",xmlEvent.contextInfo.utilizedEvents);
                        if(utilizedEvents && utilizedEvents != ""){
                            var utilizedEventsObj =  json.fromJson("{" + utilizedEvents +  "}");
                            // console.debug("utilizedEventsObj:",utilizedEventsObj);
                            if(utilizedEventsObj.useXFSelect){
                                self.useXFSelect = true;
                            }
                            if(utilizedEventsObj.useDOMFocusIN){
                                self.useDOMFocusIN = true;
                            }
                            if(utilizedEventsObj.useDOMFocusOUT){
                                self.useDOMFocusOUT = true;
                            }
                        }

                        // finally dynamically load the CSS (if some) form the embedded form
                        var cssToLoad = contextInfo.inlineCSS;
//            console.debug("css to load: ", cssToLoad);
                        var headID = document.getElementsByTagName("head")[0];
                        var mountpoint = dom.byId(xlinkTarget);

                        if(cssToLoad != undefined && cssToLoad != ""){
                            //console.debug("adding Style: ", cssToLoad);
                            var stylesheet1 = document.createElement('style');
                            domAttr.set(stylesheet1,"type", "text/css");
                            domAttr.set(stylesheet1,"name", xlinkTarget);
                            var head1 = document.getElementsByTagName('head')[0];
                            head1.appendChild(stylesheet1);
                            if (stylesheet1.styleSheet) {   // IE
                                stylesheet1.styleSheet.cssText = cssToLoad;
                            } else {                // the world
                                var textNode1 = document.createTextNode(cssToLoad);
                                stylesheet1.appendChild(textNode1);
                            }
                        }

                        var externalCssToLoad = contextInfo.externalCSS;

                        if (externalCssToLoad != undefined && externalCssToLoad != "") {
                            var styles = externalCssToLoad.split('#');
                            var head2 = document.getElementsByTagName('head')[0];
                            for (var i = 0; i <= styles.length; i = i+1) {
                                if (styles[i] != undefined && styles[i] != "") {
                                    // console.debug("adding Style: ", styles[i]);
                                    var stylesheet2 = document.createElement('link');
                                    domAttr.set(stylesheet2,"rel","stylesheet");
                                    domAttr.set(stylesheet2,"type","text/css");
                                    domAttr.set(stylesheet2,"href",styles[i]);
                                    domAttr.set(stylesheet2,"name",xlinkTarget);
                                    head2.appendChild(stylesheet2);
                                }
                            }
                        }

                        var inlineJavaScriptToLoad = contextInfo.inlineJavascript;
                        if (inlineJavaScriptToLoad != undefined && inlineJavaScriptToLoad != "") {
                            //console.debug("adding script: ", inlineJavaScriptToLoad);
                            var javascript1 = document.createElement('script');
                            domAttr.set(javascript1,"type", "text/javascript");
                            domAttr.set(javascript1,"name", xlinkTarget);
                            var head3 = document.getElementsByTagName('head')[0];
                            head3.appendChild(javascript1);
                            javascript1.text = inlineJavaScriptToLoad;
                        }

                        var externalJavaScriptToLoad = contextInfo.externalJavascript;
                        if (externalJavaScriptToLoad != undefined && externalJavaScriptToLoad != "") {
                            var scripts = externalJavaScriptToLoad.split('#');
                            var head4 = document.getElementsByTagName("head")[0];
                            for (var z = 0; z <= scripts.length; z = z+1) {
                                if (scripts[z] != undefined && scripts[z] != "") {
                                    //console.debug("adding script: ", scripts[z]);
                                    var javascript2 = document.createElement('script');
                                    domAttr.set(javascript2,"type","text/javascript");
                                    domAttr.set(javascript2,"src",scripts[z]);
                                    domAttr.set(javascript2,"name",xlinkTarget);
                                    head4.appendChild(javascript2);
                                }
                            }
                        }
                    })
                })
            }
            /*  xf:load show=none
             to unload (loaded) subforms
             */
            else if (xmlEvent.contextInfo.show == "none") {
                // console.debug("XFProcessor._handleBetterFormLoadURI: htmlEntryPoint", htmlEntryPoint);
                this._unloadDOM(xmlEvent.contextInfo.xlinkTarget);
            }
            else {
                console.error("betterform-load-uri show='" + xmlEvent.contextInfo.show + "' unknown!");
            }


        },

        _unloadDOM:function(target) {

            //delete CSS specific to subform
            var htmlEntryPoint = dom.byId(target);
            if (htmlEntryPoint == undefined) {
                return;
            }

            var styleList = document.getElementsByTagName("style");
            //console.debug("styleList" , styleList);
            if (styleList != undefined) {
                array.forEach(styleList, function(item) {
                    //console.debug("style: ", item);
                    if (item != undefined) {
                        if(domAttr.get(item,"name") == target){
                            //console.debug("removing style: ", item);
                            //console.debug("parentNode: ", item.parentNode);
                            item.parentNode.removeChild(item);
                        }
                    }
                });
            }

            /*
             unload previously loaded subform-specific stylesheets
             */
            var externalStyleList = document.getElementsByTagName("link");
            // console.debug("XFProcessor._unloadDOM: styleList" , externalStyleList);
            if (externalStyleList != undefined) {
                array.forEach(externalStyleList, function(item) {
                    //console.debug("style: ", item);
                    if (item != undefined) {
                        if(domAttr.get(item,"name") == target){
                            console.debug("removing style: ", item);
                            console.debug("parentNode: ", item.parentNode);
                            item.parentNode.removeChild(item);
                        }
                    }
                });
            }

            /*
             unload previously loaded subform-specific Javascripts
             */
            var scriptList = document.getElementsByTagName("script");
            //console.debug("scriptList" , scriptList);
            if (scriptList != undefined) {
                array.forEach(scriptList, function(item) {
                    //console.debug("script: ", item);
                    if (item != undefined) {
                        if(domAttr.get(item,"name") == target){
                            //console.debug("removing: ", item);
                            //console.debug("parentNode: ", item.parentNode);
                            item.parentNode.removeChild(item);
                        }
                    }
                });
            }

            var widgetID = "widgetid";
            if (has("ie") >= 5) {
                widgetID = "widgetId"
            }

            /*
             destroy all child dijits within subform tree
             */
            var widgets = query("*[" + widgetID + "]", htmlEntryPoint);
            var self = this;
            array.forEach(widgets,
                function(item) {
                    // console.debug("XFProcessor._unloadDOM item:",item);
                    if (item != undefined) {
                        var itemId = domAttr.get(item, 'id');
                        var childDijit = registry.byId(itemId);
                        if (childDijit != undefined) {
                            // console.debug("XFProcessor._unloadDOM: destroy itemId: ",itemId, " dijit:", childDijit);
                            self.removeSubscribers(itemId);
                            childDijit.destroy();
                        } else {
                            var dijitId = domAttr.get(item, widgetID);
                            // console.debug("XFProcessor._unloadDOM: ChildDijit is null; dijitId:",dijitId);
                            self.removeSubscribers(dijitId);
                            childDijit = registry.byId(dijitId);
                            if (childDijit != undefined) {
                                childDijit.destroy();
                            }
                        }

                    }
                }
            );
            // console.debug("Target id: ",target);
            if(this.bfDialogs && this.bfDialogs[target] && this.bfDialogs[target].length > 0){
                // console.debug("dialogs to remove: ",this.bfDialogs[target]);
                array.forEach(this.bfDialogs[target],function(id){
                    // console.debug("dialog to find and delete: ", id);
                    var dialogDijit = registry.byId(id);
                    if(dialogDijit){
                        // console.debug("destroy dialogDijit: ",dialogDijit);
                        dialogDijit.destroy();
                    }
                });
                delete this.bfDialogs[target];
            }
            // console.info("XFProcessor._unloadDOM AFTER REMOVING SUBSCRIBERS: :",this.subscribers);
            while (htmlEntryPoint.hasChildNodes()) {
                // console.debug("XFProcessor._unloadDOM: hasChildNodes START");
                // console.dirxml(htmlEntryPoint.firstChild);
                // console.debug("XFProcessor._unloadDOM: hasChildNodes END");
                htmlEntryPoint.removeChild(htmlEntryPoint.firstChild);
            }
        },
        /*
         ******************************************************************************************************
         * handles XForms xforms-submit-done events
         ******************************************************************************************************
         */
        _handleSubmitDone:function(xmlEvent) {
            // console.debug("XFProcessor._handleSubmitDone xmlEvent:",xmlEvent);

            if (xmlEvent.contextInfo.document != null) {
                //***** handle submission replace="new" *****
                //***** handle submission replace="new" *****
                //***** handle submission replace="new" *****
                var doc = xmlEvent.contextInfo.document;
                var newWindow = window.open();
                newWindow.document.write(doc);
                newWindow.document.close();
            } else if (xmlEvent.contextInfo.embedElement != null) {
                //*****   handle submission replace="embedHTML" *****
                //*****   handle submission replace="embedHTML" *****
                //*****   handle submission replace="embedHTML" *****
                if (xmlEvent.contextInfo.embedTarget == undefined) {
                    return;
                }
                var target = xmlEvent.contextInfo.embedTarget;
                var content = xmlEvent.contextInfo.embedElement;
                // console.debug("handle Embedding target:",target, " content:", content);

                //determine the DOM Element in the client DOM which is the target for embedding
                var targetid;
                if (dom.byId(target) != undefined) {
                    targetid = target;
                } else {
                    // if we reach here the target is no idref but the value of a name Attrbute that needs resolving
                    // to an id.
                    var tmp = query("*[name='" + target + "']")[0];
                    targetid = tmp.id;
                    console.debug("target id for embedding is: ", targetid);
                }

                this._unloadDOM(targetid);

                //get original Element in master DOM
                var htmlEntryPoint = dom.byId(targetid);
                htmlEntryPoint.innerHTML = content;

                require(["dojo/parser","dojo/behavior"], function(parser,behavior){
                    parser.parse(htmlEntryPoint);
                    behavior.apply();
                });
            }
        },

        /*
         ******************************************************************************************************
         * handles XForms message actions
         ******************************************************************************************************
         */
        _handleBetterFormRenderMessage:function(/*XMLEvent*/ xmlEvent) {
            console.debug("XFProcessor._handleBetterFormRenderMessage xmlEvent:", xmlEvent);
            var message = xmlEvent.contextInfo.message;
            var level = xmlEvent.contextInfo.level;
            //console.debug("XFProcessor.handleRenderMessage: message='" + message + "', level='" + level + "'");
            if (level == "ephemeral") {
                require(["dojox/widget/Toaster"],function(Toaster) {
                    if(registry.byId("betterformMessageToaster") == undefined) {
                        new Toaster({id:"betterformMessageToaster",
                            positionDirection:"bl-up",
                            duration:"8000",
                            messageTopic:'bfMessageTopic'
                        },"betterformMessageToaster");
                    }
                    connect.publish("bfMessageTopic", [ {
                        message: message,
                        type: "fatal",
                        duration: 8000
                    }]);
                })
            }
            else {
                var exception = xmlEvent.contextInfo.exception;
                if (exception != undefined) {
                    console.warn("An Exception occured in Facade: ", exception);
                } else {
                    alert(message);
                }
                console.debug("finished _handleBetterFormRenderMessage");
            }
        },

        /*
         ******************************************************************************************************
         * handles XForms xforms-out-of-range events
         ******************************************************************************************************
         */
        _handleOutOfRange:function(xmlEvent) {
            /*
             var message = "Value for ui control '" + xmlEvent.contextInfo.targetName + "' (id:"+xmlEvent.contextInfo.targetId+") is out of range";
             registry.byId("betterformMessageToaster").setContent(message,'message');
             registry.byId("betterformMessageToaster").show();
             */
            var uiControl = dom.byId(xmlEvent.contextInfo.targetId + "-value");
            if (uiControl != undefined) {
                if (domClass.contains(uiControl, "xfInRange")) {
                    domClass.remove(uiControl, "xfInRange");
                }
                domClass.add(uiControl, "xfOutOfRange");
            }
        },

        /*
         ******************************************************************************************************
         * handles XForms xforms-in-range events
         ******************************************************************************************************
         */
        _handleInRange:function(xmlEvent) {
            console.debug("XFProcessor._handleInRange xmlEvent:", xmlEvent);
            var uiControl = dom.byId(xmlEvent.contextInfo.targetId + "-value");
            if (uiControl != undefined) {
                if (domClass.contains(uiControl, "xfOutOfRange")) {
                    domClass.remove(uiControl, "xfOutOfRange");
                }
                domClass.add(uiControl, "xfInRange");
            }
        },

        //todo: probably to be merged with '_handleSubmitDone'?
        _handleBetterFormReplaceAll:function() {
            console.debug("XFProcessor._handleBetterFormReplaceAll");
            fluxProcessor.skipshutdown = true;

            // add new parameter (params are located before the anchor sign # in an URI)
            var anchorIndex = window.location.href.lastIndexOf("#");
            var queryIndex = window.location.href.lastIndexOf("?");
            var path = window.location.href;
            if (anchorIndex != -1) {
                path = window.location.href.substring(0, anchorIndex);
            }
            if (queryIndex == -1) {
                path += "?";
            }
            path += "&submissionResponse=true&sessionKey=" + fluxProcessor.sessionKey;
            if (anchorIndex != -1) {
                path += window.location.href.substring(anchorIndex);
            }

            window.open(path, "_self");
        },


        //todo: should be moved into a behavior
        _handleBetterFormDialogOpen:function(/*XMLEvent*/ xmlEvent) {
            console.debug("XFProcessor._handleBetterformDialogOpen: targetId: '",xmlEvent.contextInfo.targetId,"' parentId: " , xmlEvent.contextInfo.parentId);
            var xfControlId =xmlEvent.contextInfo.targetId;
            // if XForms Control Dijit allready exists call show on selected control
            if(registry.byId(xfControlId) != undefined){
                registry.byId(xfControlId).show();
            }else {
                console.error("XFProcessor: error during betterform-dialog-show-event: targetId '",xmlEvent.contextInfo.targetId, "', xfControlId: '", xfControlId,"' does not exist");
            }
        },

        //todo: should be moved into a behavior
        _handleBetterFormDialogClose:function(/*XMLEvent*/ xmlEvent) {
            console.debug("XFProcessor._handleBetterformDialogClose: targetId: '",xmlEvent.contextInfo.targetId,"' parentId: " , xmlEvent.contextInfo.parentId);
            var xfControlId =xmlEvent.contextInfo.targetId;
            // if XForms Control Dijit allready exists call hide on selected control
            if(registry.byId(xfControlId) != undefined){
                registry.byId(xfControlId).hide();
            }else {
                console.error("XFProcessor: error during betterform-dialog-hide-event: targetId '",xmlEvent.contextInfo.targetId,"' does not exist");
            }
        },


        _handleBetterFormStateChanged:function(/*XMLEvent*/ xmlEvent) {
            var contextInfo = xmlEvent.contextInfo;
            // IMPORTANT: DOJO/READY MUST NOT BE REMOVED!
            //  Otherwise _handleBetterFormStateChanged will be called before _handleBetterFORMInsert is finished!
            require(["dojo/ready"], function(ready){
                ready(function(){
                    // console.debug("XFProcessor._handleBetterFormStateChanged: contextInfo: ", contextInfo);
                    var parentId = contextInfo.parentId;

                    // if contextInfo.parentId is present dojo must publish to this id instead of targetid (e.g. used for value changes of labels)
                    if(parentId) {
                        // console.debug("XFProcessor._handleBetterFormStateChanged: publish (parent): bf-state-change- ", parentId, " contextInfo:",contextInfo);
                        connect.publish("bf-state-change-"+ parentId, contextInfo);

                    }
                    else {
                        // console.debug("XFProcessor._handleBetterFormStateChanged: publish: bf-state-change- ", contextInfo.targetId, " contextInfo:",contextInfo);
                        connect.publish("bf-state-change-"+ contextInfo.targetId,contextInfo);
                    }
                });
            })
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


        //todo: should be moved to a behavior
        //todo: note that xforms-insert might not target repeat or itemset
        _handleBetterFormInsertRepeatItem:function(xmlEvent) {
            // console.debug("XFProcessor.betterform-insert-repeatitem [id: '", xmlEvent.contextInfo.targetId, "'] xmlEvent:",xmlEvent);
            connect.publish("betterform-insert-repeatitem-"+ xmlEvent.contextInfo.targetId, xmlEvent.contextInfo);
        },

        _handleBetterFormInsertItemset:function(xmlEvent) {
            // console.debug("betterform-insert-itemset [id: '", xmlEvent.contextInfo.parentId+"-value", " / contextInfo:",xmlEvent.contextInfo,']' );
            connect.publish("betterform-insert-item-"+ xmlEvent.contextInfo.parentId + "-value", xmlEvent.contextInfo);
        },
        _handleBetterFormItemDeleted:function(xmlEvent) {
            // console.debug("handle betterform-item-deleted for ", xmlEvent.contextInfo.targetName, " [id: '", xmlEvent.contextInfo.targetId, "'] xmlEvent:", xmlEvent);
            if (xmlEvent.contextInfo.targetName == "itemset") {
                connect.publish("betterform-delete-item-"+ xmlEvent.contextInfo.parentId + "-value", xmlEvent.contextInfo);
            }else {
                connect.publish("betterform-item-deleted-"+ xmlEvent.contextInfo.targetId, xmlEvent.contextInfo);
            }
        },

        //todo: move to repeat behavior
        _handleBetterFormIndexChanged:function(xmlEvent) {
            // console.debug("XFProcessor._handleBetterFormIndexChanged xmlEvent:",xmlEvent);
            connect.publish("betterform-index-changed-"+xmlEvent.contextInfo.targetId, xmlEvent.contextInfo);

        },

        _handleUploadProgressEvent:function(xmlEvent) {
            console.debug("XFProcessor._handleUploadProgressEvent: xmlEvent:",xmlEvent);
            var xfControlId = xmlEvent.contextInfo.targetid;
            // if XForms Control Dijit allready exists call handleStateChanged on selected control
            if (registry.byId(xfControlId+"-value") != undefined) {
                registry.byId(xfControlId+"-value").updateProgress(xmlEvent.contextInfo.progress);
            } else {
                console.error("error during upload-progress-event: targetId " + xmlEvent.contextInfo.targetId + " does not exist");
            }
        },
        _handleXFormsFocus:function(xmlEvent) {
            console.debug("xforms-focus xmlEvent: ", xmlEvent);
            try {
                var targetName = xmlEvent.contextInfo.targetName;
                if (targetName != "group" && targetName != "repeat" && targetName != "switch" && targetName != "case") {
                    var controlToFocus = registry.byId(xmlEvent.contextInfo.targetId + "-value");
                    if(!controlToFocus){
                        controlToFocus = dom.byId(xmlEvent.contextInfo.targetId + "-value");
                    }
                    if(controlToFocus && controlToFocus.focus){
                        controlToFocus.focus();
                    }else {
                        console.warn("Control " + xmlEvent.contextInfo.targetId + " does not exist");
                    }
                }
            }
            catch(ex) {
                fluxProcessor._handleExceptions("error during xforms-focus: targetId " + xmlEvent.contextInfo.targetId + " does not exist - Exception:", ex);
            }

        },

        _handleDOMFocusIn:function(xmlEvent) {
            console.debug("XFProcessor._handleDOMFocusIn xmlEvent:",xmlEvent);
            xfControlId = xmlEvent.contextInfo.targetId + "-value";
            if (registry.byId(xfControlId) != undefined) {
                // console.debug("dom-focus-in-dijit control: ",xfControlId);
                registry.byId(xfControlId)._handleDOMFocusIn();
            } else if (dom.byId(xfControlId) != undefined) {
                // console.debug("dom-focus-in-dojo control: ",xfControlId);
                var domControlValue = dom.byId(xfControlId);
                domControlValue.focus();
            } else {
                console.warn("XFProcessor._handleDOMFocusIn no Element found for id:", xfControlId, " might have been destroyed");
            }
        },

        _handleXFormsHint:function(xmlEvent) {
            console.debug("XFProcessor._handleXFormsHint xmlEvent:",xmlEvent);
            var xfControlId = xmlEvent.contextInfo.targetId;
            var message = domAttr.get(dom.byId(xfControlId + "-value"), "title");
            registry.byId("betterformMessageToaster").setContent(message, 'message');
            registry.byId("betterformMessageToaster").show();
        },

        _handleShowHelp:function(xmlEvent) {
            console.debug("XFProcessor._handleShowHelp xmlEvent:",xmlEvent);
            fluxProcessor.currentControlId = xmlEvent.contextInfo.targetId;
            fluxProcessor.showHelp();
        },

        _handleLinkException:function(xmlEvent) {
            console.debug("XFProcessor._handleLinkException xmlEvent:",xmlEvent);
            console.error("Fatal error - " + xmlEvent.type + ": Failed to load resource: " + xmlEvent.contextInfo.resourceUri);
            //        fluxProcessor.closeSession();
        },

        fetchProgress:function(id, fileName) {
            console.debug("XFProcessor.fetchProgress id:", id);
            try {
                console.debug("XFProcessor.fetchProgress id:", id, "fileName: " , fileName , " this.sessionKey:", this.sessionKey);
                Flux.fetchProgress(id, fileName, this.sessionKey, this.applyChanges);
            }
            catch(ex) {
                fluxProcessor._handleExceptions("Failure executing Flux.fetchProgress", ex);
            }
        },

        /*
         @param: String locale
         */

        setLocale:function(locale) {
            console.debug("XFProcessor.setLocale: Changed locale to: " + locale);
            try {
                Flux.setLocale(locale, this.sessionKey, this.applyChanges);
            }
            catch(ex) {
                fluxProcessor._handleExceptions("Failure executing Flux.setLocale", ex);
            }
        },

        showHelp:function(id) {
            console.debug("showng help for:", id);
            var helpCtrl = dom.byId(id + '-help');
            if (helpCtrl == undefined) {
                console.warn("No help available for Control Id: '" + id + "'");
                return;
            }
            var helpText = dom.byId(id + "-help-text");
            var currentState = domStyle.get(helpText,"display");

            if(currentState == "none"){
                domStyle.set(helpText, "display","inline-block");
            }else{
                domStyle.set(helpText, "display","none");
            }
            //make sure that the input control at work does not loose the focus
//        dom.byId(id).focus();
        },

        getInstanceDocument:function(modelId, instanceId){
            console.debug("XFProcessor.getInstanceDocument modeId:", modelId, " instanceId:",instanceId);
            dwr.engine.setErrorHandler(this._handleExceptions);
            Flux.getInstanceDocument(modelId, instanceId, this.sessionKey,this.printInstance);
        },

        printInstance:function(data){
            console.debug("XFProcessor.printInstance data:", data);
            // console.dirxml(data);
            dom.byId("debugFrame").innerHTML=data;
        },

        addSubscriber:function(id, handle){
            if(this.subscribers[id] == undefined) {
                this.subscribers[id] = new Array();
            }
            this.subscribers[id].push(handle);
        },
        removeSubscribers:function(id){
            // console.warn("removing subscribers for id: ", id, " subcribers: ",this.subscribers[id]);
            if(this.subscribers[id]!=undefined){
                array.forEach(this.subscribers[id], function(handle) {
                    // console.debug("removing handle:",handle);
                    connect.unsubscribe(handle);
                });
                delete this.subscribers[id];
            }

        }
    })
});
