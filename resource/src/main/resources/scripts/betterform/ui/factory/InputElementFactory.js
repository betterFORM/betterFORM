/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.factory.InputElementFactory");

// creates all betterForm Widgets for xf:* UI Controls dependent on datatype and controltype.
dojo.declare(
        "betterform.ui.factory.InputElementFactory",
        null,
{
    createInputWidget:function(sourceNode, controlId, dataType, mediatype, appearance, classValue){
        var newInputWidget = null;
        var inputType = dataType;

        //TODO: ca deprecated?
        if (appearance != undefined && appearance.indexOf("ca") != -1) {
            inputType = appearance;
            // console.debug("Custom Input Type: appearance=" + appearance)
        }

        switch (inputType.toLowerCase()) {
            case "casimiletimeline":
                newInputWidget = this.createInputTimelineWidgetALPHA(controlId, sourceNode, classValue);
                break;
            case "caopmltree":
                newInputWidget = this.createInputOPMLTreeALPHA(controlId, sourceNode, classValue);
                break;
            case "date":
                newInputWidget = this.createInputDateWidget(controlId, sourceNode, classValue, appearance);
                break;
            case "datetime":
                newInputWidget = this.createInputDateTimeWidget(controlId, sourceNode, classValue, appearance);
                break;
            case "boolean":
                newInputWidget = this.createInputBooleanWidget(controlId, sourceNode, classValue, appearance);
                break;
            default:
                newInputWidget = this.createInputTextWidget(controlId, sourceNode, classValue, appearance);
                break;

        }
        return newInputWidget;
    },

    createInputTimelineWidgetALPHA:function(controlId, sourceNode, classValue, xfValue) {
        // console.debug("UIElementFactory: create new timeline", sourceNode);
        var xfValue = sourceNode.innerHTML;
        var newInputTimelineWidget = new betterform.ui.timeline.TimeLine({
            name:controlId + "-value",
            checked:xfValue,
            "class":classValue,
            xfControlId:controlId
        }, sourceNode);
        newInputTimelineWidget.startup();
        return newInputTimelineWidget;
    },

    createInputOPMLTreeALPHA:function(controlId, sourceNode, classValue) {
        // console.debug("UIElementFactory: create new tree");
        var newInputTreeWidget = new betterform.ui.tree.OPMLTree({
            name:controlId + "-value",
            "class":classValue,
            xfControlId:controlId
        }, sourceNode);
        newInputTreeWidget.startup();
        return newInputTreeWidget;
    },


    createInputDateWidget:function(controlId, sourceNode, classValue, appearance) {
        var newInputDateWidget = null;
        var xfValue = dojo.attr(sourceNode, "schemaValue");
        if(xfValue != undefined && xfValue != ""){
            xfValue = dojo.date.stamp.fromISOString(xfValue);
        }else { xfValue = ""; }
        var datePattern;

        if (appearance.indexOf("iso8601:") != -1) {
            datePattern = appearance.substring(appearance.indexOf("iso8601:")+8);
            //console.debug("UIelementFactory.createWidget 1. datePattern:" + datePattern);
            if(datePattern.indexOf(" ") != -1) {
                datePattern = datePattern.substring(0,datePattern.indexOf(" ")).trim();
                //console.debug("UIelementFactory.createWidget 2. datePattern:" + datePattern);
            }
        }

        if (datePattern != undefined) {
            try {
                newInputDateWidget = new betterform.ui.input.Date({
                    name:controlId + "-value",
                    value:xfValue,
                    required:false,
                    "class":classValue,
                    title:dojo.attr(sourceNode, "title"),
                    constraints:{
                        selector:'date',
                        datePattern:datePattern
                    },
                    xfControlId:controlId
                },
                        sourceNode);
            }
            catch (ex) {
                alert(ex)
            }
        } else {
            newInputDateWidget = new betterform.ui.input.Date({
                name:controlId + "-value",
                value:xfValue,
                "class":classValue,
                title:dojo.attr(sourceNode,"title"),
                constraints:{
                    selector:'date'
                },
                xfControlId:controlId
            }, sourceNode);
        }
        return newInputDateWidget;

    },

    createInputDateTimeWidget:function(controlId, sourceNode, classValue, appearance) {
                var newInputDateTimeWidget = null;
                var xfValue = dojo.attr(sourceNode, "schemaValue");
                // examine if dateTime value contains miliseconds and set property to true if so
                var miliseconds = false;
                if(xfValue != undefined && xfValue.indexOf(".")==19){
                    xfValue = xfValue.substring(0,19);
                    miliseconds = true;
                }
                if(xfValue == undefined){
                    xfValue = "";
                }
                var datePattern;
                if (appearance.indexOf("iso8601:") != -1) {
                    datePattern = appearance.substring(appearance.indexOf("iso8601:")+8);
                    //console.debug("UIelementFactory.createWidget 1. datePattern:" + datePattern);
                    if(datePattern.indexOf(" ") != -1) {
                        datePattern = datePattern.substring(0,datePattern.indexOf(" ")).trim();
                        //console.debug("UIelementFactory.createWidget 2. datePattern:" + datePattern);
                    }
                }

                if (datePattern != undefined) {
                    try {
                    newInputDateTimeWidget = new betterform.ui.input.DateTime({
                        name:controlId + "-value",
                        value:xfValue,
                        miliseconds:miliseconds,
                        constraints:{
                            datePattern:datePattern,
                            timePattern:'HH:mm:ss'

                        },
                        title:dojo.attr(sourceNode, "title"),
                        xfControlId:controlId
                    }, sourceNode);
                    } catch (ex) {
                        alert(ex)
                    }
                } else {

                    newInputDateTimeWidget = new betterform.ui.input.DateTime({
                        name:controlId + "-value",
                        value:xfValue,
                        miliseconds:miliseconds,
                        constraints:{
                            datePattern:'dd.MM.yyyy',
                            timePattern:'HH:mm:ss'

                        },
                        title:dojo.attr(sourceNode, "title"),
                        xfControlId:controlId
                    }, sourceNode);
                }
                /*
                    newInputDateTimeWidget = new betterform.ui.input.DateTime({
                        name:controlId + "-value",
                        value:xfValue,
                        miliseconds:miliseconds,
                        constraints:{
                            datePattern:'dd.MM.yyyy',
                            timePattern:'HH:mm:ss',
                            locale:'de-de'
                        },
                        title:dojo.attr(sourceNode,"title"),
                        xfControlId:controlId
                    }, sourceNode);
                */
                return newInputDateTimeWidget;
        },

    createInputBooleanWidget:function(controlId, sourceNode, classValue, appearance) {
        var xfValue = sourceNode.innerHTML;
        // console.debug("UIElementFactory.createWidget: Boolean Value: ", xfValue);
        if (xfValue == "false") {
            xfValue = undefined;
        }
        return new betterform.ui.input.Boolean({
            name:controlId + "-value",
            checked:xfValue,
            "class":classValue,
            title:dojo.attr(sourceNode,"title"),
            xfControlId:controlId
        }, sourceNode);
    },

    createInputTextWidget:function(controlId, sourceNode, classValue, appearance) {
        //var incrementaldelay = dojo.attr(sourceNode,"delay");
        var xfValue = sourceNode.innerHTML;
        // console.debug("UIElementFactory.createWidget: String Value: ", xfValue);
        return new betterform.ui.input.TextField({
            name:controlId + "-value",
            value:xfValue,
            "class":classValue,
            title:dojo.attr(sourceNode,"title"),
            xfControlId:controlId
        }, sourceNode);
    }
});


