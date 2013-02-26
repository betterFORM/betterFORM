define(['jquery', 'jquery-ui','bf/XFControl', 'bf/basewidget', 'XFormsProcessor'], function($) {
	$.widget("bf.bfinputtext", $.bf.bfbasewidget, {
		
		options: {
			uiWidgetName: "puiinputtext",
			uiWidgetFile: "pui/inputtext",
		},
		
		_create: function() {
			
			this._super();
			
			// needed do 'this' component can be used in another method...
			// It can also be used when registering eventhandlers to prevent the need for the 'proxy' construction
			// Use a standard for this? _self, $this, _this??? 
			$this = this;
			
			// This is just to see if and how overriding in another widget works...
			// Maybe storing $(this.xfControl).data("bfBfcontrol") somewhere??? does that work?
			// bf.bfcontrol becomes bfBcontrol.. 
			$(this.xfControl).data("bfBfcontrol").setValue = function(value,schemavalue) {
                $this.element.attr('value', value);
                $this.element.val(value);
            };
            
		},
		
        _bindEvents: function() {
        	this._unbindEvents;
            //var $this = this;
            $this = this;
            
            this.element.on("keyup.bfinputtext", function () {
    			if($this.xfControl.bfcontrol("isIncremental")){
    				$this.xfControl.bfcontrol("sendValue", this.element.val(), false);
    			}
            })
            .on("blur.bfinputtext", function () {
            		$this.xfControl.bfcontrol("sendValue", $this.element.val(), true);	
            })
            .on("focus.bfinputext",function () {
            	$this.xfControl.bfcontrol("handleOnFocus");
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
