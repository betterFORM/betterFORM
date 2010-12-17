/*
* Copyright (c) 2010. betterForm Project - http://www.betterform.de
* All Rights Reserved.
* @author Joern Turner
* @author Lars Windauer
* Licensed under the terms of BSD License
*/

dojo.provide("betterform.bfRequiredCompact");

dojo.declare("betterform.bfRequiredCompact", null,
{
    // Class to dojo.require all other classes

    constructor:function() {
        dojo.require("betterform.bfRequiredMinimal");
        compactDependencies = new betterform.bfRequiredMinimal();
        // UI Controls
        dojo.require("betterform.ui.input.Date");
        dojo.require("betterform.ui.input.DateTime");
        dojo.require("betterform.ui.output.Link");
        dojo.require("betterform.ui.output.Image");
        dojo.require("betterform.ui.output.Html");
        dojo.require("betterform.ui.output.InputLook");
        dojo.require("betterform.ui.output.Html");
        dojo.require("betterform.ui.output.Image");

        
        dojo.require("betterform.ui.select.CheckBox");
        dojo.require("betterform.ui.select.CheckBoxGroup");
        dojo.require("betterform.ui.select.OptGroup");
        dojo.require("betterform.ui.select.MultiSelect");
        dojo.require("betterform.ui.select.CheckBoxItemset");

        dojo.require("betterform.ui.select1.ComboBox");
        dojo.require("betterform.ui.select1.ComboBoxOpen");
        dojo.require("betterform.ui.select1.Plain");
        dojo.require("betterform.ui.select1.RadioButton");
        dojo.require("betterform.ui.select1.RadioGroup");
        dojo.require("betterform.ui.select1.RadioItemset");

        dojo.require("betterform.ui.textarea.MinimalTextarea");

        dojo.require("betterform.ui.trigger.LinkButton");
        dojo.require("betterform.ui.trigger.ImageButton");
        dojo.require("betterform.ui.upload.UploadPlain");

        /** CONTAINER **/
        dojo.require("betterform.ui.container.Container");
        dojo.require("betterform.ui.container.ContentPaneGroup");
        dojo.require("betterform.ui.container.Group");
        dojo.require("betterform.ui.container.OuterGroup");
        dojo.require("betterform.ui.container.Dialog");
        dojo.require("betterform.ui.container.Repeat");
        dojo.require("betterform.ui.container.RepeatItem");
        dojo.require("betterform.ui.container.Switch");
    }
});
