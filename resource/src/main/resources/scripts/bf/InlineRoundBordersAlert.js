/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/_base/declare","bf/Alert","dojo/dom-style","dijit/registry"],
    function(declare, Alert, domStyle,registry){
        return declare(Alert, {

        // @Override
        _show:function(id, commonChild) {
            console.debug("InlineRoundBordersAlert._show: [id:'",id,"' commonChild:'", commonChild,"']");
            var commonChildNode = dom.byId(id + '-' + commonChild);
            if(commonChildNode == undefined) {
                return;
            }
            this._render(id, commonChild,"block");
        },


        // @Override
        _hide:function(id, commonChild) {
            console.debug("InlineRoundBordersAlert._hide: [id:'",id,"' commonChild:'", commonChild,"']");
            var commonChildNode = dom.byId(id + '-' + commonChild);
            if(commonChildNode == undefined) {
                return;
            }
            this._render(id, commonChild,"none");

        },

        // @Override
        _displayAlert:function(id) {
            console.debug("InlineRoundBordersAlert._displayAlert id: ", id);
            this._handleBorders(id, this.alert);
            this.inherited(arguments);
        },

        // @Override
        _displayHint:function(id) {
            console.debug("InlineRoundBordersAlert._displayHint id: ", id);
            this._handleBorders(id, this.hint);
            this.inherited(arguments);
        },

        // @Override
        _displayInfo:function(id) {
            console.debug("InlineRoundBordersAlert._displayInfo id: ", id);
            this._handleBorders(id, this.info);
            this.inherited(arguments);
        },

        // @Override
        _displayNone:function(id) {
            console.debug("InlineRoundBordersAlert._displayNone id: ", id);
            this._handleBorders(id, this.none);
            this.inherited(arguments);
        },



        _handleBorders:function(id, state) {
            if (state == this.info || state == this.hint || state == this.alert) {
                this._angularBorders(id, state);

            } else {
                this._roundBorders(id, state);
            }
        },

        _roundBorders:function(id, state) {
            console.debug("InlineRoundBordersAlert._roundBorders: id:", id + "-value");
            var controlValue = registry.byId(id + "-value");
            // console.debug("InlineRoundBordersAlert._roundBorders: value: ", controlValue);
            if (controlValue != undefined) {
                domStyle.set(controlValue.domNode, "MozBorderRadiusTopright", "8px");
                domStyle.set(controlValue.domNode, "MozBorderRadiusBottomright", "8px");
                domStyle.set(controlValue.domNode, "WebkitBorderTopRightRadius", "8px");
                domStyle.set(controlValue.domNode, "WebkitBorderBottomRightRadius", "8px");

            }
        },

        _angularBorders:function(id, state) {
            // console.debug("InlineRoundBordersAlert._angularBorders: id:", id + "-value");
            var controlValue = registry.byId(id + "-value");
            // console.debug("InlineRoundBordersAlert._angularBorders: value: ", controlValue);
            if (controlValue != undefined) {
                domStyle.set(controlValue.domNode, "MozBorderRadiusTopright", "0px");
                domStyle.set(controlValue.domNode, "MozBorderRadiusBottomright", "0px");
                domStyle.set(controlValue.domNode, "WebkitBorderTopRightRadius", "0px");
                domStyle.set(controlValue.domNode, "WebkitBorderBottomRightRadius", "0px");
            } else {
                this._roundBorders(state);
            }
            //  console.debug("END InlineRoundBordersAlert._angularBorders: value: ", controlValue);
        }
    });
});