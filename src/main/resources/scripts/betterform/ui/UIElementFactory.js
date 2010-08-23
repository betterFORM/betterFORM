/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.UIElementFactory");

// creates all betterForm Widgets for xf:* UI Controls dependent on datatype and controltype.
dojo.declare(
        "betterform.ui.UIElementFactory",
        null,
{

    createWidget:function(sourceNode, controlId) {
        // console.debug("Create Widget: ",controlId , " sourceNode: ",sourceNode)
        var controlType = dojo.attr(sourceNode, "controlType");

        var dataType = dojo.attr(sourceNode, "dataType");

        dataType = betterform.ui.util.removeNamespace(dataType);
        var classValue = "xfValue";
        if(dojo.attr(sourceNode, "class")) {
            var controlClasses = dojo.attr(sourceNode, "class");
            if(controlClasses.indexOf("xfValue")==-1){
                classValue = classValue + " " + controlClasses;
            }else {
                classValue = controlClasses;
            }
        }
        var newWidget = null;
        if (controlType == undefined) {
            console.warn("UIElementFactory.createWidget: undefined controlType, Node: ", sourceNode);            
            return sourceNode;
        }
        var mediatype = dojo.attr(sourceNode, "mediatype");
        // console.debug("betterform.ui.UIElementFactory.createWidget() for Control " + controlId + " based on sourceNode:",sourceNode , " and control type " ,controlType);
        switch (controlType) {
            case "input":
                var inputType = dataType;
                var appearance = dojo.attr(sourceNode,"appearance");
                //TODO: ca deprecated?
                if (appearance != undefined && appearance.indexOf("ca") != -1) {
                    inputType = appearance;
                    // console.debug("Custom Input Type: appearance=" + appearance)
                }

                switch (inputType.toLowerCase()) {
                    case "casimiletimeline":
                        // console.debug("UIElementFactory: create new timeline", sourceNode);
                        dojo.require("betterform.ui.timeline.TimeLine");
                        newWidget = new betterform.ui.timeline.TimeLine({
                            name:controlId + "-value",
                            checked:xfValue,
                            "class":classValue,
                            xfControlId:controlId
                        }, sourceNode);
                        newWidget.startup();
                        break;
                    case "caopmltree":
                        // console.debug("UIElementFactory: create new tree");
                            dojo.require("betterform.ui.tree.OPMLTree");
                        newWidget = new betterform.ui.tree.OPMLTree({
                            name:controlId + "-value",
                            "class":classValue,
                            xfControlId:controlId
                        }, sourceNode);
                        newWidget.startup();
                        break;

                    case "date":
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
                        dojo.require("betterform.ui.input.Date");

                        if (datePattern != undefined) {
                            try {
                        newWidget = new betterform.ui.input.Date({
                            name:controlId + "-value",
                            value:xfValue,
                            "class":classValue,
                            title:dojo.attr(sourceNode,"title"),
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
                            newWidget = new betterform.ui.input.Date({
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
                        break;
                    case "datetime":
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

                        dojo.require("betterform.ui.input.DateTime");
                        if (datePattern != undefined) {
                            try {
                        newWidget = new betterform.ui.input.DateTime({
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

                            newWidget = new betterform.ui.input.DateTime({
                                name:controlId + "-value",
                                value:xfValue,
                                miliseconds:miliseconds,
                                constraints:{
                                datePattern:'dd.MM.yyyy',
                                timePattern:'HH:mm:ss'

                            },
                            title:dojo.attr(sourceNode,"title"),
                            xfControlId:controlId
                        }, sourceNode);
                        }
/*
                        newWidget = new betterform.ui.input.DateTime({
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
                        break;
                    case "boolean":
                        var xfValue = sourceNode.innerHTML;
                        // console.debug("UIElementFactory.createWidget: Boolean Value: ", xfValue);
                        if (xfValue == "false") {
                            xfValue = undefined;
                        }
                        dojo.require("betterform.ui.input.Boolean");
                        newWidget = new betterform.ui.input.Boolean({
                            name:controlId + "-value",
                            checked:xfValue,
                            "class":classValue,
                            title:dojo.attr(sourceNode,"title"),
                            xfControlId:controlId
                        }, sourceNode);
                        break;
                    default:
                        var xfValue = sourceNode.innerHTML;
                    // console.debug("UIElementFactory.createWidget: String Value: ", xfValue);
                        dojo.require("betterform.ui.input.TextField");
                        newWidget = new betterform.ui.input.TextField({
                            name:controlId + "-value",
                            value:xfValue,
                            "class":classValue,
                            title:dojo.attr(sourceNode,"title"),
                            xfControlId:controlId
                        }, sourceNode);
                        break;

                }
                break;
            case "output":
                // console.debug("betterform.ui.UIElementFactory.createOutputWidget()  for Control " + controlId + " based on sourceNode:",sourceNode, " datatype:", dataType);
                var xfValue = sourceNode.innerHTML;
                var outputType = dataType;
                // console.debug("UIElementFactory.createWidget: Output Value: ",xfValue, " sourceNode", sourceNode, " appearance: ", dojo.attr(sourceNode,"appearance"));
                var appearance = dojo.attr(sourceNode,"appearance");
                if(appearance != undefined && appearance.indexOf("ca") != -1){
                    if(appearance == "caLink"){
                        outputType = "anyURI";
                    }
                    else if (appearance == "caSourceCode") {
                        outputType = "sourcecode"
                    }
                    else
                    {
                        outputType = appearance;
                        // console.debug("Appearance="+appearance)
                    }

                }
                switch (outputType.toLowerCase()) {
                    case "anyuri":
                        //todo: clean this mess up - handling of text/html missing
                        if (mediatype == undefined || mediatype == "controlValue" || mediatype == "text") {
                            dojo.require("betterform.ui.output.Link");
                            newWidget = new betterform.ui.output.Link({
                                name:controlId + "-value",
                                href:xfValue,
                                "class":classValue,
                                title:dojo.attr(sourceNode,"title"),
                                xfControlId:controlId
                            }, sourceNode);
                        }else if (mediatype.indexOf("image/") > -1) {
                                dojo.require("betterform.ui.output.Image");
                                newWidget = new betterform.ui.output.Image({
                                    name:controlId + "-value",
                                    src:xfValue,
                                    "class":classValue,
                                    title:dojo.attr(sourceNode,"title"),
                                    xfControlId:controlId
                                }, sourceNode);
                        }else if (mediatype == "text/html") {
                            dojo.require("betterform.ui.output.Html");
                            newWidget = new betterform.ui.output.Html({
                                name:controlId + "-value",
                                value:xfValue,
                                "class":classValue,
                                title:dojo.attr(sourceNode,"title"),
                                xfControlId:controlId
                            }, sourceNode);

                        }else{
                            console.warn("UIElementFactory.createWidget(): output anyURI - unknown combination of attributes");
                        }
                        break;
                    case "sourcecode":
                        dojo.require("betterform.ui.output.SourceCode");
                        newWidget = new betterform.ui.output.SourceCode({
                            name: controlId + "-value",
                            value: xfValue,
                            "class": classValue,
                            title: dojo.attr(sourceNode, "title"),
                            xfControlId: controlId
                            }, sourceNode);
                        break;
                    default:
                        if (mediatype == undefined || mediatype == "controlValue" || mediatype == "text") {
                            dojo.require("betterform.ui.output.Plain");
                            newWidget = new betterform.ui.output.Plain({
                                name:controlId + "-value",
                                value:xfValue,
                                "class":classValue,
                                title:dojo.attr(sourceNode,"title"),
                                xfControlId:controlId
                            }, sourceNode);

                        }
                        else if (mediatype == "text/html") {
                            dojo.require("betterform.ui.output.Html");
                            newWidget = new betterform.ui.output.Html({
                                name:controlId + "-value",
                                value:xfValue,
                                "class":classValue,
                                title:dojo.attr(sourceNode,"title"),
                                xfControlId:controlId
                            }, sourceNode);

                        }
                        else if (mediatype.indexOf("image/") > -1) {
                                dojo.require("betterform.ui.output.Image");
                                newWidget = new betterform.ui.output.Image({
                                    name:controlId + "-value",
                                    src:xfValue,
                                    "class":classValue,
                                    title:dojo.attr(sourceNode,"title"),
                                    xfControlId:controlId
                                }, sourceNode);
                            }
                            else {
                                console.warn("UIElementFactory.createWidget(): unknown mediatype '" + mediatype + "' for output, rendering default output");
                                dojo.require("betterform.ui.output.Plain");
                                newWidget = new betterform.ui.output.Plain({
                                    name:controlId + "-value",
                                    value:xfValue,
                                    "class":classValue,
                                    title:dojo.attr(sourceNode,"title"),
                                    xfControlId:controlId
                                }, sourceNode);
                                
                            }
                        break;
                }
                break;
            case "range":
                var xfValue = dojo.attr(sourceNode,"value");
                var start = 0;
                var end = 10;
                var step = 1;

                if(dojo.attr(sourceNode,"start") != ""){
                    start = eval(dojo.attr(sourceNode,"start"));
                }
                if(dojo.attr(sourceNode,"end")!= ""){
                    end = eval(dojo.attr(sourceNode,"end"));
                } else if (dojo.attr(sourceNode,"end")== "" && dojo.attr(sourceNode,"start") != "") {
                    end = eval(dojo.attr(sourceNode,"start")) + end;
                }
                if(dojo.attr(sourceNode,"step") != ""){
                    step = eval(dojo.attr(sourceNode,"step"));
                }

                if(xfValue > end) {
                    xfValue = end;
                }
                if(xfValue < start) {
                    xfValue = start;
                }
                // console.debug("UIElementFactory Range: srcNode: ", sourceNode, " start: ", start, " end: ", end, " step: ",step);
                if(dojo.attr(sourceNode,"appearance")=="bf:rating"){
                    dojo.require("betterform.ui.range.Rating");
                    newWidget = new betterform.ui.range.Rating({
                        name:controlId + "-value",
                        value:xfValue,
                        "class":classValue,
                        title:dojo.attr(sourceNode,"title"),
                        xfControlId:controlId,
                        numStars:end
                    }, sourceNode);

                }else {
                    var discreteValues = ((end - start) / step) +1;
                    // create and setup Range Rules
                    var rulesNode = document.createElement('div');
                    dojo.require("betterform.ui.range.Slider");
                    sourceNode.appendChild(rulesNode);
                    var sliderRules = new dijit.form.HorizontalRule({
                        count: discreteValues,
                        container: "topDecoration",
                        style:"height:4px;"
                    },rulesNode);

                    // create and setup Range Labels
                    var labelNode = document.createElement('div');
                    sourceNode.appendChild(labelNode);

                    // setup the labels
                    var sliderLabels = new dijit.form.HorizontalRuleLabels({
                        count: 5,
                        style: "height:1.2em;font-size:75%;color:gray;",
                        labels: [start,end]
                    },labelNode);

                    // Create Slider
                    newWidget= new betterform.ui.range.Slider({
                        value:xfValue,
                        name:controlId + "-value",
                        slideDuration:0,
                        minimum:start,
                        maximum:end,
                        discreteValues:discreteValues,
                        intermediateChanges:"true",
                        showButtons:"true",
                        "class":classValue,
                        xfControlId:controlId,
                        style:"width:200px;"
                    },sourceNode);

                    // and start them both
                    newWidget.startup();
                    sliderRules.startup();

                }
                break;

            case "secret":
                var xfValue = sourceNode.innerHTML;
                dojo.require("betterform.ui.secret.Secret");
                newWidget = new betterform.ui.secret.Secret({
                    name:controlId + "-value",
                    value:xfValue,
                    "class":classValue,
                    title:dojo.attr(sourceNode,"title"),
                    xfControlId:controlId
                }, sourceNode);
                break;

            /* Select Cases */
            case "selectCheckBox":
              
                    dojo.require("betterform.ui.select.CheckBoxGroup");
                newWidget = new betterform.ui.select.CheckBoxGroup({
                    name:controlId + "-value",
                    "class":classValue,
                    title:dojo.attr(sourceNode,"title"),
                    xfControlId:controlId
                }, sourceNode);
                break;

            case "selectList":
                dojo.require("betterform.ui.select.MultiSelect");
                newWidget = new betterform.ui.select.MultiSelect({
                    name:controlId + "-value",
                    size:dojo.attr(sourceNode, "size"),
                    multiple:true,
                    "class":classValue,
                    title:dojo.attr(sourceNode,"title"),
                    xfControlId:controlId
                }, sourceNode);
                break;

            /* Select1 Cases */
            case "select1":
            case "select1ComboBox":
                dojo.require("betterform.ui.select1.ComboBox");
                newWidget = new betterform.ui.select1.ComboBox({
                    name:controlId + "-value",
                    value:"",
                    "class":classValue,
                    title:dojo.attr(sourceNode,"title"),
                    xfControlId:controlId
                }, sourceNode);
                break;

            case "select1ComboBoxOpen":
                     dojo.require("betterform.ui.select1.ComboBoxOpen");
                newWidget = new betterform.ui.select1.ComboBoxOpen({
                    name:controlId + "-value",
                    size:dojo.attr(sourceNode, "size"),
                    multiple:true,
                    "class":classValue,
                    title:dojo.attr(sourceNode,"title"),
                    xfControlId:controlId
                }, sourceNode);
                break;

            case "select1List":
                     dojo.require("betterform.ui.select1.Plain");
                newWidget = new betterform.ui.select1.Plain({
                    name:controlId + "-value",
                    size:dojo.attr(sourceNode, "size"),
                    "class":classValue,
                    title:dojo.attr(sourceNode,"title"),
                    xfControlId:controlId
                }, sourceNode);
                break;
            case "select1RadioButton":
                dojo.require("betterform.ui.select1.RadioGroup");
                newWidget = new betterform.ui.select1.RadioGroup({
                    name:controlId + "-value",
                    "class":classValue,
                    title:dojo.attr(sourceNode,"title"),
                    xfControlId:controlId
                }, sourceNode);
                break;
            case "textarea":
// todo: fixme: textarea mediatype="dojo" makes no sense - should be an appearance instead
                if(dojo.attr(sourceNode,"appearance")=="minimal" && mediatype != 'text/html' && mediatype !=  'dojo') {
                    dojo.require("betterform.ui.textarea.MinimalTextarea");
                    newWidget = new betterform.ui.textarea. MinimalTextarea({
                        name:controlId + "-value",
                        rows:5,
                        cols:40,
                        "class":classValue,
                        title:dojo.attr(sourceNode,"title"),
                        xfControlId:controlId
                    }, sourceNode);
                }
                else {
                    if(mediatype == 'text/html') {
                        dojo.require("betterform.ui.textarea.HtmlEditor");
                        newWidget = new betterform.ui.textarea.HtmlEditor({
                            name:controlId + "-value",
                            "class":classValue,
                            title:dojo.attr(sourceNode,"title"),
                            xfControlId:controlId
                            }, sourceNode);
                    } else if(mediatype == 'dojo') {
                        dojo.require("betterform.ui.textarea.DojoEditor");
                        newWidget = new betterform.ui.textarea.DojoEditor({
                            name:controlId + "-value",
                            "class":classValue,
                            rows:5,
                            cols:30,
                            title:dojo.attr(sourceNode,"title"),
                            xfControlId:controlId
                        }, sourceNode);
                    }else {
                        dojo.require("betterform.ui.textarea.SimpleTextarea");
                        newWidget = new betterform.ui.textarea.SimpleTextarea({
                            name:controlId + "-value",
                            "class":classValue,
                            title:dojo.attr(sourceNode,"title"),
                            xfControlId:controlId
                        }, sourceNode);
                    }
                }
                break;
            case "minimalTrigger":
            case "trigger":
                if(dojo.attr(sourceNode,"appearance")=="minimal") {
                    dojo.require("betterform.ui.trigger.LinkButton");
                    newWidget = new betterform.ui.trigger.LinkButton({
                        id:dojo.attr(sourceNode, "id"),
                        name:dojo.attr(sourceNode, "name")+"-value",
                        label:dojo.attr(sourceNode, "label"),
                        "class":classValue,
                        title:dojo.attr(sourceNode,"title"),
                        xfControlId:controlId
                    }, sourceNode);
                } else if (dojo.attr(sourceNode,"appearance")=="imageTrigger") {
                    dojo.require("betterform.ui.trigger.ImageButton");
                    newWidget = new betterform.ui.trigger.ImageButton({
                        id:dojo.attr(sourceNode, "id"),
                        name:dojo.attr(sourceNode, "name")+"-value",
                        label:dojo.attr(sourceNode, "label"),
                        title:dojo.attr(sourceNode,"title"),
                        xfControlId:controlId,
                        imageId:dojo.query(("button[id="+sourceNode.id+"]>span>span"))[0].id
                    }, sourceNode);
                    console.dirxml(sourceNode);
                }else {
                    dojo.require("betterform.ui.trigger.Button");
                    dojo.require("dijit.form.Button");
                    // console.debug("UIElementFactory: creating betterform.ui.trigger.Button sourceNode:",sourceNode );
                    newWidget = new betterform.ui.trigger.Button({
                        id:dojo.attr(sourceNode, "id"),
                        name:dojo.attr(sourceNode, "name")+"-value",
                        label:dojo.attr(sourceNode, "label"),
                        title:dojo.attr(sourceNode,"title"),
                        xfControlId:controlId
                    }, sourceNode);

                }
                break;
            case "submit":
                console.warn("TBD create Submit control: ", sourceNode);
                break;
            case "upload":
                switch(dataType) {
                    case "base64":
                    case "base64Binary":
                    case "hexBinary":
                    case "anyURI":
                        dojo.require("betterform.ui.upload.UploadPlain");
                        newWidget = new betterform.ui.upload.UploadPlain({
                            "class":classValue,
                            title:dojo.attr(sourceNode,"title"),
                            xfControlId:controlId
                        }, sourceNode);

                        break;
                    default:
                        console.error("Upload (" + controlId+"): unsupported datatype: ", dataType);
                }
                break;
            case "radio":
                var name = sourceNode.name;
                var value = sourceNode.value;
               // console.debug("UIElementFactory.create Radio Item for Control " + controlId + " [name:"+name+"] ! Properties: ", sourceNode, " value: " + value);
                dojo.require("betterform.ui.select1.RadioButton");
                newWidget = new betterform.ui.select1.RadioButton({
                    "class":classValue,                    
                    name:name,
                    value:value,
                    title:dojo.attr(sourceNode,"title"),
                    xfControlId:controlId
                }, sourceNode);
                break;
            default:
                console.warn("UIElementFactory: Unknown controlValue: '" +controlType + "' for Control " + controlId + "! Properties: ", sourceNode);
                break;
        }
        if(newWidget != undefined){
            return newWidget;
        }    

    }
});


