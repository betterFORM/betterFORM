/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.container.TitlePaneGroup");

dojo.require("dijit._Widget");
dojo.require("betterform.ui.container.Container");
dojo.require("dijit.TitlePane");


dojo.declare(
        "betterform.ui.container.TitlePaneGroup",
        [betterform.ui.container.Container,dijit.TitlePane],
{
    // XForms MIPs
    handleStateChange:function() {
        // console.debug("betterform.ui.container.TitlePaneGroup.handleStateChange");
        this.inherited(arguments);
    }


});


