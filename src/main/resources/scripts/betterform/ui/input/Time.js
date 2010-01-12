dojo.provide("betterform.ui.input.Time");

dojo.require("betterform.ui.ControlValue");


dojo.declare(
        "betterform.ui.input.Time",
        betterform.ui.ControlValue,
{
    postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },
    postCreate:function() {
        this.inherited(arguments);
        this.setCurrentValue();
    },

    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function(){
        this.inherited(arguments);
        if (!this.incremental) {
        this.handleOnBlur();
        }
    },

    getControlValue:function(){
        return null;
    },

    _handleSetControlValue:function() {
        console.warn("TBD: HandleState changed for DateTime");
    }
});



