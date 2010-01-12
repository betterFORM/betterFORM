dojo.provide("betterform.ui.select.CheckBox");

dojo.require("dijit.form.FilteringSelect");
dojo.require("betterform.ui.ControlValue");
dojo.require("dijit.form.CheckBox");


dojo.declare(
        "betterform.ui.select.CheckBox",
        [betterform.ui.ControlValue,dijit.form.CheckBox],
{
        selectWidgetId:"",
        value:"",
        selectWidget:null,
        templatePath: dojo.moduleUrl("betterform", "ui/templates/CheckBox.html"),


    postMixInProperties:function() {
        // console.debug("CheckBox.postMixInProperties");
        this.inherited(arguments);
        // console.debug("CheckBox srcNodeRef this.selectWidgetId:",this.selectWidgetId, " this.srcNodeRef:",this.srcNodeRef);
        this.selectWidget = dijit.byId(this.selectWidgetId);
        // console.debug("CheckBox srcNodeRef",this.srcNodeRef, " selectWidget:",this.selectWidget);
        if(this.srcNodeRef != undefined) {
            this.currentValue = dojo.attr(this.srcNodeRef,"value");
            // console.debug("CheckBox srcNodeRef",this.srcNodeRef, " currentValue:",this.currentValue);
        }

        
    },

    onClick: function(/*Event*/ evt){
        // console.debug("betterform.ui.select.CheckBox.onClick");
        this.inherited(arguments);
        this.selectWidget._setCheckBoxGroupValue();
    },

    getControlValue:function() {
        // console.debug("betterform.ui.select.CheckBox.getControlValue");
        // var value = this._getValueAttr();
        // console.debug("CheckBox.getControlValue: value: ",value);
        return this.currentValue;
    }

});


