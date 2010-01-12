dojo.provide("betterform.ui.output.Html");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("betterform.ui.ControlValue");


dojo.declare(
        "betterform.ui.output.Html",
        betterform.ui.ControlValue,
    {
        templateString:"<span id=\"${id}\" dojoAttachPoint=\"containerNode\"></span>",

        postMixInProperties:function() {
            this.inherited(arguments);
            this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
        },

        postCreate:function() {
            this.containerNode.innerHTML = this.value;
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
            return this.containerNode.innerHTML;
        },

        applyState:function(){
            /* overwritten with no content because outputs are allways readonly */
        },

        _handleSetControlValue:function(value) {
            // console.debug("betterform.ui.output.Html._handleSetControlValue value:",value);
            this.containerNode.innerHTML = value;
        }
    }
);