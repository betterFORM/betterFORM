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
            if( (action=="onFocus" || action=="onBlur") && (control.getControlValue() != '')){
                alertTooltip.show(alert.innerHTML, dojo.byId(id+"-value"));
            }
            else  if(action=="applyChanges" && (control.getControlValue() != '' || dojo.hasClass(control.domNode,"xsdBoolean"))){

                alertTooltip.show(alert.innerHTML, dojo.byId(id+"-value"));
                dojo.style(alertTooltip.domNode, "opacity", "1");
                dojo.addClass(control.domNode, "bfInvalidControl");

                setTimeout(dojo.hitch(this,"_fadeOutAndHide", id),3000);

                    }
            else {
                alertTooltip.hide(dojo.byId(id+"-value"));
            }
        }
    },

    _fadeOutAndHide:function(id) {
        var alertTooltip = dijit.byId(id+"-MasterToolTip-alert");
        // No need to check if tooltip exists since this function is only called if (after a check before) it exists

        dojo.fadeOut({
            node:alertTooltip.domNode,
            duration:1000,
            onEnd:function() {
                alertTooltip.hide(dojo.byId(id+"-value"));
    	}
        }).play();
    }

});
