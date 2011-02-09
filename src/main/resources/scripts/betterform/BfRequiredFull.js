/*
* Copyright (c) 2010. betterForm Project - http://www.betterform.de
* All Rights Reserved.
* @author Joern Turner
* @author Lars Windauer
* Licensed under the terms of BSD License
*/

dojo.provide("betterform.BfRequiredFull");

dojo.declare("betterform.BfRequiredFull", null,
{
    // Class to dojo.require all other classes

    constructor:function() {
        dojo.require("betterform.BfRequiredCompact");
        var compactDependencies = new betterform.BfRequiredCompact();
        // Range UI Factory
        dojo.require("betterform.ui.factory.RangeElementFactory");

        // Alerts
        dojo.require("betterform.ui.common.GlobalAlert");
        dojo.require("betterform.ui.common.BowlAlert");


        // UI Controls
        dojo.require("betterform.ui.timeline.TimeLine");
        dojo.require("betterform.ui.tree.OPMLTree");
        dojo.require("betterform.ui.range.Rating");
        dojo.require("betterform.ui.range.Slider");
            // classes required by betterform.ui.range.Slider:
            dojo.require("dijit.form.HorizontalRule");
            dojo.require("dijit.form.HorizontalRuleLabels");

        dojo.require("betterform.ui.textarea.MinimalTextarea");
        dojo.require("betterform.ui.textarea.HtmlEditor");
        dojo.require("betterform.ui.textarea.DojoEditor");
        dojo.require("betterform.ui.textarea.SimpleTextarea");

        /** TODO: verfify if still needed /wanted **/
        // betterform.ui.container.AccordionSwitch

    }
});
