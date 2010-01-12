/*
	All Rights Reserved.
*/

/* last changes by J.Aerts 14.11.2006 */

dojo.provide("betterform.widget.Time");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.require("dojo.event.*");
dojo.require("dojo.html.*");
dojo.require("dojo.widget.Spinner");
dojo.require("dojo.widget.validate");

/*
todo:
- support incremental
*/
dojo.declare(
	"betterform.XFTime",
	dojo.widget.HtmlWidget,
	{
		widgetType: "XFTime",
        templatePath: dojo.uri.dojoUri('../betterform/widget/templates/HtmlTime.html'),
        templateCssPath:  dojo.uri.dojoUri("../betterform/widget/templates/HtmlTime.css"),

        // parameters
        id: "",
        name: "",
        value:"",
        timevalue:"00:00:00",  // new: 15.11.2006 J.Aerts
        hoursInputWidget:null,
        minutesInputWidget:null,
        secondsInputWidget:null,

        hoursInputNode:null,
        minutesInputNode:null,
        secondsInputNode:null,

        hoursNode:null,
        minutesNode:null,
        secondsNode:null,


        postMixInProperties: function(){
            console.warn("Time.js not implemented yet");
        },

        fillInTemplate: function(args, frag) {
            var hours = this.value.substring((this.value.indexOf("T")+1),(this.value.indexOf(":")));
            var minutes = this.value.substring((this.value.indexOf(":")+1),(this.value.indexOf(":")+3));
            var seconds = this.value.substring((this.value.indexOf(":")+4),(this.value.indexOf(":")+6));
            //console.debug("MyWidgetId: " + this.widgetId);
            //dojo.("Value: " + this.value);
            // console.debug("hours: " + hours);
            // console.debug("minutes: " + minutes);
		    // console.debug("seconds: " + seconds);
		this.timevalue = hours + ":" + minutes + ":" + seconds;  // new 15.11.2006 J.Aerts
			// hours
            this.hoursInputNode = document.createElement("span");
            this.hoursNode.appendChild(this.hoursInputNode);
            var datePropsHoursInput = {
                value:hours,
                delta:"01",
                min:"00",
                max:"23",
                seperator:"",
                maxlength:"2",
                widgetId:this.widgetId +"-hours"
            };
            this.hoursInputWidget = dojo.widget.createWidget("AdjustableIntegerTextBox", datePropsHoursInput, this.hoursInputNode);

            var hoursSpinnerNode = document.createElement("span");
            this.hoursInputNode.appendChild(hoursSpinnerNode);
            var dateProbsHoursSpinner = {inputWidgetId:this.widgetId+"-hours"};
            var hoursSpinnerWidget = dojo.widget.createWidget("Spinner", dateProbsHoursSpinner, hoursSpinnerNode);
			// minutes
            this.minutesInputNode = document.createElement("span");
            this.minutesNode.appendChild(this.minutesInputNode);
            var datePropsMinutesInput = {
                value:minutes,
                delta:"1",
                min:"00",
                max:"59",
                seperator:"!",
                maxlength:"2",
                widgetId:this.widgetId +"-minutes"
            };
            this.minutesInputWidget = dojo.widget.createWidget("AdjustableIntegerTextBox", datePropsMinutesInput, this.minutesInputNode);

            var minutesSpinnerNode = document.createElement("span");
            this.minutesInputNode.appendChild(minutesSpinnerNode);
            var dateProbsMinutesSpinner = {inputWidgetId:this.widgetId+"-minutes"};
            var minutesSpinnerWidget = dojo.widget.createWidget("Spinner", dateProbsMinutesSpinner, minutesSpinnerNode);
            // seconds
            this.secondsInputNode = document.createElement("span");
			this.secondsNode.appendChild(this.secondsInputNode);
			var datePropsSecondsInput = {
                value:seconds,
                delta:"1",
                min:"00",
                max:"59",
                seperator:"!",
                maxlength:"2",
                widgetId:this.widgetId +"-seconds"
            };
			this.secondsInputWidget = dojo.widget.createWidget("AdjustableIntegerTextBox", datePropsSecondsInput, this.secondsInputNode);
			var secondsSpinnerNode = document.createElement("span");
			this.secondsInputNode.appendChild(secondsSpinnerNode);
			var dateProbsSecondsSpinner = {inputWidgetId:this.widgetId+"-seconds"};
			var secondsSpinnerWidget = dojo.widget.createWidget("Spinner", dateProbsSecondsSpinner, secondsSpinnerNode);
            // events
            dojo.connect(this.hoursNode, "onclick", this, "onSetTime");
			dojo.connect(this.hoursNode, "onchange", this, "onSetTime");
			dojo.connect(this.hoursNode, "onblur", this, "onSetTime");
			dojo.connect(this.hoursNode, "onmouseout", this, "onSetTime"); // as onblur does not seem to work !
			dojo.connect(this.minutesNode, "onclick", this, "onSetTime");
			dojo.connect(this.minutesNode, "onchange", this, "onSetTime");
			dojo.connect(this.minutesNode, "onblur", this, "onSetTime");
			dojo.connect(this.minutesNode, "onmouseout", this, "onSetTime"); // as onblur does not seem to work !
			dojo.connect(this.secondsNode, "onclick", this, "onSetTime");
			dojo.connect(this.secondsNode, "onchange", this, "onSetTime");
			dojo.connect(this.secondsNode, "onblur", this, "onSetTime");
			dojo.connect(this.secondsNode, "onmouseout", this, "onSetTime"); // as onblur does not seem to work !

        },
        onSetTime: function() {
        	var t1 = dijit.byId(this.widgetId +"-hours");
		//console.debug("Time.js: hours = " + t1.getValue());
		var t2 = dijit.byId(this.widgetId +"-minutes");
		//console.debug("minutes = " + t2.getValue());
		var t3 = dijit.byId(this.widgetId + "-seconds");
		//console.debug("seconds = " + t3.getValue());
		var hours = t1.getValue();
		if(hours.length == 1) hours = "0" + hours;
		var minutes = t2.getValue();
		if(minutes.length == 1) minutes = "0" + minutes;
		var seconds = t3.getValue();
		if(seconds.length == 1) seconds = "0" + seconds;
		//var time = t1.getValue() + ":" + t2.getValue() + ":" + t3.getValue();
		var time = hours + ":" + minutes + ":" + seconds;
            this.timevalue = time;  // new 15.11.2006 J.Aerts
		//console.debug("time = " + time);
		DWREngine.setOrdered(true);
		DWREngine.setErrorHandler(handleExceptions);
		var sessionKey = document.getElementById("bfSessionKey").value;
		Flux.setXFormsValue(updateUI, this.widgetId.substring(0,this.widgetId.length - 6), time, sessionKey);
        }
    },



    function onclick() {
            // console.debug("Time.js OnClick()");
        },

    function onInputChange() {
            // console.debug("Time.js OnInputChange()");
        },

    function onChange() {
            // console.debug("Time.js onChange()");
        }

);

