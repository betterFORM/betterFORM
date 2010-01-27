dojo.provide("betterform.ui.Select1ElementFactory");

dojo.require("betterform.ui.AbstractElementFactory");
dojo.declare(
        "betterform.ui.Select1ElementFactory",
        betterform.ui.AbstractElementFactory,
{

    createDefaultWidget:function() {
        var newWidget = null;

        var appearance = dojo.attr(sourceNode, "appearance");
        if (appearance == undefined) {
            appearance = "minimal";//default
        }

        //@selection takes precedence
        var selection = dojo.attr(sourceNode,"selection");
        if (selection == undefined) {
            newWidget = new betterform.ui.select1.ComboBoxOpen({
                name:controlId + "-value",
                size:dojo.attr(sourceNode, "size"),
                multiple:true,
                "class":classValue,
                xfControlId:controlId
            }, sourceNode);
            return newWidget;
        }

        switch (appearance.toLowerCase()) {
            case "minimal":
                newWidget = new betterform.ui.select1.ComboBox({
                    name:controlId + "-value",
                    value:"",
                    "class":classValue,
                    xfControlId:controlId
                }, sourceNode);
                break;
            case "compact":
                newWidget = new betterform.ui.select1.Plain({
                    name:controlId + "-value",
                    size:dojo.attr(sourceNode, "size"),
                    "class":classValue,
                    xfControlId:controlId
                }, sourceNode);
                break;
                break;
            case "full":
                newWidget = new betterform.ui.select1.RadioGroup({
                    name:controlId + "-value",
                    "class":classValue,
                    xfControlId:controlId
                }, sourceNode);
                break;
            default:
                break;
        }

        return newWidget;

    }


});


