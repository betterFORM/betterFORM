define(["dojo/_base/declare","dojo/_base/connect","dijit/registry","dojo/dom-attr","dojo/dom-class","bf/util"],
    function(declare,connect,registry,domAttr,domClass) {
        return declare(null,
            {
                /**
                 *
                 * @param type
                 * @param node
                 */
                create:function(type, node){
                    // console.debug("FactoryInput: type",type, " node:",node);
                    var xfControlDijit = registry.byId(bf.util.getXfId(node));
                    /*
                     xfControl is an instance of the XFControl class. This is the generic class that handles all interactions
                     with the XForms processor implementation. The concrete native browser or javascript controls are called
                     'widgets' in the context of the client side. They are the concrete representations the user interacts with.
                     @see: _createText() for more information about how to connect xfControl and a concrete Widget
                     */

                    switch(type){
                        //INPUT TYPE STRING
                        case "text":
                            this._createText(xfControlDijit,node);
                            break;
                        //INPUT TYPE BOOLEAN
                        case "checkbox":
                            this._createCheckbox(xfControlDijit, node);

                            break;
                        //INPUT TYPE DATE
                        case "date":
                            // console.debug("FactoryInput: found: .uaDesktop .xfInput.xsdDate .widgetContainer",node);
                            this._createDate(xfControlDijit, node);
                            break;
                        case "dropDownDate":
                            // console.debug("FactoryInput: found: .uaDesktop .xfInput.xsdDate .widgetContainer",node);
                            this._createDropDownDate(xfControlDijit, node);
                            break;
                        //INPUT TYPE DATE TIME
                        case "dateTime":
                            this._createDateTime(xfControlDijit,node);
                            break;
                        //INPUT TYPE TIME
                        case "timeTextBox":
                            this._createTimeTextBox(xfControlDijit,node);
                            break;
                        case "dropDownTime":
                            this._createDropDownTime(xfControlDijit,node);
                            break;
                        case "mobileDate":
                            this._createMobileDate(xfControlDijit, node);
                            break;
                        case "mobileDateTime":
                            this._createMobileDateTime(xfControlDijit, node);
                            break;
                        case "mobileTime":
                            this._createMobileTime(xfControlDijit, node);
                            break;
                        case "timeline":
                            this._createTimeline(xfControlDijit, node);
                            break;
                        case "linechart":
                            this._createLinechart(xfControlDijit, node);
                            break;

                        case "tbd":
                            console.warn("No handler for node", node, " yet");
                        default:
                            console.warn("FactoryInput unknown type for node:", node);

                    }
                },

                _createText:function(xfControlDijit,node){
                    // console.debug("FactoryInput.createInputPlain");
                    /* Overwriten "abstract" API function on XFControl to handle updating of control values */
                    xfControlDijit.setValue = function(value, schemavalue) {
                        // console.debug("FactoryInput._createText xfControlDijit.setValue:",value);
                        domAttr.set(node, "value", value);
                    };

                    /*
                     ###########################################################################################
                     EVENT BINDING
                     ###########################################################################################

                     Widgets are bound to their XFControl via events. Whenever a user changes the value of a control this change
                     must be propagated to its XFControl which will in turn send it to the server whenever appropriate.

                     There must be at least one event handler to notify XFControl of value changes. However it is highly
                     recommended to add two listeners - one to support incremental updates and one for onblur updates. Please
                     be aware that the order of registration can be significant for proper operation.
                     */

                    connect.connect(node,"onkeyup",function(evt){
                        // console.debug("onkeypress",n);
                        if(xfControlDijit.isIncremental()){
                            xfControlDijit.sendValue(node.value,false);
                        }
                    });

                    connect.connect(node,"onblur",function(evt){
                        // console.debug("onblur",n);
                        xfControlDijit.sendValue(node.value,true);
                    });

                    connect.connect(node,"onfocus",function(evt){
                        // console.debug("xf:input text got focus");
                        xfControlDijit.handleOnFocus();
                    });
                    xfControlDijit.setCurrentValue(domAttr.get(node,"value"));

                },


                _createCheckbox:function(xfControlDijit, node){
                    // console.debug("FactoryInput.createInputBoolean");
                    // console.debug("FOUND: boolean input field: ",node, " node.checked:",node.checked);
                    xfControlDijit.setCurrentValue(node.checked);
                    if(domAttr.get(node,"type") != "checkbox"){
                        domAttr.set(node,"type","checkbox");
                    }
                    /* overwritten "abstract" API function of XFControl */
                    xfControlDijit.setValue = function(value, schemavalue) {
                        domAttr.set(node,"checked",value  == true || value == 'true');
                    };

                    /*
                     input type="checkbox" fails to honor readonly attribute and thus is overwritten here. It seems this is
                     rather a HTML Spec issue as e.g. comboxes behave the same. You can visibly change the value though
                     the control is readonly. As this seems rather contra intuitive for users we have chosen to use 'disabled'
                     here instead.
                     */
                    xfControlDijit.setReadonly = function() {
                        // console.debug("overwritten checkbox function");
                        domClass.replace(node,"xfReadOnly","xfReadWrite");
                        domAttr.set(node, "disabled","disabled");
                    };
                    xfControlDijit.setReadwrite = function() {
                        domClass.replace(node,"xfReadWrite","xfReadOnly");
                        node.removeAttribute("disabled");
                    };

                    connect.connect(node,"onblur",function(evt){
                        // console.debug("onblur",node, " node.checked:",node.checked);
                        xfControlDijit.sendValue(node.checked,true);
                    });

                    connect.connect(node,"onclick",function(evt){
                        // console.debug("FactoryInput (boolean) onclick node.checked:",node.checked);
                        xfControlDijit.sendValue(node.checked,false);
                    });

                    connect.connect(node,"onfocus",function(evt){
                        // console.debug("FactoryInput (boolean) onfocus node.checked:",node.checked);
                        xfControlDijit.handleOnFocus();
                    });


                },

                _createDropDownDate:function(xfControlDijit, node){
                    var n = node;
                    var self = this;
                    require(["dojo/query","bf/input/DropDownDate"],function(query,DropDownDate){
                        n = query(".xfValue",node)[0];
                        var xfId = bf.util.getXfId(n);
                        var xfControlDijit = registry.byId(xfId);
                        var dataObj = bf.util.parseDataAttribute(n,"data-bf-params");
                        var dateFormat = dataObj.date;
                        var value = dataObj.value;
                        if(!value) {
                            value = domAttr.get(n,"value");
                        }
                        var dateWidget = new DropDownDate({
                            value:value,
                            dateFormat:dateFormat
                        },n);
                        xfControlDijit.setCurrentValue(value);
                        self._connectControlDijit(xfControlDijit, dateWidget);
                    });
                },


                _createDate:function(xfControlDijit, node){
                    var n = node;
                    var self = this;
                    require(["dojo/dom","dojo/query","dijit/form/DateTextBox"],function(dom, query,DateTextBox){
                        n = query(".xfValue",node)[0];
                        // console.debug("found date value node: n:",n);
                        var xfId = bf.util.getXfId(n);
                        var xfControlDijit = registry.byId(xfId);

                        var dataObj = bf.util.parseDataAttribute(n,"data-bf-params");
                        var datePattern = dataObj.date;
                        if(!datePattern || datePattern == ""){
                            datePattern = "MM/dd/yyyy"
                        }
                        // console.debug("input type=date datePattern:",datePattern);
                        var value = dataObj.value;
                        if(!value) {
                            value = domAttr.get(n,"value");
                        }
                        if(value == ""){
                            value = undefined;
                        }else {
                            xfControlDijit.setCurrentValue(value);
                        }
                        var hint = dom.byId(xfId+"-hint");
                        var dateWidget = new DateTextBox({
                            required:false,
                            placeHolder: (hint != undefined && !hint.hasChildNodes())? hint.innerHTML : "",
                            constraints:{
                                selector:'date',
                                datePattern:datePattern
                            } },n);
                        if(value != undefined) {
                            dateWidget.set("value",value);
                        }
                        dateWidget.validate = function(/*Boolean*/ isFocused){ return true; };
                        self._connectControlDijit(xfControlDijit, dateWidget);
                    });
                },

                _createDateTime:function(controlDijit, node){
                    var n = node;
                    // console.debug("_createDateTime: node value: ",domAttr.get(node,"value"));
                    var xfControlDijit = controlDijit;
                    var controlId =domAttr.get(n,"id");

                    var dataObj = bf.util.parseDataAttribute(n,"data-bf-params");
                    console.debug("createDateTime: dataObj:",dataObj);
                    var tmpValue = dataObj.value;
                    if(!tmpValue) {
                        tmpValue = domAttr.get(n,"value");
                    }
                    // console.debug("FactoryInput._createDateTime: tmpValue:",tmpValue);
                    var xfValue = this._getISODate(tmpValue);
                    // console.debug("FactoryInput._createDateTime: xfValue:",xfValue);

                    xfControlDijit.setCurrentValue(xfValue);
                    var self = this;
                    var xfId = bf.util.getXfId(n);
                    // console.debug("FactoryInput dateTime: id: ", controlId, " xfValue: ",xfValue, " node:",n);
                    require(["bf/input/DateTime"], function(DateTime) {
                        var dateTimeWidget = new DateTime({
                            name:controlId,
                            value:xfValue,
                            xfControlDijit:xfControlDijit,
                            miliseconds:false,
                            appearance:"minimal",
                            dateConstraints:{
                                datePattern:'M/d/yyyy'
                            },
                            timeConstraints:{
                                timePattern:'HH:mm:ss z',
                                clickableIncrement: 'T00:15:00',
                                visibleIncrement: 'T00:15:00',
                                visibleRange: 'T01:00:00'

                            },
                            title:domAttr.get(n, "title"),
                            xfControlId:xfId
                        },n);

                        connect.connect(dateTimeWidget, "set", function (attrName, value) {
                            // console.debug("dateTimeWidget: set attrName:",attrName, " value:",value, " incremental:", xfControlDijit.isIncremental());
                            if((attrName == "focused" &&  !value) || attrName == "value"){
                                if(attrName == "focused"){
                                    xfControlDijit.sendValue(this.get("value"),true);
                                }else if(attrName == "value" && xfControlDijit.isIncremental()) {
                                    xfControlDijit.sendValue(this.get("value"),false);
                                }

                            }else if(attrName == "focused" &&  value){
                                xfControlDijit.handleOnFocus();
                            }
                        });

                        xfControlDijit.setValue = function(value,schemavalue) {
                            // console.debug("FactoryInput._createDateTime xfControlDijit.setValue: ",value,schemavalue );
                            dateTimeWidget.set('value', schemavalue);
                        };

                        self._overwriteReadonly(xfControlDijit, dateTimeWidget);
                    });

                },
                _createTimeTextBox:function(controlDijit, n){
                    var xfControlDijit = controlDijit;
                    var node = n;
                    var self = this;
                    // console.info("FactoryInput Time");
                    require(["dijit/form/TimeTextBox","dojo/date/stamp"],function(TimeTextBox,stamp){
                        var value = domAttr.get(node,"value");
                        xfControlDijit.setCurrentValue(value);
                        // console.debug("FactoryInput TimeValue1:",value, " node:",node);
                        var timezone = undefined;
                        if(value.indexOf("+") !=-1){
                            timezone = value.substring(value.indexOf("+"),value.length);
                        }
                        var zulu = (value.indexOf("Z") !=-1);
                        if(value != undefined && value != "" && value.indexOf("T")==-1){
                            value = "T"+value
                        }
                        // console.debug("FactoryInput TimeValue2:",value, " node:",node);
                        var timeTextBox = new TimeTextBox({
                            value:value,
                            constraints: {
                                timePattern:'HH:mm:ss',
                                clickableIncrement: 'T00:15:00',
                                visibleIncrement: 'T00:15:00',
                                visibleRange: 'T02:00:00'
                            }
                        },node);
                        connect.connect(timeTextBox, "set", function (attrName, value) {
                            // console.debug("InputFactor (timeTextBox).set attrName:",attrName," value:",value);
                            if((attrName == "focused" &&  !value) || attrName == "value") {
                                var textboxTime = timeTextBox.get("value");
                                if(textboxTime != undefined && textboxTime != ""){
                                    textboxTime = stamp.toISOString(textboxTime,{selector:"time",zulu:zulu});
                                    // console.debug("toISOString:",textboxTime);
                                    if(textboxTime.indexOf("T") != -1){
                                        // console.debug("cut off T");
                                        textboxTime = textboxTime.substring(1,textboxTime.length);
                                    }
                                }
                                // console.debug("textboxTime:",textboxTime);
                                if(attrName == "focused") {
                                    xfControlDijit.sendValue(textboxTime,true);
                                }else if(attrName == "value" && xfControlDijit.isIncremental())
                                    xfControlDijit.sendValue(textboxTime,false);
                            }
                        });

                        xfControlDijit.setValue = function(value,schemavalue) {
                            // console.debug("value:",value);
                            if(value != undefined && value != "" && value.indexOf("T")==-1){
                                value = "T"+value
                            }
                            timeTextBox.set('value', value);
                        };
                        self._overwriteReadonly(xfControlDijit,timeTextBox);

                        connect.connect(timeTextBox,"_onFocus",function(evt){
                            xfControlDijit.handleOnFocus();
                        });

                    });
                },

                _createTimeline:function(controlDijit, n){
                    var xfControlDijit = controlDijit;
                    var node = n;
                    var self = this;
                    require(["bf/input/Timeline"],function(Timeline){
                        var value = domAttr.get(node,"value");
                        xfControlDijit.setCurrentValue(value);
                        var timeline = new Timeline({ value:value}, node);
                        console.debug("created new timeline: " , timeline );

                        xfControlDijit.setValue = function(value,schemavalue) {
                            // console.debug("value:",value);
                            timeline.set('value', value);
                        };
                    });
                },

                _createLinechart:function(controlDijit, n){
                    var xfControlDijit = controlDijit;
                    var node = n;
                    var self = this;
                    require(["bf/input/Linechart"],function(Linechart){
                        var value = domAttr.get(node,"value");
                        xfControlDijit.setCurrentValue(value);
                        var linechart= new Linechart({ value:value}, node);
                        console.debug("created new Linechart: " , linechart);

                        xfControlDijit.setValue = function(value,schemavalue) {
                            // console.debug("value:",value);
                            linechart.set('value', value);
                        };
                    });
                },



                _createDropDownTime:function(controlDijit, n){
                    var xfControlDijit = controlDijit;
                    var node = n;
                    var self = this;
                    require(["bf/input/Time","dojo/date/stamp"],function(Time,stamp){
                        var value = domAttr.get(node,"value");
                        xfControlDijit.setCurrentValue(value);
                        var time = new Time({ value:value}, node);
                        connect.connect(time, "set", function (attrName, value) {
                            // console.debug("InputFactor (dropDownTime.set value:",value);
                            if(attrName == "focused" && !value){
                                xfControlDijit.sendValue(time.get("value"), true);
                            }else if(attrName == "value" && xfControlDijit.isIncremental()){
                                xfControlDijit.sendValue(time.get("value"), false);
                            }else if(attrName == "focused" && value){
                                xfControlDijit.handleOnFocus();
                            }
                        });

                        xfControlDijit.setValue = function(value,schemavalue) {
                            // console.debug("value:",value);
                            time.set('value', value);
                        };
                        self._overwriteReadonly(xfControlDijit,time);
                    });
                },

                _createMobileDate:function(xfControlDijit, dateWidget){
                    this._createMobileWidget(xfControlDijit,dateWidget,"date");

                },

                _createMobileDateTime:function(xfControlDijit, dateTimeWidget){
                    this._createMobileWidget(xfControlDijit,dateTimeWidget,"dateTime");
                },

                _createMobileTime:function(xfControlDijit, timeWidget){
                    this._createMobileWidget(xfControlDijit,timeWidget,"time");
                },

                _createMobileWidget:function(xfControlDijit, widget, type){
                    var dataObj = bf.util.parseDataAttribute(widget,"data-bf-params");
                    var value = this._getISODate(dataObj.value);
                    domAttr.set(widget, "value", value);
                    xfControlDijit.setCurrentValue(value);

                    xfControlDijit.setValue = function(value, schemavalue) {
                        domAttr.set(widget, "value", value);
                    };
                    var self = this;
                    connect.connect(widget,"onkeyup",function(evt){
                        var value = widget.value;
                        if(type == "dateTime"){
                            value = self._getISODateTime(value);
                        }else if(type=="time"){
                            value = self._getMobileTime(value);
                        }
                        //console.debug("send: (keyup)"+ value);
                        xfControlDijit.sendValue(value,false);
                    });
                    connect.connect(widget,"onblur",function(evt){
                        var value = widget.value;
                        if(type == "dateTime"){
                            value = self._getISODateTime(value);
                        }else if(type=="time"){
                            value = self._getMobileTime(value);
                        }
                        // console.debug("send: (keyup)"+ value);
                        // console.debug("dateTime.send: (blur) "+ value);
                        xfControlDijit.sendValue(value, true);
                    });
                },
                /**
                 *
                 * @param xfControlDijit
                 * @param controlWidget
                 * @private
                 */
                _connectControlDijit:function(xfControlDijit, controlWidget){
                    // console.debug("connectDateDijit: xfControlDijit:",xfControlDijit," controlWidget:",controlWidget);
                    if(!domClass.contains(controlWidget.domNode,"xfValue")){
                        domClass.add(controlWidget.domNode,"xfValue");
                    }

                    connect.connect(controlWidget, "set", function (attrName, value) {
                        console.debug("controlWidget.set attrName:",attrName, " value:",value, " incremental: ",xfControlDijit.isIncremental());
                        if((attrName == "focused" &&  !value) || attrName == "value") {
                            var controlValue;
                            if(controlWidget.serialize){
                                try {
                                    controlValue = controlWidget.serialize(controlWidget.get("value")).substring(0, 10);
                                }
                                catch(e){
                                    // if the value could not be parsed (e.g. cause it's invalid) simply return the displayed value
                                    controlValue = controlWidget.get("displayedValue");
                                    // console.debug("Error serializing date: controlValue:",controlValue, " controlWidget:",controlWidget);
                                }

                            }else{
                                controlValue = controlWidget.get("value");
                            }
                            // looses focus
                            if(attrName == "focused"){
                                xfControlDijit.sendValue(controlValue, true);
                            }
                            else if(attrName == "value" && xfControlDijit.isIncremental()){
                                xfControlDijit.sendValue(controlValue,false);
                            }


                        }else if(attrName == "focused" &&  value){
                            xfControlDijit.handleOnFocus();
                        }
                    });
                    xfControlDijit.setValue = function(value,schemavalue) {
                        //console.debug("FactoryInput (date) xfControlDijit.setValue value:",value, " schemavalue:",schemavalue);
                        if(schemavalue == ""){
                            schemavalue = undefined;
                        }
                        controlWidget.set('value', schemavalue);
                    };
                    this._overwriteReadonly(xfControlDijit, controlWidget);
                },

                _overwriteReadonly:function(xfControlDijit,controlWidget){
                    xfControlDijit.setReadonly = function() {
                        domClass.replace(xfControlDijit.domNode,"xfReadOnly","xfReadWrite");
                        controlWidget.set('readOnly', true);
                    };
                    xfControlDijit.setReadwrite = function() {
                        domClass.replace(xfControlDijit.domNode,"xfReadWrite","xfReadOnly");
                        controlWidget.set('readOnly', false);
                    };
                },

                _getISODate:function(value){
                    var timezone = undefined;
                    if(value.indexOf("+") !=-1){
                        timezone = value.substring(value.indexOf("+"),value.length);
                        if(timezone.indexOf(":")!=-1){
                            timezone = timezone.replace(":","");
                        }
                    }
                    // console.debug("FactoryInput._getISODate: value:",value);
                    var zulu = (value.indexOf("Z") !=-1);
                    if(timezone == undefined && value != undefined && value != "" && !zulu){
                        value = value + "Z";
                    }

                    if(value) {
                        var date = new Date(value);
                        // console.debug("FactoryInput._getISODate: date:",date);
                        return date.toISOString();
                    }else {
                        // console.debug("FactoryInput._getISODate: date is undefined");
                        return "";
                    }
                },

                _getISODateTime:function(value){
                    var datePart = value.substring(0,value.indexOf('T'));
                    // console.debug("datePart:" + datePart);
                    var timePart = value.substring(value.indexOf('T'),value.length);
                    // console.debug("timePart:" + timePart + " " + timePart.length);
                    if(timePart.length == 7) {
                        timePart = timePart.substring(0,6)+":00.000Z";
                    }
                    var result = datePart + timePart;
                    // console.debug("result:" + result);
                    return result;
                },

                _getMobileTime:function(value){
                    if(value.length == 5) {
                        value = value +":00";
                    }
                    // console.debug("_getMobileTime: value" + value);
                    return value;
                }
            }
        );
    }
);

