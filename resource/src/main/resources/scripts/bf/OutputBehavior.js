/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/behavior"],
    function(behavior) {


    return {
        // ############################## OUTPUT MAPPINGS ############################################################
        // ############################## OUTPUT MAPPINGS ############################################################
        // ############################## OUTPUT MAPPINGS ############################################################
        '.xfOutput.mediatypeText .xfValue': function(n) {
            // console.debug("FOUN.xfOutput.mediatypeText .xfValuelue ",n);

            var xfId = bf.XFControl.getXfId(n);
            var xfControl = dijit.byId(xfId);

            xfControl.setValue = function(value) {
                // console.debug("xfControl.setValue: .xfOutput.mediatypeText .xfValue")
                n.innerHTML = value;
            };

        },
        '.xfOutput.mediatypeImage .xfValue': function(n) {
            // console.debug("FOUND: output mediatype image: ",n);

            var xfControl = dijit.byId(bf.XFControl.getXfId(n));

            xfControl.setValue = function(value) {
                dojo.attr(n, "src", value);
            };

        },

        '.xfOutput.xsdAnyURI .xfValue': function(n) {
            // console.debug("FOUND: .xfOutput.xsdAnyURI .xfValue",n);

            var xfControl = dijit.byId(bf.XFControl.getXfId(n));

            //todo: this solution works in FF - others have to be tested
            //todo: use dojo.style
            xfControl.setReadonly = function(){
                dojo.attr(n,"style","pointer-events:none;cursor:default;")
            };

            xfControl.setReadwrite = function(){
                //todo: this is dirty - there might be a style already
                n.removeAttribute("style");
            }

            xfControl.setValue = function(value) {
                // console.debug("xfControl.setValue: .xfOutput.xsdAnyURI .xfValue")
                dojo.attr(n, "href", value);
                n.innerHTML = value;
            }
        },
        '.xfOutput.mediatypeHtml .xfValue': function(n) {
            // console.debug("FOUND: output mediatype HTML: ",n);

            dijit.byId(bf.XFControl.getXfId(n)).setValue = function(value) {
                n.innerHTML = value;
            };
        }
    }
});

