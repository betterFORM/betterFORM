dojo.provide("betterform.ui.range.Rating");

dojo.require("dojox.form.Rating");
dojo.require("betterform.ui.ControlValue");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare(
        "betterform.ui.range.Rating",
        [betterform.ui.ControlValue,dojox.form.Rating],
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
   
    getControlValue:function() {
        return this.attr('value');
    },
    
    onStarClick:function(/* Event */evt){
        this.inherited(arguments);
        if(this.incremental){
            this.setControlValue();
        }
    },

    _handleSetControlValue:function(value) {
        // console.debug("betterform.ui.range.Rating._handleSetControlValue: Value: ", value);
        this.setAttribute("value",value);        
    }

});

