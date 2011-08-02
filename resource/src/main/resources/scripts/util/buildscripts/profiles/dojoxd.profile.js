/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dependencies = {
	//Strip all console.* calls except console.warn and console.error.
	stripConsole: "normal",

	layers: [
		{
			name: "../betterform/betterform.js",
            dependencies: [
                "betterform.FluxProcessor",
                "betterform.ClientServerEvent",
                "betterform.XFormsModelElement",

                "betterform.ui.UIElementFactory",
                "betterform.ui.Control",
                "betterform.ui.ControlValue",
                "betterform.ui.util",

                "betterform.ui.container.Container",
                "betterform.ui.container.Group",
                "betterform.ui.container.ContentPaneGroup",
                "betterform.ui.container.Repeat",
                "betterform.ui.container.RepeatItem",
                "betterform.ui.container.Switch",
                "betterform.ui.container.TabSwitch",
                "betterform.ui.container.Dialog",

                "betterform.ui.input.Boolean",
                "betterform.ui.input.Date",
                "betterform.ui.input.DateTime",
                "betterform.ui.input.TextField",

                "betterform.ui.output.Html",
                "betterform.ui.output.Image",
                "betterform.ui.output.Link",
                "betterform.ui.output.Plain",
                "betterform.ui.output.InputLook",

                "betterform.ui.range.Rating",
                "betterform.ui.range.Slider",

                "betterform.ui.secret.Secret",

                "betterform.ui.select.CheckBox",
                "betterform.ui.select.CheckBoxGroup",
                "betterform.ui.select.CheckBoxItemset",
                "betterform.ui.select.MultiSelect",
                "betterform.ui.select.OptGroup",

                "betterform.ui.select1.ComboBox",
                "betterform.ui.select1.ComboBoxOpen",
                "betterform.ui.select1.Plain",
                "betterform.ui.select1.RadioButton",
                "betterform.ui.select1.RadioGroup",
                "betterform.ui.select1.RadioItemset",


                "betterform.ui.textarea.DojoEditor",
                "betterform.ui.textarea.HtmlEditor",
                "betterform.ui.textarea.SimpleTextarea",

                "betterform.ui.trigger.Button",
                "betterform.ui.trigger.LinkButton",

                "betterform.ui.upload.Upload",
                "betterform.ui.upload.UploadPlain"
            ]
		}

	],

	prefixes: [
		[ "betterform", "../betterform" ]
	]
};
