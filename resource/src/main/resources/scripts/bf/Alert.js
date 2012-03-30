/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/_base/declare","dojo/_base/window","dojo/dom-class","dijit/registry","dojo/query","dojo/dom"],
    function(declare,win,domClass,registry,query,dom){
        return declare(null, {

        alert:"alert",
        hint:"hint",
        info:"info",
        none:"none",
        alwaysShowHint: query(".bfAlwaysShowHint", win.body())[0],

        handleValid:function(id,action){
            // TODO: applyChanges must remove an existing alert

            // console.debug("Alert.handleValid[id:" + id , " action: " + action + "]");

            var control = registry.byId(id);
            if(control == null) {
                console.warn("control '" +id +"' does not exist");
                return;
            }
            // console.debug("control: ",control);
            var controlValueIsEmpty = this._controlValueIsEmpty(control);

            // console.debug("controlValueIsEmpty:",controlValueIsEmpty, " control.getControlValue(): ",control.getControlValue());

            if(action == "init") {
                // do nothing on init
                return;
            }
            else if(action == "xfDisabled"|| action == "changeAlertType" || ((action =="applyChanges" || action=="onBlur") && controlValueIsEmpty)) {
                this._displayNone(id,action);
            }
            if(action =="onFocus" && (controlValueIsEmpty || this.alwaysShowHint != undefined)){
                this._displayHint(id,action);
            }
            else if((action =="applyChanges" || action=="onBlur") && !controlValueIsEmpty) {
                this._displayInfo(id,action);
            }else {
                console.info("Alert.handleValid: action:'", action , "' unknown, commonChild handling for control '", id, "', execution stopped");
            }

            if(domClass.contains(control.domNode,"bfInvalidControl")) {
                domClass.remove(control.domNode,"bfInvalidControl");
            }
        },

        handleInvalid:function(id,action) {
            // console.debug("Alert.handleInvalid [id:" + id , " action: " + action + "]");

            //##### SHOW NOTHING ON INIT #######
            var control = registry.byId(id);
            if(control == null) {
                console.info("control '" +id +"' does not exist");
                return;
            }

            // console.debug("control: ",control);

            // evaluate if control value is empty
            var controlValueIsEmpty = this._controlValueIsEmpty(control);

            // console.debug("controlValueIsEmpty:",controlValueIsEmpty, " control.getControlValue(): ",control.getControlValue());
            if(dom.byId(id + "-" + this.alert) == undefined || action == "init" || action == "changeAlertType") {
                return;
            }

    /*
            else if((action == "xfDisabled"|| action =="onBlur" || action =="applyChanges") && controlValueIsEmpty) {
                this._displayNone(id,action);
            }
    */

            else if(action == "onFocus" && (controlValueIsEmpty || this.alwaysShowHint != undefined) ) {
                this._displayHint(id,action);
                return;
            }
            //##### SHOW ALERT #######
            else if(action == "onFocus" || action == "xfDisabled"|| action =="onBlur" || action =="applyChanges" || action == "submitError"){
                this._displayAlert(id,action);
            }

            //##### SHOW ALL ALERTS IN RESPONSE TO SUBMIT ERRORS #######
    /*
            else if(action == "submitError") {
                this._displayAlert(id,action);
                return;
            }
    */
            else {
                console.info("Alert.handleInvalid: action:'", action , "' unknown, commonChild handling for control '", id, "', execution stopped");
            }

            if(!domClass.contains(control.domNode,"bfInvalidControl")) {
                domClass.add(control.domNode,"bfInvalidControl");
            }
        },

        _displayAlert:function(id,action) {
            this._show(id,this.alert,action);
            this._hide(id,this.hint,action);
            this._hide(id,this.info,action);
        },

        _displayHint:function(id,action) {
            this._show(id,this.hint,  action);
            this._hide(id,this.alert, action);
            this._hide(id,this.info,  action);
        },

        _displayInfo:function(id,action) {
            this._show(id,this.info,  action);
            this._hide(id,this.hint,  action);
            this._hide(id,this.alert, action);

        },

        _displayNone:function(id,action) {
            this._hide(id,this.alert, action);
            this._hide(id,this.hint,  action);
            this._hide(id,this.info, action);
        },


        _show:function(id, commonChild,action) {
            console.error("Alert._show must be overwritten by its extending class");
        },


        _hide:function(id, commonChild,action) {
            console.error("Alert._hide must be overwritten by its extending class");
        },

        _controlValueIsEmpty:function(controlDijit){

            var controlValueIsEmpty = false;
            var controlValue = controlDijit.getControlValue();
            if (controlValue == undefined ||  controlValue == '') {
                controlValueIsEmpty =  true;
            }else if (domClass.contains(controlDijit.domNode, "xsdBoolean") && !controlValue) {
                controlValueIsEmpty = true;
            } else if (domClass.contains(controlDijit.domNode, "xfRange") && (controlValue == 0 || controlValue == "0")){
                controlValueIsEmpty = true;
            }
            // console.debug("Alert._controlValueIsEmpty: ",controlValueIsEmpty, " controlValue is: ",controlValue, " controlDOMNode: ", controlDijit.domNode);
            return controlValueIsEmpty;
        }

    });
});
