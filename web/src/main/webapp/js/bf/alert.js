define(['jquery', 'jquery-ui'], function($) {
	$.widget("bf.bfalert", {
		// These options will be used as defaults
		options: {
			uiWidgetName: "puigrowl",
			uiWidgetFile: "pui/growl",
		},
		
		_create: function() {
			var _self = this
			this.growl = $('<span/>').appendTo(document.body);
			this.growl[this.options.uiWidgetName]();
			$.subscribe("xforms-valid", function(id, event) {
				//console.debug("Valid event: ", id, event);
			})
			$.subscribe("xforms-invalid", function(id, event) {
				console.debug("'xforms-invalid event: ",id, event);
				if (!(event == 'init' || event == 'onBlur')) {
					_self.growl[_self.options.uiWidgetName]('show', [{severity: 'error', summary: $('#'+id + ' xf\\\:label').html(), detail: $('#'+id + ' xf\\\:alert').html()}]);
				}
			})
		},
		
		_destroy: function() {

		}

	});
});