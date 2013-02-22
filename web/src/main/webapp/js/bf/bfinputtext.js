define(['jquery', 'jquery-ui','pui/inputtext', 'XFormsProcessor'], function($) {
	$.widget("bf.bfinputtext", $.primeui.puiinputtext, {
		// These options will be used as defaults
		options : {
			valid : true,
			id : null
		},
		
//		_handlers: {
//	         validHandler: function(e) { 
//	             //do something
//	             this.method();
//	         }
//		},
		
		// Set up the widget
		_create : function() {
			this._super("_create");

			this.options.id = getXfId(this.element);
			
			valid = this.element.attr('valid');
            if(!(valid === undefined) && valid == 'false') {
            	this.element.addClass('ui-state-error');
            }
            
            var input = this.element;
            

            
            
           	input.bind('bf-state-change', $.proxy(this.setValid, this));
           	           
            //$(document).bind('bf-state-change-'+this.options.id, $.proxy(this._handlers.validHandler, this));

            input.change($.proxy(function () {
            		this.setValue(this.element.val());
            }, this));

		},

		// Use the _setOption method to respond to changes to options
		_setOption : function(key, value) {
			switch (key) {
			case "valid":
				this.setValid(value);
				break;
			case "value":
				this.setValue(value);
				break;
			}

			this._super("_setOption", key, value);
		},
		
		bfStateChange : function(contextInfo) {
			alert(contextInfo + " " + this.options);
		},
		
		_setValue : function(newvalue) {
			if (this.options.value != newvalue) {
				xformsprocessor.setControlValue(this.options.id, newvalue);
			}
			this.options.value = newvalue;
			this.element.attr('value', newvalue);
		},
		
		_setValid : function(valid) {
			this.options.valid = valid;
			if(this.options.valid == 'false') {
                this.element.addClass('ui-state-error');
            } else {
            	this.element.removeClass('ui-state-error');
            }
		},

		_destroy : function() {

		}

	});
	
	$('#container')
	  .on('click', function(e) {
	      $('#input1-value').trigger('bf-state-change');
	   });

	$('#container2')
	  .on('click', function(e) {
	      $(document).trigger('bf-state-change-input1-value');
	   });

	
});
