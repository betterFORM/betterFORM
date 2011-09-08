/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.factory.UIElementFactoryImpl");

dojo.require("betterform.ui.factory.InputElementFactory");
dojo.require("betterform.ui.factory.OutputElementFactory");
dojo.require("betterform.ui.factory.RangeElementFactory");
dojo.require("betterform.ui.factory.SelectElementFactory");

// creates all betterForm Widgets for xf:* UI Controls dependent on datatype and controltype.
dojo.declare(
        "betterform.ui.factory.UIElementFactoryImpl",
        null,
{
    outputFactory:null,

    constructor:function() {
        this.outputFactory = new betterform.ui.factory.OutputElementFactory();
        this.inputFactory = new betterform.ui.factory.InputElementFactory();
        this.rangeFactory = new betterform.ui.factory.RangeElementFactory();
        this.selectFactory = new betterform.ui.factory.SelectElementFactory();
    },

    createWidget:function(sourceNode, controlId) {
        // console.debug("Create Widget: ",controlId , " sourceNode: ",sourceNode)
        var controlType = dojo.attr(sourceNode, "controlType");

        var dataType = dojo.attr(sourceNode, "dataType");

        dataType = betterform.ui.util.removeNamespace(dataType);
        var classValue = dojo.attr(sourceNode, "class");
        if(classValue == undefined) {
            classValue = "xfValue";
        }else if(classValue.indexOf("xfValue")==-1) {
            classValue = "xfValue "  + classValue;
        }
        var newWidget = undefined;

        if (controlType == undefined) {
            console.warn("UIElementFactory.createWidget: undefined controlType, Node: ", sourceNode);
            return sourceNode;
        }
        var mediatype = dojo.attr(sourceNode, "mediatype");
        var appearance = dojo.attr(sourceNode,"appearance");
        // console.debug("betterform.ui.factory.UIElementFactoryImpl.createWidget() for Control " + controlId + " based on sourceNode:",sourceNode , " and control type " ,controlType);
        switch (controlType) {
            case "input":
                newWidget = this.inputFactory.createInputWidget(sourceNode, controlId, dataType, mediatype,appearance,classValue);
                break;
            case "output":
                newWidget = this.outputFactory.createOutputWidget(sourceNode, controlId, dataType, mediatype,appearance,classValue);
                break;
            case "range":
                newWidget = this.rangeFactory.createRangeWidget(sourceNode, controlId, classValue, appearance);
                break;

            case "secret":
                newWidget = this.createSecretWidget(sourceNode, controlId, classValue);
                break;

            /* Select Cases */
            case "selectCheckBoxGroup":
            case "selectList":
            case "checkBoxEntry":
                return this.selectFactory.createSelectMultipleWidget(controlType, sourceNode, controlId, classValue);
                break;

            /* Select1 Cases */
            case "select1":
            case "select1ComboBox":
            case "select1ComboBoxOpen":
            case "select1List":
            case "select1RadioButton":
            case "radio":
                return this.selectFactory.createSelectSingleWidget(controlType, sourceNode, controlId, classValue);
                break;

            case "textarea":
                return this.createTextareaWidget(sourceNode, controlId, mediatype, appearance, classValue);
                break;
            case "minimalTrigger":
            case "trigger":
                return this.createTriggerWidget(sourceNode, controlId, appearance, classValue);
                break;
            case "submit":
                  // FIXME see warning
                  // TODO see warning
                console.warn("TBD create Submit control: ", sourceNode);
                break;
            case "upload":
                return this.createUploadWidget(sourceNode, controlId, dataType, classValue);
                break;
            case "dialog":
                //console.debug("UIElementFactory.create Dialog Control " + controlId + " [name:"+dialogName+"] ! Properties: ", sourceNode, " value: " + dialogValue);
                newWidget = new betterform.ui.container.Dialog({
                    "class":classValue,
                    name:dojo.attr(sourceNode,"name"),
                    value:dojo.attr(sourceNode,"value"),
                    title:dojo.attr(sourceNode,"title"),
                    xfControlId:controlId
                }, sourceNode);
                break;
            default:
                console.warn("UIElementFactory: Unknown controlValue: '" +controlType + "' for Control " + controlId + "! Properties: ", sourceNode);
                break;
        }
        return newWidget;
    },

    // ###################### END BETTERFORM UI FACTORY
    // ###################### END BETTERFORM UI FACTORY
    // ###################### END BETTERFORM UI FACTORY


    /**
     * createUploadWidget
     *
     * @param sourceNode
     * @param controlId
     * @param datatype
     * @param classValue
     */

    createUploadWidget:function(sourceNode, controlId, datatype, classValue) {
        if(datatype == "base64" || datatype == "base64Binary" || datatype == "hexBinary" || datatype == "anyURI") {
            return new betterform.ui.upload.UploadPlain({
                    "class":classValue,
                    title:dojo.attr(sourceNode,"title"),
                    xfControlId:controlId
                }, sourceNode);

        } else {
            console.error("Upload (" + controlId+"): unsupported datatype: ", datatype);
            return null;
        }

    },


    createTriggerWidget:function(sourceNode, controlId, appearance, classValue) {
        if(appearance=="minimal") {
            return new betterform.ui.trigger.LinkButton({
                id:dojo.attr(sourceNode, "id"),
                name:dojo.attr(sourceNode, "name")+"-value",
                label:dojo.attr(sourceNode, "label"),
                "class":classValue,
                title:dojo.attr(sourceNode,"title"),
                xfControlId:controlId
            }, sourceNode);
        } else if (appearance=="imageTrigger") {
            return new betterform.ui.trigger.ImageButton({
                id:dojo.attr(sourceNode, "id"),
                name:dojo.attr(sourceNode, "name")+"-value",
                label:dojo.attr(sourceNode, "label"),
                "class":classValue,
                title:dojo.attr(sourceNode,"title"),
                xfControlId:controlId
            }, sourceNode);
            // console.dirxml(sourceNode);
        }else {
            // console.debug("UIElementFactory: creating betterform.ui.trigger.Button sourceNode:",sourceNode );
            return new betterform.ui.trigger.Button({
                id:dojo.attr(sourceNode, "id"),
                name:dojo.attr(sourceNode, "name")+"-value",
                label:dojo.attr(sourceNode, "label"),
                "class":classValue,
                title:dojo.attr(sourceNode,"title"),
                xfControlId:controlId
            }, sourceNode);

        }
    },


    createTextareaWidget:function(sourceNode, controlId, mediatype, appearance, classValue) {
        // todo: fixme: textarea mediatype="dojo" makes no sense - should be an appearance instead
        console.debug("sourceNode: ",sourceNode," controlId:",controlId," mediatype:",mediatype," appearance:",appearance," classValue:",classValue);
        if(mediatype && mediatype.toLowerCase() == "text/html") {
            if(!dojo.hasClass(sourceNode,"xfTextareaHTMLValue")){
                dojo.addClass(sourceNode,"")
            }
            return new betterform.ui.textarea.HtmlEditor({
                name:controlId + "-value",
                "class":classValue,
                title:dojo.attr(sourceNode,"title"),
                xfControlId:controlId,
                height:'100%',
                extraPlugins:[]
                }, sourceNode);
        }
        else if(appearance && appearance.toLowerCase() == "growing"){
            if(!dojo.hasClass(sourceNode,"xfTextareaGrowingValue")){
                dojo.addClass(sourceNode,"")
            }

            return new betterform.ui.textarea.DojoEditor({
                name:controlId + "-value",
                "class":classValue,
                rows:5,
                cols:30,
                title:dojo.attr(sourceNode,"title"),
                xfControlId:controlId
            }, sourceNode);

        }
        else {
            if(!dojo.hasClass(sourceNode,"xfTextareaValue")){
                dojo.addClass(sourceNode,"")
            }
            return new betterform.ui.textarea.SimpleTextarea({
                name:controlId + "-value",
                "class":classValue,
                title:dojo.attr(sourceNode,"title"),
                xfControlId:controlId
            }, sourceNode);

        }
    },


    createSecretWidget:function(sourceNode, controlId, classValue) {
        // console.debug("UIElementFactory Range: srcNode: ", sourceNode, " start: ", start, " end: ", end, " step: ",step);
        var xfValue = sourceNode.innerHTML;
        return new betterform.ui.secret.Secret({
            name:controlId + "-value",
            value:xfValue,
            "class":classValue,
            title:dojo.attr(sourceNode,"title"),
            xfControlId:controlId
        }, sourceNode);
    }
});


