/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/behavior"],
    function(behavior) {

    return {
        'body.ToolTipAlert': function(n) {
            console.debug("AlertBehaviour.found: ToolTipAlert");
        },
        'body.InlineAlert': function(n) {
            console.debug("AlertBehaviour.found: InlineAlert");
        }
    }
});

