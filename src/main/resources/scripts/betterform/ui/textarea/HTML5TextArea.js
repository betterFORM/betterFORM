/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.textarea.HTML5TextArea");

dojo.require("betterform.ui.ControlValue");

dojo.declare(
        "betterform.ui.textarea.HTML5TextArea",
        betterform.ui.ControlValue,
{
    rows:5,
    cols:40,
    templatePath: dojo.moduleUrl("betterform", "ui/templates/HTML5TextArea.html"),
    templateString: null,

   postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },

    postCreate:function() {
        this.inherited(arguments);
        this.inputNode.innerHTML = this.srcNodeRef.innerHTML;
        dojo.connect(this.domNode,"onkeypress", this,"_valueChanged");
/*
        if (localStorage.getItem(this.id)) {
          editable.innerHTML = localStorage.getItem(this.id);
        }
*/

    },

    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
        document.designMode = 'on';

    },

    _onBlur:function(){
        this.inherited(arguments);
        this.handleOnBlur();
        /*localStorage.setItem(this.id, this.inputNode.innerHTML);*/
        document.designMode = 'off';

    },

    getControlValue:function(){
        return this.inputNode.value;
    },

    _valueChanged: function(evt){
        if(this.incremental){
            this.setControlValue();
        }
    },
    

    applyState:function() {
        // console.debug("betterform.ui.textarea.HTML5TextArea.applyState",this);
        if (this.xfControl.isReadonly()) {
            dojo.attr(this.inputNode,"disabled","disabled");
        } else if(dojo.hasAttr(this.inputNode,"readonly")) {
            dojo.removeAttr(this.inputNode,"disabled");
        }else if(dojo.hasAttr(this.inputNode,"disabled")) {
            dojo.removeAttr(this.inputNode,"disabled");
        }
    },

    _handleSetControlValue:function(value) {
        // console.debug("betterform.ui.textarea.HTML5TextArea._handleSetControlValue: Value: ", value);
        this._setValueAttr(value);
    }
});
