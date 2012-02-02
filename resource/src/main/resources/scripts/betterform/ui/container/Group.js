/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
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
    },

    // Might be moved to Container.js?
     _setLabel:function( value) {
        var targetId = this.id;
        var labelNode = dojo.byId(targetId + "-label");
        // labelledBy is an alertnative way to find the corresponding label.
        // Compact repeats only have this at the moment
        if (labelNode == undefined && dojo.attr(this.domNode, "labelledBy") != undefined) {
            labelNode = dojo.byId(dojo.attr(this.domNode, "labelledBy"));
        }
        if (labelNode != undefined && value != undefined) {
            labelNode.innerHTML = value;
            labelNode.title = value;
        }
     }
});


