dojo.provide("betterform.ui.IFramePane");
/*
	All Rights Reserved.


*/

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dijit.layout.ContentPane");

dojo.declare(
        "betterform.ui.IFramePane",
        dijit.layout.ContentPane,
{
    _loadCheck: function(forceLoad){
        if(this.domNode.style.display != 'none' && this.isLoaded != true){
//            dojo.addClass(this.domNode,"loading");
            dojo.connect(this.domNode, "onload",this,"hideProgressIndicator");
            this.domNode.src = this.href;
            this.isLoaded = true;
        }
    },

    hideProgressIndicator: function(){
        this.domNode.style.background="white";
    }

});


