/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/behavior","dojo/_base/connect","dijit/registry","dojo/query"],
    function(behavior,connect,registry,query) {

        function selectMinimalSendValue(xfControl,n,evt) {
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
        };


        return {

        // ############################## SELECT MAPPINGS ############################################################
        // ############################## SELECT MAPPINGS ############################################################
        // ############################## SELECT MAPPINGS ############################################################

        '.xfSelect.aMinimal .xfValue, .xfSelect.aDefault .xfValue, .xfSelect.aCompact .xfValue': function(n) {
            var xfId = bf.util.getXfId(n);
            var xfControl = registry.byId(xfId);

            connect.connect(n,"onchange",function(evt){
                selectMinimalSendValue(xfControl, n,evt);
            });

            connect.connect(n,"onblur",function(evt){
                selectMinimalSendValue(xfControl, n,evt);
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

            require(["bf/Select"], function(Select) {
                var selectFull = new Select({id:n.id,xfControl:xfControl}, n);

                connect.connect(n,"onchange",function(evt){
                    selectFull.selectFullSendValue(xfControl, n,evt);
                });

            });

            xfControl.setValue=function(value) {
                query(".xfCheckBoxValue",n).forEach(function(item){
                    item.checked = value.indexOf(item.value) != -1;
                });
            };
        }
    };
});
