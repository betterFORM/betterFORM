/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.container.AccordionSwitchPane")

dojo.require("betterform.ui.container.Container");

dojo.declare("betterform.ui.container.AccordionSwitchPane",
        dijit.layout.AccordionPane,
{
    caseId:"null",
	_onTitleClick: function(){
        // console.debug("_onTitleClick",this, " caseId: ", this.caseId);
        this.inherited(arguments);
        var btnToActivate = "t-" + this.caseId;
        fluxProcessor.dispatchEvent(btnToActivate);
	}

});
