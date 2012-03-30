/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/behavior","dojo/_base/connect"],
    function(behavior,connect) {
        return {


        // ############################## TRIGGER MAPPINGS ############################################################
        // ############################## TRIGGER MAPPINGS ############################################################
        // ############################## TRIGGER MAPPINGS ############################################################

        '.xfTrigger .xfValue': function(n) {
            var parentId = n.id.substring(0,n.id.lastIndexOf("-"));
            connect.connect(n, "onclick", function(){
                fluxProcessor.dispatchEvent(parentId);
            });
        }
    }
});

