/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.common.InlineAlert");


dojo.require("betterform.ui.common.Alert");

dojo.declare("betterform.ui.common.InlineAlert",
        betterform.ui.common.Alert,
{

    // @Override
    _show:function(id, commonChild) {
        console.debug("InlineAlert._show [id:'",id,"' commonChild:'", commonChild,"']");
        var commonChildNode = dojo.byId(id + '-' + commonChild);
        if(commonChildNode == undefined || commonChild == this.info) {
            return;
        }
        this._render(id, commonChild,"inline");
    },


    // @Override
    _hide:function(id, commonChild) {
        console.debug("InlineAlert._hide [id:'",id,"' commonChild:'", commonChild,"']");
        var commonChildNode = dojo.byId(id + '-' + commonChild);
        if(commonChildNode == undefined || commonChild == this.info) {
            return;
        }
        this._render(id, commonChild,"none");

    },

    _render:function(id, commonChild, show) {
        console.debug("InlineAlert._render [id:'",id,"' commonChild:'", commonChild," ' show:'",show, "']");
        var mip = dojo.byId(id + "-" + commonChild);
        if (mip != undefined && mip.innerHTML != '') {
            // add onclick handler to alerts to close them by mouse click
            if(commonChild == "alert" && show=="inline") {
                dojo.style(mip, "cursor", "pointer");
                mip.onclick = dojo.hitch(this, function(evt) {
                    // console.debug("Alert clicked id: ", id, " commonChild: ", commonChild, " show: " , show);
                    this._hide(id,commonChild);
                    this._show(id,"hint");
                });
            }
            dojo.style(mip, "display", show);
        } else {
            console.info(id + "-" + commonChild + " is not defined for Control " + id);
        }
    }

});
