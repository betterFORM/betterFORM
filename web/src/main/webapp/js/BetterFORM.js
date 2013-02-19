require.config({

  // Sets the js folder as the base directory for all future relative paths
  baseUrl: "./js",

  // 3rd party script alias names (Easier to type "jquery" than "libs/jquery, etc")
  paths: {

      // Core Libraries
      // --------------
      "jquery": "libs/jquery",
      //"jquery": "https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min",

      "jquery-ui": "libs/jquery-ui",
      //"jquery-ui": "https://ajax.googleapis.com/ajax/libs/jqueryui/1.9.2/jquery-ui.min",
      
      "domReady" : "libs/domReady",

  },

  shim: {

    "domReady" : ["jquery-ui"],
    "jquery-ui": ["jquery"],
    "bf/bfinputtext" : ["pui/inputtext"]

  }

});

require(["domReady!", "jquery", "jquery-ui", "XFormsProcessor", "bf/bfinputtext"], function(doc, $) {
    $(function() {
    	
    	xformsprocessor = new XFormsProcessor();
    	xformsprocessor.init();
    	
    	getXfId = function(/*Node*/n){
            var tmp = $(n).attr('id').substring(0,$(n).attr('id').lastIndexOf("-"));
            // console.debug("returning xfId: ",tmp);
            return tmp;
        };
    	
    	 	
        $('body').addClass('Ronald');
        
        $(function() {
            $('#in').bfinputtext();
        });
        $(function() {
            $('#error').bfinputtext();
        });
        $(function() {
            $('#disabled').bfinputtext();
        });
        
    });
});