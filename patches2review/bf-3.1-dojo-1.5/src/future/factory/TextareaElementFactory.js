dojo.provide("betterform.ui.TextareaElementFactory");

dojo.require("betterform.ui.AbstractElementFactory");
dojo.declare(
        "betterform.ui.TextareaElementFactory",
        betterform.ui.AbstractElementFactory,
{

    createDefaultWidget:function() {
        var newWidget = null;

        if(mediatype == undefined || mediatype == 'text/html') {
            newWidget = new betterform.ui.textarea.HtmlEditor({
                name:controlId + "-value",
                "class":classValue,
                xfControlId:controlId
            }, sourceNode);

        } else if(mediatype == undefined || mediatype == 'dojo') {
            newWidget = new betterform.ui.textarea.DojoEditor({
                name:controlId + "-value",
                "class":classValue,
                rows:5,
                cols:30,
                xfControlId:controlId
            }, sourceNode);
        }else {
            newWidget = new betterform.ui.textarea.SimpleTextarea({
                name:controlId + "-value",
                "class":classValue,
                xfControlId:controlId
            }, sourceNode);

        }
        return newWidget;
    }

});


