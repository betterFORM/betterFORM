/*
* Copyright (c) 2010. betterForm Project - http://www.betterform.de
* All Rights Reserved.
* @author Joern Turner
* @author Lars Windauer
* Licensed under the terms of BSD License
*/

dojo.provide("betterform.bfRequiredFull");

dojo.declare("betterform.bfRequiredFull", null,
{
    // Class to dojo.require all other classes

    constructor:function() {
        dojo.require("betterform.bfRequiredCompact");
        var compactDependencies = new betterform.bfRequiredCompact();
        // Alerts
        dojo.require("betterform.ui.common.GlobalAlert");
        dojo.require("betterform.ui.common.BowlAlert");
        dojo.require("betterform.ui.common.InlineAlert");
        dojo.require("betterform.ui.common.ToolTipAlert");

        // UI Controls
        dojo.require("betterform.ui.timeline.TimeLine");
        dojo.require("betterform.ui.tree.OPMLTree");
        dojo.require("betterform.ui.range.Rating");
        dojo.require("betterform.ui.range.Slider");
        dojo.require("betterform.ui.textarea.HtmlEditor");
        dojo.require("betterform.ui.textarea.DojoEditor");
        dojo.require("betterform.ui.textarea.SimpleTextarea");

        /** CONTAINER **/
        dojo.require("betterform.ui.container.TabSwitch");
        dojo.require("betterform.ui.container.TitlePaneGroup");
        /** TODO: verfify if still needed /wanted **/
        // betterform.ui.container.AccordionSwitch

    }
});
