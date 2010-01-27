dojo.provide("betterform.ui.SubmitElementFactory");

dojo.require("betterform.ui.AbstractElementFactory");
dojo.declare(
        "betterform.ui.SubmitElementFactory",
        betterform.ui.AbstractElementFactory,
{

    createDefaultWidget:function() {
        newWidget = new betterform.ui.Submit.TextField({
            name:controlId + "-value",
            value:xfValue,
            "class":classValue,
            xfControlId:controlId
        }, sourceNode);
    },

    createDateWidget:function() {
        var xfValue = dojo.date.stamp.fromISOString(dojo.attr(sourceNode, "schemaValue"));
        var newWidget = new betterform.ui.Submit.Date({
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
        var newWidget = new betterform.ui.Submit.Boolean({
            name:controlId + "-value",
            checked:xfValue,
            "class":classValue,
            xfControlId:controlId
        }, sourceNode);
        return newWidget;
    }

});


