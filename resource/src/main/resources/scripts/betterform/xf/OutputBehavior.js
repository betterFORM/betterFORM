/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.xf.OutputBehavior");

dojo.require("betterform.xf.XFControl");
dojo.require("betterform.util");

var outputBehavior = {
    // ############################## OUTPUT MAPPINGS ############################################################
    // ############################## OUTPUT MAPPINGS ############################################################
    // ############################## OUTPUT MAPPINGS ############################################################
    '.xfOutput.xsdString.mediatypeText .xfValue': function(n) {
        console.debug("FOUND: output string: ",n);

        var xfId = getXfId(n);
        var xfControl = dijit.byId(xfId);

        xfControl.setValue = function(value) {
            n.innerHTML = value;
        };

    },
    '.xfOutput.mediatypeImage .xfValue': function(n) {
        console.debug("FOUND: output mediatype image: ",n);

        var xfControl = dijit.byId(getXfId(n));

        xfControl.setValue = function(value) {
            dojo.attr(n, "src", value);
        };

    },

    '.xfOutput.xsdAnyURI .xfValue': function(n) {
        console.debug("FOUND: output anyURI: ",n);

        var xfControl = dijit.byId(getXfId(n));

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
            dojo.attr(n, "href", value);
            n.innerHTML = value;
        }
    },
    '.xfOutput.mediatypeHtml .xfValue': function(n) {
        console.debug("FOUND: output mediatype HTML: ",n);

        dijit.byId(getXfId(n)).setValue = function(value) {
            n.innerHTML = value;
        };
    }
};

