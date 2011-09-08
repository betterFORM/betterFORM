/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.container.Switch");

dojo.require("dijit._Widget");
dojo.require("betterform.ui.container.Container");


dojo.declare(
        "betterform.ui.container.Switch",
        betterform.ui.container.Container,
{

    /**
     * HandleStateChanged for all Container Dijits
     * @param contextInfo
     */
    handleStateChanged:function(contextInfo) {
        // console.debug("betterform.ui.container.Switch.handleStateChanged");
    },

    toggleCase:function(contextInfo){
        // console.debug("betterform.ui.container.Switch.toggleCase", contextInfo);
        // deselect case
        if(contextInfo.deselected != undefined) {
            var caseToDeselect = dojo.byId(contextInfo.deselected);
            // console.debug("betterform.ui.container.Switch.toggleCase: Case to deselect:",caseToDeselect);
            if(dojo.hasClass(caseToDeselect, "xfSelectedCase")){
                dojo.removeClass(caseToDeselect,"xfSelectedCase");
            }
            dojo.addClass(caseToDeselect,"xfDeselectedCase");            
        }

        // select case
        if(contextInfo.selected){
            var caseToSelect = dojo.byId(contextInfo.selected);
            // console.debug("betterform.ui.container.Switch.toggleCase: Case to select:",caseToSelect);
            if(dojo.hasClass(caseToSelect, "xfDeselectedCase")){
                dojo.removeClass(caseToSelect,"xfDeselectedCase");
            }
            dojo.addClass(caseToSelect,"xfSelectedCase");            
        }


    }
}       
);


