/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/behavior","dojo/dom-attr","dijit/registry","bf/util"],
    function(behavior,domAttr,registry) {


    return {
        // ############################## OUTPUT MAPPINGS ############################################################
        // ############################## OUTPUT MAPPINGS ############################################################
        // ############################## OUTPUT MAPPINGS ############################################################
        '.xfOutput.mediatypeText .xfValue': function(n) {
            // console.debug("FOUND .xfOutput.mediatypeText .xfValuelue ",n);

            var xfId = bf.util.getXfId(n);
            var xfControl = registry.byId(xfId);

            xfControl.setValue = function(value) {
                // console.debug("xfControl.setValue: .xfOutput.mediatypeText .xfValue")
                n.innerHTML = value;
            };

        },
        '.xfOutput.mediatypeImage .xfValue': function(n) {
            // console.debug("FOUND: output mediatype image: ",n);

            var xfControl = registry.byId(bf.util.getXfId(n));

            xfControl.setValue = function(value) {
                domAttr.set(n, "src", value);
            };

        },

        '.xfOutput.xsdAnyURI .xfValue': function(n) {
            // console.debug("FOUND: .xfOutput.xsdAnyURI .xfValue",n);

            var xfControl = registry.byId(bf.util.getXfId(n));

            //todo: this solution works in FF - others have to be tested
            //todo: use domStyle.set
            xfControl.setReadonly = function(){
                domAttr.set(n,"style","pointer-events:none;cursor:default;")
            };

            xfControl.setReadwrite = function(){
                //todo: this is dirty - there might be a style already
                n.removeAttribute("style");
            };

            xfControl.setValue = function(value) {
                // console.debug("xfControl.setValue: .xfOutput.xsdAnyURI .xfValue")
                domAttr.set(n, "href", value);
                n.innerHTML = value;
            };
        },
        '.xfOutput.mediatypeHtml .xfValue': function(n) {
            // console.debug("FOUND: output mediatype HTML: ",n);

            registry.byId(bf.util.getXfId(n)).setValue = function(value) {
                n.innerHTML = value;
            };
        }
    }
});

