/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.output.SourceCode");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dojox.highlight");
//dojo.require("dojox.highlight.languages._www");
dojo.require("dojox.highlight.languages.xml");

dojo.require("betterform.ui.output.Html");

dojo.declare(
        "betterform.ui.output.SourceCode",
        betterform.ui.output.Html,
{

    postCreate:function() {
        this.containerNode.innerHTML = this.value;
//        this.inherited(arguments);
        this.load_css("/betterform/resources/scripts/release/dojo/dojox/highlight/resources/highlight.css");
        this.load_css("/betterform/resources/scripts/release/dojo/dojox/highlight/resources/pygments/pastie.css");
        this.highlight();
    },

    _handleSetControlValue:function(value) {
        // console.debug("betterform.ui.output.SourceCode._handleSetControlValue: Value: ", value);
        this.inherited(arguments);
        this.containerNode.innerHTML = value;
        this.highlight();
    },

    highlight: function() {
        dojo.query("code", this.containerNode).forEach(dojox.highlight.init);
    },

    // todo: refactor! -- fabian.otto@betterform.de
    load_css: function(sheet) {
        var oLink = document.createElement('link');
        oLink.href = sheet;
        oLink.rel = 'stylesheet';
        oLink.type = 'text/css';
        document.body.appendChild(oLink);
    }
});
