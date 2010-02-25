dojo.provide("betterform.ui.common.ToolTipAlert");

dojo.declare("betterform.ui.common.ToolTipAlert",
        null,
{
    handleValid:function(id,action){
        var control = dijit.byId(id+"-value");
        // console.debug("betterform.ui.common.ToolTipAlert.valid [id:" + id , " action: " + action + " control: ", control, "]");
        var alertDijit = undefined;
        alertDijit = dijit.byId(id + "-MasterToolTip-alert");

        if (alertDijit != undefined) {
            alertDijit.hide(control.domNode);
        }

        if(dojo.hasClass(control.domNode,"bfInvalidControl")){
            dojo.removeClass(control.domNode, "bfInvalidControl");
        }

    },

    handleInvalid:function(id,action){
        // console.debug("betterform.ui.common.ToolTipAlert.invalid [id:" + id , " action: " + action + "]");
        var alertTooltip = dijit.byId(id+"-MasterToolTip-alert");
        if(alertTooltip == undefined) {
            alertTooltip = new dijit._MasterTooltip({id:id+"-MasterToolTip-alert"});
        }

        var control = dijit.byId(id);
        var alert = dojo.byId(id + '-alert');
        // console.debug("betterform.ui.common.ToolTipAlert.invalid [alertTooltip:" + alertTooltip , " alertNode: " + alert + "]");
        if (alert != undefined) {
            if(action == "xfDisabled") {
                alertTooltip.hide(control.domNode);
            }
            if(action=="onFocus" && (control.getControlValue() != '')){
                alertTooltip.show(alert.innerHTML, dojo.byId(id+"-value"));
            }
            else  if(action=="applyChanges" && (control.getControlValue() != '' || dojo.hasClass(control.domNode,"xsdBoolean"))){

                alertTooltip.show(alert.innerHTML, dojo.byId(id+"-value"));
                dojo.style(alertTooltip.domNode, "opacity", "1");
                
                dojo.fadeOut({
                    node:alertTooltip.domNode,
                    duration:3000,
                    invalidControl:control.controlValue,
                    onBegin:function() {
                        // console.debug("this.invalidControl: ",this.invalidControl);
                        dojo.addClass(this.invalidControl.domNode, "bfInvalidControl");
                    }
                }).play();
            }
            else {
                alertTooltip.hide(dojo.byId(id+"-value"));
            }
        }
    }

});
