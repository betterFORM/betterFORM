/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("bf.Select1Behavior");
dojo.require("bf.Select1");
dojo.require("bf.XFControl");
var select1Behavior = {

    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################
    // ############################## SELECT1 MAPPINGS ############################################################

    '.xfSelect1.aMinimal .xfValue, .xfSelect1.aDefault .xfValue': function(n) {
        var xfId = getXfId(n);
        var xfControl = dijit.byId(xfId);

        /*
         if incremental support is needed this eventhandler has to be added for the widget
         */
        dojo.connect(n,"onchange",function(evt){
            // console.debug("onchange",n);
            xfControl.sendValue(n.value,evt);
        });

        dojo.connect(n,"onblur",function(evt){
            // console.debug("onblur",n);
            xfControl.sendValue(n.value, evt);
        });

        new bf.Select1Minimal({id:n.id}, n);

    },
    '.xfSelect1.aCompact .xfValue': function(n) {
        var xfId = getXfId(n);
        var xfControl = dijit.byId(xfId);

        dojo.connect(n,"onblur",function(evt){
            xfControl.sendValue(n.value, evt);
        });
        dojo.connect(n,"onchange",function(evt){
            xfControl.sendValue(n.value,evt);
        });


        new bf.Select1Compact({id:n.id}, n);

    },
    '.xfSelect1.aFull .xfValue': function(n) {
        var xfId = getXfId(n);
        var xfControl = dijit.byId(xfId);

        dojo.query(".xfRadioValue", n).forEach(function(radioValue){
            radioValue.onclick = function(evt) {
                xfControl.sendValue(radioValue.value,evt );
            }
        });

        xfControl.setValue = function(value) {
            dojo.query(".xfRadioValue", n).forEach(function(radioValue){
                if(radioValue.value == value){
                    dojo.attr(radioValue,"checked", true);
                }
            });
        };
/*
        dojo.connect(n,"onblur",function(evt){
            console.debug("handle on blur for select1 full");
            // xfControl.sendValue(n.value, evt);
        });
*/


        new bf.Select1Full({id:n.id,controlId:xfId}, n);

    }
};

