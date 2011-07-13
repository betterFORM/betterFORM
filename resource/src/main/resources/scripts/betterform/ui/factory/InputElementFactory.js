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
        if (appearance != undefined && (appearance.indexOf("ca") != -1 || appearance.indexOf("bf:") != -1)) {
            if ( appearance.indexOf("=") != -1 ) {
                inputType = appearance.substring(0, appearance.indexOf("="));
            } else {
                inputType = appearance;
            }
            console.debug("Custom Input Type: appearance=" + appearance)
        }

        console.debug("inputType.toLowerCase" + inputType.toLowerCase);
        switch (inputType.toLowerCase()) {
            case "casimiletimeline":
                newInputWidget = this.createInputTimelineWidgetALPHA(controlId, sourceNode, classValue);
                break;
            case "caopmltree":
                newInputWidget = this.createInputOPMLTreeALPHA(controlId, sourceNode, classValue);
                break;
            case "bf:time":
                newInputWidget = this.createInputBfTimeWidget(controlId, sourceNode, classValue, appearance);
                break;
            case "bf:dropdowndate":
                newInputWidget = this.createInputBfDropDownDateWidget(controlId, sourceNode, classValue, appearance);
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

    createInputBfTimeWidget:function(controlId, sourceNode, classValue, appearance) {
        var xfValue = sourceNode.innerHTML;
        return newInputTimeWidget = new betterform.ui.input.Time({
            name:controlId + "-value",
            value:xfValue,
            "class":classValue,
            title:dojo.attr(sourceNode,"title"),
            xfControlId:controlId
        }, sourceNode);
    },

    createInputBfDropDownDateWidget:function(controlId, sourceNode, classValue, appearance) {
        //TODO: no rangeStart/rangeEnd -> use predefined template
        var newInputDateWidget = null;
                var xfValue = dojo.attr(sourceNode, "schemaValue");
                if(xfValue == undefined){
                    xfValue = "";
                }

                var rangeStart;
                var rangeEnd;

                var now = new Date().getFullYear();
                if (appearance.indexOf("=") != -1) {
                    rangeStart = appearance.substring(appearance.indexOf("=")+1);


                    if (rangeStart.indexOf(":") != -1) {
                        rangeEnd = rangeStart.substring(rangeStart.indexOf(":")+1);
                        rangeStart = rangeStart.substring(0, rangeStart.indexOf(":"));
                    }

                    if (rangeStart.indexOf("-") != -1) {
                        rangeStart = now - parseInt(rangeStart.substring(rangeStart.indexOf("-")+1), "10");
                    }

                    if (rangeEnd != undefined) {
                            if (rangeEnd.indexOf("+") != -1) {
                                rangeEnd = now + parseInt(rangeEnd.substring(rangeEnd.indexOf("+")+1), "10");
                            } else if (rangeEnd.indexOf("-") != -1) {
                                rangeEnd = now - parseInt(rangeEnd.substring(rangeEnd.indexOf("-")+1), "10");
                            }
                    } else {
                        rangeEnd = now;
                    }

                } else {
                    rangeEnd = now;
                    rangeStart = rangeEnd -10;
                }

                rangeStart = parseInt(rangeStart, "10");
                rangeEnd = parseInt(rangeEnd, "10");

                var templateString = "<div class='xfDropDownDateControl'><input class='xfValue' type='hidden' dojoAttachPoint='valueNode' value=''/><span class='xfDropDownDate'><select size='1' dojoType='dijit.form.ComboBox' dojoAttachPoint='daysFacet' class='xfDropDownDateDays'><option></option><option>01</option><option>02</option><option>03</option><option>04</option><option>05</option><option>06</option><option>07</option><option>08</option><option>09</option><option>10</option><option>11</option><option>12</option><option>13</option><option>14</option><option>15</option><option>16</option><option>17</option><option>18</option><option>19</option><option>20</option><option>21</option><option>22</option><option>23</option><option>24</option><option>25</option><option>26</option><option>27</option><option>28</option><option>29</option><option>30</option><option>31</option></select><select size='1' dojoType='dijit.form.ComboBox' dojoAttachPoint='monthsFacet' class='xfDropDownDateMonths'><option value=''></option><option value='01'>January</option><option value='02'>February</option><option value='03'>March</option><option value='04'>April</option><option value='05'>May</option><option value='06'>June</option><option value='07'>July</option><option value='08'>August</option><option value='09'>September</option><option value='10'>October</option><option value='11'>November</option><option value='12'>December</option></select><select size='1' dojoType='dijit.form.ComboBox' dojoAttachPoint='yearsFacet' class='xfDropDownDateYears'>";
                var end = "</select></span></div>";

                templateString = templateString + "<option></option>";
                for (var i = rangeStart; i <= rangeEnd; i++) {
                    templateString = templateString + "<option>" + i + "</option>";
                }
                templateString = templateString + end;


                    try {
                        newInputDateWidget = new betterform.ui.input.DropDownDate({
                            name:controlId + "-value",
                            value:xfValue,
                            "class":classValue,
                            title:dojo.attr(sourceNode, "title"),
                            rangeStart:rangeStart,
                            rangeEnd:rangeEnd,
                            templateString: templateString,
                            constraints:{
                                selector:'date'
                            },
                            xfControlId:controlId
                        },
                                sourceNode);
                    }
                    catch (ex) {
                        alert(ex)
                    }

                return newInputDateWidget;
    },

    createInputDateWidget:function(controlId, sourceNode, classValue, appearance) {
        var newInputDateWidget = null;
        var xfValue = dojo.attr(sourceNode, "schemaValue");
        if(xfValue != undefined && xfValue != ""){
            xfValue = dojo.date.stamp.fromISOString(xfValue);
        }else { xfValue = undefined; }
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


