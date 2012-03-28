/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("bf.XFormsProcessor");

dojo.require("dijit._Widget");

/*
This class represents the interface to the XForms processor (aka 'betterForm Web'). It is the only class
actually having dependency on DWR to handle the AJAX part of things and calling remote Java methods on
de.betterform.web.flux.FluxFacade.
*/
dojo.declare("bf.XFormsProcessor",
        dijit._Widget,
{
    sessionKey:"",

    constructor: function() {
        console.info("creating XFormsProcessor");
    },

    init: function(){
    },
    
    keepAlive: function() {
    },

    closeSession: function() {
    },

    ignoreExceptions: function (msg){
    },

    //eventually an 'activate' method still makes sense to provide a simple DOMActivate of a trigger Element

    dispatchEvent: function (targetId) {
    },

    setControlValue: function (id, value) {
    },

    setRange: function (id, value) {
    },


    setRepeatIndex:function (targetRepeatElement){
    },


    _useLoadingMessage:function(){
    },

    _handleExceptions:function(msg) {
        console.error(msg);
    }
});
