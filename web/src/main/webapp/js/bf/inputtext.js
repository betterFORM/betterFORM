define(['jquery', 'jquery-ui','bf/XFControl', 'bf/basewidget', 'XFormsProcessor'], function($) {
	$.widget("bf.bfinputtext", $.bf.bfbasewidget, {
		
		options: {
			uiWidgetName: "",
			uiWidgetFile: "",
		},
		
		_create: function() {
			
			this._super();
			
			// needed do 'this' component can be used in another method...
			// It can also be used when registering eventhandlers to prevent the need for the 'proxy' construction
			// Use a standard for this? _self, $this, _this???
			// The construction with var _self seems shorter than using the 'proxy' construct, but if you forget 
			// to put the var in front, it becomes a GLOBAL variable, so always the LAST element processed will be in it!!!
			var _self = this;
			
			
			// This is just to see if and how overriding in another widget works...
			// Maybe storing $(this.xfControl).data("bfBfcontrol") somewhere??? does that work?
			// bf.bfcontrol becomes bfBcontrol.. 
			// This method is only needed if the value is 'pushed' from the server.
			// in the example input2 gets its value from input1 and this method is needed then
			

			
			$(this.xfControl).data("bfBfcontrol").setValue = function(value,schemavalue) {
                _self.element.attr('value', value);
                _self.element.val(value);
            };
            
		},
		
        _bindEvents: function() {
        	this._unbindEvents;
            //var $this = this;
            var _self = this;
            
            // Not sure why, but using $this instead of the $.proxy(...., this) construct fails 
            // when using xf:input instead of parsed html...
            // Look into it if needed... but for now... Success 
            this.element.on("keyup.bfinputtext", function () {
    			if(_self.xfControl.bfcontrol("isIncremental")){
    				_self.xfControl.bfcontrol("sendValue", _self.element.val(), false);
    			}
            })
            .on("blur.bfinputtext", function () {
            		_self.xfControl.bfcontrol("sendValue", _self.element.val(), true);	
            })
            .on("focus.bfinputext",function () {
            	_self.xfControl.bfcontrol("handleOnFocus");
            });
            	
		},
		
		_unbindEvents: function() {
			this.element.off("keyup.bfinputtext blur.bfinputtext focus.bfinputext");
			
		},

		_setCurrentValue: function () {
			this.xfControl.bfcontrol("setCurrentValue", this.element.attr('value'));
		},
		
		_setValue: function(newValue) {
			if(!newValue === undefined) {
				this.element.attr('value', newValue);
			}
		},
		
		_destroy: function() {
			this._super();
		}

	});
});
