dojo.provide("betterform.ui.OutputElementFactory");

dojo.require("betterform.ui.AbstractElementFactory");
dojo.declare(
        "betterform.ui.OutputElementFactory",
        betterform.ui.AbstractElementFactory,
{

    createDefaultWidget:function() {
        console.debug("betterform.ui.UIElementFactory.createOutputWidget()  for Control " + controlId + " based on sourceNode:", sourceNode, " datatype:", dataType);
        var newWidget = null;
        var xfValue = sourceNode.innerHTML;
        var outputType = dataType;
        // console.debug("UIElementFactory.createWidget: Output Value: ",xfValue, " sourceNode", sourceNode, " appearance: ", dojo.attr(sourceNode,"appearance"));
        var appearance = dojo.attr(sourceNode, "appearance");
        if (appearance != undefined && appearance.indexOf("ca") != -1) {
            if (appearance == "caLink") {
                outputType = "anyURI";
            } else {
                outputType = appearance;
                console.debug("Appearance=" + appearance)
            }

        }
        switch (outputType.toLowerCase()) {
            case "casimiletimeline":
                console.debug("UIElementFactory: create new timeline", sourceNode);
                newWidget = new betterform.ui.input.TimeLine({
                    name:controlId + "-value",
                    checked:xfValue,
                    "class":classValue,
                    xfControlId:controlId
                }, sourceNode);
                newWidget.startup();
                break;
            case "caopmltree":
                console.debug("UIElementFactory: create new tree");
                newWidget = new betterform.ui.tree.OPMLTree({
                    name:controlId + "-value",
                    "class":classValue,
                    xfControlId:controlId
                }, sourceNode);
                newWidget.startup();
                break;

            case "anyuri":
                newWidget = new betterform.ui.output.Link({
                    name:controlId + "-value",
                    href:xfValue,
                    "class":classValue,
                    xfControlId:controlId
                }, sourceNode);
                break;
            default:
                if (mediatype == undefined || mediatype == "text") {
                    newWidget = new betterform.ui.output.Plain({
                        name:controlId + "-value",
                        value:xfValue,
                        "class":classValue,
                        xfControlId:controlId
                    }, sourceNode);

                }
                else if (mediatype == "text/html") {
                    newWidget = new betterform.ui.output.Html({
                        name:controlId + "-value",
                        value:xfValue,
                        "class":classValue,
                        xfControlId:controlId
                    }, sourceNode);

                }
                else if (mediatype.indexOf("image/") > -1) {
                        newWidget = new betterform.ui.output.Image({
                            name:controlId + "-value",
                            src:xfValue,
                            "class":classValue,
                            xfControlId:controlId
                        }, sourceNode);
                    }
                    else {
                        console.warn("UIElementFactory.createWidget(): unknown mediatype '" + mediatype + "' for output")
                    }
                break;
        }
    }

});


