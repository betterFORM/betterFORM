dojo.provide("betterform.ui.common.InlineAlert");

dojo.require("dijit._Widget");

dojo.declare("betterform.ui.common.InlineAlert",
        dijit._Widget,
{
    handleValid:function(id,action){
        // console.debug("betterform.ui.common.InlineAlert.handleValidState[id:" + id , " action: " + action + "]");

        if(action == "xfDisabled") {
            this._showState(id, "none");
        }
        var control = dijit.byId(id);
        if(action =="onFocus" && control.getControlValue() == ''){
                this._showState(id, "hint");
        }
        else if((action =="applyChanges" || action=="onBlur") && control.getControlValue() != '') {
            this._showState(id, "info");
        }
        else if((action =="applyChanges" || action=="onBlur") && control.getControlValue() == '') {
            this._showState(id, "none");
        }

        var controlValue = dijit.byId(id+"-value");
        if(dojo.hasClass(controlValue.domNode,"bfInvalidControl")) {
            dojo.removeClass(controlValue.domNode,"bfInvalidControl");
        }

    },

    handleInvalid:function(id,action) {
        console.debug("betterform.ui.common.InlineAlert.handleInvalidState[id:" + id , " action: " + action + "]");
        var control = dijit.byId(id);
        var alert = this._placeAlert(id);

        if(alert == undefined) {
            return;
        }
        // console.debug("Control: ", control, " value: "+ control.getControlValue());

        //##### SHOW NOTHING ON INIT #######
        if(action == "init") {
            return;
        }

        //##### SHOW ALL ALERTS IN RESPONSE TO SUBMIT ERRORS #######
        if(action == "submitError") {
            this._showState(id, "alertAttachPoint")
        }

        var emptyValue;
        console.debug("handle Invalid: " + id , " action: " + action + " value: " , control.getControlValue(), "]");
        if((control.getControlValue() == undefined || control.getControlValue() == '') && !(dojo.hasClass(control.domNode,"xsdBoolean"))){
            emptyValue = true;
        }else {
            emptyValue = false;
        }

        //##### HIDE ALL ALERTS IF CONTROL IS DISABLED (relevance = false) #######
        if(action == "xfDisabled") {
            this._showState(id, "none");
        }



        if(emptyValue) {
            //##### SHOW NOTHING #######
            if(action =="onBlur"){
                this._showState(id,"none");
            }
            else if(action =="applyChanges"){
                this._showState(id,"none");
            }
            //##### SHOW HINT #######
            else if (action == "onFocus") {
                this._showState(id, "hint");
            }
            else if (action =="applyChanges") {
                this._showState(id, "hint");
            }

        }
        //##### SHOW ALERT #######
        else {
            if(action =="applyChanges" ){
                console.debug("handleInvalid for applyChanges: id: ", id);
                this._showState(id,"alert");
                dojo.addClass(control.controlValue.domNode,"bfInvalidControl");
            }
            else if(action == "onFocus"){
                this._showState(id,"alert");
            }
        }

    },

    _placeAlert:function(id) {
        // console.debug("InlineAlert._placeAlert()");
        var alertAttachPoint = dojo.byId(id+"-alertAttachPoint");
        var alertNode = dojo.byId(id+"-alert");
        if(alertNode != undefined && alertAttachPoint != undefined && !alertAttachPoint.hasChildNodes()){
            dojo.place(alertNode, alertAttachPoint);
            dojo.attr(alertNode, "style", "");
        }
        return alertNode;


    },

    _showState:function(id, state) {
        // console.debug("InlineAlert._showState: state:", state);

        if (state == "alert") {
            this._display(id,"hint", "none");
            this._display(id,"info", "none");
            var alertAttachPoint = dojo.byId(id+"-alertAttachPoint")
            dojo.attr(alertAttachPoint,"style","");
        }
        else if (state == "hint") {
            this._display(id,"alertAttachPoint", "none");
            this._display(id,"info", "none");
            // this._display(id,"hint", "block");
            var hintAttachPoint = dojo.byId(id+"-hint")
            dojo.attr(hintAttachPoint,"style","");

        }
        else if (state == "info") {
            this._display(id,"alertAttachPoint", "none");
            this._display(id,"hint", "none");
            this._display(id,"info", "block");
        }
        else if (state == "none") {
            this._display(id,"alertAttachPoint", "none");
            this._display(id,"hint", "none");
            this._display(id,"info", "none");
        } else {
            console.warn("State '" + state + "' for Control " + id + " is unknown");
        }
    },

    _display:function(id, commonChild, show) {
        var mip = dojo.byId(id + "-" + commonChild);
        if (mip != undefined && mip.innerHTML != '') {
            dojo.style(mip, "display", show);
        } else {
            // console.warn(id + "-" + commonChild + " is not defined for Control " + id);
        }
    }

});
