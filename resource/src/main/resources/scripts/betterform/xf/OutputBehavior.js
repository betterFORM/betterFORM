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

/*
        dojo.connect(dijit.byId(xfId), "_handleSetControlValue", function(value){
            betterform.util.innerHTML(n, value);
        });
*/

/*

        dojo.connect(dijit.byId(xfId), "handleStateChanged", function(contextInfo){
            console.debug("OutputBehaviour.handleStateChanged for:  ",n);
            if(contextInfo){
                console.debug("contextInfo",contextInfo);
            }
            //apply value to widget - handle required, valid and readonly if necessary
            //todo: this is probably not even necessary here?
            var newValue = contextInfo["value"];
            if(newValue == undefined){
                newValue = "";
            }
            console.debug("newValue: ",newValue);
            n.innerHTML = newValue;
        });
*/


    },

    '.xfOutput.xsdString img.xfValue': function(n) {
        //todo: implement + fix missing 'xfValue' on img element
    },

    '.xfOutput.xsdAnyURI .xfValue': function(n) {
        //todo: implement
    }
};

