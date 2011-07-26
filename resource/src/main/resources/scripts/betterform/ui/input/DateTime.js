    /*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.input.DateTime");


dojo.require("dijit.form.DateTextBox");
dojo.require("dijit.form.TimeTextBox");

dojo.declare(
        "betterform.ui.input.DateTime",
        betterform.ui.ControlValue,
{
    templateString: dojo.cache("betterform", "ui/templates/DateTime.html"),
    id:null,
    widgetsInTemplate:true,
    dateDijit:null,
    timeDijit:null,
    constraints:null,
    timezone:null,

    postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },

    postCreate:function() {
        this.inherited(arguments);
        dojo.attr(this.domNode, "value", this.value);

        // console.debug("DateTime.postCreate: value:",this.value);
        this.applyValues(this.value);
    },


    _onFocus:function() {
        // console.debug("DateTime.onFocus()");
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function(){
        this.incremental = false;
        this.handleOnBlur()
    },

    applyValues:function(value) {
        // console.debug("DateTime.applyValues value",value);
        if(value.indexOf("+") !=-1){
            this.timezone = value.substring(value.indexOf("+"),value.length);
            value = value.substring(0,value.indexOf("+"));
        }
        if(value.indexOf("Z") !=-1){
            this.timezone = "Z"
            value = value.substring(0,value.indexOf("Z"));
        }
        // console.debug("this.timezone:",this.timezone, " value: ",value);
        var dateValue;
        var timeValue;
        if(value != undefined && value != ""){
            var tmpValue = value.split("T");
            dateValue = dojo.date.stamp.fromISOString(tmpValue[0],this.constraints);
            timeValue = dojo.date.stamp.fromISOString("T"+tmpValue[1],this.constraints);
        }else {
            dateValue = "";
            timeValue = "";
        }
        // console.debug("Date:", dateValue, " Time:",timeValue);

        // handle date part
        if(this.dateDijit == undefined) {
            this.dateDijit = dijit.byId(this.dateFacet.id);
            if(this.constraints.formatLength != undefined){
                this.dateDijit.constraints.formatLength = this.constraints.formatLength;
            }
            if(this.constraints.locale != undefined){
                this.dateDijit.constraints.locale = this.constraints.locale;
            }
            if(this.constraints.datePattern != undefined){
                this.dateDijit.constraints.datePattern = this.constraints.datePattern;
            }
            this.dateDijit.constraints.selector = "date";
        }
        this.dateDijit._setValueAttr(dateValue);

        // handle time part
        if(this.timeDijit == undefined) {
            this.timeDijit = dijit.byId(this.timeFacet.id);
            if(this.constraints.formatLength != undefined){
                this.timeDijit.constraints.formatLength = this.constraints.formatLength;
            }
            if(this.constraints.locale != undefined){
                this.timeDijit.constraints.locale = this.constraints.locale;
            }
            if(this.constraints.timePattern != undefined){
                this.timeDijit.constraints.timePattern = this.constraints.timePattern;
            }
            this.timeDijit.constraints.selector = "time";
        }

        this.timeDijit._setValueAttr(timeValue);
        this.setCurrentValue(value);
    },

    getControlValue:function(){
        var currentDate;
        var notISODate = this.dateDijit.get('value');
        if(notISODate == undefined){
           // console.debug("Empty (undefined) Date: this.dateDijit: " , this.dateDijit);
           currentDate = this.dateDijit.focusNode.value;
        }else {
            currentDate =  dojo.date.stamp.toISOString(notISODate,this.constraints);
        }
        if(currentDate == undefined){
            currentDate ="";
        } else if(currentDate.length == 25 && currentDate.indexOf("T") == 10){
            currentDate = currentDate.substring(0,11);
        }


        var currentTime = this.timeDijit.textbox.value;
        // console.debug("CurrentTime: ",currentTime, " currentTime.length:"+ currentTime.length + " currentTime.indexOf(':') :"+currentTime.indexOf(":") + " milliseconds:",this.miliseconds);
        // console.debug("_getDisplayedValueAttr: ",this.timeDijit._getDisplayedValueAttr());
        if(currentTime == undefined){
            currentTime = "";
        }else if(currentTime.length == 5 && currentTime.indexOf(":")==2){
            currentTime += ":00";
        }

        if(currentTime.length == 8 && currentTime.indexOf(":")==2 && this.miliseconds){
            currentTime += ".000";
        }

        if((currentTime.length == 12 || currentTime.length ==8) && currentTime.indexOf(":")==2 && this.value.indexOf("Z") !=-1) {
            currentTime += "Z";
        }else if(this.timezone != undefined) {
            currentTime +=this.timezone;
        }
        // console.debug("DateTime.getControlValue time: ",currentTime);

        this.value = currentDate   +currentTime;
        //console.debug("DateTime.getControlValue value: ",this.value);
        return this.value;

    },

    _handleSetControlValue:function(value) {
         console.debug("_handleSetControlValue value",value);
        if(this.miliseconds && value.indexOf(".") != -1){
            value = value.substring(0,value.indexOf("."));
        }
        // console.debug("Date._handleSetControlValue date:",value);
        this.applyValues(value);
    },

    /*
     only needs to check if XForms MIP readonly is true and disable control in that case. The value itself
     is already present and other MIPs are entirely managed through CSS.
     */
    applyState:function() {
        this.dateDijit.set("readOnly",this.xfControl.isReadonly());
        this.timeDijit.set("readOnly",this.xfControl.isReadonly());
    }

});



