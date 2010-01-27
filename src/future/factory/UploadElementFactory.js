dojo.provide("betterform.ui.InputElementFactory");

dojo.require("betterform.ui.AbstractElementFactory");
dojo.declare(
        "betterform.ui.InputElementFactory",
        betterform.ui.AbstractElementFactory,
{

    createAnyURIWidget:function(){
        return new betterform.ui.upload.UploadPlain({
            "class":classValue,
            xfControlId:controlId
        }, sourceNode);
    },

    createBase64BinaryWidget:function(){
        return null;
    },

    createHexBinary:function(){
        return null;
    }
});


