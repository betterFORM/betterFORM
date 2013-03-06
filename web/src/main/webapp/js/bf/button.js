define(['jquery', 'jquery-ui','bf/XFControl', 'bf/basewidget', 'XFormsProcessor'], function($) {
	$.widget("bf.bfbutton", $.bf.bfbasewidget, {
		// These options will be used as defaults
		options: {
			uiWidgetName: "puibutton",
			uiWidgetFile: "pui/button",
		},
		
		_create: function() {
			this._super();
		},
		
		_setCurrentValue: function () {
			this.xfControl.bfcontrol("setCurrentValue", this.element.prop('checked'));
		},
		
		_bindEvents: function() {
            
			this._unbindEvents();
            var _self = this;
            
            this.element.on('click.bfbutton', function(evt){
            	// Send to xfcontrol??? Not directly
                xformsprocessor.dispatchEvent(_self.xfControl.bfcontrol("getId"));
            });
		 
		 },
		 
		 _unbindEvents: function() {
			 this.element.off('click.bfbutton');
		 },

		_setValue: function(newValue) {
			// console.debug("_setValue called on " + this.widgetName + " for " + this.xfControl.bfcontrol("getId"));
		},

		_destroy: function() {

		}

	});
});
