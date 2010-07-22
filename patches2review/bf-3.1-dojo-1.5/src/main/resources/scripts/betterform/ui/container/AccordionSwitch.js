/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.container.AccordionSwitch")

dojo.require("dijit.layout.AccordionContainer");
dojo.require("betterform.ui.container.Container");

dojo.declare("betterform.ui.container.AccordionSwitch",
        [betterform.ui.container.Container,dijit.layout.AccordionContainer],
{
    postCreate: function(/*Event*/ evt){
        this.inherited(arguments);

    },

    _onKeyPress: function(/*Event*/ e){
        // console.debug("event:",e);
        this.inherited(arguments);
    },
    toggleCase:function(contextInfo){
        console.debug("betterform.ui.container.AccordionSwitch.toggleCase", contextInfo);
    }
    

});