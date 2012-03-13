/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.xf.TriggerBehavior");
dojo.require("betterform.xf.XFControl");
dojo.require("dijit.form.DateTextBox");

var triggerBehavior = {


    // ############################## TRIGGER MAPPINGS ############################################################
    // ############################## TRIGGER MAPPINGS ############################################################
    // ############################## TRIGGER MAPPINGS ############################################################

    '.xfTrigger .xfValue': function(n) {
        console.debug("node: ",n);
        var parentId = n.id.substring(0,n.id.lastIndexOf("-"));

        //connecting widget to XFControl listening for external value changes (coming from FluxProcessor)
        dojo.connect(n, "onclick", function(){
            // ##### setting value by platform/component-specific means #####
//            console.debug("self:  ",n);
//            console.debug("self:  ",parentId);
            fluxProcessor.dispatchEvent(parentId);
        });
        //todo: add onblur res. onchange handler
    }


};

