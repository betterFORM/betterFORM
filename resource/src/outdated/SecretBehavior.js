/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["../../../lib/dojo-release-1.6.1-src/dojo/behavior","../main/lib/dojo-release-1.7.2-src/dojo/_base/connect","dijit/registry"],
    function(behavior,connect,registry) {


    return {

        // ############################## SECRET MAPPINGS ############################################################
        // ############################## SECRET MAPPINGS ############################################################
        // ############################## SECRET MAPPINGS ############################################################
        '.xfSecret .xfValue':function(n){
            console.debug("FOUND: secret input: ",n);

            var xfControl = registry.byId( bf.util.getXfId(n));

            connect.connect(n,"onkeyup",function(evt){
                xfControl.sendValue(n.value,evt);
            });

            connect.connect(n,"onblur",function(evt){
                xfControl.sendValue(n.value, evt);
            });
        }

    }
});

