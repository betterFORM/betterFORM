dojo.provide("betterform.ui.select1.Plain");


dojo.require("betterform.ui.ControlValue");
dojo.require("dojox.form.DropDownSelect");


dojo.declare(
        "betterform.ui.select1.Plain",
        betterform.ui.ControlValue,
{

    buildRendering:function() {
        //console.debug("betterform.ui.select1.Plain.buildRendering: Create compact Select1");
        this.domNode = this.srcNodeRef;
    },

      postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
          if(dojo.attr(this.srcNodeRef, "incremental") == undefined || dojo.attr(this.srcNodeRef, "incremental") == "" || dojo.attr(this.srcNodeRef, "incremental") == "true"){
              this.incremental = true;
          }else {
              this.incremental = false;
          }
          
    },

    postCreate:function() {
        dojo.connect(this.domNode,"onchange", this,"_onChange");
        this.setCurrentValue();
        //console.debug("betterform.ui.select1.Plain.postCreate: this.currentValue:",this.currentValue);
    },

    _onFocus:function() {
        this.inherited(arguments);
        this.handleOnFocus();
    },

    _onBlur:function(){
        this.inherited(arguments);
        this.handleOnBlur();
    },

    _onChange:function() {
        var selectedNode = this.domNode.options[this.domNode.selectedIndex];
        fluxProcessor.dispatchEventType(this.xfControl.id, "DOMActivate", dojo.attr(selectedNode,"id"));

        if(this.incremental){
            this.setControlValue();
        }
    },

    getControlValue:function(){
       var value
        if(this.domNode.selectedIndex == undefined || this.domNode.selectedIndex == -1){
            return "";
        }
        var value = this.domNode.options[this.domNode.selectedIndex].value;
        // console.debug("betterform.ui.select1.Plain value: ", value);
        if(value != undefined) {
            return value;
        }else {
            return "";
        }
    },


    displayValidity:function(/*Boolean*/ valid) {
        if (valid) {
            dojo.removeClass(this.domNode, "caDisplayInvalid");
            this.hideAlert();
        } else {
            dojo.addClass(this.domNode, "caDisplayInvalid");
            if(this.focused || !this.hideAlertOnFocus){
                this.showAlert();
            }
        }
    },

    applyState:function(){
        if(this.xfControl.isReadonly()){
            dojo.attr(this.domNode,"disabled","disabled");
        }else{
             this.domNode.removeAttribute("disabled");
        }
    },

    _handleSetControlValue:function(value){
        for(i =0;i<this.domNode.options.length;i++){
            if(this.domNode.options[i].value == value){
                this.domNode.selectedIndex = i;
            }
        }
    }
});



