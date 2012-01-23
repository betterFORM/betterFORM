/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.FluxProcessor");

/**
 All Rights Reserved.
 @author Joern Turner
 @author Lars Windauer

 This class represents the interface to the remote XForms processor (aka 'betterForm Web') with DWR. It is the only class
 actually having dependency on DWR to handle the AJAX part of things and calling remote Java methods on
 de.betterform.web.betterform.FluxFacade.
 **/

dojo.declare("betterform.FluxProcessor", betterform.XFormsProcessor,
{
    sessionKey:"",
    dataPrefix:"",
    skipshutdown:false,
    isDirty:false,
    factory:null,
    currentControlId:"",
    unloadMsg:"You are about to leave this XForms application",
    webtest:'@WEBTEST@',
    isReady:false,
    contextroot:"",
    defaultAlertHandler:null,
    subscribers:[],
    clientServerEventQueue:[],
    requestPending:false,
    fifoReaderTimer:null,
    lastServerClientFocusEvent:null,
    _earlyTemplatedStartup:true,
    widgetsInTemplate:true,
    usesDOMFocusIN:false,
    logEvents:false,


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
        /*
         var fluxAttribute = function(attribute) {
         return dojo.attr(dojo.byId("fluxProcessor"), attribute);
         };
         */
        // This is used for referencing this object from within ajax-callback-functions
        this.indicatorContainer = document.getElementById('bfLoading');
        this.indicatorImage = document.getElementById('indicator');
        this.indicatorImage.className = 'xfDisabled';

        // Initialize the clientServerEventQueue for immediately being able to append Elements
        this.clientServerEventQueue = new Array();
        this.factory = new betterform.ui.UIElementFactory();
        if (this.webtest != 'true') {
            dojo.connect(window, "onbeforeunload", this, "handleUnload");
            dojo.connect(window, "onunload", this, "close");
        }
        this.skipshutdown = false;

        //#########    ALERT IMPLEMENTATION  #############
        //#########    ALERT IMPLEMENTATION  #############

        var globalAlertEnabled = dojo.query(".GlobalAlert", dojo.doc)[0];
        if (globalAlertEnabled != undefined) {
            dojo.require("betterform.ui.common.GlobalAlert");
            this.defaultAlertHandler = new betterform.ui.common.GlobalAlert({});
            console.warn("!! WARNING: GLOBAL ALERT HANDLER NOT IMPLEMENTED YET !!!");
        }

        var bowlAlertEnabled = dojo.query(".BowlAlert", dojo.doc)[0];
        if (bowlAlertEnabled != undefined) {
            dojo.require("betterform.ui.common.BowlAlert");
            this.defaultAlertHandler = new betterform.ui.common.BowlAlert({});
            console.warn("!! WARNING: BOWL ALERT HANDLER NOT IMPLEMENTED YET !!!");
        }

        var inlineRoundBordersAlertEnabled = dojo.query(".InlineRoundBordersAlert", dojo.doc)[0];
        if (inlineRoundBordersAlertEnabled != undefined) {
            this.defaultAlertHandler = new betterform.ui.common.InlineRoundBordersAlert({});
        }

        var inlineAlertEnabled = dojo.query(".InlineAlert", dojo.doc)[0];
        if (inlineAlertEnabled != undefined) {
            dojo.require("betterform.ui.common.InlineAlert");
            this.defaultAlertHandler = new betterform.ui.common.InlineAlert({});
            console.debug("Enabled InlineAlert Handler ", this.defaultAlertHandler);

        }

        var toolTipAlertEnabled = dojo.query(".ToolTipAlert", dojo.doc)[0];
        if (toolTipAlertEnabled != undefined || (this.defaultAlertHandler == undefined)) {
            dojo.require("betterform.ui.common.ToolTipAlert");
            this.defaultAlertHandler = new betterform.ui.common.ToolTipAlert({});
            console.debug("Enabled ToolTipAlert Handler ", this.defaultAlertHandler);
        }

        this.subscribers[0] = dojo.subscribe("/xf/valid", this.defaultAlertHandler, "handleValid");
        this.subscribers[1] = dojo.subscribe("/xf/invalid", this.defaultAlertHandler, "handleInvalid");

        //#########    ALERT IMPLEMENTATION  END #############
        //#########    ALERT IMPLEMENTATION  END #############

        //Moved to dojo.xsl after dojo.parse();
        /* try {
         console.debug("contextroot + \"/Flux\": " + fluxAttribute("contextroot") + "/Flux" );
         Flux._path = fluxAttribute("contextroot") + "/Flux";
         Flux.init( fluxAttribute("sessionkey"), dojo.hitch(this,this.applyChanges));
         }catch(ex) {
         fluxProcessor._handleExceptions("Failure executing initcall within Flux Constructor ", ex);
         }*/
    },

    setInlineRoundBorderAlertHandler:function() {
        console.debug("FluxProcessor.setInlineRoundBorderAlertHandler");
        // this.hideAllCommonChilds(dojo.doc);
        this.unsubscribeFromAlertHandler();
        this.defaultAlertHandler = new betterform.ui.common.InlineRoundBordersAlert({});
        this.subscribers[0] = dojo.subscribe("/xf/valid", this.defaultAlertHandler, "handleValid");
        this.subscribers[1] = dojo.subscribe("/xf/invalid", this.defaultAlertHandler, "handleInvalid");
        this.showAllCommonChilds(dojo.doc, "changeAlertType");

    },


    setToolTipAlertHandler:function() {
        console.debug("setToolTipAlertHandler");
        // this.hideAllCommonChilds(dojo.doc);
        this.unsubscribeFromAlertHandler();
        dojo.require("betterform.ui.common.ToolTipAlert");
        this.defaultAlertHandler = new betterform.ui.common.ToolTipAlert({});
        this.subscribers[0] = dojo.subscribe("/xf/valid", this.defaultAlertHandler, "handleValid");
        this.subscribers[1] = dojo.subscribe("/xf/invalid", this.defaultAlertHandler, "handleInvalid");
        this.showAllCommonChilds(dojo.doc,"changeAlertType");
    },

    // Hide commonChilds 'alert', 'hint', 'info'
    hideAllCommonChilds:function(node) {
        dojo.query(".xfControl", node).forEach(dojo.hitch(this, function(control) {
            console.debug("hide commonChild for control: ", control);
            this.defaultAlertHandler._displayNone(dojo.attr(control,"id"),"applyChanges");
        }));
    },

    // Show commonChilds 'alert', 'hint', 'info'
    showAllCommonChilds:function(node,event) {
        dojo.query(".xfControl", node).forEach(dojo.hitch(this, function(control) {
            // console.debug("hide/show commonChild for control: ", control, " control valid state is:", dojo.hasClass(control),"xfValid");
            if(dojo.hasClass(control),"xfValid"){
                this.defaultAlertHandler.handleValid(dojo.attr(control,"id"),event);
            }else {
                this.defaultAlertHandler.handleInvalid(dojo.attr(control,"id"),event);
            }
        }));
    },

    unsubscribeFromAlertHandler:function() {
        for (var i = 0; i < this.subscribers.length; i++) {
            dojo.unsubscribe(this.subscribers[i]);
        }
    },

    handleUnload:function(evt) {
        // console.debug("FluxProcessor.handleUnload Event: ", evt);
        if (this.isDirty && !this.skipshutdown) {
            dojo.stopEvent(evt);
            // console.debug(this.unloadMsg);
            // For IE
            evt.returnValue = this.unloadMsg;
            // For all others
            return this.unloadMsg;
        }
    },

    close:function() {
        var tmpSkipShutdown = dojo.hitch(this, fluxProcessor.skipShutdown).skipshutdown;
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
        console.warn("FluxProcessor.ignoreExceptions():");
    },


    /* Tries to sequentially process pending Events, as long as there is no other roundtrip in progress (Client-Server-Client)
     * Initiates a roundtrip the following conditions apply:
     * 1) The related Objects still exist:  Dijit + Dojo/DOM
     * 2) The Control is NOT read-only (only validated if it CAN be read-only)
     * 3) If an appended Attribute is a reference, the target (dijit/dojo) still needs to exist.
     * ... changed references are updated. (e.g. xf:repeat items)
     */
    eventFifoReader: function() {
        // console.debug("eventFifoReader: this.clientServerEventQueue:",this.clientServerEventQueue);
        var nextPendingClientServerEvent = null;
        var raceCondition = false;
        var dojoObject = null;
        var dijitObject = null;
        var messageString = "";

        //Loop as long as Pending Events are being skipped (as long as no Request is being initiated)
        while ((!this.requestPending) && (this.clientServerEventQueue.length != 0)) {
            nextPendingClientServerEvent = this.clientServerEventQueue.shift();
            switch (nextPendingClientServerEvent.getCallerFunction()) {
                case "dispatchEvent":       console.info("FIFO-READ:  dispatchEvent(" + nextPendingClientServerEvent.getTargetId() + ")"); break;
                case "dispatchEventType":   console.info("FIFO-READ:  dispatchEventType(" + nextPendingClientServerEvent.getTargetId() + ", " + nextPendingClientServerEvent.getEventType() + ", " + nextPendingClientServerEvent.getContextInfo() + ")"); break;
                case "setControlValue":     console.info("FIFO-READ:  setControlValue(" + nextPendingClientServerEvent.getTargetId() + ", " + nextPendingClientServerEvent.getValue() + ")"); break;
                case "setRepeatIndex":      console.info("FIFO-READ: setRepeatIndex(" + nextPendingClientServerEvent.getTargetId() + ", " + nextPendingClientServerEvent.getValue() + ")"); break;
                default:                                        break;
            }

            //*****************************************************************************
            // START: skip this pending Event, if one of the following conditions occurred:
            //*****************************************************************************

            dojoObject = dojo.byId(nextPendingClientServerEvent.getTargetId());
            if (dojoObject == null) {
                console.warn("Event (Client to Server) for Dojo Control " + dojoObject + " skipped. CAUSE: OBJECT is NULL");
                continue;
            }

            dijitObject = dijit.byId(nextPendingClientServerEvent.getTargetId())
            if (dijitObject == null) {
                console.warn("Event (Client to Server) for Dijit Control " + dijitObject + " skipped. CAUSE: OBJECT is NULL");
                continue;
            }

            // Test if this dijit-control has an isReadonly() method

            if (dijitObject && dijitObject.isReadonly()) {
                console.warn("Event (Client to Server) for Dijit Control " + dijitObject + " skipped. CAUSE: READ-ONLY");
                continue;
            }

            // Test if the Control's event was a setControlValue
            if (nextPendingClientServerEvent.getCallerFunction() == "setControlValue") {
                // Test if the next Control-Value-Change originates from the same Control as this Control-Value-Change
                if (this.clientServerEventQueue[0] != null) {
                    // Test if the targetId of this event and the next one are equal
                    if (this.clientServerEventQueue[0].getTargetId() == nextPendingClientServerEvent.getTargetId()) {
                        // Test if the CallerFunction of the next Event is also setControlValue
                        if (this.clientServerEventQueue[0].getCallerFunction() == "setControlValue") {
                            console.debug("Event (Client to Server) for Dijit Control " + dijitObject + " skipped. CAUSE: superseeded by following value-change of same Control");
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
            if (nextPendingClientServerEvent.getCallerFunction() == "setRepeatIndex") {
                if (nextPendingClientServerEvent.getRepeatItem() == null) {
                    console.warn("Event (Client to Server) for Dijit Control " + nextPendingClientServerEvent.getTargetId() + " skipped. CAUSE: Repeat-Item for being selected has disappeared");
                    continue;
                }

                if (nextPendingClientServerEvent.getValue() != dijit.byNode(nextPendingClientServerEvent.getRepeatItem())._getXFormsPosition()) {
                    console.warn("Original Position: " + nextPendingClientServerEvent.getValue + " New Position: " + nextPendingClientServerEvent.getRepeatItem()._getXFormsPosition());
                    // Update the changed Position of this XForms-Repeat-Item
                    nextPendingclientServerEvent.setValue(dijit.byNode(nextPendingClientServerEvent.getRepeatItem())._getXFormsPosition());
                }
            }

            //*****************************************************************************
            // END:   skip this pending Event, if one of the following conditions occurred:
            //*****************************************************************************

            if (dojoObject != null) {
                this._useLoadingMessage(dojoObject);
            }

            switch (nextPendingClientServerEvent.getCallerFunction()) {
                case "dispatchEvent":                this.requestPending = true; this._dispatchEvent(nextPendingClientServerEvent.getTargetId()); break;
                case "dispatchEventType":        this.requestPending = true; this._dispatchEventType(nextPendingClientServerEvent.getTargetId(), nextPendingClientServerEvent.getEventType(), nextPendingClientServerEvent.getContextInfo()); break;
                case "setControlValue":            this.requestPending = true; this._setControlValue(nextPendingClientServerEvent.getTargetId(), nextPendingClientServerEvent.getValue()); break;
                //Re-transform the dojo-Id to repeat-Id
                case "setRepeatIndex":            this.requestPending = true; this._setRepeatIndex(dojo.attr(nextPendingClientServerEvent.getTargetId(), "repeatId"), nextPendingClientServerEvent.getValue()); break;
                default:                                        break;
            }
        }

        //Check if there are still more events pending
        if (this.clientServerEventQueue.length != 0) {
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
        //insert the new clientServerEvent at the beginning of the Buffer
        this.clientServerEventQueue.push(clientServerEvent);
        switch (clientServerEvent) {
            case "dispatchEvent":                console.info("FIFO-WRITE: dispatchEvent(" + clientServerEvent.getTargetId() + ")"); break;
            case "dispatchEventType":        console.info("FIFO-WRITE: dispatchEventType(" + clientServerEvent.getTargetId() + ", " + clientServerEvent.getEventType() + ", " + clientServerEvent.getContextInfo() + ")"); break;
            case "setControlValue":            console.info("FIFO-WRITE: setControlValue(" + clientServerEvent.getTargetId() + ", " + clientServerEvent.getValue() + ")"); break;
            case "setRepeatIndex":            console.info("FIFO-WRITE: setRepeatIndex(" + clientServerEvent.getTargetId() + ", " + clientServerEvent.getValue() + ")"); break;
            default:                                        break;
        }
        //schedule the next try for reading the next pending Event of the FIFO-Buffer
        clearTimeout(this.fifoReaderTimer);
        this.fifoReaderTimer = setTimeout("fluxProcessor.eventFifoReader()", 0);
    },

    //eventually an 'activate' method still makes sense to provide a simple DOMActivate of a trigger Element
    dispatchEvent: function (targetId) {
        var newClientServerEvent = new betterform.ClientServerEvent();
        newClientServerEvent.setTargetId(targetId);
        newClientServerEvent.setCallerFunction("dispatchEvent");
        this.eventFifoWriter(newClientServerEvent);
    },

    //eventually an 'activate' method still makes sense to provide a simple DOMActivate of a trigger Element
    _dispatchEvent: function (targetId) {
        //console.debug("FluxProcessor.dispatch(",targetId,") this: ", this);
        try {
            dwr.engine.setErrorHandler(this._handleExceptions);
            dwr.engine.setOrdered(true);
            Flux.dispatchEvent(targetId, this.sessionKey, this.applyChanges);
        } catch(ex) {
            fluxProcessor._handleExceptions("Failure executing Flux.dispatchEvent", ex);
        }
        return false;
    },

    dispatchEventType:function(targetId, eventType, contextInfo) {
        // console.debug("FluxProcessor.dispatchEventType(",targetId,") this: ", this, " eventType:",eventType, " contextInfo:",contextInfo);
        var newClientServerEvent = new betterform.ClientServerEvent();
        newClientServerEvent.setTargetId(targetId);
        newClientServerEvent.setEventType(eventType);
        newClientServerEvent.setContextInfo(contextInfo);
        newClientServerEvent.setCallerFunction("dispatchEventType");
        this.eventFifoWriter(newClientServerEvent);
    },

    _dispatchEventType:function(targetId, eventType, contextInfo) {
        // console.debug("FluxProcessor._dispatchEventType(",targetId,") this: ", this, " eventType:",eventType, " contextInfo:",contextInfo);
        try {
            dwr.engine.setErrorHandler(this._handleExceptions);
            dwr.engine.setOrdered(true);
            if (contextInfo == undefined) {
                Flux.dispatchEventType(targetId, eventType, this.sessionKey, dojo.hitch(this, this.applyChanges));
            } else {
                Flux.dispatchEventTypeWithContext(targetId, eventType, this.sessionKey, contextInfo, dojo.hitch(this, this.applyChanges));
            }
        }
        catch(ex) {
            fluxProcessor._handleExceptions("Failure executing Flux.dispatchEventType", ex);
        }
    },

    setControlValue: function(id, value) {
        var newClientServerEvent = new betterform.ClientServerEvent();
        newClientServerEvent.setTargetId(id);
        newClientServerEvent.setValue(value);
        newClientServerEvent.setCallerFunction("setControlValue");
        this.eventFifoWriter(newClientServerEvent);
    },

    _setControlValue: function (id, value) {
        // console.debug("FluxProcessor.setControlValue", id, value);
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
        var newClientServerEvent = new betterform.ClientServerEvent();

        // Start: Obtaining the real Id for this repeat-element
        var targetElements = dojo.query("*[repeatId='" + repeatId + "']");
        var targetId = dojo.attr(targetElements[0], "id");
        // End:   Obtaining the real Id for this repeat-element

        // Obtain the Repeat-Item Dijit-Object for the selected Line:
        var repeatItem = dijit.byId(targetId)._getRepeatItems()[targetPosition - 1];

        newClientServerEvent.setTargetId(targetId);
        newClientServerEvent.setValue(targetPosition);
        newClientServerEvent.setRepeatItem(repeatItem);
        newClientServerEvent.setCallerFunction("setRepeatIndex");
        this.eventFifoWriter(newClientServerEvent);
    },

    _setRepeatIndex:function(/*String*/repeatId, /*String*/targetPosition) {
        // console.debug("FluxProcessor.setRepeatIndex for Repeat "+ repeatId + " to position " + targetPosition);
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
        dojo.removeClass(this.indicatorTargetObject, "bfPending");


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
        // console.debug("FluxProcessor._useLoadingMessage");
        if (fluxProcessor.indicatorObjectTimer) {
            clearTimeout(fluxProcessor.indicatorObjectTimer);
        }
        if (this.indicatorTargetObject) {
            dojo.removeClass(this.indicatorTargetObject, "bfPending");
        }

        this.indicatorTargetObject = dojoObject;

        dojo.addClass(dojoObject, "bfPending");

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
        // console.debug("FluxProcessor._handleExceptions msg:",msg, " exception: ", exception);
        if (msg != undefined && exception != undefined) {
            console.error(msg, ' - Exception: ', exception);
        } else if (msg != undefined) {
            console.error(msg);
            if (this.webtest != 'true') {
                alert(msg);
            } else {
                //only for testing purposes
                this.logTestMessage(xmlEvent.contextInfo.errorinformation);
            }
        } else {
            console.error("Unknown exception occured! arguments: ", arguments);
        }
    },


    applyChanges: function(data) {
        // console.debug("FluxProcessor.applyChanges data:",data);
        try {
            var validityEvents = new Array();
            var index = 0;

            //eventLog writing
            var eventLog = dojo.byId("eventLog");

            dojo.forEach(data,
                    function(xmlEvent) {
//                        console.debug(xmlEvent.type, " [", xmlEvent.contextInfo, "]");

                        /*
                        if 'logEvents' is true the eventlog from the server will be written
                        to DOM and can be viewed in a separate expandable section in the window.
                        */
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
                                        tableCells += "<tr><td class='propName'>"+ dataItem + "</td><td class='propValue'><a href='#' onclick='reveal(this);'>" + contextInfo[dataItem] + "</a></td></tr>"
                                    }else if(dataItem == "targetElement" && xmlEvent.type == "betterform-load-uri"){
                                        var targetElement = contextInfo.xlinkTarget;
                                        tableCells += "<tr><td class='propName'>"+ dataItem + "</td><td class='propValue'><a href='#' onclick='reveal(this);'>" + targetElement + "</a></td></tr>"
                                    }
                                    else {
                                        tableCells += "<tr><td class='propName'>"+ dataItem + "</td><td class='propValue'>" +  contextInfo[dataItem] + "</td></tr>"
                                    }
                                }
                            }
                            //create output
                            dojo.create("li", {
                                innerHTML: "<a href='#' onclick='toggleEntry(this);'><span>"+xmlEvent.type+"</span></a><table class='eventLogTable'>" + tableCells + "</table>"
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
                            case "betterform-dialog-open"        : fluxProcessor._handleBetterFormDialogOpen(xmlEvent); break;
                            case "betterform-dialog-close"       : fluxProcessor._handleBetterFormDialogClose(xmlEvent); break;
                            case "betterform-AVT-changed"        : fluxProcessor._handleAVTChanged(xmlEvent);break;
                            case "betterform-instance-created"   : fluxProcessor._handleInstanceCreated(xmlEvent);break;
                            case "betterform-model-removed"      : fluxProcessor._handleModelRemoved(xmlEvent);break;
                            case "betterform-exception"         : fluxProcessor._handleBetterformException(xmlEvent); break;
                            case "upload-progress-event"         : fluxProcessor._handleUploadProgressEvent(xmlEvent); break;
                            case "xforms-focus"                  : fluxProcessor._handleXFormsFocus(xmlEvent); break;
                            case "xforms-help"                   : fluxProcessor._handleShowHelp(xmlEvent); break;
                            case "xforms-hint"                   : fluxProcessor._handleXFormsHint(xmlEvent); break;
                            case "xforms-link-exception"         : fluxProcessor._handleLinkException(xmlEvent); break;
                            case "betterform-switch-toggled"     : fluxProcessor._handleSwitchToggled(xmlEvent); break;
                            case "betterform-script-action"      : eval(xmlEvent.contextInfo["script"]); break;
                            case "xforms-value-changed"          : /* console.debug(xmlEvent); */ break;
                            case "xforms-version-exception"      : fluxProcessor._handleVersionException(xmlEvent); break;
                            case "xforms-binding-exception"      : fluxProcessor._handleBindingException(xmlEvent);break;
                            case "xforms-submit-error"           : fluxProcessor._handleSubmitError(xmlEvent); break;
                            case "DOMFocusIn"                    : fluxProcessor.lastServerClientFocusEvent = {postponedFunction:fluxProcessor._handleDOMFocusIn, postponedXmlEvent:xmlEvent}; break;    //cache the xmlEvent for being processed later
                            case "xforms-out-of-range"           : fluxProcessor._handleOutOfRange(xmlEvent);break;
                            case "xforms-in-range"               : fluxProcessor._handleInRange(xmlEvent);break;
                            case "xforms-invalid"                :
                            case "xforms-valid"                  :validityEvents[index] = xmlEvent; index++;break;

                            /* default handling for known events */
                            case "betterform-id-generated"       :
                            case "DOMActivate"                   :
                            case "xforms-select"                 :
                            case "xforms-deselect"               :
                            case "DOMFocusOut"                   :
                            case "xforms-model-construct"        :
                            case "xforms-model-construct-done"   :break;
                            case "xforms-ready"                  : this.isReady = true;dojo.publish("/xf/ready", []);break; //not perfect - should be on XFormsModelElement
                            case "xforms-submit"                 : break;
                            case "xforms-submit-done"            : fluxProcessor._handleSubmitDone(xmlEvent);break;

                            /* Unknow XMLEvent: */
                            default                         : console.error("Event " + xmlEvent.type + " unknown [Event:", xmlEvent, "]"); break;
                        }
                    }
                    );

            if(fluxProcessor.logEvents){
                // add a devider for eventLogViewer
                dojo.create("li", {
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

    _handleAVTChanged:function(xmlEvent){
        dojo.attr(xmlEvent.contextInfo.targetId,xmlEvent.contextInfo.attribute,xmlEvent.contextInfo.value);
    },

    _handleInstanceCreated:function(xmlEvent){
        dojo.require("dojox.fx");
        var debugPane = dojo.byId("debug-pane-links");
        if(debugPane != null){
            var contextroot = dojo.attr(dojo.byId("debug-pane"),"context");
            var newLink = document.createElement("a");
            dojo.attr(newLink,"href",contextroot + xmlEvent.contextInfo.modelId + "/" + xmlEvent.contextInfo.instanceId);
            dojo.attr(newLink,"target","_blank");
            dojo.attr(newLink,"modelId",xmlEvent.contextInfo.modelId);
            var linkText = document.createTextNode("Model:" + xmlEvent.contextInfo.modelId + " :: " + "Instance:" + xmlEvent.contextInfo.instanceId);
            newLink.appendChild(linkText);
            debugPane.appendChild(newLink);
            dojox.fx.highlight({node:newLink, color:'#999999', duration:600}).play()
        }
    },

    _handleModelRemoved:function(xmlEvent){
        var modelId = xmlEvent.contextInfo.modelId;
        dojo.query("#debug-pane a[modelId='" + modelId +"']").orphan();

    },

    _handleValidity:function(validityEvents) {
        dojo.forEach(validityEvents, function(xmlEvent) {
            var control = dijit.byId(xmlEvent.contextInfo.targetId);
            if (control != undefined) {
                if (xmlEvent.type == "xforms-valid") {
                    control._handleSetValidProperty(true);
                } else {
                    control._handleSetValidProperty(false);
                }
            }
        });
    },

    _handleBindingException:function(xmlEvent) {
        if (this.webtest != 'true') {
            console.warn("xforms-binding-exception at " + xmlEvent.contextInfo.targetId + " - " + xmlEvent.contextInfo.defaultinfo);
        } else {
            //only for testing purposes
            this.logTestMessage("xforms-binding-exception");
        }
    },

    _handleVersionException:function(xmlEvent) {
        if (this.webtest != 'true') {
            console.error(xmlEvent.contextInfo.errorinformation);
        } else {
            //only for testing purposes
            this.logTestMessage(xmlEvent.contextInfo.errorinformation);
        }
    },

     _handleBetterformException:function(xmlEvent) {
        if (this.webtest != 'true') {
            var description = xmlEvent.contextInfo.message;
            console.error(xmlEvent.contextInfo.message);
            var exception = dojo.byId('betterFORM-exception');
            var log;
            if (!exception) {
                log = document.createElement('div');
                log.id = 'betterFORM-exceptionLog';
                document.body.appendChild(log);
                exception = document.createElement('exception');
                exception.id = 'betterFORM-exception';
                var exceptionText = document.createTextNode(description);
                exception.appendChild(exceptionText);
                log.appendChild(exception);
            } else {
                exception.removeChild(exception.firstChild);
                var exceptionText = document.createTextNode(description);
                exception.appendChild(exceptionText);
            }
        } else {
            //only for testing purposes
            fluxProcessor.logTestMessage(xmlEvent.contextInfo.message);
        }
    },

    _handleSubmitError:function(xmlEvent) {
        console.warn("xforms-submit-error at ", xmlEvent.contextInfo);
        dojo.query(".xfInvalid", dojo.doc).forEach(function(control) {
            // console.debug("_handleSubmitError: invalid control: ", control);
            dojo.publish("/xf/invalid", [dojo.attr(control, "id"),"submitError"]);
        });
        dojo.query(".xfRequired", dojo.doc).forEach(function(control) {
            //if control has no value add CSS class xfRequiredEmpty
            var xfControl = dijit.byId(control.id);
            if(xfControl != undefined && xfControl.getControlValue === 'function'){
                var xfValue = xfControl.getControlValue();
                if(xfValue == undefined || xfValue == ''){
                    dojo.addClass(xfControl.domNode,"xfRequiredEmpty");
                }
            }
        });
    },

    _handleBetterFormLoadURI:function(/*XMLEvent*/ xmlEvent) {
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
            if (dojo.byId(xlinkTarget) != undefined) {
                targetid = xlinkTarget;
            } else {
                // if we reach here the xlinkTarget is no idref but the value of a name Attrbute that needs resolving
                // to an id.
                var tmp = dojo.query("*[name='" + xlinkTarget + "']")[0];
                targetid = tmp.id;
                console.debug("target id for embedding is: ", targetid);
            }

            this._unloadDOM(targetid);

            //get original Element in master DOM
            var htmlEntryPoint = dojo.byId(targetid);
            htmlEntryPoint.innerHTML = xmlEvent.contextInfo.targetElement;
            dojo.attr(htmlEntryPoint, "id", xlinkTarget + "Old");
            var nodesToEmbed = dojo.byId(targetid);

            dojo.require("dojo.parser");
            dojo.parser.parse(htmlEntryPoint);

            dojo.place(nodesToEmbed, htmlEntryPoint, "before");
//            dojo.fx.wipeIn({node: nodesToEmbed,duration: 500}).play();
            dojo.style(nodesToEmbed,"display","block");

            //copy classes from mountpoint
            var classes = dojo.attr(htmlEntryPoint, "class");
            dojo.attr(nodesToEmbed, "class", classes);
            htmlEntryPoint.parentNode.removeChild(htmlEntryPoint);

            // finally dynamically load the CSS (if some) form the embedded form
            var cssToLoad = xmlEvent.contextInfo.inlineCSS;
//            console.debug("css to load: ", cssToLoad);
            var headID = document.getElementsByTagName("head")[0];
            var mountpoint = dojo.byId(xlinkTarget);

            if(cssToLoad != undefined && cssToLoad != ""){
                //console.debug("adding Style: ", cssToLoad);
                var stylesheet1 = document.createElement('style');
                stylesheet1.setAttribute("type", "text/css");
                stylesheet1.setAttribute("name", xlinkTarget);
                var head1 = document.getElementsByTagName('head')[0];
                head1.appendChild(stylesheet1);
                if (stylesheet1.styleSheet) {   // IE
                        stylesheet1.styleSheet.cssText = cssToLoad;
                } else {                // the world
                        var textNode1 = document.createTextNode(cssToLoad);
                        stylesheet1.appendChild(textNode1);
                }
            }

            var externalCssToLoad = xmlEvent.contextInfo.externalCSS;

            if (externalCssToLoad != undefined && externalCssToLoad != "") {
                var styles = externalCssToLoad.split('#');
                var head2 = document.getElementsByTagName('head')[0];
                for (var i = 0; i <= styles.length; i = i+1) {
                    if (styles[i] != undefined && styles[i] != "") {
                        //console.debug("adding Style: ", styles[i]);
                        var stylesheet2 = document.createElement('link');
                        stylesheet2.setAttribute("rel","stylesheet");
                        stylesheet2.setAttribute("type","text/css");
                        stylesheet2.setAttribute("href",styles[i]);
                        stylesheet2.setAttribute("name",xlinkTarget);
                        head2.appendChild(stylesheet2);
                    }
                }
            }

            var inlineJavaScriptToLoad = xmlEvent.contextInfo.inlineJavascript;
            if (inlineJavaScriptToLoad != undefined && inlineJavaScriptToLoad != "") {
                //console.debug("adding script: ", inlineJavaScriptToLoad);
                var javascript1 = document.createElement('script');
                javascript1.setAttribute("type", "text/javascript");
                javascript1.setAttribute("name", xlinkTarget);
                var head3 = document.getElementsByTagName('head')[0];
                head3.appendChild(javascript1);
                javascript1.text = inlineJavaScriptToLoad;
            }

            var externalJavaScriptToLoad = xmlEvent.contextInfo.externalJavascript;
            if (externalJavaScriptToLoad != undefined && externalJavaScriptToLoad != "") {
                var scripts = externalJavaScriptToLoad.split('#');
                var head4 = document.getElementsByTagName("head")[0];
                for (var i = 0; i <= scripts.length; i = i+1) {
                    if (scripts[i] != undefined && scripts[i] != "") {
                        //console.debug("adding script: ", scripts[i]);
                        var javascript2 = document.createElement('script');
                        javascript2.setAttribute("type","text/javascript");
                        javascript2.setAttribute("src",scripts[i]);
                        javascript2.setAttribute("name",xlinkTarget);
                        head4.appendChild(javascript2);
                    }
                }
            }

        }
        /*  xf:load show=none
         to unload (loaded) subforms
         */
        else if (xmlEvent.contextInfo.show == "none") {
            // console.debug("FluxProcessor._handleBetterFormLoadURI: htmlEntryPoint", htmlEntryPoint);
            this._unloadDOM(xmlEvent.contextInfo.xlinkTarget);
        }
        else {
            console.error("betterform-load-uri show='" + xmlEvent.contextInfo.show + "' unknown!");
        }
    },

    _handleSubmitDone:function(xmlEvent) {
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


            //determine the DOM Element in the client DOM which is the target for embedding
            var targetid;
            if (dojo.byId(target) != undefined) {
                targetid = target;
            } else {
                // if we reach here the target is no idref but the value of a name Attrbute that needs resolving
                // to an id.
                var tmp = dojo.query("*[name='" + target + "']")[0];
                targetid = tmp.id;
                console.debug("target id for embedding is: ", targetid);
            }

            this._unloadDOM(targetid);

            //get original Element in master DOM
            var htmlEntryPoint = dojo.byId(targetid);
            htmlEntryPoint.innerHTML = content;
            dojo.require("dojo.parser");
            dojo.parser.parse(htmlEntryPoint);
        }
    },

    _unloadDOM:function(target) {
        //delete CSS specific to subform
        var htmlEntryPoint = dojo.byId(target);
        if (htmlEntryPoint == undefined) {
            return;
        }

        var styleList = document.getElementsByTagName("style");
        //console.debug("styleList" , styleList);
        if (styleList != undefined) {
        dojo.forEach(styleList, function(item) {
                //console.debug("style: ", item);
                if (item != undefined) {
            if(dojo.attr(item,"name") == target){
                        //console.debug("removing style: ", item);
                        //console.debug("parentNode: ", item.parentNode);
                item.parentNode.removeChild(item);
            }
                }
        });
        }

        var externalStyleList = document.getElementsByTagName("link");
        console.debug("styleList" , externalStyleList);
        if (externalStyleList != undefined) {
        dojo.forEach(externalStyleList, function(item) {
                //console.debug("style: ", item);
                if (item != undefined) {
            if(dojo.attr(item,"name") == target){
                        console.debug("removing style: ", item);
                        console.debug("parentNode: ", item.parentNode);
                item.parentNode.removeChild(item);
            }
                }
        });
        }

        var scriptList = document.getElementsByTagName("script");
        //console.debug("scriptList" , scriptList);
        if (scriptList != undefined) {
            dojo.forEach(scriptList, function(item) {
                //console.debug("script: ", item);
                if (item != undefined) {
                    if(dojo.attr(item,"name") == target){
                        //console.debug("removing: ", item);
                        //console.debug("parentNode: ", item.parentNode);
                        item.parentNode.removeChild(item);
                    }
                }
            });
        }

        var widgetID = "widgetid";
        if (dojo.isIE) {
            widgetID = "widgetId"
        }

        var widgets = dojo.query("*[" + widgetID + "]", htmlEntryPoint);

        dojo.forEach(widgets,
                function(item) {
                    if (item != undefined) {
                        var childDijit = dijit.byId(dojo.attr(item, 'id'));
                        if (childDijit != undefined) {
                            // console.debug("FluxProcessor._unloadDOM: destroy ", childDijit);
                            childDijit.destroy();
                        } else {
                            // console.debug("FluxProcessor._unloadDOM: ChildDijit is null ");
                            childDijit = dijit.byId(dojo.attr(item, widgetID));
                            if (childDijit != undefined) {
                                childDijit.destroy();
                            }
                        }

                    }
                }
                );
        while (htmlEntryPoint.hasChildNodes()) {
            htmlEntryPoint.removeChild(htmlEntryPoint.firstChild);
        }
    },


    _handleBetterFormRenderMessage:function(/*XMLEvent*/ xmlEvent) {
        var message = xmlEvent.contextInfo.message;
        var level = xmlEvent.contextInfo.level;
        //console.debug("FluxProcessor.handleRenderMessage: message='" + message + "', level='" + level + "'");
        if (this.webtest != 'true') {

            if (level == "ephemeral") {
                dijit.byId("betterformMessageToaster").setContent(message, 'message');
                dijit.byId("betterformMessageToaster").show();
            }
            else {
                var exception = xmlEvent.contextInfo.exception;
                if (exception != undefined) {
                    console.warn("An Exception occured in Facade: ", exception);
                } else {
                    alert(message);
                    // the following code had to be disabled because of focusing problems:
                    // when dialog is opened by a DOMFocusIn event the behavior of Dialog cause an endless loop
                    // of focusIn events as the Dialog will send the focus back to the control that had focus before
                    // opening the Dialog. This effectively causes the page to 'hang'. Focusing can be disabled in
                    // Dialog but then the original focus will be lost. The standard alert does not have these
                    // problems.

/*


                    var messageNode = dojo.create("div",  null, dojo.body());
                    dojo.attr(messageNode, "title", "Message");
                    dojo.require("dijit.Dialog");
                    var messageDialog = new dijit.Dialog({
                        title: "Message: ",
                        content: message

                    }, messageNode);

                    var closeBtnWrapper = dojo.create("div", null , messageDialog.domNode);

                    dojo.style(closeBtnWrapper, "position","relative");
                    dojo.style(closeBtnWrapper, "right","5px");
                    dojo.style(closeBtnWrapper, "text-align","right");
                    dojo.style(closeBtnWrapper, "width","40px;");

                    var emptySpace= dojo.create("div", null , messageDialog.domNode);
                    dojo.style(emptySpace,"height","10px");
                    var closeBtnNode = dojo.create("div", null , closeBtnWrapper);
                    var closeBtnDijit = new dijit.form.Button({label: "OK",
                                                   onClick: function() {
                                                       messageDialog.hide();
                                                       messageDialog.destroy();
                                                       dojo.empty(messageNode);
                                                   }
                                                },
                                                closeBtnNode);
                    messageDialog.show();
*/
                }
            }
        } else {
            //only for testing purposes
            this.logTestMessage(message);
        }
    },

    _handleOutOfRange:function(xmlEvent) {
        /*
         var message = "Value for ui control '" + xmlEvent.contextInfo.targetName + "' (id:"+xmlEvent.contextInfo.targetId+") is out of range";
         if(this.webtest != 'true') {
         dijit.byId("betterformMessageToaster").setContent(message,'message');
         dijit.byId("betterformMessageToaster").show();
         }else{
         this.logTestMessage(message);
         }
         */
        var uiControl = dojo.byId(xmlEvent.contextInfo.targetId + "-value");
        if (uiControl != undefined) {
            if (dojo.hasClass(uiControl, "xfInRange")) {
                dojo.removeClass(uiControl, "xfInRange");
            }
            dojo.addClass(uiControl, "xfOutOfRange");
        }
    },

    _handleInRange:function(xmlEvent) {
        var uiControl = dojo.byId(xmlEvent.contextInfo.targetId + "-value");
        if (uiControl != undefined) {
            if (dojo.hasClass(uiControl, "xfOutOfRange")) {
                dojo.removeClass(uiControl, "xfOutOfRange");
            }
            dojo.addClass(uiControl, "xfInRange");
        }
    },

    /*
     * function for testing purpose to avoid usage of JS alerts that can cause problems with Selenium
     */
    logTestMessage:function(message) {
        var log = dojo.byId('messageLog');
        if (!log) {
            log = document.createElement('div');
            log.id = 'messageLog';
            document.body.appendChild(log);
        }
        var messageDiv = document.createElement('message');
        messageDiv.id = 'message' + ( this._countMessages(log) + 1);
        var messageText = document.createTextNode(message);
        messageDiv.appendChild(messageText);
        log.appendChild(messageDiv);
    },

    _countMessages:function (log) {
        var logMessagesCount = log.getElementsByTagName('message').length;
        return logMessagesCount;
    },

    _handleBetterFormReplaceAll:function() {
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

    _handleBetterFormDialogOpen:function(/*XMLEvent*/ xmlEvent) {
       // console.debug("FluxProcessor._handleBetterformDialogOpen: targetId: >",xmlEvent.contextInfo.targetId,"< parentId: " , xmlEvent.contextInfo.parentId);
       var xfControlId =xmlEvent.contextInfo.targetId;
       // if XForms Control Dijit allready exists call show on selected control
       if(dijit.byId(xfControlId) != undefined){
            dijit.byId(xfControlId).show();
       }else {
            console.error("error during betterform-dialog-show-event: targetId >",xmlEvent.contextInfo.targetId,"<, xfControlId: >",xfControlId,"< does not exist");
       }
    },

    _handleBetterFormDialogClose:function(/*XMLEvent*/ xmlEvent) {
       // console.debug("FluxProcessor._handleBetterformDialogClose: targetId: >",xmlEvent.contextInfo.targetId,"< parentId: " , xmlEvent.contextInfo.parentId);
       var xfControlId =xmlEvent.contextInfo.targetId;
       // if XForms Control Dijit allready exists call hide on selected control
       if(dijit.byId(xfControlId) != undefined){
            dijit.byId(xfControlId).hide();
       }else {
            console.error("error during betterform-dialog-hide-event: targetId >",xmlEvent.contextInfo.targetId,"< does not exist");
       }
    },

    _handleBetterFormStateChanged:function(/*XMLEvent*/ xmlEvent) {
        //console.debug("FluxProcessor._handleBetterFormStateChanged: targetId: " + xmlEvent.contextInfo.targetId , " parentId: " , xmlEvent.contextInfo.parentId);

        /*
         console.debug("FluxProcessor._handleStateChanged this:", this,
         "\n\txmlEvent: ",xmlEvent,
         "\n\tcontextInfo: ",xmlEvent.contextInfo,
         "\n\tparentId: ",xmlEvent.contextInfo.parentId,
         "\n\ttargetId: ",xmlEvent.contextInfo.targetId,
         "\n\tvalue: ",xmlEvent.contextInfo.value,
         "\n\ttargetName: ",xmlEvent.contextInfo.targetName,
         "\n\tmip:readonly: ",xmlEvent.contextInfo.readonly,
         "\n\tmip:required: ",xmlEvent.contextInfo.required,
         "\n\tmip:valid: ",xmlEvent.contextInfo.valid,
         "\n\tmip:enabled: ",xmlEvent.contextInfo.enabled
         );
         */

        var xfControlId = xmlEvent.contextInfo.targetId;

        /**
         *
         * If:  StateChange Target is a XForms Repeat
         *
         * **/
        if (xmlEvent.contextInfo.targetName != undefined && xmlEvent.contextInfo.targetName == "repeat") {
            // console.debug("FluxProcessor._handleBetterFormStateChanged for Repeat");
            var repeatElement = dojo.query("*[repeatId='" + xfControlId + "']")[0];
            // console.debug("FluxProcessor._handleBetterFormStateChanged for Repeat:",repeatElement);
            if (repeatElement == undefined) {
                console.error("(FluxProcessor._handleBetterFormStateChanged xf:repeat: ", xfControlId, " does not exist");
                return;
            }
            var repeat = dijit.byId(dojo.attr(repeatElement, "id"));
            if (repeat != undefined) {
                repeat.handleStateChanged(xmlEvent.contextInfo);
            }
            else if (repeat == undefined && repeatElement != undefined) {
                repeat = new betterform.ui.container.Repeat({}, repeatElement);
                repeat.handleStateChanged(xmlEvent.contextInfo);
            }
        }
        else if (xmlEvent.contextInfo.targetName != undefined && xmlEvent.contextInfo.targetName == "group") {
            // console.debug("FluxProcessor._handleBetterFormStateChanged for Group");
            var group = dijit.byId(xmlEvent.contextInfo.targetId);

            if (group == undefined && dojo.byId(xmlEvent.contextInfo.targetId) != undefined) {
                // console.debug("creating new Group: ",dojo.byId(xmlEvent.contextInfo.targetId));
                dojo.require("betterform.ui.container.Group");
                group = new betterform.ui.container.Group({}, dojo.byId(xmlEvent.contextInfo.targetId));
            }
            /* group markup does not exist in ui, check if targetid references an repeatItem */
            else if (xmlEvent.contextInfo.targetId != undefined) {
                // console.debug("creating new Group (xmlEvent.contextInfo.targetId = undefined) : ",xmlEvent.contextInfo.targetId);
                var control = dojo.query("*[repeatItemId='" + xmlEvent.contextInfo.targetId + "']")[0];
                if (control != undefined && dojo.hasClass(control, "xfRepeatItem")) {
                    // console.debug("group get: ",dijit.byId(dojo.attr(control, "id")));
                    group = dijit.byId(dojo.attr(control, "id"));
                }
            }
            if (group != undefined) {
                group.handleStateChanged(xmlEvent.contextInfo);
            } else {
                console.warn("FluxProcessor._handleBetterFormStateChanged: don't know how to handle xmlEvent: ", xmlEvent, " for target: " + xmlEvent.contextInfo.targetId + " [", xmlEvent.contextInfo.targetName, "]");
            }
        }
        // HANDLING XF:COPY FOR ALL SELECTS
        else if (xmlEvent.contextInfo.targetName != undefined && xmlEvent.contextInfo.targetName == "select1" && xmlEvent.contextInfo.copyItem != undefined) {
            // console.debug("FluxProcessor._handleBetterFormStateChanged xf:copy handling: xmlEvent: ",xmlEvent, " contextInfo: ", xmlEvent.contextInfo);
            var warningMsg = "FluxProcessor._handleBetterFormStateChanged: Select1 ControlValue " + xmlEvent.contextInfo.targetId + "-value: No item selected"
            var select1 = dojo.byId(xmlEvent.contextInfo.targetId + "-value");
            if (select1 != undefined) {
                var selectedItemId = xmlEvent.contextInfo.selectedItem;
                // console.debug("FluxProcessor._handleBetterFormStateChanged xf:copy: selectedItem: ", selectedItemId);
                if (selectedItemId != undefined && selectedItemId != "") {
                    var selectItems = dojo.query(".xfSelectorItem", select1);
                    var itemSelected = false;
                    for (var i = 0; i < selectItems.length; i++) {
                        if (dojo.attr(selectItems[i], "id") == selectedItemId) {
                            // console.debug("SelectedItem: ", selectItems[i]);
                            select1.selectedIndex = (i);
                            itemSelected = true;
                        }
                    }
                    if (!itemSelected) {
                        console.warn(warningMsg);
                    }
                }
                else {
                    console.warn(warningMsg);
                }
            } else {
                console.warn(warningMsg);
            }
        }

        /**
         *
         * Else If:  XForms Control Dijit allready exists call handleStateChanged on selected control
         *
         * **/
        else if (dijit.byId(xfControlId) != undefined) {
            // console.debug("FluxProcessor.handleStateChanged on existing Dijit [id: " + xfControlId + ", / object:",dijit.byId(xfControlId),+"]");
            var xfControlDijit = dijit.byId(xfControlId);
            // console.debug("_handleBetterFormStateChanged: ", xfControlDijit, " xmlEvent.contextInfo:",xmlEvent.contextInfo);
            xfControlDijit.handleStateChanged(xmlEvent.contextInfo);
        }
        /**
         *
         * Else If: XForms Control Dijit does not yet exist but a DOM Prototype Template is allready present
         *          represent state-changed-events directly after betterform-item-inserted event
         *
         * **/

        else if (dojo.byId(xfControlId) != undefined) {
            // console.debug("FluxProcessor.handleStateChanged on existing DOM  [id: " + xfControlId + ", / xmlEvent:",xmlEvent,+"]");
            var controlNodeCreated = new betterform.ui.Control({contextInfo:xmlEvent.contextInfo}, dojo.byId(xfControlId));
            if(controlNodeCreated.handleStateChanged) {
                controlNodeCreated.handleStateChanged(xmlEvent.contextInfo);
            }else  {
                console.warn("controlNodeCreated.handleStateChanged does not exist for widget ", controlNodeCreated);
            }

        }
        /**
         *
         * Else If: No XForms Control for the given id exist at all, e.q. inserting into repeats / itemsets,
         *          Algorithm relies on parent id of the given XForms Control
         * **/


        else if (xmlEvent.contextInfo.parentId != undefined && xmlEvent.contextInfo.parentId != "") {
            // console.debug("FluxProcessor.handleStateChanged: xmlEvent.contextInfo.parentId = " + xmlEvent.contextInfo.parentId);
            var parentDijit = dijit.byId(xmlEvent.contextInfo.parentId);
            // parent dijit does exist and executes handleStateChanged
            if (parentDijit != undefined) {
                // console.debug("FluxProcessor.handleStateChanged(ParentDijit" + parentDijit.id + ") no control found, execute handle state change on parent");
                parentDijit.handleStateChanged(xmlEvent.contextInfo);
            }
            // parent dijit does not(!!) exist yet
            else {
                var parentControlNode = dojo.byId(xmlEvent.contextInfo.parentId);
                if (parentControlNode == undefined) {
                    console.error("FluxProcessor betterform-state-changed  Warning: Neither Target nor its Parent does exist [xmlEvent", xmlEvent, "]");
                }
                //  special handling for Select controls, check if parent node is selector item
                else if (dojo.hasClass(parentControlNode, "xfSelectorItem")) {
                    // console.debug("FluxProcessor.handleStateChanged Target Node does not exist, Parent Control is SelectorItem (ParentSelector:" , parentControlNode , ")");
                    var selectParentId = dojo.attr(parentControlNode.parentNode, "id");
                    if(dijit.byId(selectParentId)) {
                        dijit.byId(selectParentId).handleStateChanged(xmlEvent.contextInfo);
                    }else if (parentControlNode){
                        // DIJIT COULD NOT BE FOUND - SEARCH FOR PROTOTYPE SELECT OPTIONS
                        // console.debug("found Selector Item Node: ",parentControlNode);
                        if(xmlEvent.contextInfo.targetName == "label") {
                            // console.debug("Update label of option - value: ",xmlEvent.contextInfo.value);
                            parentControlNode.innerHTML = xmlEvent.contextInfo.value;
                        }
                        else if(xmlEvent.contextInfo.targetName == "value") {
                            // console.debug("Update value of option - value: ",xmlEvent.contextInfo.value);
                            dojo.attr(parentControlNode,"value",xmlEvent.contextInfo.value);
                        }else {
                            console.warn("FluxProcessor betterform-state-changed: : error updating xfSelector item ",xmlEvent.contextInfo);
                        }
                    }else {
                        console.warn("FluxProcessor betterform-state-changed: : can't find xfSelectorItem ", selectParentId);
                    }
                }
                else {
                    console.warn("FluxProcessor betterform-state-changed: No handleStateChanged implementation availabled for contextinfo: ", xmlEvent.contextInfo);
                }
            }
        }
        // Check if it is a nested output in a trigger label. If so, (really quick hack: chance xmlEvent.contextInfo)
        else if(xmlEvent.contextInfo.targetName != undefined  && xmlEvent.contextInfo.targetName == "output"){
            // console.debug("FluxProcessor._handleBetterFormStateChanged xf:output inside label handling: xmlEvent: ",xmlEvent, " contextInfo: ", xmlEvent.contextInfo);

	        var possibleId = xmlEvent.contextInfo.targetId.substring(1,xmlEvent.contextInfo.targetId.length) -2 ;
            var warningMsg = "FluxProcessor._handleBetterFormStateChanged: element for dynamic label " + xmlEvent.contextInfo.targetId + ": Control not found ";
	        var control = dijit.byId("C"+possibleId);
	        if ((control != undefined) && (control.controlType == "trigger")) {
                // console.debug("FluxProcessor._handleBetterFormStateChanged for dynamic label on trigger control: " ,control, "controlType: ", control.controlType);
		        xmlEvent.contextInfo.targetId = "C"+(possibleId-1);
		        xmlEvent.contextInfo.parentId = "C"+(possibleId-2);
		        xmlEvent.contextInfo.targetName = "label";
                control.handleStateChanged(xmlEvent.contextInfo);
            } else if (control != undefined) {
		        // There was a dijit, so currently assuming it is either an output, input or group. Try setting it...
		        // target ID does not change, parent does
                // console.debug("FluxProcessor._handleBetterFormStateChanged input/output/group control: " ,control, "controlType: ", control.controlType);
		        xmlEvent.contextInfo.parentId = "C"+(possibleId);
		        xmlEvent.contextInfo.targetName = "label";
                control.handleStateChanged(xmlEvent.contextInfo);
  	        } else {
		        // Currently the only case encountered where this is needed is for a selectorItem
	            control = dojo.byId("C"+possibleId-2);
	            if (control != undefined) {
                    // console.debug("FluxProcessor._handleBetterFormStateChanged selectorItem control: " ,control, "controlType: ", control.controlType);
		            // targetId stays the same
		            xmlEvent.contextInfo.targetName = "label";
		            xmlEvent.contextInfo.parentId = "C"+(possibleId-2);
                    this._handleBetterFormStateChanged(xmlEvent);
	    	    } else {
	 	            console.warn(warningMsg);
		        }
	        }
	    } else {
            console.error("FluxProcessor betterform-state-changed Error: Processor does not know how to handle betterform-state-changed based on xmlEvent ", xmlEvent.contextInfo.targetId);
        }

    },

    _handleBetterFormInsertRepeatItem:function(xmlEvent) {
        // console.debug("betterform-insert-repeatitem [id: '", xmlEvent.contextInfo.targetId, "'] contextInfo:",xmlEvent.contextInfo);
        var repeatToInsertIntoDOM = dojo.query("*[repeatId='" + xmlEvent.contextInfo.targetId + "']");
        // console.debug("FluxProcessor._handleBetterFormInsertRepeatItem repeatToInsertIntoDOM: ",repeatToInsertIntoDOM);
        var repeatToInsertInto = dijit.byId(dojo.attr(repeatToInsertIntoDOM[0], "id"));
        if (repeatToInsertInto == undefined) {
            // console.debug("FluxProcessor._handleBetterFormInsertRepeatItem ",repeatToInsertIntoDOM);
            // console.dirxml(repeatToInsertIntoDOM[0]);
            dojo.require("betterform.ui.container.Repeat");
            repeatToInsertInto = new betterform.ui.container.Repeat({}, repeatToInsertIntoDOM[0]);
        }
        repeatToInsertInto.handleInsert(xmlEvent.contextInfo);

    },

    _handleBetterFormInsertItemset:function(xmlEvent) {
        // console.debug("betterform-insert-itemset [id: '", xmlEvent.contextInfo.targetId, " / contextInfo:",xmlEvent.contextInfo,']' );
        if (dijit.byId(xmlEvent.contextInfo.targetId) != undefined) {
            // console.debug("betterform-insert-itemset handle Insert [id: '", xmlEvent.contextInfo.targetId, " / dijit:",dijit.byId(xmlEvent.contextInfo.targetId),']' );
            dijit.byId(xmlEvent.contextInfo.targetId).handleInsert(xmlEvent.contextInfo);
        } else {
            var itemsetDOM = dojo.byId(xmlEvent.contextInfo.targetId);
            // console.debug("betterform-insert-itemset [id: '", xmlEvent.contextInfo.targetId, " / dom:'",dojo.byId(xmlEvent.contextInfo.targetId),"']");
            var itemsetType = dojo.attr(itemsetDOM, "dojoType");
            // Prototypes don't have a dojoType, search for controlType instead
            if(itemsetType == undefined) {
                var controlType = dojo.attr(itemsetDOM, "controlType");
                if(controlType == "optGroup") {
                    itemsetType = "betterform.ui.select.OptGroup";
                }
            }

            var itemsetDijit;
            if(itemsetType != undefined) {
                if (itemsetType == "betterform.ui.select.OptGroup") {
                    itemsetDijit = new betterform.ui.select.OptGroup({contextInfo:xmlEvent.contextInfo}, itemsetDOM);
                }
                else if (itemsetType == "betterform.ui.select1.RadioItemset") {
                    itemsetDijit = new betterform.ui.select1.RadioItemset({contextInfo:xmlEvent.contextInfo}, itemsetDOM);
                }
                else if (itemsetType == "betterform.ui.select.CheckBoxItemset") {
                    itemsetDijit = new betterform.ui.select.CheckBoxItemset({contextInfo:xmlEvent.contextInfo}, itemsetDOM);
                }
                else {
                    console.warn("FluxProcessor apply betterform-insert-itemset: Itemset Type " + itemsetType + " not supported yet");
                }
            } else {
                console.warn("FluxProcessor apply betterform-insert-itemset: ItemSet Type is null");
                return;
            }
            // console.debug("betterform-insert-itemset [id: '", xmlEvent.contextInfo.targetId, " / dojotype:'",itemsetType,"']");
            if (itemsetDijit != undefined) {
                itemsetDijit.handleInsert(xmlEvent.contextInfo);
            } else {
                console.warn("FluxProcessor apply betterform-insert-itemset: Error during itemset creation: ItemsetId " + xmlEvent.contextInfo.targetId + " itemsetType: " + itemsetType + " not supported yet");
            }
        }

    },
    _handleBetterFormItemDeleted:function(xmlEvent) {
        console.debug("handle betterform-item-deleted for ", xmlEvent.contextInfo.targetName, " [id: '", xmlEvent.contextInfo.targetId, "'] contextInfo:", xmlEvent.contextInfo);
        if (xmlEvent.contextInfo.targetName == "itemset") {
            dijit.byId(xmlEvent.contextInfo.targetId).handleDelete(xmlEvent.contextInfo);
        }
        else if (xmlEvent.contextInfo.targetName == "repeat" || xmlEvent.contextInfo.targetName == "tbody") {
            var repeatElement = dojo.query("*[repeatId='" + xmlEvent.contextInfo.targetId + "']");
            var repeatDijit  = dijit.byId(dojo.attr(repeatElement[0], "id"));
            repeatDijit.handleDelete(xmlEvent.contextInfo);
            var positionOfDeletedItem = xmlEvent.contextInfo.position;
            if(positionOfDeletedItem <= repeatDijit._getSize()){
                repeatDijit._handleSetRepeatIndex(positionOfDeletedItem);
            }
        }
    },
    _handleBetterFormIndexChanged:function(xmlEvent) {
        var repeatElement = dojo.query("*[repeatId='" + xmlEvent.contextInfo.targetId + "']");
        var repeat = dijit.byId(dojo.attr(repeatElement[0], "id"));
        console.debug("FluxProcessor.betterform-index-changed Repeat: ", repeat, " targetId: ", xmlEvent.contextInfo.targetId);
        repeat.handleSetRepeatIndex(xmlEvent.contextInfo);
    },

    _handleUploadProgressEvent:function(xmlEvent) {
        // console.debug("_handleUploadProgressEvent: xmlEvent:",xmlEvent);
        var xfControlId = xmlEvent.contextInfo.targetid;
        // if XForms Control Dijit allready exists call handleStateChanged on selected control
        if (dijit.byId(xfControlId) != undefined) {
            dijit.byId(xfControlId).updateProgress(xmlEvent.contextInfo.progress);
        } else {
            console.error("error during upload-progress-event: targetId " + xmlEvent.contextInfo.targetId + " does not exist");
        }
    },
    _handleXFormsFocus:function(xmlEvent) {
        console.debug("xforms-focus xmlEvent: ", xmlEvent);
        try {
            var targetName = xmlEvent.contextInfo.targetName;
            if (targetName != "group" && targetName != "repeat" && targetName != "switch" && targetName != "case") {
                var controlToFocus = dijit.byId(xmlEvent.contextInfo.targetId + "-value");
                if(controlToFocus && controlToFocus.focus){
                   controlToFocus.focus();
                }else if(dojo.byId(xmlEvent.contextInfo.targetId)){
                    console.warn("Control is no dijit, focusing domNode: " + xmlEvent.contextInfo.targetId );
                    controlToFocus.domNode.focus();
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
        xfControlId = xmlEvent.contextInfo.targetId + "-value";
        if (dijit.byId(xfControlId) != undefined) {
        	// console.debug("dom-focus-in-dijit control: ",xfControlId);
            dijit.byId(xfControlId)._handleDOMFocusIn();
        } else if (dojo.byId(xfControlId) != undefined) {
        	// console.debug("dom-focus-in-dojo control: ",xfControlId);
            var domControlValue = dojo.byId(xfControlId);
            domControlValue.focus();
        } else {
            console.warn("FluxProcessor._handleDOMFocusIn no Element found for id:", xfControlId, " might have been destroyed");
        }
    },

    _handleXFormsHint:function(xmlEvent) {
        var xfControlId = xmlEvent.contextInfo.targetId;
        var message = dojo.attr(dojo.byId(xfControlId + "-value"), "title");
        dijit.byId("betterformMessageToaster").setContent(message, 'message');
        dijit.byId("betterformMessageToaster").show();
        if (this.webtest == 'true') {
            //only for testing purposes
            this.logTestMessage(message);
        }
    },

    _handleShowHelp:function(xmlEvent) {
        fluxProcessor.currentControlId = xmlEvent.contextInfo.targetId;
        fluxProcessor.showHelp();
    },

    _handleLinkException:function(xmlEvent) {
        console.debug("_handleLinkException: ", xmlEvent);
        if (this.webtest != 'true') {
            console.error("Fatal error - " + xmlEvent.type + ": Failed to load resource: " + xmlEvent.contextInfo.resourceUri);
            alert("xforms-link-exception: " + "resource '" + xmlEvent.contextInfo['resource-uri'] + "' failed with error= '" + xmlEvent.contextInfo['resource-error'] + "'");
        } else {
            //only for testing purposes
            fluxProcessor.logTestMessage("Fatal error - " + xmlEvent.type + ": Failed to load resource: " + xmlEvent.contextInfo.resourceUri);
        }
        //        fluxProcessor.closeSession();
    },

    _handleSwitchToggled:function(xmlEvent) {
        // console.debug("FluxProcessor._handleSwitchToggled xmlEvent:", xmlEvent);
        var tmpSwitch = dijit.byId(xmlEvent.contextInfo.targetId);
        if (tmpSwitch == undefined && dojo.byId(xmlEvent.contextInfo.targetId) != undefined) {
            // console.debug("create new switch: ", xmlEvent);
            dojo.require("betterform.ui.container.Switch");
            tmpSwitch = new betterform.ui.container.Switch({}, dojo.byId(xmlEvent.contextInfo.targetId));
        }
        tmpSwitch.toggleCase(xmlEvent.contextInfo);
    },

    fetchProgress:function(id, fileName) {
        try {
            console.debug("FluxProcessor.fetchProgress id:", id, "fileName: " , fileName , " this.sessionKey:", this.sessionKey);
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
        // console.debug("FluxProcessor.setLocale: Changed locale to: " + locale);
        try {
            Flux.setLocale(locale, this.sessionKey, this.applyChanges);
        }
        catch(ex) {
            fluxProcessor._handleExceptions("Failure executing Flux.setLocale", ex);
        }
    },

    showHelp:function(id) {
        console.debug("showng help for:", id);


        var helpCtrl = dojo.byId(id + '-help');
        if (helpCtrl == undefined) {
            console.warn("No help available for Control Id: '" + id + "'");
            return;
        }

        var helpText = dojo.byId(id + "-help-text");
        var currentState = dojo.style(helpText,"display");

        if(currentState == "none"){
            dojo.style(helpText, { "display":"inline-block"});
        }else{
            dojo.style(helpText, { "display":"none"});
        }
        //make sure that the input control at work does not loose the focus
//        dojo.byId(id).focus();
    },

    getInstanceDocument:function(modelId, instanceId){
        dwr.engine.setErrorHandler(this._handleExceptions);
        Flux.getInstanceDocument(modelId, instanceId, this.sessionKey,this.printInstance);
    },

    printInstance:function(data){
        console.dirxml(data);
        dojo.byId("debugFrame").innerHTML=data;
    }
});
