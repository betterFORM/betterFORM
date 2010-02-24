/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.container.TabSwitch");

dojo.require("dijit.layout.TabContainer");
dojo.require("betterform.ui.container.Container");



dojo.declare("betterform.ui.container.TabSwitch",
        [betterform.ui.container.Container,dijit.layout.TabContainer],
{

    topics:[],



    postCreate: function(/*Event*/ evt){
        this.inherited(arguments);
        dojo.connect(this.tablist, "onSelectChild", this, "onTabClicked");
        this.topics[0] = dojo.subscribe("/xf/invalid",this,"_handleInvalid");
        this.topics[1] = dojo.subscribe("/xf/valid", this,"_handleValid");

    },

    onTabClicked:function(/*Event*/ evt){
        // console.debug("TabSwitch.onTabClicked event: ",evt);
        var btnToActivate = "t-" + dojo.attr(dojo.byId(evt.id),"caseid");         
        fluxProcessor.dispatchEvent(btnToActivate);

    },
    /* extension point to overwrite handleStateChanged for containers */
    handleStateChanged:function(contextInfo) {
        this.inherited(arguments);

    },
    _handleInvalid:function(control) {
        // console.debug("betterform.ui.container.TabSwitch._handleInvalid control",control);
        var affectedTab = betterform.ui.util.getContainerByClass(control.domNode,"xfCase");
        if(affectedTab == undefined){
            return;
        }
        var affectedTabDijit = dijit.byId(dojo.attr(affectedTab,"id"));
        if(affectedTabDijit != undefined) {
            var affectedTabCtrl = dijit.byId(affectedTabDijit.controlButton.id);
            // console.debug("controlButton Dijit: ",affectedTabCtrl);
            if(dojo.byId(affectedTabCtrl.id+"-image")== undefined){
                var imageDiv = document.createElement("img");
                dojo.addClass(imageDiv,"xfInvalidIcon");
                dojo.attr(imageDiv, "id", affectedTabCtrl.id+"-image");
                dojo.attr(imageDiv, "src", dojo.moduleUrl("dijit","themes/tundra/images/warning.png"));
                dojo.place(imageDiv,affectedTabCtrl.tabContent,3);
            }
        }
    },
    
    _handleValid:function(control) {
        // console.debug("betterform.ui.container.TabSwitch._handleValid control",control);
        var affectedTab = betterform.ui.util.getContainerByClass(control.domNode,"xfCase");
        if(affectedTab == undefined){
            return;
        }
        var affectedTabDijit = dijit.byId(dojo.attr(affectedTab,"id"));
/*
        if(affectedTabDijit != undefined) {
            var affectedTabCtrl = dijit.byId(affectedTabDijit.controlButton.id);
            if(affectedTabCtrl){
                // console.debug("controlButton Dijit: ",affectedTabCtrl);
                var invalidControls = dojo.query(".caDisplayInvalid",affectedTab);
                // console.debug("invalidControls: ",invalidControls);
                if(invalidControls.length == 0 && dojo.byId(affectedTabCtrl.id+"-image") != undefined){
                    var imageDiv = dojo.byId(affectedTabCtrl.id+"-image");
                    affectedTabCtrl.tabContent.removeChild(imageDiv);
                }
            }            
        }
*/
    },

    toggleCase:function(contextInfo){
        // console.debug("betterform.ui.container.TabSwitch.toggleCase", contextInfo, " this:",this);
		var case2selectNode = dojo.query("div[caseid='"+ contextInfo.selected + "']",this.domNode)[0];
        var case2selectDijit = dijit.byId(dojo.attr(case2selectNode,"id"));
        this.selectChild(case2selectDijit);
    }

});
