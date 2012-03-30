/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/behavior","dojo/_base/connect","dijit/registry"],
    function(behavior,connect,registry) {

        return {

        // ############################## SELECT MAPPINGS ############################################################
        // ############################## SELECT MAPPINGS ############################################################
        // ############################## SELECT MAPPINGS ############################################################

        '.xfSelect.aMinimal .xfValue, .xfSelect.aDefault .xfValue, .xfSelect.aCompact .xfValue': function(n) {
            var xfId = bf.util.getXfId(n);
            var xfControl = registry.byId(xfId);

            connect.connect(n,"onchange",function(evt){
                bf.SelectBehavior.selectMinimalSendValue(xfControl, n,evt);
            });

            connect.connect(n,"onblur",function(evt){
                bf.SelectBehavior.selectMinimalSendValue(xfControl, n,evt);
            });

            xfControl.setValue=function(value) {
                query(".xfSelectorItem",n).forEach(function(item){
                    item.selected = value.indexOf(item.value) != -1;
                });
            };
        },
        '.xfSelect.aFull .xfValue': function(n) {
            var xfId = bf.util.getXfId(n);
            var xfControl = registry.byId(xfId);

            connect.connect(n,"onchange",function(evt){
                bf.SelectBehavior.selectFullSendValue(xfControl, n,evt);
            });

            xfControl.setValue=function(value) {
                query(".xfCheckBoxValue",n).forEach(function(item){
                    item.checked = value.indexOf(item.value) != -1;
                });
            };

            new bf.Select({id:n.id,control:xfControl}, n);
        }
    };


    bf.SelectBehavior.selectMinimalSendValue = function(xfControl,n,evt) {
        var selectedValue = "";
        query(".xfSelectorItem",n).forEach(function(item){
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

    bf.SelectBehavior.selectFullSendValue = function(xfControl,n,evt) {
        var selectedValue = "";
        query(".xfCheckBoxValue",n).forEach(function(item){
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
});
