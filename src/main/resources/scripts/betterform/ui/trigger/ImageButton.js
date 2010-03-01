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
                this.iconNode.appendChild(image);
                this.showLabel = false;
            }
        

        console.dirxml(this.srcNodeRef);
        console.debug("betterform.ui.trigger.ImageButton.buildRendering: END");
    }
});
