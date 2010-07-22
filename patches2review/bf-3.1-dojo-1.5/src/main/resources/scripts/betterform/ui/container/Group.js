/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.container.Group");

dojo.require("dijit._Widget");
dojo.require("betterform.ui.container.Container");


dojo.declare(
        "betterform.ui.container.Group",
        betterform.ui.container.Container,
{
    // XForms MIPs
    handleStateChange:function() {
        // console.debug("betterform.ui.container.Group.handleStateChange", contextInfo);
        this.inherited(arguments);
    }


});


