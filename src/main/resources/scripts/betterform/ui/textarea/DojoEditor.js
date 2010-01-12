dojo.provide("betterform.ui.textarea.DojoEditor");

dojo.require("betterform.ui.ControlValue");
dojo.require("dijit.form.Textarea");

dojo.declare(
        "betterform.ui.textarea.DojoEditor",
        [betterform.ui.ControlValue, dijit.form.Textarea],
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
        //this.handleOnFocus();
    },

    _onBlur:function(){
        this.inherited(arguments);
        this.handleOnBlur();
    },

    getControlValue:function(){
        return this._getValueAttr();
    },
    
    _onInput: function(){
        this.inherited(arguments);
        // console.debug("betterform.ui.textarea.DojoEditor._onInput");
        if(this.incremental){
            this.setControlValue();
        }
    },

    _handleSetControlValue:function(value) {
        // console.debug("betterform.ui.textarea.DojoEditor._handleSetControlValue: Value: ", value);
        this._setValueAttr(value);
    }
});


