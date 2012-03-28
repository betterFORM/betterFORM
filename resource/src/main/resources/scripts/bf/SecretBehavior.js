/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("bf.SecretBehavior");


var secretBehavior = {

    // ############################## SECRET MAPPINGS ############################################################
    // ############################## SECRET MAPPINGS ############################################################
    // ############################## SECRET MAPPINGS ############################################################
    '.xfSecret .xfValue':function(n){
        console.debug("FOUND: secret input: ",n);

        var xfControl = dijit.byId( getXfId(n));

        dojo.connect(n,"onkeyup",function(evt){
            xfControl.sendValue(n.value,evt);
        });

        dojo.connect(n,"onblur",function(evt){
            xfControl.sendValue(n.value, evt);
        });
    }

};

