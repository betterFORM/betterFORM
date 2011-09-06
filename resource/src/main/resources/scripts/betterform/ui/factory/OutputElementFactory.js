/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.factory.OutputElementFactory");

// creates all betterForm Widgets for xf:* UI Controls dependent on datatype and controltype.
dojo.declare(
        "betterform.ui.factory.OutputElementFactory",
        null,
{


    createOutputWidget:function(sourceNode, controlId, dataType, mediatype, appearance, classValue) {
        // console.debug("betterform.ui.factory.UIElementFactoryImpl.createOutputWidget()  for Control " + controlId + " based on sourceNode:",sourceNode, " datatype:", dataType);
        var xfValue = sourceNode.innerHTML;
        var outputType = dataType;
        // console.debug("UIElementFactory.createOutputWidget: Output Value: ",xfValue, " sourceNode", sourceNode, " appearance: ", dojo.attr(sourceNode,"appearance"));
        if (appearance != undefined && (appearance.indexOf("ca") != -1 || appearance.indexOf("bf") != -1)) {
            if (appearance == "caLink") {
                outputType = "anyURI";
            }
            else if (appearance == "caSourceCode") {
                outputType = "sourcecode"
            }
            else {
                outputType = appearance;
            }
        }
        switch (outputType.toLowerCase()) {
            case "anyuri":
                //todo: clean this mess up - handling of text/html missing
                if (mediatype == undefined || mediatype == "controlValue" || mediatype == "text") {
                    return this.createOutputLinkWidget(controlId, sourceNode, classValue, xfValue);
                } else if (mediatype.indexOf("image/") > -1) {
                    return this.createOutputImageWidget(controlId, sourceNode, classValue, xfValue);

                } else if (mediatype == "text/html") {
                    return this.createOutputHTMLWidget(controlId, sourceNode, classValue, xfValue);
                } else {
                    console.warn("UIElementFactory.createOutputWidget(): output anyURI - unknown combination of attributes");
                    return null;
                }
                break;
            case "sourcecode":
                return new betterform.ui.output.SourceCode({
                    name: controlId + "-value",
                    value: xfValue,
                    "class": classValue,
                    title: dojo.attr(sourceNode, "title"),
                    xfControlId: controlId
                }, sourceNode);
                break;
            case "bfinputlook":
                return new betterform.ui.output.InputLook({
                    name:controlId + "-value",
                    value:xfValue,
                    "class":classValue,
                    title:dojo.attr(sourceNode, "title"),
                    xfControlId:controlId
                }, sourceNode);

                break;
            default:
                if (mediatype == undefined || mediatype == "controlValue" || mediatype == "text") {
                    return this.createOutputPlainWidget(controlId, sourceNode, classValue, xfValue);
                }
                else if (mediatype == "text/html") {
                    return this.createOutputHTMLWidget(controlId, sourceNode, classValue, xfValue);
                }
                else if (mediatype.indexOf("image/") > -1) {
                    return this.createOutputImageWidget(controlId, sourceNode, classValue,xfValue);
                }
                else {
                    console.warn("UIElementFactory.createWidget(): unknown mediatype '" + mediatype + "' for output, rendering default output");
                    return this.createOutputPlainWidget(controlId, sourceNode, classValue,xfValue);
                }
                break;
        }
    },

    createOutputLinkWidget:function(controlId, sourceNode, classValue,xfValue) {
        return new betterform.ui.output.Link({
            name:controlId + "-value",
            href:xfValue,
            "class":classValue,
            title:dojo.attr(sourceNode,"title"),
            xfControlId:controlId
        }, sourceNode);
    },

    createOutputImageWidget:function(controlId, sourceNode, classValue,xfValue) {
        return new betterform.ui.output.Image({
                name:controlId + "-value",
                src:xfValue,
                "class":classValue,
                title:dojo.attr(sourceNode,"title"),
                xfControlId:controlId
            }, sourceNode);

    },

    createOutputHTMLWidget:function(controlId, sourceNode, classValue, xfValue) {
        return betterform.ui.output.Html({
            name:controlId + "-value",
            value:xfValue,
            "class":classValue,
            title:dojo.attr(sourceNode,"title"),
            xfControlId:controlId
        }, sourceNode);

    },

    createOutputPlainWidget:function(controlId, sourceNode, classValue, xfValue) {
        // console.debug("UIElementFactory.createOutputPlainWidget: controlId:", controlId, " sourceNode: ", sourceNode, " classValue: ", classValue)
        return new betterform.ui.output.Plain({
            name:controlId + "-value",
            value:xfValue,
            "class":classValue,
            title:dojo.attr(sourceNode,"title"),
            xfControlId:controlId
        }, sourceNode);

    }
});


