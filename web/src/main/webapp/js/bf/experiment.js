(function ($) {

$.widget("bf.baseWidget", {
    
    options: {
        bfBaseOption: "bfBaseOptionValue",
    },
  _create: function(){
      var _self=this;
      this.element.focus(function (event) {
          console.log("focussed from: " +_self.widgetName);
      });
  },
   _destroy: function() {
      }
  });
    
$.widget("pui.puiinputtext", $.bf.baseWidget, {
    
    options: {
        puiInputtextOption: "puiInputtextOptionValue",
    },
    
  _create: function(){
      this._super();
      var _self = this;
      this.element.focus(function (event) {
          console.log("focussed from: " + _self.widgetName);
      });
  },
   _destroy: function() {
      }
  });    

$.widget("bf.bfinputtext", $.pui.puiinputtext, {
    
    options: {
        bfInputtextOption: "bfInputtextOptionValue",
    },
  _create: function(){
      this._super();
      var _self = this;
      this.element.focus(function () {
          console.log('bfInputtextWidget: ');
          $.each(_self.options, function(index, option) {console.log(index + ": " + option);} );
      });
          
  },
   _destroy: function() {
   
   }
  }); 

$('#one').bfinputtext();

    
}(jQuery));