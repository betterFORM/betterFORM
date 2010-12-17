/**
	All Rights Reserved.
	@author Joern Turner
	@author Lars Windauer


**/
dojo.provide("betterform.resourceLoader.Minimal");

// betterFORM Lifecycle Classes
dojo.require("betterform.FluxProcessor");
dojo.require("betterform.XFormsProcessor");


dojo.require("betterform.XFormsModelElement");
dojo.require("betterform.ui.util");
dojo.require("betterform.ClientServerEvent");
dojo.require("betterform.ui.Control");
dojo.require("betterform.ui.ControlValue");
dojo.require("betterform.ui.UIElementFactory");

// default alert implementation
dojo.require("betterform.ui.common.InlineRoundBordersAlert");


// betterform UI Controls
dojo.require("betterform.ui.input.Boolean");
dojo.require("betterform.ui.input.TextField");
dojo.require("betterform.ui.output.Plain");
dojo.require("betterform.ui.secret.Secret");
dojo.require("betterform.ui.trigger.Button");

/** Dojo Classes that are allways required **/
/** Dojo Classes that are allways required **/
/** Dojo Classes that are allways required **/


// iuitially needed to hide (fadeOut) / show  (fadeIn) overlays (e.q. at startup dojo.xsl )
dojo.require("dojo.NodeList-fx");

// Toaster widget for ephemeral messages
dojo.require("dojox.widget.Toaster");

// dojo.require("dojo._base.html");

// Use for displaying XForms help (fluxProcessor)
dojo.require("dojox.layout.FloatingPane");


dojo.declare("betterform.resourceLoader.Minimal", null,
{
        // Class just to require other classes
});
