/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.container.Dialog");

dojo.require("dijit._Widget");
dojo.require("betterform.ui.container.Container");
dojo.require("dijit.Dialog");


dojo.declare(
        "betterform.ui.container.Dialog",
        [betterform.ui.container.Container, dijit.Dialog],
{

   /* postCreate: function(){

	    var djVersion = new dojo.version();
	    if (djVersion.minor = 3) {
                    dojo.style(this.domNode, {
                           visibility:"hidden",
                           position:"absolute",
                           display:"",
                           top:"-9999px"
                    });
	    } else {	
                    dojo.style(this.domNode, {
                           position:"absolute",
                           display:"none"
                    });
	    }	
            //dojo.body().appendChild(this.domNode);

	    //do not enable, otherwise the appendChild is executed anyway
            //this.inherited(arguments);

            this.connect(this, "onExecute", "hide");
            this.connect(this, "onCancel", "hide");
            this._modalconnects = [];
    },*/

    handleStateChanged:function(contextInfo){
        var relevant = eval(contextInfo["enabled"]);
        // console.debug("Dialog.handleStateChanged relevant:",relevant);
        if (relevant) {
            betterform.ui.util.replaceClass(this.domNode, "xfDisabled", "xfEnabled");
        }
        else {
            betterform.ui.util.replaceClass(this.domNode, "xfEnabled", "xfDisabled");
        }

     },

     handleShow:function(/*Map*/ contextInfo){
         // console.debug("handleShow contextInfo: ",contextInfo);
     },
        
     handleHide:function(/*Map*/ contextInfo){
         // console.debug("handleHide contextInfo: ",contextInfo);
     }

}
);


