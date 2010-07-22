dojo.provide("betterform.ui.common.InlineRoundBordersAlert");

dojo.require("betterform.ui.common.InlineAlert");
dojo.require("dojo.behavior");
    
dojo.declare("betterform.ui.common.InlineRoundBordersAlert",
        betterform.ui.common.InlineAlert,
{



//    constructor:function() {
//        // console.debug("creating InlineRoundBorderAlert handler");
//      
//        var zoomBehavior = dojo.behavior.add({
//            'span.xfAlert': {
//                onmouseenter: function(evt) {
//                    console.debug("InlineRoundBordersAlert.onMouseEnter behaviour: ", this, " evt: ", evt);
///*
//                    var textBigger = dojo.animateProperty(
//                        {
//                          node: this,duration: 1000,
//                          properties: {
//                            fontSize: {end: 25, unit: "px"}
//                          }
//                        });
//                    textBigger.play();
//*/
//
//                    var coords = dojo.coords(this);
//                    var heightBefore = coords.h - 9;
//                    dojo.style(this, "whiteSpace", "normal");
//                     dojo.style(this, "position", "absolute");
//                    var top = coords.y;
//
//                    dojo.style(this, "top", top + "px");
//
//                    // console.debug("HeightBefore: ", heightBefore, " dojo.style(this, 'height'):",dojo.style(this, "height"));
//
//
//                    if (heightBefore < dojo.style(this, "height")) {
//                        // console.debug("manipulating borders");
//                        dojo.style(this, "MozBorderRadiusBottomleft", "8px");
//                        dojo.style(this, "WebkitBorderBottomLeftRadius", "8px");
//                    }
//                },
//
//                onmouseleave:function(evt) {
//                     console.debug("InlineRoundBordersAlert.onmouseleave: node:",this);
//
///*
//                    var textSmaller = dojo.animateProperty(
//                      {
//                        node: this,duration: 1000,
//                        properties: {
//                          fontSize: {end: 10, unit: "px"}
//                        }
//                      });
//                    textSmaller.play();
//*/
//
//                     dojo.style(this, "whiteSpace", "nowrap");
//                     dojo.style(this, "MozBorderRadiusBottomleft", "0px");
//                     dojo.style(this, "WebkitBorderBottomLeftRadius", "0px");
//                }
//
//            }
//        });
//        dojo.behavior.add(zoomBehavior);
//        dojo.behavior.apply();
//    },

    _showState:function(id, state) {
        // console.debug("InlineRoundBordersAlert._showState: state:", state);
        var mip = dojo.byId(id + "-" + state);
        this._handleBorders(id, state);
        if (state == "alert") {
            this._display(id, "hint", "none");
            this._display(id, "info", "none");
            this._display(id, "alert", "block");
        }
        else if (state == "hint") {
            this._display(id, "alert", "none");
            this._display(id, "info", "none");
            this._display(id,"hint", "block");

        }
        else if (state == "info") {
            this._display(id, "alert", "none");
            this._display(id, "hint", "none");
            this._display(id, "info", "block");
        }
        else if (state == "none") {
            this._display(id, "alert", "none");
            this._display(id, "hint", "none");
            this._display(id, "info", "none");
        } else {
            // console.warn("State '" + state + "' for Control " + id + " is unknown");
        }
    },

    _display:function(id, commonChild, show) {
        // console.debug("InlineRoundBorders._display: id: " + id + "-" + commonChild + " show: " + show);
        var mip = dojo.byId(id + "-" + commonChild);
        if (mip != undefined && mip.innerHTML != '') {
            dojo.style(mip, "display", show);
          //  dojo.style(mip, "position", "static");
          //  dojo.style(mip, "whiteSpace", "nowrap");

        } else {
            console.warn(id + "-" + commonChild + " is not defined for Control " + id);
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
        // console.debug("InlineRoundBordersAlert._roundBorders: id:", id + "-value");
        var controlValue = dijit.byId(id +"-value");
        // console.debug("InlineRoundBordersAlert._roundBorders: value: ", controlValue);
        if(controlValue != undefined) {
            dojo.style(controlValue.domNode, "MozBorderRadiusTopright", "8px");
            dojo.style(controlValue.domNode, "MozBorderRadiusBottomright", "8px");
            dojo.style(controlValue.domNode, "WebkitBorderTopRightRadius", "8px");
            dojo.style(controlValue.domNode, "WebkitBorderBottomRightRadius", "8px");

        }
    },

    _angularBorders:function(id, state) {
        // console.debug("InlineRoundBordersAlert._angularBorders: id:", id + "-value");
        var controlValue = dijit.byId(id + "-value");
       // console.debug("InlineRoundBordersAlert._angularBorders: value: ", controlValue);
        if (controlValue != undefined) {
            dojo.style(controlValue.domNode, "MozBorderRadiusTopright", "0px");
            dojo.style(controlValue.domNode, "MozBorderRadiusBottomright", "0px");
            dojo.style(controlValue.domNode, "WebkitBorderTopRightRadius", "0px");
            dojo.style(controlValue.domNode, "WebkitBorderBottomRightRadius", "0px");
        } else {
            this._roundBorders(state);
        }
       //  console.debug("END InlineRoundBordersAlert._angularBorders: value: ", controlValue);
    }
});
