/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.container.ContentPaneGroup");

dojo.require("dijit._Widget");
dojo.require("betterform.ui.container.Container");
dojo.require("dijit.layout.ContentPane");

dojo.declare(
        "betterform.ui.container.ContentPaneGroup",
        [betterform.ui.container.Container, dijit.layout.ContentPane],
{

    buildRendering:function() {
        this.inherited(arguments);
        betterform.ui.util.setDefaultClasses(this.domNode);
    },

    // XForms MIPs
    handleStateChanged:function(contextInfo){
        // console.debug("betterform.ui.container.ContentPaneGroup.handleStateChange", contextInfo);
        this.inherited(arguments);

    }

});


