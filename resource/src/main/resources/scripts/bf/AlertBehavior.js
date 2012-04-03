/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/behavior","dojo/_base/connect"],
    function(behavior,connect) {

        function subscribe(alert){
            connect.subscribe("xforms-valid", alert, "handleValid");
            connect.subscribe("xforms-invalid", alert, "handleInvalid");

        }

        return {
            'body.ToolTipAlert': function(n) {
                console.debug("AlertBehaviour.found: ToolTipAlert");

                require(["bf/AlertToolTip","dojo/domReady!"],
                    function(AlertToolTip) {
                        var alertToolTip = new AlertToolTip({});
                        subscribe(alertToolTip);
                        // console.debug("created ToolTipAlert:",alertToolTip);

                    });

            },
            'body.InlineAlert': function(n) {
                console.debug("AlertBehaviour.found: InlineAlert");

                require(["bf/AlertInline","dojo/domReady!"],
                    function(AlertInline) {
                        var alertInline= new AlertInline({});
                        subscribe(alertInline);
                        // console.debug("created InlineAlert:",alertInline);

                    });
            }
        }
});

