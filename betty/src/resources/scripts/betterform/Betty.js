/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.Betty");

dojo.require("dojo._base.html");
dojo.require("betterform.XFormsProcessor");
dojo.require("betterform.ui.UIElementFactory");



    /*
    This class represents the interface to the XForms processor (aka 'betterForm BettyProcessor').
    */
dojo.declare("betterform.Betty",
            betterform.XFormsProcessor,
{
    factory:null,
    bettyProcessor:null,
    webtest:false,

    topicLoadURI:null,
    topicRenderMessage:null,
    topicReplaceAll:null,
    topicScriptAction:null,
    topicVersionException:null,
    topicSubmitError:null,
    topicSubmitDone:null,


    constructor:function() {
        console.log("hurrah - Betty is running");
        this.factory = new betterform.ui.UIElementFactory();
        this.bettyProcessor = document.getElementById("bettyProcessor");

        // todo:subcribe to all topics and integrate handler functions
        this.topicRenderMessage = dojo.subscribe("betterform-render-message",this,"_handleBetterFormRenderMessage");
    },

    applyChanges:function(event, object){
        console.log(event, object);
        dojo.publish(event,[object]);
    },

    dispatchEvent:function(targetId){
        console.log("Betty fires trigger " + targetId);
        this.bettyProcessor.dispatch(targetId,"DOMActivate");
    },

    dispatchEventType: function (targetId,event) {
        console.log("Betty dispatching ",event," to ",targetId);
        this.bettyProcessor.dispatch(targetId,event);
    },

    setControlValue: function (id, value) {
        console.log("Betty setControlValue");
        this.bettyProcessor.setValue(id,value);
    },

    setRange: function (id, value) {
    },


    setRepeatIndex:function (targetRepeatElement){
    },

    setView: function (html) {
        alert("Betty.setView");
        var xformsui = document.getElementById("xformsui");
        xformsui.innerHTML = html;
        xformsui.className = "enabled";
        dojo.parser.parse(xformsui);
        return true;
    },

    _handleBetterFormRenderMessage:function(event) {
        var message = event[0].message;
        var level = event[0].level;
        console.debug("FluxProcessor.handleRenderMessage: message=", message );
        console.debug("FluxProcessor.handleRenderMessage: level=", level );

        if (level == "ephemeral") {
            dijit.byId("betterformMessageToaster").setContent(message, 'message');
            dijit.byId("betterformMessageToaster").show();
        }
        else {
            var exception = event.exception;
            if (exception != undefined) {
                console.warn("An Exception occured in Facade: ", exception);
            } else {
                dojo.require("dijit.Dialog");
                var messageDialog = dijit.byId("bfMessageDialog");
                dojo.query("#messageContent",messageDialog.domNode)[0].innerHTML=message;
                messageDialog.show();
            }
        }
    },

    debug:function(message){
        console.debug(message);
    }
});
