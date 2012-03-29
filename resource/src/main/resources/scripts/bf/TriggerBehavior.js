/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/behavior"],
    function(behavior) {
        return {


        // ############################## TRIGGER MAPPINGS ############################################################
        // ############################## TRIGGER MAPPINGS ############################################################
        // ############################## TRIGGER MAPPINGS ############################################################

        '.xfTrigger .xfValue': function(n) {
            var parentId = n.id.substring(0,n.id.lastIndexOf("-"));
            dojo.connect(n, "onclick", function(){
                fluxProcessor.dispatchEvent(parentId);
            });
        }
    }
});

