/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/behavior","dojo/dom-attr","dojo/_base/connect","dijit/registry", "dijit/form/HorizontalSlider","dijit/form/HorizontalRuleLabels","dijit/form/HorizontalRule"],
    function(behavior,domAttr,connect,registry, HorizontalSlider, HorizontalRuleLabels,HorizontalRule) {

        return {


        // ############################## RANGE MAPPINGS ############################################################
        // ############################## RANGE MAPPINGS ############################################################
        // ############################## RANGE MAPPINGS ############################################################
        '.xfRange.xsdInteger .xfValue':function(n){
            // console.debug("Found xf:range: node:",n);
            var xfId = n.id.substring(0,n.id.lastIndexOf("-"));
            var xfControl = registry.byId(xfId);

            var xfValue = domAttr.get(n,"value");
            if (xfValue == "") {
                xfValue = 0;
            } else {
                xfValue = parseInt(xfValue, "10");
            }
            // console.debug("createRangeSliderWidget: xfValue:",xfValue);
            var start = 0; var end = 10; var step = 1;
            var minAttr = domAttr.get(n,"min");
            if(minAttr != ""){ start = parseInt(minAttr , "10"); }
            var maxAttr = domAttr.get(n,"max");
            if(maxAttr!= ""){
                end = parseInt(maxAttr , "10");
            } else if (maxAttr == "" && minAttr != "") {
                end = parseInt(minAttr, "10") + end;
            }
            var stepAttr = domAttr.get(n,"step");

            if(stepAttr != ""){step = parseInt(stepAttr, "10");}
            if(xfValue > end) {xfValue = end;}
            if(xfValue < start) {xfValue = start;}
            var discreteValues = ((end - start) / step) +1;
            // create and setup Range Rules
            var rulesNode = document.createElement('div');

            n.appendChild(rulesNode);
            var sliderRules = new HorizontalRule({
                count: discreteValues,
                container: "topDecoration",
                style:"height:4px;"
            },rulesNode);
            // create and setup Range Labels
            var labelNode = document.createElement('div');
            n.appendChild(labelNode);

            // setup the labels
            var sliderLabels = new HorizontalRuleLabels({
                count: 5,
                style: "height:1.2em;font-size:75%;color:gray;",
                labels: [start,end]
            },labelNode);
            // Create Slider
            var slider = new HorizontalSlider({
                value:xfValue,
                slideDuration:0,
                minimum:start,
                className:"xfValue",
                maximum:end,
                discreteValues:discreteValues,
                intermediateChanges:"true",
                showButtons:"true",
                style: "width:200px;display:inline-block;"
            },n);
            // and start them both
            slider.startup();
            sliderRules.startup();

            xfControl.setValue = function(value){
                slider._setValueAttr(value, true);
            };

            xfControl.setReadonly = function() {
                slider.set('readOnly', true);
            };
            xfControl.setReadwrite = function() {
                slider.set('readOnly', false);
            };

            connect.connect(slider, "_setValueAttr", function(/*Number*/ value, /*Boolean?*/ priorityChange) {
                if(priorityChange){
                    xfControl.sendValue(value);
                }
            });
        }

        //todo: no sensible mapping for rating control yet

    }
});
