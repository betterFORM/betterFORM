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
    "dojo/date/stamp",
    "dojo/_base/connect",
    "dojo/_base/lang",
    "dojo/i18n!dojo/cldr/nls/en/gregorian"],
    function(declare, WidgetBase, TemplatedMixin, template,DateTextBox, TimeTextBox,stamp,connect,lang) {
        return declare([WidgetBase, TemplatedMixin],
            {

                templateString: template,

                id:null,
                widgetsInTemplate:true,
                dateDijit:null,
                timeDijit:null,
                timezone:null,
                zulu:false,
                currentValue:null,
                xfControlDijit:null,

                applyValues:function(value) {
                    // console.debug("DateTime.applyValues value",value, " this.id:",this.id);
                    this.currentValue = value;

                    // handle date part
                    var init = false;
                    if(this.dateDijit == undefined){
                        init = true;
                        this.dateDijit = new DateTextBox({value:value, constraints:this.dateConstraints},this.dateFacet);
                        // overwritten Dojo functions to avoid validation by Dojo
                        this.dateDijit.validate = function(/*Boolean*/ isFocused){ return true; };
                    }
                    this.dateDijit.set("value", value);

                    if(this.timeDijit == undefined){
                        this.timeDijit = new TimeTextBox({value:value, constraints:this.timeConstraints},this.timeFacet);
                        this.timeDijit.validate = function(/*Boolean*/ isFocused){ return true; };
                    }
                    this.timeDijit.set("value",value);

                    if(init == true){
                        connect.connect(this.dateDijit,"set",lang.hitch(this, function(attrName, value) {
                            // console.debug("DateTime: dateDijit.set: attrName: ", attrName, " value: ",value, " incremental: ",this.xfControlDijit.isIncremental());
                            if(((attrName == "focused" && !value)  || attrName == "value") && this.xfControlDijit.isIncremental()) {
                                this.xfControlDijit.sendValue(this.get("value"),false);
                            }
                        }));

                        connect.connect(this.timeDijit,"set",lang.hitch(this, function(attrName, value) {
                            // console.debug("DateTime: timeDijit.set: attrName: ", attrName, " value: ",value, " incremental: ",this.xfControlDijit.isIncremental());
                            if(((attrName == "focused" && !value)  || attrName == "value") && this.xfControlDijit.isIncremental()) {
                                this.xfControlDijit.sendValue(this.get("value"));
                            }
                        }));
                    }
                },

                _getControlValue:function(){
                    var notISODate = this.dateDijit.get("value");
                    var currentDate = "";
                    if(notISODate){
                        currentDate = stamp.toISOString(notISODate,{ selector: "date" });
                    }
                    // console.debug("DateTime._getControlValue currentDate: ",currentDate);
                    var notISOTime = this.timeDijit.get("value");
                    // convert to msec substract local time zone offset
                    var utcDate = new Date(notISOTime.getTime() + (notISOTime.getTimezoneOffset() * 60000)).toISOString();
                    //console.debug("UTC Date:",utcDate, " iso:",utcDate);
                    var currentTime = utcDate.substring(utcDate.indexOf("T"),utcDate.length);
                    // console.debug("DateTime._getControlValue currentTime: ",currentTime);
                    this.currentValue = currentDate  + currentTime;
                    // console.info("DateTime._getControlValue value: ",this.currentValue);
                    return this.currentValue;

                },
                // set function must be overwritten by any class creating an instance of DateTime
                set:function(attrName, value){
                    // console.warn("DateTime.set WARNING: Function must be overwritten by any class creating an instance of DateTime");
                    if(attrName == "value"){
                        // console.debug("DateTime._handleSetControlValue value",value);
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
