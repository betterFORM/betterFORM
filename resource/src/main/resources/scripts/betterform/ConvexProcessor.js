/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ConvexProcessor");

dojo.require("dojo._base.html");
dojo.require("betterform.XFormsProcessor");
dojo.require("betterform.ui.UIElementFactory");



    /*
    This class represents the interface to the remote XForms processor (aka 'betterForm Web') with DWR. It is the only class
    actually having dependency on DWR to handle the AJAX part of things and calling remote Java methods on
    de.betterform.web.flux.FluxFacade.
    */
dojo.declare("betterform.ConvexProcessor",
            betterform.XFormsProcessor,
{
    factory:null,
    convex:null,

    constructor:function() {
        console.debug("hurrah - convexProcessor is running");
        this.factory = new betterform.ui.UIElementFactory();
        this.convex = document.getElementById("convex");
        dojo.subscribe("xforms-invalid",function(value){
           console.debug("received event: ", value); 
        });
    },

    publishChange:function(event, object){
        console.debug("publishChange: ",event, object);
         dojo.publish(event,[object]);
    },


    dispatchEventType: function (targetId,event) {
        console.debug("Convex.dispatch");
        convex.dispatch(targetId,event);
    },

    setControlValue: function (id, value) {
        console.debug("Convex.setControlValue");
        convex.setValue(id,value);
    },

    setRange: function (id, value) {
    },


    setRepeatIndex:function (targetRepeatElement){
    },

    setView: function (html) {
        alert("ConvexProcessor.setView");

        var xformsui = document.getElementById("xformsui");

        xformsui.innerHTML = html;
        xformsui.className = "enabled";

        dojo.parser.parse(xformsui);

        return true;

    },
    
    debug:function(message){
        console.debug(message);
    }
});
