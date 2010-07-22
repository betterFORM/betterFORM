/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ConvexProcessor");
dojo.require("betterform.FluxProcessor");

    /*
    This class represents the interface to the remote XForms processor (aka 'betterForm Web') with DWR. It is the only class
    actually having dependency on DWR to handle the AJAX part of things and calling remote Java methods on
    de.betterform.web.flux.FluxFacade.
    */
    dojo.declare("betterform.ConvexProcessor",
            "betterform.FluxProcessor",
    {

    sessionKey:"",
    skipshutdown:false,
    isDirty:false,
    factory:null,
    currentControlId:"",
    unloadMsg:"You are about to leave this XForms application",
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
        this.factory = new betterform.ui.UIElementFactory();
    },


        _buildUI: function(startElement){
            alert("ConvexProcessor.buildUI");
            dojo.query("*",startElement).forEach(
                function(input) {
                    // console.debug("element: ", input);

                    //todo:detect right prefix

                    if(input.tagName.indexOf("XF:") != -1){
                       // console.debug("XForms UI element: ", input.tagName);
                    }

                }
            );
        },

        closeSession: function () {
            alert("ConvexProcessor.closeSession");
            //        DWREngine.setErrorHandler(ignoreExceptions);
            //        DWREngine.setAsync(false);
            //        Flux.close(dojo.byId("bfSessionKey").value);
        },

        ignoreExceptions: function (msg) {
            alert("ConvexProcessor.ignoreException");
        },

        //eventually an 'activate' method still makes sense to provide a simple DOMActivate of a trigger Element

        dispatchEvent: function (targetId) {
            alert("ConvexProcessor.dispatch");
            //        this._useLoadingMessage();
            //        DWREngine.setErrorHandler(this._handleExceptions);
            //        DWREngine.setOrdered(true);
            //        Flux.fireAction(this.changeManager.applyChanges, targetId, dojo.byId("bfSessionKey").value);
            //        return false;
        },

        setControlValue: function (id, value) {
            alert("ConvexProcessor.setControlValue");
            //        DWREngine.setErrorHandler(this._handleExceptions);
            //        this._useLoadingMessage();
            //        DWREngine.setOrdered(true);
            //        DWREngine.setErrorHandler(this._handleExceptions);
            //        Flux.setXFormsValue(this.changeManager.applyChanges, id, value, dojo.byId("bfSessionKey").value);
        },

        setRange: function (id, value) {
            alert("ConvexProcessor.setRange");
            //        DWREngine.setErrorHandler(this._handleExceptions);
            //        Flux.setXFormsValue(this.changeManager.applyChanges, id, value, dojo.byId("bfSessionKey").value);
        },


        setRepeatIndex:function (targetRepeatElement) {
            alert("ConvexProcessor.setRepeatIndex");
            //        this._useLoadingMessage();
            //        DWREngine.setErrorHandler(this._handleExceptions);
            //        DWREngine.setOrdered(true);
            //        Flux.setRepeatIndex(this.changeManager.applyChanges, repeatId, targetPosition, dojo.byId("bfSessionKey").value);
        },


        _useLoadingMessage:function() {
            alert("ConvexProcessor.useLoadingMessage");
            //nope
        },

        _handleExceptions:function(msg) {
            alert("ConvexProcessor.handleExceptions");
            console.error(msg);
        },

        setView: function (html) {
            alert("ConvexProcessor.setView");

            var xformsui = dojo.byId("xformsui");

            xformsui.innerHTML = html;
            xformsui.className = "enabled";

            dojo.parser.parse(xformsui);

            return true;

        }
    });
