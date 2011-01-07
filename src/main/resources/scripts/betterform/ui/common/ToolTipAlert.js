dojo.provide("betterform.ui.common.ToolTipAlert");

dojo.require("dojo.NodeList-fx");
dojo.require("betterform.ui.common.Tooltip");

dojo.declare("betterform.ui.common.ToolTipAlert",
        betterform.ui.common.Alert,
{
    displayDuration:3000,
    hideSpeed:1000,

    _show:function(id, commonChild,action) {
        // console.debug("ToolTipAlert._show: [id:" + id , " commonChild: " + commonChild + "]");
        var commonChildNode = dojo.byId(id + '-' + commonChild);

        if(commonChildNode == undefined || commonChild != this.alert) {
            return;
        }



        var toolTipId = id+"-MasterToolTip-" +commonChild;
        var alertTooltip = dijit.byId(toolTipId);

        if(alertTooltip == undefined) {
            alertTooltip = new dijit._MasterTooltip({id:toolTipId});

            var valueNode = dojo.byId(id + '-value');
            dojo.connect(alertTooltip, "onClick", this, dojo.hitch(this, function() {
                    alertTooltip.hide(valueNode);
            }));
        }

        var controlValue = dijit.byId(id+"-value");
        var controlValueIsEmpty = (controlValue.getControlValue() == undefined || controlValue.getControlValue() == '') && !(dojo.hasClass(controlValue.domNode, "xsdBoolean"));

        alertTooltip.show(commonChildNode.innerHTML, dojo.byId(id+"-value"));

        if (action == "applyChanges" && (!controlValueIsEmpty || dojo.hasClass(controlValue.domNode, "xsdBoolean"))) {
            dojo.style(alertTooltip.domNode, "opacity", "1");
            dojo.addClass(controlValue.domNode, "bfInvalidControl");

            setTimeout(dojo.hitch(this,function() {this._fadeOutAndHide(id,commonChild)}),this.displayDuration);
        }

        dojo.style(alertTooltip.domNode, "opacity", "1");
        dojo.addClass(controlValue.domNode, "bfInvalidControl");



    },


    _hide:function(id, commonChild,action) {
        // console.debug("ToolTipAlert._hide: [id:" + id , " commonChild: " + commonChild + "]");
        var commonChildNode = dojo.byId(id + '-' + commonChild);


        if (commonChildNode != undefined && commonChild == this.alert) {
            var controlValue = dojo.byId(id + "-value");
            var alertDijit = dijit.byId(id+"-MasterToolTip-" +commonChild);
            if (alertDijit != undefined && controlValue != undefined) {
                alertDijit.hide(controlValue);
            }
        }
    },


    _fadeOutAndHide:function(id,commonChild) {
        var alertTooltip = dijit.byId(id+"-MasterToolTip-" +commonChild);
        // No need to check if tooltip exists since this function is only called if (after a check before) it exists
        // console.debug("ToolTipAlert._fadeOutAndHide  [id: " + id + " - alertTooltip:" , alertTooltip ,"]");
        var speed = this.hideSpeed;
        dojo.fadeOut({
            node:alertTooltip.domNode,
            duration:speed,
            onEnd:function() {
                alertTooltip.hide(dojo.byId(id+"-value"));
    	}
        }).play();
    }



});
