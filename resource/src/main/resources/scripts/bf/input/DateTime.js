    /*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
define(["dojo/_base/declare",
    "dijit/_WidgetBase",
    "dijit/_TemplatedMixin",
    "dojo/text!./DateTime.html",
    "dijit/form/DateTextBox",
    "dijit/form/TimeTextBox",
    "dojo/date/stamp"],
    function(declare, WidgetBase, TemplatedMixin, template,DateTextBox, TimeTextBox,stamp) {
        return declare([WidgetBase, TemplatedMixin],
            {

                templateString: template,

                id:null,
                widgetsInTemplate:true,
                dateDijit:null,
                timeDijit:null,
                timezone:null,
                currentValue:null,

                postCreate:function() {
                    this.inherited(arguments);
                    console.debug("DateTime.postCreate: this.value:",this.value);
                    // domAttr.set(this.domNode, "value", this.value);
                    // console.debug("DateTime.postCreate: value:",this.value);
                    this.applyValues(this.value);
                },

                applyValues:function(value) {
                    console.debug("DateTime.applyValues value",value);
                    if(this.currentValue == value){
                       return;
                    }

                    console.debug("\n\nDateTime.setControlValue 1: ",stamp.fromISOString(value));
                    console.debug("DateTime.setControlValue 2: ",stamp.fromISOString(value,{selector:"time"}));
                    console.debug("DateTime.setControlValue 3: ",stamp.fromISOString(value,{zulu:true}));
                    console.debug("DateTime.setControlValue 4: ",stamp.fromISOString(value,{zulu:false}),"\n\n");

                    this.currentValue = value;

                    if(value.indexOf("+") !=-1){
                        this.timezone = value.substring(value.indexOf("+"),value.length);
                        console.info("DateTime: timezone is: ",this.timezone);
                    }
                    if(value.indexOf("Z") !=-1){
                        this.timezone = "Z";
                        console.info("DateTime: timezone is: ",this.timezone);
                    }
                    // handle date part
                    if(this.dateDijit == undefined) {
                        this.dateDijit = new DateTextBox({constraints:this.dateConstraints},this.dateFacet);
                    }
                    var dateValue = "";
                    if(value != undefined && value != ""){
                        dateValue = stamp.fromISOString(value,{selector: "date",zulu:this.timezone == "Z"});
                    }
                    console.debug("DateTime.applyValues: Schema Value: '", value, "' dateValue:'",dateValue,"'");

                    this.dateDijit.set("value", dateValue);

                    // handle time part
                    if(this.timeDijit == undefined) {
                        this.timeDijit = new TimeTextBox({constraints:this.timeConstraints},this.timeFacet);
                    }
                    var timeValue = "";
                    if(value != undefined && value != ""){
                        timeValue = stamp.fromISOString(value,{selector: "time",zulu:this.timezone == "Z"});
                    }
                    this.timeDijit.set("value",timeValue);

                    // overwritten Dojo functions to avoid validation by Dojo
                    this.dateDijit.validate = function(/*Boolean*/ isFocused){ return true; };
                    this.timeDijit.validate = function(/*Boolean*/ isFocused){ return true; };

                },

                _getControlValue:function(){
                    var notISODate = this.dateDijit.get("value");
                    var currentDate = undefined;

                    if(notISODate){
                        currentDate = stamp.toISOString(notISODate,{ selector: "date" });
                    }else {
                        currentDate= "";
                    }
                    console.debug("DateTime._getControlValue currentDate: ",currentDate);

                    var notISOTime = this.timeDijit.get("value");

                    console.debug("\n\nDateTime._getControlValue 1: ",stamp.toISOString(notISOTime));
                    console.debug("DateTime._getControlValue 2: ",stamp.toISOString(notISOTime,{selector:"time"}));
                    console.debug("DateTime._getControlValue 3: ",stamp.toISOString(notISOTime,{zulu:true}));
                    console.debug("DateTime._getControlValue 4: ",stamp.toISOString(notISOTime,{zulu:false}),"\n\n");

                    console.debug("notISOTime: ",notISOTime);
                    console.debug("notISOTime offset: ",notISOTime.getTimezoneOffset());
                    var currentTime = undefined;
                    if(notISOTime){
                        if(this.timezone && this.timezone != "Z"){
                            currentTime = stamp.toISOString(notISOTime,{ selector: "time", zulu:false });
                        }
                        else {
                            currentTime = stamp.toISOString(notISOTime,{ selector: "time",zulu: this.timezone == "Z" });
                            console.debug("currentTimeXYZ:",currentTime);
                            if(currentTime.indexOf("+") != -1){
                                currentTime = currentTime.substring(0,currentTime.indexOf("+"));
                            }
                            console.debug("currentTimeABC:",currentTime);
                        }

                    }else {
                        currentTime= "";
                    }
                    console.debug("DateTime._getControlValue currentTime: ",currentTime);
                    this.currentValue = currentDate  + currentTime;
                    console.info("DateTime._getControlValue value: ",this.currentValue);
                    return this.currentValue;

                },
                // set function must be overwritten by any class creating an instance of DateTime
                set:function(attrName, value){
                    // console.warn("DateTime.set WARNING: Function must be overwritten by any class creating an instance of DateTime");
                    // console.debug("DateTime.set: attrName: "+ attrName+ "  value",value);
                    if(attrName == "value"){
                        console.debug("DateTime._handleSetControlValue value",value);
                        if(this.miliseconds && value.indexOf(".") != -1){
                            value = value.substring(0,value.indexOf("."));
                        }
                        // console.debug("DateTime.set value:",value);
                        this.applyValues(value);
                    }
                    else if(attrName == "readOnly"){
                        this.dateDijit.set("readOnly",value);
                        this.timeDijit.set("readOnly",value);
                    }
                },

                get:function(attrName) {
                    // console.debug("DropDownDate.get: attrName",attrName);
                    if(attrName == "value"){
                        return this._getControlValue();
                    }
                }
            }
        );
    }
);
