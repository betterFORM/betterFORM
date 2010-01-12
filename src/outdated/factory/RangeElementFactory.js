dojo.provide("betterform.ui.RangeElementFactory");

dojo.require("betterform.ui.AbstractElementFactory");
dojo.declare(
        "betterform.ui.RangeElementFactory",
        betterform.ui.AbstractElementFactory,
{

    createDefaultWidget:function() {
        var newWidget = null;
        var xfValue = dojo.attr(sourceNode,"value");
        var start = 0;
        var end = 10;
        var step = 1;

        if(dojo.attr(sourceNode,"start") != ""){
            start = eval(dojo.attr(sourceNode,"start"));
        }
        if(dojo.attr(sourceNode,"end")!= ""){
            end = eval(dojo.attr(sourceNode,"end"));
        }
        if(dojo.attr(sourceNode,"step") != ""){
            step = eval(dojo.attr(sourceNode,"step"));
        }
        if(dojo.attr(sourceNode,"appearance")=="bf:rating"){

            newWidget = new betterform.ui.range.Rating({
                name:controlId + "-value",
                value:xfValue,
                "class":classValue,
                xfControlId:controlId,
                numStars:end
            }, sourceNode);

        }else {
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
            newWidget= new betterform.ui.range.Slider({
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
                style:"width:200px;"
            },sourceNode);

            // and start them both
            newWidget.startup();
            sliderRules.startup();

        }
        return widget;
    }

});


