define(['jquery', 'jquery-ui'], function($) {
    $.widget("primeui.puiinputtext", {
      	
        _create: function() {
            var input = this.element,
            disabled = input.prop('disabled');

            //visuals
            input.addClass('pui-inputtext ui-widget ui-state-default ui-corner-all');
           
            if(disabled) {
                input.addClass('ui-state-disabled');
            }
            else {
                this._bindEvents();
            }

            //aria
            input.attr('role', 'textbox').attr('aria-disabled', disabled)
                                          .attr('aria-readonly', input.prop('readonly'))
                                          .attr('aria-multiline', input.is('textarea'));
        },
        
        _bindEvents: function() {
        	this._unbindEvents(); // to make sure no duplicate events are registered...
        	var $this = this;
        	this.element.hover(function() {
                $this.element.toggleClass('ui-state-hover');
            }).focus(function() {
                $this.element.addClass('ui-state-focus');
            }).blur(function() {
                $this.element.removeClass('ui-state-focus');
            });
        },
        
        _unbindEvents: function() {
        	this.element.off('hover focus blur');
        },
        
        disable: function() {
            this._unbindEvents();
            this.element.attr('readonly', 'readonly');
            this.element.addClass('ui-state-disabled');
        },
       
        enable: function() {
            this._bindEvents();
            this.element.removeAttr('readonly');
            this.element.removeClass('ui-state-disabled');
        },
        
        valid: function() {
        	this.element.removeClass('ui-state-error');
        },
        
        invalid: function() {
        	this.element.addClass('ui-state-error');
        },
        
        _destroy: function() {
           
        }
       
    });
   
});
