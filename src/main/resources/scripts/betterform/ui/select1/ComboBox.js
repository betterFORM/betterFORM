dojo.provide("betterform.ui.select1.ComboBox");


dojo.require("betterform.ui.ControlValue");


dojo.declare(
        "betterform.ui.select1.ComboBox",
        betterform.ui.ControlValue,
{


    buildRendering:function() {
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
        // console.debug("ComboBox.postCreate");
        dojo.connect(this.domNode,"onchange", this,"_onChange");
        this.setCurrentValue();
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

    getControlValue:function() {
        if(this.domNode.selectedIndex != -1 &&  this.domNode.options != undefined) {
        var option = this.domNode.options[this.domNode.selectedIndex];
        return dojo.attr(option,"value");
        }else {
            return null;
        }
    },


    displayValidity:function(/*Boolean*/ valid) {
        // console.debug("ComboBox.displayValidity, this", this);
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
        // console.debug("ComboBox._handleSetControlValue value: ",value);
        for(i =0;i<this.domNode.options.length;i++){
            if(this.domNode.options[i].value == value){
                this.domNode.selectedIndex = i;
            }
        }
    }
});



