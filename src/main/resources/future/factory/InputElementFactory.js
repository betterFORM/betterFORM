dojo.provide("betterform.ui.InputElementFactory");

dojo.require("betterform.ui.AbstractElementFactory");
dojo.declare(
        "betterform.ui.InputElementFactory",
        betterform.ui.AbstractElementFactory,
{

    createDefaultWidget:function() {
        newWidget = new betterform.ui.input.TextField({
            name:controlId + "-value",
            value:xfValue,
            "class":classValue,
            xfControlId:controlId
        }, sourceNode);
    },

    createDateWidget:function() {
        var xfValue = dojo.date.stamp.fromISOString(dojo.attr(sourceNode, "schemaValue"));
        var newWidget = new betterform.ui.input.Date({
            name:controlId + "-value",
            value:xfValue,
            "class":classValue,
            xfControlId:controlId
        }, sourceNode);
        return newWidget;
    },

    createBooleanWidget:function() {
        var xfValue = sourceNode.innerHTML;
        // console.debug("UIElementFactory.createWidget: Boolean Value: ", xfValue);
        if (xfValue == "false") {
            xfValue = undefined;
        }
        var newWidget = new betterform.ui.input.Boolean({
            name:controlId + "-value",
            checked:xfValue,
            "class":classValue,
            xfControlId:controlId
        }, sourceNode);
        return newWidget;
    }

});


