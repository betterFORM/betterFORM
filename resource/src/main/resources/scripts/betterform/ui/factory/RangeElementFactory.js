/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.factory.RangeElementFactory");

// creates all betterForm Widgets for xf:* UI Controls dependent on datatype and controltype.
dojo.declare(
        "betterform.ui.factory.RangeElementFactory",
        null,
{

    createRangeWidget:function(sourceNode, controlId, classValue,appearance) {
        // console.debug("UIElementFactory Range: srcNode: ", sourceNode, " start: ", start, " end: ", end, " step: ",step);
        if(appearance=="bf:rating"){
            return this.createRangeRatingWidget(controlId, sourceNode,classValue)
        }else {
            return this.createRangeSliderWidget(controlId, sourceNode,classValue)

        }
    },

    createRangeRatingWidget:function(controlId, sourceNode, classValue) {
        // console.debug("createRangeRatingWidget: controlId:",controlId, " sourceNode:",sourceNode );
        var xfValue = parseInt(dojo.attr(sourceNode,"value"), "10");
        var end = 10;
        if(dojo.attr(sourceNode,"end")!= ""){
            end = parseInt(dojo.attr(sourceNode, "end"), "10");
        } else if (dojo.attr(sourceNode,"end")== "" && dojo.attr(sourceNode,"start") != "") {
            end = parseInt(dojo.attr(sourceNode,"start"), "10") + end;
        }
        if(xfValue > end) {
            xfValue = end;
        }

        return new betterform.ui.range.Rating({
            name:controlId + "-value",
            value:xfValue,
            "class":classValue,
            title:dojo.attr(sourceNode,"title"),
            xfControlId:controlId,
            numStars:end
        }, sourceNode);
    },



    createRangeSliderWidget:function(controlId, sourceNode, classValue) {
        // console.debug("createRangeSliderWidget: controlId:",controlId, " sourceNode:",sourceNode );
        var xfValue = dojo.attr(sourceNode,"value");
        if (xfValue == "") {
            xfValue = 0;
        } else {
            xfValue = parseInt(xfValue, "10");
        }
        // console.debug("createRangeSliderWidget: xfValue:",xfValue);
        var start = 0;
        var end = 10;
        var step = 1;

        if(dojo.attr(sourceNode,"start") != ""){
            start = eval(dojo.attr(sourceNode,"start"));
        }
        if(dojo.attr(sourceNode,"end")!= ""){
            end = parseInt(dojo.attr(sourceNode,"end") , "10");
        } else if (dojo.attr(sourceNode,"end")== "" && dojo.attr(sourceNode,"start") != "") {
            end = parseInt(dojo.attr(sourceNode,"start"), "10") + end;
        }
        if(dojo.attr(sourceNode,"step") != ""){
            step = parseInt(dojo.attr(sourceNode,"step"), "10");
        }
        if(xfValue > end) {
            xfValue = end;
        }
        if(xfValue < start) {
            xfValue = start;
        }
        var discreteValues = ((end - start) / step) +1;
        // create and setup Range Rules
        var rulesNode = document.createElement('div');

        sourceNode.appendChild(rulesNode);
        var sliderRules = new dijit.form.HorizontalRule({
            count: discreteValues,
            container: "topDecoration",
            style:"height:4px;"
        },rulesNode);
        // create and setup Range Labels
        var labelNode = document.createElement('div');
        sourceNode.appendChild(labelNode);

        // setup the labels
        var sliderLabels = new dijit.form.HorizontalRuleLabels({
            count: 5,
            style: "height:1.2em;font-size:75%;color:gray;",
            labels: [start,end]
        },labelNode);
        // Create Slider
        var newRangeSliderWidget = null;
        newRangeSliderWidget= new betterform.ui.range.Slider({
            value:xfValue,
            name:controlId + "-value",
            slideDuration:0,
            minimum:start,
            maximum:end,
            discreteValues:discreteValues,
            intermediateChanges:"true",
            showButtons:"true",
            "class":classValue,
            xfControlId:controlId,
            style: "width:200px;"
        },sourceNode);
        // and start them both
        newRangeSliderWidget.startup();
        sliderRules.startup();
        return newRangeSliderWidget;
    }

});


