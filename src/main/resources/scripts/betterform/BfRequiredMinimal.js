/**
	All Rights Reserved.
	@author Joern Turner
	@author Lars Windauer
**/

dojo.provide("betterform.BfRequiredMinimal");

    // Class to dojo.require a minimal set of dojo classes

dojo.declare("betterform.BfRequiredMinimal", null,
{
    // betterFORM Lifecycle Classes

    constructor:function() {
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
        dojo.require("betterform.ui.factory.RangeElementFactory");
        dojo.require("betterform.ui.factory.SelectElementFactory");

        // default alert implementation
        dojo.require("betterform.ui.common.InlineRoundBordersAlert");


        // betterform UI Controls
        dojo.require("betterform.ui.input.Boolean");
        dojo.require("betterform.ui.input.TextField");
        dojo.require("betterform.ui.output.Plain");
        dojo.require("betterform.ui.secret.Secret");
        dojo.require("betterform.ui.trigger.Button");
        // needed for FormsBrowser (?!)
        dojo.require("betterform.ui.output.SourceCode");

        /** Dojo Classes that are allways required **/
        /** Dojo Classes that are allways required **/
        /** Dojo Classes that are allways required **/


        // iuitially needed to hide (fadeOut) / show  (fadeIn) overlays (e.q. at startup dojo.xsl )
        dojo.require("dojo.NodeList-fx");

        // Toaster widget for ephemeral messages
        dojo.require("dojox.widget.Toaster");

        // check if needed
        // dojo.require("dojo._base.html");

        // Use for displaying XForms help (fluxProcessor)
        dojo.require("dojox.layout.FloatingPane");
    }
});
