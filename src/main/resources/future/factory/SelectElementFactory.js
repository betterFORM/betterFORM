dojo.provide("betterform.ui.SelectElementFactory");

dojo.require("betterform.ui.AbstractElementFactory");
dojo.declare(
        "betterform.ui.SelectElementFactory",
        betterform.ui.AbstractElementFactory,
{

    createDefaultWidget:function() {
        var newWidget = null;

        var appearance = dojo.attr(sourceNode, "appearance");
        if (appearance == undefined) {
            appearance = "minimal";//default
        }

        switch (appearance.toLowerCase()) {
            case "minimal":
                newWidget = new betterform.ui.select.MultiSelect({
                    name:controlId + "-value",
                    size:dojo.attr(sourceNode, "size"),
                    multiple:true,
                    "class":classValue,
                    xfControlId:controlId
                }, sourceNode);
                break;
            case "compact":
                newWidget = new betterform.ui.select.MultiSelect({
                    name:controlId + "-value",
                    size:dojo.attr(sourceNode, "size"),
                    multiple:true,
                    "class":classValue,
                    xfControlId:controlId
                }, sourceNode);
                break;
            case "full":
                newWidget = new betterform.ui.select.CheckBoxGroup({
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


