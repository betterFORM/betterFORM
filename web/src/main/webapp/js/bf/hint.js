define(['jquery', 'jquery-ui','bf/XFControl', 'bf/basewidget', 'XFormsProcessor'], function($) {
	$.widget("bf.bfhint", {
		// These options will be used as defaults
		options: {
			uiWidgetName: "puitooltip",
			uiWidgetFile: "pui/tooltip",
		},
		
		_create: function() {
			$('input', this.element.parent())[this.options.uiWidgetName]({content: this.element.html()});
		},
		
		_destroy: function() {

		}

	});
});
