/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.xf.Select1Behavior");
dojo.require("betterform.xf.Select1");
var select1Behavior = {

    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################

    '.xfMinimalSelect1 .select1wrapper .xfValue': function(n) {
        console.debug("select1 field: ",n);

        var xfId = getXfId(n);
        var xfControl = dijit.byId(xfId);

        dojo.connect(dijit.byId(xfId), "handleStateChanged", function(contextInfo){
            // ##### setting value by platform/component-specific means #####
            console.debug("handleStateChanged for:  ",n);
            if(contextInfo){
                console.debug("contextInfo",contextInfo);
            }
            //apply value to widget - handle required, valid and readonly if necessary
            //todo: this is probably not even necessary here?
            var newValue = contextInfo["value"];
            if(newValue != undefined){
                console.debug("newValue: ",newValue);
                n.value=newValue;
            }
        });

        /*
         if incremental support is needed this eventhandler has to be added for the widget
         */
        dojo.connect(n,"onkeyup",function(evt){
            console.debug("onkeypress",n);
            xfControl.setValue(n.value,evt);
//            xfControl.setValue(n.value);
        });

        dojo.connect(n,"onblur",function(evt){
            console.debug("onblur",n);
            xfControl.setValue(n.value, evt);
        });

        new betterform.xf.Select1Minimal({id:n.id}, n);

    },

    '.xfSelect .xfValue':function(n){
        //todo: no sensible mapping for listbox
    },

    '.xfSelect .xfValue':function(n){
        //todo: no sensible mapping for radiolist
    }

};

