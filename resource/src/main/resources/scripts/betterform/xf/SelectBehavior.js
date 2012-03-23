/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.xf.SelectBehavior");


var selectBehavior = {

    // ############################## SELECT MAPPINGS ############################################################
    // ############################## SELECT MAPPINGS ############################################################
    // ############################## SELECT MAPPINGS ############################################################

    '.xfSelect.aMinimal .xfValue, .xfSelect.aDefault .xfValue, .xfSelect.aCompact .xfValue': function(n) {
        var xfId = getXfId(n);
        var xfControl = dijit.byId(xfId);

        /*
         if incremental support is needed this eventhandler has to be added for the widget
         */
        dojo.connect(n,"onchange",function(evt){
            // console.debug("xfSelect.onchange",n);
            // xfControl.sendValue(n.value,evt);
        });

        dojo.connect(n,"onblur",function(evt){
            console.debug("xfSelect.onblur",n);
            var selectedValue = undefined;
            dojo.query(".xfSelectorItem",n).forEach(function(item){
                console.debug("analysing xfSelectorItem item:",item);
                if(item.selected){
                    console.debug("item:",item, " is selected");
                    if(selectedValue  == undefined){
                        selectedValue = item.value;
                    }else {
                        selectedValue = " " + item.value;
                    }
                }
            });
            console.debug("selected items: ",selectedValue);

            xfControl.sendValue(selectedValue, evt);
        });

        // new betterform.xf.Select1Minimal({id:n.id}, n);

    },
    '.xfSelect.aFull .xfValue': function(n) {
        var xfId = getXfId(n);
        var xfControl = dijit.byId(xfId);

        /*
         if incremental support is needed this eventhandler has to be added for the widget
         */
        dojo.connect(n,"onchange",function(evt){
            console.debug("xfSelectFull.onchange",n);
            xfControl.sendValue(n.value,evt);
        });

        dojo.connect(n,"onblur",function(evt){
            console.debug("xfSelectFull.onblur",n);
            xfControl.sendValue(n.value, evt);
        });

        // new betterform.xf.Select1Minimal({id:n.id}, n);

    }

};

