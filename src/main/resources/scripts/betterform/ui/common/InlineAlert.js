dojo.provide("betterform.ui.common.InlineAlert");


dojo.require("betterform.ui.common.Alert");

dojo.declare("betterform.ui.common.InlineAlert",
        betterform.ui.common.Alert,
{

    // @Override
    _show:function(id, commonChild) {
        // console.debug("InlineAlert._show [id:'",id,"' commonChild:'", commonChild,"']");
        var commonChildNode = dojo.byId(id + '-' + commonChild);
        if(commonChildNode == undefined || commonChild != this.info) {
            return;
        }
        this_render(id, commonChild,"inline");
    },


    // @Override
    _hide:function(id, commonChild) {
        // console.debug("InlineAlert._hide [id:'",id,"' commonChild:'", commonChild,"']");
        var commonChildNode = dojo.byId(id + '-' + commonChild);
        if(commonChildNode == undefined || commonChild != this.info) {
            return;
        }
        this_render(id, commonChild,"none");

    },

    _render:function(id, commonChild, show) {
        // console.debug("InlineAlert._render [id:'",id,"' commonChild:'", commonChild," ' show:'",show, "']");
        var mip = dojo.byId(id + "-" + commonChild);
        if (mip != undefined && mip.innerHTML != '') {
            dojo.style(mip, "display", show);
        } else {
            console.warn(id + "-" + commonChild + " is not defined for Control " + id);
        }
    }

});
