/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.trigger.LinkButton");



dojo.declare(
        "betterform.ui.trigger.LinkButton",
        betterform.ui.ControlValue,
{
    label:"",
    templateString: dojo.cache("betterform", "ui/templates/HtmlLinkButton.html"),

    postMixInProperties:function() {
        this.inherited(arguments);
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
        this.label = dojo.attr(this.srcNodeRef,"label");
    },
    
    postCreate:function() {
        this.linknode.innerHTML=this.label;
    },

    onClick:function(e){
        if(!(dojo.attr(this.domNode,"disabled")=="disabled")){
            //console.debug("Button " + this.xfControlId + " clicked! Event: ", e);
            e.cancelBubble = true;
            //console.debug("Canceld Bubble");
            fluxProcessor.dispatchEvent(this.xfControlId);

        }
    },

    getControlValue:function(){
        console.warn("TBD: betterform.ui.trigger.Button.getControlValue");
        return dojo.attr(this.domNode,"value");
    },

    _handleSetControlValue:function(value) {
        console.warn("TBD: betterform.ui.trigger.Button._handleSetControlValue: Value: ", value);
    },
    _setLabel:function(value) {
        // console.debug("LinkButton._setLabel value:",value, " domNode: ",this.domNode);
        dojo.query("a",this.domNode)[0].innerHTML = value;        
    }
});
