define(["dojo/_base/declare","dojo/_base/connect","dijit/registry","dojo/dom-attr","dojo/dom-class","bf/util"],
    function(declare,connect,registry,domAttr,domClass) {
        return declare(null,
            {
                /**
                 *
                 * @param type
                 * @param node
                 */
                create:function(cssType, n){
                    var type = cssType;
                    var node = n;
                    var xfControlDijit = registry.byId(bf.util.getXfId(node));
                    /*
                     xfControl is an instance of the XFControl class. This is the generic class that handles all interactions
                     with the XForms processor implementation. The concrete native browser or javascript controls are called
                     'widgets' in the context of the client side. They are the concrete representations the user interacts with.
                     @see: _createText() for more information about how to connect xfControl and a concrete Widget
                     */

                    var appearance = domAttr.get(node,"appearance");
                    if("bf:dropdowntime" == appearance){
                        type = "dropDownTime"
                    }
                    else if("bf:timetextbox" == appearance){
                        type = "timeTextBox"
                    }


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
                            console.warn("tbd: input dateTime");
                            break;
                        case "mobileTime":
                            console.warn("tbd: input Time");
                            break;
                        case "tbd":
                            console.warn("No handler for node", node, " yet");
                        default:
                            console.warn("FactoryInput.default");

                    }
                },

                _createText:function(xfControlDijit,node){
                    // console.debug("FactoryInput.createInputPlain");
                    /* Overwriten "abstract" API function on XFControl to handle updating of control values */
                    xfControlDijit.setValue = function(value, schemavalue) {
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
                        xfControlDijit.sendValue(node.value,evt);
                    });

                    connect.connect(node,"onblur",function(evt){
                        // console.debug("onblur",n);
                        xfControlDijit.sendValue(node.value, evt);
                    });

                },


                _createCheckbox:function(xfControlDijit, node){
                    // console.debug("FactoryInput.createInputBoolean");
                    // console.debug("FOUND: boolean input field: ",n);
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
                        bf.util.replaceClass(node,"xfReadWrite","xfReadOnly");
                        domAttr.set(node, "disabled","disabled");
                    };
                    xfControlDijit.setReadwrite = function() {
                        bf.util.replaceClass(node,"xfReadOnly","xfReadWrite");
                        node.removeAttribute("disabled");
                    };

                    connect.connect(node,"onblur",function(evt){
                        // console.debug("onblur",n);
                        if(node.checked != undefined){
                            xfControlDijit.sendValue(node.checked,evt);
                        }
                    });
                    connect.connect(node,"onclick",function(evt){
                        // console.debug("FactoryInput (boolean) onclick node.checked:",node.checked);
                        if(node.checked != undefined){
                            xfControlDijit.sendValue(node.checked,evt);
                        }
                    });

                },

                _createDate:function(xfControlDijit, node){
                    var n = node;
                    var self = this;
                    require(["dojo/query"],function(query){

                        n = query(".xfValue",node)[0];
                        // console.debug("found date value node: n:",n);

                        var xfId = bf.util.getXfId(n);
                        var xfControlDijit = registry.byId(xfId);
                        var appearance = domAttr.get(n,"appearance");
                        // console.debug("create new date for xfId:",xfId ," with appearance:",appearance);
                        var datePattern;
                        if (appearance && appearance.indexOf("iso8601:") != -1) {
                            datePattern = appearance.substring(appearance.indexOf("iso8601:")+8);
                            if(datePattern.indexOf(" ") != -1) {
                                datePattern = datePattern.substring(0,datePattern.indexOf(" ")).trim();

                            }
                        }
                        // console.debug("input type=date appearance:",appearance, " datePattern:",datePattern);
                        var xfValue = new Date(domAttr.get(n,"schemavalue"));
                        var dateWidget = undefined;
                        if(appearance && appearance.indexOf("bf:dropdowndate") != -1){
                            require(["bf/input/DropDownDate"], function(DropDownDate) {
                                dateWidget = new DropDownDate({
                                    value:domAttr.get(n,"schemavalue"),
                                    appearance:appearance,
                                    constraints:{
                                        selector:'date'
                                    }
                                },n);
                                self._connectDateDijit(xfControlDijit, dateWidget);
                            });
                        } else if (datePattern) {
                            require(["dijit/form/DateTextBox"], function(DateTextBox) {
                                dateWidget = new DateTextBox({
                                    value:xfValue,
                                    required:false,
                                    constraints:{
                                        selector:'date',
                                        datePattern:datePattern
                                    } },n);
                                dateWidget.validate = function(/*Boolean*/ isFocused){ return true; };
                                self._connectDateDijit(xfControlDijit, dateWidget);
                            });
                        } else {
                            require(["dijit/form/DateTextBox"], function(DateTextBox) {
                                dateWidget = new DateTextBox({
                                    value:xfValue,
                                    required:false,
                                    constraints:{
                                        selector:'date'
                                    }}, n);
                                dateWidget.validate = function(/*Boolean*/ isFocused){ return true; };
                                self._connectDateDijit(xfControlDijit, dateWidget);
                            });
                        }
                    });
                },

                _createDateTime:function(controlDijit, node){
                    var n = node;
                    var xfControlDijit = controlDijit;
                    var controlId =domAttr.get(n,"id");
                    var xfValue = domAttr.get(n,"schemavalue");
                    var xfId = bf.util.getXfId(n);
                    console.debug("FactoryInput dateTime: id: ", controlId, " xfValue: ",xfValue, " node:",n);
                    require(["bf/input/DateTime"], function(DateTime) {
                        var dateTimeWidget = new DateTime({
                            name:controlId,
                            value:xfValue,
                            miliseconds:false,
                            appearance:"minimal",
                            dateConstraints:{
                                datePattern:'M/d/yyyy'
                            },
                            timeConstraints:{
                                timePattern:'HH:mm:ss',
                                clickableIncrement: 'T00:15:00',
                                visibleIncrement: 'T00:15:00',
                                visibleRange: 'T01:00:00'

                            },
                            title:domAttr.get(n, "title"),
                            xfControlId:xfId
                        },n);

                        connect.connect(dateTimeWidget, "set", function (attrName, value) {
                            if((attrName == "focused" &&  !value) || attrName == "value") {
                                var dateTimeValue = dateTimeWidget.get("value");
                                var evt = new Object();
                                evt.type = attrName == "focused" ? "blur" : "focus";
                                xfControlDijit.sendValue(dateTimeValue, evt);
                            }
                        });

                        xfControlDijit.setValue = function(value,schemavalue) {
                            dateTimeWidget.set('value', schemavalue);
                        };
                        xfControlDijit.setReadonly = function() {
                            bf.util.replaceClass(n,"xfReadWrite","xfReadOnly");
                            dateTimeWidget.set('readOnly', true);
                        };
                        xfControlDijit.setReadwrite = function() {
                            bf.util.replaceClass(n,"xfReadOnly","xfReadWrite");
                            dateTimeWidget.set('readOnly', false);
                        };
                    });

                },
                _createTimeTextBox:function(controlDijit, n){
                    var xfControlDijit = controlDijit;
                    var node = n;
                    console.info("FactoryInput Time");
                    require(["dijit/form/TimeTextBox","dojo/date/stamp"],function(TimeTextBox,stamp){
                        var value = domAttr.get(node,"value");
                        console.debug("FactoryInput TimeValue1:",value, " node:",node);
                        var timezone = undefined;
                        if(value.indexOf("+") !=-1){
                            timezone = value.substring(value.indexOf("+"),value.length);
                        }
                        var zulu = (value.indexOf("Z") !=-1);
                        if(value != undefined && value != "" && value.indexOf("T")==-1){
                            value = "T"+value
                        }
                        console.debug("FactoryInput TimeValue2:",value, " node:",node);
                        var timeTextBox = new TimeTextBox({ value:value,
                            constraints: {
                                timePattern:'HH:mm:ss',
                                clickableIncrement: 'T00:15:00',
                                visibleIncrement: 'T00:15:00',
                                visibleRange: 'T02:00:00'
                            }
                        },node);
                        connect.connect(timeTextBox, "set", function (attrName, value) {
                            console.debug("InputFactor (timeTextBox.set value:",value);
                            if((attrName == "focused" &&  !value) || attrName == "value") {
                                var textboxTime = timeTextBox.get("value");
                                if(textboxTime != undefined && textboxTime != ""){
                                    textboxTime = stamp.toISOString(textboxTime,{selector:"time",zulu:zulu});
                                    console.debug("toISOString:",textboxTime);
                                    if(textboxTime.indexOf("T") != -1){
                                        console.debug("cut off T");
                                        textboxTime = textboxTime.substring(1,textboxTime.length);
                                    }
                                }
                                console.debug("textboxTime:",textboxTime);
                                var evt = new Object();
                                evt.type = attrName == "focused" ? "blur" : "focus";
                                xfControlDijit.sendValue(textboxTime, evt);
                            }
                        });
                        xfControlDijit.setValue = function(value,schemavalue) {
                            console.debug("value:",value);
                            if(value != undefined && value != "" && value.indexOf("T")==-1){
                                value = "T"+value
                            }
                            timeTextBox.set('value', value);
                        };
                        xfControlDijit.setReadonly = function() {
                            bf.util.replaceClass(n,"xfReadWrite","xfReadOnly");
                            timeTextBox.set('readOnly', true);
                        };
                        xfControlDijit.setReadwrite = function() {
                            bf.util.replaceClass(n,"xfReadOnly","xfReadWrite");
                            timeTextBox.set('readOnly', false);
                        };
                    });
                },

                _createDropDownTime:function(controlDijit, n){
                    var xfControlDijit = controlDijit;
                    var node = n;
                    require(["bf/input/Time","dojo/date/stamp"],function(Time,stamp){
                        var value = domAttr.get(node,"value");
                        var time = new Time({ value:value}, node);
                        connect.connect(time, "set", function (attrName, value) {
                            console.debug("InputFactor (dropDownTime.set value:",value);
                            if((attrName == "focused" &&  !value) || attrName == "value") {
                                var dateTimeValue = time.get("value");
                                var evt = new Object();
                                evt.type = attrName == "focused" ? "blur" : "focus";
                                xfControlDijit.sendValue(dateTimeValue, evt);
                            }
                        });

                        xfControlDijit.setValue = function(value,schemavalue) {
                            console.debug("value:",value);
                            time.set('value', value);
                        };
                        xfControlDijit.setReadonly = function() {
                            bf.util.replaceClass(n,"xfReadWrite","xfReadOnly");
                            time.set('readOnly', true);
                        };
                        xfControlDijit.setReadwrite = function() {
                            bf.util.replaceClass(n,"xfReadOnly","xfReadWrite");
                            time.set('readOnly', false);
                        };
                    });
                },


        /**
                 *
                 * @param xfControlDijit
                 * @param dateWidget
                 * @private
                 */
                _connectDateDijit:function(xfControlDijit, dateWidget){
                    // console.debug("connectDateDijit: xfControlDijit:",xfControlDijit," dateWidget:",dateWidget);
                    domClass.add(dateWidget.domNode,"xfValue");
                    connect.connect(dateWidget, "set", function (attrName, value) {
                        if((attrName == "focused" &&  !value) || attrName == "value") {
                            var dateValue;
                            if(dateWidget.serialize){
                                try {
                                    dateValue = dateWidget.serialize(dateWidget.get("value")).substring(0, 10);
                                }
                                catch(e){
                                    dateValue = dateWidget.get("displayedValue");
                                    console.debug("Error serializing date: dateValue:",dateValue, " dateWidget:",dateWidget);
                                }

                            }else{
                                dateValue = dateWidget.get("value");
                            }
                            var evt = new Object();
                            evt.type = attrName == "focused" ? "blur" : "focus";
                            xfControlDijit.sendValue(dateValue,evt);
                        }
                        xfControlDijit.setValue = function(value,schemavalue) {
                            dateWidget.set('value', schemavalue);
                        };
                        xfControlDijit.setReadonly = function() {
                            bf.util.replaceClass(xfControlDijit.domNode,"xfReadWrite","xfReadOnly");
                            dateWidget.set('readOnly', true);
                        };
                        xfControlDijit.setReadwrite = function() {
                            bf.util.replaceClass(xfControlDijit.domNode,"xfReadOnly","xfReadWrite");
                            dateWidget.set('readOnly', false);
                        };
                    });
                },

                _createMobileDate:function(xfControlDijit, dateWidget){
                    xfControlDijit.setValue = function(value, schemavalue) {
                        domAttr.set(node, "value", value);
                    };
                    connect.connect(n,"onkeyup",function(evt){
                        xfControlDijit.sendValue(n.value,evt);
                    });

                    connect.connect(n,"onblur",function(evt){
                        xfControlDijit.sendValue(n.value, evt);
                    });
                }
            }
        );
    }
);

