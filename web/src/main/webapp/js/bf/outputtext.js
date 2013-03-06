define(['jquery', 'jquery-ui','bf/XFControl', 'bf/basewidget', 'XFormsProcessor'], function($) {
	$.widget("bf.bfoutputtext", $.bf.bfbasewidget, {
		
		options: {
			uiWidgetName: "",
			uiWidgetFile: "",
		},
		
		_create: function() {
			
			this._super();
			
			var _self = this;
		
			this._setCurrentValue();
			$(this.xfControl).data("bfBfcontrol").setValue = function(value,schemavalue) {
                _self.element.html(value);
            };
            
		},
		
        _bindEvents: function() {
            	
		},
		
		_unbindEvents: function() {
					
		},

		_setCurrentValue: function () {
			this.xfControl.bfcontrol("setCurrentValue", this.element.html());
		},
		
		_setValue: function(newValue) {
			if(!newValue === undefined) {
				this.element.html(newValue);
			}
		},
		
		_destroy: function() {
			this._super();
		}

	});
});
