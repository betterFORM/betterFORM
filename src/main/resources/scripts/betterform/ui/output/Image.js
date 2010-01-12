dojo.provide("betterform.ui.output.Image");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("betterform.ui.ControlValue");


dojo.declare(
        "betterform.ui.output.Image",
        betterform.ui.ControlValue,
    {
        src:"",
        alt:"",
        templateString:"<img src=\"${src}\" alt=\"${alt}\" class=\"xfValue\"></img>",


    postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },

    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function(){
        this.inherited(arguments);
        this.handleOnBlur();
    },

    getControlValue:function(){
        return dojo.attr(this.domNode,"src");
    },

    _handleSetControlValue:function(value) {
        // console.debug("betterform.ui.output.Image._handleSetControlValue value",value, " this:",this);
        dojo.attr(this.domNode, "src", value);
    },
    applyState:function(){
        // console.debug("betterform.ui.output.Image.applyState this",this);
        /* overwritten with no content because outputs are allways readonly */
    }
});
