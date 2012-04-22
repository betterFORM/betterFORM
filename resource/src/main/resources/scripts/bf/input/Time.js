define(["dojo/_base/declare",
    "dijit/_WidgetBase",
    "dijit/_TemplatedMixin",
    "dojo/text!./Time.html",
    "dijit/form/ComboBox",
    "dojo/dom-class",
    "dojo/dom-attr",
    "dojo/_base/connect",
    "dojo/_base/array"],
    function(declare, WidgetBase, TemplatedMixin, template, ComboBox,domClass, domAttr,connect,array){
        return declare([WidgetBase, TemplatedMixin], {

            templateString: template,
            widgetsInTemplate:true,
            appearance:null,

            postCreate:function() {
                console.debug("Time.postCreate: before this.inherited");
                this.inherited(arguments);
                this.hoursWidget = new ComboBox({},this.hoursFacet);
                this.minutesWidget = new ComboBox({},this.minutesFacet);
                this.secondsWidget = new ComboBox({},this.secondsFacet);
                this.applyValues(this.value);

                // console.debug("postCreate: this.daysDijit:",this.daysDijit);
                connect.connect(this.hoursWidget, "onChange", this, "_onHoursChanged");
                connect.connect(this.minutesWidget, "onChange", this, "_onMinutesChanged");
            },

            applyValues:function(value) {
                console.debug("Time.applyValues value:",value);
                var timeContainer = value.split(":");
                if(timeContainer.length != 3) {
                    return;
                }
                // console.debug("DropDownTime.postCreate this.timeContainer:", timeContainer);
                domAttr.set(this.hoursWidget.focusNode, "value", timeContainer[0]);
                domAttr.set(this.minutesWidget.focusNode, "value", timeContainer[1]);
                domAttr.set(this.secondsWidget.focusNode, "value", timeContainer[2]);
            },

            _onHoursChanged:function(evt) {
                // console.debug("DropDownTime.onHoursChanged: evt:",evt, " keyCode: ", evt.keyCode);
                var hours = domAttr.get(this.hoursWidget.focusNode,"value");
                // console.debug("constraint:", evt.keyCode != 16 && evt.keyCode != 9 && hours.length == "2");
                if(evt.keyCode != 16 && evt.keyCode != 9 && hours.length == "2"){
                    this.minutesWidget.focusNode.focus();
                }
            },

            _onMinutesChanged:function(evt) {
                // console.debug("DropDownTime.onMinutesChanged: evt:",evt, " keyCode: ", evt.keyCode);
                var minutes = domAttr.get(this.minutesWidget.focusNode,"value");
                if(evt.keyCode != 16 && evt.keyCode != 9 && minutes.length == "2"){
                    this.secondsWidget.focus();
                }
            },

            _onFocus:function() {
                // console.debug("betterform.ui.input.DropDownTime._onFocus");
                this.inherited(arguments);
            },

            _onBlur:function() {
                // console.debug("betterform.ui.input.DropDownTime._onBlur");
                this.inherited(arguments);
            },


            onChange: function(/*anything*/ newValue, /*Boolean, optional*/ priorityChange){
                // console.debug("betterform.ui.input.DropDownTime.onChange");
            },

            _getControlValue:function(){
                var hours = domAttr.get(this.hoursWidget.focusNode, "value");
                if(hours == undefined || hours == ""){
                    hours = "00";
                }
                var minutes = domAttr.get(this.minutesWidget.focusNode, "value");
                if(minutes == undefined || minutes == ""){
                    minutes = "00";
                }
                var seconds = domAttr.get(this.secondsWidget.focusNode, "value");
                if(seconds == undefined || seconds == ""){
                    seconds = "00";
                }
                var currentValue = hours + ":" + minutes + ":" + seconds;
                // console.debug("betterform.ui.input.DropDownTime.getControlValue currentValue: ", currentTime);
                return currentValue;
            },

            set:function(attrName, value){
                console.debug("Time.set: attrName: "+ attrName+ "  value",value);
                if(attrName == "value" && value != this.value){
                    this.value = value;
                    this.applyValues(value);
                    }
                else if(attrName == "readOnly"){
                    this.hoursWidget.set("readOnly", value);
                    this.minutesWidget.set("readOnly", value);
                    this.secondsWidget.set("readOnly", value);
                }
            },

            get:function(attrName) {
                console.debug("Time.get: attrName",attrName);
                if(attrName == "value"){
                    return this._getControlValue();
                }
            }
        });
    });