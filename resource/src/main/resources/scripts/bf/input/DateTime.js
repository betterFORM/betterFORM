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
                zulu:false,
                currentValue:null,



                postCreate:function() {
                    this.inherited(arguments);

                    if(this.value.indexOf("+") !=-1){
                        this.timezone = this.value.substring(this.value.indexOf("+"),this.value.length);
                    }
                    this.zulu = (this.value.indexOf("Z") !=-1);
                    // console.debug("DateTime.postCreate: value:",this.value, " timezone:",this.timezone, " zulu:",this.zulu);
                    this.applyValues(this.value);

                },

                applyValues:function(value) {
                    // console.debug("DateTime.applyValues value",value);
                    if(this.currentValue == value){
                       return;
                    }
                    this.currentValue = value;

                    // handle date part
                    var dateValue = "";
                    if(value != undefined && value != ""){
                        dateValue = stamp.fromISOString(value,{selector: "date",zulu:this.zulu});
                    }
                    // console.debug("DateTime.applyValues: Schema Value: '", value, "' dateValue:'",dateValue,"'");

                    if(this.dateDijit == undefined) {
                        this.dateDijit = new DateTextBox({value:dateValue, constraints:this.dateConstraints},this.dateFacet);
                    }
                    this.dateDijit.set("value", dateValue);

                    // handle time part
                    var timeValue = "";
                    if(value != undefined && value != ""){
                        timeValue = stamp.fromISOString(value,{zulu:this.zulu});
                    }

                    if(this.timeDijit == undefined) {
                        this.timeDijit = new TimeTextBox({value:timeValue,constraints:this.timeConstraints},this.timeFacet);
                    }
                    this.timeDijit.set("value",timeValue);

                    // overwritten Dojo functions to avoid validation by Dojo
                    this.dateDijit.validate = function(/*Boolean*/ isFocused){ return true; };
                    this.timeDijit.validate = function(/*Boolean*/ isFocused){ return true; };

                },

                _getControlValue:function(){
                    // console.debug("DateTime._getControlValue timezone: ", this.timezone, " zulu:",this.zulu);
                    var notISODate = this.dateDijit.get("value");
                    var currentDate = "";
                    if(notISODate){
                        currentDate = stamp.toISOString(notISODate,{ selector: "date" });
                    }
                    // console.debug("DateTime._getControlValue currentDate: ",currentDate);

                    var notISOTime = this.timeDijit.get("value");
                    // console.debug("notISOTime: ",notISOTime, " notISOTime.toUTCString()", notISOTime.toISOString());
                    var currentTime = "";
                    if(notISOTime){
                        if(this.zulu || this.timezone){
                            var zuluDT = stamp.toISOString(notISOTime, {zulu:this.zulu});
                            currentTime = zuluDT.substring(zuluDT.indexOf("T"),zuluDT.length);
                        }
                        else {
                            currentTime = stamp.toISOString(notISOTime,{ selector:"time", zulu:false });
                        }
                    }
                    // console.debug("DateTime._getControlValue currentTime: ",currentTime);
                    this.currentValue = currentDate  + currentTime;
                    // console.info("DateTime._getControlValue value: ",this.currentValue);
                    return this.currentValue;

                },
                // set function must be overwritten by any class creating an instance of DateTime
                set:function(attrName, value){
                    // console.warn("DateTime.set WARNING: Function must be overwritten by any class creating an instance of DateTime");
                    // console.debug("DateTime.set: attrName: "+ attrName+ "  value",value);
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
