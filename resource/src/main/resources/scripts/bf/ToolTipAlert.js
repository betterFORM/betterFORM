/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/_base/declare","bf/Alert","dojo/dom-style","dojo/_base/connect","dojo/_base/lang"],
    function(declare, Alert,domStyle,connect,lang){
        return declare(Alert, {

        displayDuration:3000,
        hideSpeed:1000,

        _show:function(id, commonChild,action) {
            // console.debug("ToolTipAlert._show: [id:" + id , " commonChild: " + commonChild + "]");
            var commonChildNode = dom.byId(id + '-' + commonChild);

            if(commonChild != undefined && commonChild == this.hint) {
                this._render(id, commonChild,"inline");
            }
            else if(commonChildNode != undefined && commonChild == this.alert) {
                // console.debug("ToolTipAlert._show: [id:" + id , " commonChildNode: " + commonChildNode + "]");

                var toolTipId = id+"-MasterToolTip-" +commonChild;
                var alertTooltip = dijit.byId(toolTipId);

                var valueNode = query('.xfValue', dom.byId(id))[0];
                if(alertTooltip == undefined) {
                    alertTooltip = new dijit._MasterTooltip({id:toolTipId});


                    connect.connect(alertTooltip, "onClick", this, lang.hitch(this, function() {
                            alertTooltip.hide(valueNode);
                    }));
                }

                // console.debug("ToolTipAlert: controlValueNode:",valueNode);
                alertTooltip.show(commonChildNode.innerHTML, valueNode);

                domStyle.set(alertTooltip.domNode, "opacity", "1");
                domStyle.set(alertTooltip.domNode, "cursor", "pointer");
                domClass.add(alertTooltip.domNode, "bfToolTipAlert");
                domClass.add(valueNode, "bfInvalidControl");

    /*        if (action == "applyChanges" && (!controlValueIsEmpty || domClass.contains(controlValue.domNode, "xsdBoolean"))) {
                setTimeout(lang.hitch(this,function() {this._fadeOutAndHide(id,commonChild)}),this.displayDuration);
              }
    */
            }
        },


        _hide:function(id, commonChild,action) {
            // console.debug("ToolTipAlert._hide: [id:" + id , " commonChild: " + commonChild + "]");
            var commonChildNode = dom.byId(id + '-' + commonChild);


            if (commonChildNode != undefined && commonChild == this.alert) {
                var controlValue = query('.xfValue', dom.byId(id))[0];
                var alertDijit = dijit.byId(id+"-MasterToolTip-" +commonChild);
                if (alertDijit != undefined && controlValue != undefined) {
                    alertDijit.hide(controlValue);
                }
                if(domClass.contains(controlValue,"bfInvalidControl")) {
                    domClass.remove(controlValue,"bfInvalidControl");
                }

            } else if (commonChild != undefined && commonChild == this.hint) {
                this._render(id, commonChild,"none");
            }
        },

        _render:function(id, commonChild, show) {
            // console.debug("ToolTipAlert._render [id:'",id,"' commonChild:'", commonChild," ' show:'",show, "']");
            var mip = dom.byId(id + "-" + commonChild);
            if (mip != undefined && mip.innerHTML != '') {
                domStyle.set(mip, "display", show);
            } else {
                console.info(id + "-" + commonChild + " is not defined for Control " + id);
            }
        },



        _fadeOutAndHide:function(id,commonChild) {
            var alertTooltip = dijit.byId(id+"-MasterToolTip-" +commonChild);
            // No need to check if tooltip exists since this function is only called if (after a check before) it exists
            var valueNode = query('.xfValue', dom.byId(id))[0];
            // console.debug("ToolTipAlert._fadeOutAndHide  [id: " + id + " - alertTooltip:" , alertTooltip ,"]");
            var speed = this.hideSpeed;
            dojo.fadeOut({
                node:alertTooltip.domNode,
                duration:speed,
                onEnd:function() {
                    alertTooltip.hide(valueNode);
            }
            }).play();
        }
    });
});
