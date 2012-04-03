/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/behavior","dojo/dom-attr","dojo/_base/connect","dijit/registry","dojo/query"],
    function(behavior,domAttr,connect,registry,query) {

        /**
         *  Overwriten "abstract" API function on XFControl to handle updating of control values
         * @param xfControlDijit
         * @param node
         */
        function overwriteSetValue(xfControlDijit, node) {
            xfControlDijit.setValue = function(value, schemavalue) {
                domAttr.set(node, "value", value);
            };
        }

        return {


        // ############################## SELECT1 MAPPINGS ############################################################
        // ############################## SELECT1 MAPPINGS ############################################################
        // ############################## SELECT1 MAPPINGS ############################################################
        // ############################## SELECT1 MAPPINGS ############################################################
        // ############################## SELECT1 MAPPINGS ############################################################
        // ############################## SELECT1 MAPPINGS ############################################################

        '.xfSelect1.aMinimal .xfValue, .xfSelect1.aDefault .xfValue': function(n) {
            var xfId = bf.util.getXfId(n);
            var xfControl = registry.byId(xfId);

            overwriteSetValue(xfControl,n);
            /*
             if incremental support is needed this eventhandler has to be added for the widget
             */
            connect.connect(n,"onchange",function(evt){
                // console.debug("onchange",n);
                xfControl.sendValue(n.value,evt);
            });

            connect.connect(n,"onblur",function(evt){
                // console.debug("onblur",n);
                xfControl.sendValue(n.value, evt);
            });
            require(["bf/Select1ComboBox"], function(Select1ComboBox) {
                new Select1ComboBox({id:n.id}, n);
            })


        },
        '.xfSelect1.aCompact .xfValue': function(n) {
            var xfId = bf.util.getXfId(n);
            var xfControl = registry.byId(xfId);

            overwriteSetValue(xfControl,n);

            connect.connect(n,"onblur",function(evt){
                xfControl.sendValue(n.value, evt);
            });
            connect.connect(n,"onchange",function(evt){
                xfControl.sendValue(n.value,evt);
            });
            require(["bf/Select1ComboBox"], function(Select1ComboBox) {
                new Select1ComboBox({id:n.id}, n);
            });

        },
        '.xfSelect1.aFull .xfValue': function(n) {
            var xfId = bf.util.getXfId(n);
            var xfControl = registry.byId(xfId);

            query(".xfRadioValue", n).forEach(function(radioValue){
                radioValue.onclick = function(evt) {
                    xfControl.sendValue(radioValue.value,evt );
                }
            });

            xfControl.setValue = function(value) {
                query(".xfRadioValue", n).forEach(function(radioValue){
                    if(radioValue.value == value){
                        domAttr.set(radioValue,"checked", true);
                    }
                });
            };
    /*
            connect.connect(n,"onblur",function(evt){
                console.debug("handle on blur for select1 full");
                // xfControl.sendValue(n.value, evt);
            });
    */

            require(["bf/Select1Radio"], function(Select1Radio) {
                new Select1Radio({id:n.id,controlId:xfId}, n);
            });
        }
    }
});

