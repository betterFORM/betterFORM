define(['jquery', 'jquery-ui','bf/XFControl', 'bf/basewidget', 'XFormsProcessor'], function($) {
	$.widget("bf.bfcheckbox", $.bf.bfbasewidget, {
		// These options will be used as defaults
		options: {
			uiWidgetName: "",
			uiWidgetFile: "",
		},
		
		_create: function() {
			this._super();
			var _self = this;
			$(this.xfControl).data("bfBfcontrol").setValue = function(value,schemavalue) {
                _self._setValue(value);
            };
		},
		
		_setCurrentValue: function () {
			console.log("checkbox.setCurrentValue()");
			this.xfControl.bfcontrol("setCurrentValue", this.element.prop('checked'));
		},
		
		_bindEvents: function() {
	            
			this._unbindEvents();
            var _self = this;
            
            this.element.on("change.bfcheckbox", function (ev) {
            	
    			if(_self.xfControl.bfcontrol("isIncremental")){
    				
    				
    				_self.xfControl.bfcontrol("sendValue", _self.element.prop("checked"), false);
    			} else {
    				console.log("change.bfcheckbox NOT INCREMENTAL");
    			}
    			
            })
            //.on("blur.bfcheckbox",function () {
            .on("blur.bfcheckbox",function (ev) {
            	console.log("blur.bfcheckbox");
            	//The actual input is losing focus, wait a few milliseconds to see if it 'stays'
            	// within the control (e.g. a click on the custom ui part)
        		_self.checkingBlur = setTimeout(function () {
                    console.log("blur.bfcheckbox.timeout, self:", $(_self.xfControl).has(":focus").length > 0);
                    if (($(_self.xfControl).has(":focus").length == 0 )) {
                    	_self.xfControl.bfcontrol("sendValue", _self.element.prop("checked"), true);
                    } else {
                    	console.log("focus 'stayed' on the same input");
                    }
                }, 100);
	
            })
            .on("focus.bfcheckbox",function () {
            	console.log("handling On Focus in bfCheckbox");
            	if ($(_self.xfControl).has(":focus") == 0) {;
            		_self.xfControl.bfcontrol("handleOnFocus");
            	} else {
            		console.log("focus was already in the is control")
            	}
            });
		 },
		 
		 _unbindEvents: function() {
			 this.element.off("change.bfcheckbox blur.bfcheckbox focus.bfcheckbox");
		 },

		_setValue: function(newValue) {
			console.debug("_setValue called on " + this.widgetName + " for " + this.xfControl.bfcontrol("getId")+ " with value " +newValue);
			if(!newValue === undefined) {
				if (newValue  == true || newValue == 'true') {
					this.element.puicheckbox("check", false, true);
				} else if (newValue  == false || newValue == 'false') {
					this.element.puicheckbox("uncheck", false, true);
				} else {
					console.error("_setValue called with invalid value (" + newValue + ") " + this.widgetName + " for " + this.xfControl.bfcontrol("getId"));
				}
				
			}
		},
			
		_destroy: function() {

		}

	});
});
