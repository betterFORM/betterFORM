/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/_base/declare", "dijit/_Widget","dojo/dom-class","dijit/registry"],
    function(declare, _Widget,domClass,registry){
        return declare(_Widget, {

        handleStateChanged:function(contextInfo){
              console.debug("Container.handleStateChanged: ",contextInfo);

             if (contextInfo["parentId"]) {
                 this._handleHelperChanged(contextInfo);
             }else{
                 this.valid = contextInfo["valid"];
                 this.readonly = contextInfo["readonly"];
                 this.required = contextInfo["required"];
                 this.relevant = contextInfo["enabled"];
                  console.debug("Container.handleStateChanged value:",this.value," valid:", this.valid, " readonly:",this.readonly," required:",this.required, " relevant:",this.relevant, " contextInfo:",contextInfo);

                 if (this.valid != null) {
                     this._handleSetValidProperty(eval(this.valid));
                 }
                 if (this.readonly != null) {
                     this._handleSetReadonlyProperty(eval(this.readonly));
                 }
                 if (this.required != null) {
                     this._handleSetRequiredProperty(eval(this.required));
                 }
                 if (this.relevant != null) {
                     this._handleSetEnabledProperty(eval(this.relevant));
                 }

             }

         },

         _handleSetValidProperty:function(valid){
             if (valid) {
                 bf.util.replaceClass(this.domNode, "xfInvalid", "xfValid");
             } else {
                 bf.util.replaceClass(this.domNode, "xfValid", "xfInvalid");
             }

         },
         _handleSetReadonlyProperty: function(readonly){
             console.debug("Container._handleSetReadonlyProperty readonly = " , (this.readonly == false));
             if (readonly) {
                 bf.util.replaceClass(this.domNode, "xfReadWrite", "xfReadOnly");
             }
             else {
                 bf.util.replaceClass(this.domNode, "xfReadOnly", "xfReadWrite");

             }
         },

         _handleSetRequiredProperty:function(){
             if (this.required == "true") {
                 bf.util.replaceClass(this.domNode, "xfOptional", "xfRequired");
             }
             else {
                 bf.util.replaceClass(this.domNode, "xfRequired", "xfOptional");
             }
         },

         _handleSetEnabledProperty: function(){
             var targetId = this.id;
             var label = dom.byId(targetId + "-label");

             if (this.relevant == "true") {
                 bf.util.replaceClass(this.domNode, "xfDisabled", "xfEnabled");
                 bf.util.replaceClass(label, "xfDisabled", "xfEnabled");
             }
             else {
                 bf.util.replaceClass(this.domNode, "xfEnabled", "xfDisabled");
                 bf.util.replaceClass(label, "xfEnabled", "xfDisabled");
             }
         },

         _handleHelperChanged: function(properties){
             console.debug("Container.handleHelperChanged: type='" + properties["type"] + "',  value='" + properties["value"] + "'");
             switch (properties["targetName"]) {
                 case "label":
                     this._setLabel( properties["value"]);
                     return;
                 case "help":
                     this._setHelp( properties["value"]);
                     return;
                 case "hint":
                     this._setHint( properties["value"]);
                     return;
                 case "alert":
                     this._setAlert( properties["value"]);
                     return;
             }
         },

         isRequired:function(){
             if(domClass.contains(this.domNode,"xfOptional")){
                 return false;
             }else if(domClass.contains(this.domNode,"xfRequired")){
                 return true;
             }else {
                 console.warn("Container: No required state found, Container is probably unbound")
             }
         },

         isReadonly:function(){
             console.debug("Container.isReadonly",this.id);
             if(domClass.contains(this.domNode,"xfReadWrite")){
                 return false;
             }else if(domClass.contains(this.domNode,"xfReadOnly")){
                 return true;
             }else {
                 console.warn("Container: No readonly state found, Container is probably unbound")
             }
         },

         isRelevant:function(){
             console.debug("Container.isRelevant",this.domNode);
             if(domClass.contains(this.domNode,"xfDisabled")){
                 return false;
             }else if(domClass.contains(this.domNode,"xfEnabled")){
                 return true;
             }else {
                 console.warn("Container: No relevant state found, Container is probably unbound")
             }
         },

         isValid:function(){
             if(domClass.contains(this.domNode,"xfInvalid")){
                 return false;
             }else if(domClass.contains(this.domNode,"xfValid")){
                 return true;
             }else {
                 console.warn("Container No validate state found for " + this.id + " Container is probably unbound");
             }
         },

         _setLabel:function( value) {
             console.warn("TBD: Container._setLabel value:"+ value);
         },

         _setHelp:function( value) {
             console.warn("TBD: Container._setHelp value:"+ value);
         },

         _setHint:function( value) {
             console.warn("TBD: Container._setHint value:"+ value);
    /*
           var hint = registry.byId(this.target.id + "-hint");
           hint.label = value;
    */
         },

         _setAlert:function( value) {
             console.warn("TBD: Container._setAlert value:"+ value);

         },

         _setValueChild:function( value) {
             console.warn("TBD: Container._setValueChild value:"+ value);
         }

    });
});
