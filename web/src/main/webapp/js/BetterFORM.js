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
      
      //"subtopic" : "https://raw.github.com/pmelander/Subtopic/master/jquery-subtopic"
      "subtopic" : "libs/jquery-subtopic"

  },

  shim: {

    "domReady" : ["jquery-ui"],
    "jquery-ui": ["jquery"],
  	"subtopic" : ["jquery"],
    "XFormsProcessor" : ["bf/XFControl"],
    "pui/basewidget"  : ["jquery-ui"], 
//    "pui/inputtext"   : ["pui/basewidget"],
//    "pui/checkbox"    : ["pui/basewidget"],
  	"bf/inputtext"  : ["pui/inputtext"],
	"bf/checkbox"   : ["pui/checkbox"],
	"bf/button"   : ["pui/button"]
  }

});

require(["domReady!", "jquery", "jquery-ui", "subtopic", "XFormsProcessor", "bf/XFControl", "pui/inputtext", "pui/checkbox", "pui/button", "bf/inputtext", "bf/checkbox", "bf/button"], function(doc, $) {
    $(function() {
    	
    	xformsprocessor = new XFormsProcessor();
    	xformsprocessor.init();
    	
    	getXfId = function(/*Node*/n){
            var tmp = $(n).attr('id').substring(0,$(n).attr('id').lastIndexOf("-"));
            // console.debug("returning xfId: ",tmp);
            return tmp;
        };
        
        setDefaultClasses = function (element) {
            if(!$(element).hasClass("xfEnabled") && !$(element).hasClass("xfDisabled")){
                domClass.add(element,"xfEnabled");
            }
            if(!$(element).hasClass("xfOptional") && !$(element).hasClass("xfRequired")){
                domClass.add(element,"xfOptional");
            }
            if(!$(element).hasClass("xfReadWrite") && !$(element).hasClass("xfReadOnly")){
                domClass.add(element,"xfReadWrite");
            }
            if(!$(element).hasClass("xfValid") && !$(element).hasClass("xfInvalid")){
                domClass.add(element,"xfValid");
            }
        };
        
    });
});