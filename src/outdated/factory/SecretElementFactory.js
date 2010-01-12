dojo.provide("betterform.ui.SecretElementFactory");

dojo.require("betterform.ui.AbstractElementFactory");
dojo.declare(
        "betterform.ui.SecretElementFactory",
        betterform.ui.AbstractElementFactory,
{

    createDefaultWidget:function() {
        var xfValue = sourceNode.innerHTML;
        var newWidget = new betterform.ui.secret.Secret({
            name:controlId + "-value",
            value:xfValue,
            "class":classValue,
            xfControlId:controlId
        }, sourceNode);
        return newWidget
    }


});


