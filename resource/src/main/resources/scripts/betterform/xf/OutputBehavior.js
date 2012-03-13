/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.xf.OutputBehavior");

var outputBehavior = {
    // ############################## OUTPUT MAPPINGS ############################################################
    // ############################## OUTPUT MAPPINGS ############################################################
    // ############################## OUTPUT MAPPINGS ############################################################
    '.xfOutput.xsdString .xfValue': function(n) {
        console.debug("output field: ",n);

        var xfControl = dijit.byId(getXfId(n));

        dojo.connect(xfControl, "handleStateChanged", function(contextInfo){
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
                n.innerHTML = newValue;
            }
        });

    },

    '.xfOutput.xsdString img.xfValue': function(n) {
        //todo: implement + fix missing 'xfValue' on img element
    },

    '.xfOutput.xsdAnyURI .xfValue': function(n) {
        //todo: implement
    }
};

