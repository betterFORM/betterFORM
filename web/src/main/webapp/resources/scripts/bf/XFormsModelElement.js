/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/_base/declare"],
    function(declare){
        return declare("bf.XFormsModelElement",null, {

    /**
        All Rights Reserved.
        @author Joern Turner
        @author Lars Windauer

        Client-side IDL implemenation of XForms 1.1 4.8.1

        Note: because of the async nature of the server inteface it's not possible to implement the IDL
        with the exact signature, as the remoting functions need to use a callback function to receive the results
        from the async request. To avoid the pain of making a AJAX-driven applications behave synchronously an additional
        param 'func' is required which is the callback function to be called with the response.

    **/
        constructor: function() {
        },

        postCreate:function(){
            console.info("creating XFormsModelElement","[@id=" + this.id + "]");
        },

        getInstanceDocument:function(/*String*/ instanceId, /*callbackFunction*/ func){
            this._useLoadingMessage();
            dwr.engine.setErrorHandler(fluxProcessor._handleExceptions);
            XFormsModelElement.getInstanceDocument(this.id, instanceId, fluxProcessor.sessionKey,func);
        },
        getInstanceAsString:function(instanceId,func){
            this._useLoadingMessage();
            dwr.engine.setErrorHandler(fluxProcessor._handleExceptions);
            XFormsModelElement.getInstanceAsString(this.id, instanceId, fluxProcessor.sessionKey,func);
        },

        rebuild: function(){
            this._useLoadingMessage();
            dwr.engine.setErrorHandler(fluxProcessor._handleExceptions);
            XFormsModelElement.rebuild(this.id, fluxProcessor.getSessionKey(),null);
        },

        recalculate: function(){
            this._useLoadingMessage();
            dwr.engine.setErrorHandler(fluxProcessor._handleExceptions);
            XFormsModelElement.recalculate(this.id, fluxProcessor.getSessionKey(),null);
        },

        revalidate:function(){
            this._useLoadingMessage();
            dwr.engine.setErrorHandler(fluxProcessor._handleExceptions);
            XFormsModelElement.revalidate(this.id, fluxProcessor.getSessionKey(),null);
        },

        refresh:function(){
            this._useLoadingMessage();
            dwr.engine.setErrorHandler(fluxProcessor._handleExceptions);
            XFormsModelElement.refresh(this.id, fluxProcessor.getSessionKey(),null);
        },
        _useLoadingMessage:function(){
            dwr.engine.setPreHook(function() {
                 document.getElementById('indicator').className = 'xfEnabled';
                });

                dwr.engine.setPostHook(function() {
                 document.getElementById('indicator').className = 'xfDisabled';
                });
        }

    });
});
