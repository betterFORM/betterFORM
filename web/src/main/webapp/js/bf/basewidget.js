define(['require', 'jquery', 'jquery-ui','bf/XFControl', 'XFormsProcessor'], function(require, $) {
	$.widget("bf.bfbasewidget", {

		options: {
			uiWidgetName: "",
			uiWidgetFile: "",
		},
		
		// Set up the widget
		_create: function() {
			
			// How do I get the require working here.... the 'uiWidgetFile' is already loaded now,
			// but do not expect this in the future when real dynamic loading takes place.
			//$this = this;
			//require([$this.options.uiWidgetFile], function() { 
					this.element[this.options.uiWidgetName]();
			//});
			//this.options.id = getXfId(this.element);
			this.xfControl = $(this.element).closest(".xfControl");
			this._setValid(this.xfControl.bfcontrol("isValid"));
			this._setReadonly(this.xfControl.bfcontrol("isReadonly"));
			this._setRequired(this.xfControl.bfcontrol("isRequired"));
			this._setRelevant(this.xfControl.bfcontrol("isRelevant"));
            
            this._bindEvents();
            this._setCurrentValue();
            
            this.xfControl.bfcontrol("setWidget", this);
            
		},
		
		_bindEvents: function () {
			console.log("_bindEvents needs to be implemented by the implementing widget: " +this.widgetName);
		},
		
		_unbindEvents: function () {
			console.log("_unbindEvents needs to be implemented by the implementing widget: " +this.widgetName);
		}, 
		
		// Use the _setOption method to respond to changes to options
		// Still needed? Don think so...
		_setOption: function(key, value) {
			switch (key) {
				case "valid":
					this._setValid(value);
					break;
				case "value":
					this._setValue(value);
					break;
				case "readonly":
					this._setReadonly(value);
					break;
				case "required":
					this._setRequired(value);
				case "relevant":
					this._setRelevant(value);
				break;
			}
			this._super("_setOption", key, value);
		},
		
		
		_setValue: function(newValue) {
			console.error("_setValue needs to be implemented by widget implementation: " + this.widgetName);
		},

		//Override in implementation widget if specific behaviour is needed 
		_setValid: function(valid) {
			console.debug("_setValid called on " + this.widgetName + " for " + this.xfControl.bfcontrol("getId"));
			if (valid  == true || valid == 'true') {
				this.element[this.options.uiWidgetName]("valid");
			} else if (valid  == false || valid == 'false') {
				this.element[this.options.uiWidgetName]("invalid");
			} else {
				console.error("_setValid called with invalid value (" + valid + ")" + this.widgetName + " for " + this.xfControl.bfcontrol("getId"));
			}
		},

		_setReadonly: function(value) {
			console.debug("_setReadonly called on " + this.widgetName + " for " + this.xfControl.bfcontrol("getId"));
			if (value  == true || value == 'true') {
				this._unbindEvents();
				this.element[this.options.uiWidgetName]("disable");
			} else if (value  == false || value == 'false') {
				this._bindEvents();
				this.element[this.options.uiWidgetName]("enable");
			} else {
				console.error("_setReadonly called with invalid value (" + valid + ")" + this.widgetName + " for " + this.xfControl.bfcontrol("getId"));
			}
		},
		
		_setRequired: function(value) {
			//console.error("_setRequired needs to be implemented by widget implementation: " + this.widgetName);
		},
		
		_setRelevant: function(value) {
			//console.error("_setRelevant can be implemented by widget implementation: " + this.widgetName);
		},
		
		_destroy: function () {
			
		}
		
	});
});