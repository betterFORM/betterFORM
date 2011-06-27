/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.factory.SelectElementFactory");

// creates all betterForm Widgets for xf:* UI Controls dependent on datatype and controltype.
dojo.declare(
        "betterform.ui.factory.SelectElementFactory",
        null,
{


    createSelectMultipleWidget:function(controlType, sourceNode, controlId, classValue){
        if(controlType == "selectCheckBox") {
            return new betterform.ui.select.CheckBoxGroup({
                                name:controlId + "-value",
                                "class":classValue,
                                title:dojo.attr(sourceNode,"title"),
                                xfControlId:controlId
                            }, sourceNode);
        }else {
            return new betterform.ui.select.MultiSelect({
                name:controlId + "-value",
                size:dojo.attr(sourceNode, "size"),
                multiple:true,
                "class":classValue,
                title:dojo.attr(sourceNode,"title"),
                xfControlId:controlId
            }, sourceNode);
        }
    },

    createSelectSingleWidget:function(controlType, sourceNode, controlId, classValue) {
        switch (controlType) {
            case "select1":
            case "select1ComboBox":
                return new betterform.ui.select1.ComboBox({
                    name:controlId + "-value",
                    value:"",
                    "class":classValue,
                    title:dojo.attr(sourceNode, "title"),
                    xfControlId:controlId
                }, sourceNode);
                break;

            case "select1ComboBoxOpen":
                return new betterform.ui.select1.ComboBoxOpen({
                    name:controlId + "-value",
                    size:dojo.attr(sourceNode, "size"),
                    multiple:true,
                    "class":classValue,
                    title:dojo.attr(sourceNode, "title"),
                    xfControlId:controlId
                }, sourceNode);
                break;

            case "select1List":
                return new betterform.ui.select1.Plain({
                    name:controlId + "-value",
                    size:dojo.attr(sourceNode, "size"),
                    "class":classValue,
                    title:dojo.attr(sourceNode, "title"),
                    xfControlId:controlId
                }, sourceNode);
                break;

            case "select1RadioButton":
                return new betterform.ui.select1.RadioGroup({
                    name:controlId + "-value",
                    "class":classValue,
                    title:dojo.attr(sourceNode, "title"),
                    xfControlId:controlId
                }, sourceNode);
                break;

            case "radio":
                var radioName = sourceNode.name;
                var radioValue = sourceNode.value;
                // console.debug("UIElementFactory.create Radio Item for Control " + controlId + " [name:"+radioName+"] ! Properties: ", sourceNode, " value: " + radioValue);

                var newSelectWidget = new betterform.ui.select1.RadioButton({
                    "class":classValue,
                    name:radioName,
                    value:radioValue,
                    title:dojo.attr(sourceNode,"title"),
                    xfControlId:controlId
                }, sourceNode);
                newSelectWidget.startup();
                return newSelectWidget;
                break

            default:
                console.warn("Unknown Select1 Control sourceNode: ", sourceNode);
        }
    }

});


