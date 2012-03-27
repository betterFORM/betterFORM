/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.xf.SelectBehavior");

dojo.require("betterform.xf.Select");

var selectBehavior = {

    // ############################## SELECT MAPPINGS ############################################################
    // ############################## SELECT MAPPINGS ############################################################
    // ############################## SELECT MAPPINGS ############################################################

    '.xfSelect.aMinimal .xfValue, .xfSelect.aDefault .xfValue, .xfSelect.aCompact .xfValue': function(n) {
        var xfId = getXfId(n);
        var xfControl = dijit.byId(xfId);

        dojo.connect(n,"onchange",function(evt){
            betterform.xf.SelectBehavior.selectMinimalSendValue(xfControl, n,evt);
        });

        dojo.connect(n,"onblur",function(evt){
            betterform.xf.SelectBehavior.selectMinimalSendValue(xfControl, n,evt);
        });

        xfControl.setValue=function(value) {
            dojo.query(".xfSelectorItem",n).forEach(function(item){
                item.selected = value.indexOf(item.value) != -1;
            });
        };
    },
    '.xfSelect.aFull .xfValue': function(n) {
        var xfId = getXfId(n);
        var xfControl = dijit.byId(xfId);

        dojo.connect(n,"onchange",function(evt){
            betterform.xf.SelectBehavior.selectFullSendValue(xfControl, n,evt);
        });

        xfControl.setValue=function(value) {
            dojo.query(".xfCheckBoxValue",n).forEach(function(item){
                item.checked = value.indexOf(item.value) != -1;
            });
        };

        new betterform.xf.Select({id:n.id,control:xfControl}, n);
    }
};


betterform.xf.SelectBehavior.selectMinimalSendValue = function(xfControl,n,evt) {
    var selectedValue = "";
    dojo.query(".xfSelectorItem",n).forEach(function(item){
        if(item.selected){
            if(selectedValue  == ""){
                selectedValue = item.value;
            }else {
                selectedValue += " " + item.value;
            }
        }
    });
    xfControl.sendValue(selectedValue, evt);
}

betterform.xf.SelectBehavior.selectFullSendValue = function(xfControl,n,evt) {
    var selectedValue = "";
    dojo.query(".xfCheckBoxValue",n).forEach(function(item){
        if(item.checked){
            if(selectedValue  == ""){
                selectedValue = item.value;
            }else {
                selectedValue += " " + item.value;
            }
        }
    });
    xfControl.sendValue(selectedValue, evt);
}

