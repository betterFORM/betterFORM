/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.xf.Select1Behavior");
dojo.require("betterform.xf.Select1");
dojo.require("betterform.xf.XFControl");
var select1Behavior = {

    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################

    '.xfSelect1.aMinimal .select1wrapper .xfValue, .xfSelect1.aDefault .select1wrapper .xfValue': function(n) {
        console.debug("select1 field: ",n);

        var xfId = getXfId(n);
        var xfControl = dijit.byId(xfId);

        /*
         if incremental support is needed this eventhandler has to be added for the widget
         */
        dojo.connect(n,"onkeyup",function(evt){
            console.debug("onkeypress",n);
            xfControl.sendValue(n.value,evt);
        });

        dojo.connect(n,"onblur",function(evt){
            console.debug("onblur",n);
            xfControl.sendValue(n.value, evt);
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

