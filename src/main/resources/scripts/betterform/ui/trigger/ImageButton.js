/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.trigger.ImageButton");

dojo.require("betterform.ui.trigger.Button");

dojo.declare(
        "betterform.ui.trigger.ImageButton",
        [betterform.ui.trigger.Button],
{
    buildRendering:function() {
        console.debug("betterform.ui.trigger.ImageButton.buildRendering: START");
        this.inherited(arguments);
        var imageSource = dojo.query(".xfValue", this.srcNodeRef)[0].innerHTML;

            if (imageSource != undefined && imageSource != "") {
                var image = document.createElement("img");

                dojo.attr(image, "src", imageSource);
                dojo.attr(this.iconNode, "id", this.imageId);
                this.iconNode.appendChild(image);
                this.showLabel = false;
            }
        

        console.dirxml(this.srcNodeRef);
        console.debug("betterform.ui.trigger.ImageButton.buildRendering: END");
    },

    _handleSetControlValue:function(value) {
        var iconNodeList = dojo.query(("span[id=" + this.id + "][dojoattachpoint=iconNode] > img"));
        if (iconNodeList.length == 1) {
            iconNodeList[0].src = value;
        }else{
            console.warn("imageTirrgger without iconNode found");
        }

    }

});
