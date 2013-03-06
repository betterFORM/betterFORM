/**
 * MappingProcessor generates dojo.behaviors of the CSS matchers defined in
 * Mapping.js. Further it calls the the Factory.create function of a given
 * factory and passes in the matched node and the 3rd param of the mapping (a
 * string telling the factory which Widget to create)
 */
var MappingProcessor = function(){};

MappingProcessor.prototype = {
            
    apply:function() {
    	//console.debug("Mappings: ", mappings);
        // require(["jquery", "bf/XFControl","bf/Mapping"],
            // function($){
        $.each(mappings, function(index, mapping){

        	try {
        		$(mapping.selector)[mapping.xfWidgetName]({uiWidgetName: mapping.uiWidgetName, uiWidgetFile: mapping.uiWidgetFile});
        	} catch (ex) {
        		console.error("Could not create widget(s) for mapping : ",mapping, ex.message);
        	}
        });

    }
}


