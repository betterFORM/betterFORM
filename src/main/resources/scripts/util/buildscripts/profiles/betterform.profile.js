dependencies = {
	//Strip all console.* calls except console.warn and console.error.
	stripConsole: "normal",

	layers: [
		{
			name: "../dijit/dijit.js",
			dependencies: [
				"dijit.dijit"
			]
		},
		{
			name: "../dijit/dijit-all.js",
			layerDependencies: [
				"../dijit/dijit.js"
			],
			dependencies: [
				"dijit.dijit-all"
    			]
		},
		{
			name: "../dojox/off/offline.js",
			layerDependencies: [
			],
			dependencies: [
				"dojox.off.offline"
			]
		},
		{
			name: "../dojox/grid/DataGrid.js",
			dependencies: [
				"dojox.grid.DataGrid"
			]
		},
		{
			name: "../dojox/gfx.js",
			dependencies: [
				"dojox.gfx"
			]
		},
		// FIXME:
		//		we probably need a better structure for this layer and need to
		//		add some of the most common themes
		{
			name: "../dojox/charting/widget/Chart2D.js",
			dependencies: [
				"dojox.charting.widget.Chart2D",
				"dojox.charting.widget.Sparkline",
				"dojox.charting.widget.Legend"
			]
		},
		{
			name: "../dojox/dtl.js",
			dependencies: [
				"dojox.dtl",
				"dojox.dtl.Context",
				"dojox.dtl.tag.logic",
				"dojox.dtl.tag.loop",
				"dojox.dtl.tag.date",
				"dojox.dtl.tag.loader",
				"dojox.dtl.tag.misc",
				"dojox.dtl.ext-dojo.NodeList"
			]
                },
		{
			name: "../betterform/betterform.js",
//			layerDependencies: [
//                            "../dijit/dijit-all.js"
//                        ],
                        dependencies: [
                            "betterform.FluxProcessor",
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
                            "betterform.ui.input.Boolean",
                            "betterform.ui.input.Date",
                            "betterform.ui.input.TextField",
                            "betterform.ui.output.Html",
                            "betterform.ui.output.Image",
                            "betterform.ui.output.Link",
                            "betterform.ui.output.Plain",
                            "betterform.ui.range.Rating",
                            "betterform.ui.range.Slider",
                            "betterform.ui.secret.Secret",
                            "betterform.ui.select1.ComboBox",
                            "betterform.ui.select1.ComboBoxOpen",
                            "betterform.ui.select1.Plain",
                            "betterform.ui.select1.RadioButton",
                            "betterform.ui.select1.RadioGroup",
                            "betterform.ui.select1.RadioItemset",
                            "betterform.ui.select.CheckBox",
                            "betterform.ui.select.CheckBoxGroup",
                            "betterform.ui.select.CheckBoxItemset",
                            "betterform.ui.select.MultiSelect",
                            "betterform.ui.select.OptGroup",
                            "betterform.ui.textarea.DojoEditor",
                            "betterform.ui.textarea.HtmlEditor",
                            "betterform.ui.textarea.SimpleTextarea",
                            "betterform.ui.trigger.Button",
                            "betterform.ui.trigger.LinkButton",
                            "betterform.ui.upload.Upload",
                            "betterform.ui.upload.UploadPlain",
                            "dojox.widget.Toaster"
//                            "dojox.layout.FloatingPane"
                        ]
		}
              
	],

	prefixes: [
		[ "dijit", "../dijit" ],
		[ "dojox", "../dojox" ],
		[ "betterform", "../betterform" ]
	]
};
/*

                "betterform.ui.tree.OpmlTree.js",
                "betterform.ui.timeline.TimeLine.js",
*/
