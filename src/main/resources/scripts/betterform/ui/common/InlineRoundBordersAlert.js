dojo.provide("betterform.ui.common.InlineRoundBordersAlert");

dojo.require("betterform.ui.common.InlineAlert");

dojo.declare("betterform.ui.common.InlineRoundBordersAlert",
        betterform.ui.common.InlineAlert,
{
    onMousEnter:function(ev) {
        dojo.style(this, "position", "absolute");
        dojo.style(this, "whiteSpace", "normal");
    },

    onMouseLeave:function(ev) {
        dojo.style(this, "position", "static");
        dojo.style(this, "whiteSpace", "nowrap");
    },

    _placeAlert:function(id) {
        // console.debug("InlineRoundBordersAlert._placeAlert()");
        var alertAttachPoint = dojo.byId(id + "-alertAttachPoint");
        var alertNode = dojo.byId(id + "-alert");
        if (alertNode != undefined && alertAttachPoint != undefined && !alertAttachPoint.hasChildNodes()) {
            dojo.place(alertNode, alertAttachPoint);
            dojo.attr(alertNode, "style", "");
        }

        this._connectHandlers(alertNode);

        //alertNode.onclick =  betterform.ui.util.showFullAlertDemo;
        return alertNode;
    },

    _showState:function(id, state) {
        console.debug("InlineRoundBordersAlert._showState: state:", state);

        this._handleBorders(id, state);
        if (state == "alert") {
            this._display(id, "hint", "none");
            this._display(id, "info", "none");
            var alertAttachPoint = dojo.byId(id + "-alertAttachPoint")
            dojo.attr(alertAttachPoint, "style", "");
        }
        else if (state == "hint") {
            this._display(id, "alertAttachPoint", "none");
            this._display(id, "info", "none");
            // this._display(id,"hint", "block");
            var hintAttachPoint = dojo.byId(id + "-hint")
            this._connectHandlers(hintAttachPoint);
            dojo.attr(hintAttachPoint, "style", "");

        }
        else if (state == "info") {
            this._display(id, "alertAttachPoint", "none");
            this._display(id, "hint", "none");
            this._display(id, "info", "block");
        }
        else if (state == "none") {
            this._display(id, "alertAttachPoint", "none");
            this._display(id, "hint", "none");
            this._display(id, "info", "none");
        } else {
            console.warn("State '" + state + "' for Control " + id + " is unknown");
        }
    },

    _display:function(id, commonChild, show) {
        var mip = dojo.byId(id + "-" + commonChild);
        if (mip != undefined && mip.innerHTML != '') {
            dojo.style(mip, "display", show);
            dojo.style(this, "position", "static");
            dojo.style(this, "whiteSpace", "nowrap");
        } else {
            // console.warn(id + "-" + commonChild + " is not defined for Control " + id);
        }
    },

    _handleBorders:function(id, state) {
        if (state == "info" || state == "hint" || state == "alert") {
            this._angularBorders(id, state);

        } else {
            this._roundBorders(id, state);
        }
    },

    _roundBorders:function(id, state) {
        // console.debug("Control._roundBorders: id:", id + "-value");
        dojo.style(dojo.byId(id + "-value"), "MozBorderRadiusTopright", "8px");
        dojo.style(dojo.byId(id + "-value"), "MozBorderRadiusBottomright", "8px");
        dojo.style(dojo.byId(id + "-value"), "WebkitBorderTopRightRadius", "8px");
        dojo.style(dojo.byId(id + "-value"), "WebkitBorderBottomRightRadius", "8px");
    },

    _angularBorders:function(id, state) {
        // console.debug("Control._angularBorders: id:", id + "-value");
        var mip = dojo.byId(id + "-" + state);
        if (mip != undefined && mip.innerHTML != '') {
            dojo.style(dojo.byId(id + "-value"), "MozBorderRadiusTopright", "0px");
            dojo.style(dojo.byId(id + "-value"), "MozBorderRadiusBottomright", "0px");
            dojo.style(dojo.byId(id + "-value"), "WebkitBorderTopRightRadius", "0px");
            dojo.style(dojo.byId(id + "-value"), "WebkitBorderBottomRightRadius", "0px");
        } else {
            this._roundBorders(state);
        }
    },

    _connectHandlers:function(node) {
        this._handleOnMouseEnter(node);
        this._handleOnMouseLeave(node);
    },

    _handleOnMouseEnter:function(node) {
        dojo.connect(node, "onmouseenter", function (e) {
            var coords = dojo.coords(node);
            var heightBefore = coords.h - 9;
            dojo.style(node, "whiteSpace", "normal");
            dojo.style(node, "position", "absolute");
            var top = coords.y;

            dojo.style(node, "top", top + "px");
            //var coordsAfter = dojo.coords(node);
            //var heightAfter = coordsAfter.h;
            //var yNew = y - (heightAfter - heightBefore);

           
            //dojo.coords(node, coordsNew);


            console.debug(heightBefore);
            console.debug(dojo.style(node, "height"));
            if (heightBefore < dojo.style(node, "height")) {
                console.debug("manipulating borders");
                dojo.style(node, "MozBorderRadiusBottomleft", "8px");
                dojo.style(node, "WebkitBorderBottomLeftRadius", "8px");
            }
        });
    },

    _handleOnMouseLeave:function(node) {
        dojo.connect(node, "onmouseleave", function (e) {
            dojo.style(node, "whiteSpace", "nowrap");
            dojo.style(node, "MozBorderRadiusBottomleft", "0px");
            dojo.style(node, "WebkitBorderBottomLeftRadius", "0px");
        });
    }
});
