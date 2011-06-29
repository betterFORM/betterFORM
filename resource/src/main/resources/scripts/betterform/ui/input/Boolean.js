/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.input.Boolean");

dojo.require("dijit.form.CheckBox");

dojo.declare(
        "betterform.ui.input.Boolean",
        [betterform.ui.ControlValue,dijit.form.CheckBox],
{
    postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },

    postCreate:function() {
        this.inherited(arguments);
        this.setCurrentValue();
    },

    onClick: function() {
        this.inherited(arguments);
        this.setControlValue();
    },

    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function(){
        this.inherited(arguments);
        this.handleOnBlur();
    },

    

    _handleSetControlValue:function(checked) {
//        console.debug("betterform.ui.input.Boolean._handleSetControlValue() : value: ", checked);
        if(checked == 'true' || checked == 'false') {
            this.set('checked', eval(checked));
        }
    },

    getControlValue:function() {
        //console.debug("betterform.ui.input.Boolean.getControlValue for Control "+ this.id +": ",this.attr('checked') );
        var checkedValue = this.get('checked');
        //console.debug("betterform.ui.input.Boolean.getControlValue: ",checkedValue );
         var checked;

        if(checkedValue == 'true' || checkedValue == true) {
            //console.debug("betterform.ui.input.Boolean.getControlValue: checked", true);
            checked = true;
        } else if ( checkedValue == 'false' || checkedValue == false) {
            //console.debug("betterform.ui.input.Boolean.getControlValue: checked", false);
            checked = false;
        }


        if(checked == undefined) {
            var value = this.get('value');
            //console.debug("betterform.ui.input.Boolean value: ",value );
             if(value == 'true' || value == true) {
            //console.debug("betterform.ui.input.Boolean.getControlValue: checked", true);
            checked = true;
            } else if ( value == 'false' || value == false) {
            //console.debug("betterform.ui.input.Boolean.getControlValue: checked", false);
            checked = false;
            }
        }

        if(checked != undefined && checked) {
            return true;
        }else {
            return false;
        }
    },

    /* function needed by InlineEditBox */
    setTextValue:function(/* String */ checked) {
        this.xfControl.setControlValue(this.checked);

    },

    /* function needed by InlineEditBox */
    getTextValue:function() {
      return this.checked;

    }
});


