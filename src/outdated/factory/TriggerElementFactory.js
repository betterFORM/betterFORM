dojo.provide("betterform.ui.TriggerElementFactory");

dojo.require("betterform.ui.AbstractElementFactory");
dojo.declare(
        "betterform.ui.TriggerElementFactory",
        betterform.ui.AbstractElementFactory,
{

    createDefaultWidget:function() {
        var newWidget = null;

        if(dojo.attr(sourceNode,"appearance")=="minimal") {
            console.debug("Minimal Trigger");
            newWidget = new betterform.ui.trigger.LinkButton({
                id:dojo.attr(sourceNode, "id"),
                name:dojo.attr(sourceNode, "name")+"-value",
                label:dojo.attr(sourceNode, "label"),
                "class":classValue,
                xfControlId:controlId
            }, sourceNode);

        }else {
            newWidget = new betterform.ui.trigger.Button({
                id:dojo.attr(sourceNode, "id"),
                name:dojo.attr(sourceNode, "name")+"-value",
                label:dojo.attr(sourceNode, "label"),
                "class":classValue,
                xfControlId:controlId
            }, sourceNode);

        }
        return newWidget;
    }

});


