/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/behavior","dojo/_base/connect"],
    function(behavior,connect) {


    return {

        // ############################## SECRET MAPPINGS ############################################################
        // ############################## SECRET MAPPINGS ############################################################
        // ############################## SECRET MAPPINGS ############################################################
        '.xfSecret .xfValue':function(n){
            console.debug("FOUND: secret input: ",n);

            var xfControl = dijit.byId( bf.XFControl.getXfId(n));

            connect.connect(n,"onkeyup",function(evt){
                xfControl.sendValue(n.value,evt);
            });

            connect.connect(n,"onblur",function(evt){
                xfControl.sendValue(n.value, evt);
            });
        }

    }
});

