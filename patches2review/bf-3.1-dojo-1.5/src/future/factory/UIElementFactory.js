dojo.provide("betterform.ui.UIElementFactory");

dojo.require("betterform.ui.factory.InputElementFactory");
dojo.require("betterform.ui.OutputElementFactory");
dojo.require("betterform.ui.RangeElementFactory");
dojo.require("betterform.ui.SecretElementFactory");
dojo.require("betterform.ui.Select1ElementFactory");
dojo.require("betterform.ui.SelectElementFactory");
dojo.require("betterform.ui.SubmitElementFactory");
dojo.require("betterform.ui.TextareaElementFactory");
dojo.require("betterform.ui.TreeElementFactory");
dojo.require("betterform.ui.UploadElementFactory");

dojo.require("betterform.ui.input.Date");
// creates all betterForm Widgets for xf:* UI Controls dependent on datatype and controltype.
dojo.declare(
        "betterform.ui.UIElementFactory",
        null,
{

    createWidget:function(sourceNode, controlId) {
        var controlType = dojo.attr(sourceNode, "controlType");
        switch (controlType) {
            case "input":
                return new betterform.ui.InputElementFactory().createWidget(sourceNode,controlId);
            case "output":
                return new betterform.ui.OutputElementFactory().createWidget(sourceNode,controlId);
            case "range":
                return new betterform.ui.RangeElementFactory().createWidget(sourceNode,controlId);
            case "secret":
                return new betterform.ui.SecretElementFactory().createWidget(sourceNode,controlId);
            case "select":
                return new betterform.ui.SelectElementFactory().createWidget(sourceNode,controlId);
            case "select1":
                return new betterform.ui.Select1ElementFactory().createWidget(sourceNode,controlId);
            case "textarea":
                return new betterform.ui.TextareaElementFactory().createWidget(sourceNode,controlId);
            case "trigger":
                return new betterform.ui.TreeElementFactory().createWidget(sourceNode,controlId);
            case "trigger":
                return new betterform.ui.TriggerElementFactory().createWidget(sourceNode,controlId);
            case "submit":
                return new betterform.ui.SubmitElementFactory().createWidget(sourceNode,controlId);
            case "upload":
                return new betterform.ui.UploadElementFactory().createWidget(sourceNode,controlId);
            default:
                // todo: allow elements that are not already present in the XForms space (if not senseful mapping can be made)
                console.error("Unknown controlType: '" + controlType );
                break;
        }
    }

});


