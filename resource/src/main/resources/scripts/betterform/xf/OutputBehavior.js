/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.xf.OutputBehavior");

dojo.require("betterform.xf.XFControl");
dojo.require("betterform.util");

var outputBehavior = {
    // ############################## OUTPUT MAPPINGS ############################################################
    // ############################## OUTPUT MAPPINGS ############################################################
    // ############################## OUTPUT MAPPINGS ############################################################
    '.xfOutput.xsdString .xfValue': function(n) {
        console.debug("output field: ",n);

        var xfId = getXfId(n);
        var xfControl = dijit.byId(xfId);

        xfControl.setValue = function(value) {
            n.innerHTML = value;
        };

    },

    '.xfOutput.xsdString img.xfValue': function(n) {
        //todo: implement + fix missing 'xfValue' on img element
    },

    '.xfOutput.xsdAnyURI .xfValue': function(n) {
        //todo: implement
    }
};

