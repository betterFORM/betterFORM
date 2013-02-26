define(['jquery', 'jquery-ui','bf/XFControl', 'bf/basewidget', 'XFormsProcessor'], function($) {
	$.widget("bf.bfcheckbox", $.bf.bfbasewidget, {
		// These options will be used as defaults
		options: {
			uiWidgetName: "puicheckbox",
			uiWidgetFile: "pui/checkbox",
		},
		
		_create: function() {
			this._super();
		},
		
		_setCurrentValue: function () {
			this.xfControl.bfcontrol("setCurrentValue", this.element.prop('checked'));
		},
		
		_bindEvents: function() {
	            
            var $this = this;
            
            this.element.on("change.bfcheckbox", function () {
    			if($this.xfControl.bfcontrol("isIncremental")){
    				$this.xfControl.bfcontrol("sendValue", $this.element.prop("checked"), false);
    			}
            })
            .on("blur.bfcheckbox",function () {
            	$this.xfControl.bfcontrol("sendValue", $this.element.prop("checked"), true);	
            })
            .on("focus.bfcheckbox",function () {
            	$this.xfControl.bfcontrol("handleOnFocus");  
            });
		 
		 },
		 
		 

		_setValue: function(newValue) {
			console.debug("_setValue called on " + this.widgetName + " for " + this.xfControl.bfcontrol("getId")+ " with value " +newValue);
			if(!newValue === undefined) {
				if (newValue  == true || newValue == 'true') {
					this.element.puicheckbox("check");
				} else if (newValue  == false || newValue == 'false') {
					this.element.puicheckbox("uncheck");
				} else {
					console.error("_setValue called with invalid value (" + newValue + ") " + this.widgetName + " for " + this.xfControl.bfcontrol("getId"));
				}
				
			}
		},
			
		_destroy: function() {

		}

	});
});
