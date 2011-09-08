/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

/**
 All Rights Reserved.
 @author Joern Turner
 @author Lars Windauer
 **/

dojo.provide("betterform.BfRequiredMinimal");

// Class to dojo.require a minimal set of dojo classes

dojo.require("betterform.XFormsProcessor");
dojo.require("betterform.FluxProcessor");

dojo.require("betterform.XFormsModelElement");
dojo.require("betterform.ui.util");
dojo.require("betterform.ClientServerEvent");
dojo.require("betterform.ui.Control");
dojo.require("betterform.ui.ControlValue");
dojo.require("betterform.ui.UIElementFactory");
dojo.require("betterform.ui.factory.UIElementFactoryImpl");
dojo.require("betterform.ui.factory.InputElementFactory");
dojo.require("betterform.ui.factory.OutputElementFactory");

// default alert implementation
dojo.require("betterform.ui.common.InlineAlert");
dojo.require("betterform.ui.common.InlineRoundBordersAlert");
dojo.require("betterform.ui.common.ToolTipAlert");

// betterform UI Controls
dojo.require("betterform.ui.input.Boolean");
dojo.require("betterform.ui.input.TextField");
dojo.require("betterform.ui.input.Date");
dojo.require("betterform.ui.input.Time");
dojo.require("betterform.ui.input.DropDownDate");
dojo.require("betterform.ui.input.DateTime");
dojo.require("betterform.ui.output.Link");
dojo.require("betterform.ui.output.Image");
dojo.require("betterform.ui.output.Html");
dojo.require("betterform.ui.output.InputLook");
dojo.require("betterform.ui.output.Html");
dojo.require("betterform.ui.output.Image");


dojo.require("betterform.ui.output.Plain");
dojo.require("betterform.ui.secret.Secret");
dojo.require("betterform.ui.trigger.Button");
dojo.require("betterform.ui.trigger.LinkButton");
dojo.require("betterform.ui.trigger.ImageButton");

// needed for FormsBrowser (?!)
dojo.require("betterform.ui.output.SourceCode");

dojo.require("betterform.ui.container.Container");
dojo.require("betterform.ui.container.ContentPaneGroup");
dojo.require("betterform.ui.container.Group");
dojo.require("betterform.ui.container.OuterGroup");
dojo.require("betterform.ui.container.Dialog");

/** Dojo Classes that are allways required **/
/** Dojo Classes that are allways required **/
/** Dojo Classes that are allways required **/

// used in various forms
dojo.require("dijit.Tooltip");

// iuitially needed to hide (fadeOut) / show  (fadeIn) overlays (e.q. at startup dojo.xsl )

dojo.require("dojo.NodeList-fx");

// Toaster widget for ephemeral messages
dojo.require("dojox.widget.Toaster");

// check if needed
// dojo.require("dojo._base.html");

// Use for displaying XForms help (fluxProcessor)
dojo.require("dojox.layout.FloatingPane");