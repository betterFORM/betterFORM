/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/behavior","dojo/_base/connect","dijit/registry"],
    function(behavior,connect,registry) {

        return {

        // ############################## TEXTAREA MAPPINGS ############################################################
        // ############################## TEXTAREA MAPPINGS ############################################################
        // ############################## TEXTAREA MAPPINGS ############################################################
        // xfControl xfTextarea aDefault xsdString xfEnabled xfReadWrite xfOptional xfValid mediatypeHtml
        '.xfTextarea.mediatypeHtml .xfValue' : function (n) {
            var xfControl = registry.byId(bf.util.getXfId(n));

            xfControl.setValue = function (value) {
                n.innerHTML = value;
            };

            connect.connect(n,"onkeyup",function(evt){
                // console.debug("onkeypress",n);
                xfControl.sendValue(n.value,evt);
            });

            connect.connect(n,"onblur",function(evt){
                // console.debug("onblur",n);
                xfControl.sendValue(n.value, evt);
            });

        }
    }
});

